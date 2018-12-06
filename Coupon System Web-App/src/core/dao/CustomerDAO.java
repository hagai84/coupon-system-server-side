package core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.exception.CouponSystemException;
import core.util.ConnectionPool;

/**
 * Implements {@link ICustomerDAO} interface
 * @author Yair
 *
 */
public class CustomerDAO implements ICustomerDAO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConnectionPool pool = ConnectionPool.getInstance();
	private static ICustomerDAO custDAO = new CustomerDAO();

	/**
	 * Private singleton constructor.
	 */
	private CustomerDAO() {
	}

	/**
	 * Returns a static instance of CustomerDBDAO
	 * @return A static instance of CustomerDBDAO
	 */
	public static ICustomerDAO getInstance() {
		return custDAO;
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CustomerDAO#createCustomer(coupon.system.beans.Customer)
	 */
	@Override
	public void createCustomer(CustomerBean customer) throws CouponSystemException {
		Connection con = pool.getConnection();
		
		String sql = "INSERT INTO customer VALUES(?,?,?)";
		try(PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setLong(1, customer.getId());
			stmt.setString(2, customer.getCustName());
			stmt.setString(3, customer.getPassword());
			int dml = stmt.executeUpdate();
			//TODO add check to see how many rows were updated
			if(dml==0) {
				throw new CouponSystemException("create customer failed, ID : " + customer.getId());
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new CouponSystemException("create customer failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}
	}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#updateCustomer(coupon.system.beans.Customer)
		 */
		@Override
		public void updateCustomer(CustomerBean customer) throws CouponSystemException {
			Connection con = pool.getConnection();		
			
	//		String sql = "UPDATE customer SET PASSWORD=? WHERE ID=?";
			String sql = "UPDATE customer SET ID=?, CUST_NAME=?, PASSWORD=? WHERE ID=?";
			try(PreparedStatement stmt = con.prepareStatement(sql)) {
	/*			stmt.setString(1, customer.getPassword());
				stmt.setLong(2, customer.getId());*/
				stmt.setLong(1, customer.getId());
				stmt.setString(2, customer.getCustName());
				stmt.setString(3, customer.getPassword());
				stmt.setLong(4, customer.getId());
				int dml = stmt.executeUpdate();
				//TODO add check to see how many rows were updated
				if(dml==0) {
					throw new CouponSystemException("update customer failed, ID : " + customer.getId());
				}
			} catch (SQLException e) {
				throw new CouponSystemException("update customer failed : ", e);
			} finally {
				pool.returnConnection(con);			
			}
	
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#removeCustomer(coupon.system.beans.Customer)
		 */
		@Override
		public void removeCustomer(long customerId) throws CouponSystemException {	
			Connection con = pool.getConnection();
				
			String sql = "DELETE FROM customer WHERE id = ?";
			try(PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setLong(1, customerId);
				int dml = stmt.executeUpdate();
				//TODO add check to see how many rows were updated
				if(dml==0) {
					throw new CouponSystemException("remove customer failed, ID : " + customerId);
				}			
			} catch (SQLException e) {
				throw new CouponSystemException("remove customer failed : ", e);
			} finally {
				pool.returnConnection(con);			
			}
			
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#getCustomer(long)
		 */
		@Override
		public CustomerBean getCustomer(long custId) throws CouponSystemException {
			Connection con = pool.getConnection();
			CustomerBean customer = null;
			
			String sql = "SELECT * FROM customer WHERE ID=?";
			try (PreparedStatement stmt = con.prepareStatement(sql)){
				stmt.setLong(1, custId);
				ResultSet rs = stmt.executeQuery();			
				if(rs.next()) {
					customer = readCustomer(rs);
					rs.close();
					return customer;
				}else {
					rs.close();
					throw new CouponSystemException("get customer failed,  ID : " + custId);
				}
			} catch (SQLException e) {
				throw new CouponSystemException("get customer failed : ", e);
			} finally {
				pool.returnConnection(con);			
			}
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#getCustomerByName(String)
		 */
		public CustomerBean getCustomerByName(String custName) throws CouponSystemException {
			Connection con = pool.getConnection();
			CustomerBean customer=null;
			
			String sql = "SELECT * FROM customer WHERE CUST_NAME=?";
			try (PreparedStatement stmt = con.prepareStatement(sql)){
				stmt.setString(1, custName);
				ResultSet rs = stmt.executeQuery();		
				if(rs.next()) {
					customer = readCustomer(rs);
					rs.close();
					return customer;
				}else {
					rs.close();
					throw new CouponSystemException("get customer failed, name : " + custName);
				}
			} catch (SQLException e) {
				throw new CouponSystemException("get customer by name failed : ", e);
			} finally {
				pool.returnConnection(con);			
			}
		}

	
	
	

	/* (non-Javadoc)
	 * @see coupon.system.dao.CustomerDAO#getCoupons()
	 */
	/*@Override
	public Collection<Coupon> getCoupons(long customerId) throws CouponSystemException {
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Connection con = pool.getConnection();
		
		String sql="SELECT coupon.* FROM coupon " 
				+"INNER JOIN customer_coupon ON coupon.id = customer_coupon.coupon_id "
				+ "WHERE customer_coupon.cust_id =?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){	
			stmt.setLong(1, customerId.getId());	
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
	}*/

	/* (non-Javadoc)
	 * @see coupon.system.dao.CustomerDAO#getAllCustomers()
	 */
	@Override
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException {
		Collection<CustomerBean> customers = new ArrayList<CustomerBean>();
		Connection con = pool.getConnection();
		CustomerBean customer = null;	
		
		String sql = "SELECT * FROM customer";
		try (Statement stmt = con.createStatement();){	
			ResultSet rs = stmt.executeQuery(sql);			
			while(rs.next()) {
				customer = readCustomer(rs);				
				customers.add(customer);
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException("get all customers failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}		
		return customers;
		
	}

	/**
	 * Returns true if the given customer user name is in the DB and if the given
	 * password is equal to the password in the DB (same row as the customer name)
	 *
	 * @param custName The customer's user name
	 * @param password The customer's password
	 * @return <code>true</code> if user name and password match; otherwise <code>false</code>
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 *
	 */
	public long customerLogin(String custName, String password) throws CouponSystemException {
		
		Connection con = pool.getConnection();
		
		String sql = "SELECT id FROM customer WHERE CUST_NAME=? AND PASSWORD=?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setString(1, custName);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getLong("ID");
			}
			rs.close();
		} catch (SQLException e) {
			throw new CouponSystemException("customer login failed : ", e);
		} finally {
			pool.returnConnection(con);			
		}	
		return -1;
	}

	/**
	 * Reads a ResultSet of a customer's specific coupon from the DB and into a {@link CouponBean} object
	 * @param rs ResultSet of a single coupon
	 * @return A Coupon object representation of the coupon
	 * @throws SQLException If reading information from the ResultSet fails
	 */
	/*private Coupon readCoupon(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		Coupon coupon = new Coupon();
		coupon.setId(rs.getLong("ID"));
		coupon.setTitle(rs.getString("TITLE"));
		coupon.setStartDate(rs.getDate("START_DATE"));
		coupon.setEndDate(rs.getDate("END_DATE"));
		coupon.setAmount(rs.getInt("AMOUNT"));
		coupon.setType(CouponType.valueOf(rs.getString("TYPE")));
		coupon.setMessage(rs.getString("MESSAGE"));
		coupon.setPrice(rs.getDouble("PRICE"));
		coupon.setImage(rs.getString("IMAGE"));
		return coupon;
	}
*/
	@Override
	public boolean customerNameAlreadyExists(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean customerIdAlreadyExists(long customerId) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Reads a ResultSet of a specific customer from the DB and into a {@link CustomerBean} object
	 * @param rs ResultSet of a single customer
	 * @return A {@link CustomerBean} object representation of the customer
	 * @throws SQLException If reading information from the ResultSet fails
	 */
	private CustomerBean readCustomer(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		CustomerBean customer = new CustomerBean();
		customer.setId(rs.getLong("ID"));
		customer.setCustName(rs.getString("CUST_NAME"));
		customer.setPassword(rs.getString("PASSWORD"));
		return customer;
	}
}
