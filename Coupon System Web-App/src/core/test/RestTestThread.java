package core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public abstract class RestTestThread extends Thread{

	private static String url = "http://localhost:8080/Coupon_System_Web-App/rest";
	
	public void loginAdmin()  {
		System.out.println("LOG : Admin logged in");
	}
	
	public long loginCompany(String user, String password) {
		
		List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("name", user));
        form.add(new BasicNameValuePair("password", password));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);		
        HttpPost postMethod = new HttpPost(url + "/companies/login");
        postMethod.setEntity(entity);
		HttpResponse response;
		try {
			response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : Company logged in : " + user);
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

	public long loginCustomer(String user, String password) {
		List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("name", user));
        form.add(new BasicNameValuePair("password", password));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);		
        HttpPost postMethod = new HttpPost(url + "/customers/login");
        postMethod.setEntity(entity);
		HttpResponse response;
		try {
			response = HttpClientBuilder.create().build().execute(postMethod);	
			int status = response.getStatusLine().getStatusCode();
			if(status==200) {
				System.out.println("LOG : customer logged in : " + user);
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
