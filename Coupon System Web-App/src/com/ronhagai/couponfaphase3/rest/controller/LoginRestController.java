package com.ronhagai.couponfaphase3.rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public long login(
			@FormParam("userName") String userName,
			@FormParam("userPassword") String userPassword,
			@FormParam("userType") String userType,
			@FormParam("remeberMe") String remeberMe)
			throws CouponSystemException {
		long userId;
		Cookie cookieUserId;
		
		
		
		if(userName == null || userPassword == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"name/password cant be null");
		}
		
		if(userType == null && !userName.equals("admin")) {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		if (userName.equals("admin") && userPassword.equals("1234")) {
			cookieUserId = new Cookie("userId", "123456789");
			userType = "admin";
			userId = -1;
		}else if (userType.equals("customer")) {
			userId = CustomerService.getInstance().customerLogin(userName, userPassword);
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else if (userType.equals("company")) {
			userId = CompanyService.getInstance().companyLogin(userName, userPassword);
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}

		if (remeberMe != null && remeberMe.equals("true")) {
			cookieUserId.setMaxAge(60 * 60 * 24 * 365);
		} else {
			cookieUserId.setMaxAge(-1);
		}

		Cookie cookieUserTypeCookie = new Cookie("userType", userType);
		response.addCookie(cookieUserId);
		response.addCookie(cookieUserTypeCookie);
		return userId;
	}


}
