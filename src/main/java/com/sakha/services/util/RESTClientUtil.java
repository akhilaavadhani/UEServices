package com.sakha.services.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class RESTClientUtil {

	static final Logger log = Logger.getLogger(RESTClientUtil.class);
	
	public static String post(String url, StringEntity parameter)
	{
		String result = "";
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			post.setEntity(parameter);
			
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			
			String line = "";
			while (( line = rd.readLine()) != null) {
				result += line;
			}
			
		}catch(Exception e){
			log.error("Exception :",e);
			result = "{\"error\":\""+e.getMessage()+"\"}";
		} 
		return result;
	}
	
	
}
