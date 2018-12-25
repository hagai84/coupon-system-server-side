package com.ronhagai.couponfaphase3.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Implements {@link ICustomerDAO} interface
 * @author Ron
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

	@Override
	public long createCustomer(CustomerBean customer) throws CouponSystemException {
		Connection connection = connectionPool.startTransaction();
		
		String insertSql = "INSERT INTO customer (cust_name, password) VALUES(?,?)";
		String selectSql = "SELECT LAST_INSERT_ID() AS ID";
		try(PreparedStatement insertStatement = connection.prepareStatement(insertSql);PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			insertStatement.setString(1, customer.getCustName());
			insertStatement.setInt(2, customer.getPassword().hashCode());
			if(insertStatement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - THROWS EXCEPTION BEFORE
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create customer failed : ");
			}			
			ResultSet resultSet = selectStatement.executeQuery();
			if(resultSet.next()) {
				connectionPool.endTransaction();
				return resultSet.getLong("ID");
			}else {
				connectionPool.rollback();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"create customer failed : ");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			connectionPool.rollback();
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"create customer failed : ", e);
		} finally {
		}
	}

	
	@Override
	public void updateCustomer(CustomerBean customer) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String updateSql = "UPDATE customer SET CUST_NAME=? WHERE ID=?";
		try(PreparedStatement updateStaement = connection.prepareStatement(updateSql)) {
			updateStaement.setString(1, customer.getCustName());
			updateStaement.setLong(2, customer.getId());
			if(updateStaement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update customer failed, ID : " + customer.getId());
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update customer failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		
	}
	
	@Override
	public void updateCustomerPassword(long customerId, String newPassword) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();		
		
		String updateSql = "UPDATE customer SET PASSWORD=? WHERE ID=? ";
		try(PreparedStatement updateStaement = connection.prepareStatement(updateSql)) {
			updateStaement.setInt(1, newPassword.hashCode());
			updateStaement.setLong(2, customerId);
			if(updateStaement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"update customer's password failed, ID : " + customerId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"update customer's password failed", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		
	}
	
	
	@Override
	public void removeCustomer(long customerId) throws CouponSystemException {	
		Connection connection = connectionPool.getConnection();
		
		String deleteSql = "DELETE FROM customer WHERE id = ?";
		try(PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
			deleteStatement.setLong(1, customerId);
			if(deleteStatement.executeUpdate()==0) {
				//SHLD NEVER HAPPEN - CLIENT SIDE ERROR
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"remove customer failed, ID : " + customerId);
			}			
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"remove customer failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
		
	}
	
	@Override
	public CustomerBean getCustomer(long custId) throws CouponSystemException {
		Connection connection = connectionPool.getConnection();
		CustomerBean customer = null;
		
		String selectSql = "SELECT * FROM customer WHERE ID=?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setLong(1, custId);
			ResultSet resultSet = selectStatement.executeQuery();			
			if(resultSet.next()) {
				customer = readCustomer(resultSet);
				resultSet.close();
				return customer;
			}else {
				resultSet.close();
				throw new CouponSystemException(ExceptionsEnum.FAILED_OPERATION,"get customer failed,  ID : " + custId);
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
	}
	
	@Override
	public Collection<CustomerBean> getAllCustomers() throws CouponSystemException {
		Collection<CustomerBean> customers = new ArrayList<CustomerBean>();
		Connection connection = connectionPool.getConnection();
		CustomerBean customer = null;	
		
		String selectSql = "SELECT * FROM customer";
		try (Statement selectStatement = connection.createStatement();){	
			ResultSet resultSet = selectStatement.executeQuery(selectSql);			
			while(resultSet.next()) {
				customer = readCustomer(resultSet);				
				customers.add(customer);
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get all customers failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
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
		
		Connection connection = connectionPool.getConnection();
		
		String selectSql = "SELECT id FROM customer WHERE CUST_NAME=? AND PASSWORD=?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)){
			selectStatement.setString(1, customerName);
			selectStatement.setInt(2, password.hashCode());
			ResultSet resultSet = selectStatement.executeQuery();
			
			if(resultSet.next()) {
//				resultSet.close();
				return resultSet.getLong("ID");
			}else {
				throw new CouponSystemException(ExceptionsEnum.AUTHENTICATION,"Incorrect Name Or Password");
			}	
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"customer login failed : ", e);
//		} catch (NullPointerException e) {
//			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"name/password cant be null");
		}
		finally {
			connectionPool.returnConnection(connection);			
		}	
	}
	
	@Override
	public boolean customerNameAlreadyExists(String customerName) throws CouponSystemException {
		// TODO Auto-generated method stub
		Connection connection = connectionPool.getConnection();
		String selectSql = "SELECT CUST_NAME FROM customer WHERE CUST_NAME=?  LIMIT 1";
		try (PreparedStatement selectStTEMENT = connection.prepareStatement(selectSql)){
			selectStTEMENT.setString(1, customerName);
			ResultSet resultSet = selectStTEMENT.executeQuery();
			if (resultSet.next()) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new CouponSystemException(ExceptionsEnum.DATA_BASE_ERROR,"get customer by name failed : ", e);
		} finally {
			connectionPool.returnConnection(connection);			
		}
	}
	
	/**
	 * Reads a ResultSet of a specific customer from the DB and into a {@link CustomerBean} object
	 * @param resultSet ResultSet of a single customer
	 * @return A {@link CustomerBean} object representation of the customer
	 * @throws SQLException If reading information from the ResultSet fails
	 */
	private CustomerBean readCustomer(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		CustomerBean customer = new CustomerBean();
		customer.setId(resultSet.getLong("ID"));
		customer.setCustName(resultSet.getString("CUST_NAME"));
		customer.setPassword("***PASSWORD***");
		return customer;
	}
}
