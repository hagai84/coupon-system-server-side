/**
 * 
 */
package core.controller;

import java.util.Collection;

import core.beans.CouponBean;
import core.dao.CouponDAO;
import core.dao.ICouponDAO;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.util.ConnectionPool;
import core.util.IdGenerator;
import core.validation.CouponBeanValidator;

/**
 * Facade used to access the coupon system by Administrators
 * 
 * private constructor, instance is received thru static method login()
 * 
 * @author Ron
 *
 */
public class CouponController implements IController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ICouponDAO couponDAO = CouponDAO.getInstance();

	/**
	 * Private constructor
	 */
	public CouponController() {
	}

//	private final int RETRIES = 3;

	////////////////////////////////////////////////////////////////////

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
	public void createCoupon(CouponBean coupon, long companyId) throws CouponSystemException {
		CouponBeanValidator.checkCoupon(coupon);
		if (couponDAO.couponTitleAlreadyExists(coupon.getTitle())) {
			throw new CouponSystemException("Coupon Title already exists");
		}
		coupon.setCouponId(IdGenerator.generatCouponId());
		if (couponDAO.couponIdAlreadyExists(coupon.getCouponId())) {
			throw new CouponSystemException("Coupon ID already exists");
		}
		couponDAO.createCoupon(coupon, companyId);
	}

	/**
	 * Logs in to the coupon system as an admin.
	 * 
	 * @param name     Admin user name
	 * @param password Admin password
	 * @return a new AdminFacade instance if admin's user name and password are
	 *         correct; otherwise, throws {@link CouponController}
	 * @throws AdminFacadeException if user name or password are incorrect
	 */
	/*
	 * public static IController login(String name, String password) throws
	 * CouponSystemException { if(name.equals("admin")&&password.equals("1234")) {
	 * return new CouponController(); }else throw new
	 * CouponSystemException("Admin : incorrect user name and/or password "); }
	 */

	/**
	 * checks if the customer Id already exist
	 * 
	 * @param customer the customer to be checked
	 * @return true if customer id exists, false if doesn't
	 * @throws DAOException when DB error occurred
	 */
	/*
	 * private boolean customerIdExists(Customer customer) throws DAOException { //
	 * TODO Auto-generated method stub try {
	 * customerDAO.getCustomer(customer.getId()); } catch (CustomerException e) { //
	 * No Customer Found return false; } return true; }
	 */

	/**
	 * checks if the customer name already exist
	 * 
	 * @param customer the customer to be checked
	 * @return true if customer name exists, false if doesn't
	 * @throws DAOException when DB error occurred
	 */
	/*
	 * private boolean customerNameExists(Customer customer) throws DAOException {
	 * // TODO Auto-generated method stub try {
	 * customerDAO.getCustomerByName(customer.getCustName()); } catch
	 * (CustomerException e) { // No Customer Found return false; } return true; }
	 */

	/*
	 * public void removeCoupon(Coupon coupon) throws CompanyFacadeException {
	 * //check if the coupon exists // if this company is authorize to remove the
	 * coupon. getCoupon(coupon.getId()); this.checkIfCompanyAuthorize(coupon); try
	 * { couponDAO.removeCouponFromCompanies(coupon); // remove coupon from
	 * customer_coupon table ; couponDAO.removeCouponFromCustomers(coupon); //
	 * remove coupon from coupon table ; couponDAO.removeCoupon(coupon); } catch
	 * (DAOException | CouponException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant remove coupon", e); throw exception; } }
	 */

	/**
	 * Updates a specific {@link CouponBean} in the DB.
	 *
	 * @param clientCoupon The coupon to be updated.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public void updateCoupon(CouponBean coupon, long companyId) throws CouponSystemException {
		CouponBeanValidator.checkCoupon(coupon);
		// gets original coupon data
		// TODO Transaction, rollback for safety
		ConnectionPool.getInstance().startTransaction();
		try {
			CouponBean updatedCoupon = getCoupon(coupon.getCouponId());
			// TODO add code when tables change

			if (updatedCoupon.getCompanyId() == companyId) {
				throw new CouponSystemException("Coupon " + coupon.getCouponId() + " doesn't belong to company " + companyId);
			}

			// alter the coupon data to the new ALLOWED ones
			updatedCoupon.setEndDate(coupon.getEndDate());
			updatedCoupon.setAmount(coupon.getAmount());
			updatedCoupon.setPrice(coupon.getPrice());
			// updates the coupon
			couponDAO.updateCoupon(updatedCoupon);
		} catch (CouponSystemException e) {
			ConnectionPool.getInstance().rollback();
			throw e;
		} finally {
		}
		ConnectionPool.getInstance().endTransaction();
	}

	/**
	 * checks if the company Id already exist
	 * 
	 * @param company the company to be checked
	 * @return true if company id exists, false if doesn't
	 * @throws DAOException when DB error occurred
	 */
	/*
	 * private boolean companyIdExists(Company company) throws DAOException { //
	 * TODO Auto-generated method stub try { companyDAO.getCompany(company.getId());
	 * } catch (CompanyException e) { // No Company Found return false; } return
	 * true; }
	 */
	/**
	 * checks if the company name already exist
	 * 
	 * @param company the company to be checked
	 * @return true if company name exists, false if doesn't
	 * @throws DAOException when DB error occurred
	 */

	////////////////////////////////////////////////////////////////////

	/*
	 * public void createCoupon(Coupon coupon) throws CompanyFacadeException { //
	 * generate new unique coupon id try {
	 * coupon.setId(IdGenerator.generatCouponId()); } catch (IdGeneratorException e)
	 * { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; }
	 * 
	 * try { // check if coupon id is unique in db
	 * couponDAO.getCoupon(coupon.getId()); CompanyFacadeException CompanyException
	 * = new CompanyFacadeException(
	 * "cant create new coupon, the genereted id is olrady in db"); throw
	 * CompanyException;
	 * 
	 * } catch (DAOException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; } catch
	 * (CouponException e) { //No coupon was found can proceed }
	 * 
	 * try { // check if coupon title is unique in db
	 * couponDAO.getCouponByTitle(coupon.getTitle()); CompanyFacadeException
	 * CompanyException = new CompanyFacadeException(
	 * "cant create new coupon, the title is olrady in db"); throw CompanyException;
	 * 
	 * } catch (DAOException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; } catch
	 * (CouponException e) { //No coupon was found can proceed }
	 * 
	 * try { // check if coupon bean is legal data CouponUtil.checkCoupon(coupon);
	 * couponDAO.createCoupon(coupon); couponDAO.addCouponToCompany(coupon,
	 * company.getId()); } catch (DAOException | CouponException e) {
	 * CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon : \n" + e.getMessage(), e);
	 * throw exception; }
	 * 
	 * }
	 */

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
	public void removeCoupon(long couponId) throws CouponSystemException {
		// TODO Start transaction
		ConnectionPool.getInstance().startTransaction();
		try {
			couponDAO.removeCouponFromCustomers(couponId);
			couponDAO.removeCoupon(couponId);
		} catch (CouponSystemException e) {
			ConnectionPool.getInstance().rollback();
			throw e;
		} finally {
		}
		ConnectionPool.getInstance().endTransaction();
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
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException {
		// checks if customer already owns this coupon
		// TODO consider adding boolean method in couponDAO
		CouponBean coupon1 = couponDAO.getCoupon(couponId);
		if (couponDAO.getCustomerCoupons(customerId).contains(coupon1))
			throw new CouponSystemException("You already own this coupon");
		// TODO transaction
		ConnectionPool.getInstance().startTransaction();
		try {

			CouponBean coupon = couponDAO.getCoupon(couponId);

			if (coupon.getAmount() < 1 || coupon.getEndDate().getTime() < System.currentTimeMillis()) {
				throw new CouponSystemException("Coupon purchase failed : coupon is expired");
			}
			couponDAO.purchaseCoupon(couponId, customerId);
		} catch (CouponSystemException e) {
			ConnectionPool.getInstance().rollback();
			throw e;
		} finally {
		}
		ConnectionPool.getInstance().endTransaction();
	}

	/*
	 * public void removeCoupon(Coupon coupon) throws CompanyFacadeException {
	 * //check if the coupon exists // if this company is authorize to remove the
	 * coupon. getCoupon(coupon.getId()); this.checkIfCompanyAuthorize(coupon); try
	 * { couponDAO.removeCouponFromCompanies(coupon); // remove coupon from
	 * customer_coupon table ; couponDAO.removeCouponFromCustomers(coupon); //
	 * remove coupon from coupon table ; couponDAO.removeCoupon(coupon); } catch
	 * (DAOException | CouponException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant remove coupon", e); throw exception; } }
	 */

	/**
	 * Fetches a specific {@link CouponBean} from the DB using its ID.
	 *
	 * @param couponID The ID of the desired {@link CouponBean}.
	 * @return The coupon that matches the ID; <br>
	 *         <code>null</code> if there is no match.
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		return couponDAO.getCoupon(couponID);
	}

	/**
	 * Gets all coupons of the given CouponType
	 * 
	 * @param couponType The Type of coupons desired.
	 * @return a Collection of all coupons with matching a Type
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	public Collection<CouponBean> getCouponsByType(CouponType couponType) throws CouponSystemException {
		return couponDAO.getCouponsByType(couponType);
	}

	/**
	 * Returns all available coupons
	 * 
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		return couponDAO.getAllCoupons();
	}

	/**
	 * Returns all available coupons of the given company
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		return couponDAO.getCompanyCoupons(companyId);
	}

	/**
	 * Returns all available coupons of the given company with a specific type
	 * 
	 * @param companyId the company id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type)
			throws CouponSystemException {
		return couponDAO.getCompanyCouponsByType(companyId, type);
	}

	/**
	 * Returns all available coupons of the given Customer
	 * 
	 * @param customerId the Customer id that we want to return her coupons
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException {
		return couponDAO.getCustomerCoupons(customerId);
	}

	/**
	 * Returns all available coupons of the given customer with a specific type
	 * 
	 * @param customerId the customer id that we want to return her coupons
	 * @paran type the Coupon Type that we want to get
	 * @return Collection of Coupons
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type)
			throws CouponSystemException {
		return couponDAO.getCustomerCouponsByType(customerId, type);
	}

	/**
	 * Gets all coupons up to a certain price that were purchased by the customer
	 * 
	 * @param customerId
	 * @param price      the max price of the coupons to select
	 * @return Collection of Coupons associated with the Customer
	 * @throws CustomerFacadeException If retrieval of coupons fails
	 */
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, Double price)
			throws CouponSystemException {
		return couponDAO.getCustomerCouponsByPrice(customerId, price);
	}

	////////////////////////////////////////////////////////////////////

	/*
	 * public void createCoupon(Coupon coupon) throws CompanyFacadeException { //
	 * generate new unique coupon id try {
	 * coupon.setId(IdGenerator.generatCouponId()); } catch (IdGeneratorException e)
	 * { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; }
	 * 
	 * try { // check if coupon id is unique in db
	 * couponDAO.getCoupon(coupon.getId()); CompanyFacadeException CompanyException
	 * = new CompanyFacadeException(
	 * "cant create new coupon, the genereted id is olrady in db"); throw
	 * CompanyException;
	 * 
	 * } catch (DAOException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; } catch
	 * (CouponException e) { //No coupon was found can proceed }
	 * 
	 * try { // check if coupon title is unique in db
	 * couponDAO.getCouponByTitle(coupon.getTitle()); CompanyFacadeException
	 * CompanyException = new CompanyFacadeException(
	 * "cant create new coupon, the title is olrady in db"); throw CompanyException;
	 * 
	 * } catch (DAOException e) { CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon", e); throw exception; } catch
	 * (CouponException e) { //No coupon was found can proceed }
	 * 
	 * try { // check if coupon bean is legal data CouponUtil.checkCoupon(coupon);
	 * couponDAO.createCoupon(coupon); couponDAO.addCouponToCompany(coupon,
	 * company.getId()); } catch (DAOException | CouponException e) {
	 * CompanyFacadeException exception = new
	 * CompanyFacadeException("cant create new coupon : \n" + e.getMessage(), e);
	 * throw exception; }
	 * 
	 * }
	 */
}
