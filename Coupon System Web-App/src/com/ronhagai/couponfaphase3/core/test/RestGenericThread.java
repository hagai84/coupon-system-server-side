package com.ronhagai.couponfaphase3.core.test;

import java.io.IOException;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronhagai.couponfaphase3.core.beans.CompanyBean;
import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.beans.CustomerBean;
import com.ronhagai.couponfaphase3.core.beans.UserBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;

public abstract class RestGenericThread extends GenericThread{

	private static String url = "http://localhost:8080/Coupon_System_Web-App/rest";
	
	@Override
	protected void loginAdmin()  {		
		try {
			UserBean loginBean = new UserBean();
			loginBean.setUserName("admin");
			loginBean.setUserPassword("1234");
			loginBean.setUserType(UserType.ADMIN);
			String json = new ObjectMapper().writeValueAsString(loginBean);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/login");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : REST Admin logged in");
				return;
			}else {				
				System.err.println(EntityUtils.toString(response.getEntity()));
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	protected long loginCompany(String user, String password) {	
		try {
			UserBean loginBean = new UserBean();
			loginBean.setUserName(user);
			loginBean.setUserPassword(password);
			loginBean.setUserType(UserType.COMPANY);
			HttpPost postMethod = new HttpPost(url + "/login");
			postMethod.addHeader("Accept-Language", "en");
			String json = new ObjectMapper().writeValueAsString(loginBean);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : REST Company logged in : " + user);
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

	@Override
	protected long loginCustomer(String user, String password) {	
		try {
			UserBean loginBean = new UserBean();
			loginBean.setUserName(user);
			loginBean.setUserPassword(password);
			loginBean.setUserType(UserType.CUSTOMER);
			String json = new ObjectMapper().writeValueAsString(loginBean);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/login");
			postMethod.addHeader("Accept-Language", "de");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : REST customer logged in : " + user);
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
	
	protected long createCoupon(CouponBean coupon, long userId) {
		try {
			String json = new ObjectMapper().writeValueAsString(coupon);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/coupons");
			postMethod.setEntity(entity);
			postMethod.setHeader("Cookie", "userId="+userId+"; userType=COMPANY");
	        postMethod.addHeader("Accept-Language", "it");
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : REST Coupon created" + coupon);
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

	protected long createCompany(CompanyBean company) {
		try {
			String json = new ObjectMapper().writeValueAsString(company);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/companies");
			postMethod.setHeader("Cookie", "userId=123456789; userType=ADMIN");
	        postMethod.addHeader("Accept-Language", "en");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : REST Company created - " + company);
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

	protected long createCustomer(CustomerBean customer) {
		try {
			String json = new ObjectMapper().writeValueAsString(customer);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			HttpPost postMethod = new HttpPost(url + "/customers");
			postMethod.setHeader("Cookie", "userId=123456789; userType=ADMIN");
	        postMethod.addHeader("Accept-Language", "de");
			postMethod.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
//			System.out.println(status);
			if(status==200) {
				System.out.println("LOG : REST Customer created - " + customer);
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

