package com.yealink.model;

public class ADGroupModel {
	String nodeId;
	String name;
	String parentId;
	
	
	
	public ADGroupModel(String nodeId, String name, String parentId) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.parentId = parentId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	@Override
	public String toString() {
		return "ADGroupModel [nodeId=" + nodeId + ", name=" + name + ", parentId=" + parentId + "]";
	}
}
