package com.sakha.services.pojo;

import java.util.List;

public class AssociationsData {

	private List<Association> associationsData;

	public List<Association> getAssociationsData() {
		return associationsData;
	}

	public void setAssociationsData(List<Association> associationsData) {
		this.associationsData = associationsData;
	}
	
	@Override
	public String toString() {
		return "{ \"associationsData\":" + associationsData
				+"}";
	}
}
