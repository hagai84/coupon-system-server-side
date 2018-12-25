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
import com.ronhagai.couponfaphase3.core.enums.ClientType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.service.CompanyService;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyRestController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyService companyService = CompanyService.getInstance();


	/**
	 * Creates a new company in the database
	 * @param company Company to add to the DB
	 * @throws AdminFacadeException
	 *  If company name or ID already exist in the DB
	 *  If company creation fails
	 */
	@POST
	public long createCompany(CompanyBean company, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
//		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
//		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		return companyService.createCompany(company/*, userId, userType*/);
	}
	

	/**
	 * Updates a company's details in the database
	 * @param company Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If updating of the company's details fails
	 */
	@PUT
	public void updateCompany(CompanyBean company, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		companyService.updateCompany(company, userId, userType);
	}

	@PUT
	@Path("/{companyId}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateCompanyPassword(@PathParam ("companyId") long companyId, @FormParam("oldPassword") String oldPassword,@FormParam("newPassword") String newPassword, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		companyService.updateCompanyPassword(companyId, oldPassword, newPassword, userId, userType);
	}
	/**
	 * Deletes a company in the database
	 * -removes its coupons from DB (all tables)
	 * @param companyId Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If company deletion fails
	 */
	@DELETE
	@Path("{companyId}")
	public void removeCompany(@PathParam ("companyId") long companyId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		ClientType userType = ((ClientType)httpServletRequest.getAttribute("userType"));
		companyService.removeCompany(companyId, userId, userType);
	}


	
	/**
	 * Gets a company's details from the database
	 * 
	 * @param companyID ID of company to retrieve from the DB
	 * @return Company the company with the matching id
	 * @throws AdminFacadeException
	 *  If there is a connection problem or an SQLException is thrown to the DAO.
	 *  If the given company's ID can't be found in the DB (0 rows were returned).
	 */
	@GET
	@Path("{companyId}")
	public CompanyBean getCompany(@PathParam("companyId")long companyId) throws CouponSystemException {	
		return companyService.getCompany(companyId);
	}

	

	
	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws AdminFacadeException If there is a connection problem or an <code>SQLException</code> is thrown to the DAO.
	 */
	@GET
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException{
		return companyService.getAllCompanies();
	}


//	/**
//	 * Logs in to the coupon system as a specific company.
//	 * @param name Company username
//	 * @param password Company password
//	 * @return a new CompanyFacade instance if company's username and password are correct; otherwise, throws {@link CompanyController}
//	 * @throws CompanyFacadeException if username or password are incorrect
//	 */
//	
//	public long companyLogin(String name, String password) throws CouponSystemException {
//		return companyDAO.companyLogin(name, password);
//	}
	/**
	 * Logs in to the coupon system as a specific company.
	 * @param custName Customer username
	 * @param password Customer password
	 * @return a new CustomerFacade instance if customer's username and password are correct; otherwise, throws {@link CustomerService}
	 * @throws CustomerFacadeException if username or password are incorrect
	 */
	/*@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public long companyLogin(@FormParam("name") String companyName,@FormParam("password") String password) throws  CouponSystemException {
		if(companyName == null || password == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"name/password cant be null");
		}
		return companyService.companyLogin(companyName, password);
	}*/

}