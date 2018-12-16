package core.exception;

public enum ExceptionsEnum {
	
	UNAUTHORIZED(600),
	DATA_BASE_ERROR(601),
	DATA_BASE_ACCSESS(602),
	DATA_BASE_TIMOUT(603),
	AUTHENTICATION(604),
	IDGENERATOR_INIT_ERROR(605),
	CONNECTION_POOL_INIT_ERROR(606),
	VALIDATION(607),
	TEST(608),
	NAME_EXISTS(609),
	BAD_NAME_OR_PASSWORD(610),
	USER_TYPE_REQUIRED(611),
	NULL_DATA(612),
	
	FAILED_OPERATION(700),
	CONNECTION_POOL_FAILUER(701),
	ID_EXISTS(702),
	IO_EXCEPTION(703),
	REST_ERROR(704),
	SECURITY_BREACH(705),
	
	DATA_CONFLICTS(800);
	
	
	
	
	
	private final int statusCode;
	private ExceptionsEnum(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getStatusCode() {
		return statusCode;
	}
	

}
