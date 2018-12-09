package core.exception;

public enum ExceptionsEnum {
	CANT_CREATE_COMPANY(510),
	CANT_UPDATE_COMPANY(510),
	CANT_REMOVE_COMPANY(510),
	CANT_GET_COMPANY(510),
	CANT_GET_ALL_COMPANIES(510),
	CANT_LOG_IN(510);
	
	
	private final int statusCode;
	private ExceptionsEnum(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getStatusCode() {
		return statusCode;
	}
	

}
