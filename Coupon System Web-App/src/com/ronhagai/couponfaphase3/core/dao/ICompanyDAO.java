package com.ronhagai.couponfaphase3.core.dao;

import java.io.Serializable;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;

/**
 * An interface for a DAO class which accesses a {@link CompanyBean} type DAO.
 *
 * @author Hagai
 *
 */
public interface ICompanyDAO extends Serializable{
	
	/**
	 * Attempts to create a given company in the repository
	 * 
	 * @param company The company to create
	 * @return the created company's ID. 
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 * 
	 */
	long createCompany(CompanyBean company) throws CouponSystemException;
	
	/**
	 * Updates a company entity in the repository.
	 * 
	 * @param company the company entity to be updated.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void updateCompany(CompanyBean company) throws CouponSystemException;

	/**
	 * updates the company's password
	 * 
	 * @param companyId The company to update
	 * @param newPassword The new password
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts.
	 */
	void updateCompanyPassword(long companyId, String newPassword) throws CouponSystemException;
	
	/**
	 * Removes a company entity from the companies repository.
	 * 
	 * @param companyId the company's ID.
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	void removeCompany(long companyId) throws CouponSystemException;

	/**
	 * Retrieves a company entity from the repository.
	 * 
	 * @param companyId the company's ID.
	 * @return a CouponBean object
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	CompanyBean getCompany(long companyId) throws CouponSystemException;

	/**
	 * Retrieves all the companies entities from the repository .
	 * 
	 * @return a Collection of CouponBean objects
	 * @throws CouponSystemException if the operation failed due to (1) DB error, (2) data conflicts such as : no matching data.
	 */
	Collection<CompanyBean> getAllCompanies() throws CouponSystemException;

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
	long companyLogin(String companyName, String password) throws CouponSystemException;

	/**
	 * Checks if a company's Name already exists in the repository .
	 * 
	 * @param companyName the name to match.
	 * @return a boolean value, (1) true if a matching Name was found, (2) false if no match was found 
	 * @throws CouponSystemException if the operation failed due to (1) DB error
	 */
	boolean companyNameAlreadyExists(String companyName) throws CouponSystemException;

}
