package com.yealink.common.util;

public enum UserStatus {
	AVAILABLE("0"),DISABLE("1");
	
	private String status;
	private UserStatus(String status){
		this.status = status ;
	}
	public String getStatus() {
		return status;
	}

	
	
}
 