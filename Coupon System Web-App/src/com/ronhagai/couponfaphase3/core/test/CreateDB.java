package com.ronhagai.couponfaphase3.core.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.util.ConnectionPool;

/**
 * Class used for the creation of a new DB
 * @author Ron
 */

public class CreateDB {

	private ConnectionPool pool ;

	/**
	 * Gets a database connection from connection pool
	 */
	public CreateDB() throws CouponSystemException {
		pool = ConnectionPool.getInstance();
	}

	/**
	 * Creates a new empty DB.
	 * @throws CouponSystemException If any of the tables fail to create
	 */
	public void createDb() throws CouponSystemException {

		Connection con = pool.getConnection();

		try (Statement stmt = con.createStatement()){
			// create company table
			String str = "CREATE TABLE company("
					+ "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
					+ "comp_name VARCHAR(50) NOT NULL, "
					+ "password INTEGER, "
					+ "email VARCHAR(50) NOT NULL) AUTO_INCREMENT=1000000000000000001";
			stmt.executeUpdate(str);

			//create customer table
			str = "CREATE TABLE customer("
					+ "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
					+ "cust_name VARCHAR(50) NOT NULL, "
					+ "password INTEGER) AUTO_INCREMENT=2000000000000000001";
			stmt.executeUpdate(str);

			// create coupon table
			str = "CREATE TABLE coupon ("
					+ "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
					+ "title VARCHAR (255), "
					+ "start_date DATE, "
					+ "end_date DATE NOT NULL, "
					+ "amount INTEGER, "
					+ "type VARCHAR(30), "
					+ "message VARCHAR(255), "
					+ "price DOUBLE NOT NULL, "
					+ "image VARCHAR(255), "
					+ "comp_id BIGINT NOT NULL, "
					+ "FOREIGN KEY (comp_id) REFERENCES company(id)) AUTO_INCREMENT=3000000000000000001";
//					+ "ON DELETE CASCADE ON UPDATE NO ACTION ";
			stmt.executeUpdate(str);

			// create customer coupon table
			str = "CREATE TABLE customer_coupon ("
					+ "cust_id BIGINT NOT NULL, "
					+ "coupon_id BIGINT NOT NULL, "
					+ "FOREIGN KEY (cust_id) REFERENCES customer(id), "
					/*+ "ON DELETE CASCADE ON UPDATE NO ACTION, "
					+ "FOREIGN KEY (coupon_id) REFERENCES coupon(id) "
					+ "ON DELETE CASCADE ON UPDATE NO ACTION), "*/
					+ "PRIMARY KEY (cust_id, coupon_id))";
			stmt.executeUpdate(str);

			System.out.println("LOG : new db created");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new CouponSystemException(ExceptionsEnum.TEST,"create DB failed : ", e);
		}finally {
			pool.returnConnection(con);
		}
	}

	/**
	 * <strong>!CAUTION!</strong> Drops <strong>ALL</strong> DB tables.
	 * This is a <strong>PERMANENT ACTION</strong> that <strong>CANNOT</strong> be undone.
	 *
	 * @throws CouponSystemException If any table deletion fails.
	 */
	public void dropTables() throws CouponSystemException {
		Connection con = pool.getConnection();

		try (Statement stmt = con.createStatement()){
			stmt.executeUpdate("drop table if exists customer_coupon, customer, coupon, company");
			System.out.println("LOG : DB removed");

		} catch (SQLException e) {
			// TODO Manager handling
			//e.printStackTrace();
			System.err.println("delete DB failed : " + e);
		}finally {
			pool.returnConnection(con);
		}
	}

}
