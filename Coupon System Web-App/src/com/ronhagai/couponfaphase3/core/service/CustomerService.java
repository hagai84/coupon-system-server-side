package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.CustomerDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICustomerDAO;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Facade used to access the coupon system by Customers
 * @author Ron
 *
 */
public class CustomerService implements Serializable, IBeanValidatorConstants{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CustomerService customerServiceInstance = new CustomerService();
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	private ICustomerDAO customerDAO = CustomerDAO.getInstance();
	private ICouponDAO couponDAO = CouponDAO.getInstance();


	private CustomerService() {
		
	}
	
	public static CustomerService getInstance() {
		return customerServiceInstance;
	}

	/**
	 * Adds a new customer entity to the repository.
	 * 
	 * @param customer the new customer entity to be added.
	 * @return the created customer's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing name, (3) Invalid data.
	 */
	public long createCustomer(CustomerBean customer) throws CouponSystemException {
		//can be used if customer registration should be restricted 
		
		checkCustomer(customer);
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if(customerDAO.customerNameAlreadyExists(customer.getCustName())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Customer Name already exists");
		}

		long customerId = customerDAO.createCustomer(customer);
		System.out.println("LOG : Customer created : " + customer);
		return customerId;
	}

	/**
	 * Updates a customer entity in the repository.
	 * 
	 * @param customer the customer object to be updated.
	 * @param userId the user updating the customer
	 * @param userType the user type updating the customer
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 * 	(3) Invalid data, (4) security breach.
	 */
	public void updateCustomer(CustomerBean customer, long userId, UserType userType) throws CouponSystemException {		
		if ((customer.getId() != userId || !userType.equals(UserType.CUSTOMER)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to update customer " + customer);
		}
		checkCustomer(customer);
		CustomerBean tmpCustomer = getCustomer(customer.getId());
		tmpCustomer.setPassword(customer.getPassword());
		customerDAO.updateCustomer(tmpCustomer);
		System.out.println("LOG : User " + userType + " - " + userId + " updated customer " + customer);		
	}

	/**
	 * updates the customer's password
	 * 
	 * @param customerId The customer to update
	 * @param newPassword The new password
	 * @param oldPassword The old password
	 * @param userId the user updating the coupon
	 * @param userType the user type updating the coupon
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 */
	public void updateCustomerPassword(long customerId, String oldPassword, String newPassword, long userId, UserType userType) throws CouponSystemException {
		if ((customerId != userId || !userType.equals(UserType.CUSTOMER)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to change customer's password " + customerId);
		}
		if(userType.equals(UserType.CUSTOMER)) {			
			String customerName = customerDAO.getCustomer(customerId).getCustName();
			customerDAO.customerLogin(customerName, oldPassword);
		}		
		customerDAO.updateCustomerPassword(customerId, newPassword);
		System.out.println("LOG : User " + userType + " - " + userId + " changed customer's password " + customerId);
	}
	
	/**
	 * Removes a customer entity from the customers repositories.
	 * removes the customer's coupons as well.
	 * 
	 * @param customerId the customer's ID.
	 * @param userId the user removing the customer.
	 * @param userType the user type
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 *  (3) Invalid data, (4) security breach.
	 */
	public void removeCustomer(long customerId, long userId, UserType userType) throws CouponSystemException {
		//can be modified if customer removing should be restricted
		if ((customerId != userId || !userType.equals(UserType.CUSTOMER)) && !userType.equals(UserType.ADMIN)) {
//		if (!userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to remove customer " + customerId);
		}
		connectionPool.startTransaction();
		try {
			couponDAO.removeCustomerCoupons(customerId);
			customerDAO.removeCustomer(customerId);	
		}catch (CouponSystemException e) {
			connectionPool.rollback();	
			throw e;
		}finally {
		}
		connectionPool.endTransaction();			
		System.out.println("LOG : User " + userType + " - " + userId + " removed customer " + customerId);			
	}

	/**
	 * Retrieves a customer entity from the repository.
	 * 
	 * @param customerId the customer's ID.
	 * @return a CompanyBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public CustomerBean getCustomer(long customerId) throws CouponSystemException {
		return customerDAO.getCustomer(customerId);
	}

	/**
	 * Retrieves all the customers entities from the repository .
	 * 
	 * @return a Collection of customers objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException{
		return customerDAO.getAllCustomers();
	}

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
	public long customerLogin(String customerName, String password) throws  CouponSystemException {
		return customerDAO.customerLogin(customerName, password);
	}
	
	private void checkCustomer(CustomerBean customer) throws CouponSystemException {
		checkCustomerName(customer.getCustName());
		checkCustomerPassword(customer.getPassword());
	}

	

	private void checkCustomerName(String custName) throws CouponSystemException {
		if (custName.length() > CUST_NAME_LENGTH) {
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"The customer password cant be longer than " + CUST_PASSWORD_MAX_LENGTH + " characters");
		}
	}

	private void checkCustomerPassword(String password) throws CouponSystemException {
		if (password.length() > CUST_PASSWORD_MAX_LENGTH) {
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"The customer password need to be longer than " + CUST_PASSWORD_MAX_LENGTH + " characters");
		}
		if (password.length() < CUST_PASSWORD_MIN_LENGTH) {
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"The customer password can't be shorter than " + CUST_PASSWORD_MIN_LENGTH + " characters");
		}
	}
}
