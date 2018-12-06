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
	private static ConnectionPool pool  = ConnectionPool.getInstance();
	private static ICouponDAO coupDAO = new CouponDAO();

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
		return coupDAO;
	}

	@Override
	public void createCoupon(CouponBean coupon, long companyId) throws CouponSystemException {
		// TODO Auto-generated method stub
		
		Connection con = pool.getConnection();
			
		String sql = "INSERT INTO coupon VALUES(?,?,?,?,?,?,?,?,?,?) ";

		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, coupon.getCouponId());
			stmt.setString(2, coupon.getTitle());
			stmt.setDate(3, coupon.getStartDate());
			stmt.setDate(4, coupon.getEndDate());
			stmt.setInt(5, coupon.getAmount());
			stmt.setString(6,coupon.getType().toString());
			stmt.setString(7, coupon.getMessage());
			stmt.setDouble(8, coupon.getPrice());
			stmt.setString(9, coupon.getImage());
			stmt.setLong(10, companyId);

			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("add coupon to coupon failed, ID  : " + coupon.getCouponId());
			}					
		} catch (SQLException e) {
			throw new CouponSystemException("add coupon to coupon failed : ", e);
		}finally {			
			pool.returnConnection(con);	
		}
			
		
	}

	@Override
	public void updateCoupon(CouponBean coupon) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();
		
//		String sql = "UPDATE coupon SET END_DATE=?, AMOUNT=?, PRICE=? WHERE ID=?";
		String sql = "UPDATE coupon SET  ID=?, TITLE=?, START_DATE=?, END_DATE=?, AMOUNT=?, TYPE=?,"
					+ "MESSAGE=?, PRICE=?, IMAGE=?, COMP_ID=? WHERE ID=?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, coupon.getCompanyId());
			stmt.setString(2, coupon.getTitle());
			stmt.setDate(3, coupon.getStartDate());
			stmt.setDate(4, coupon.getEndDate());
			stmt.setInt(5, coupon.getAmount());
			stmt.setString(6,String.valueOf(coupon.getType()));
			stmt.setString(7, coupon.getMessage());
			stmt.setDouble(8, coupon.getPrice());
			stmt.setString(9, coupon.getImage());
			stmt.setLong(10, coupon.getCompanyId());
			stmt.setLong(11, coupon.getCouponId());
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("update coupon failed, ID  : " + coupon.getCouponId());
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			throw new CouponSystemException("update coupon failed : ", e);
		}finally {
			pool.returnConnection(con);			
		}
	}

	@Override
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException{
		int amount = getCoupon(couponId).getAmount();
		
		/*if(amount<1)
			throw new CouponSystemException("Coupon out of Stock");*/
		
		Connection con = pool.getConnection();
		
		String sql = "INSERT INTO customer_coupon VALUES(?,?)";	
		String sql2 = "UPDATE coupon SET AMOUNT=?";
		try(PreparedStatement stmt = con.prepareStatement(sql);PreparedStatement stmt2 = con.prepareStatement(sql2)) {
			stmt.setLong(1, customerId);
			stmt.setLong(2, couponId);
			int dml = stmt.executeUpdate();
			if(dml==0) {
				throw new CouponSystemException("purchase coupon failed, ID  : " + couponId);
			}				
			
			stmt2.setInt(1,amount -1);
			int dml2 = stmt2.executeUpdate();
			if(dml2==0) {
				throw new CouponSystemException("update coupon failed, ID  : " + couponId);
			}				
		} catch (SQLException e) {
			throw new CouponSystemException("purchase coupon failed : ", e);
		}finally {			
			pool.returnConnection(con);	
		}
	}

	@Override
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		Connection con = pool.getConnection();
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
				throw new CouponSystemException("get coupon failed, ID : " + couponID);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("get coupon failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}	
	}

	@Override
	public CouponBean getCouponByTitle(String title) throws CouponSystemException {
		Connection con = pool.getConnection();
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
				throw new CouponSystemException("get coupon failed, Title : " + title);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("get coupon by title failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}	
	}

	@Override
	public Collection<CouponBean> getCouponsByType(CouponType type) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
		
		String sql ="SELECT * FROM coupon WHERE TYPE =?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, type.toString());
			ResultSet rs =  stmt.executeQuery();
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException("get coupons by type failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
	
		try (Statement stmt = con.createStatement();){		
			ResultSet rs = stmt.executeQuery("SELECT * FROM coupon");			
			while(rs.next()) {	
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException("get all coupons failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		Connection con = pool.getConnection();
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();		
		String sql = "SELECT FROM coupon WHERE comp_id = ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setLong(1, companyId);	
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				coupons.add(readCoupon(rs));
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException("get company coupon failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
		
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
			throw new CouponSystemException("get Company Coupons by type failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
		
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
			throw new CouponSystemException("get customer coupons failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
		
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
			throw new CouponSystemException("get customer coupons failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection con = pool.getConnection();
		
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
			throw new CouponSystemException("get customer coupons failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}		
		return coupons;
	}

	

	
	
	/*@Override
	public void addCouponToCompany(Coupon coupon, long compId) throws CouponSystemException {
		Connection con = pool.getConnection();
		
		String sql = "INSERT INTO company_coupon VALUES(?,?)";	
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, compId);
			stmt.setLong(2, coupon.getCouponId());
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("add coupon to company failed, ID  : " + coupon.getCouponId());
			}			
		} catch (SQLException e) {
			throw new CouponSystemException("add coupon to company failed : ", e);
		}finally {			
			pool.returnConnection(con);	
		}
	}*/

	/*@Override
	public void removeCouponFromCompanies(long couponId) throws CouponSystemException {
		Connection con = pool.getConnection();
		
		String sql = "DELETE FROM company_coupon WHERE coupon_ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponId);
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("remove coupon from companies failed, ID  : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from companies failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}
	}*/

	/*@Override
	public void removeCouponFromCustomers(long couponId) throws CouponSystemException {
		Connection con = pool.getConnection();
	
		String sql = "DELETE FROM customer_coupon WHERE cust_ coupon_ID = ? ";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(2, couponId);
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("remove coupon from customer failed, ID  : " + couponId.getCouponId());
			}
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from customer failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}
	}*/

	@Override
	public void removeCoupon(long couponId) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();		
		
		String sql = "DELETE FROM coupon WHERE ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponId);
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("remove coupon from coupon failed, ID  : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from coupon failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}		
	}

	@Override
	public void removeCustomerCoupons(long customerId) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();		
		
		String sql = "DELETE FROM customer_coupon WHERE cust_ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, customerId);
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("remove customer coupon failed, ID  : " + customerId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from coupon failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}	
	}

	@Override
	public void removeCompanyCoupons(long companyId) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();		
		
		String sql = "DELETE FROM coupon WHERE comp_ID = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("remove company coupon from coupon failed, ID  : " + companyId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from coupon failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}	
	}
	@Override
	public void removeCouponFromCustomers(long couponId) throws CouponSystemException {
		Connection con = pool.getConnection();		
		
		String sql = "DELETE FROM customer_coupon WHERE coupon_ID = ? ";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, couponId );
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException("remove coupon from customers failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}
	}

	@Override
	public void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();		
		
		String sql = "DELETE customer_coupon FROM customer_coupon"
				+ " INNER JOIN coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.comp_id = ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setLong(1, companyId);
			int dml = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException("remove Expired Coupons failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}	
		
	}

	@Override
	public void removeExpiredCoupons() throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection con = pool.getConnection();		
		
		String sql = "DELETE coupon, customer_coupon FROM coupon"
				+ " INNER JOIN customer_coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.end_date <= ?";
		try(PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDate(1, new Date(System.currentTimeMillis()));
			int dml = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException("remove Expired Coupons failed : ", e);
		}finally {			
			pool.returnConnection(con);
		}	
		
	}

	@Override
	public boolean couponIdAlreadyExists(long couponId) {
		// TODO Auto-generated method stub
		return false;
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

