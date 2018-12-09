package core.exception;

public enum ExceptionsEnum {
	ID_NOT_EXIST(510),
	NOT_AOTURIZED(511),;
	
	
	private final int statusCode;
	private ExceptionsEnum(int number) {
		this.statusCode = number;
	}
	public int getNumber() {
		return statusCode;
	}
	

}
