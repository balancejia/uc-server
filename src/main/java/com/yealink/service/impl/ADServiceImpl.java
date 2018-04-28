package com.yealink.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.yealink.cache.local.ILocalCache;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.EncrypUtils;
import com.yealink.common.util.GroupLoginUtil;
import com.yealink.common.util.GroupSourceEnum;
import com.yealink.common.util.UserStatus;
import com.yealink.model.ADApplicationProperties;
import com.yealink.service.IADService;
import com.yealink.uc.dao.UserGroupMapper;
import com.yealink.uc.dao.UserMapper;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;

@EnableScheduling
@Service
public class ADServiceImpl implements CommandLineRunner ,IADService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int PAGESIZE = 20;
	
	 @Autowired
	 private UserMapper userMapper;
	 
	 @Autowired
	 private ADApplicationProperties adApplicationProperties;
	 
	 @Autowired
	 private UserGroupMapper userGroupMapper;
	 
	 @Autowired  
	 private Environment environment;
	
	 @Autowired
	 private ILocalCache<String, Byte> syncCache;
	 
	 @Override
	 public void run(String... args) throws Exception {
		//getAllGroupFromAD();
		//getAllUserFromAD();
		
	}
	
	 /**
	  * 这个方法每天1点执行一次
	  * cron: 定时任务表达式.
	  * 指定：秒，分钟，小时，日期，月份，星期，年（可选）.
	  *  *：任意.
	  */
	 @Override
	 @Scheduled(cron="0 0 1 * * ?")
	 public void getAllGroupFromAD(){
		logger.debug("getAllGroupFromAD start " + new Date());
		if(syncCache.getIfPresent("getAllGroupFromAD()") == null){
			syncCache.put("getAllGroupFromAD()", (byte)1);
		}else {
			throw new UCException(ErrorCode.AD_SYNC_ERROR);
		}
	    List<Map<String,String>> adName = adApplicationProperties.getAdName();
	    for(int i = 0; i < adName.size(); i++){
	    	String hostName = null;
	    	String host = null;
	    	for(Map.Entry<String,String> entry : adName.get(i).entrySet()){
	    		host = entry.getKey(); //获取ad域host
	    		hostName = entry.getValue(); //获取ad域hostName
	    	}
	    	String username = environment.getProperty("ad.adProperties."+ hostName +".username");
	    	String password = environment.getProperty("ad.adProperties."+ hostName +".password");
	    	 if(StringUtils.isEmpty(host)){
	 	    	throw new UCException(ErrorCode.AD_NOT_EXIST);
	 	    }
	    	 String port = environment.getProperty("ad.adProperties."+ hostName +".port"); // 端口
	 	    Hashtable<String, Object> env = GroupLoginUtil.groupLogin(username, password, hostName, host, port);
	 	    try {
	 	    	LdapContext ctx = new InitialLdapContext(env, null);
	 	    	SearchControls searchCtls = new SearchControls();
	 			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	 			String searchFilter = "(&(objectClass=group))";
	 			String searchBase = environment.getProperty("ad.adProperties."+ hostName +".base");
	 			String returnedAtts[] = {"member","cn","displayName","objectGUID","sAMAccountName","objectClass"};
	 			searchCtls.setReturningAttributes(returnedAtts);
	 		    try {
	 				ctx.setRequestControls(new Control[]{ new PagedResultsControl(PAGESIZE, Control.CRITICAL) });
	 			} catch (IOException e) {
	 				throw new UCException(ErrorCode.AD_SEARCH_ERROR);
	 			}
	 		    byte[] cookie = null;
	 			do {
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
	 					
	 					String sAMAccountName = atts.get("sAMAccountName").toString().split(": ")[1];
	 					byte[] GUID = (byte[]) atts.get("objectGUID").get(0);
	 					String guid = EncrypUtils.byteArrayToHexString(GUID);
	 					if(!StringUtils.isEmpty(resultName)){
	 						UserGroup userGroup = new UserGroup();
	 						userGroup.setGuid(guid);
	 						userGroup.setSource(GroupSourceEnum.THIRD.toString());
	 						userGroup.setRealm(host);
	 						userGroup.setName(resultName);
	 						userGroup.setCode(sAMAccountName);
	 						//如果该用户组在AD域进行了更新，需要进行同步
	 						UserGroupExample userGroupExample = new UserGroupExample();
	 						userGroupExample.createCriteria().andGuidEqualTo(guid);
	 						List<UserGroup> userGroupInDBs = userGroupMapper.selectByExample(userGroupExample);
	 						if(userGroupInDBs.isEmpty() && userGroupInDBs.size() == 0){
	 							try {
		 							userGroupMapper.insertSelective(userGroup);
		 						} catch (DuplicateKeyException e) {
		 							// ignore duplicate key
		 						}
	 							continue;
	 						}
	 						UserGroup userGroupInDB = userGroupInDBs.get(0);
	 						userGroup.setId(userGroupInDB.getId());
	 						if(!userGroup.equals(userGroupInDB)){
	 							userGroupMapper.updateByExampleSelective(userGroup, userGroupExample);
	 						}
	 					}
	 				}
	 				 Control[] controls = ctx.getResponseControls();
	 		         if (controls != null) {
	 		             for (int j = 0; j < controls.length; j++) {
	 		                 if (controls[j] instanceof PagedResultsResponseControl) {
	 		                     PagedResultsResponseControl prrc = (PagedResultsResponseControl)controls[j];
	 		                     cookie = prrc.getCookie();
	 		                 }
	 		             }
	 		         }
	 		         try {
	 	                 ctx.setRequestControls(new Control[]{
	 	                		 new PagedResultsControl(PAGESIZE, cookie, Control.CRITICAL) });
	 				} catch (IOException e) {
	 					throw new UCException(ErrorCode.AD_SEARCH_ERROR);
	 				}
	 			} while (cookie != null);
	 			ctx.close();
	 			logger.debug("getAllGroupFromAD() end " + new Date());
	 	    } catch (NamingException err) {
	 	    	new UCException(ErrorCode.AD_SEARCH_ERROR);
	 	    }
	    }
	   
	}


	@Override
	@Scheduled(cron="0 0 1 * * ?")
	public void getAllUserFromAD() {
		logger.debug("getAllUserFromAD start " + new Date());
		if(syncCache.getIfPresent("getAllUserFromAD()") == null){
			syncCache.put("getAllUserFromAD()", (byte)1);
		}else {
			throw new UCException(ErrorCode.AD_SYNC_ERROR);
		}
		List<Map<String,String>> adName = adApplicationProperties.getAdName();
		for(int i = 0; i < adName.size(); i++){
			String hostName = null;
	    	String host = null;
	    	for(Map.Entry<String,String> entry : adName.get(i).entrySet()){
	    		host = entry.getKey(); //获取ad域host
	    		hostName = entry.getValue(); //获取ad域hostName
	    	}
	    	String username = environment.getProperty("ad.adProperties."+ hostName +".username");
	    	String password = environment.getProperty("ad.adProperties."+ hostName +".password");
	    	   if(StringUtils.isEmpty(host)){
	   	    	throw new UCException(ErrorCode.AD_NOT_EXIST);
	   	    }
	   	    String port = environment.getProperty("ad.adProperties."+ hostName +".port"); // 端口
	 	    Hashtable<String, Object> env = GroupLoginUtil.groupLogin(username, password, hostName, host, port);
	   	    try {
	   	    	LdapContext ctx = new InitialLdapContext(env, null);
	   	    	SearchControls searchCtls = new SearchControls();
	   			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	   			String searchFilter = "(&(objectClass=user))";
	 			String searchBase = environment.getProperty("ad.adProperties."+ hostName +".base");
	   			String returnedAtts[] = {"userPrincipalName","cn","displayName","objectGUID","sAMAccountName"};
	   			searchCtls.setReturningAttributes(returnedAtts);
	   		    byte[] cookie = null;
	   		    try {
	   				ctx.setRequestControls(new Control[]{ new PagedResultsControl(PAGESIZE, Control.CRITICAL) });
	   			} catch (IOException e) {
	   				throw new UCException(ErrorCode.AD_SEARCH_ERROR);
	   			}
	   	
	   			do {
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
	   					
	   					String userPrincipalName = atts.get("userPrincipalName").toString().split(": ")[1];
	   				//	byte[] GUID = (byte[]) atts.get("objectGUID").get(0);
	   				//	String guid = EncrypUtils.byteArrayToHexString(GUID);
	   					if(!StringUtils.isEmpty(resultName)){

	   						User user = new User();
	   						user.setUsername(userPrincipalName);
	   						user.setEmail(userPrincipalName);
	   						user.setNickname(resultName);
	   						user.setRealm(host);
	   						user.setStatus(UserStatus.AVAILABLE.getStatus());
	   						user.setSource(GroupSourceEnum.THIRD.toString());
	   						//如果该用户在AD域进行了更新，需要进行同步
	   						UserExample userExample = new UserExample();
	   						userExample.createCriteria().andUsernameEqualTo(userPrincipalName);
	   						List<User> userInDBs = userMapper.selectByExample(userExample);
	   						if(userInDBs.isEmpty() && userInDBs.size() == 0){
	   							try {
	   		   						user.setCreateTime(new Date());
		   							userMapper.insertSelective(user);
		   						} catch (DuplicateKeyException e) {
		   							// ignore duplicate key
		   						}
	   							continue;
	   						}
	   						User userInDB = userInDBs.get(0);
	   						user.setId(userInDB.getId());
	   						user.setStatus(userInDB.getStatus());
	   						user.setCreateTime(userInDB.getCreateTime());
	   						user.setUpdateTime(userInDB.getUpdateTime());
	   						if(!user.equals(userInDB)){
	   							userMapper.updateByExampleSelective(user, userExample);
	   						}
	   					}
	   				}
	   				 Control[] controls = ctx.getResponseControls();
	   		         if (controls != null) {
	   		             for (int j = 0; j < controls.length; j++) {
	   		                 if (controls[j] instanceof PagedResultsResponseControl) {
	   		                     PagedResultsResponseControl prrc = (PagedResultsResponseControl)controls[j];
	   		                     cookie = prrc.getCookie();
	   		                 }
	   		             }
	   		         }
	   		         try {
	   	                 ctx.setRequestControls(new Control[]{
	   	                		 new PagedResultsControl(PAGESIZE, cookie, Control.CRITICAL) });
	   				} catch (IOException e) {
	   					throw new UCException(ErrorCode.AD_SEARCH_ERROR);
	   				}

	   			} while (cookie != null);
	   			ctx.close();
	   			logger.debug("getAllUserFromAD() end " + new Date());
	   	    } catch (NamingException err) {
	   	    	new UCException(ErrorCode.AD_SEARCH_ERROR);
	   	    }
		}
	}
	
	@Override
	public void getUserFromAD(LdapContext ctx, String username, String host, String hostName) throws NamingException{
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "(&(objectClass=user)(userPrincipalName="+username+"))";
		String searchBase = environment.getProperty("ad.adProperties."+ hostName +".base");
		String returnedAtts[] = {"cn","displayName"};
		searchCtls.setReturningAttributes(returnedAtts);
		NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
		while (answer.hasMoreElements()){
			SearchResult sr = (SearchResult) answer.next();
			Attributes atts = sr.getAttributes();
			String resultName;
			if(atts.get("displayName") != null){
				resultName = atts.get("displayName").toString().split(": ")[1];
			}else{
				resultName = atts.get("cn").toString().split(": ")[1];
			}
			User user = new User();
			user.setUsername(username);
			user.setEmail(username);
			user.setNickname(resultName);
			user.setRealm(host);
			user.setStatus(UserStatus.AVAILABLE.getStatus());
			user.setSource(GroupSourceEnum.THIRD.toString());
			user.setCreateTime(new Date());
			try {
				userMapper.insertSelective(user);
			} catch (DuplicateKeyException e) {
				// ignore duplicate key
			}
		}
	}
	
	
	@Override
	public Set<String> getUsernameByGroup(String groupCode, String host){
		logger.debug("getUsernameByGroup start " + new Date());
	    List<Map<String,String>> adName = adApplicationProperties.getAdName();
    	String hostName = null;
	    for(int i = 0; i < adName.size(); i++){
	    	if(!StringUtils.isEmpty(adName.get(i).get(host))){
	    		hostName = adName.get(i).get(host);
	    		break;
	    	}
	    }
    	String username = environment.getProperty("ad.adProperties."+ hostName +".username");
    	String password = environment.getProperty("ad.adProperties."+ hostName +".password");
    	 if(StringUtils.isEmpty(host)){
 	    	throw new UCException(ErrorCode.AD_NOT_EXIST);
 	    }
    	 String port = environment.getProperty("ad.adProperties."+ hostName +".port"); // 端口
 	    Hashtable<String, Object> env = GroupLoginUtil.groupLogin(username, password, hostName, host, port);
 	   Set<String> usernameList = new HashSet<>();
 	    try {
 	    	LdapContext ctx = new InitialLdapContext(env, null);
 	    	SearchControls searchCtls = new SearchControls();
 			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
 			String searchFilter = "(&(objectClass=group)(sAMAccountName="+groupCode+"))";
 			String searchBase = environment.getProperty("ad.adProperties."+ hostName +".base");
 			String returnedAtts[] = {"member"};
 			searchCtls.setReturningAttributes(returnedAtts);
 			
			NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes atts = sr.getAttributes();
				logger.debug("--->" + atts);
				if(atts.get("member") != null){
					String member = atts.get("member").toString();
					String[] resultMember = member.split("(, |: )");
					for(int i = 1; i < resultMember.length; i++){
						getADUsername(ctx, searchBase, resultMember[i], usernameList);
					}
				}
 			ctx.close();
 			logger.debug("getUsernameByGroup end " + new Date());
			}
 	    } catch (NamingException err) {
 	    	new UCException(ErrorCode.AD_SEARCH_ERROR);
 	    }
 	    return usernameList;
	}
	
	private void getADUsername(LdapContext ctx,String searchBase ,String member, Set<String> result) throws NamingException{
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "(&(distinguishedName="+ member +"))";
		String returnedAtts[] = {"member", "cn","objectClass"};
		searchCtls.setReturningAttributes(returnedAtts);
		NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult) answer.next();
			Attributes atts = sr.getAttributes();
			logger.debug("--->" + atts);
			
			String objectClass = atts.get("objectClass").toString();
			String[] resultObjectClass = objectClass.split("(, |: )");
			for(int i = 0; i < resultObjectClass.length; i++){
				if(resultObjectClass[i].equals("user")){
					String resultCn = atts.get("cn").toString().split(": ")[1];
					if(!StringUtils.isEmpty(resultCn)){
						result.add(resultCn);
					}
				}else if (resultObjectClass[i].equals("group")) {
					if(atts.get("member") != null){
						String resultMember = atts.get("member").toString();
						String[] memberArray = resultMember.split("(, |: )");
						for(int j = 1; j < memberArray.length; j++){
							getADUsername(ctx, searchBase, memberArray[j], result);
						}
					}else{
						return ;
					}
				}
			}
		}
	}
	
}
