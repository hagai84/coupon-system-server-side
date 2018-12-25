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


/**
 * A filter that set the header properties for cors platform accsess.
 * @author hagai
 *
 */
@WebFilter("/CorsFilter")
public class CorsFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		System.out.println("the user url is: " + httpRequest.getRequestURL());
		httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
		httpResponse.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, OPTIONS, HEAD");
		
		Cookie[] cookies = httpRequest.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				System.out.println("user cookie name: " + cookie.getName() + " value: " + cookie.getValue());
			}
		} else {
			System.out.println("Cors Filter: no cookies for users");
		}

		if (httpRequest.getMethod().equals("OPTIONS")) {
			System.out.println("the option method happend");
			httpResponse.setStatus(200);
			return;
		}
		
		chain.doFilter(request, response);

	}
}
