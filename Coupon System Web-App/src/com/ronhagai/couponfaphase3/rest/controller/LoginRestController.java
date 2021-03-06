package com.ronhagai.couponfaphase3.rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronhagai.couponfaphase3.core.beans.UserBean;
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
	private final String ADMIN_NAME = "admin";
	private final String ADMIN_PASSWORD = "1234";
	private final int REMEMBER_ME = 60 * 60 * 24 * 365;
	private final int ADMIN_ID = 123456789;
	
	
			
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
	public long login(UserBean loginBean) throws CouponSystemException {
		long userId;
		Cookie cookieUserId, cookieUserType;
		//check if there isent null data
		if(loginBean.getUserName() == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user name seem to be missing");
		}
		if(loginBean.getUserPassword() == null) {
			throw new CouponSystemException(ExceptionsEnum.NULL_DATA,"user password seem to be missing");
		}
		
		if(loginBean.getUserType() == null) {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		//check if userType, userName and userPassword are match to the data stored in the DB
		if (loginBean.getUserType().equals(UserType.ADMIN) && loginBean.getUserName().equals(ADMIN_NAME) && loginBean.getUserPassword().equals(ADMIN_PASSWORD)) {
			userId = ADMIN_ID;
		}else if (loginBean.getUserType().equals(UserType.CUSTOMER)) {
			userId = CustomerService.getInstance().customerLogin(loginBean.getUserName(), loginBean.getUserPassword());
		} else if (loginBean.getUserType().equals(UserType.COMPANY)) {
			userId = CompanyService.getInstance().companyLogin(loginBean.getUserName(), loginBean.getUserPassword());

		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be worng");
		}
		
		//if all user give the correct name and password set cookies (user id and type) in the response.
		cookieUserType = new Cookie("userType", loginBean.getUserType().toString());
		cookieUserId = new Cookie("userId", String.valueOf(userId));

		if (loginBean.getRememberMe() != null && loginBean.getRememberMe().equals("true")) {
			cookieUserType.setMaxAge(REMEMBER_ME);
			cookieUserId.setMaxAge(REMEMBER_ME);
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
	
	
	//check if the user has the login cookies if so return to user userBean (userid+userType)
	@GET
	@Path("/check")
	public UserBean checkAutoLogin() {
	
		UserBean loginBean = new UserBean();
		Object tmpUserId = httpServletRequest.getAttribute("userId");
		Object tmpUserType = httpServletRequest.getAttribute("userType");
		if(tmpUserId!=null&&tmpUserType!=null) {
			long userId = ((Long)tmpUserId).longValue();
			UserType userType = ((UserType)tmpUserType);			
			Cookie cookieUserId = new Cookie("userId",String.valueOf(userId));
			Cookie cookieUserTypeCookie = new Cookie("userType",userType.toString());;
			cookieUserId.setMaxAge(REMEMBER_ME);
			cookieUserTypeCookie.setMaxAge(REMEMBER_ME);
			response.addCookie(cookieUserId);
			response.addCookie(cookieUserTypeCookie);
			System.out.println(String.format("LOG : user %s %s auto-logged in",userType, userId));
			loginBean.setUserId(userId);
			loginBean.setUserType(userType);
//			loginBean.setRememberMe("true");
		}else {
			loginBean.setUserId(-1);
			loginBean.setUserType(null);
//			loginBean.setRememberMe("false");
			System.out.println("Login -1");
			
		}
		return loginBean;
	}
}

