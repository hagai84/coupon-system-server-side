package com.ronhagai.couponfaphase3.core.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.enums.CouponType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;;


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
	 * Returns an instance of CouponDBDAO
	 * @return the single object instance of CouponDBDAO
	 */
	public static ICouponDAO getInstance() {
		return couponDAOInstance;
	}

	@Override
	public long createCoupon(CouponBean coupon) throws CouponSystemException {	
		Connection connection = connectionPool.startTransaction();
			
		String insertSql = "INSERT INTO coupon "
				+ "(title, start_date, end_date, amount, type, message, price, image, comp_id) "
				+ "VALUES(?,?,?,?,?,?,?,?,?) ";
		String selectSql = "SELECT LAST_INSERT_ID() AS ID";
		try(PreparedStatement insertStatment = connection.prepareStatement(insertSql);PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			insertStatment.setString(1, coupon.getTitle());
			insertStatment.setDate(2, coupon.getStartDate());
			insertStatment.setDate(3, coupon.getEndDate());
			insertStatment.setInt(4, coupon.getAmount());
			insertStatment.setString(5,coupon.getType().toString());
			insertStatment.setString(6, coupon.getMessage());
			insertStatment.setDouble(7, coupon.getPrice());
			insertStatment.setString(8, coupon.getImage());
			insertStatment.setLong(9, coupon.getCompanyId());

			if(insertStatment.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - THROWS SQL EXCEPTION BEFORE
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"Create coupon failed : " + coupon);
			}
			ResultSet createIdResultSet = selectStatement.executeQuery();
			if(createIdResultSet.next()) {
				long id = createIdResultSet.getLong("ID");
				connectionPool.endTransaction();
				return id;
			}else {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"create coupon failed : " + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"Create coupon failed", e);
		}finally {			
		}	
	}

	@Override
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException{
		
		Connection connection = connectionPool.startTransaction();
		
		String insertSql = "INSERT INTO customer_coupon VALUES(?,?)";	
		String updateSql = "UPDATE coupon SET AMOUNT=amount-1 WHERE ID=? AND amount>0";
		try(PreparedStatement insertStatement = connection.prepareStatement(insertSql);PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			insertStatement.setLong(1, customerId);
			insertStatement.setLong(2, couponId);
			updateStatement.setLong(1, couponId);
			if(updateStatement.executeUpdate()==0) {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"purchase coupon failed - Coupon out of Stock ID : " + couponId);
			}				
			insertStatement.executeUpdate();				
		} catch (SQLException e) {
			connectionPool.rollback();
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"purchase failed - Customer might already owns Coupon", e);
		}finally {			
		}
		connectionPool.endTransaction();
	}

	@Override
	public void updateCoupon(CouponBean coupon) throws CouponSystemException {
		Connection connnection = connectionPool.getConnection();
		
		String updatesSql = "UPDATE coupon SET TITLE=?, START_DATE=?, END_DATE=?, AMOUNT=?, TYPE=?,"
					+ "MESSAGE=?, PRICE=?, IMAGE=?, COMP_ID=? WHERE ID=?";
		try(PreparedStatement updateStatement = connnection.prepareStatement(updatesSql)) {
			updateStatement.setString(1, coupon.getTitle());
			updateStatement.setDate(2, coupon.getStartDate());
			updateStatement.setDate(3, coupon.getEndDate());
			updateStatement.setInt(4, coupon.getAmount());
			updateStatement.setString(5,String.valueOf(coupon.getType()));
			updateStatement.setString(6, coupon.getMessage());
			updateStatement.setDouble(7, coupon.getPrice());
			updateStatement.setString(8, coupon.getImage());
			updateStatement.setLong(9, coupon.getCompanyId());
			updateStatement.setLong(10, coupon.getCouponId());
			if(updateStatement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"update coupon failed :" + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update coupon failed", e);
		}finally {
			connectionPool.returnConnection(connnection);			
		}
	}

	@Override
	public void updateCouponAmout(long couponId, int amoutDelta) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		
		String updateSql = "UPDATE coupon SET AMOUNT=AMOUNT+? WHERE ID=? AND AMOUNT+?>=0 ";
		try(PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			updateStatement.setInt(1, amoutDelta);
			updateStatement.setLong(2,couponId);
			updateStatement.setInt(3, amoutDelta);
			if(updateStatement.executeUpdate()==0) {
				//NEGATIVE DELTA TO BIG or CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS ,"update amount failed : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR ,"update coupon failed", e);
		}finally {
			connectionPool.returnConnection(connection);			
		}
	}
	
	@Override
	public void removeCoupon(long couponId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE FROM coupon WHERE ID = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, couponId);
			if(deleteStatement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.DATA_CONFLICTS,"remove coupon failed, ID  : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon failed", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}		
	}

	@Override
	public void removeCompanyCoupons(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE FROM coupon WHERE comp_ID = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, companyId);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from coupon failed : ", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}	
	}

	@Override
	public void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE customer_coupon FROM customer_coupon"
				+ " INNER JOIN coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.comp_id = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, companyId);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove Company Coupons From Customers failed : ", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}	
		
	}

	@Override
	public void removeCustomerCoupons(long customerId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE FROM customer_coupon WHERE cust_ID = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, customerId);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from coupon failed : ", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}	
	}

	@Override
	public void removeCouponFromCustomers(long couponId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE FROM customer_coupon WHERE coupon_ID = ? ";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, couponId );
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from customers failed : ", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}
	}

	@Override
	public void removeExpiredCoupons() throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE coupon, customer_coupon FROM coupon"
				+ " INNER JOIN customer_coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.end_date < ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setDate(1, new Date(System.currentTimeMillis()));
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove Expired Coupons failed : ", e);
		}finally {			
			connectionPool.returnConnection(connection);
		}	
		
	}

	@Override
	public CouponBean getCoupon(long couponID) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		CouponBean coupon = null;		
		
		String selectSql = "SELECT * FROM coupon WHERE id =?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, couponID);
			ResultSet couponResultSet =  selectStatement.executeQuery();
			if(couponResultSet.next()) {
				coupon = readCoupon(couponResultSet);
				couponResultSet.close();
				return coupon;
			}else {
				couponResultSet.close();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get coupon failed, ID : " + couponID);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupon failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}	
	}


	@Override
	public Collection<CouponBean> getCouponsByType(CouponType type) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE TYPE =?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setString(1, type.toString());
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getAllCoupons() throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
	
		try (Statement selectStatement = connection.createStatement();){		
			ResultSet couponsResultSet = selectStatement.executeQuery("SELECT * FROM coupon");			
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get all coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCoupons(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();		
		String selectSql = "SELECT * FROM coupon WHERE comp_id = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setLong(1, companyId);	
			ResultSet couponsResultSet = selectStatement.executeQuery();
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company coupon failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType type) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND TYPE =?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setString(2, type.toString());
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double price) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND PRICE <= ?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setDouble(2, price);
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date date) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND END_DATE <= ?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setDate(2, date);
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCoupons(long customerId) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){	
			selectStatement.setLong(1, customerId);	
			ResultSet couponsResultSet = selectStatement.executeQuery();			
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType type) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.type =?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){	
			selectStatement.setLong(1, customerId);	
			selectStatement.setString(2, type.toString());
			ResultSet couponsResultSet = selectStatement.executeQuery();			
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double price) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.price <=?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){	
			selectStatement.setLong(1, customerId);	
			selectStatement.setDouble(2, price);
			ResultSet couponsResultSet = selectStatement.executeQuery();			
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public boolean couponTitleAlreadyExists(String title) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection connection = connectionPool.getConnection();
		String selectSql = "SELECT TITLE FROM coupon WHERE TITLE=? LIMIT 1";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setString(1, title);
			ResultSet titleResultSet = selectStatement.executeQuery();
			if (titleResultSet.next()) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"check coupon title failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
	}
	
	@Override
	public boolean customerAlreadyOwnsCoupon(long couponId, long customerId) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection connection = connectionPool.getConnection();
		String selectSql = "SELECT * FROM customer_coupon WHERE coupon_id=? AND cust_id=? LIMIT 1";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setLong(1, couponId);
			selectStatement.setLong(2, customerId);
			ResultSet titleResultSet = selectStatement.executeQuery();
			if (titleResultSet.next()) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"check customer's ownership failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
	}

	/**
	 * Reads a ResultSet of a specific coupon from the DB and into a {@link CouponBean} object
	 * @param resultSet ResultSet of a single coupon
	 * @return A Coupon object representation of the coupon
	 * @throws SQLException If reading information from the ResultSet fails
	 */
	private CouponBean readCoupon(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		CouponBean coupon = new CouponBean();
		coupon.setCouponId(resultSet.getLong("ID"));
		coupon.setTitle(resultSet.getString("TITLE"));
		coupon.setStartDate(resultSet.getDate("START_DATE"));
		coupon.setEndDate(resultSet.getDate("END_DATE"));
		coupon.setAmount(resultSet.getInt("AMOUNT"));
		coupon.setType(CouponType.valueOf(resultSet.getString("TYPE")));
		coupon.setMessage(resultSet.getString("MESSAGE"));
		coupon.setPrice(resultSet.getDouble("PRICE"));
		coupon.setImage(resultSet.getString("IMAGE"));
		coupon.setCompanyId(resultSet.getLong("COMP_ID"));
		return coupon;
	}

}

