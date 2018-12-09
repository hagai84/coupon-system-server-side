package core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import core.beans.CompanyBean;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
import core.util.ConnectionPool;

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
	public void createCompany(CompanyBean company) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		String sqlInsert = "INSERT INTO company VALUES(?,?,?,?)";
		try (PreparedStatement pstmt = con.prepareStatement(sqlInsert);) {
			pstmt.setLong(1, company.getId());
			pstmt.setString(2, company.getCompName());
			pstmt.setString(3, company.getPassword());
			pstmt.setString(4, company.getEmail());
			if (pstmt.executeUpdate() == 0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create company faild");
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"create company failed", e);
			throw exception;
		} finally {
			connectionPool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#updateCompany(coupon.system.beans.Company)
	 */
	@Override
	public void updateCompany(CompanyBean company) throws CouponSystemException {

		Connection con = connectionPool.getConnection();
		String sql = "UPDATE company SET password =? , email=?, comp_name =? WHERE id=?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setString(1, company.getPassword());
			prepardStatement.setString(2, company.getEmail());
			prepardStatement.setString(3, company.getCompName());
			prepardStatement.setLong(4, company.getId());
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
				com.setPassword(set.getString(3));
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
	 * @see coupon.system.dao.CompanyDAO#getCompanyByName(String)
	 */
	@Override
	public CompanyBean getCompanyByName(String companyName) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		String sql = "SELECT * FROM company WHERE comp_name = ?'" ;
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setString(1,companyName);
			ResultSet set = prepardStatement.executeQuery();
			if (set.next()) {
				CompanyBean com = new CompanyBean();
				com.setId(set.getLong(1));
				com.setCompName(set.getString(2));
				com.setPassword(set.getString(3));
				com.setEmail(set.getString(4));
				return com;
			} else {
				CouponSystemException exception = new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"cant found company with name " + companyName);
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company by name failed", e);
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
				com.setPassword(set.getString(3));
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
//		String sql = "SELECT password, id FROM company where comp_name =?";
		String sql = "SELECT id FROM company WHERE COMP_NAME=? AND PASSWORD=?";
		try (PreparedStatement prepardStatement  = con.prepareStatement(sql);) {
			prepardStatement.setString(1,companyName);
			prepardStatement.setString(2,password);
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
		} finally {
			connectionPool.returnConnection(con);
		}		
	}

	@Override
	public boolean companyNameAlreadyExists(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean companyIdAlreadyExists(long companyId) {
		// TODO Auto-generated method stub
		return false;
	}

}
