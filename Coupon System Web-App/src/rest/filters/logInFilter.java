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

/**
 * Servlet Filter implementation class logInFilter
 */
@WebFilter("/logInFilter")
public class logInFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("log in filter was run");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		String userId = null;
		String pathRequstedByUser = httpRequest.getPathInfo();
		System.out.println("the path is: " + pathRequstedByUser);
		
		Cookie[] userCookies = httpRequest.getCookies();
		// if user have a userId cookie then he loged in an can countunue.
		if (userCookies != null) {
			System.out.println("log in filter found some cookies");
			for (Cookie cookie : userCookies) {
				if (cookie.getName() == "userId") {
					System.out.println("log in filter found userId cookie");
					userId = cookie.getValue();
					httpRequest.setAttribute("userId", userId);
					chain.doFilter(request, response);
					return;
				}
			}
		}
		// if try to login let him
		if (pathRequstedByUser.equals("/login")) {
			System.out.println("login filter: login path");
			httpRequest.setAttribute("userId", userId);
			chain.doFilter(request, response);
			return;
		}
		// if user not login and not try to login throw exception
		httpRespone.setStatus(401);
	}
}
