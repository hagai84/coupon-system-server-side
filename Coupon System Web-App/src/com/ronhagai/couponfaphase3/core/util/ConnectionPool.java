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

	private final int POOL_SIZE = 1;
	private final int CLOSING_WAITING_PERIOD = 5000; //milliseconds
	private final int VALIDITY_WAITING_PERIOD = 10; //seconds

	private String driverName = null;
	private String databaseUrl;
	private String userName;
	private String password;	

	private ArrayList<Connection> availableConnections;
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
		if(initialized && usedConnections.containsKey(Thread.currentThread())) {
			return usedConnections.get(Thread.currentThread());
		}

		Connection connection = null;
		if (closing) {
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_CLOSING,"Connection pool shutting down");
		}
		
		synchronized (this) {
			if (!initialized) {
				initializePool();
			}
			// Check if there is a connection available. There are times when all the
			// connections in the pool may be used up
			if (availableConnections.size() < 1) {
				connection = newConnection();
			}else {
				connection = availableConnections.remove(0);
				try {
					//checks if connection isValid if not attempts to open a new one
					if (!connection.isValid(VALIDITY_WAITING_PERIOD)) {
						connection.close();
						connection = newConnection();					
					}
				} catch (SQLException e) {				
					throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"database access error occurred", e);
				}
			}

			// Giving away the connection from the connection pool
			usedConnections.put(Thread.currentThread(), connection);
			return connection;
		}
	}

	/**
	 * Gives connection back to ConnectionPool
	 * checks if connection exists in Out collection
	 * 
	 * @param connection Connection to return
	 */
	public void returnConnection(Connection connection) {

		try {
			if(connection.getAutoCommit()) {
				if (initialized) {
					synchronized (this) {
						//attempts to remove from Out collection
						if (usedConnections.remove(Thread.currentThread(), connection)) {
							if(availableConnections.size()<POOL_SIZE) {
								// Adding the connection from the client back to the connection pool
								availableConnections.add(connection);
								notifyAll();
							}else {
								connection.close();
							}
						}	
					}
				}else {
					connection.close();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("LOG : return connection failed");
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
		if (usedConnections.size()>0) {
			try {
				Thread.sleep(CLOSING_WAITING_PERIOD);
			} catch (InterruptedException e) {
				System.err.println("LOG : close all connections wait interrupted");
				e.printStackTrace();
			}
		}
		synchronized (this) {
			initialized = false;
			for (Connection connection : availableConnections) {
				try {
					connection.close();				
				} catch (SQLException e) {
					System.err.println("LOG : close all - close connection failed");
					e.printStackTrace();
				}
			}
			for (Connection connection : usedConnections.values()) {
				try {
					connection.close();				
				} catch (SQLException e) {
					System.err.println("LOG : close all - close connection failed");
					e.printStackTrace();
				}
			}			
		}
		closing = false;
		System.out.println("LOG : Connection pool Closed");
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
		//Critical section should be in synchronized block
		//however in order to not create a queue of threads invoking this method
		//it is privately invoked in a synchronized block
				
		initialized = false;
		if (driverName == null) {
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Server details are not set");
		}
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
//			System.err.println(e);
			throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"Connection Driver Class not found : ", e);
		}
		availableConnections = new ArrayList<Connection>();
		usedConnections = new HashMap<Thread, Connection>((int) (POOL_SIZE*1.33));
		while (availableConnections.size() < POOL_SIZE) {
			try {
				availableConnections.add(newConnection());
			} catch (CouponSystemException e) {
				closeAllConnections();
				throw new CouponSystemException(ExceptionsEnum.CONNECTION_POOL_INIT_ERROR,"database access error occurred ", e);
			}
		}
		initialized = true;
		System.out.println("LOG : connection pool initialized");

	}

	/**
	 * Creates a connection, then returns it
	 * @return A created connection
	 * @throws CouponSystemException 
	 * @throws SQLException If the connection timed out
	 * @throws SQLTimeoutException If a database access error occurs or the url is null
 	 */
	private Connection newConnection() throws CouponSystemException  {
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(databaseUrl, userName, password);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLTimeoutException e) {
			//get connection timed out
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_TIMOUT,"Server busy. Try again later", e);
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"database access error occurred", e);
		}	
		return connection;
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
	public void setServer(String driverName, String databaseUrl, String userName, String password) throws CouponSystemException {	
		synchronized(this) {
			if (initialized) {
				closeAllConnections();
			}
			this.driverName = driverName;
			this.databaseUrl = databaseUrl;
			this.userName = userName;
			this.password = password;
			
			initializePool();			
		}
	}
	
	public Connection startTransaction() throws CouponSystemException {
		Connection connection = getConnection();
		setAutoCommit(false);	
		return connection;
	}
	
	public void endTransaction() throws CouponSystemException {
		Connection connection = getConnection();
		try {
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - end transaction/commit", e);
			} catch (SQLException e1) {
				try {
					connection.close();
					throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - end transaction/rollback", e1);
				} catch (SQLException e2) {
					throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - end transaction/close", e2);					
				}
			}
		}finally {
			setAutoCommit(true);
			returnConnection(connection);
		}
		
	}
	
	public void rollback() throws CouponSystemException {
		Connection connection = getConnection();
		try {
			connection.rollback();
		} catch (SQLException e1) {
			try {
				connection.close();
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - rollback/rollback", e1);
			} catch (SQLException e) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - rollback/close", e);
			}
		}finally {
			setAutoCommit(true);
			returnConnection(connection);
		}			
	}
	
	private void setAutoCommit(boolean autoCommit) throws CouponSystemException {
		Connection connection = getConnection();
		try {
			connection.setAutoCommit(autoCommit);
		}catch (SQLException e) {
			try {
				connection.close();
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - set autocommit", e);
			} catch (SQLException e1) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"failed operation - set autocommit", e1);
			}
		}finally {			
			returnConnection(connection);
		}
	}
}