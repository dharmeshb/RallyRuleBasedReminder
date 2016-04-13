package com.dharmeshborad.rallyreminder.core.framework;

public class RuleBean {
	private String rule;
	private String message;

	public RuleBean(String rule,String message) {
		this.message=message;
		this.rule=rule;
	}
	
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
