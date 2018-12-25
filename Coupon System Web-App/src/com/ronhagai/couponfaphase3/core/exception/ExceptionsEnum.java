package com.ronhagai.couponfaphase3.core.exception;
/**
 * Enum for deferment type of exceptions
 * @author hagai
 */
public enum ExceptionsEnum {
	
	//THE OPERATION DID NOT RESULT IN ANY CHANGE - POSSIBLE SECURITY BREACH 
	
	/**************************       COMMON ERRORS         **********************************/
	/********   OPERATION ERRORS  **********************************/
	SQL_DML_ZERO(1601,"Add Client Side Developer Message"),
	COUPON_NOT_CREATED(1602,"Add Client Side Developer Message"),
	COUPON_NOT_PURCHASED(1603,"Add Client Side Developer Message"),
	COUPON_NOT_UPDATED(1604,"Add Client Side Developer Message"),
	COUPON_NOT_REMOVED(1605,"Add Client Side Developer Message"),
	AMOUNT_NOT_UPDATED(1606,"Add Client Side Developer Message"),
	GET_COUPON_FAILED(1607,"Add Client Side Developer Message"),

	/********   EXTERNAL/DB ERRORS  **********************************/
	
	DATA_CONFLICTS(1701,"Add Client Side Developer Message"),
	GENERATE_ID_NOT_RETRIEVED(1702,"Add Client Side Developer Message"),
	ROLLBACK_FAILED(1703,"Add Client Side Developer Message"),
	DATA_BASE_ERROR(1704,"Add Client Side Developer Message"),
	DATA_BASE_ACCSESS(1705,"Add Client Side Developer Message"),
	DATA_BASE_TIMOUT(1706,"Add Client Side Developer Message"),
	FAILED_OPERATION(1707,"Add Client Side Developer Message"),
	CONNECTION_POOL_FAILURE(1708,"Add Client Side Developer Message"),
	CONNECTION_POOL_CLOSING(1709,"Add Client Side Developer Message"),
	IO_EXCEPTION(1710,"Add Client Side Developer Message"),
	
	
	/********   CLIENT ERRORS  **********************************/
	
	AUTHENTICATION(1801,"Add Client Side Developer Message"),
	VALIDATION(1802,"Add Client Side Developer Message"),
	NAME_EXISTS(1803,"Add Client Side Developer Message"),
	BAD_NAME_OR_PASSWORD(1804,"Add Client Side Developer Message"),
	USER_TYPE_REQUIRED(1805,"Add Client Side Developer Message"),
	USER_NAME_REQUIRED(1806,"Add Client Side Developer Message"),
	USER_PASSWORD_REQUIRED(1807,"Add Client Side Developer Message"),
	NULL_DATA(1808,"Add Client Side Developer Message"),
	CUSTOMER_OWNS_COUPON(1809,"Add Client Side Developer Message"),
	ID_EXISTS(1810,"Add Client Side Developer Message"),
	NO_COOKIES(1811,"Add Client Side Developer Message"),
	REST_ERROR(1812,"Add Client Side Developer Message"),
	
	/**************************       SECURITY ERRORS         **********************************/

	UNAUTHORIZED(1901,"Add Client Side Developer Message"),
	SECURITY_BREACH(1902,"Add Client Side Developer Message"),
	
	
	
	/**************************       CRITICAL ERRORS         **********************************/
	IDGENERATOR_INIT_ERROR(2001,"Add Client Side Developer Message"),
	CONNECTION_POOL_INIT_ERROR(2002,"Add Client Side Developer Message"),
	TEST(2003,"Add Client Side Developer Message");
	
	
	
	
	
	
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
