package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Pattern;

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.dao.CompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICompanyDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.enums.UserType;
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

	private CompanyService() {
		
	}
	
	public static CompanyService getInstance() {
	return companyServiceInstance;
}


	/**
	 * Adds a new company entity to the repository.
	 * 
	 * @param company the new company entity to be added.
	 * @return the created company's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as :
	 * 	existing name, (3) Invalid data.
	 */
	public long createCompany(CompanyBean company) throws CouponSystemException {
		//can be used if company registration should be restricted 
		checkCompany(company);
		//CLD BE HANDLED BY DAO LAYER BY MAKING IT UNIQUE
		if(companyDAO.companyNameAlreadyExists(company.getCompName())) {
			throw new CouponSystemException(ExceptionsEnum.NAME_EXISTS,"Company Name already exists");
		}	
		long companyId = companyDAO.createCompany(company);
		System.out.println("LOG : Company created : " + company);
		return companyId;
	}


	/**
	 * Updates a company entity in the repository.
	 * 
	 * @param company the company object to be updated.
	 * @param userId the user updating the company
	 * @param userType the user type updating the company
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 * 	(3) Invalid data, (4) security breach.
	 */
	public void updateCompany(CompanyBean company, long userId, UserType userType) throws CouponSystemException {
		if ((company.getId() != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to update company %s",userType , userId, company));
		}
		CompanyBean originalCompany = getCompany(company.getId());
		originalCompany.setEmail(company.getEmail());
		originalCompany.setCompName(company.getCompName());
		checkCompany(originalCompany);
		companyDAO.updateCompany(originalCompany);
		System.out.println(String.format("LOG : User %s %s updated company %s",userType , userId, company));		
	}
	
	/**
	 * updates the company's password
	 * 
	 * @param companyId The company to update
	 * @param newPassword The new password
	 * @param oldPassword The old password
	 * @param userId the user updating the coupon
	 * @param userType the user type updating the coupon
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 */
	public void updateCompanyPassword(long companyId, String oldPassword, String newPassword, long userId, UserType userType) throws CouponSystemException {
		if ((companyId != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to change company's %s password ",userType , userId, companyId));
		}
		if(userType.equals(UserType.COMPANY)) {			
			String companyName = companyDAO.getCompany(companyId).getCompName();
			companyDAO.companyLogin(companyName, oldPassword);
		}	
		companyDAO.updateCompanyPassword(companyId, newPassword);
		System.out.println(String.format("LOG : User %s %s changed company's %s password ",userType , userId, companyId));
	}
	
	/**
	 * Removes a company entity from the companies repositories.
	 * removes the company's coupons as well.
	 * 
	 * @param companyId the company's ID.
	 * @param userId the user removing the company.
	 * @param userType the user type
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data,
	 *  (3) Invalid data, (4) security breach.
	 */
	public void removeCompany(long companyId, long userId, UserType userType) throws CouponSystemException {
		//can be modified if company removing should be restricted
		if ((companyId != userId || !userType.equals(UserType.COMPANY)) && !userType.equals(UserType.ADMIN)) {
//		if (!userType.equals(ClientType.ADMIN)) {
			throw new CouponSystemException(ExceptionsEnum.SECURITY_BREACH,String.format("User %s %s attempts to remove company %s",userType , userId, companyId));
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
		System.out.println(String.format("LOG : User %s %s attempts to remove company %s",userType , userId, companyId));			
	}
	
	/**
	 * Retrieves a company entity from the repository.
	 * 
	 * @param companyId the company's ID.
	 * @return a CompanyBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public CompanyBean getCompany(long companyID) throws CouponSystemException {	
		return companyDAO.getCompany(companyID);
	}
	
	/**
	 * Retrieves all the companies entities from the repository .
	 * 
	 * @return a Collection of companies objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException{
		return companyDAO.getAllCompanies();
	}

	/**
	 * Check the password and user name,
	 * returns the user ID if correct.
	 * 
	 * @param companyName The company's user name
	 * @param password The company's password
	 * @return a long userId - the user's ID
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 * 
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
		if (compName.length() > COMPANY_NAME_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company name cant be more than " + COMPANY_NAME_LENGTH + " characters");
			throw e;
		}
	}

	private void checkCompanyPassword(String compPassword) throws CouponSystemException {
		if (compPassword.length() > COMPANY_PASSWORD_MAX_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password cant be more than " + COMPANY_PASSWORD_MAX_LENGTH + " characters");
			throw e;
		}
		if (compPassword.length() < COMPANY_PASSWORD_MIN_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password need to be more than " + COMPANY_PASSWORD_MIN_LENGTH + " characters");
			throw e;
		}
	}

	private void checkCompanyEmail(String companyEmail) throws CouponSystemException {
		if (companyEmail == null) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be null");
			throw e;
		}

		if (companyEmail.length() > COMPANY_EMAIL_LENGTH){
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be more than" + COMPANY_EMAIL_LENGTH + "leters");
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
