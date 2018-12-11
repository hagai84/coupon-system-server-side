package core.validation;

import core.beans.CustomerBean;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;

/**
 * Utility class used to check validity (length and/or format) of Customer's String properties (checkCustomer calls all other methods in utility class)
 * @author Yair
 *
 */
public class CustomerBeanValidator implements IBeanValidatorConstants{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Private constructor
	 */
	private CustomerBeanValidator() {
		// TODO Auto-generated constructor stub
	}

	public static void checkCustomer(CustomerBean customer) throws CouponSystemException {

		// check if customer name is not too long
		if (!checkCustomerName(customer.getCustName())) {
			CouponSystemException e = new CouponSystemException(
				ExceptionsEnum.VALIDATION,"The customer name cant be more than " + CUST_NAME_LENGTH + " characters");
			throw e;
		}

		// check if customer password is not too long
		if (!checkCustomerPassword(customer.getPassword())) {
			CouponSystemException e = new CouponSystemException(
					ExceptionsEnum.VALIDATION,"The customer password cant be more than " + CUST_PASSWORD_LENGTH + " characters");
			throw e;
		}
		// check if customer password is not too short
		if (!checkCustomerPasswordNotShort(customer.getPassword())) {
			CouponSystemException e = new CouponSystemException(
					ExceptionsEnum.VALIDATION,"The customer password need to be more than " + CUST_PASSWORD_MIN_LENGTH + " characters");
			throw e;
		}

	}

	private static boolean checkCustomerPasswordNotShort(String password) {
		if (password.length() < CUST_PASSWORD_MIN_LENGTH) {
			return false;
		}
		return true;
	}

	private static boolean checkCustomerName(String custName) {
		if (custName.length() > CUST_NAME_LENGTH) {
			return false;
		}
		return true;
	}

	private static boolean checkCustomerPassword(String password) {
		if (password.length() > CUST_PASSWORD_LENGTH) {
			return false;
		}
		return true;
	}



}
