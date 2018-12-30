package com.ronhagai.couponfaphase3.rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


import com.ronhagai.couponfaphase3.core.beans.LoginBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;
import com.ronhagai.couponfaphase3.core.service.CompanyService;
import com.ronhagai.couponfaphase3.core.service.CustomerService;

/**
 *  A login rest controller that handle login and logout
 * @author hagai
 *
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class LoginRestController {
	@Context
	HttpServletResponse response;
	@Context
	HttpServletRequest httpServletRequest;
	
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
	@Path("/login")
	public long login(LoginBean loginBean) throws CouponSystemException {
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

		if (loginBean.getUserType().equals(UserType.ADMIN) && loginBean.getUserName().equals("admin") && loginBean.getUserPassword().equals("1234")) {
			userId = 123456789;
		}else if (loginBean.getUserType().equals(UserType.CUSTOMER)) {
			userId = CustomerService.getInstance().customerLogin(loginBean.getUserName(), loginBean.getUserPassword());
		} else if (loginBean.getUserType().equals(UserType.COMPANY)) {
			userId = CompanyService.getInstance().companyLogin(loginBean.getUserName(), loginBean.getUserPassword());

		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}
		
		cookieUserType = new Cookie("userType", String.valueOf(loginBean.getUserType().toString()));
		cookieUserId = new Cookie("userId", String.valueOf(userId));

		if (loginBean.getRememberMe() != null && loginBean.getRememberMe().equals("true")) {
			cookieUserType.setMaxAge(60 * 60 * 24 * 365);
			cookieUserId.setMaxAge(60 * 60 * 24 * 365);
		} else {
			System.out.println("the user remmeber ne is: " + loginBean.getRememberMe());
			cookieUserType.setMaxAge(-1);
			cookieUserId.setMaxAge(-1);
		}

		System.out.println(String.format("LOG : user %s %s logged in",loginBean.getUserType(), userId));
		response.addCookie(cookieUserId);
		response.addCookie(cookieUserType);
		return userId;
	}
	
	/**
	 * delete all user cookies (id and type)
	 */
	@DELETE
	@Path("/logout")
	public void logout() {
		long userId = ((Long)httpServletRequest.getAttribute("userId")).longValue();
		UserType userType = ((UserType)httpServletRequest.getAttribute("userType"));
		Cookie cookieUserId = new Cookie("userId","null");
		Cookie cookieUserTypeCookie = new Cookie("userType","null");;
		cookieUserId.setMaxAge(0);
		cookieUserTypeCookie.setMaxAge(0);
		response.addCookie(cookieUserId);
		response.addCookie(cookieUserTypeCookie);
		System.out.println(String.format("LOG : user %s %s logged out",userType, userId));
	}
}

