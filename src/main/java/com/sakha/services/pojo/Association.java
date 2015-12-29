package com.sakha.services.pojo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Association {
	
	private String association;
	private String duration = "";
	private String major = "";
	
	public String getAssociation() {
		return association;
	}
	public void setAssociation(String association) {
		
		try {
			this.association = URLEncoder.encode( association , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.association="";
		}
		
		
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		
		try {
			this.duration = URLEncoder.encode( duration  , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.duration="";
		}
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		try {
			this.major = URLEncoder.encode( major , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.major="";
		}
	}
	
	@Override
	public String toString() {
		return "{ \"association\":\"" + association
				+ "\", \"duration\":\"" + duration + "\", \"major\":\"" + major + "\"}";
	}
	
}
	
