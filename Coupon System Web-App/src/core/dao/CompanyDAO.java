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
import core.util.ConnectionPool;

/**
 * Implements {@link ICompanyDAO} interface
 * @author Hagai
 *
 */
public class CompanyDAO implements ICompanyDAO{
	
	private static final long serialVersionUID = 1L;
	private static ConnectionPool pool = ConnectionPool.getInstance();
	private static CompanyDAO instance = new CompanyDAO();

	/**
	 * Private constructor.
	 */
	private CompanyDAO() {

	}

	/**
	 * Returns a static instance of CompanyDBDAO
	 * @return A static instance of CompanyDBDAO
	 */
	public static CompanyDAO getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#createCompany(coupon.system.beans.Company)
	 */
	public void createCompany(CompanyBean company) throws CouponSystemException {
		Connection con = pool.getConnection();
		String sqlInsert = "INSERT INTO company VALUES(?,?,?,?)";
		try (PreparedStatement pstmt = con.prepareStatement(sqlInsert);) {
			pstmt.setLong(1, company.getId());
			pstmt.setString(2, company.getCompName());
			pstmt.setString(3, company.getPassword());
			pstmt.setString(4, company.getEmail());
			if (pstmt.executeUpdate() == 0) {
				CouponSystemException exception = new CouponSystemException("0 rows were insert");
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("create company failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#updateCompany(coupon.system.beans.Company)
	 */
	@Override
	public void updateCompany(CompanyBean company) throws CouponSystemException {

		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement();) {
			String sql = "UPDATE company " + "SET password ='" + company.getPassword() + "', email='"
					+ company.getEmail() + "' comp_name ='" + company.getCompName() + "' WHERE id=" + company.getId();
			if (stmt.executeUpdate(sql) == 0) {
				CouponSystemException exception = new CouponSystemException("0 rows were update");
				throw exception;
			}

		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("update company failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#removeCompany(coupon.system.beans.Company)
	 */
	@Override
	public void removeCompany(long companyId) throws CouponSystemException {
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement();) {
			String sql = "DELETE FROM company WHERE id = " + companyId;
			if (stmt.executeUpdate(sql) == 0) {
				CouponSystemException exception = new CouponSystemException("0 rows were remove");
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("remove company failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getCompany(long)
	 */
	@Override
	public CompanyBean getCompany(long id) throws CouponSystemException {
		Connection con = pool.getConnection();

		try (Statement stmt = con.createStatement();) {
			String sql = "SELECT * " + "FROM company " + "WHERE id = " + id;
			ResultSet set = stmt.executeQuery(sql);
			if (set.next()) {
				CompanyBean com = new CompanyBean();
				com.setId(set.getLong(1));
				com.setCompName(set.getString(2));
				com.setPassword(set.getString(3));
				com.setEmail(set.getString(4));
				return com;
			} else {
				CouponSystemException exception = new CouponSystemException("can't find company with id num : " + id);
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("get company by id failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getCompanyByName(String)
	 */
	@Override
	public CompanyBean getCompanyByName(String name) throws CouponSystemException {
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement();) {
			String sql = "SELECT * " + "FROM company " + "WHERE comp_name = '" + name + "'";
			ResultSet set = stmt.executeQuery(sql);
			if (set.next()) {
				CompanyBean com = new CompanyBean();
				com.setId(set.getLong(1));
				com.setCompName(set.getString(2));
				com.setPassword(set.getString(3));
				com.setEmail(set.getString(4));
				return com;
			} else {
				CouponSystemException exception = new CouponSystemException("cant found company with name " + name);
				throw exception;
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("get company by name failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getAllCompanies()
	 */
	@Override
	public Collection<CompanyBean> getAllCompanies() throws CouponSystemException {
		Connection con = pool.getConnection();
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
			CouponSystemException exception = new CouponSystemException("get all companies failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
		return allCompanies;
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CompanyDAO#getCoupons()
	 */
	/*@Override
	public Collection<Coupon> getCoupons(long companyId) throws CouponSystemException {
		Connection con = pool.getConnection();
		List<Coupon> couponList = new ArrayList<>();
		try (Statement stmt = con.createStatement();) {
			String sql = "SELECT * " + "FROM company_coupon RIGHT JOIN coupon "
					+ "ON coupon.id = company_coupon.coupon_id " + "WHERE comp_id = " + companyId;
			ResultSet set = stmt.executeQuery(sql);

			while (set.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(set.getLong(3));
				coupon.setTitle(set.getString(4));
				coupon.setStartDate(set.getDate(5));
				coupon.setEndDate(set.getDate(6));
				coupon.setAmount(set.getInt(7));
				CouponType couponType = CouponType.valueOf(set.getString(8));
				coupon.setType(couponType);
				coupon.setMessage(set.getString(9));
				coupon.setPrice(set.getLong(10));
				coupon.setImage(set.getString(11));
				couponList.add(coupon);
			}
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("get all company coupons failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
		return couponList;
	}*/

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
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement();) {
			String sql = "SELECT password, id " + "FROM company " + "where comp_name = '" + companyName + "'";
			ResultSet set = stmt.executeQuery(sql);
			if (set.next()) {
				if (set.getString("password").equals(password)) {
					return set.getLong("ID");
				}
			}
			//TODO throw exception 
		} catch (SQLException e) {
			CouponSystemException exception = new CouponSystemException("login failed", e);
			throw exception;
		} finally {
			pool.returnConnection(con);
		}
		return -1;
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
