package core.controller;

import java.util.Collection;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.dao.CompanyDAO;
import core.dao.CouponDAO;
import core.dao.ICompanyDAO;
import core.dao.ICouponDAO;
import core.exception.CouponSystemException;
import core.util.ConnectionPool;
import core.util.IdGenerator;
import core.validation.CompanyBeanValidator;

/**
 * Facade used to access the coupon system by Companies
 * @author Hagai
 *
 */
public class CompanyController implements IController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final ICouponDAO couponDAO = CouponDAO.getInstance();
	private static final ICompanyDAO companyDAO = CompanyDAO.getInstance();
//	private final Company company;

	/**
	 * Checks if the company is authorized to access a given coupon.
	 * @param coupon The desired coupon to access
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	/*private void checkIfCompanyAuthorize(Coupon coupon) throws CompanyFacadeException {
		if (!this.getAllCoupons().contains(coupon)) {
			CompanyFacadeException CompanyException = new CompanyFacadeException("Compay is not authorize to access given coupon");
			throw CompanyException;
		}
	}*/
	
	/**
	 * Public constructor initializes given company's access
	 * @param company Company to access in the system
	 */
	public CompanyController() {
		
	}


	/**
	 * Creates a new company in the database
	 * @param company Company to add to the DB
	 * @throws AdminFacadeException
	 *  If company name or ID already exist in the DB
	 *  If company creation fails
	 */
	public void createCompany(CompanyBean company) throws CouponSystemException {
		CompanyBeanValidator.checkCompany(company);
		
		if(companyDAO.companyNameAlreadyExists(company.getCompName())) {
			throw new CouponSystemException("Company Name already exists");
		}
		
		company.setId(IdGenerator.generatCompanyId());
		
		if(companyDAO.companyIdAlreadyExists(company.getId())) {
			throw new CouponSystemException("Company ID already exists");
		}
		companyDAO.createCompany(company);		
	}


	/**
	 * Updates a company's details in the database
	 * @param company Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If updating of the company's details fails
	 */
	public void updateCompany(CompanyBean company) throws CouponSystemException {
		CompanyBeanValidator.checkCompany(company);
		
		CompanyBean tmpCompany = getCompany(company.getId());
		tmpCompany.setEmail(company.getEmail());
		tmpCompany.setPassword(company.getPassword());
		
		companyDAO.updateCompany(tmpCompany);
	}


	/**
	 * Checks if the company is authorized to access a given coupon.
	 * @param coupon The desired coupon to access
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	/*private void checkIfCompanyAuthorize(Coupon coupon) throws CompanyFacadeException {
		if (!this.getAllCoupons().contains(coupon)) {
			CompanyFacadeException CompanyException = new CompanyFacadeException("Compay is not authorize to access given coupon");
			throw CompanyException;
		}
	}*/
	
	/**
	 * Deletes a company in the database
	 * -removes its coupons from DB (all tables)
	 * @param companyId Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If company deletion fails
	 */
	public void removeCompany(long companyId) throws CouponSystemException {
		Collection<CouponBean> compCoupons;
		ConnectionPool.getInstance().startTransaction();
		try {	
			couponDAO.removeCompanyCouponsFromCustomers(companyId);
			couponDAO.removeCompanyCoupons(companyId);
			companyDAO.removeCompany(companyId);
		}catch (CouponSystemException e) {
			ConnectionPool.getInstance().rollback();
			throw e;
		} finally {
		}
		ConnectionPool.getInstance().endTransaction();
	
	}


	/**
	 * Checks if the company is authorized to access a given coupon.
	 * @param coupon The desired coupon to access
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	/*private void checkIfCompanyAuthorize(Coupon coupon) throws CompanyFacadeException {
		if (!this.getAllCoupons().contains(coupon)) {
			CompanyFacadeException CompanyException = new CompanyFacadeException("Compay is not authorize to access given coupon");
			throw CompanyException;
		}
	}*/
	
	/**
	 * Gets a company's details from the database
	 * 
	 * @param companyID ID of company to retrieve from the DB
	 * @return Company the company with the matching id
	 * @throws AdminFacadeException
	 *  If there is a connection problem or an SQLException is thrown to the DAO.
	 *  If the given company's ID can't be found in the DB (0 rows were returned).
	 */
	public CompanyBean getCompany(long companyID) throws CouponSystemException {	
		return companyDAO.getCompany(companyID);
	}


	/**
	 * Checks if the company is authorized to access a given coupon.
	 * @param coupon The desired coupon to access
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	/*private void checkIfCompanyAuthorize(Coupon coupon) throws CompanyFacadeException {
		if (!this.getAllCoupons().contains(coupon)) {
			CompanyFacadeException CompanyException = new CompanyFacadeException("Compay is not authorize to access given coupon");
			throw CompanyException;
		}
	}*/
	
	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws AdminFacadeException If there is a connection problem or an <code>SQLException</code> is thrown to the DAO.
	 */
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException{
		return companyDAO.getAllCompanies();
	}


	/**
	 * Logs in to the coupon system as a specific company.
	 * @param name Company username
	 * @param password Company password
	 * @return a new CompanyFacade instance if company's username and password are correct; otherwise, throws {@link CompanyController}
	 * @throws CompanyFacadeException if username or password are incorrect
	 */
	public long companyLogin(String name, String password) throws CouponSystemException {
		return companyDAO.companyLogin(name, password);
		/*try {
			if (CompanyDBDAO.companyLogin(name, password)!=-1) {
				return new CompanyFacade(companyDAO.getCompanyByName(name));
			}
		} catch (DAOException | CompanyException e) {
			CompanyFacadeException exception = new CompanyFacadeException("cant login :\n" + e.getMessage(), e);
			throw exception;
		}
		CompanyFacadeException e = new CompanyFacadeException("user name or password are incorrect");
		throw e;*/
	}

	/**
	 * Checks if the company is authorized to access a given coupon.
	 * @param coupon The desired coupon to access
	 * @throws CompanyFacadeException if operation was unsuccessful
	 */
	/*private void checkIfCompanyAuthorize(Coupon coupon) throws CompanyFacadeException {
		if (!this.getAllCoupons().contains(coupon)) {
			CompanyFacadeException CompanyException = new CompanyFacadeException("Compay is not authorize to access given coupon");
			throw CompanyException;
		}
	}*/

}
