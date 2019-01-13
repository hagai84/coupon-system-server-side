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

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.beans.UserBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
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
	 * @param customerBean the new customer entity to be added.
	 * @return the created customer's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing name, (3) Invalid data.
	 */
	@POST
	public long createCustomer(CustomerBean customerBean, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		checkIfBeanDontHaveNullData(customerBean);
		return customerService.createCustomer(customerBean);			
	}
	
	/**
	 * Updates a customer entity in the repository.
	 * 
	 * @param customerBean the customer object to be updated.
	 * @param userId the user updating the customer
	 * @param userType the user type updating the customer
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 * 	(3) Invalid data, (4) security breach.
	 */
	@PUT
	public void updateCustomer(CustomerBean customerBean, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {		
		checkIfBeanDontHaveNullData(customerBean);
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		customerService.updateCustomer(customerBean, userId, userType);
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
	public void updateCustomerPassword(UserBean userBean, @PathParam("customerId") long customerId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		if(userBean.getUserName() ==null || userBean.getUserPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"name or password cant be null");
		}
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		customerService.updateCustomerPassword(customerId, userBean.getUserName(), userBean.getUserPassword(), userId, userType);
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
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
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
	
	private void checkIfBeanDontHaveNullData(CustomerBean customerBean) throws CouponSystemException {
		if (customerBean.getCustName()== null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"name cant be null");
		}
		if (customerBean.getPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"password cant be null");
		}
	}
}
