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

}
