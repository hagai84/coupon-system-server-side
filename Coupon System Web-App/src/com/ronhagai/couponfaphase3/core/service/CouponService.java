/**
 * 
 */
package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CartBean;
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.enums.CartStatus;
import com.ronhagai.couponfaphase3.core.enums.CouponType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Service used to access coupon related operations
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

	/**
	 * returns the single CouponService instance
	 * 
	 * @return the class singleton instance
	 */
	public static CouponService getInstance() {
		return couponServiceInstance;
	}

	/**
	 * Adds a new coupon entity to the repository.
	 * 
	 * @param coupon the new CouponBean object to be added.
	 * @param userId the user creating the new coupon
	 * @return the created coupon's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing title, (3) Invalid data, (4) security breach.
	 */
	public long createCoupon(CouponBean coupon, long userId, UserType userType) throws CouponSystemException {
		checkCoupon(coupon);
		//check if userId matches coupon's companyId
		if ((coupon.getCompanyId() != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to create the coupon %s", userType, userId, coupon));
		}
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if (couponDAO.couponTitleAlreadyExists(coupon.getTitle())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Coupon Title already exists");
		}
		long couponId = couponDAO.createCoupon(coupon);
		System.out.println(String.format("LOG : %s %s created coupon %s", userType, userId, couponId));
		return couponId;
	}


	/**
	 * Adds a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if it results in a negative coupon's amount, or if customer already owns this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param userId the user ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : out of stock,
	 *  existing ownership or no matching data.
	 */
	public void purchaseCoupon(long couponId, long customerId, long userId, UserType userType) throws CouponSystemException {
		//checks userType
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to purchase coupon %s on user %s", userType, userId, couponId, customerId));
		}
		if (couponDAO.customerAlreadyOwnsCoupon(couponId, customerId)) {
			throw new CouponSystemException(ExceptionsEnum.CUSTOMER_OWNS_COUPON,"Customer already owns coupon");
		}
		
		couponDAO.purchaseCoupon(couponId, customerId);	
		
		/*if(paymentGateway.checkout(shoppingCart)) {
			cancelPurchaseCoupon(couponId, customerId, userId, userType);		
		}*/
		
		System.out.println(String.format("User %s %s purchased coupon %s", userType, userId, couponId));
	}
	/**
	 * Adds a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if it results in a negative coupon's amount, or if customer already owns this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param userId the user ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : out of stock,
	 *  existing ownership or no matching data.
	 */
	public CartBean checkoutCart(CartBean cartBean, long customerId, long userId, UserType userType) throws CouponSystemException {
		//TODO check if list doesn't contain a coupon twice, handled down the line and rare but cld save O
		Collection<Long> availableCoupons = new ArrayList<>();
		Collection<Long> refusedCoupons = new ArrayList<>();
		//checks userType
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to purchase coupons %s on user %s", userType, userId, cartBean.getCoupons(), customerId));
		}
		
		//First round preliminary checks
		for (Long couponId : cartBean.getCoupons()) {		
			if (couponDAO.customerAlreadyOwnsCoupon(couponId, customerId) ||
					couponDAO.getCoupon(couponId).getAmount()==0) {
				//Add coupon to refused list 
				refusedCoupons.add(Long.valueOf(couponId));
			}else {
				//Add coupon to available 
				availableCoupons.add(Long.valueOf(couponId));
				//cld lock resource for client
			}
		}
		//Second round if no issues purchase cart
		if(refusedCoupons.size()==0) {
			availableCoupons.clear();
			for (Long couponId : cartBean.getCoupons()) {
				try {
					couponDAO.purchaseCoupon(couponId, customerId);
					availableCoupons.add(couponId);
				}catch (CouponSystemException e) {
					refusedCoupons.add(couponId);
				}	
			}
			if(refusedCoupons.size()==0) {
				//Begin payment transaction or Finish payment transaction depending on business logic
				System.out.println(String.format("User %s %s purchased coupon %s", userType, userId, cartBean.getCoupons()));
				cartBean.setStatus(CartStatus.PURCHASED);
//				cartBean.setCoupons(availableCoupons);
//				return cartBean;
			}else {
				for (Long couponId : availableCoupons) {
					try {
						couponDAO.cancelPurchaseCoupon(couponId, customerId);
					}catch (CouponSystemException e) {
						// TODO: Log failure to cancel purchase
						
					}
				}
				cartBean.setStatus(CartStatus.DENIED);
//				cartBean.setCoupons(availableCoupons);
//				return cartBean;				
			}
		}else {
			cartBean.setStatus(CartStatus.UPDATED);
//			cartBean.setCoupons(availableCoupons);
//			return cartBean;
		}
		cartBean.setCoupons(availableCoupons);
		return cartBean;
		
		/*if(paymentGateway.checkout(shoppingCart)) {
			cancelPurchaseCoupon(couponId, customerId, userId, userType);		
		}*/
		
	}
	/**
	 * Adds a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if it results in a negative coupon's amount, or if customer already owns this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param userId the user ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : out of stock,
	 *  existing ownership or no matching data.
	 */
	public void cancelPurchaseCoupon(long couponId, long customerId, long userId, UserType userType) throws CouponSystemException {
		//checks userType
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to cancel purchase coupon %s on user %s", userType, userId, couponId, customerId));
		}
		if (couponDAO.customerAlreadyOwnsCoupon(couponId, customerId)) {
			throw new CouponSystemException(ExceptionsEnum.CUSTOMER_OWNS_COUPON,"Customer does not own coupon");
		}
		
		couponDAO.cancelPurchaseCoupon(couponId, customerId);	
		
		System.out.println(String.format("User %s %s purchased coupon %s", userType, userId, couponId));
	}

	/**
	 * Updates a coupon entity in the repository.
	 * 
	 * @param coupon the CouponBean object to be updated.
	 * @param userId the user updating the coupon
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 * 	(3) Invalid data, (4) security breach.
	 */
	public void updateCoupon(CouponBean coupon, long userId, UserType userType) throws CouponSystemException {
		// gets original coupon data
		CouponBean originalCoupon = couponDAO.getCoupon(coupon.getCouponId());

		if (coupon.getCompanyId() != originalCoupon.getCompanyId() && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to change ownership of coupon ", userType, userId, coupon));
		}
		if ((originalCoupon.getCompanyId() != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to a coupon %s it doesn't own", userType, userId, coupon));
		}
		// alter the coupon data to the new ALLOWED ones
		originalCoupon.setEndDate(coupon.getEndDate());	
		originalCoupon.setPrice(coupon.getPrice());
		originalCoupon.setCompanyId(coupon.getCompanyId());
		originalCoupon.setImage(coupon.getImage());
		originalCoupon.setMessage(coupon.getMessage());
//		originalCoupon.setTitle(coupon.getTitle());
		originalCoupon.setType(coupon.getType());
		// updates the coupon
		checkCoupon(originalCoupon);
		couponDAO.updateCoupon(originalCoupon);
		System.out.println(String.format("LOG : User %s %s updated coupon %s", userType, userId, coupon.getCouponId()));
	}
	
	/**
	 * Updates a coupon entity's amount in the repository.
	 * cannot be resolve if it results in a negative amount. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param userId the user updating the coupon's amount.
	 * @param userType the user type
	 * @param amountDelta the amount of coupons to be added or removed (negative amount).
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : negative delta to exceeds stock,
	 *  no matching data, (3) Invalid data, (4) security breach.
	 */
	public void updateCouponAmout(long couponId, int amountDelta, long userId, UserType userType) throws CouponSystemException {
		// gets original coupon data
		CouponBean originalCoupon = couponDAO.getCoupon(couponId);
		
		if ((originalCoupon.getCompanyId() != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to update a coupon's amount it doesn't own %s", userType, userId, couponId));
		}
		if (originalCoupon.getAmount()+amountDelta<0) {
			throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"negative amount is not allowed");
		}
		couponDAO.updateCouponAmount(couponId, amountDelta);
		System.out.println(String.format("User %s %s updated coupon's amount %s by %s units", userType, userId, couponId, amountDelta));
	}

	/**
	 * Removes a coupon entity from the coupons and customers' coupons repositories.
	 * 
	 * @param couponId the coupon's ID.
	 * @param userId the user removing the coupon.
	 * @param userType the user type
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 *  (3) Invalid data, (4) security breach.
	 */
	public void removeCoupon(long couponId, long userId, UserType userType) throws CouponSystemException {
		// TODO Start transaction
		connectionPool.startTransaction();
		CouponBean originalCoupon = couponDAO.getCoupon(couponId);
		if ((originalCoupon.getCompanyId() != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to remove a coupon it doesn't own %s", userType, userId, couponId));
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
		System.out.println(String.format("LOG : User %s %s removed coupon %s", userType, userId, couponId));
	}

	/**
	 * Retrieves a coupon entity from the repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @return a CouponBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		return couponDAO.getCoupon(couponID);
	}

	/**
	 * Retrieves all the coupons entities of said type from the repository .
	 * 
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCouponsByType(CouponType couponType) throws CouponSystemException {
		return couponDAO.getCouponsByType(couponType);
	}

	/**
	 * Retrieves all the coupons entities from the repository .
	 * 
	 * @return a Collection of coupons objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		return couponDAO.getAllCoupons();
	}

	/**
	 * Retrieves all the coupons entities for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		return couponDAO.getCompanyCoupons(companyId);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException {
		return couponDAO.getCompanyCouponsByType(companyId, type);
	}
	
	/**
	 * Retrieves all the coupons entities bellow said Price for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException {
		return couponDAO.getCompanyCouponsByPrice(companyId, price);
	}
	
	/**
	 * Retrieves all the coupons entities expiring before said Date for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param date the latest (max) expiration Date.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException {
		return couponDAO.getCompanyCouponsByDate(companyId, date);
	}
		
	/**
	 * Retrieves all the coupons entities for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCustomerCoupons(long customerId, long userId, UserType userType) throws CouponSystemException {
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to view user %s coupons", userType, userId, customerId));
		}
		return couponDAO.getCustomerCoupons(customerId);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */	
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type, long userId, UserType userType) throws CouponSystemException {
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to view user %s coupons", userType, userId, customerId));
		}
		return couponDAO.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Retrieves all the coupons entities bellow said Price for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price, long userId, UserType userType) throws CouponSystemException {
		if( (!userType.equals(UserType.CUSTOMER) || customerId != userId) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to view user %s coupons", userType, userId, customerId));
		}
		return couponDAO.getCustomerCouponsByPrice(customerId, price);
	}

	private void checkCoupon(CouponBean coupon) throws CouponSystemException {
		checkTitle(coupon.getTitle());
//		checkStartDate(coupon.getStartDate());
		checkEndDate(coupon.getEndDate(), coupon.getStartDate());
		checkAmount(coupon.getAmount());
		checkType(coupon.getType());
		checkPrice(coupon.getPrice());
		checkImage(coupon.getImage());
		checkMessage(coupon.getMessage());		
	}

	private void checkTitle(String title) throws CouponSystemException {
		if(title.length()>COUPOMER_TITLE_LENGTH)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's title can't be longer than " + COUPOMER_TITLE_LENGTH + " characters");
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
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon type cant be more than " + COUPOMER_TITLE_LENGTH + " characters");
	}

	private void checkPrice(double price) throws CouponSystemException {
		if(price<0)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's price can't be negative");
	}

	private void checkImage(String image) throws CouponSystemException {
		//TODO if not null check if file exists/valid
//		throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon image cant be ... ");//No restrictions at the momment
	}

	private void checkMessage(String message) throws CouponSystemException {
		if(message.length()>COUPON_MSG_LENGTH)
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's message can't be longer than " + COUPON_MSG_LENGTH + " characters");		
	}
}
