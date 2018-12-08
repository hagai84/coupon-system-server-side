package core;

import java.sql.Date;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.controller.CompanyController;
import core.controller.CouponController;
import core.controller.CustomerController;
import core.enums.ClientType;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.util.CreateDB;

/**
 * Tests the implementation of the coupon system and it's classes.
 * @author Ron
 *
 */
public class Test {
	private static CouponSystem sys = CouponSystem.getInstance();
	private static CouponController adminFacade = new CouponController();
	private static CompanyController companyFacade = new CompanyController();
	private static CustomerController customerFacade = new CustomerController();
//	private static IController CCF = null;
	
	/*private static String driverName = "org.apache.derby.jdbc.ClientDriver";
	private static String databaseUrl = "jdbc:derby://localHost:1527/coupon_system;create = true";
	private static String userName = null;
	private static String password = null;*/
	private static String driverName = "com.mysql.jdbc.Driver";
	private static String databaseUrl = "jdbc:mysql://db4free.net:3306/coupon_system";
	private static String userName = "coupon_group";
	private static String password = "12345678";
	
	public static void main(String[] args) {	
		sys.setServer(driverName, databaseUrl, userName, password);
		testExceptions();
		try {
			createDefault();
		} catch (CouponSystemException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 		
		sys.shutdown();					
	}
	
	private static void loginAdmin() throws CouponSystemException {
		System.out.println("LOG : Admin logged in");
	}
	
	private static void loginCompany(String user, String password) throws CouponSystemException {
		companyFacade.companyLogin(user, password);	
		System.out.println("LOG : Company logged in");
	}

	private static void loginCustomer(String user, String password) throws CouponSystemException {
		customerFacade.customerLogin(user, password);	
		System.out.println("LOG : customer logged in");
	}

	/**
	 * Populates the DB with default values
	 * 
	 * @throws CouponSystemException
	 */
	private static void createDefault() throws CouponSystemException {
//		resetDB();
		loginAdmin();
		if(adminFacade==null)
			return;
		System.out.println("creating default all");
		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		CompanyBean company = new CompanyBean();
		for (char i = 97; i < 117; i++) {
			company.setId(100000 + i);
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			companyFacade.createCompany(company);
			System.out.println("LOG : Company created \n" + company);
			loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			coupon.setCouponId(200000 + i);
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(50);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage("aaaaaa");
			coupon.setPrice(200);
			coupon.setImage("aaaaaaaaaaaaaa");
			adminFacade.createCoupon(coupon, company.getId());
			System.out.println("LOG : Coupon created \n" + coupon);
			customer.setId(100032 + i);
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			customer.setPassword(""+i+i+i+i+i+i);
			customerFacade.createCustomer(customer);
			
			loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i); 
			adminFacade.purchaseCoupon(coupon.getCouponId(), customer.getId());
			System.out.println("LOG : Coupon purchased \n" + coupon);

		}
	}
	private static void testExceptions() {
		/*try {
			resetDB();
		} catch (CouponSystemException e1) {
			e1.printStackTrace();
		}*/
		try {
			loginAdmin();
		} catch (CouponSystemException e) {System.err.println(e.getMessage());}
		if(adminFacade==null)
			return;
		System.out.println("running exception test");
		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		CompanyBean company = new CompanyBean();
		for (char i = 97; i < 117; i++) {
			company.setId(100000 + i);
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			coupon.setCouponId(200000 + i);
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(50);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage("aaaaaa");
			coupon.setPrice(200);
			coupon.setImage("aaaaaaaaaaaaaa");	
			customer.setId(100032 + i);
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			customer.setPassword(""+i+i+i+i+i+i);
			
			
			switch(i%16) {
			case 0:
				company.setId(100000 + i);
				break;
			case 1:
				company.setPassword(""+i+i+i+i);
				break;
			case 2:
				company.setEmail(""+i+i+i+"@"+i+i+i+i+"");
				break;
			case 3:
				coupon.setCouponId(200000 + i);
				break;
			case 4:
				coupon.setTitle(""+i+i+i+" "+i+i+i+i +"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 5:
				coupon.setStartDate(new Date(System.currentTimeMillis()-(1000*60*60*24)));
				break;
			case 6:
				coupon.setEndDate(new Date(System.currentTimeMillis()-(1000*60*60*24*30)));
				break;
			case 7:
				coupon.setAmount(-4);
				break;
			case 8:
				coupon.setType(null);
				break;
			case 9:
				coupon.setMessage("aaaaaa"+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 10:
				coupon.setPrice(-4);
				break;
			case 11:
				coupon.setImage("aaaaaaaaaaaaaa");	
				break;
			case 12:
				customer.setId(100032 + i);
				break;
			case 13:
				customer.setCustName(""+i+i+i+" "+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 14:
				customer.setPassword(""+i+i+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 15:
				company.setCompName(""+i+i+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			}
			//TODO Add unauthorized update/remove + same coupon purchase
			
			
			try {
				companyFacade.createCompany(company);
				System.out.println("LOG : Company created \n" + company);

			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				if(companyFacade != null) {
					adminFacade.createCoupon(coupon, company.getId());
					System.out.println("LOG : Coupon created \n" + coupon);
				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customerFacade.createCustomer(customer);
				System.out.println("LOG : Customer created \n" + customer);
			} catch (CouponSystemException e) {System.err.println(e);}
			
			try {
				if(customerFacade != null)
				loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);} 
			try {
				if(customerFacade != null) {
					adminFacade.purchaseCoupon(coupon.getCouponId(), customer.getId());
					System.out.println("LOG : Coupon purchased \n" + coupon);

				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				if(companyFacade != null) {
					adminFacade.removeCoupon(coupon.getCouponId());
					System.out.println("LOG : Coupon deleted \n" + coupon);
				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customerFacade.removeCustomer(customer.getId());
				System.out.println("LOG : Customer deleted \n" + customer);

			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				companyFacade.removeCompany(company.getId());
				System.out.println("LOG : Company deleted \n" + company);
			} catch (CouponSystemException e) {System.err.println(e);}	
			//TODO add all facade methods
		}
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
