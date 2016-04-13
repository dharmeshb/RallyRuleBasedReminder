package com.dharmeshborad.rallyreminder.core;

public class Developer {
	
	public Developer(String name, String emailId) {
		this.name = name;
		this.emailId = emailId;
	}
	
	private String name;
	private String emailId;
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
