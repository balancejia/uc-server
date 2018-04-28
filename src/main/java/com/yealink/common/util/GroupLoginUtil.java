package com.yealink.common.util;

import java.util.Hashtable;

import javax.naming.Context;

public class GroupLoginUtil {
	
	public static Hashtable<String, Object> groupLogin(String username, String password, String hostName, String host, String port){
		
 	    String url = new String("ldap://" + host + ":" + port);
 	    
 	    Hashtable<String, Object> env = new Hashtable<String, Object>();
 	    env.put(Context.SECURITY_AUTHENTICATION, "simple");
 	    env.put(Context.SECURITY_PRINCIPAL, username);
 	    env.put(Context.SECURITY_CREDENTIALS, password);
 	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
 	    env.put(Context.PROVIDER_URL, url);
 	    env.put("java.naming.ldap.attributes.binary","objectGUID");
		return env;
	}
}
