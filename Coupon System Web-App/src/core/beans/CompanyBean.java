package core.beans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JBean representing a Company.
 * Companies have an <b>ID</b>, <b>company name</b>, <b>password</b>, <b>e-mail</b> and a <b>Collection of Coupons they sell</b>.
 * @author Hagai
 */
 
public class CompanyBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<CouponBean> coupons;

	/**
	 * Company bean constructor that instantiates an ID number.
	 * @param id Company ID.
	 */
	public CompanyBean(long id) {
		super();
		this.id = id;
	}

	/**
	 * Empty Company bean constructor.
	 */
	public CompanyBean() {

	}

	/**
	 * Gets the company's ID number.
	 * @return The company's ID number.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the company's ID number.
	 * @param id The company's ID number.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the company's name.
	 * @return The company's name.
	 */
	public String getCompName() {
		return compName;
	}

	/**
	 * Sets the company's name.
	 * @param compName The company's name.
	 */
	public void setCompName(String compName) {
		this.compName = compName;
	}

	/**
	 * Gets the company's password.
	 * @return The company's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the company's password.
	 * @param password The company's password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the company's e-mail.
	 * @return email The company's e-mail.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the company's e-mail.
	 * @param email The company's e-mail.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the company's Coupons Collection.
	 * @return The company's Coupons Collection.
	 */
	public Collection<CouponBean> getCoupons() {
		return coupons;
	}

	/**
	 * Sets the company's Coupons Collection.
	 * @param coupons The company's Coupons Collection.
	 */
	public void setCoupons(Collection<CouponBean> coupons) {
		this.coupons = coupons;
	}


	/**
	 * String representation of the Company's data.
	 * @return A String representation of the Company's data.
	 */
	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", password=" + password + ", email=" + email
				+ ", coupons=" + coupons + "]";
	}

	/*
	 * (non-Javadoc)
	 * Compare 2 Company beans
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
		CompanyBean other = (CompanyBean) obj;
		if (compName == null) {
			if (other.compName != null)
				return false;
		} else if (!compName.equals(other.compName))
			return false;
		if (coupons == null) {
			if (other.coupons != null)
				return false;
		} else if (!coupons.equals(other.coupons))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
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
