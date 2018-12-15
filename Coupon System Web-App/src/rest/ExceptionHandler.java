package rest;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import core.exception.ClientSideException;
import core.exception.CouponSystemException;

@Provider
public class ExceptionHandler implements ExceptionMapper<CouponSystemException> {
	@Context 
	HttpServletRequest request;
	@Override
	public Response toResponse(CouponSystemException e) {
		ResourceBundle errorMessages;
		int statusCode = e.getExceptionsEnum().getStatusCode();
		String message;
		try {
			errorMessages = ResourceBundle.getBundle("core.exception.errorMessages", request.getLocale());			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			errorMessages = ResourceBundle.getBundle("core.exception.errorMessages_en");
			System.err.println(String.format("%s does not have error support", request.getLocale().getDisplayLanguage()));
		}
		try {
			message = errorMessages.getString(e.getExceptionsEnum().name());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println(String.format("Error code %s is not supported for %s", String.valueOf(statusCode), request.getLocale().getDisplayLanguage()));
			errorMessages = ResourceBundle.getBundle("core.exception.errorMessages_en");
			try {
				message = errorMessages.getString(e.getExceptionsEnum().name());
			} catch (Exception e2) {
				System.err.println(String.format("Error code %s does not have default error message", String.valueOf(statusCode)));
				message = "error message unavailable";
			}
		}
		System.out.println("ExceptionMapper was called :");
		System.out.println("Enum : " + e.getExceptionsEnum());
		System.out.println("Internal message : " + e.getMessage());
		
		ClientSideException clientSide = new ClientSideException(e.getMessage(), message, statusCode);
		if (statusCode >= 600 && statusCode < 700) {
			//TODO appropriate handling, log
		}
		if (statusCode > 700 && statusCode < 800 ) {
			//TODO send email to manager.
		}
		return Response.status(statusCode).entity(clientSide).build();
	}
}
