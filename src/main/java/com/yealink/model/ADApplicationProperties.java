package com.yealink.model;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component  
@ConfigurationProperties(prefix="ad") //接收application.yml中的ad下面的属性  
public class ADApplicationProperties {
	private List<Map<String,String>> adName;

	public List<Map<String, String>> getAdName() {
		return adName;
	}

	public void setAdName(List<Map<String, String>> adName) {
		this.adName = adName;
	}
	
	

}
