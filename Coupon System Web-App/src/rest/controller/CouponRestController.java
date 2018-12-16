package rest.controller;

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

import core.beans.CouponBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.service.CouponService;



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
	 * Adds a new {@link CouponBean} to the DB in the following order:
	 * <ul>
	 * <li>To Table <code>coupon</code></li>
	 * <li>To Table <code>comp_coupon</code></li>
	 * </ul>
	 * 
	 * @param coupon The new {@link CouponBean} to be added.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	
	@POST
	public long createCoupon(CouponBean coupon, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.createCoupon(coupon, companyId);
	}
	/**
	 * Updates a specific {@link CouponBean} in the DB.
	 *
	 * @param clientCoupon The coupon to be updated.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	@PUT
	public void updateCoupon(CouponBean coupon, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.updateCoupon(coupon, companyId);
	}

	@PUT
	@Path("/amount/{couponId}")
	public void updateCouponAmout(@PathParam("couponId") long couponId,@QueryParam("amountDelta") int amoutDelta, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.updateCouponAmout(couponId, companyId, amoutDelta);
	}
	/**
	 * Removes a {@link CouponBean} to the DB in the following order:
	 * <ul>
	 * <li>From Table <code>cust_coupon</code></li>
	 * <li>From Table <code>comp_coupon</code></li>
	 * <li>From Table <code>coupon</code></li>
	 * </ul>
	 *
	 * @param couponId The coupon to be removed.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	@DELETE
	@Path("/{couponId}")
	public void removeCoupon(@PathParam("couponId") long couponId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.removeCoupon(couponId, companyId);
	}
	 
	/**
	 * Purchases the given coupon, adds it to the customer and updates the coupons's
	 * amount. -checks if customer owns this coupon
	 * 
	 * @param couponId Coupon to purchase
	 * @throws CustomerFacadeException If coupon is out of stock or expired
	 * @throws CustomerFacadeException If coupon purchase fails
	 */
	@PUT
	@Path("/{couponID}")
	public void purchaseCoupon(@PathParam("couponId") long couponId, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long customerId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		couponService.purchaseCoupon(couponId, customerId);
	}


	/**
	 * Fetches a specific {@link CouponBean} from the DB using its ID.
	 *
	 * @param couponID The ID of the desired {@link CouponBean}.
	 * @return The coupon that matches the ID; <br>
	 *         <code>null</code> if there is no match.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	@GET
	@Path("/{couponId}")
	public CouponBean getCoupon(@PathParam("couponId") long couponID) throws CouponSystemException {
		return couponService.getCoupon(couponID);
	}

	/**
	 * Gets all coupons of the given CouponType
	 * 
	 * @param couponType The Type of coupons desired.
	 * @return a Collection of all coupons with matching a Type
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	
	@GET
	@Path("/type")
	public Collection<CouponBean> getCouponsByType(@QueryParam("couponType") CouponType couponType) throws CouponSystemException {
		System.out.println("Get with query param");
		return couponService.getCouponsByType(couponType);
	}

	/**
	 * Returns all available coupons
	 * 
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	public Collection<CouponBean> getAllCoupons(@Context HttpServletRequest request) throws CouponSystemException {
		System.out.println("Get without query param");

		return couponService.getAllCoupons();
	}

	/**
	 * Returns all available coupons of the given company
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/company")
	public Collection<CouponBean> getCompanyCoupons(@Context HttpServletRequest request, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCompanyCoupons(companyId);
	}

	/**
	 * Returns all available coupons of the given company with a specific type
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/company/type")
	public Collection<CouponBean> getCompanyCouponsByType(@QueryParam("couponType") CouponType type, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCompanyCouponsByType(companyId, type);
	}

	@GET
	@Path("/company/price")
	public Collection<CouponBean> getCompanyCouponsByPrice(@QueryParam("price") double price, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCompanyCouponsByPrice(companyId, price);
	}
	@GET
	@Path("/company/date")
	public Collection<CouponBean> getCompanyCouponsByDate(@QueryParam("date") Date date, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long companyId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long companyId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCompanyCouponsByDate(companyId, date);
	}
	/**
	 * Returns all available coupons of the given Customer
	 * 
	 * @param customerId the Customer id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/customer")
	public Collection<CouponBean> getCustomerCoupons(@Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long customerId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCustomerCoupons(customerId);
	}

	/**
	 * Returns all available coupons of the given customer with a specific type
	 * 
	 * @param customerId the customer id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/customer/type")
	public Collection<CouponBean> getCustomerCouponsByType(@QueryParam("couponType") CouponType type, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long customerId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Gets all coupons up to a certain price that were purchased by the customer
	 * 
	 * @param customerId
	 * @param price      the max price of the coupons to select
	 * @return Collection of Coupons associated with the Cu stomer
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/customer/price")
	public Collection<CouponBean> getCustomerCouponsByPrice(@QueryParam("price") double price, @Context HttpServletRequest httpServletRequest) throws CouponSystemException {
		long customerId = Long.parseLong(httpServletRequest.getHeader("userId"));
//		long customerId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		return couponService.getCustomerCouponsByPrice(customerId, price);
	}
}
