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
	 * Attempts to create a given company in the DB
	 * 
	 * @param company The company to create
	 * @throws DAOException If there is a connection problem or SQLException.
	 * @throws CompanyException If insertion of the given company to the DB fails (e.g. Company ID already exists).
	 * 
	 */
	long createCompany(CompanyBean company) throws CouponSystemException;
	
	/**
	 * Updates all of a company's fields (except ID) in the DB according to the given company bean.
	 * 
	 * @param company The company to be updated
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CompanyException If the given company's ID can't be found in the DB (0 rows were updated).
	 */
	void updateCompany(CompanyBean company) throws CouponSystemException;

	void updateCompanyPassword(long companyId, String newPassword) throws CouponSystemException;
	/**
	 * Removes a specified company from the DB.
	 * 
	 * @param companyId The company to be removed.
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CompanyException If the given company's ID can't be found in the DB.
	 * 
	 */
	void removeCompany(long companyId) throws CouponSystemException;

	/**
	 * Searches the DB for a company with the given ID and
	 * returns a Company bean with it's data from the DB.
	 * 
	 * @param companyId The id of the company to find in the DB.
	 * @return {@link CompanyBean} bean; <code>null</code> - if no company with the given ID exists in DB
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * @throws CompanyException If the given company's ID can't be found in the DB (0 rows were returned).
	 */
	CompanyBean getCompany(long companyId) throws CouponSystemException;

	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 * 
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws DAOException If there is a connection problem or an <code>SQLException</code> is thrown.
	 */
	Collection<CompanyBean> getAllCompanies() throws CouponSystemException;

	/**
	 * Returns true if the given company user name is in the DB and if the given
	 * password is equal to the password in the DB (same row as the company name)
	 * 
	 * @param companyName The company's user name
	 * @param password The company's password
	 * @return <code>true</code> if user name and password match; otherwise <code>false</code>
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 * 
	 */
	long companyLogin(String companyName, String password) throws CouponSystemException;

	boolean companyNameAlreadyExists(String companyName) throws CouponSystemException;

}
