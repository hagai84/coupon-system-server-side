package core.service;

import java.io.Serializable;
import java.util.Collection;

import core.beans.CustomerBean;
import core.dao.CouponDAO;
import core.dao.CustomerDAO;
import core.dao.ICouponDAO;
import core.dao.ICustomerDAO;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
import core.util.ConnectionPool;

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
//	private final Customer customer;

	/**
	 * Private constructor that initializes given customer's access
	 * @param custId ID of the customer that is accessing the system
	 * 
	 * @return CustomerFacadeException
	 */
	private CustomerService() {
		
	}
	
	public static CustomerService getInstance() {
		return customerServiceInstance;
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
	public long createCustomer(CustomerBean customer) throws CouponSystemException {
		checkCustomer(customer);
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if(customerDAO.customerNameAlreadyExists(customer.getCustName())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Customer Name already exists");
		}
//		customer.setId(IdGenerator.generatCustomerId());		
		//IS ALSO HANDLED BY DAO LAYER
		/*if(customerDAO.customerIdAlreadyExists(customer.getId())) {
			throw new CouponSystemException(ExceptionsEnum.ID_EXISTS,"Customer ID already exists");
		}*/
		return customerDAO.createCustomer(customer);
		
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
		checkCustomer(customer);
		CustomerBean tmpCustomer = getCustomer(customer.getId());
		tmpCustomer.setPassword(customer.getPassword());
		customerDAO.updateCustomer(tmpCustomer);
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

	/**
	 * Logs in to the coupon system as a specific company.
	 * @param custName Customer username
	 * @param password Customer password
	 * @return a new CustomerFacade instance if customer's username and password are correct; otherwise, throws {@link CustomerService}
	 * @throws CustomerFacadeException if username or password are incorrect
	 */
	//@Override
	public long customerLogin(String customerName, String password) throws  CouponSystemException {
		return customerDAO.customerLogin(customerName, password);
	}

	public CustomerBean getCustomerByName(String customerName) throws CouponSystemException {
		// TODO Auto-generated method stub
		return customerDAO.getCustomerByName(customerName);
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
