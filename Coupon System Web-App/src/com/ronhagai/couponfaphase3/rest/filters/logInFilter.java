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

import com.ronhagai.couponfaphase3.core.enums.UserType;

/**
 * filter to check if user is login before he can continue to the jersy
 * contaniner the filter exclude few restcontroller methods like login, get all
 * coupons on site and more.
 * 
 * @author hagai
 *
 */
@WebFilter("/logInFilter")
public class logInFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		Long userId = null;
		UserType userType = null;
		String pathRequestedByUser = httpRequest.getPathInfo();

		Cookie[] userCookies = httpRequest.getCookies();
		// only if user have cookies
		if (userCookies != null) {
			// add to the user id and type (from the cookies)
			// to the request attributes for leter use.
			for (Cookie cookie : userCookies) {
				if (cookie.getName().equals("userId")) {
					userId = Long.valueOf(cookie.getValue());
					httpRequest.setAttribute("userId", userId);
					continue;
				}
				if (cookie.getName().equals("userType")) {
					userType = UserType.valueOf(cookie.getValue());
					httpRequest.setAttribute("userType", userType);
				}
			}
			// check if user have a userId cookie if so then he lodged in an can continue.
			if (userId != null && userType != null) {
				if(pathRequestedByUser.equals("/check")) {
					httpRespone.setContentType(MediaType.APPLICATION_JSON);
					httpRespone.getWriter().print(userId);
					httpRespone.flushBuffer();
					return;
				}
				chain.doFilter(request, response);
				return;
			}
		}
		// if the user try to login/register or to get all coupons let him.
		if (pathRequestedByUser.equals("/login")
				|| (pathRequestedByUser.startsWith("/coupons") && httpRequest.getMethod().equals("GET") && !pathRequestedByUser.startsWith("/coupons/customer"))
				|| (pathRequestedByUser.equals("/customers") && (httpRequest.getMethod().equals("POST")||httpRequest.getMethod().equals("GET"))) 
				|| (pathRequestedByUser.equals("/companies") && (httpRequest.getMethod().equals("POST")||httpRequest.getMethod().equals("GET")))) {
			chain.doFilter(request, response);
			return;
		}
		// if the user try to turn to the rest controller without to be logged in throw
		// exception
		httpRespone.setStatus(HttpServletResponse.SC_FORBIDDEN);
		httpRespone.setContentType(MediaType.APPLICATION_JSON);
		httpRespone.getWriter().print("please sign in first (hagai: no cookies of user id)");
		httpRespone.flushBuffer();
	}
}
