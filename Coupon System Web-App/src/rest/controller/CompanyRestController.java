package rest.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import core.beans.CompanyBean;
import core.exception.CouponSystemException;
import core.service.CompanyService;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyRestController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyService companyService = CompanyService.getInstance();

	/**
	 * Creates a new company in the database
	 * @param company Company to add to the DB
	 * @throws AdminFacadeException
	 *  If company name or ID already exist in the DB
	 *  If company creation fails
	 */
	@POST
	public void createCompany(CompanyBean company) throws CouponSystemException {
		companyService.createCompany(company);
	}
	



	/**
	 * Updates a company's details in the database
	 * @param company Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If updating of the company's details fails
	 */
	@PUT
	public void updateCompany(CompanyBean company) throws CouponSystemException {
		companyService.updateCompany(company);
	}

	
	/**
	 * Deletes a company in the database
	 * -removes its coupons from DB (all tables)
	 * @param companyId Company to remove from the DB
	 * @throws AdminFacadeException
	 *  If given company's details don't match the details of the company with the same ID in the DB
	 *  If company deletion fails
	 */
	@Path("{companyId}")
	@DELETE
	public void removeCompany(@PathParam ("companyId") long companyId) throws CouponSystemException {
		companyService.removeCompany(companyId);
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
	@GET
	@Path("{companyId}")
	public CompanyBean getCompany(@PathParam("companyId")long companyId) throws CouponSystemException {	
		return companyService.getCompany(companyId);
	}

	

	
	/**
	 * Assemble and return an <code>ArrayList</code> of all the companies in the DB.
	 *
	 * @return An <code>ArrayList</code> of all the companies in DB.
	 * @throws AdminFacadeException If there is a connection problem or an <code>SQLException</code> is thrown to the DAO.
	 */
	@Path("companies")
	@GET
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException{
		return companyService.getAllCompanies();
	}


//	/**
//	 * Logs in to the coupon system as a specific company.
//	 * @param name Company username
//	 * @param password Company password
//	 * @return a new CompanyFacade instance if company's username and password are correct; otherwise, throws {@link CompanyController}
//	 * @throws CompanyFacadeException if username or password are incorrect
//	 */
//	
//	public long companyLogin(String name, String password) throws CouponSystemException {
//		return companyDAO.companyLogin(name, password);
//	}


}
