package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Pattern;

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.dao.CompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.enums.ClientType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Facade used to access the coupon system by Companies
 * @author Hagai
 *
 */
public class CompanyService implements Serializable, IBeanValidatorConstants{
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
	public long createCompany(CompanyBean company, long userId, ClientType userType) throws CouponSystemException {
		//can be used if company registration should be restricted 
		if (!userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to create company " + company);
		}
		checkCompany(company);
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if(companyDAO.companyNameAlreadyExists(company.getCompName())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Company Name already exists");
		}	
		long companyId = companyDAO.createCompany(company);
		System.out.println("LOG : User " + userType + " - " + userId + " created company " + company);
		return companyId;
	}


	/**
	 * Updates a company's details in the database
	 * @param company Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If updating of the company's details fails
	 */
	public void updateCompany(CompanyBean company, long userId, ClientType userType) throws CouponSystemException {
		if ((company.getId() != userId || !userType.equals(ClientType.COMPANY)) && !userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to update company " + company);
		}
		checkCompany(company);
		CompanyBean tmpCompany = getCompany(company.getId());
		tmpCompany.setEmail(company.getEmail());
		tmpCompany.setPassword(company.getPassword());
		
		companyDAO.updateCompany(tmpCompany);
		System.out.println("LOG : User " + userType + " - " + userId + " updated company " + company);		
	}

	public void updateCompanyPassword(long companyId, String oldPassword, String newPassword, long userId, ClientType userType) throws CouponSystemException {
		if ((companyId != userId || !userType.equals(ClientType.COMPANY)) && !userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to change company's password " + companyId);
		}
		if(userType.equals(ClientType.CUSTOMER)) {			
			String customerName = companyDAO.getCompany(companyId).getCompName();
			companyDAO.companyLogin(customerName, oldPassword);
		}	
		companyDAO.updateCompanyPassword(companyId, newPassword);
		System.out.println("LOG : User " + userType + " - " + userId + " changed company's password " + companyId);		
	}
	/**
	 * Deletes a company in the database
	 * -removes its coupons from DB (all tables)
	 * @param companyId Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If company deletion fails
	 */
	public void removeCompany(long companyId, long userId, ClientType userType) throws CouponSystemException {
		//can be modified if company removing should be restricted
		if (!userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,"User " + userType + " - " + userId + " attempts to remove company " + companyId);
		}
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
		System.out.println("LOG : User " + userType + " - " + userId + " removed company " + companyId);			
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

	private void checkCompany(CompanyBean company) throws CouponSystemException {
		checkCompanyName(company.getCompName());
		checkCompanyPassword(company.getPassword());
		checkCompanyEmail(company.getEmail());
	}

	private void checkCompanyName(String compName) throws CouponSystemException {
		if (compName.length() > COMP_NAME_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company name cant be more than " + COMP_NAME_LENGTH + " characters");
			throw e;
		}
	}

	private void checkCompanyPassword(String compPassword) throws CouponSystemException {
		if (compPassword.length() > COMP_PASSWORD_MAX_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password cant be more than " + COMP_PASSWORD_MAX_LENGTH + " characters");
			throw e;
		}
		if (compPassword.length() < COMP_PASSWORD_MIN_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password need to be more than " + COMP_PASSWORD_MIN_LENGTH + " characters");
			throw e;
		}
	}

	private void checkCompanyEmail(String companyEmail) throws CouponSystemException {
		if (companyEmail == null) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be null");
			throw e;
		}

		if (companyEmail.length() > COMP_EMAIL_LENGTH){
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be more than" + COMP_EMAIL_LENGTH + "leters");
			throw e;
		}

		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (!pat.matcher(companyEmail).matches()) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email is not valid");
			throw e;
		}
	}
}
