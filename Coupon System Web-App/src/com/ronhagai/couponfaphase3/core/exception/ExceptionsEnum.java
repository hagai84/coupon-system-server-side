package com.ronhagai.couponfaphase3.core.exception;

public enum ExceptionsEnum {
	
	//THE OPERATION DID NOT RESULT IN ANY CHANGE - POSSIBLE SECURITY BREACH 
	
	/**************************       COMMON ERRORS         **********************************/
	/********   OPERATION ERRORS  **********************************/
	SQL_DML_ZERO(1601,"Add Internal Message"),
	COUPON_NOT_CREATED(1602,"Add Internal Message"),
	COUPON_NOT_PURCHASED(1603,"Add Internal Message"),
	COUPON_NOT_UPDATED(1604,"Add Internal Message"),
	COUPON_NOT_REMOVED(1605,"Add Internal Message"),
	AMOUNT_NOT_UPDATED(1606,"Add Internal Message"),
	GET_COUPON_FAILED(1607,"Add Internal Message"),

	/********   EXTERNAL/DB ERRORS  **********************************/
	
	DATA_CONFLICTS(1701,"Add Internal Message"),
	GENERATE_ID_NOT_RETRIEVED(1702,"Add Internal Message"),
	ROLLBACK_FAILED(1703,"Add Internal Message"),
	DATA_BASE_ERROR(1704,"Add Internal Message"),
	DATA_BASE_ACCSESS(1705,"Add Internal Message"),
	DATA_BASE_TIMOUT(1706,"Add Internal Message"),
	FAILED_OPERATION(1707,"Add Internal Message"),
	CONNECTION_POOL_FAILURE(1708,"Add Internal Message"),
	CONNECTION_POOL_CLOSING(1709,"Add Internal Message"),
	IO_EXCEPTION(1710,"Add Internal Message"),
	
	
	/********   CLIENT ERRORS  **********************************/
	
	AUTHENTICATION(1801,"Add Internal Message"),
	VALIDATION(1802,"Add Internal Message"),
	NAME_EXISTS(1803,"Add Internal Message"),
	BAD_NAME_OR_PASSWORD(1804,"Add Internal Message"),
	USER_TYPE_REQUIRED(1805,"Add Internal Message"),
	USER_NAME_REQUIRED(1806,"Add Internal Message"),
	USER_PASSWORD_REQUIRED(1807,"Add Internal Message"),
	NULL_DATA(1808,"Add Internal Message"),
	CUSTOMER_OWNS_COUPON(1809,"Add Internal Message"),
	ID_EXISTS(1810,"Add Internal Message"),
	NO_COOKIES(1811,"Add Internal Message"),
	REST_ERROR(1812,"Add Internal Message"),
	
	/**************************       SECURITY ERRORS         **********************************/

	UNAUTHORIZED(1901,"Add Internal Message"),
	SECURITY_BREACH(1902,"Add Internal Message"),
	
	
	
	/**************************       CRITICAL ERRORS         **********************************/
	IDGENERATOR_INIT_ERROR(2001,"Add Internal Message"),
	CONNECTION_POOL_INIT_ERROR(2002,"Add Internal Message"),
	TEST(2003,"Add Internal Message"),
	
	;
	
	
	
	
	
	
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
