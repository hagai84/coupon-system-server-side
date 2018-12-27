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
		httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
		httpResponse.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, OPTIONS, HEAD");
		
		chain.doFilter(request, response);

	}
}
