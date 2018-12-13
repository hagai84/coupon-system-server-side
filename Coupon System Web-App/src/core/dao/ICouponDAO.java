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
 * An interface for a DAO class which accesses a {@link CouponBean} type DAO.
 * 
 * @author Ron
 *
 */
public interface ICouponDAO extends Serializable{

	/**
	 * Adds a new {@link CouponBean} to coupon table
	 * 
	 * @param coupon The new {@link CouponBean} to be added.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If {@link CouponBean}  could not be added
	 */
	long createCoupon(CouponBean coupon) throws CouponSystemException;
	/**
	 * Updates a specific {@link CouponBean} in the DB.
	 * 
	 * @param coupon The {@link CouponBean}  to be updated.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If {@link CouponBean}  could not be updated
	 */
	void updateCoupon(CouponBean coupon) throws CouponSystemException;
	/**
	 * Adds a {@link CouponBean} to the {@link CustomerBean} and updates the amount.
	 * @param couponId The {@link CouponBean} to purchase.
	 * @param customerId The ID of the {@link CustomerBean} purchasing the coupon.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If coupon could not be purchased
	 */
	void updateCouponAmout(long couponId, long companyId, int amoutDelta) throws CouponSystemException;
	
	void purchaseCoupon(long couponId, long customerId) throws CouponSystemException;

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
		 * Removes a {@link CouponBean} from coupon table
		 * 
		 * @param couponId The {@link CouponBean}  to be removed.
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If {@link CouponBean}  could not be removed
		 */
		void removeCoupon(long couponId) throws CouponSystemException;
	/**
		 * Removes a coupon from a Company ({@link CompanyBean}).
		 *
		 * @param couponId the coupon to be removed
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If could not remove coupon from company
		 */
	//	void removeCouponFromCompanies(long couponId) throws CouponSystemException;
	
		/**
		 * Removes a coupon from a Company ({@link CompanyBean}).
		 *
		 * @param couponId the coupon to be removed
		 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
		 * @throws CouponException If could not remove coupon from company
		 */
	//	void removeCouponFromCompanies(long couponId) throws CouponSystemException;
	
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
	 * Fetches a specific {@link CouponBean} from the DB using its title.
	 *
	 * @param title The title of the desired {@link CouponBean}.
	 * @return The {@link CouponBean}  that matches the title; <br>
	 * 		   <code>null</code> if there is no match.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CouponException If could not get coupon
	 */
	CouponBean getCouponByTitle(String title) throws CouponSystemException;

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
	
		boolean couponTitleAlreadyExists(String title);
}
