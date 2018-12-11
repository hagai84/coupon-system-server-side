package core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import core.beans.CustomerBean;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
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
	private static ICustomerDAO customerDAOInstance = new CustomerDAO();
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

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
		return customerDAOInstance;
	}

	/* (non-Javadoc)
	 * @see coupon.system.dao.CustomerDAO#createCustomer(coupon.system.beans.Customer)
	 */
	@Override
	public void createCustomer(CustomerBean customer) throws CouponSystemException {
		Connection con = connectionPool.getConnection();
		
		String sql = "INSERT INTO customer "
				+ "(cust_name, password) "
				+ "VALUES(?,?)";
		try(PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setString(1, customer.getCustName());
			stmt.setString(2, customer.getPassword());
			if(stmt.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create customer failed : ");
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"create customer failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}
	}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#updateCustomer(coupon.system.beans.Customer)
		 */
		@Override
		public void updateCustomer(CustomerBean customer) throws CouponSystemException {
			Connection con = connectionPool.getConnection();		
			
			String sql = "UPDATE customer SET ID=?, CUST_NAME=?, PASSWORD=? WHERE ID=?";
			try(PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setLong(1, customer.getId());
				stmt.setString(2, customer.getCustName());
				stmt.setString(3, customer.getPassword());
				stmt.setLong(4, customer.getId());
				if(stmt.executeUpdate()==0) {
					//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
					throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update customer failed, ID : " + customer.getId());
				}
			} catch (SQLException e) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update customer failed : ", e);
			} finally {
				connectionPool.returnConnection(con);			
			}
	
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#removeCustomer(coupon.system.beans.Customer)
		 */
		@Override
		public void removeCustomer(long customerId) throws CouponSystemException {	
			Connection con = connectionPool.getConnection();
				
			String sql = "DELETE FROM customer WHERE id = ?";
			try(PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setLong(1, customerId);
				if(stmt.executeUpdate()==0) {
					//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
					throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"remove customer failed, ID : " + customerId);
				}			
			} catch (SQLException e) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove customer failed : ", e);
			} finally {
				connectionPool.returnConnection(con);			
			}
			
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#getCustomer(long)
		 */
		@Override
		public CustomerBean getCustomer(long custId) throws CouponSystemException {
			Connection con = connectionPool.getConnection();
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
					throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get customer failed,  ID : " + custId);
				}
			} catch (SQLException e) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer failed : ", e);
			} finally {
				connectionPool.returnConnection(con);			
			}
		}

	/* (non-Javadoc)
		 * @see coupon.system.dao.CustomerDAO#getCustomerByName(String)
		 */
		public CustomerBean getCustomerByName(String custName) throws CouponSystemException {
			Connection con = connectionPool.getConnection();
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
					throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get customer failed, name : " + custName);
				}
			} catch (SQLException e) {
				throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer by name failed : ", e);
			} finally {
				connectionPool.returnConnection(con);			
			}
		}


	/* (non-Javadoc)
	 * @see coupon.system.dao.CustomerDAO#getAllCustomers()
	 */
	@Override
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException {
		Collection<CustomerBean> customers = new ArrayList<CustomerBean>();
		Connection con = connectionPool.getConnection();
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
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get all customers failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}		
		return customers;		
	}

	/**
	 * Returns true if the given customer user name is in the DB and if the given
	 * password is equal to the password in the DB (same row as the customer name)
	 *
	 * @param customerName The customer's user name
	 * @param password The customer's password
	 * @return <code>true</code> if user name and password match; otherwise <code>false</code>
	 * @throws CouponSystemException If there is a connection problem or an <code>SQLException</code> is thrown.
	 *
	 */
	public long customerLogin(String customerName, String password) throws CouponSystemException {
		
		Connection con = connectionPool.getConnection();
		
		String sql = "SELECT id FROM customer WHERE CUST_NAME=? AND PASSWORD=?";
		try (PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setString(1, customerName);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
//				rs.close();
				return rs.getLong("ID");
			}else {
				throw new CouponSystemException(ExceptionsEnum.AUTHENTICATION,"Incorrect Name Or Password");
			}	
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"customer login failed : ", e);
		} finally {
			connectionPool.returnConnection(con);			
		}	
	}

	@Override
	public boolean customerNameAlreadyExists(String name) {
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
//		customer.setPassword(rs.getString("PASSWORD"));
		return customer;
	}
}
