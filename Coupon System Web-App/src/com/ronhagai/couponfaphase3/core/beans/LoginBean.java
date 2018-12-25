package com.ronhagai.couponfaphase3.core.beans;

import com.ronhagai.couponfaphase3.core.enums.UserType;

public class LoginBean {
	private String userName;
	private String userPassword;
	private UserType userType;
	private String remeberMe;
	public LoginBean() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public String getRemeberMe() {
		return remeberMe;
	}
	public void setRemeberMe(String remeberMe) {
		this.remeberMe = remeberMe;
	}
	
	
}
