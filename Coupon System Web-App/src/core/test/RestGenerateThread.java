package core.test;

import java.io.IOException;
import java.sql.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.beans.CompanyBean;
import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.enums.CouponType;
import core.exception.CouponSystemException;

public class RestGenerateThread extends RestTestThread {
	
	private static String url = "http://localhost:8080/Coupon_System_Web-App/rest";

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("creating default values");
		super.run();
		try {
			createDefaultValues();
		} catch (CouponSystemException e) {
			System.err.println(Thread.currentThread().getName() + e.getMessage());
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
//			company.setId(100000 + i);
			company.setCompName(""+i+i+i+" "+i+i+i+i);
			company.setPassword(""+i+i+i+i+i+i);
			company.setEmail(""+i+i+i+"@"+i+i+i+i+".com");
			createCompany(company);
			company.setId(loginCompany(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i));
//			coupon.setCouponId(200000 + i);
			coupon.setTitle(""+i+i+i+" "+i+i+i+i);
			coupon.setStartDate(new Date(System.currentTimeMillis()+(1000*60*60*24)));
			coupon.setEndDate(new Date(System.currentTimeMillis()+(1000*60*60*24*30*12)));
			coupon.setAmount(10);
			coupon.setType(CouponType.CAMPING);
			coupon.setMessage(""+i+i+i+i+i+i);
			coupon.setPrice(200);
			coupon.setImage(""+i+i+i+i+i+i+i+i+i+i+i+i);
			coupon.setCompanyId(company.getId());
			coupon.setCouponId(createCoupon(coupon, company.getId()));
//			customer.setId(100032 + i);
			customer.setCustName(""+i+i+i+" "+i+i+i+i);
			customer.setPassword(""+i+i+i+i+i+i);
			createCustomer(customer);		
			customer.setId(loginCustomer(""+i+i+i+" "+i+i+i+i, ""+i+i+i+i+i+i));

//			purchaseCoupon(coupon.getCouponId(), customer.getId());
//			System.out.println("LOG : Coupon purchased \n" + coupon);

		}
	}
	
	private long createCoupon(CouponBean coupon, long userId) {
		try {
			String json = new ObjectMapper().writeValueAsString(coupon);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/coupons");
			postMethod.setEntity(entity);
			postMethod.setHeader("userId", String.valueOf(userId));
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : Coupon created" + coupon);
				return Long.parseLong(EntityUtils.toString(response.getEntity()));
			}else {				
				System.err.println(EntityUtils.toString(response.getEntity()));
				return -1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	private long createCompany(CompanyBean company) {
		try {
			String json = new ObjectMapper().writeValueAsString(company);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/companies");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : Company created - " + company);
				return Long.parseLong(EntityUtils.toString(response.getEntity()));
			}else {				
				System.err.println(EntityUtils.toString(response.getEntity()));
				return -1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	private long createCustomer(CustomerBean customer) {
		try {
			String json = new ObjectMapper().writeValueAsString(customer);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/customers");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
//			System.out.println(status);
			if(status==200) {
				System.out.println("LOG : Customer created - " + customer);
				return Long.parseLong(EntityUtils.toString(response.getEntity()));
			}else {				
				System.err.println(EntityUtils.toString(response.getEntity()));
				return -1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	
	
}
