package rest;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import core.exception.CouponSystemException;

@Provider
public class ExceptionHandler implements ExceptionMapper<CouponSystemException> {
	@Context 
	HttpServletRequest request;
	@Override
	public Response toResponse(CouponSystemException e) {
		try {
			System.out.println(request.getLocale());
			ResourceBundle errorMessages = ResourceBundle.getBundle("core.exception.errorMessages", request.getLocale());
			int statusCode = e.getExceptionsEnum().getStatusCode();
			String message = errorMessages.getString(e.getExceptionsEnum().name());
			
			System.out.println("ExceptionMapper was called :");
			System.out.println("Enum : " + e.getExceptionsEnum());
			System.out.println("Internal message : " + e.getMessage());

			if (statusCode >= 600 && statusCode < 700) {
				return Response.status(statusCode).entity(message).build();
			}
			if (statusCode > 700 && statusCode < 800 ) {
				//TODO send email to manager.
				return Response.status(statusCode).entity(message).build();
			}
			return Response.status(800).entity("Action failed").build();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return Response.status(900).entity("Language not supported").build();
		}
	}
}
