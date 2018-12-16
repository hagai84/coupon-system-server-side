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
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Context;

/**
 * Servlet Filter implementation class logInFilter
 */
@WebFilter("/logInFilter")
public class logInFilter implements Filter {
	

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		String userId = null;
		String pathRequstedByUser = httpRequest.getPathTranslated();
		
		
		Cookie [] userCookies =  httpRequest.getCookies();
		for (Cookie cookie : userCookies) {
			if (cookie.getName() == "userId") {
				userId = cookie.getValue();
			}
		}
		
		if (userId != null || pathRequstedByUser == "login") {
			httpRequest.setAttribute("userId",userId);
			chain.doFilter(request, response);
			return;
		}
		
		httpRespone.setStatus(401);
		
	}

}
