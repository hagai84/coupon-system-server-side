/**
 * 
 */
package com.ronhagai.couponfaphase3.core.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.enums.CouponType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;

/**
 * An interface for a DAO class which provides access to {@link CouponBean} DTO data type.
 * 
 * @author Ron
 *
 */
public interface ICouponDAO extends Serializable{

	/**
	 * Adds a new coupon entity to the repository.
	 * 
	 * @param coupon the new CouponBean object to be added.
	 * @return the created coupon's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 */
	long createCoupon(CouponBean coupon) throws CouponSystemException;
	
	/**
	 * Adds a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if it results in a negative coupon's amount, or if customer already owns this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param customerId the customer's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : out of stock,
	 *  existing ownership or no matching data.
	 */
	void purchaseCoupon(long couponId, long customerId) throws CouponSystemException;
	
	/**
	 * Removes a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if customer doesn't own this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param customerId the customer's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : 
	 * 			no ownership or no matching data.
	 */
	void cancelPurchaseCoupon(long couponId, long customerId) throws CouponSystemException;

	/**
	 * Updates a coupon entity in the repository.
	 * 
	 * @param coupon the coupon entity to be updated.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void updateCoupon(CouponBean coupon) throws CouponSystemException;
	
	/**
	 * Updates a coupon entity's amount in the repository.
	 * cannot be resolve if it results in a negative amount. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param amountDelta the amount of coupons to be added or removed (negative amount).
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : negative delta to exceeds stock,
	 *  no matching data.
	 */
	void updateCouponAmount(long couponId, int amoutDelta) throws CouponSystemException;
	
	/**
	 * Removes a coupon entity from the coupons repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCoupon(long couponId) throws CouponSystemException;
	
	
	/**
	 * Removes a company's coupons from the coupons repository.
	 * 
	 * @param companyId the company's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCompanyCoupons(long companyId) throws CouponSystemException;
	
	/**
	 * Removes a customer's coupons from the customers repository.
	 * 
	 * @param customerId the customer's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCustomerCoupons(long customerId) throws CouponSystemException;
	
	/**
	 * Removes a coupon from the customers repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCouponFromCustomers(long couponId) throws CouponSystemException;
	
	/**
	 * Removes a company's coupons from the customers repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException;
	
	/**
	 * Removes expired coupons entities from the all repositories.
	 * 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeExpiredCoupons() throws CouponSystemException;
	
	/**
	 * Retrieves a coupon entity from the repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @return a CouponBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	CouponBean getCoupon(long couponID) throws CouponSystemException;

	/**
	 * Retrieves all the coupons entities of said type from the repository .
	 * 
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCouponsByType(CouponType type) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities from the repository .
	 * 
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getAllCoupons() throws CouponSystemException;

	/**
	 * Retrieves all the coupons entities for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities of said Type for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities bellow said Price for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities expiring before said Date for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param date the latest (max) expiration Date.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException;
	
	/**
	 * Retrieves all the coupons entities of said Type for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */	
	Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type) throws CouponSystemException;
		
	/**
	 * Retrieves all the coupons entities bellow said Price for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price) throws CouponSystemException;
	
	/**
	 * Checks if a coupon's Title already exists in the repository .
	 * 
	 * @param title the Title to match.
	 * @return a boolean value, (1) true if a matching Title was found, (2) false if no match was found 
	 * @throws CouponSystemException if the operation failed due to (1) DB error
	 */
	boolean couponTitleAlreadyExists(String title) throws CouponSystemException;

	/**
	 * Checks if a customer already owns a coupon.
	 * 
	 * @param couponId the coupon's Id.
	 * @param customerId the customer's Id.
	 * @return a boolean value, (1) true if a matching Title was found, (2) false if no match was found 
	 * @throws CouponSystemException if the operation failed due to (1) DB error
	 */
	boolean customerAlreadyOwnsCoupon(long couponId, long customerId) throws CouponSystemException;
}
