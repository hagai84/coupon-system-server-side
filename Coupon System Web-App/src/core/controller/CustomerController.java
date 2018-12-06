package core.controller;

import java.util.Collection;

import core.beans.CustomerBean;
import core.dao.CouponDAO;
import core.dao.CustomerDAO;
import core.dao.ICouponDAO;
import core.dao.ICustomerDAO;
import core.exception.CouponSystemException;
import core.util.ConnectionPool;
import core.util.IdGenerator;
import core.validation.CustomerBeanValidator;

/**
 * Facade used to access the coupon system by Customers
 * @author Yair
 *
 */
public class CustomerController implements IController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final ICustomerDAO customerDAO = CustomerDAO.getInstance();
	private static final ICouponDAO couponDAO = CouponDAO.getInstance();
//	private final Customer customer;

	/**
	 * Private constructor that initializes given customer's access
	 * @param custId ID of the customer that is accessing the system
	 * 
	 * @return CustomerFacadeException
	 */
	public CustomerController() {
		
	}

	/**
	 * Logs in to the coupon system as a specific company.
	 * @param custName Customer username
	 * @param password Customer password
	 * @return a new CustomerFacade instance if customer's username and password are correct; otherwise, throws {@link CustomerController}
	 * @throws CustomerFacadeException if username or password are incorrect
	 */
	//@Override
	public long customerLogin(String customerName, String password) throws  CouponSystemException {
		return customerDAO.login(customerName, password);
	}

	
	
	/**
	 * Attempts to create a given customer in the DB
	 *
	 * @param customer The customer to create
	 * @throws CouponSystemException
	 *  If there is a connection problem or <code>SQLException</code> is thrown to the DAO.
	 *  If insertion of the given customer to the DB fails (e.g. <code>Customer</code> ID already exists or is invalid).
	 *
	 */
	public void createCustomer(CustomerBean customer) throws CouponSystemException {
		CustomerBeanValidator.checkCustomer(customer);
		
		if(customerDAO.customerNameAlreadyExists(customer.getCustName())) {
			throw new CouponSystemException("Customer Name already exists");
		}
		customer.setId(IdGenerator.generatCustomerId());
		
		if(customerDAO.customerIdAlreadyExists(customer.getId())) {
			throw new CouponSystemException("Customer ID already exists");
		}
		customerDAO.createCustomer(customer);
		
	}

	/**
	 * Deletes a specified customer from the DB.
	 * -removes its coupons from the DB (customer_coupon table)
	 *
	 * @param customerId The customer to be removed.
	 * @throws CouponSystemException
	 *  If there is a connection problem or an <code>SQLException</code> is thrown.
	 *  If the given customer's ID can't be found in the DB.
	 *
	 */
	public void removeCustomer(long customerId) throws CouponSystemException {
		ConnectionPool.getInstance().startTransaction();
		try {
			couponDAO.removeCustomerCoupons(customerId);
			customerDAO.removeCustomer(customerId);	
		}catch (CouponSystemException e) {
			ConnectionPool.getInstance().rollback();	
			throw e;
		}finally {
		}
		ConnectionPool.getInstance().endTransaction();			
		
	}

	/**
	 * Updates all of a customer's fields (except ID) in the DB according to the given customer bean.
	 *
	 * @param customer The customer to be updated
	 * @throws CouponSystemException
	 *  If there is a connection problem or an <code>SQLException</code> is thrown.
	 *  If the given customer's ID can't be found in the DB (0 rows were updated).
	 */
	public void updateCustomer(CustomerBean customer) throws CouponSystemException {		
		CustomerBeanValidator.checkCustomer(customer);
		CustomerBean tmpCustomer = getCustomer(customer.getId());
		tmpCustomer.setPassword(customer.getPassword());
		customerDAO.updateCustomer(tmpCustomer);
	}

	/**
	 * Searches the DB for a customer with the given ID and
	 * returns a Customer bean with it's data from the DB.
	 *
	 * @param customerId The id of the customer to find in the DB.
	 * @return {@link CustomerBean} bean; <code>null</code> - if no customer with the given ID exists in DB
	 * @throws CouponSystemException
	 *  If there is a connection problem or an <code>SQLException</code> is thrown.
	 *  If the given customer's ID can't be found in the DB (0 rows were returned).
	 */
	public CustomerBean getCustomer(long customerId) throws CouponSystemException {
		return customerDAO.getCustomer(customerId);
	}

	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException{
		return customerDAO.getAllCustomers();
	}
}
