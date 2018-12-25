package com.ronhagai.couponfaphase3.rest.controller;

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

import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.enums.ClientType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.service.CustomerService;

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
	 * Adds a new customer entity to the repository.
	 * 
	 * @param customer the new customer entity to be added.
	 * @return the created customer's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing name, (3) Invalid data.
	 */
	@POST
	public long createCustomer(CustomerBean customer, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		return customerService.createCustomer(customer);			
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
	@PUT
	public void updateCustomer(CustomerBean customer, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {		
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		customerService.updateCustomer(customer, userId, userType);
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
	@PUT
	@Path("/{customerId}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateCustomerPassword(@PathParam("customerId") long customerId, @FormParam("oldPassword") String oldPassword,@FormParam("newPassword") String newPassword, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		customerService.updateCustomerPassword(customerId, oldPassword, newPassword, userId, userType);
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
	@DELETE
	@Path("/{customerId}")
	public void removeCustomer(@PathParam("customerId") long customerId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {			
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		customerService.removeCustomer(customerId, userId, userType);				
	}
	
	/**
	 * Retrieves a customer entity from the repository.
	 * 
	 * @param customerId the customer's ID.
	 * @return a CompanyBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/{customerId}")
	public CustomerBean getCustomer(@PathParam("customerId") long customerId) throws CouponSystemException {
		System.out.println("api get customer is cold");
		return customerService.getCustomer(customerId);
	}
	
	/**
	 * Retrieves all the customers entities from the repository .
	 * 
	 * @return a Collection of customers objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException{
		return customerService.getAllCustomers();
	}
}
