package com.monocept.myapp.entity;


public class MailStructure {
	private String subject;
	private String message;
	public MailStructure( String subject, String message) {
		super();
		this.subject = subject;
		this.message = message;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
	
	
}
