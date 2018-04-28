package com.yealink.cache.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.yealink.cache.guava.GuavaAbstractLoadingCache;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.GroupLoginUtil;
import com.yealink.common.util.GroupSourceEnum;
import com.yealink.model.ADApplicationProperties;
import com.yealink.security.authentication.constant.TokenConstant;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;

@Component
public class ADUserGroupCacheImpl extends GuavaAbstractLoadingCache<String, List<String>> implements ILocalCache<String, List<String>>{

	 @Autowired  
	 private Environment environment;
	 
	@Autowired
	private ADApplicationProperties adApplicationProperties;
	
	private ADUserGroupCacheImpl() {
		setMaximumSize(1000);
		setExpireAfterWriteDuration(TokenConstant.DEFAULT_ACCESS_TOKEN_VALIDITY);
	}
	
	@Override
	public List<String> get(String key) {
		try {
			return getValue(key);
		} catch (ExecutionException e) {
			logger.error("Unable to get cache data",key,e);
			return null;
		}
	}

	@Override
	public void refresh(String key) {
		getCache().refresh(key);
	}

	@Override
	public void put(String key, List<String> value) {
		getCache().put(key, value);
		
	}

	@Override
	public void invalidate(String key) {
		getCache().invalidate(key);
	}

	@Override
	public void invalidateAll() {
		getCache().invalidateAll();
		
	}

	@Override
	public ConcurrentMap<String, List<String>> asMap() {
		return getCache().asMap();
	}

	@Override
	public List<String> getIfPresent(String key) {
		return getCache().getIfPresent(key);
	}

	@Override
	protected List<String> fetchData(String key) {
	    String[] usernameTmp = key.split("@");
	    List<Map<String,String>> adName = adApplicationProperties.getAdName();
    	String host = usernameTmp[usernameTmp.length-1];
    	String hostName = null;
	    for(int i = 0; i < adName.size(); i++){
	    	if(!StringUtils.isEmpty(adName.get(i).get(host))){
	    		hostName = adName.get(i).get(host);
	    		break;
	    	}
	    }
		String port = environment.getProperty("ad.adProperties."+ hostName +".port"); // 端口
	    String adusername = environment.getProperty("ad.adProperties."+ hostName +".username");
    	String password = environment.getProperty("ad.adProperties."+ hostName +".password");
	 	Hashtable<String, Object> env = GroupLoginUtil.groupLogin(adusername, password, hostName, host, port);
	 	List<String> resultGroup = new ArrayList<>();
	    try {
	    	LdapContext ctx = new InitialLdapContext(env, null);
	    	resultGroup = getUserGroupFromAD(ctx, key, hostName);
			ctx.close();
	    } catch (NamingException err) {
	    	throw new UCException("UNAUTHORIZED", "uc.token.unauthorized", HttpStatus.UNAUTHORIZED);
	    } 
		return resultGroup;
	}
	
	public List<String> getUserGroupFromAD(LdapContext ctx, String username, String hostName) throws NamingException{
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "(&(objectClass=user)(userPrincipalName=" + username + "))";
		String searchBase = environment.getProperty("ad.adProperties."+ hostName +".base");
		String returnedAtts[] = {"memberOf", "userPrincipalName","sAMAccountName"};
		searchCtls.setReturningAttributes(returnedAtts);
		NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
		List<String> group = new ArrayList<>();
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult) answer.next();
			Attributes atts = sr.getAttributes();
			logger.debug("--->" + atts);
			if(atts.get("memberOf") != null){
				String memberOf = atts.get("memberOf").toString();
				String[] memberOfArray = memberOf.split("(, |: )");
				for(int i = 1; i < memberOfArray.length; i++){
					getADGroup(ctx, searchBase, memberOfArray[i], group);
				}
			}
		}
		return group;
	}
	
	private void getADGroup(LdapContext ctx,String searchBase ,String memberOf, List<String> result) throws NamingException{
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "(&(objectClass=group)(distinguishedName="+ memberOf +"))";
		String returnedAtts[] = {"memberOf", "cn","displayName"};
		searchCtls.setReturningAttributes(returnedAtts);
		NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult) answer.next();
			Attributes atts = sr.getAttributes();
			logger.debug("--->" + atts);
			String resultName;
				if(atts.get("displayName") != null){
					resultName = atts.get("displayName").toString().split(": ")[1];
				}else{
					resultName = atts.get("cn").toString().split(": ")[1];
				}
			if(!StringUtils.isEmpty(resultName)){
				result.add(resultName);
			}
			
			if(atts.get("memberOf") != null){
				String resultMemberOf = atts.get("memberOf").toString();
				String[] memberOfArray = resultMemberOf.split("(, |: )");
				for(int i = 1; i < memberOfArray.length; i++){
					getADGroup(ctx, searchBase, memberOfArray[i], result);
				}
			}else{
				return ;
			}
			logger.debug(memberOf);
		}
	}

}
