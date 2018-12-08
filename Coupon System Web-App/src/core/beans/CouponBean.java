package core.beans;

import java.io.Serializable;
import java.sql.Date;
import core.enums.CouponType;
/**
 * JBean representing a Coupon.
 * Coupons have an <b>ID</b>, <b>title</b>, <b>start date</b>, <b>end date</b>, <b>amount</b>, <b>coupon type</b>, <b>message</b>, <b>price</b>, and <b>a path to a coupon-related image</b>.
 * @author Ron
 */

public class CouponBean implements Serializable{

	private static final long serialVersionUID = -927238060353756890L;
	private long couponId;


	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;
	private long companyId;

	/**
	 * Empty Coupon bean constructor.
	 */
	public CouponBean() {
		
	}

	/**
	 * Gets the coupon's ID number.
	 * @return The coupon's ID number.
	 */
	public long getCouponId() {
		return couponId;
	}

	/**
	 * Sets the coupon's ID number.
	 * @param id The coupon's ID number.
	 */
	public void setCouponId(long id) {
		this.couponId = id;
	}

	/**
	 * Gets the coupon's title.
	 * @return The coupon's title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the coupon's title.
	 * @param title The coupon's title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the coupon's start date.
	 * @return The coupon's start date.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the coupon's start date.
	 * @param startDate The coupon's start date.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the coupon's end date.
	 * @return The coupon's end date.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the coupon's end date.
	 * @param endDate The coupon's end date.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the available amount of this coupon to sell.
	 * @return The available amount of this coupon to sell.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the available amount of this coupon to sell.
	 * @param amount The available amount of this coupon to sell.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Gets the coupon's CouponType.
	 * @return The coupon's CouponType.
	 */
	public CouponType getType() {
		return type;
	}

	/**
	 * Sets the coupon's CouponType.
	 * @param type The coupon's CouponType.
	 */
	public void setType(CouponType type) {
		this.type = type;
	}

	/**
	 * Gets the coupon's message.
	 * @return The coupon's message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the coupon's message.
	 * @param message The coupon's message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the coupon's price.
	 * @return The coupon's price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the coupon's price.
	 * @param price The coupon's price.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Gets the path to the image related to the coupon.
	 * @return The path to the image related to the coupon.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the path to the image related to the coupon.
	 * @param image The path to the image related to the coupon.
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the companyId
	 */
	public long getCompanyId() {
		return companyId;
	}
	
	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	/**
	 * String representation of the Coupon's data.
	 * @return A String representation of the Coupon's data.
	 */
	@Override
	public String toString() {
		return "Coupon [id=" + couponId + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", amount=" + amount + ", type=" + type + ", message=" + message + ", price=" + price + "]";
	}

	
	/*
	 * (non-Javadoc)
	 * Compare 2 Coupon beans
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CouponBean other = (CouponBean) obj;
		/*if (amount != other.amount)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;*/
		if (couponId != other.couponId)
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
}
