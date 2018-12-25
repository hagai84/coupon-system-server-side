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

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#createCompany(coupon.system.beans.Company)
	 */
	public long createCompany(CompanyBean company) throws CouponSystemException {
		Connection con = connectionPool.startTransaction();

		String sqlInsert = "INSERT INTO company "
						+ "(comp_name, password, email) "
						+ "VALUES(?,?,?)";
		String sql2 = "SELECT LAST_INSERT_ID() AS ID";
		try(PreparedStatement pstmt = con.prepareStatement(sqlInsert);PreparedStatement stmt2 = con.prepareStatement(sql2)){
			pstmt.setString(1, company.getCompName());
			pstmt.setInt(2, company.getPassword().hashCode());
			pstmt.setString(3, company.getEmail());
			if (pstmt.executeUpdate() == 0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create company faild : ");
				throw exception;
			}
			ResultSet set = stmt2.executeQuery();
			if(set.next()) {
				connectionPool.endTransaction();
				return set.getLong("ID");
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

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#updateCompany(coupon.system.beans.Company)
	 */
	@Override
	public void updateCompany(CompanyBean company) throws CouponSystemException {

		Connection con = connectionPool.getConnection();
		String sql = "UPDATE company SET  email=?, comp_name =? WHERE id=?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setString(1, company.getEmail());
			prepardStatement.setString(2, company.getCompName());
			prepardStatement.setLong(3, company.getId());
			//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
			if (prepardStatement.executeUpdate() == 0) {
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update company failed");
				throw exception;
			}

		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update company failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(con);
		}
	}

	@Override
	public void updateCompanyPassword(long companyId, String newPassword) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "UPDATE company SET PASSWORD=? WHERE ID=? ";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, newPassword.hashCode());
			stmt.setLong(2, companyId);
			if(stmt.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update company's password failed, ID : " + companyId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update company's password failed", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#removeCompany(coupon.system.beans.Company)
	 */
	@Override
	public void removeCompany(long companyId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		String sql = "DELETE FROM company WHERE id = ?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setLong(1,companyId);
			if (prepardStatement.executeUpdate() == 0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"remove company failed");
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove company failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getCompany(long)
	 */
	@Override
	public CompanyBean getCompany(long companyId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();

		String sql = "SELECT * FROM company WHERE id = ?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setLong(1,companyId);
			ResultSet set = prepardStatement.executeQuery();
			if (set.next()) {
				CompanyBean com = new CompanyBean();
				com.setId(set.getLong(1));
				com.setCompName(set.getString(2));
				com.setPassword("***PASSWORD***");
				com.setEmail(set.getString(4));
				return com;
			} else {
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"can't find company with id num : " + companyId);
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company by id failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(con);
		}
	}


	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getAllCompanies()
	 */
	@Override
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		List<CompanyBean> allCompanies = new ArrayList<>();
		try (Statement stmt = con.createStatement();) {
			String sql = "SELECT * FROM company";
			ResultSet set = stmt.executeQuery(sql);
			while (set.next()) {
				CompanyBean com = new CompanyBean();
				com.setId(set.getLong(1));
				com.setCompName(set.getString(2));
				com.setPassword("***PASSWORD***");
				com.setEmail(set.getString(4));
				allCompanies.add(com);
			}	
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ACCSESS,"get all companies failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(con);
		}
		return allCompanies;
	}


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
	public long companyLogin(String companyName, String password) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		String sql = "SELECT id FROM company WHERE COMP_NAME=? AND PASSWORD=?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setString(1,companyName);
			prepardStatement.setInt(2,password.hashCode());
			ResultSet set = prepardStatement.executeQuery();
			if (set.next()) {
				long id = set.getLong("id");
				set.close();
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
			connectionPool.returnConnection(con);
		}		
	}


	@Override
	public boolean companyNameAlreadyExists(String companyName) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = connectionPool.getConnection();
		String sql = "SELECT COMP_NAME FROM company WHERE COMP_NAME=? LIMIT 1";
		try (PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setString(1, companyName);
			ResultSet set = stmt.executeQuery();
			if (set.next()) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company by name failed", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
	}

}