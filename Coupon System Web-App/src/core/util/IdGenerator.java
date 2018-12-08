package core.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.dao.CompanyDAO;
import core.dao.CouponDAO;
import core.dao.CustomerDAO;
import core.dao.ICompanyDAO;
import core.dao.ICouponDAO;
import core.dao.ICustomerDAO;
import core.exception.CouponSystemException;

/**
 * 
 * 
 * ID generator used to store and track the latest company, customer and coupon ID's. <br >
 * On startup of system, IdGenerator loads the files if they exist. Otherwise, it checks
 * what the latest ID's are and creates a file with said data.
 * 
 * An Id generator which provides unique ids to all 3 types of Beans. <br>
 * Uses a files to store the last incremented ids (txt/bin/encrypted). <br>
 * If said files are not found it attempts to read highest ids from DB. <br>
 * If still unsuccessful (DB empty) it sets the ids to predetermined values.
 * 
 * @author Ron
 */
public class IdGenerator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ICompanyDAO companyDAO = CompanyDAO.getInstance();
	private static ICustomerDAO customerDAO = CustomerDAO.getInstance();
	private static ICouponDAO couponDAO = CouponDAO.getInstance();
	private static File companyIdFile = new File("company_id.bin");
	private static File customerIdFile = new File("customer_id.bin");
	private static File couponIdFile = new File("coupon_id.bin");
	private static long DEFAULT_STARTING_COMPANY_ID = 1000000;
	private static long DEFAULT_STARTING_CUSTOMER_ID = 2000000;
	private static long DEFAULT_STARTING_COUPON_ID = 3000000;

	static {
		long MaxId;
		try {
			
			if (!companyIdFile.exists()) {
				MaxId=DEFAULT_STARTING_COMPANY_ID;
				companyIdFile.createNewFile();
			}else {
				MaxId=generatCompanyId()-1;
			}
			for (CompanyBean company : companyDAO.getAllCompanies()) {
				if(MaxId<company.getId()) {
					MaxId=company.getId();
				}
			}
			setCompanyStaticId(MaxId+1);
			
			if (!customerIdFile.exists()) {
				MaxId=DEFAULT_STARTING_CUSTOMER_ID;
				customerIdFile.createNewFile();
			}else {
				MaxId = generatCustomerId()-1;
			}
			for (CustomerBean customer : customerDAO.getAllCustomers()) {
				if(MaxId<customer.getId()) {
					MaxId=customer.getId();
				}
			}
			setCustomerStaticId(MaxId+1);
			
			if (!couponIdFile.exists()) {
				MaxId=DEFAULT_STARTING_COUPON_ID;
				couponIdFile.createNewFile();
			}else {
				MaxId = generatCouponId()-1;
			}
			for (CouponBean coupon : couponDAO.getAllCoupons()) {
				if(MaxId<coupon.getCouponId()) {
					MaxId=coupon.getCouponId();
				}
			}
			setCouponStaticId(MaxId+1);

		} catch (IOException | CouponSystemException e) {
			// TODO Manager handling
			// e.printStackTrace();
			System.err.println("Id generator initialization failed : " + e);
		} 
		System.out.println("LOG : Id Generator initialized");
	}
	
	private IdGenerator() {}

	/**
	 * Generates a new Company ID
	 * @return New Company ID
	 * @throws CouponSystemException
	 */
	public static synchronized long generatCompanyId() throws CouponSystemException {
		
		long id = -1;
		
		try (DataInputStream in = new DataInputStream(new FileInputStream(companyIdFile));) {
			id = in.readLong();
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to read company Id file", e);
		}
		
		setCompanyStaticId(id+1);
		
		return id;
	}

	/**
	 * Generates a new Customer ID
	 * @return New Customer ID
	 * @throws CouponSystemException
	 */
	public static synchronized long generatCustomerId() throws CouponSystemException {
		
		long id = -1;
		
		try (DataInputStream in = new DataInputStream(new FileInputStream(customerIdFile));) {
			id = in.readLong();
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to read customer Id file", e);
		}
		
		setCustomerStaticId(id+1);
		
		return id;
	}

	/**
	 * Generates a new Coupon ID
	 * @return New Coupon ID
	 * @throws CouponSystemException
	 */
	public static synchronized long generatCouponId() throws CouponSystemException {
		
		long id = -1;
		
		try (DataInputStream in = new DataInputStream(new FileInputStream(couponIdFile));) {
			id = in.readLong();
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to read coupon Id file", e);
		}
		
		setCouponStaticId(id+1);
		
		return id;
	}
	
	
	
	
	
/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws URISyntaxException 
 * @throws IOException 
 */
	public static synchronized void setCompanyStaticId(long id) throws CouponSystemException {

		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(companyIdFile));) {
			out.writeLong(id);
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to write company Id file", e);
		}
	}

/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws URISyntaxException 
 * @throws IOException 
 */
	public static synchronized void setCustomerStaticId(long id) throws CouponSystemException {
	
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(customerIdFile));) {
			out.writeLong(id);
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to write customer Id file", e);
		}
	}

/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws IOException 
 * @throws URISyntaxException 
 */
	public static synchronized void setCouponStaticId(long id) throws CouponSystemException {

		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(couponIdFile));) {
			out.writeLong(id);
		} catch (IOException e) {
			throw new CouponSystemException("IdGenerator : failed to write coupon Id file", e);
		}
	}

}
