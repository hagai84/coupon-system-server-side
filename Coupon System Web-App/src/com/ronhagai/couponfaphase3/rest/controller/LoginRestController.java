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
		Cookie cookieUserId, cookieUserType;
		
		
		
		if(userName == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user name seem to be missing");
		}
		if(userPassword == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user password seem to be missing");
		}
		
		if(userType == null) {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		if (userType.equals("ADMIN") && userName.equals("admin") && userPassword.equals("1234")) {
			userId = 123456789;
		}else if (userType.equals("CUSTOMER")) {
			userId = CustomerService.getInstance().customerLogin(userName, userPassword);
		} else if (userType.equals("COMPANY")) {
			userId = CompanyService.getInstance().companyLogin(userName, userPassword);
		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}

		cookieUserType = new Cookie("userType", userType);
		cookieUserId = new Cookie("userId", String.valueOf(userId));
		if (remeberMe != null && remeberMe.equals("true")) {
			cookieUserType.setMaxAge(60 * 60 * 24 * 365);
			cookieUserId.setMaxAge(60 * 60 * 24 * 365);
		} else {
			cookieUserType.setMaxAge(-1);
			cookieUserId.setMaxAge(-1);
		}

		response.addCookie(cookieUserId);
		response.addCookie(cookieUserType);
		return userId;
	}


}
