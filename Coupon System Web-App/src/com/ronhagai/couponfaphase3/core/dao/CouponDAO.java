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
				throw new CouponSystemException(ExceptionsEnum.COUPON_NOT_CREATED,"Create coupon failed : " + coupon);
			}
			ResultSet createIdResultSet = selectStatement.executeQuery();
			if(createIdResultSet.next()) {
				long id = createIdResultSet.getLong("ID");
				connectionPool.endTransaction();
				return id;
			}else {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.GENERATE_ID_NOT_RETRIEVED,"create coupon failed : " + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"Create coupon failed : " + coupon, e);
		}finally {			
		}	
	}

	@Override
	public void purchaseCoupon(long couponId, long customerId) throws CouponSystemException{		
		String purchaseSql = "CALL purchase_coupon(?,?)";	
		Connection connection = connectionPool.getConnection();
		try(PreparedStatement purchaseStatement = connection.prepareStatement(purchaseSql)) {
			purchaseStatement.setLong(1, couponId);
			purchaseStatement.setLong(2, customerId);
			purchaseStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ACCSESS,"purchase coupon failed : " + couponId, e);
		}finally {
			connectionPool.returnConnection(connection);		
		}	
	}
	
	@Override
	public void cancelPurchaseCoupon(long couponId, long customerId) throws CouponSystemException{		
		String purchaseSql = "CALL cancel_purchase_coupon(?,?)";	
		Connection connection = connectionPool.getConnection();
		try(PreparedStatement purchaseStatement = connection.prepareStatement(purchaseSql)) {
			purchaseStatement.setLong(1, couponId);
			purchaseStatement.setLong(2, customerId);
			purchaseStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.CANCEL_PURCHASE_CUSTOMER_FAILED,"cancel purchase coupon failed : " + couponId, e);
		}finally {
			connectionPool.returnConnection(connection);		
		}	
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
				throw new CouponSystemException(ExceptionsEnum.COUPON_NOT_UPDATED,"update coupon failed : " + coupon);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update coupon failed : " + coupon, e);
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
				throw new CouponSystemException(ExceptionsEnum.AMOUNT_NOT_UPDATED ,"update amount failed : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR ,"update coupon failed : " + couponId, e);
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
				throw new CouponSystemException(ExceptionsEnum.COUPON_NOT_REMOVED,"remove coupon failed : " + couponId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon failed : " + couponId, e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove company's coupons failed : " + companyId, e);
		}finally {			
			connectionPool.returnConnection(connection);
		}	
	}

	@Override
	public void removeCompanyCouponsFromCustomers(long companyId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "DELETE customer_coupon FROM customer_coupon"
				+ " JOIN coupon ON customer_coupon.coupon_id = coupon.id"
				+ " WHERE coupon.comp_id = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, companyId);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove company's Coupons From Customers failed : " + companyId, e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove customer's coupons failed : " + customerId, e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove coupon from customers failed : " + couponId, e);
		}finally {			
			connectionPool.returnConnection(connection);
		}
	}

	@Override
	public void removeExpiredCoupons() throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String deleteSql = "CALL delete_expired_coupons(?) ";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setDate(1, new Date(System.currentTimeMillis()));
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove Expired Coupons from customers failed", e);
		}finally {			
//			connectionPool.returnConnection(connection);
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
				throw new CouponSystemException(ExceptionsEnum.GET_COUPON_FAILED,"get coupon failed : " + couponID);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupon failed : " + couponID, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}	
	}


	@Override
	public Collection<CouponBean> getCouponsByType(CouponType couponType) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE TYPE =?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setString(1, couponType.toString());
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get coupons by type failed : " + couponType, e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get all coupons failed", e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get company coupons failed : " + companyId, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByType(long companyId, CouponType couponType) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND TYPE =?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setString(2, couponType.toString());
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : " + companyId + " - " + couponType, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByPrice(long companyId, double couponPrice) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND PRICE <= ?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setDouble(2, couponPrice);
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by price failed : " + companyId + " - " + couponPrice, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCompanyCouponsByDate(long companyId, Date expirationDate) throws CouponSystemException {
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql ="SELECT * FROM coupon WHERE COMP_ID =? AND END_DATE <= ?";
		try(PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
			selectStatement.setLong(1, companyId);
			selectStatement.setDate(2, expirationDate);
			ResultSet couponsResultSet =  selectStatement.executeQuery();
			while(couponsResultSet.next()) {	
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get Company Coupons by type failed : " + companyId + " - " + expirationDate, e);
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons failed : " + customerId, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByType(long customerId, CouponType couponType) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.type =?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){	
			selectStatement.setLong(1, customerId);	
			selectStatement.setString(2, couponType.toString());
			ResultSet couponsResultSet = selectStatement.executeQuery();			
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons by type failed : " + customerId + " - " + couponType, e);
		} finally {
			connectionPool.returnConnection(connection);			
		}		
		return coupons;
	}

	@Override
	public Collection<CouponBean> getCustomerCouponsByPrice(long customerId, double couponPrice) throws CouponSystemException {
		// TODO Auto-generated method stub
		Collection<CouponBean> coupons = new ArrayList<CouponBean>();
		Connection connection = connectionPool.getConnection();
		
		String selectSql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =? AND coupon.price <=?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){	
			selectStatement.setLong(1, customerId);	
			selectStatement.setDouble(2, couponPrice);
			ResultSet couponsResultSet = selectStatement.executeQuery();			
			while(couponsResultSet.next()) {
				coupons.add(readCoupon(couponsResultSet));
			}
			couponsResultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer coupons by price failed : " + customerId + " - " + couponPrice, e);
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

