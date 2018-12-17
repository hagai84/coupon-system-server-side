package com.ronhagai.couponfaphase3.core.service;

import java.io.Serializable;

/**
 * Collection of constants for validity checks
 * 
 * @author Hagai
 */
public interface IBeanValidatorConstants extends Serializable{
	public static final int COMP_NAME_LENGTH = 50;
	public static final int COMP_PASSWORD_MAX_LENGTH = 10;
	public static final int COMP_EMAIL_LENGTH = 50;
	public static final int COMP_PASSWORD_MIN_LENGTH = 6;
	public static final int CUST_NAME_LENGTH = 50;
	public static final int CUST_PASSWORD_MAX_LENGTH = 10;
	public static final int CUST_PASSWORD_MIN_LENGTH = 6;
	public static final int COUP_TITLE_LENGTH = 50;
	public static final int COUP_MSG_LENGTH = 250;
	
}
