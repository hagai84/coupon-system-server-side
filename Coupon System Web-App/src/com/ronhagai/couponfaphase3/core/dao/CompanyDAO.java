package com.ronhagai.couponfaphase3.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Implements {@link ICompanyDAO} interface
 * @author Hagai
 *
 */
public class CompanyDAO implements ICompanyDAO{
	
	private static final long serialVersionUID = 1L;
	private static ICompanyDAO companyDAOInstance = new CompanyDAO();
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

	/**
	 * Private constructor.
	 */
	private CompanyDAO() {

	}

	/**
	 * Returns a static instance of CompanyDBDAO
	 * @return A static instance of CompanyDBDAO
	 */
	public static ICompanyDAO getInstance() {
		return companyDAOInstance;
	}

	@Override
	public long createCompany(CompanyBean company) throws CouponSystemException {
		Connection connection = connectionPool.startTransaction();

		String insertSql = "INSERT INTO company "
						+ "(comp_name, password, email) "
						+ "VALUES(?,?,?)";
		String selectSql = "SELECT LAST_INSERT_ID() AS ID";
		try(PreparedStatement insertStatement = connection.prepareStatement(insertSql);PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			insertStatement.setString(1, company.getCompName());
			insertStatement.setInt(2, company.getPassword().hashCode());
			insertStatement.setString(3, company.getEmail());
			if (insertStatement.executeUpdate() == 0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create company faild : ");
				throw exception;
			}
			ResultSet resultSet = selectStatement.executeQuery();
			if(resultSet.next()) {
				connectionPool.endTransaction();
				return resultSet.getLong("ID");
			}else {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create company failed : ");
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"create company failed : ", e);
			throw exception;
		} finally {
		}
	}

	
	@Override
	public void updateCompany(CompanyBean company) throws CouponSystemException {

		Connection connection = connectionPool.getConnection();
		String updateSql = "UPDATE company SET  email=?, comp_name =? WHERE id=?";
		try (PreparedStatement updateStatement  = connection.prepareStatement(updateSql);) {
			updateStatement.setString(1, company.getEmail());
			updateStatement.setString(2, company.getCompName());
			updateStatement.setLong(3, company.getId());
			//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
			if (updateStatement.executeUpdate() == 0) {
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update company failed");
				throw exception;
			}

		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update company failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(connection);
		}
	}

	@Override
	public void updateCompanyPassword(long companyId, String newPassword) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String updateSql = "UPDATE company SET PASSWORD=? WHERE ID=? ";
		try(PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			updateStatement.setInt(1, newPassword.hashCode());
			updateStatement.setLong(2, companyId);
			if(updateStatement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update company's password failed, ID : " + companyId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update company's password failed", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		
	}
	
	
	@Override
	public void removeCompany(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		String deleteSql = "DELETE FROM company WHERE id = ?";
		try (PreparedStatement deleteStatement  = connection.prepareStatement(deleteSql);) {
			deleteStatement.setLong(1,companyId);
			if (deleteStatement.executeUpdate() == 0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"remove company failed");
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove company failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(connection);
		}
	}

	@Override
	public CompanyBean getCompany(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();

		String selectSql = "SELECT * FROM company WHERE id = ?";
		try (PreparedStatement selectStatement  = connection.prepareStatement(selectSql);) {
			selectStatement.setLong(1,companyId);
			ResultSet resultSet = selectStatement.executeQuery();
			if (resultSet.next()) {
				CompanyBean company = readCompany(resultSet);
				return company;
			} else {
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"can't find company with id num : " + companyId);
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company by id failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(connection);
		}
	}

	@Override
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		List<CompanyBean> allCompanies = new ArrayList<>();
		try (Statement selectStatement = connection.createStatement();) {
			String selectSql = "SELECT * FROM company";
			ResultSet resultSet = selectStatement.executeQuery(selectSql);
			while (resultSet.next()) {
				CompanyBean company = readCompany(resultSet);
				allCompanies.add(company);
			}	
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ACCSESS,"get all companies failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(connection);
		}
		return allCompanies;
	}
	
	@Override
	public long companyLogin(String companyName, String password) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		String selectSql = "SELECT id FROM company WHERE COMP_NAME=? AND PASSWORD=?";
		try (PreparedStatement selectStatement  = connection.prepareStatement(selectSql);) {
			selectStatement.setString(1,companyName);
			selectStatement.setInt(2,password.hashCode());
			ResultSet resultSet = selectStatement.executeQuery();
			if (resultSet.next()) {
				long id = resultSet.getLong("id");
				resultSet.close();
				return id;
			}else {
				throw new CouponSystemException(ExceptionsEnum.AUTHENTICATION,"Incorrect Name Or Password");
			}			
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"login failed", e);
			throw exception;
//		} catch (NullPointerException e) {
//			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"name/password cant be null");
		} finally {
			connectionPool.returnConnection(connection);
		}		
	}


	@Override
	public boolean companyNameAlreadyExists(String companyName) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection connection = connectionPool.getConnection();
		String selectSql = "SELECT COMP_NAME FROM company WHERE COMP_NAME=? LIMIT 1";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setString(1, companyName);
			ResultSet resultSet = selectStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company by name failed", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
	}
	public CompanyBean readCompany(ResultSet resultSet) throws SQLException{
		CompanyBean company = new CompanyBean();
		company.setId(resultSet.getLong("ID"));
		company.setCompName(resultSet.getString("COMP_NAME"));
		company.setPassword("*PASSWORD*");
		company.setEmail(resultSet.getString("EMAIL"));
		return company;
	}

}
