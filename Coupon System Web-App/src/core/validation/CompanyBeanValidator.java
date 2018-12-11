package core.validation;

import java.util.regex.Pattern;

import core.beans.CompanyBean;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;

/**
 * Utility class used to check validity (length and/or format) of Company's
 * String properties (checkCompany calls all other methods in utility class)
 * 
 * @author Hagai
 */
public class CompanyBeanValidator implements IBeanValidatorConstants {

	private static final long serialVersionUID = 1L;

	/**
	 * Private constructor
	 */
	private CompanyBeanValidator() {
	}

	public static void checkCompany(CompanyBean company) throws CouponSystemException {
		checkCompanyName(company.getCompName());
		checkCompanyPassword(company.getPassword());
		checkCompanyEmail(company.getEmail());
	}

	public static void checkCompanyName(String compName) throws CouponSystemException {
		if (compName.length() > COMP_NAME_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company name cant be more than " + COMP_NAME_LENGTH + " characters");
			throw e;
		}
	}

	public static void checkCompanyPassword(String compPassword) throws CouponSystemException {
		if (compPassword.length() > COMP_PASSWORD_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password cant be more than " + COMP_PASSWORD_LENGTH + " characters");
			throw e;
		}
		if (compPassword.length() < COMP_PASSWORD_MIN_LENGTH) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company password need to be more than " + COMP_PASSWORD_MIN_LENGTH + " characters");
			throw e;
		}
	}

	public static void checkCompanyEmail(String companyEmail) throws CouponSystemException {
		if (companyEmail == null) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be null");
			throw e;
		}

		if (companyEmail.length() > COMP_EMAIL_LENGTH){
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email cant be more than" + COMP_PASSWORD_LENGTH + "leters");
			throw e;
		}

		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (!pat.matcher(companyEmail).matches()) {
			CouponSystemException e = new CouponSystemException(ExceptionsEnum.VALIDATION,
					"The company email is not valid");
			throw e;
		}
	}
}
