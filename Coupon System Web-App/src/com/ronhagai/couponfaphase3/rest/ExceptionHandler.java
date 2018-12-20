package com.ronhagai.couponfaphase3.rest;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ronhagai.couponfaphase3.core.beans.ExceptionBean;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.exception.ExceptionsEnum;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
	@Context
	HttpServletRequest request;

	@Override
	public Response toResponse(Throwable exception) {
		
		if (exception instanceof CouponSystemException) {
			CouponSystemException theException = (CouponSystemException)exception;
			ResourceBundle errorMessages;
			ExceptionsEnum exceptionsEnum = theException.getExceptionsEnum();
			int statusCode = exceptionsEnum.getStatusCode();
			String externalMessage;
			try {
				errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", request.getLocale());			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.err.println(String.format("%s does not have error support", request.getLocale().getDisplayLanguage()));
				errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages_en");
			}
			try {
				externalMessage = errorMessages.getString(theException.getExceptionsEnum().name());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.err.println(String.format("Error code %s is not supported for %s", String.valueOf(statusCode), request.getLocale().getDisplayLanguage()));
				errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages_en");
				try {
					externalMessage = errorMessages.getString(theException.getExceptionsEnum().name());
				} catch (Exception e2) {
					System.err.println(String.format("Error code %s does not have default error message", String.valueOf(statusCode)));
					externalMessage = "error message unavailable";
				}
			}
			
			ExceptionBean exceptionBean = new ExceptionBean(statusCode, externalMessage, exceptionsEnum.getInternalMessage());
			if (statusCode >= 600 && statusCode < 700) {
				// TODO appropriate handling, log
				return Response.status(statusCode).entity(exceptionBean).build();
			}
			if (statusCode > 700 && statusCode < 800) {
				// TODO send email to manager +log
				return Response.status(statusCode).entity(exceptionBean).build();
			}
			if (statusCode > 700 && statusCode < 800) {
				// TODO send email to manager +log
			}
			return Response.status(statusCode).entity(exceptionBean).build();
		}
		
		System.err.println("exception massge form uncatch exceptions from the hendler: " + exception);
		return Response.status(999).entity("Something went wrong").build();
		
	}

}
