/**
 * 
 */
package core.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import core.beans.CouponBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;

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
	 * @param coupon the new {@link CouponBean} object to be added.
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
	 * Updates a coupon entity in the repository.
	 * 
	 * @param coupon the {@link CouponBean} object to be updated.
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
	void updateCouponAmout(long couponId, int amoutDelta) throws CouponSystemException;
	
	/**
	 * Removes a coupon entity from the repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCoupon(long couponId) throws CouponSystemException;
	
	/*
	 * jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
	 */
		/**
		 * Removes all coupons of a company ({@link CompanyBean}).
		 *
		 * @param companyId The customer's Id 
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If could not remove coupon from customer
		 */
		void removeCompanyCoupons(long companyId) throws CouponSystemException;
	/**
	 * Removes all coupons of a Customer ({@link CustomerBean}).
	 *
	 * @param customerId The customer's Id 
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not remove coupon from customer
	 */
	void removeCustomerCoupons(long customerId) throws CouponSystemException;
	/**
	 * Removes a coupon from all Customers ({@link CustomerBean}).
	 *
	 * @param couponId The coupon to be removed
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not remove coupon from customers
	 */
	void removeCouponFromCustomers(long couponId) throws CouponSystemException;
	/**
		 * Removes a coupon from a Company ({@link CompanyBean}).
		 *
		 * @param couponId the coupon to be removed
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If could not remove coupon from company
		 */
	//	void removeCouponFromCompanies(long couponId) throws CouponSystemException;
	
		void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException;
	/**
	 * Removes a coupon from all Customers ({@link CustomerBean}).
	 *
	 * @param couponId The coupon to be removed
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not remove coupon from customers
	 */
	void removeExpiredCoupons() throws CouponSystemException;
	/**
	 * Fetches a specific {@link CouponBean} from the DB using its ID.
	 *  
	 * @param couponID The ID of the desired {@link CouponBean}.
	 * @return The coupon that matches the ID; <br>
	 * 		   <code>null</code> if there is no match.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not get coupon
	 */
	CouponBean getCoupon(long couponID) throws CouponSystemException;

	/**
	 * Gets all coupons of the given CouponType
	 * @param type The Type of coupons desired.
	 * @return a Collection of all coupons with matching a Type
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCouponsByType(CouponType type) throws CouponSystemException;
	/**
	 * Fetches and assembles all the coupons in a {@link Collection}.
	 *
	 * @return A {@link Collection} of all coupons.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getAllCoupons() throws CouponSystemException;

	/**
	 * Assemble and return an <code>ArrayList</code> of all the coupons of
	 * a given company from the DB.
	 * 
	 * @param companyId The ID of the company whose coupons we want to get.
	 * @return A populated <code>ArrayList</code> of all the coupons; An empty <code>ArrayList</code>
	 * if there is none).
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException;
	/**
	 * Assemble and return an <code>ArrayList</code> of all the coupons of
	 * a given company from the DB.
	 * 
	 * @param companyId The ID of the company whose coupons we want to get.
	 * @param type The Type of coupons desired.
	 * @return A populated <code>ArrayList</code> of all the coupons; An empty <code>ArrayList</code>
	 * if there is none).
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException;
	/**
	 * Assemble and return an <code>ArrayList</code> of all the coupons of
	 * a given customer from the DB.
	 *
	 * @param customerId The ID of the customer whose coupons we want to get.
	 * @return A populated <code>ArrayList</code> of all the coupons; An empty <code>ArrayList</code>
	 * if there is none).
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException;
	Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException;
	Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException;
	/**
	 * Assemble and return an <code>ArrayList</code> of all the coupons of
	 * a given customer from the DB.
	 *
	 * @param customerId The ID of the customer whose coupons we want to get.
	 * @param type The Type of coupons desired.
	 * @return A populated <code>ArrayList</code> of all the coupons; An empty <code>ArrayList</code>
	 * if there is none).
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type) throws CouponSystemException;
	/**
	 * Assemble and return an <code>ArrayList</code> of all the coupons of
	 * a given customer from the DB.
	 *
	 * @param customerId The ID of the customer whose coupons we want to get.
	 * @param price The max price of coupons desired.
	 * @return A populated <code>ArrayList</code> of all the coupons; An empty <code>ArrayList</code>
	 * if there is none).
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price) throws CouponSystemException;

	/**
	 * Adds a coupon to a Company ({@link CompanyBean}).
	 *
	 * @param coupon The coupon to be added
	 * @param compId The company's Id 
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not add coupon to company
	 */
//	void addCouponToCompany(CouponBean coupon, long compId) throws CouponSystemException;

	
	
		/**
		 * Removes a coupon from a Company ({@link CompanyBean}).
		 *
		 * @param couponId the coupon to be removed
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If could not remove coupon from company
		 */
	//	void removeCouponFromCompanies(long couponId) throws CouponSystemException;
	
		boolean couponTitleAlreadyExists(String title) throws CouponSystemException;
}
