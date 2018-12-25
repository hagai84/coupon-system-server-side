package com.ronhagai.couponfaphase3.core.dao;

import java.io.Serializable;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;

/**
 * An interface for a DAO class which accesses a {@link CustomerBean} type DAO.
 *
 * @author Ron
 *
 */
public interface ICustomerDAO extends Serializable {
	
	/**
	 * Attempts to create a given customer in the repository
	 * 
	 * @param customer The customer to create
	 * @return the created customer's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 * 
	 */
	long createCustomer(CustomerBean customer) throws CouponSystemException;

	/**
	 * Updates a customer entity in the repository.
	 * 
	 * @param customer the customer entity to be updated.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void updateCustomer(CustomerBean customer) throws CouponSystemException;
	/**
	 * updates the customer's password
	 * 
	 * @param customerId The customer to update
	 * @param newPassword The new password
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 */
	void updateCustomerPassword(long customerId, String newPassword) throws CouponSystemException;
	/**
	 * Removes a customer entity from the customers repository.
	 * 
	 * @param customerId the customer's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCustomer(long customerId) throws CouponSystemException;
	
	/**
	 * Retrieves a customer entity from the repository.
	 * 
	 * @param customerId the customer's ID.
	 * @return a CouponBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	CustomerBean getCustomer(long customerID) throws CouponSystemException;

	/**
	 * Retrieves all the customers entities from the repository .
	 * 
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CustomerBean> getAllCustomers() throws CouponSystemException;
	
	/**
	 * Check the password and user name,
	 * returns the user ID if correct.
	 * 
	 * @param customerName The customer's user name
	 * @param password The customer's password
	 * @return a long userId - the user's ID
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 * 
	 */
	long customerLogin(String customerName, String password) throws CouponSystemException;
	
	/**
	 * Checks if a customer's Name already exists in the repository .
	 * 
	 * @param customerName the name to match.
	 * @return a boolean value, (1) true if a matching Name was found, (2) false if no match was found 
	 * @throws CouponSystemException if the operation failed due to (1) DB error
	 */
	boolean customerNameAlreadyExists(String custName) throws CouponSystemException;
	
	
}
