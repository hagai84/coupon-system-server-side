package core;

import java.io.Serializable;

import core.exception.CouponSystemException;
import core.util.ConnectionPool;

/**
 * The main class of the coupon system module
 * 
 * @author Ron
 *
 */
public class CouponSystem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CouponSystem couponSystemInstance = new CouponSystem();
	private static ConnectionPool connectionPool = ConnectionPool.getInstance();
	private static DailyCouponExpirationTask dailyTask = DailyCouponExpirationTask.getInstance();;
	
	private CouponSystem() { 
	}

	/**
	 * Gets an instance of {@link CouponSystem}.
	 * @return An instance of {@link CouponSystem}
	 */
	public static CouponSystem getInstance(){	
		return couponSystemInstance;	
	}


	/**
	 * Stops all tasks and shuts down the module
	 */
	public void shutdown(){
		dailyTask.stopTask();
		try {
			dailyTask.getT().join();
		} catch (InterruptedException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("CS shutdown join interrupted : " + e);
		}
		connectionPool.closeAllConnections();
		System.out.println("LOG : shut down completed");
	}

	/**
	 * Sets the server to connect to and gets the {@link DailyCouponExpirationTask} instance.
	 * @param driverName Driver name of the driver to connect with
	 * @param databaseUrl URL of the database
	 * @param userName Login user name
	 * @param password Login password
	 */
	public void setServer(String driverName, String databaseUrl, String userName, String password) {
		try {
			connectionPool.setServer(driverName, databaseUrl, userName, password);
//			Class.forName("core.util.IdGenerator");
//			dailyTask = DailyCouponExpirationTask.getInstance();
		} catch (CouponSystemException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("Set Server Initialize failed : " + e);
//		} catch (ClassNotFoundException e) {
			// TODO Manager handling
			// e.printStackTrace();
//			System.err.println("Id generator class not found : " + e);
		}	
	}
}
