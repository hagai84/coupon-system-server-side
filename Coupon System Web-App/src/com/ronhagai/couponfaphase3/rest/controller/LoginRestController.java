package com.ronhagai.couponfaphase3.rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginRestController {
	@Context
	HttpServletResponse response;

	@POST
	public long login(LoginBean loginBean) throws CouponSystemException {
		System.out.println("login is on");
		long userId;
		Cookie cookieUserId;

		if (loginBean.getUserName() == null || loginBean.getUserPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA, "name/password cant be null");
		}

		if (loginBean.getUserType() == null && !loginBean.getUserName().equals("admin")) {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		if (loginBean.getUserName().equals("admin") && loginBean.getUserPassword().equals("1234")) {
			cookieUserId = new Cookie("userId", "123456789");
			loginBean.setUserType("admin");
			userId = -1;
		} else if (loginBean.getUserType().equals("customer")) {
			userId = CustomerService.getInstance().customerLogin(loginBean.getUserName(), loginBean.getUserPassword());
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else if (loginBean.getUserType().equals("company")) {
			userId = CompanyService.getInstance().companyLogin(loginBean.getUserName(), loginBean.getUserPassword());
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}

		if (loginBean.getRemeberMe() != null && loginBean.getRemeberMe().equals("true")) {
			cookieUserId.setMaxAge(60 * 60 * 24 * 365);
		} else {
			cookieUserId.setMaxAge(60 * 30);
		}

		Cookie cookieUserTypeCookie = new Cookie("userType", loginBean.getUserType());

		if (loginBean.getRemeberMe() != null && loginBean.getRemeberMe().equals("true")) {
			cookieUserTypeCookie.setMaxAge(60 * 60 * 24 * 365);
		} else {
			cookieUserTypeCookie.setMaxAge(60 * 30);
		}
		response.addCookie(cookieUserId);
		response.addCookie(cookieUserTypeCookie);
		return userId;
	}
	
	
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

