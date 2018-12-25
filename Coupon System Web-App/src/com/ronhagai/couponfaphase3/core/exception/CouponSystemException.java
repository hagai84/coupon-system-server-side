package com.ronhagai.couponfaphase3.core.exception;

/**
 * The CouponSystemException exception used throughout the coupon system.
 * @author hagai
 */
public class CouponSystemException extends Exception {
	private static final long serialVersionUID = 1L;
	ExceptionsEnum exceptionsEnum;
	
	public CouponSystemException(ExceptionsEnum exceptionsEnum, String developperMessage, Throwable cause) {
		super(developperMessage, cause);
		this.exceptionsEnum = exceptionsEnum;
	}
	public CouponSystemException(ExceptionsEnum exceptionsEnum, String developperMessage) {
		super(developperMessage);
		this.exceptionsEnum = exceptionsEnum;
	}
	public CouponSystemException(ExceptionsEnum exceptionsEnum) {
		this.exceptionsEnum = exceptionsEnum;
	}

	public ExceptionsEnum getExceptionsEnum() {
		return exceptionsEnum;
	}
	public String get() {
		return super.getMessage();
	}

	
}
