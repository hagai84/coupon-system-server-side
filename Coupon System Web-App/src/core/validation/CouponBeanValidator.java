package core.validation;

import java.sql.Date;

import core.beans.CouponBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;
import core.exception.ExceptionsEnum;

/**
 * Utility class used to check validity (length and/or format) of Coupon's String properties (checkCoupon calls all other methods in utility class)
 * @author Ron
 *
 */
public class CouponBeanValidator implements IBeanValidatorConstants{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Private constructor
	 */
	private CouponBeanValidator() {
		// TODO Auto-generated constructor stub
	}

	public static void checkCoupon(CouponBean coupon) throws CouponSystemException {
		if (!checkTitle(coupon.getTitle()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's title can't be longer than " + COUP_TITLE_LENGTH + " characters");
		if (!checkStartDate(coupon.getStartDate()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's start date can't be earlier than today");
		if (!checkEndDate(coupon.getEndDate()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's expiration date can't be earlier than today");
		if (!checkAmount(coupon.getAmount()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's amount can't be negative");
		if (!checkType(coupon.getType()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon title cant be more than " + COUP_TITLE_LENGTH + " characters");
		if (!checkPrice(coupon.getPrice()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's price can't be negative");
		if (!checkImage(coupon.getImage()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon image cant be ... ");//No restrictions at the momment
		if (!checkMessage(coupon.getMessage()))
			throw new CouponSystemException(ExceptionsEnum.VALIDATION,"Coupon's message can't be longer than " + COUP_MSG_LENGTH + " characters");		
		
	}

	/*public static boolean checkId(long id) {
		return true;
	}*/

	public static boolean checkTitle(String title) {
		if(title.length()>COUP_TITLE_LENGTH)
			return false;
		return true;
	}

	public static boolean checkStartDate(Date startDate) {
		if(startDate.before(new java.util.Date(System.currentTimeMillis())))
			return false;
		return true;
	}

	public static boolean checkEndDate(Date endDate) {
		if(endDate.before(new java.util.Date(System.currentTimeMillis())))
			return false;
		return true;
	}

	public static boolean checkAmount(int amount) {
		if(amount<0)
			return false;
		return true;
	}

	public static boolean checkType(CouponType type) {
		if(type == null)
			return false;
		return true;
	}

	public static boolean checkPrice(double price) {
		if(price<0)
			return false;
		return true;
	}

	public static boolean checkImage(String image) {
		return true;
	}

	public static boolean checkMessage(String message) {
		if(message.length()>COUP_MSG_LENGTH)
			return false;
		return true;
	}
}
