package api;

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
import core.controller.CouponController;
import core.dao.CouponDAO;
import core.dao.ICouponDAO;
import core.enums.CouponType;
import core.exception.CouponSystemException;

/*import core.beans.CouponBean;
import core.controller.CompanyFacadeException;
import core.controller.CustomerFacadeException;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.util.ConnectionPool;
import core.util.IdGenerator;
import core.validation.CouponBeanValidator;*/

@Path("/Coupons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CouponAPI {
	
	private final CouponController couponController = new CouponController();

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
		couponController.createCoupon(coupon, companyId);
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
			couponController.updateCoupon(coupon, companyId);
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
		couponController.removeCoupon(couponId);
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
	@PUT
	@Path("/{couponID}/{customerId}")
	public void purchaseCoupon(@PathParam("couponId") long couponId, @PathParam("customerId") long customerId) throws CouponSystemException {
		couponController.purchaseCoupon(couponId, customerId);
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
		return couponController.getCoupon(couponID);
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
		return couponController.getCouponsByType(couponType);
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

		return couponController.getAllCoupons();
	}

	/**
	 * Returns all available coupons of the given company
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/Company/{companyId}")
	public Collection<CouponBean> getCompanyCoupons(@PathParam("companyId") long companyId) throws CouponSystemException {
		return couponController.getCompanyCoupons(companyId);
	}

	/**
	 * Returns all available coupons of the given company with a specific type
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@Path("/type/Company/{companyId}")
	public Collection<CouponBean> getCompanyCouponsByType(@PathParam("companyId") long companyId,@QueryParam("couponType") CouponType type)
			throws CouponSystemException {
		return couponController.getCompanyCouponsByType(companyId, type);
	}

	/**
	 * Returns all available coupons of the given Customer
	 * 
	 * @param customerId the Customer id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@GET
	@Path("/Customer/{customerId}")
	public Collection<CouponBean> getCustomerCoupons(@PathParam("customerId") long customerId) throws CouponSystemException {
		return couponController.getCustomerCoupons(customerId);
	}

	/**
	 * Returns all available coupons of the given customer with a specific type
	 * 
	 * @param customerId the customer id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@Path("/type/Company/{companyId}")
	public Collection<CouponBean> getCustomerCouponsByType(@PathParam("customerId") long customerId,@QueryParam("couponType") CouponType type) throws CouponSystemException {
		return couponController.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Gets all coupons up to a certain price that were purchased by the customer
	 * 
	 * @param customerId
	 * @param price      the max price of the coupons to select
	 * @return Collection of Coupons associated with the Customer
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	@Path("/price/Customer/{customerId}")
	public Collection<CouponBean> getCustomerCouponsByPrice(@PathParam("customerId") long customerId,@QueryParam("couponPrice") Double price) throws CouponSystemException {
		return couponController.getCustomerCouponsByPrice(customerId, price);
	}
}
