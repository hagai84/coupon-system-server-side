package rest.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import core.beans.CustomerBean;
import core.exception.CouponSystemException;
import core.service.CustomerService;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerRestController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CustomerService customerService = CustomerService.getInstance();
	
	/**
	 * Attempts to create a given customer in the DB
	 *
	 * @param customer The customer to create
	 * @throws CouponSystemException
	 *  If there is a connection problem or <code>SQLException</code> is thrown to the DAO.
	 *  If insertion of the given customer to the DB fails (e.g. <code>Customer</code> ID already exists or is invalid).
	 *
	 */
	
	@POST
	public long createCustomer(CustomerBean customer) throws CouponSystemException {
		return customerService.createCustomer(customer);			
	}
	
	/**
	 * Updates all of a customer's fields (except ID) in the DB according to the given customer bean.
	 *
	 * @param customer The customer to be updated
	 * @throws CouponSystemException
	 *  If there is a connection problem or an <code>SQLException</code> is thrown.
	 *  If the given customer's ID can't be found in the DB (0 rows were updated).
	 */
	@PUT
	public void updateCustomer(CustomerBean customer) throws CouponSystemException {		
		customerService.updateCustomer(customer);
	}
	
	
	@PUT
	@Path("password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateCustomerPassword(@FormParam("oldPassword") String oldPassword,@FormParam("newPassword") String newPassword, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = Long.parseLong(httpServletRequest.getHeader("userId"));
		customerService.updateCustomerPassword(customerId, oldPassword, newPassword);
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
	@DELETE
	@Path("/{customerId}")
	public void removeCustomer(@PathParam("customerId") long customerId) throws CouponSystemException {			
		customerService.removeCustomer(customerId);				
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
	@GET
	@Path("/{customerId}")
	public CustomerBean getCustomer(@PathParam("customerId") long customerId) throws CouponSystemException {
		return customerService.getCustomer(customerId);
	}
	
	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	@GET
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException{
		return customerService.getAllCustomers();
	}
	
	/**
	 * Logs in to the coupon system as a specific customer.
	 * @param custName Customer username
	 * @param password Customer password
	 * @return a new CustomerFacade instance if customer's username and password are correct; otherwise, throws {@link CustomerService}
	 * @throws CustomerFacadeException if username or password are incorrect
	 */
	/*@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public long customerLogin(@FormParam("name") String customerName,@FormParam("password") String password) throws  CouponSystemException {
		if(customerName == null || password == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"name/password cant be null");
		}
		return customerService.customerLogin(customerName, password);
	}*/
	
}
