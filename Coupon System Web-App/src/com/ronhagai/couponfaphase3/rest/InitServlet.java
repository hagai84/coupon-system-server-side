package com.ronhagai.couponfaphase3.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.ronhagai.couponfaphase3.core.CouponSystem;


public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static CouponSystem couonSystem = CouponSystem.getInstance();   
       
	private static String driverName;// = "com.mysql.jdbc.Driver";
	private static String databaseUrl;// = "jdbc:mysql://db4free.net:3306/coupon_system";
	private static String userName;// = "coupon_group";
	private static String password;// = "12345678";


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		couonSystem.shutdown();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		//from xml
//		driverName = getInitParameter("driverName");
//		databaseUrl = getInitParameter("databaseUrl");
//		userName = getInitParameter("userName");
//		password = getInitParameter("password");
		
		
		//connect to localhost mySql
//		driverName = "com.mysql.jdbc.Driver";
//		databaseUrl = "jdbc:mysql://localhost:3306/coupon_system?createDatabaseIfNotExist=true";
//		userName = "root";
//		password = "1234";
		
		
		//connect to AWS RDB
		driverName = "com.mysql.jdbc.Driver";
		databaseUrl = "jdbc:mysql://couponsystem2.c2r3koxg7oj5.us-east-2.rds.amazonaws.com:3306/coupon_system?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false";
		userName = "hagai";
		password = "hagairon";
		
		couonSystem.setServer(driverName, databaseUrl, userName, password);	
	}
}
