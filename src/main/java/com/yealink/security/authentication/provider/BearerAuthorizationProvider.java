package com.yealink.security.authentication.provider;

public interface BearerAuthorizationProvider {

	String USERID = "Userid";

	String getAuthorization();

	String getUserid();
}
