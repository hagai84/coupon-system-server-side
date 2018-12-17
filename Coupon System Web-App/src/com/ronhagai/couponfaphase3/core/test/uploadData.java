package com.ronhagai.couponfaphase3.core.test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.ronhagai.couponfaphase3.core.CouponSystem;
import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.enums.CouponType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;
import com.ronhagai.couponfaphase3.core.service.CompanyService;
import com.ronhagai.couponfaphase3.core.service.CouponService;
import com.ronhagai.couponfaphase3.core.service.CustomerService;

public class uploadData {
private static CouponSystem couponSystem = CouponSystem.getInstance();
	
	private static String driverName = "com.mysql.jdbc.Driver";
	private static String databaseUrl = "jdbc:mysql://db4free.net:3306/coupon_system";
	private static String userName = "coupon_group";
	private static String password = "12345678";
	
	
	public static void main(String[] args) {
		System.out.println("critical point 1");
		List<Long> companyids = new ArrayList<>();
		couponSystem.setServer(driverName, databaseUrl, userName, password);
		CreateDB db;
		try {
			System.out.println("critical point 2");
			db = new CreateDB();
			System.out.println("critical point 2.5");
//			db.dropTables();
			System.out.println("critical point 2.8");
			db.createDb();
			System.out.println("critical point 3");
		} catch (CouponSystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		for (int i = 0; i < 1 ; i++) {
			CustomerBean customer = new CustomerBean();
			customer.setCustName("name is: " + i);
			customer.setId((10000 + i));
			customer.setPassword("100000" + i);
			try {
				CustomerService.getInstance().createCustomer(customer);
				System.out.println("critical point 4");
			} catch (CouponSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < 1; i++) {
			CompanyBean company = new CompanyBean();
			company.setCompName("name is: " + i);
			company.setId((200000000 + i));
			company.setPassword("1000000" + i);
			company.setEmail("email"+i+"@gmail.com");
			try {
				companyids.add(CompanyService.getInstance().createCompany(company));
				System.out.println("critical point 5");
			} catch (CouponSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < 1; i++) {
			CouponBean coupon = new CouponBean();
			coupon.setTitle("Title is: " + i);
			coupon.setCouponId((300000000 + i));
			coupon.setStartDate(new Date(System.currentTimeMillis()));
			coupon.setEndDate(new Date((long) (System.currentTimeMillis()+1000)));
			coupon.setAmount(i * i);
			coupon.getType();
			coupon.setMessage(
					"this is the coupon message bla bla bla");
			coupon.setPrice(i*i);
			coupon.setCompanyId(200000000 + i);
			coupon.setImage("http://bestjquery.com/tutorial/product-grid/demo3/images/img-1.jpeg");
			if (i<20) {
				coupon.setType(CouponType.CAMPING);
			}
			if (i<40) {
				coupon.setType(CouponType.ELECTRICITY);
			}
			if (i<60) {
				coupon.setType(CouponType.FOOD);
			}
			if (i<80) {
				coupon.setType(CouponType.SPORTS);
			}
			if (i<1000) {
				coupon.setType(CouponType.TRAVELLING);
			}

			try {
				CouponService.getInstance().createCoupon(coupon, companyids.remove(0));
				System.out.println("critical point 6");
			} catch (CouponSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
