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

import com.ronhagai.couponfaphase3.core.beans.CartBean;
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;
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
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		return couponService.createCoupon(coupon, userId, userType);
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
	@Path("/{couponId}/{customerId}")
	public void purchaseCoupon(@PathParam("couponId") long couponId, @PathParam("customerId") long customerId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		couponService.purchaseCoupon(couponId, customerId, userId, userType);
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
	@Path("/cart/{customerId}")
	public CartBean checkoutCart(CartBean cartBean, @PathParam("customerId") long customerId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		return couponService.checkoutCart(cartBean, customerId, userId, userType);
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
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		couponService.updateCoupon(coupon, userId, userType);
		couponService.updateCouponAmount(coupon, userId, userType);
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
//	@PUT
//	@Path("/amount/{couponId}")
//	public void updateCouponAmount(@PathParam("couponId") long couponId,@QueryParam("amountDelta") int amoutDelta, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
//		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
//		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
//		couponService.updateCouponAmout(couponId, amoutDelta, userId, userType);
//	}
	
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
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		couponService.removeCoupon(couponId, userId, userType);
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
	@Path("/couponType")
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
	public Collection<CouponBean> getCompanyCoupons(@PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCoupons(companyId);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param couponType the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/couponType")
	public Collection<CouponBean> getCompanyCouponsByType(@QueryParam("couponType") CouponType couponType, @PathParam("companyId") long companyId) throws CouponSystemException {
		return couponService.getCompanyCouponsByType(companyId, couponType);
	}

	/**
	 * Retrieves all the coupons entities bellow said Price for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param couponPrice the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/couponPrice")
	public Collection<CouponBean> getCompanyCouponsByPrice(@PathParam("companyId") long companyId, @QueryParam("couponPrice") double couponPrice) throws CouponSystemException {
		return couponService.getCompanyCouponsByPrice(companyId, couponPrice);
	}
	
	/**
	 * Retrieves all the coupons entities expiring before said Date for said Company from the repository .
	 * 
	 * @param companyId the company's Id.
	 * @param expirationDate the latest (max) expiration Date.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/company/{companyId}/expirationDate")
	public Collection<CouponBean> getCompanyCouponsByDate(@PathParam("companyId") long companyId, @QueryParam("expirationDate") Date expirationDate) throws CouponSystemException {
		return couponService.getCompanyCouponsByDate(companyId, expirationDate);
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
	public Collection<CouponBean> getCustomerCoupons(@PathParam("customerId") long customerId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		return couponService.getCustomerCoupons(customerId, userId, userType);
	}

	/**
	 * Retrieves all the coupons entities of said Type for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param couponType the coupons Type.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/customer/{customerId}/couponType")
	public Collection<CouponBean> getCustomerCouponsByType(@PathParam("customerId") long customerId, @QueryParam("couponType") CouponType couponType, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		return couponService.getCustomerCouponsByType(customerId, couponType, userId, userType);
	}

	/**
	 * Retrieves all the coupons entities bellow said Price for said Customer from the repository .
	 * 
	 * @param customerId the customer's Id.
	 * @param couponPrice the coupons Price.
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	@GET
	@Path("/customer/{customerId}/couponPrice")
	public Collection<CouponBean> getCustomerCouponsByPrice(@PathParam("customerId") long customerId, @QueryParam("couponPrice") double couponPrice, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		return couponService.getCustomerCouponsByPrice(customerId, couponPrice, userId, userType);
	}
}
