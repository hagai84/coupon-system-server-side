package core.exception;

public enum ExceptionsEnum {
	UNAUTHORIZED(601),
	DATA_BASE_ERROR(602),
	DATA_BASE_TIMOUT(603),
	
	
	private final int statusCode;
	private ExceptionsEnum(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getStatusCode() {
		return statusCode;
	}
	

}
