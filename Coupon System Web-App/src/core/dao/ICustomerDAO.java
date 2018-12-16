package core.dao;

import java.io.Serializable;
import java.util.Collection;

import core.beans.CustomerBean;
import core.exception.CouponSystemException;

/**
 * An interface for a DAO class which accesses a {@link CustomerBean} type DAO.
 *
 * @author Ron
 *
 */
public interface ICustomerDAO extends Serializable {
	/**
	 * Attempts to create a given customer in the DB
	 *
	 *
	 * @param customer The customer to create
	 * @throws DAOException If there is a connection problem or <code>SQLException</code>.
	 * @throws CustomerException If insertion of the given customer to the DB fails (e.g. <code>Customer</code> ID already exists).
	 *
	 */
	long createCustomer(CustomerBean customer) throws CouponSystemException;

	/**
	 * Removes a specified customer from the DB.
	 *
	 * @param customerId The customer to be removed.
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CustomerException If the given customer's ID can't be found in the DB.
	 *
	 */
	void removeCustomer(long customerId) throws CouponSystemException;

	/**
	 * Updates all of a customer's fields (except ID) in the DB according to the given customer bean.
	 *
	 * @param customer The customer to be updated
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CustomerException If the given customer's ID can't be found in the DB (0 rows were updated).
	 */
	void updateCustomer(CustomerBean customer) throws CouponSystemException;

	void updateCustomerPassword(long customerId, String oldPassword, String newPassword) throws CouponSystemException;
	/**
	 * Searches the DB for a customer with the given ID and
	 * returns a Customer bean with it's data from the DB.
	 *
	 * @param customerID The id of the customer to find in the DB.
	 * @return {@link CustomerBean} bean; <code>null</code> - if no customer with the given ID exists in DB
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CustomerException If the given customer's ID can't be found in the DB (0 rows were returned).
	 */
	CustomerBean getCustomer(long customerID) throws CouponSystemException;

	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CustomerBean> getAllCustomers() throws CouponSystemException;
	
	long customerLogin(String customerName, String password) throws CouponSystemException;
	boolean customerNameAlreadyExists(String custName) throws CouponSystemException;
	
	
}
