package com.ronhagai.couponfaphase3.rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.catalina.mbeans.UserMBean;

import com.ronhagai.couponfaphase3.core.beans.LoginBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.service.CompanyService;
import com.ronhagai.couponfaphase3.core.service.CustomerService;

/**
 *  A login rest controller that handle login and logout
 * @author hagai
 *
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginRestController {
	@Context
	HttpServletResponse response;

	
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@FormParam("userName") String userName,
//	@FormParam("userPassword") String userPassword,
//	@FormParam("userType") String userType,
//	@FormParam("remeberMe") String remeberMe)
	
	/**
	 * login controller that check the user type and call to the right login service.
	 * if login was successful than token and type cookies will be planted at the user
	 * @param loginBean that contain:
	 * user name
	 * user password
	 * user type
	 * Remember me (if user want a long login connection.
	 * @return the userId from the DB
	 * @throws CouponSystemException if:
	 * user name/paswword are not match
	 * if one of the loginBean is null (except the remember me parameter)
	 */
	@POST
	public long login(LoginBean loginBean) throws CouponSystemException {
		System.out.println("login is on");
		long userId;

		Cookie cookieUserId, cookieUserType;
		if(loginBean.getUserName() == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user name seem to be missing");
		}
		if(loginBean.getUserPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user password seem to be missing");
		}
		
		if(loginBean.getUserType() == null) {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		if (loginBean.getUserType().equals("ADMIN") && loginBean.getUserName().equals("admin") && loginBean.getUserPassword().equals("1234")) {
			userId = 123456789;
		}else if (loginBean.getUserType().equals("CUSTOMER")) {
			userId = CustomerService.getInstance().customerLogin(loginBean.getUserName(), loginBean.getUserPassword());
		} else if (loginBean.getUserType().equals("COMPANY")) {
			userId = CompanyService.getInstance().companyLogin(loginBean.getUserName(), loginBean.getUserPassword());

		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}
		
		cookieUserType = new Cookie("userType", loginBean.getUserType());
		cookieUserId = new Cookie("userId", String.valueOf(userId));
		if (loginBean.getRemeberMe() != null && loginBean.getRemeberMe().equals("true")) {
			cookieUserType.setMaxAge(60 * 60 * 24 * 365);
			cookieUserId.setMaxAge(60 * 60 * 24 * 365);
		} else {
			cookieUserType.setMaxAge(60*30);
			cookieUserId.setMaxAge(60*30);
		}

		response.addCookie(cookieUserId);
		response.addCookie(cookieUserType);
		return userId;
	}
	
	/**
	 * delete all user cookies (id and type)
	 */
	@DELETE
	public void logout() {
		System.out.println("user try to loguot");
		Cookie cookieUserId = new Cookie("userId","null");
		Cookie cookieUserTypeCookie = new Cookie("userType","null");;
		cookieUserId.setMaxAge(0);
		cookieUserTypeCookie.setMaxAge(0);
		response.addCookie(cookieUserId);
		response.addCookie(cookieUserTypeCookie);
		System.out.println("user try to loguot the end");
	}
}

