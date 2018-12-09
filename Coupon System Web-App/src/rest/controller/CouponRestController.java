package rest.controller;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	@Path("/{companyId}")
	public void createCoupon(CouponBean coupon, @PathParam("companyId") long companyId) throws CouponSystemException {
		couponService.createCoupon(coupon, companyId);
	}


	/**
	 * Updates a specific {@link CouponBean} in the DB.
	 *
	 * @param clientCoupon The coupon to be updated.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	@PUT
	@Path("/{companyId}")
	public void updateCoupon(CouponBean coupon, @PathParam("companyId") long companyId) throws CouponSystemException {
			couponService.updateCoupon(coupon, companyId);
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
	public void removeCoupon(@PathParam("couponId") long couponId) throws CouponSystemException {
		couponService.removeCoupon(couponId);
	}

	/*
	 * private boolean companyNameExists(Company company) throws DAOException { //
	 * TODO Auto-generated method stub try {
	 * companyDAO.getCompanyByName(company.getCompName()); } catch (CompanyException
	 * e) { // No Company Found return false; } return true; }
	 */
	/**
	 * Purchases the given coupon, adds it to the customer and updates the coupons's
	 * amount. -checks if customer owns this coupon
	 * 
	 * @param couponId Coupon to purchase
	 * @throws CustomerFacadeException If coupon is out of stock or expired
	 * @throws CustomerFacadeException If coupon purchase fails
	 */
	@GET
	@Path("/{couponID}/{customerId}")
	public void purchaseCoupon(@PathParam("couponId") long couponId, @PathParam("customerId") long customerId) throws CouponSystemException {
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
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
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
	@Path("/company/{companyId}")
	public Collection<CouponBean> getCompanyCoupons(@PathParam("companyId") long companyId) throws CouponSystemException {
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
	@Path("/type/company/{companyId}")
	public Collection<CouponBean> getCompanyCouponsByType(@PathParam("companyId") long companyId,@QueryParam("couponType") CouponType type)
			throws CouponSystemException {
		return couponService.getCompanyCouponsByType(companyId, type);
	}

	@GET
	@Path("/price/company/{companyId}")
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException {
		return couponService.getCompanyCouponsByPrice(companyId, price);
	}
	@GET
	@Path("/date/company/{companyId}")
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException {
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
	@Path("/customer/{customerId}")
	public Collection<CouponBean> getCustomerCoupons(@PathParam("customerId") long customerId) throws CouponSystemException {
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
	@Path("/type/customer/{companyId}")
	public Collection<CouponBean> getCustomerCouponsByType(@PathParam("customerId") long customerId,@QueryParam("couponType") CouponType type) throws CouponSystemException {
		return couponService.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Gets all coupons up to a certain price that were purchased by the customer
	 * 
	 * @param customerId
	 * @param price      the max price of the coupons to select
	 * @return Collection of Coupons associated with the Customer
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/price/customer/{customerId}")
	public Collection<CouponBean> getCustomerCouponsByPrice(@PathParam("customerId") long customerId,@QueryParam("couponPrice") Double price) throws CouponSystemException {
		return couponService.getCustomerCouponsByPrice(customerId, price);
	}
}