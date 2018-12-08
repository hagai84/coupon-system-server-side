package core.test;


import core.CouponSystem;
import core.exception.CouponSystemException;
import core.util.CreateDB;

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
		try {
			resetDB();
		} catch (CouponSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RegularThread testThread1 = new RegularThread();	
		RegularThread testThread2 = new RegularThread();	
		testThread1.start();	
		testThread2.start();	
		try {
			testThread1.join();
			testThread2.join();
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
