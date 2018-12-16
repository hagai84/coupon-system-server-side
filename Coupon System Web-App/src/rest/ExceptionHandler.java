package rest;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import core.beans.ExceptionBean;
import core.exception.CouponSystemException;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
	HttpServletRequest request;

	@Override
	public Response toResponse(Throwable exception) {
		
		if (exception instanceof CouponSystemException) {
			CouponSystemException theException = (CouponSystemException)exception;
			int statusCode = theException.getExceptionsEnum().getStatusCode();

			ResourceBundle errorMessages;
			String externalMessage;
			try {
				errorMessages = ResourceBundle.getBundle("core.exception.errorMessages", request.getLocale());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				errorMessages = ResourceBundle.getBundle("core.exception.errorMessages_en");
				System.err.println(
						String.format("%s does not have error support", request.getLocale().getDisplayLanguage()));
			}
			try {
				externalMessage = errorMessages.getString(theException.getExceptionsEnum().name());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.err.println(String.format("Error code %s is not supported for %s", String.valueOf(statusCode),
						request.getLocale().getDisplayLanguage()));
				errorMessages = ResourceBundle.getBundle("core.exception.errorMessages_en");
				try {
					externalMessage = errorMessages.getString(theException.getExceptionsEnum().name());
				} catch (Exception e2) {
					System.err.println(String.format("Error code %s does not have default error message",
							String.valueOf(statusCode)));
					externalMessage = "error message unavailable";
				}
			}
			System.out.println("ExceptionMapper was called :");
			System.out.println("Enum : " + theException.getExceptionsEnum());
			System.out.println("Internal message : " + theException.getMessage());

			ExceptionBean exceptionBean = new ExceptionBean(statusCode, externalMessage, theException.getInternalMessage());
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
		return Response.status(500).entity("Something went wrong").build();
		
	}

}
