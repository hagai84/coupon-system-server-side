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
		System.out.println("LOG : Admin logged in");
	}
	
	public void loginCompany(String user, String password) throws CouponSystemException {
		System.out.println("LOG : Company logged in : " + companyService.companyLogin(user, password));
	}

	public void loginCustomer(String user, String password) throws CouponSystemException {
		System.out.println("LOG : customer logged in : " + customerService.customerLogin(user, password));
	}

}
