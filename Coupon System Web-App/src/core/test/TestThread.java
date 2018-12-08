package core.test;

import core.controller.CompanyController;
import core.controller.CouponController;
import core.controller.CustomerController;
import core.exception.CouponSystemException;

public abstract class TestThread extends Thread{
	public CouponController couponController = CouponController.getInstance();
	public CompanyController companyController = CompanyController.getInstance();
	public CustomerController customerController = CustomerController.getInstance();
	
	public void loginAdmin() throws CouponSystemException {
		System.out.println("LOG : Admin logged in");
	}
	
	public void loginCompany(String user, String password) throws CouponSystemException {
		companyController.companyLogin(user, password);	
		System.out.println("LOG : Company logged in");
	}

	public void loginCustomer(String user, String password) throws CouponSystemException {
		customerController.customerLogin(user, password);	
		System.out.println("LOG : customer logged in");
	}

}
