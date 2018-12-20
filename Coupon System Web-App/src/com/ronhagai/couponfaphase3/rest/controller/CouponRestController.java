package com.ronhagai.couponfaphase3.rest.controller;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.enums.CouponType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.service.CouponService;

/**
 * An interface for a DAO class which provides access to {@link CouponBean} DTO data type.
 * 
 * @author Ron
 *
 */

@Path("/coupons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CouponRestController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CouponService couponService = CouponService.getInstance();

	/**
	 * Adds a new coupon entity to the repository.
	 * 
	 * @param coupon the new CouponBean object to be added.
	 * @return the created coupon's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing title, (3) Invalid data, (4) security breach.
	 */
	@POST
	public long createCoupon(CouponBean coupon, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.createCoupon(coupon, companyId);
	}
	
	/**
	 * Updates a coupon entity in the repository.
	 * 
	 * @param coupon the CouponBean object to be updated.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 * 	(3) Invalid data, (4) security breach.
	 */
	@PUT
	public void updateCoupon(CouponBean coupon, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.updateCoupon(coupon, companyId);
	}

	/**
	 * Updates a coupon entity's amount in the repository.
	 * cannot be resolve if it results in a negative amount. 
	 * 
	 * @param couponId the coupon's ID.
	 * @param amountDelta the amount of coupons to be added or removed (negative amount).
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : negative delta to exceeds stock,
	 *  no matching data, (3) Invalid data, (4) security breach.
	 */
	@PUT
	@Path("/amount/{couponId}")
	public void updateCouponAmout(@PathParam("couponId") long couponId,@QueryParam("amountDelta") int amoutDelta, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.updateCouponAmout(couponId, companyId, amoutDelta);
	}
	
	/**
	 * Removes a coupon entity from the coupons and customers' coupons repositories.
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 *  (3) Invalid data, (4) security breach.
	 */
	@DELETE
	@Path("/{couponId}")
	public void removeCoupon(@PathParam("couponId") long couponId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.removeCoupon(couponId, companyId);
	}
	 
	/**
	 * Adds a coupon to a customer entity, and updates the entity's amount in the repository.
	 * cannot be resolve if it results in a negative coupon's amount, or if customer already owns this coupon. 
	 * 
	 * @param couponId the coupon's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : out of stock,
	 *  existing ownership or no matching data.
	 */
	@PUT
	@Path("/{couponID}")
	public void purchaseCoupon(@PathParam("couponId") long couponId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.purchaseCoupon(couponId, customerId);
	}

	/**
	 * Retrieves a coupon entity from the repository.
	 * 
	 * @param couponId the coupon's ID.
	 * @return a CouponBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/{couponId}")
	public CouponBean getCoupon(@PathParam("couponId") long couponID) throws CouponSystemException {
		return couponService.getCoupon(couponID);
	}

	/**
	 * Retrieves all the coupons entities of said type from the repository .
	 * 
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/type")
	public Collection<CouponBean> getCouponsByType(@QueryParam("couponType") CouponType couponType) throws CouponSystemException {
		return couponService.getCouponsByType(couponType);
	}

	/**
	 * Retrieves all the coupons entities from the repository .
	 * 
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	public Collection<CouponBean> getAllCoupons(@Context HttpServletRequest request) throws CouponSystemException {
		return couponService.getAllCoupons();
	}

	/**
	 * Retrieves all the coupons entities for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}")
	public Collection<CouponBean> getCompanyCoupons(@Context HttpServletRequest request, @PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCoupons(companyId);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/type")
	public Collection<CouponBean> getCompanyCouponsByType(@QueryParam("couponType") CouponType type, @PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCouponsByType(companyId, type);
	}

	/**
	 * Retrieves all the coupons entities bellow said Price for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/price")
	public Collection<CouponBean> getCompanyCouponsByPrice(@QueryParam("price") double price, @PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCouponsByPrice(companyId, price);
	}
	
	/**
	 * Retrieves all the coupons entities expiring before said Date for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param date the latest (max) expiration Date.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/date")
	public Collection<CouponBean> getCompanyCouponsByDate(@QueryParam("date") Date date, @PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCouponsByDate(companyId, date);
	}
	
	/**
	 * Retrieves all the coupons entities for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/customer/{customerId}")
	public Collection<CouponBean> getCustomerCoupons(@PathParam("customerId") long customerId) throws CouponSystemException {
		return couponService.getCustomerCoupons(customerId);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param type the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/customer/{customerId}/type")
	public Collection<CouponBean> getCustomerCouponsByType(@QueryParam("couponType") CouponType type, @PathParam("customerId") long customerId) throws CouponSystemException {
		return couponService.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Retrieves all the coupons entities bellow said Price for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param price the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/customer/{customerId}/price")
	public Collection<CouponBean> getCustomerCouponsByPrice(@QueryParam("price") double price, @PathParam("customerId") long customerId) throws CouponSystemException {
		return couponService.getCustomerCouponsByPrice(customerId, price);
	}
}
