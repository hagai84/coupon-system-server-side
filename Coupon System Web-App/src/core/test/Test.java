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
	private static String databaseUrl = "jdbc:mysql://db4free.net:3306/coupon_system";
	private static String userName = "coupon_group";
	private static String password = "12345678";
	
	
	
	public static void main(String[] args) {
		couponSystem.setServer(driverName, databaseUrl, userName, password);
		/*try {
			resetDB();
		} catch (CouponSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		TestThread testThread1 = new GenerateThread();	
		TestThread testThread2 = new ExceptionThread();	
		TestThread testThread3 = new SynchronisationThread(0, 0);	
		TestThread testThread4 = new SynchronisationThread(0, 5);	
		TestThread testThread5 = new SynchronisationThread(0, 10);	
		TestThread testThread6 = new SynchronisationThread(0, 15);	
//		testThread1.start();	
//		testThread2.start();	
		testThread3.start();	
		testThread4.start();	
		testThread5.start();	
		testThread6.start();	
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
