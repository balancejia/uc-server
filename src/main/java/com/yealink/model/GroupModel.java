package com.yealink.model;

public class GroupModel {
	private Long id;
	private String realm;
	private String code;
	private String name;
	private String source;
	
	
	public GroupModel(Long id, String realm, String code, String name, String source) {
		super();
		this.id = id;
		this.realm = realm;
		this.code = code;
		this.name = name;
		this.source = source;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
}
