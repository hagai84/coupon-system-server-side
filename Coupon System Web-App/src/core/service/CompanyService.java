package core.service;

import java.io.Serializable;
import java.util.Collection;

import core.beans.CompanyBean;
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
public class CompanyService implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CompanyService companyServiceInstance = new CompanyService();
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	private ICouponDAO couponDAO = CouponDAO.getInstance();
	private ICompanyDAO companyDAO = CompanyDAO.getInstance();
//	private final Company company;


	
	/**
	 * Public constructor initializes given company's access
	 * @param company Company to access in the system
	 */
	private CompanyService() {
		
	}
	
	public static CompanyService getInstance() {
	return companyServiceInstance;
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
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if(companyDAO.companyNameAlreadyExists(company.getCompName())) {
			throw new CouponSystemException("Company Name already exists");
		}	
		company.setId(IdGenerator.generatCompanyId());
		//IS ALSO HANDLED BY DAO LAYER
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
	 * Deletes a company in the database
	 * -removes its coupons from DB (all tables)
	 * @param companyId Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If company deletion fails
	 */
	public void removeCompany(long companyId) throws CouponSystemException {
		connectionPool.startTransaction();
		try {	
			couponDAO.removeCompanyCouponsFromCustomers(companyId);
			couponDAO.removeCompanyCoupons(companyId);
			companyDAO.removeCompany(companyId);
		}catch (CouponSystemException e) {
			connectionPool.rollback();
			throw e;
		} finally {
		}
		connectionPool.endTransaction();
	
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
	public CompanyBean getCompany(long companyID) throws CouponSystemException {	
		return companyDAO.getCompany(companyID);
	}


	
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
	 * @return a new CompanyFacade instance if company's username and password are correct; otherwise, throws {@link CompanyService}
	 * @throws CompanyFacadeException if username or password are incorrect
	 */
	public long companyLogin(String name, String password) throws CouponSystemException {
		return companyDAO.companyLogin(name, password);
	}

	public CompanyBean getCompanyByName(String companyName) throws CouponSystemException {
		return companyDAO.getCompanyByName(companyName);
	}

}
