package rest.filters;

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
import javax.ws.rs.core.Response;

import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;

/**
 * Servlet Filter implementation class logInFilter
 */
@WebFilter("/logInFilter")
public class logInFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		Long userId = null;
		String pathRequstedByUser = httpRequest.getPathInfo();
		
		// if try to login let him
		if (pathRequstedByUser.equals("/login")) {

			chain.doFilter(request, response);
			return;
		}
		Cookie[] userCookies = httpRequest.getCookies();
		// if user have a userId cookie then he loged in an can countunue.
		if (userCookies != null) {
			for (Cookie cookie : userCookies) {
				if (cookie.getName().equals("userId")) {
					userId = Long.valueOf(cookie.getValue());
					httpRequest.setAttribute("userId", userId);
					chain.doFilter(request, response);
					return;
				}
			}
		}
		httpRespone.setStatus(HttpServletResponse.SC_FORBIDDEN);
		httpRespone.setContentType(MediaType.APPLICATION_JSON);
		httpRespone.getWriter().print("There are no cookies");
		httpRespone.flushBuffer();
	}
}
