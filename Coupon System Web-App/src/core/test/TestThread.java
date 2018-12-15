package core.test;

import core.exception.CouponSystemException;
import core.service.CompanyService;
import core.service.CouponService;
import core.service.CustomerService;

public abstract class TestThread extends Thread{
	public CouponService couponService = CouponService.getInstance();
	public CompanyService companyService = CompanyService.getInstance();
	public CustomerService customerService = CustomerService.getInstance();
	
	public void loginAdmin()  {
		System.out.println(Thread.currentThread().getName() + " : LOG : Admin logged in");
	}
	
	public long loginCompany(String user, String password) throws CouponSystemException {
		System.out.println(Thread.currentThread().getName() + " : LOG : Company logged in : " + user);
		return companyService.companyLogin(user, password);
	}

	public long loginCustomer(String user, String password) throws CouponSystemException {
		System.out.println(Thread.currentThread().getName() + " : LOG : customer logged in : " + user);
		return customerService.customerLogin(user, password);
	}

}
