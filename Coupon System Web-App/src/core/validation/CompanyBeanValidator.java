package core.validation;

import java.util.regex.Pattern;

import core.beans.CompanyBean;
import core.exception.CouponSystemException;

/**
 * Utility class used to check validity (length and/or format) of Company's String properties (checkCompany calls all other methods in utility class)
 * @author Hagai
 */
public class CompanyBeanValidator implements IBeanValidatorConstants{

	private static final long serialVersionUID = 1L;

	/**
	 * Private constructor
	 */
	private CompanyBeanValidator() {
	}

	public static void checkCompany(CompanyBean company) throws CouponSystemException {

		// check if company name is not too long
		if (!checkCompanyName(company.getCompName())) {
			CouponSystemException e = new CouponSystemException(
					"The company name cant be more than " + COMP_NAME_LENGTH + " characters");
			throw e;
		}

		// check if company password is not too long
		if (!checkCompanyPassword(company.getPassword())) {
			CouponSystemException e = new CouponSystemException(
					"The company password cant be more than " + COMP_PASSWORD_LENGTH + " characters");
			throw e;
		}
		// check if company password is not too short
		if (!checkCompanyPasswordNotShort(company.getPassword())) {
			CouponSystemException e = new CouponSystemException(
					"The company password need to be more than " + COMP_PASSWORD_MIN_LENGTH + " characters");
			throw e;
		}

		// check if company email is not too long
		if (!checkCompanyEmail(company.getEmail())) {
			CouponSystemException e = new CouponSystemException(
					"The company email cant be more than" + COMP_PASSWORD_LENGTH + "leters");
			throw e;
		}
		// check if company email is valid
		if (!isValidEmailAddress(company.getEmail())) {
			CouponSystemException e = new CouponSystemException("The company email is not valid");
			throw e;
		}

	}

	public static boolean checkCompanyPasswordNotShort(String password) {
		if (password.length() < COMP_PASSWORD_MIN_LENGTH) {
			return false;
		}
		return true;
	}

	public static boolean checkCompanyName(String compName) {
		if (compName.length() > COMP_NAME_LENGTH) {
			return false;
		}
		return true;
	}

	public static boolean checkCompanyPassword(String compPass) {
		if (compPass.length() > COMP_PASSWORD_LENGTH) {
			return false;
		}
		return true;
	}

	public static boolean checkCompanyEmail(String compEmail) {
		if (compEmail.length() > COMP_EMAIL_LENGTH) {
			return false;
		}
		return true;
	}

	public static boolean isValidEmailAddress(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}
}
