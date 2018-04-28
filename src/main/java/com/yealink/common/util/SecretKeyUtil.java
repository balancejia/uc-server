package com.yealink.common.util;

import java.util.UUID;

public class SecretKeyUtil {
	
	public static String getKey()
	  {
	    String key = UUID.randomUUID().toString();
	    return key;
	  }
	  
	  public static String getMac(String uri, String nonce, String secretKey)
	  {
	    String mac = null;
	    StringBuilder sbRawMac = new StringBuilder();
	    sbRawMac.append(uri);
	    sbRawMac.append("\n");
	    sbRawMac.append(nonce);
	    sbRawMac.append("\n");
	    mac = EncrypUtils.encryptHMac256(sbRawMac.toString(), secretKey);
	    return mac;
	  }
}
