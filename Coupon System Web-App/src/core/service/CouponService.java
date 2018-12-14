/**
 * 
 */
package core.service;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import core.beans.CouponBean;
import core.dao.CouponDAO;
import core.dao.ICouponDAO;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
import core.util.ConnectionPool;

/**
 * Facade used to access the coupon system by Administrators
 * 
 * private constructor, instance is received thru static method login()
 * 
 * @author Ron
 *
 */
public class CouponService implements Serializable, IBeanValidatorConstants{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CouponService couponServiceInstance = new CouponService();
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	private ICouponDAO couponDAO = CouponDAO.getInstance();

	/**
	 * Private constructor
	 */
	private CouponService() {
	}

	public static CouponService getInstance() {
		return couponServiceInstance;
	}

	/**
	 * Adds a new {@link CouponBean} to the DB in the following order:
	 * <ul>
	 * <li>To Table <code>coupon</code></li>
	 * <li>To Table <code>comp_coupon</code></li>
	 * </ul>
	 * 
	 * @param coupon The new {@link CouponBean} to be added.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public long createCoupon(CouponBean coupon, long companyId) throws CouponSystemException {
		checkCoupon(coupon);
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if (couponDAO.couponTitleAlreadyExists(coupon.getTitle())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Coupon Title already exists");
		}

		if (coupon.getCompanyId() != companyId) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"Company " + companyId + " attempts to ceate coupon on different company " + coupon.getCompanyId());
		}
		return couponDAO.createCoupon(coupon);
	}


	/**
	 * Purchases the given coupon, adds it to the customer and updates the coupons's
	 * amount. -checks if customer owns this coupon
	 * 
	 * @param couponId Coupon to purchase
	 * @throws CustomerFacadeException If coupon is out of stock or expired
	 * @throws CustomerFacadeException If coupon purchase fails
	 */
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException {
		couponDAO.purchaseCoupon(couponId, customerId);		
	}

	/**
	 * Updates a specific {@link CouponBean} in the DB.
	 *
	 * @param clientCoupon The coupon to be updated.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public void updateCoupon(CouponBean coupon, long companyId) throws CouponSystemException {
		checkCoupon(coupon);
		// gets original coupon data
		CouponBean originalCoupon = couponDAO.getCoupon(coupon.getCouponId());
		
		if (coupon.getCompanyId() != originalCoupon.getCompanyId()) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"Company " + companyId + " attempts to change ownership of coupon " + coupon.getCouponId());
		}
		if (originalCoupon.getCompanyId() != companyId) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"Company " + companyId + " attempts to update a coupon it doesn't own  " + coupon.getCouponId());
		}
		// alter the coupon data to the new ALLOWED ones
		originalCoupon.setEndDate(coupon.getEndDate());
		//TODO MOVE TO DELTA METHOD
//		originalCoupon.setAmount(coupon.getAmount());
		originalCoupon.setPrice(coupon.getPrice());
		// updates the coupon
		couponDAO.updateCoupon(originalCoupon);
	}

	public void updateCouponAmout(long couponId, long companyId, int amoutDelta) throws CouponSystemException {
		// gets original coupon data
		CouponBean originalCoupon = couponDAO.getCoupon(couponId);
		
		if (originalCoupon.getCompanyId() != companyId) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"Company " + companyId + " attempts to update a coupon's amount it doesn't own  " + couponId);
		}
		if (amoutDelta+originalCoupon.getAmount()<0) {
			throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"negative amount not allowed");
		}
		couponDAO.updateCouponAmout(couponId, amoutDelta);
	}

	/**
	 * Removes a {@link CouponBean} to the DB in the following order:
	 * <ul>
	 * <li>From Table <code>cust_coupon</code></li>
	 * <li>From Table <code>comp_coupon</code></li>
	 * <li>From Table <code>coupon</code></li>
	 * </ul>
	 *
	 * @param couponId The coupon to be removed.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public void removeCoupon(long couponId, long companyId) throws CouponSystemException {
		// TODO Start transaction
		connectionPool.startTransaction();
		CouponBean coupon = couponDAO.getCoupon(couponId);
		if (coupon.getCompanyId() != companyId) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"Company " + companyId + " attempts to remove coupon " + couponId +" of a different company " + coupon.getCompanyId());
		}
		try {
			couponDAO.removeCouponFromCustomers(couponId);
			couponDAO.removeCoupon(couponId);
		} catch (CouponSystemException e) {
			connectionPool.rollback();
			throw e;
		} finally {
		}
		connectionPool.endTransaction();
	}

	/**
	 * Fetches a specific {@link CouponBean} from the DB using its ID.
	 *
	 * @param couponID The ID of the desired {@link CouponBean}.
	 * @return The coupon that matches the ID; <br>
	 *         <code>null</code> if there is no match.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		return couponDAO.getCoupon(couponID);
	}

	/**
	 * Gets all coupons of the given CouponType
	 * 
	 * @param couponType The Type of coupons desired.
	 * @return a Collection of all coupons with matching a Type
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public Collection<CouponBean> getCouponsByType(CouponType couponType) throws CouponSystemException {
		return couponDAO.getCouponsByType(couponType);
	}

	/**
	 * Returns all available coupons
	 * 
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		return couponDAO.getAllCoupons();
	}

	/**
	 * Returns all available coupons of the given company
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		return couponDAO.getCompanyCoupons(companyId);
	}

	/**
	 * Returns all available coupons of the given company with a specific type
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type)
			throws CouponSystemException {
		return couponDAO.getCompanyCouponsByType(companyId, type);
	}
	
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException {
		return couponDAO.getCompanyCouponsByPrice(companyId, price);
	}
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException {
		return couponDAO.getCompanyCouponsByDate(companyId, date);
	}
		
	/**
	 * Returns all available coupons of the given Customer
	 * 
	 * @param customerId the Customer id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException {
		return couponDAO.getCustomerCoupons(customerId);
	}

	/**
	 * Returns all available coupons of the given customer with a specific type
	 * 
	 * @param customerId the customer id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type)
			throws CouponSystemException {
		return couponDAO.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Gets all coupons up to a certain price that were purchased by the customer
	 * 
	 * @param customerId
	 * @param price      the max price of the coupons to select
	 * @return Collection of Coupons associated with the Customer
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price)
			throws CouponSystemException {
		return couponDAO.getCustomerCouponsByPrice(customerId, price);
	}

	public CouponBean getCouponByTitle(String couponTitle) throws CouponSystemException {
		// TODO Auto-generated method stub
		return couponDAO.getCouponByTitle(couponTitle);
	}

	private void checkCoupon(CouponBean coupon) throws CouponSystemException {
		checkTitle(coupon.getTitle());
		checkStartDate(coupon.getStartDate());
		checkEndDate(coupon.getEndDate(), coupon.getStartDate());
		checkAmount(coupon.getAmount());
		checkType(coupon.getType());
		checkPrice(coupon.getPrice());
//		checkImage(coupon.getImage());
		checkMessage(coupon.getMessage());		
	}

	/*public void checkId(long id) {
		return true;
	}*/

	private void checkTitle(String title) throws CouponSystemException {
		if(title.length()>COUP_TITLE_LENGTH)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's title can't be longer than " + COUP_TITLE_LENGTH + " characters");
	}

	private void checkStartDate(Date startDate) throws CouponSystemException {
		if(startDate.before(new java.util.Date(System.currentTimeMillis())))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's start date can't be earlier than today");
	}

	private void checkEndDate(Date endDate, Date startDate) throws CouponSystemException {
		if(endDate.before(startDate))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's expiration date can't be earlier than today");
	}

	private void checkAmount(int amount) throws CouponSystemException {
		if(amount<0)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's amount can't be negative");
	}

	private void checkType(CouponType type) throws CouponSystemException {
		if(type == null)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon title cant be more than " + COUP_TITLE_LENGTH + " characters");
	}

	private void checkPrice(double price) throws CouponSystemException {
		if(price<0)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's price can't be negative");
	}

	private void checkImage(String image) throws CouponSystemException {
		//TODO if not null check if file exists/valid
		throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon image cant be ... ");//No restrictions at the momment
	}

	private void checkMessage(String message) throws CouponSystemException {
		if(message.length()>COUP_MSG_LENGTH)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's message can't be longer than " + COUP_MSG_LENGTH + " characters");		
	}
}
