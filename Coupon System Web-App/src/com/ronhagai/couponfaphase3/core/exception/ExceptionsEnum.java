package com.ronhagai.couponfaphase3.core.exception;

public enum ExceptionsEnum {
	
	UNAUTHORIZED(600,"Add Internal Message"),
	DATA_BASE_ERROR(601,"Add Internal Message"),
	DATA_BASE_ACCSESS(602,"Add Internal Message"),
	DATA_BASE_TIMOUT(603,"Add Internal Message"),
	AUTHENTICATION(604,"Add Internal Message"),
	IDGENERATOR_INIT_ERROR(605,"Add Internal Message"),
	CONNECTION_POOL_INIT_ERROR(606,"Add Internal Message"),
	VALIDATION(607,"Add Internal Message"),
	TEST(608,"Add Internal Message"),
	NAME_EXISTS(609,"Add Internal Message"),
	BAD_NAME_OR_PASSWORD(610,"Add Internal Message"),
	USER_TYPE_REQUIRED(611,"Add Internal Message"),
	NULL_DATA(612,"Add Internal Message"),
	CUSTOMER_OWNS_COUPON(613,"Add Internal Message"),
	
	FAILED_OPERATION(700,"Add Internal Message"),
	CONNECTION_POOL_FAILUER(701,"Add Internal Message"),
	ID_EXISTS(702,"Add Internal Message"),
	IO_EXCEPTION(703,"Add Internal Message"),
	REST_ERROR(704,"Add Internal Message"),
	SECURITY_BREACH(705,"Add Internal Message"),
	
	DATA_CONFLICTS(800,"Add Internal Message"),
	NO_COOKIES(401,"Add Internal Message");
	
	
	
	
	private final int statusCode;
	private final String internalMessage;
	
	
	public String getInternalMessage() {
		return internalMessage;
	}


	private ExceptionsEnum(int statusCode, String internalMessage) {
		this.statusCode = statusCode;
		this.internalMessage = internalMessage;
	}


	public int getStatusCode() {
		return statusCode;
	}
	

}
