package core.beans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JBean representing a Customer.
 * Customers have an <b>ID</b>, <b>customer name</b>, <b>password</b>, and a <b>Collection of Coupons they bought</b>.
 * @author Yair Argaman
 */
@XmlRootElement
public class CustomerBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/* Attributes */
	private long                id;
	private String              custName;
	private String              password;
	private Collection <CouponBean> coupons;
	
	/* Constructors */

	/**
	 * Empty Customer bean constructor.
	 */
	public CustomerBean( ) {
	}
	
	/* Public Methods */

	/**
	 * Gets the customer's ID number.
	 * @return The customer's ID number.
	 */
	public long getId( ) {
		return id;
	}

	/**
	 * Sets the customer's ID number.
	 * @param id The customer's ID number.
	 */
	public void setId( long id ) {
		this.id = id;
	}

	/**
	 * Gets the customer's name.
	 * @return The customer's name.
	 */
	public String getCustName( ) {
		return custName;
	}

	/**
	 * Sets the customer's name.
	 * @param custName The customer's name.
	 */
	public void setCustName( String custName ) {
		this.custName = custName;
	}

	/**
	 * Gets the customer's password.
	 * @return The customer's password.
	 */
	public String getPassword( ) {
		return password;
	}

	/**
	 * Sets the customer's password.
	 * @param password The customer's password.
	 */
	public void setPassword( String password ) {
		this.password = password;
	}

	/**
	 * Gets the customer's Coupons Collection.
	 * @return The customer's Coupons Collection.
	 */
	public Collection<CouponBean> getCoupons( ) {
		return coupons;
	}

	/**
	 * Sets the customer's Coupons Collection.
	 * @param coupons The customer's Coupons Collection.
	 */
	public void setCoupon( Collection <CouponBean> coupons ) {
		this.coupons = coupons;
	}

	/**
	 * String representation of the Customer's data.
	 * @return A String representation of the Customer's data.
	 */
	@Override
	public String toString() {
		return "Customer [id=" + id + ", custName=" + custName + ", password=" + password + ", coupons=" + coupons
				+ "]";
	}


	/*
	 * (non-Javadoc)
	 * Compare 2 Customer beans
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
		CustomerBean other = (CustomerBean) obj;
		if (coupons == null) {
			if (other.coupons != null)
				return false;
		} else if (!coupons.equals(other.coupons))
			return false;
		if (custName == null) {
			if (other.custName != null)
				return false;
		} else if (!custName.equals(other.custName))
			return false;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
	
}