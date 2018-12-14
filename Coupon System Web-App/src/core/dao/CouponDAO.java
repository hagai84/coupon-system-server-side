package core.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import core.beans.CouponBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
import core.util.ConnectionPool;;


/**
 * Implements {@link ICouponDAO} interface
 * @author Ron
 *
 */
public class CouponDAO implements ICouponDAO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ICouponDAO couponDAOInstance = new CouponDAO();
	private ConnectionPool connectionPool  = ConnectionPool.getInstance();

	/**
	 * Private singleton constructor.
	 */
	private CouponDAO(){
		
	}

	/**
	 * Returns a static instance of CouponDBDAO
	 * @return A static instance of CouponDBDAO
	 */
	public static ICouponDAO getInstance() {
		return couponDAOInstance;
	}

	@Override
	public long createCoupon(CouponBean coupon) throws CouponSystemException {	
//		Connection con = connectionPool.getConnection();
		Connection con = connectionPool.startTransaction();
			
		String sql = "INSERT INTO coupon "
				+ "(title, start_date, end_date, amount, type, message, price, image, comp_id) "
//				+ "OUTPUT inserted.ID "
				+ "VALUES(?,?,?,?,?,?,?,?,?) ";
		String sql2 = "SELECT LAST_INSERT_ID() AS ID";
		try(PreparedStatement stmt = con.prepareStatement(sql);PreparedStatement stmt2 = con.prepareStatement(sql2)){
//			stmt.setLong(1, coupon.getCouponId());
			stmt.setString(1, coupon.getTitle());
			stmt.setDate(2, coupon.getStartDate());
			stmt.setDate(3, coupon.getEndDate());
			stmt.setInt(4, coupon.getAmount());
			stmt.setString(5,coupon.getType().toString());
			stmt.setString(6, coupon.getMessage());
			stmt.setDouble(7, coupon.getPrice());
			stmt.setString(8, coupon.getImage());
			stmt.setLong(9, coupon.getCompanyId());

			if(stmt.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"Create coupon failed : " + coupon);
			}
			ResultSet set = stmt2.executeQuery();
			if(set.next()) {
				connectionPool.endTransaction();
				return set.getLong("ID");
			}else {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"create customer failed : " + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"Create coupon failed", e);
		}finally {			
//			connectionPool.returnConnection(con);	
		}	
	}

	@Override
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException{
		
		Connection con = connectionPool.startTransaction();
		
		String sql = "INSERT INTO customer_coupon VALUES(?,?)";	
		String sql2 = "UPDATE coupon SET AMOUNT=amount-1 WHERE ID=? AND amount>0";
		try(PreparedStatement stmt = con.prepareStatement(sql);PreparedStatement stmt2 = con.prepareStatement(sql2)) {
			stmt.setLong(1, customerId);
			stmt.setLong(2, couponId);
			stmt2.setLong(1, couponId);
			if(stmt2.executeUpdate()==0) {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"purchase coupon failed - Coupon out of Stock ID : " + couponId);
			}				
			stmt.executeUpdate();				
		} catch (SQLException e) {
			connectionPool.rollback();
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"purchase failed - Customer might already owns Coupon", e);
		}finally {			
		}
		connectionPool.endTransaction();
	}

	@Override
	public void updateCoupon(CouponBean coupon) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		
		String sql = "UPDATE coupon SET TITLE=?, START_DATE=?, END_DATE=?, AMOUNT=?, TYPE=?,"
					+ "MESSAGE=?, PRICE=?, IMAGE=?, COMP_ID=? WHERE ID=?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
//			stmt.setLong(1, coupon.getCompanyId());
			stmt.setString(1, coupon.getTitle());
			stmt.setDate(2, coupon.getStartDate());
			stmt.setDate(3, coupon.getEndDate());
			stmt.setInt(4, coupon.getAmount());
			stmt.setString(5,String.valueOf(coupon.getType()));
			stmt.setString(6, coupon.getMessage());
			stmt.setDouble(7, coupon.getPrice());
			stmt.setString(8, coupon.getImage());
			stmt.setLong(9, coupon.getCompanyId());
			stmt.setLong(10, coupon.getCouponId());
			if(stmt.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"update coupon failed :" + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update coupon failed", e);
		}finally {
			connectionPool.returnConnection(con);			
		}
	}

	@Override
	public void updateCouponAmout(long couponId, int amoutDelta) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		
		String sql = "UPDATE coupon SET AMOUNT=AMOUNT+? WHERE ID=? AND AMOUNT+?>=0 ";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, amoutDelta);
			stmt.setLong(2,couponId);
			stmt.setInt(3, amoutDelta);
			if(stmt.executeUpdate()==0) {
				//NEGATIVE DELTA TO BIG or CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS ,"update amount failed : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR ,"update coupon failed", e);
		}finally {
			connectionPool.returnConnection(con);			
		}
	}
	
	@Override
	public void removeCoupon(long couponId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE FROM coupon WHERE ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponId);
			if(stmt.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"remove coupon failed, ID  : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon failed", e);
		}finally {			
			connectionPool.returnConnection(con);
		}		
	}

	@Override
	public void removeCustomerCoupons(long customerId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE FROM customer_coupon WHERE cust_ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, customerId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from coupon failed : ", e);
		}finally {			
			connectionPool.returnConnection(con);
		}	
	}

	@Override
	public void removeCompanyCoupons(long companyId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE FROM coupon WHERE comp_ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from coupon failed : ", e);
		}finally {			
			connectionPool.returnConnection(con);
		}	
	}

	@Override
	public void removeCouponFromCustomers(long couponId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE FROM customer_coupon WHERE coupon_ID = ? ";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponId );
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from customers failed : ", e);
		}finally {			
			connectionPool.returnConnection(con);
		}
	}

	@Override
	public void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE customer_coupon FROM customer_coupon"
				+ " INNER JOIN coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.comp_id = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove Company Coupons From Customers failed : ", e);
		}finally {			
			connectionPool.returnConnection(con);
		}	
		
	}

	@Override
	public void removeExpiredCoupons() throws CouponSystemException {
		Connection con = connectionPool.getConnection();		
		
		String sql = "DELETE coupon, customer_coupon FROM coupon"
				+ " INNER JOIN customer_coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.end_date < ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDate(1, new Date(System.currentTimeMillis()));
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove Expired Coupons failed : ", e);
		}finally {			
			connectionPool.returnConnection(con);
		}	
		
	}

	@Override
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		CouponBean coupon = null;		
		
		String sql = "SELECT * FROM coupon WHERE id =?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponID);
			ResultSet rs =  stmt.executeQuery();
			if(rs.next()) {
				coupon = readCoupon(rs);
				rs.close();
				return coupon;
			}else {
				rs.close();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get coupon failed, ID : " + couponID);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupon failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}	
	}

	@Override
	public CouponBean getCouponByTitle(String title) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		CouponBean coupon = null;
		
		String sql = "SELECT * FROM coupon WHERE TITLE =?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, title);
			ResultSet rs =  stmt.executeQuery();
			if(rs.next()) {
				coupon = readCoupon(rs);
				rs.close();
				return coupon;
			}else {
				rs.close();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get coupon failed, Title : " + title);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupon by title failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}	
	}

	@Override
	public Collection<CouponBean> getCouponsByType(CouponType type) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql ="SELECT * FROM coupon WHERE TYPE =?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, type.toString());
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
	
		try (Statement stmt = con.createStatement();){		
			ResultSet rs = stmt.executeQuery("SELECT * FROM coupon");			
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get all coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();		
		String sql = "SELECT * FROM coupon WHERE comp_id = ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setLong(1, companyId);	
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company coupon failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql ="SELECT * FROM coupon WHERE COMP_ID =? AND TYPE =?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			stmt.setString(2, type.toString());
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql ="SELECT * FROM coupon WHERE COMP_ID =? AND PRICE <= ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			stmt.setDouble(2, price);
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql ="SELECT * FROM coupon WHERE COMP_ID =? AND END_DATE <= ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			stmt.setDate(2, date);
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){	
			stmt.setLong(1, customerId);	
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()) {
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.type =?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){	
			stmt.setLong(1, customerId);	
			stmt.setString(2, type.toString());
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()) {
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = connectionPool.getConnection();
		
		String sql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.price <=?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){	
			stmt.setLong(1, customerId);	
			stmt.setDouble(2, price);
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()) {
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public boolean couponTitleAlreadyExists(String title) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Reads a ResultSet of a specific coupon from the DB and into a {@link CouponBean} object
	 * @param rs ResultSet of a single coupon
	 * @return A Coupon object representation of the coupon
	 * @throws SQLException If reading information from the ResultSet fails
	 */
	private CouponBean readCoupon(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		CouponBean coupon = new CouponBean();
		coupon.setCouponId(rs.getLong("ID"));
		coupon.setTitle(rs.getString("TITLE"));
		coupon.setStartDate(rs.getDate("START_DATE"));
		coupon.setEndDate(rs.getDate("END_DATE"));
		coupon.setAmount(rs.getInt("AMOUNT"));
		coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
		coupon.setMessage(rs.getString("MESSAGE"));
		coupon.setPrice(rs.getDouble("PRICE"));
		coupon.setImage(rs.getString("IMAGE"));
		coupon.setCompanyId(rs.getLong("COMP_ID"));
		return coupon;
	}
}

