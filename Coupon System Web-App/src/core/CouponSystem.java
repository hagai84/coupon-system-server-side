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
	private static CouponSystem coupSys = new CouponSystem();
	private static ConnectionPool pool = ConnectionPool.getInstance();
	private static DailyCouponExpirationTask task = DailyCouponExpirationTask.getInstance();
	
	private CouponSystem() { 
	}

	/**
	 * Gets an instance of {@link CouponSystem}.
	 * @return An instance of {@link CouponSystem}
	 */
	public static CouponSystem getInstance(){	
		return coupSys;	
	}

	/**
	 * Logs in to a user using the given info
	 * @param name Login username
	 * @param password Login password
	 * @param type User login type
	 * @return The appropriate Facade (Admin, Company, or Customer)
	 * @throws CouponSystemException
	 */
	/*public CouponClientFacade login(String name, String password, ClientType type) throws  CouponSystemException {	
		switch (type) {
		case ADMIN:
			return AdminFacade.login(name, password);			
		case COMPANY:
			return CompanyFacade.companyLogin(name, password);			
		case CUSTOMER:
			return CustomerFacade.login(name, password);			
		default:
			return null;
		}	
	}*/

	/**
	 * Stops all tasks and shuts down the module
	 */
	public void shutdown(){
		task.stopTask();
		try {
			task.getT().join();
		} catch (InterruptedException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("CS shutdown join interrupted : " + e);
		}
		pool.closeAllConnections();
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
			pool.setServer(driverName, databaseUrl, userName, password);
			Class.forName("coupon.system.util.IdGenerator");
			task = DailyCouponExpirationTask.getInstance();
		} catch (CouponSystemException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("Set Server Initialize failed : " + e);
		} catch (ClassNotFoundException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("Id generator class not found : " + e);
		}	
	}
}
