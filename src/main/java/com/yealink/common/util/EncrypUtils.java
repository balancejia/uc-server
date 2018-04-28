package com.yealink.common.util;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.Base64;
import com.netflix.loadbalancer.ServerStatusChangeListener;
import com.yealink.security.authentication.model.UCAccount;

public class EncrypUtils {
	
	 public static final String KEY_SHA = "SHA";
	  public static final String KEY_MD5 = "MD5";
	  public static final String KEY_HMD5_256 = "HmacSHA256";
	  public static final Integer SALT_LENGTH = 12;
	  private static Logger logger = LoggerFactory.getLogger(EncrypUtils.class);
	  private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	  
	  public static String encryptMD5_STD(String content)
	  {
	    String resultString = "";
	    String salt = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
	    try
	    {
	      MessageDigest md5 = MessageDigest.getInstance("MD5");
	      md5.update(salt.getBytes());
	      md5.update(content.getBytes());
	      
	      resultString = byteArrayToHexString(md5.digest());
	    }
	    catch (Exception e)
	    {
	      logger.error("encryptMD5 fail", e);
	    }
	    return (salt+resultString).toLowerCase();
	  }
	  
	  public static boolean validPassword(String password, String passwordInDb){
		  String salt = passwordInDb.substring(0, SALT_LENGTH);
		  String resultString = "";
		  try {
			  MessageDigest md5 = MessageDigest.getInstance("MD5");
		      md5.update(salt.getBytes());
		      md5.update(password.getBytes());
		      
		      resultString = byteArrayToHexString(md5.digest());
		} catch (Exception e) {
			 logger.error("encryptMD5 valid fail", e);
		}
		  
		  if((salt + resultString).toLowerCase().equals(passwordInDb)){
			  return true;
		  }
		  return false;
	  }
	  
	  
	  public static String encryptHMac256(String content, String key)
	  {
	    String resultString = "";
	    try
	    {
	      SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
	      Mac mac = Mac.getInstance(secretKey.getAlgorithm());
	      mac.init(secretKey);
	      byte[] digest = mac.doFinal(content.getBytes());
	      resultString = new String(Base64.byteArrayToBase64(digest));
	    }
	    catch (Exception e)
	    {
	      logger.error("encryptHMac256 fail", e);
	    }
	    return resultString;
	  }
	
	  
	  public static byte[] encryptSHA(String content)
	    throws Exception
	  {
	    MessageDigest sha = MessageDigest.getInstance("SHA");
	    sha.update(content.getBytes());
	    
	    return sha.digest();
	  }
	  public static byte[] encryptDes(byte[] src, byte[] key)
	    throws Exception
	  {
	    SecureRandom sr = new SecureRandom();
	    DESKeySpec dks = new DESKeySpec(key);
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	    SecretKey securekey = keyFactory.generateSecret(dks);
	    Cipher cipher = Cipher.getInstance("DES");
	    cipher.init(1, securekey, sr);
	    return cipher.doFinal(src);
	  }
	  
	  public static byte[] decryptDes(byte[] src, byte[] key)
	    throws Exception
	  {
	    SecureRandom sr = new SecureRandom();
	    DESKeySpec dks = new DESKeySpec(key);
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	    SecretKey securekey = keyFactory.generateSecret(dks);
	    Cipher cipher = Cipher.getInstance("DES");
	    cipher.init(2, securekey, sr);
	    return cipher.doFinal(src);
	  }
	  
	  public static String byteArrayToHexString(byte[] b)
	  {
	    StringBuffer resultSb = new StringBuffer();
	    for (int i = 0; i < b.length; i++) {
	      resultSb.append(byteToHexString(b[i]));
	    }
	    return resultSb.toString();
	  }
	  
	  public static String byteToHexString(byte b)
	  {
	    int n = b;
	    if (n < 0) {
	      n = 256 + n;
	    }
	    int d1 = n / 16;
	    int d2 = n % 16;
	    return hexDigits[d1] + hexDigits[d2];
	  }
	  
	  public static void main(String[] args) {
		  System.out.println(EncrypUtils.encryptHMac256("Message","secret").equals("qnR8UCqJggD55PohusaBNviGoOJ67HC6Btry4qXLVZc="));
	  }
}
