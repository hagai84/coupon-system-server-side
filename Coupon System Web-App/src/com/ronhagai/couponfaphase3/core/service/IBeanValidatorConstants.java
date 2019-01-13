package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;

/**
 * Collection of constants for validity checks
 * 
 * @author Hagai
 */
public interface IBeanValidatorConstants extends Serializable{
	public static final int COMPANY_NAME_LENGTH = 50;
	public static final int COMPANY_PASSWORD_MAX_LENGTH = 10;
	public static final int COMPANY_EMAIL_LENGTH = 50;
	public static final int COMPANY_PASSWORD_MIN_LENGTH = 6;
	public static final int CUSTOMER_NAME_LENGTH = 50;
	public static final int CUSTOMER_PASSWORD_MAX_LENGTH = 10;
	public static final int CUSTOMER_PASSWORD_MIN_LENGTH = 6;
	public static final int COUPOMER_TITLE_LENGTH = 50;
	public static final int COUPON_MSG_LENGTH = 250;
	
}
