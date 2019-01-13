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
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.beans.UserBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.service.CompanyService;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyRestController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyService companyService = CompanyService.getInstance();

	/**
	 * Adds a new company entity to the repository.
	 * 
	 * @param company the new company entity to be added.
	 * @return the created company's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts such as : existing name, (3)
	 *                               Invalid data.
	 */
	@POST
	public long createCompany(CompanyBean companyBean, @Context HttpServletRequest httpServletRequest)
			throws CouponSystemException {
		checkIfBeanDontHaveNullData(companyBean);
		return companyService.createCompany(companyBean);
	}

	/**
	 * Updates a company entity in the repository.
	 * 
	 * @param companyBean the company object to be updated.
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts such as : no matching data,
	 *                               (3) Invalid data, (4) security breach.
	 */
	@PUT
	public void updateCompany(CompanyBean companyBean, @Context HttpServletRequest httpServletRequest)
			throws CouponSystemException {
		checkIfBeanDontHaveNullData(companyBean);
		long userId = ((Long) httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType) httpServletRequest.getAttribute("userType"));
		companyService.updateCompany(companyBean, userId, userType);
	}

	/**
	 * updates the company's password
	 * 
	 * @param companyId   The company to update
	 * @param newPassword The new password
	 * @param oldPassword The old password
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts.
	 */
	@PUT
	@Path("/{companyId}/password")
	public void updateCompanyPassword(UserBean userBean, @PathParam("companyId") long companyId,
			@Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		if(userBean.getUserName() ==null || userBean.getUserPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"name or password cant be null");
		}
		long userId = ((Long) httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType) httpServletRequest.getAttribute("userType"));
		// using userBean as substitute for loginBean, same on client side
		companyService.updateCompanyPassword(companyId, userBean.getUserName(), userBean.getUserPassword(),
				userId, userType);
	}

	/**
	 * Removes a company entity from the companies repositories. removes the
	 * company's coupons as well.
	 * 
	 * @param companyId the company's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts such as : no matching data,
	 *                               (3) Invalid data, (4) security breach.
	 */
	@DELETE
	@Path("{companyId}")
	public void removeCompany(@PathParam("companyId") long companyId, @Context HttpServletRequest httpServletRequest)
			throws CouponSystemException {
		long userId = ((Long) httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType) httpServletRequest.getAttribute("userType"));
		companyService.removeCompany(companyId, userId, userType);
	}

	/**
	 * Retrieves a company entity from the repository.
	 * 
	 * @param companyId the company's ID.
	 * @return a CompanyBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("{companyId}")
	public CompanyBean getCompany(@PathParam("companyId") long companyId) throws CouponSystemException {
		return companyService.getCompany(companyId);
	}

	/**
	 * Retrieves all the companies entities from the repository .
	 * 
	 * @return a Collection of companies objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error,
	 *                               (2) data conflicts such as : no matching data.
	 */
	@GET
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException {
		return companyService.getAllCompanies();
	}

	private void checkIfBeanDontHaveNullData(CompanyBean companyBean) throws CouponSystemException {
		if (companyBean.getCompName() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"name cant be null");
		}
		if (companyBean.getEmail() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"email cant be null");
		}
		if (companyBean.getPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.DATA_CANT_BE_NULL,"password cant be null");
		}
	}
	
}
