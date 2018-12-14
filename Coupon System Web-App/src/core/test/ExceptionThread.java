package core.test;

import java.sql.Date;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;

public class ExceptionThread extends TestThread {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		testExceptions();
	}
	
	private void testExceptions() {		
		System.out.println("running exception test");
		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		CompanyBean company = new CompanyBean();
		for (char i = 97; i < 117; i++) {
			
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			company.setId(100001+i-97);
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			
			coupon.setCouponId(300001+i-97);
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(50);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage("aaaaaa");
			coupon.setPrice(200);
			coupon.setImage("aaaaaaaaaaaaaa");
			coupon.setCompanyId(company.getId());
			/*try {
				customer.setId(loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i));
			} catch (CouponSystemException e1) {
				System.err.println(e1);
				continue;
			}*/
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			customer.setId(200001+i-97);
			customer.setPassword(""+i+i+i+i+i+i);
			
			
			switch(i%17) {
			case 0:
				company.setId(110000 + i);
				break;
			case 1:
				company.setPassword(""+i+i+i+i);
				break;
			case 2:
				company.setEmail(""+i+i+i+"@"+i+i+i+i+"");
				break;
			case 3:
				coupon.setCouponId(330000 + i);
				break;
			case 4:
				coupon.setTitle(""+i+i+i+" "+i+i+i+i +"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 5:
				coupon.setStartDate(new Date(System.currentTimeMillis()-(1000*60*60*24)));
				break;
			case 6:
				coupon.setEndDate(new Date(System.currentTimeMillis()-(1000*60*60*24*30)));
				break;
			case 7:
				coupon.setAmount(-4);
				break;
			case 8:
				coupon.setType(null);
				break;
			case 9:
				coupon.setMessage("aaaaaa"+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 10:
				coupon.setPrice(-4);
				break;
			case 11:
				coupon.setImage("aaaaaaaaaaaaaa");	
				break;
			case 12:
				customer.setId(220000 + i);
				break;
			case 13:
				customer.setCustName(""+i+i+i+" "+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 14:
				customer.setPassword(""+i+i+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 15:
				company.setCompName(""+i+i+i+i+i+i+"ggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
						+ "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				break;
			case 16:
				coupon.setCompanyId(440000);
			}
			//TODO Add unauthorized update/remove + same coupon purchase
			
			
			try {
				company.setId(companyService.createCompany(company));
				System.out.println("LOG : Company created \n" + company);

			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				coupon.setCouponId(couponService.createCoupon(coupon, company.getId()));
				System.out.println("LOG : Coupon created \n" + coupon);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customer.setId(customerService.createCustomer(customer));
				System.out.println("LOG : Customer created \n" + customer);
			} catch (CouponSystemException e) {System.err.println(e);}
			
			try {
				loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);} 
			try {
				couponService.purchaseCoupon(coupon.getCouponId(), customer.getId());
				System.out.println("LOG : Coupon purchased \n" + coupon);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				couponService.removeCoupon(coupon.getCouponId(), company.getId());
				System.out.println("LOG : Coupon deleted \n" + coupon);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customerService.removeCustomer(customer.getId());
				System.out.println("LOG : Customer deleted \n" + customer);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				companyService.removeCompany(company.getId());
				System.out.println("LOG : Company deleted \n" + company);
			} catch (CouponSystemException e) {System.err.println(e);}	
			//TODO add all service methods
		}
	}
}
