package com.ronhagai.couponfaphase3.core.test;

import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.service.CompanyService;
import com.ronhagai.couponfaphase3.core.service.CouponService;
import com.ronhagai.couponfaphase3.core.service.CustomerService;

public abstract class GenericThread extends Thread{
	public CouponService couponService = CouponService.getInstance();
	public CompanyService companyService = CompanyService.getInstance();
	public CustomerService customerService = CustomerService.getInstance();
	
	protected void loginAdmin()  {
		System.out.println(Thread.currentThread().getName() + " : LOG : Admin logged in");
	}
	
	protected long loginCompany(String user, String password) throws CouponSystemException {
		System.out.println(Thread.currentThread().getName() + " : LOG : Company logged in : " + user);
		return companyService.companyLogin(user, password);
	}

	protected long loginCustomer(String user, String password) throws CouponSystemException {
		System.out.println(Thread.currentThread().getName() + " : LOG : customer logged in : " + user);
		return customerService.customerLogin(user, password);
	}

}
