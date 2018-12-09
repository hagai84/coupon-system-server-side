package core.test;

import java.sql.Date;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;

public class RegularThread extends TestThread {
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("creating default values");
		super.run();
		try {
			createDefaultValues();
		} catch (CouponSystemException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
	}
/**
	 * Populates the DB with default values
	 * 
	 * @throws CouponSystemException
	 */
	private void createDefaultValues() throws CouponSystemException {

		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		CompanyBean company = new CompanyBean();
		for (char i = 97; i < 117; i++) {
			company.setId(100000 + i);
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			companyService.createCompany(company);
			System.out.println("LOG : Company created \n" + company);
			loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			coupon.setCouponId(200000 + i);
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(10);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage("aaaaaa");
			coupon.setPrice(200);
			coupon.setImage("aaaaaaaaaaaaaa");
			couponService.createCoupon(coupon, company.getId());
			System.out.println("LOG : Coupon created \n" + coupon);
			customer.setId(100032 + i);
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			customer.setPassword(""+i+i+i+i+i+i);
			customerService.createCustomer(customer);
			
			loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			//Only has correct Id because refferenced object is updated
			/*coupon.setCouponId(couponController.getCouponByTitle(coupon.getTitle()).getCouponId());
			customer.setId(customerController.getCustomerByName(customer.getCustName()).getId());

			couponController.purchaseCoupon(coupon.getCouponId(), customer.getId());
			System.out.println("LOG : Coupon purchased \n" + coupon);*/

		}
	}
	
}
