package core.exception;

/**
 * The CouponSystemException and its subclasses are exceptions used throughout the coupon system.
 */
public class CouponSystemException extends Exception {
	private static final long serialVersionUID = 1L;
	ExceptionsEnum exceptionsEnum;
	
	public CouponSystemException(ExceptionsEnum exceptionsEnum, String message, Throwable cause) {
		super(message, cause);
		this.exceptionsEnum = exceptionsEnum;
	}
	public CouponSystemException(ExceptionsEnum exceptionsEnum, String message) {
		super(message);
		this.exceptionsEnum = exceptionsEnum;
	}
	public CouponSystemException(ExceptionsEnum exceptionsEnum) {
		this.exceptionsEnum = exceptionsEnum;
	}
	

	public ExceptionsEnum getExceptionsEnum() {
		return exceptionsEnum;
	}


	public void setExceptionsEnum(ExceptionsEnum exceptionsEnum) {
		this.exceptionsEnum = exceptionsEnum;
	}


	public CouponSystemException() {
		// TODO Auto-generated constructor stub
	}

	public CouponSystemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public CouponSystemException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CouponSystemException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CouponSystemException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	} 
	
}
