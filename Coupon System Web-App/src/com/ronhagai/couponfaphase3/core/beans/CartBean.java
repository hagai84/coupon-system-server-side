package com.ronhagai.couponfaphase3.core.beans;

import java.io.Serializable;
import java.util.Collection;

import com.ronhagai.couponfaphase3.core.enums.CartStatus;

public class CartBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CartStatus status;
	private Collection<Long> coupons;

	public CartStatus getStatus() {
		return status;
	}
	public void setStatus(CartStatus status) {
		this.status = status;
	}
	public Collection<Long> getCoupons() {
		return coupons;
	}
	public void setCoupons(Collection<Long> coupons) {
		this.coupons = coupons;
	}
}
