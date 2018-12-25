//NOT IN USE

package com.ronhagai.couponfaphase3.core.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.dao.CompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.CustomerDAO;
import com.ronhagai.couponfaphase3.core.dao.ICompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICustomerDAO;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;

/**
 * 
 * this class is deprecated
 * ID generator used to store and track the latest company, customer and coupon ID's. <br >
 * On startup of system, IdGenerator loads the files if they exist. Otherwise, it checks
 * what the latest ID's are and creates a file with said data.
 * 
 * An Id generator which provides unique ids to all 3 types of Beans. <br>
 * Uses a files to store the last incremented ids (txt/bin/encrypted). <br>
 * If said files are not found it attempts to read highest ids from DB. <br>
 * If still unsuccessful (DB empty) it sets the ids to predetermined values.
 * 
 * @author hagai
 */
@Deprecated
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
			//CLD CREATE GET_MAX_ID IN DAO
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
			//CLD CREATE GET_MAX_ID IN DAO
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
			//CLD CREATE GET_MAX_ID IN DAO
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
	public static long generatCompanyId() throws CouponSystemException {
		
		synchronized (companyIdFile) {
			long id = -1;
			try (DataInputStream in = new DataInputStream(new FileInputStream(companyIdFile));) {
				id = in.readLong();
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to read company Id file", e);
			}
			setCompanyStaticId(id + 1);
			return id;
		}
	}

	/**
	 * Generates a new Customer ID
	 * @return New Customer ID
	 * @throws CouponSystemException
	 */
	public static long generatCustomerId() throws CouponSystemException {
		
		synchronized (customerIdFile) {
			long id = -1;
			try (DataInputStream in = new DataInputStream(new FileInputStream(customerIdFile));) {
				id = in.readLong();
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to read customer Id file", e);
			}
			setCustomerStaticId(id + 1);
			return id;
		}
	}

	/**
	 * Generates a new Coupon ID
	 * @return New Coupon ID
	 * @throws CouponSystemException
	 */
	public static long generatCouponId() throws CouponSystemException {
		
		synchronized (couponIdFile) {
			long id = -1;
			try (DataInputStream in = new DataInputStream(new FileInputStream(couponIdFile));) {
				id = in.readLong();
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to read coupon Id file", e);
			}
			setCouponStaticId(id + 1);
			return id;
		}
	}
	
	
	
	
	
/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws URISyntaxException 
 * @throws IOException 
 */
	public static void setCompanyStaticId(long id) throws CouponSystemException {

		synchronized (companyIdFile) {
			try (DataOutputStream out = new DataOutputStream(new FileOutputStream(companyIdFile));) {
				out.writeLong(id);
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to write company Id file", e);
			}
		}
	}

/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws URISyntaxException 
 * @throws IOException 
 */
	public static void setCustomerStaticId(long id) throws CouponSystemException {
	
		synchronized (customerIdFile) {
			try (DataOutputStream out = new DataOutputStream(new FileOutputStream(customerIdFile));) {
				out.writeLong(id);
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to write customer Id file", e);
			}
		}
	}

/**
 * Resets the file data to the given id.
 * @param id the ID that we want to reset the file to
 * @throws IOException 
 * @throws URISyntaxException 
 */
	public static void setCouponStaticId(long id) throws CouponSystemException {

		synchronized (couponIdFile) {
			try (DataOutputStream out = new DataOutputStream(new FileOutputStream(couponIdFile));) {
				out.writeLong(id);
			} catch (IOException e) {
				throw new CouponSystemException(ExceptionsEnum.IO_EXCEPTION,
						"IdGenerator : failed to write coupon Id file", e);
			}
		}
	}

}
