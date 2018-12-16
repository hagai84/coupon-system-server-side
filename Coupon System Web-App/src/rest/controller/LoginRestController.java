package rest.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import core.beans.CompanyBean;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;
import core.service.CompanyService;
import core.service.CustomerService;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginRestController {
	@Context
	HttpServletResponse response;

	@GET
	public long login(@QueryParam("userName") String userName, @QueryParam("userPassword") String userPassword,
			@QueryParam("userType") String userType, @QueryParam("remeberMe") Boolean remeberMe)
			throws CouponSystemException {
		System.out.println("login service was cold");
		long userId;
		Cookie cookieUserId;

		if (userType == "cutomer") {
			userId = CustomerService.getInstance().customerLogin(userName, userPassword);
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else if (userType == "company") {
			userId = CompanyService.getInstance().companyLogin(userName, userPassword);
			cookieUserId = new Cookie("userId", String.valueOf(userId));
		} else if (userName == "admin" && userPassword == "1234") {
			cookieUserId = new Cookie("userId", "-1");
			userType = "admin";
			userId = -1;
		} else {
			throw new CouponSystemException(ExceptionsEnum.USER_TYPE_REQUIRED, "user type seem to be missing");
		}

		if (remeberMe != null && remeberMe == true) {
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
