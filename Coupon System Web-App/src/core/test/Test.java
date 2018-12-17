package core.test;

import core.CouponSystem;
import core.exception.CouponSystemException;

/**
 * Tests the implementation of the coupon system and it's classes.
 * @author Ron
 *
 */
public class Test {
	private static CouponSystem couponSystem = CouponSystem.getInstance();
	
	private static String driverName = "com.mysql.jdbc.Driver";
	private static String databaseUrl = "jdbc:mysql://localhost:3306/coupon_system?createDatabaseIfNotExist=true";
	private static String userName = "root";
	private static String password = "1234";
	
	
	
	public static void main(String[] args) {
		couponSystem.setServer(driverName, databaseUrl, userName, password);
//		try {
//			resetDB();
//		} catch (CouponSystemException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		GenericThread testThread1 = new CreateThread();	
		GenericThread testThread2 = new ExceptionThread();	
		GenericThread testThread3 = new RestCreateThread();	
		GenericThread testThread4 = new PurchaseThread(0, 0);	
		GenericThread testThread5 = new PurchaseThread(0, 5);	
		GenericThread testThread6 = new PurchaseThread(0, 10);	
//		testThread1.start();	
//		testThread2.start();	
		testThread3.start();	
//		testThread4.start();	
//		testThread5.start();
//		testThread6.start();	
		try {
			testThread1.join();
			testThread2.join();
			testThread3.join();
			testThread4.join();
			testThread5.join();
			testThread6.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		couponSystem.shutdown();					
	}
	

	/**
	 * Resets the DB from scratch.
	 * @throws DAOException
	 */
	public static void resetDB() throws CouponSystemException {
			CreateDB db = new CreateDB();
			db.dropTables();
			db.createDb();
	}
	
}
