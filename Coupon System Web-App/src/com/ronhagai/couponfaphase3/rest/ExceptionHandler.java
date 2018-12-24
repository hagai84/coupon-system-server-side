package com.ronhagai.couponfaphase3.rest;

import java.util.Locale;
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
				// OPERATIONS THAT SHLDNT NORMALLY FAIL
			}
			if (statusCode >= 1700 && statusCode < 1800) {
				// TODO appropriate handling, log
				// EXTERNAL FACTORS SHLDNT BE MORE THAN OCCASIONALY DEPENDING ON OTHERS' STABILLITY
			}
			if (statusCode >= 1800 && statusCode < 1900) {
				// CLIENT SIDE ERRORS WITH HIGHER OCCURENCES 
			}
			if (statusCode >= 1900 && statusCode < 2000) {
				// TODO send email to manager +log
				// SECURITY ERRORS HIGH CHANCES OF BREACH ATTEMPTS
				// HIDES THE TRUE CAUSE
				exceptionEnum = ExceptionsEnum.DATA_BASE_ERROR;
				statusCode = exceptionEnum.getStatusCode();
				internalMessage = exceptionEnum.getInternalMessage();
			}
			if (statusCode >= 2000) {
				// CRITICAL ERRORS NOTIFY IMMIDIATLY
				exception.printStackTrace();
			}
		}else {
			//TODO Log uncaught exception
			System.err.println("Different exception than CouponSystemException: ");
			exception.printStackTrace();
			exceptionEnum = ExceptionsEnum.FAILED_OPERATION;
			statusCode = exceptionEnum.getStatusCode();
			internalMessage = exceptionEnum.getInternalMessage();
		}
		externalMessage = getTranslatedMessage(exceptionEnum);
//		externalMessage = exception.getMessage();//TODO only for developing purposes 
		exceptionBean = new ExceptionBean(statusCode, externalMessage, internalMessage);
		return Response.status(statusCode).entity(exceptionBean).build();	
	}
	
	private String getTranslatedMessage(ExceptionsEnum exceptionEnum) {
		ResourceBundle errorMessages;
		Locale locale = request.getLocale();
		try {
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", locale);			
		} catch (Exception e1) {
			// TODO LOG IN TRANSLATION LOG
			System.err.println(String.format("%s does not have error language support", request.getLocale().getDisplayLanguage()));
			locale = new Locale("en");
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", locale);			
		}
		try {
			return errorMessages.getString(exceptionEnum.name());
		} catch (Exception e1) {
			// TODO LOG IN TRANSLATION LOG
			System.err.println(String.format("Error code %s does not have support for %s", String.valueOf(exceptionEnum.getStatusCode()), locale.getDisplayLanguage()));
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages_en");
			try {
				return errorMessages.getString(exceptionEnum.name());
			} catch (Exception e2) {
				// TODO LOG IN TRANSLATION LOG
				System.err.println(String.format("Error code %s does not have a default error message", String.valueOf(exceptionEnum.getStatusCode())));
				return "error message unavailable";
			}
		}
	}
}
