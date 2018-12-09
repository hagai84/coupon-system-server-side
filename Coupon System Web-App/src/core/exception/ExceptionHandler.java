package core.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<CouponSystemException> {
	@Override
	public Response toResponse(CouponSystemException e) {
		System.out.println("ExceptionMapper was cold");

		if (e.getExceptionsEnum().getStatusCode() > 600 && e.getExceptionsEnum().getStatusCode() < 700) {
			return Response.status(e.getExceptionsEnum().getStatusCode()).entity(e.getMessage()).build();
		}
		if (e.getExceptionsEnum().getStatusCode() > 700 && e.getExceptionsEnum().getStatusCode() < 800 ) {
			//TODO send email to manager.
			return Response.status(e.getExceptionsEnum().getStatusCode()).entity(e.getMessage()).build();
		}
		return Response.status(800).entity("Action failed").build();
	}
}
