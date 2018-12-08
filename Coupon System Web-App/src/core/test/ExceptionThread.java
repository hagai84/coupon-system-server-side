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
		/*try {
			resetDB();
		} catch (CouponSystemException e1) {
			e1.printStackTrace();
		}*/
		
		System.out.println("running exception test");
		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		CompanyBean company = new CompanyBean();
		for (char i = 97; i < 117; i++) {
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			try {
				company.setId(companyService.getCompanyByName(company.getCompName()).getId());
			} catch (CouponSystemException e1) {
				System.err.println(e1);
				continue;
			}
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			try {
				coupon.setCouponId(couponService.getCouponByTitle(coupon.getTitle()).getCouponId());
			} catch (CouponSystemException e1) {
				System.err.println(e1);
				continue;
			}
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(50);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage("aaaaaa");
			coupon.setPrice(200);
			coupon.setImage("aaaaaaaaaaaaaa");	
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			try {
				customer.setId(customerService.getCustomerByName(customer.getCustName()).getId());
			} catch (CouponSystemException e1) {
				System.err.println(e1);
				continue;
			}
			customer.setPassword(""+i+i+i+i+i+i);
			
			
			switch(i%16) {
			case 0:
				company.setId(100000 + i);
				break;
			case 1:
				company.setPassword(""+i+i+i+i);
				break;
			case 2:
				company.setEmail(""+i+i+i+"@"+i+i+i+i+"");
				break;
			case 3:
				coupon.setCouponId(200000 + i);
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
				customer.setId(100032 + i);
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
			}
			//TODO Add unauthorized update/remove + same coupon purchase
			
			
			try {
				companyService.createCompany(company);
				System.out.println("LOG : Company created \n" + company);

			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				if(companyService != null) {
					couponService.createCoupon(coupon, company.getId());
					System.out.println("LOG : Coupon created \n" + coupon);
				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customerService.createCustomer(customer);
				System.out.println("LOG : Customer created \n" + customer);
			} catch (CouponSystemException e) {System.err.println(e);}
			
			try {
				if(customerService != null)
				loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i);
			} catch (CouponSystemException e) {System.err.println(e);} 
			try {
				if(customerService != null) {
					couponService.purchaseCoupon(coupon.getCouponId(), customer.getId());
					System.out.println("LOG : Coupon purchased \n" + coupon);

				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				if(companyService != null) {
					couponService.removeCoupon(coupon.getCouponId());
					System.out.println("LOG : Coupon deleted \n" + coupon);
				}
			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				customerService.removeCustomer(customer.getId());
				System.out.println("LOG : Customer deleted \n" + customer);

			} catch (CouponSystemException e) {System.err.println(e);}
			try {
				companyService.removeCompany(company.getId());
				System.out.println("LOG : Company deleted \n" + company);
			} catch (CouponSystemException e) {System.err.println(e);}	
			//TODO add all facade methods
		}
	}
}
