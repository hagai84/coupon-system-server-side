package com.ronhagai.couponfaphase3.rest.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.ronhagai.couponfaphase3.core.enums.ClientType;


/**
 * Servlet Filter implementation class logInFilter
 */
@WebFilter("/logInFilter")
public class logInFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("login filter is on");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		Long userId = null;
		ClientType userType = null;
		String pathRequstedByUser = httpRequest.getPathInfo();
		// if try to login let him
		if (pathRequstedByUser.equals(
				"/login") ||
				(pathRequstedByUser.equals("/coupons") && httpRequest.getMethod().equals("GET"))||
				pathRequstedByUser.equals("/customers") && httpRequest.getMethod().equals("POST")) {
			System.out.println("LOGIN FILTER: user try to login or get all coupons");
			chain.doFilter(request, response);
			return;
		}
		Cookie[] userCookies = httpRequest.getCookies();
		// if user have a userId cookie then he loged in an can countunue.
				if (userCookies != null) {
			for (Cookie cookie : userCookies) {
				if (cookie.getName().equals("userId")) {
					System.out.println("LOGIN FILTER: the user have cookieID");
					userId = Long.valueOf(cookie.getValue());
					httpRequest.setAttribute("userId", userId);
				}
				if (cookie.getName().equals("userType")) {
					userType = ClientType.valueOf(cookie.getValue());
					httpRequest.setAttribute("userType", userType);
				}
			}
			if (userId != null && userType != null) {
				chain.doFilter(request, response);
				return;
			}
		}
		
		System.out.println("LOGIN FILTER: the user dont have id cookie and is not try to log in");
		httpRespone.setStatus(HttpServletResponse.SC_FORBIDDEN);
		httpRespone.setContentType(MediaType.APPLICATION_JSON);
		httpRespone.getWriter().print("please sign in first (hagai: no cookies of user id)");
		httpRespone.flushBuffer();
	}
}