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
		ExceptionBean exceptionBean;
		ExceptionsEnum exceptionEnum;
		String externalMessage;
		String internalMessage;
		int statusCode;
		
		
		if (exception instanceof CouponSystemException) {
			CouponSystemException theException = (CouponSystemException)exception;
			exceptionEnum = theException.getExceptionsEnum();
			statusCode = exceptionEnum.getStatusCode();
			internalMessage = exceptionEnum.getInternalMessage();
			
			if (statusCode >= 1600 && statusCode < 1700) {
				// TODO appropriate handling, log
			}
			if (statusCode >= 1700 && statusCode < 1800) {
				// TODO send email to manager +log
			}
			if (statusCode >= 1800) {
				// TODO send email to manager +log
			}
		}else {
			//TODO Log uncaught exception
			System.err.println("Different exception than CouponSystemException: ");
			exceptionEnum = ExceptionsEnum.FAILED_OPERATION;
			statusCode = exceptionEnum.getStatusCode();
			internalMessage = exceptionEnum.getInternalMessage();
		}
//		TODO proper logging
//		exception.printStackTrace();
		externalMessage = getTranslatedMessage(exceptionEnum);
		exceptionBean = new ExceptionBean(statusCode, externalMessage, internalMessage);
		return Response.status(statusCode).entity(exceptionBean).build();	
	}
	
	private String getTranslatedMessage(ExceptionsEnum exceptionEnum) {
		ResourceBundle errorMessages;
		try {
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", request.getLocale());			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println(String.format("%s does not have error support", request.getLocale().getDisplayLanguage()));
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages_en");
		}
		try {
			return errorMessages.getString(exceptionEnum.name());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println(String.format("Error code %s is not supported for %s", String.valueOf(exceptionEnum.getStatusCode()), request.getLocale().getDisplayLanguage()));
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages_en");
			try {
				return errorMessages.getString(exceptionEnum.name());
			} catch (Exception e2) {
				System.err.println(String.format("Error code %s does not have default error message", String.valueOf(exceptionEnum.getStatusCode())));
				return "error message unavailable";
			}
		}
	}
}
