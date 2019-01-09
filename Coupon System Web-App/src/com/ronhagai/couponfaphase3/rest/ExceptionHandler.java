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

/**
 * Exception handler for all the Coupon System application exceptions
 * @author hagai
 *
 */
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
//		return Response.status(900).entity(exception).build();	
//	}
		//all the known exceptions hendeld by type
		if (exception instanceof CouponSystemException) {
			CouponSystemException theException = (CouponSystemException)exception;
			//get the data from the exception.
			exceptionEnum = theException.getExceptionsEnum();
			statusCode = exceptionEnum.getStatusCode();
			internalMessage = exceptionEnum.getInternalMessage();
			
			//OPERATION ERRORS
			// OPERATIONS THAT SHLDNT NORMALLY FAIL
			//dml equals 0
			if (statusCode >= 1600 && statusCode < 1700) {
			// TODO appropriate handling, log
			}
			//EXTERNAL/DB ERRORS
			// EXTERNAL FACTORS SHLDNT BE MORE THAN OCCASIONALY DEPENDING ON OTHERS' STABILLITY
			if (statusCode >= 1700 && statusCode < 1800) {
				// TODO appropriate handling, log 
			}
			// CLIENT SIDE ERRORS WITH HIGHER OCCURENCES 
			if (statusCode >= 1800 && statusCode < 1900) {
				
			}
				// SECURITY ERRORS HIGH CHANCES OF BREACH ATTEMPTS
				// HIDES THE TRUE CAUSE
			if (statusCode >= 1900 && statusCode < 2000) {
				System.err.println("LOG : " + exception.getMessage());
//				exception.pr
				exceptionEnum = ExceptionsEnum.DATA_BASE_ERROR;
				statusCode = exceptionEnum.getStatusCode();
				internalMessage = exceptionEnum.getInternalMessage();
			}
			// CRITICAL ERRORS NOTIFY IMMIDIATLY
			if (statusCode >= 2000) {
				// TODO send email to manager + log
				exception.printStackTrace();
			}
		}else {
			//handle the uncaught exceptions by sending general message to the clint and print to log
			//TODO Log uncaught exceptions
			System.err.println("Different exception than CouponSystemException: ");
			exception.printStackTrace();
			exceptionEnum = ExceptionsEnum.FAILED_OPERATION;
			statusCode = exceptionEnum.getStatusCode();
			internalMessage = exceptionEnum.getInternalMessage();
		}
		///set the message to user and for client developer
		externalMessage = getTranslatedMessage(exceptionEnum);
		//TODO only for developing purposes!!!! 
		externalMessage = exception.getMessage();
		exceptionBean = new ExceptionBean(statusCode, externalMessage, internalMessage);
		return Response.status(statusCode).entity(exceptionBean).build();	
	}
	
	//set the message that the user will see to his language 
	private String getTranslatedMessage(ExceptionsEnum exceptionEnum) {
		ResourceBundle errorMessages;
		//get the user language 
		Locale locale = request.getLocale();
		try {
			//get the right translated file for user language 
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", locale);			
		} catch (Exception e1) {
			// TODO LOG IN TRANSLATION LOG
			//if the requested language isn't supported, sets default to English 
			System.err.println(String.format("%s does not have error language support", request.getLocale().getDisplayLanguage()));
			locale = new Locale("en");
			errorMessages = ResourceBundle.getBundle("com.ronhagai.couponfaphase3.core.exception.errorMessages", locale);			
		}
		try {
			//return the translate message
			return errorMessages.getString(exceptionEnum.name());
		} catch (Exception e1) {
			// TODO LOG IN TRANSLATION LOG
			//if a translated message dosent  exist return as difolt the english one
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
