package com.yealink.common.util;

public enum GroupSourceEnum {
	INNER("INNER"),THIRD("THIRD");
	
	private String source;

	private GroupSourceEnum(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

}
