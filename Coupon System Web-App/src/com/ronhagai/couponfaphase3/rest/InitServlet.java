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
		driverName = getInitParameter("driverName");
		databaseUrl = getInitParameter("databaseUrl");
		userName = getInitParameter("userName");
		password = getInitParameter("password");
		couonSystem.setServer(driverName, databaseUrl, userName, password);	
	}
}
