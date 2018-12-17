package com.ronhagai.couponfaphase3.core.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;;

/**
 * 
 * A singleton Connection pool class with a set number of connections that manages 
 * the pool providing the users. Keeps a collection of "in-use" connections.<br>
 * Checks connections before distribution and refreshes them if necessary.<br>
 * Shuts down if DB error, reinitializes when needed.
 *
 * @author Ron
 * 
 */
public class ConnectionPool implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static ConnectionPool connectionPoolInstance = new ConnectionPool();

	private final int POOL_SIZE = 3;

	private String driverName = null;
	private String databaseUrl;
	private String userName;
	private String password;	

	private ArrayList<Connection> availableConnections;
//	private ArrayList<Connection> connectionsOut;
	private HashMap<Thread, Connection> usedConnections;
	private boolean closing = false;
	private boolean initialized = false;

	/**
	 * Private constructor
	 */
	private ConnectionPool() {
	}

	/**
	 * returns a static instance of the ConnectionPool
	 * @return A static instance of the ConnectionPool
	 */
	public static ConnectionPool getInstance() {
		return connectionPoolInstance;
	}

	/**
	 * Gets a connection from the pool
	 * checks for connection's validity
	 * initializes the pool in needed
	 * 
	 * @return A connection to the DB; <br>
	 *         <code>null</code> if unsuccessful
	 * @throws CouponSystemException If connection is unsuccessful (timeout/server down)
	 */
	public Connection getConnection() throws CouponSystemException {
		if(usedConnections.containsKey(Thread.currentThread())) {
			return usedConnections.get(Thread.currentThread());
		}

		Connection con = null;
		if (closing) {
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Connection pool shutting down");
		}
		
		synchronized (this) {
			if (!initialized) {
				initializePool();
			}
			// Check if there is a connection available. There are times when all the
			// connections in the pool may be used up
			while (availableConnections.size() < 1) {
				try {
					if (closing) {
						throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Connection pool is shutting down");
					}
					wait();
				} catch (InterruptedException e) {
					// TODO Manager handling
					// e.printStackTrace();
					System.err.println("get connection wait interrupted : " + e);
					throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"database access error occurred", e);

				}
			}
			con = availableConnections.remove(0);
			
			try {
				//checks if connection isValid if not attempts to open a new one
				if (!con.isValid(5)) {
					con.close();
					con = newConnection();
				}
			} catch (SQLTimeoutException e) {
				//new connection timed out
				availableConnections.add(con);
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_TIMOUT,"Server busy. Try again later", e);
			} catch (SQLException e) {
				//DB error initiates pool shutdown
				availableConnections.add(con);
//				closeAllConnections();//is there a need ?
//				initialized = false;
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"database access error occurred", e);
			}
			// Giving away the connection from the connection pool
			usedConnections.put(Thread.currentThread(), con);
			return con;
		}
	}

	/**
	 * Gives connection back to ConnectionPool
	 * checks if connection exists in Out collection
	 * 
	 * @param con Connection to return
	 */
	public void returnConnection(Connection con) {

		try {
			if(con.getAutoCommit()) {
				if (initialized) {
					synchronized (this) {
						//attempts to remove from Out collection
						if (usedConnections.remove(Thread.currentThread(), con)) {
							// Adding the connection from the client back to the connection pool
							availableConnections.add(con);
							notifyAll();
						}	
					}
				}else {
					con.close();
				}
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Closes all connections in ConnectionPool
	 * during the process closing=true
	 * sets initialized to false
	 * 
	 */
	public void closeAllConnections() {
		closing = true;
		//if there are connections out wait for a set amount of millisecond
		if (availableConnections.size() < POOL_SIZE) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		initialized = false;
		synchronized (this) {
			for (Connection connection : availableConnections) {
				try {
					connection.close();				
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
			for (Connection connection : usedConnections.values()) {
				try {
					connection.close();				
				} catch (SQLException e) {
					System.err.println(e);
				}
			}			
		}
		closing = false;
	}
			

	/**
	 * Initializes the Connection Pool
	 * sets initialized to true
	 * 
	 * @throws CouponSystemException 
	 * If driver class isn't found
	 * If connection times out
	 * If an error occurred while attempting to access the database
	 */
	private void initializePool() throws CouponSystemException {
		if (driverName == null) {
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Server details are not set");
		}
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Connection Driver Class not found : ", e);
		}
		availableConnections = new ArrayList<Connection>();
		usedConnections = new HashMap<Thread, Connection>((int) (POOL_SIZE*1.33));
		while (availableConnections.size() < POOL_SIZE) {
			try {
				availableConnections.add(newConnection());
			} catch (SQLTimeoutException e) {
				System.err.println("new Connection can't be established time out exception " + e);
			} catch (SQLException e) {
				closeAllConnections();
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"database access error occurred ", e);
			}
		}
		initialized = true;
		System.out.println("LOG : connection pool initialized");

	}

	/**
	 * Creates a connection, then returns it
	 * @return A created connection
	 * @throws SQLException If the connection timed out
	 * @throws SQLTimeoutException If a database access error occurs or the url is null
 	 */
	private Connection newConnection() throws SQLException, SQLTimeoutException {
		Connection con = DriverManager.getConnection(databaseUrl, userName, password);
		
		con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		return con;
	}

	/**
	 * Returns if the ConnectionPool has been initialized
	 * @return <code>true</code> if the ConnectionPool has been initialized; otherwise - <code>false</code>
	 */
	public boolean isInitialized() {
		return initialized;
	}

//	public setServer(String driverName)
	/**
	 * Sets the server to connect to.
	 * Closes all connection before
	 * Initializes pool after
	 * 
	 * 
	 * @param driverName Driver name of the driver to connect with
	 * @param databaseUrl URL of the database
	 * @param userName Login user name
	 * @param password Login password
	 * @throws CouponSystemException 
	 * If driver class isn't found
	 * If connection times out
	 * If an error occurred while attempting to access the database
	 */
	public synchronized void setServer(String driverName, String databaseUrl, String userName, String password)
			throws CouponSystemException {
		if (initialized) {
			closeAllConnections();
		}
		this.driverName = driverName;
		this.databaseUrl = databaseUrl;
		this.userName = userName;
		this.password = password;

		initializePool();
	}
	
	public Connection startTransaction() throws CouponSystemException {
//		connectionsOut.get(Thread)
		Connection con = getConnection();
		try {
			con.setAutoCommit(false);
		
		} catch (SQLException e) {
			// TODO set autocommit failed
			e.printStackTrace();
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation");
		}finally {
			returnConnection(con);
		}
		return con;
	}
	
	public void endTransaction() throws CouponSystemException {
		Connection con = getConnection();
		try {
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Commit failed rollback?
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation");
			/* ************************************************************************************* */
		}finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO set Autocommit failed
				e.printStackTrace();
			}
			returnConnection(con);
		}
		
	}
	
	public void rollback() throws CouponSystemException {
		Connection con = getConnection();
		try {
			con.rollback();
		} catch (SQLException e1) {
			// TODO Commit failed rollback?
			// TODO Auto-generated catch block
			e1.printStackTrace();
			/* ************************************************************************************* */
		}finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO set Autocommit failed
				e.printStackTrace();
			}
			returnConnection(con);
		}
				
	}
}