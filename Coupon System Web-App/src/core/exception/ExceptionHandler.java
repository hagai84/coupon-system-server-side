package core.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<CouponSystemException> {
	@Override
	public Response toResponse(CouponSystemException e) {
		System.out.println("ExceptionMapper was cold");
		return Response.status(e.getExceptionsEnum().getNumber()).entity(e.getCause()).build();
	}
}
