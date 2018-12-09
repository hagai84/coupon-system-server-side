package core.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.xml.internal.bind.v2.TODO;

@Provider
public class ExceptionHandler implements ExceptionMapper<CouponSystemException> {
	@Override
	public Response toResponse(CouponSystemException e) {
		System.out.println("ExceptionMapper was cold");

		if (e.getExceptionsEnum().getStatusCode() == 510) {
			return Response.status(e.getExceptionsEnum().getStatusCode()).entity(e.getMessage()).build();
		}
		if (e.getExceptionsEnum().getStatusCode() == 511) {
			//TODO send email to manager.
			return Response.status(e.getExceptionsEnum().getStatusCode()).entity(e.getMessage()).build();
		}
		return Response.status(512).entity("Activation failed").build();
	}
}
