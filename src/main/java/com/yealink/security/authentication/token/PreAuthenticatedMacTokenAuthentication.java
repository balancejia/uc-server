package com.yealink.security.authentication.token;

public class PreAuthenticatedMacTokenAuthentication extends AbstractPreAuthenticatedAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5109046949744784095L;
	private String id;
	private String mac;
	private String nonce;
	private String httpMethod;
	private String requestUri;
	private String host;

	public PreAuthenticatedMacTokenAuthentication(String id, String mac, String nonce, String httpMethod,
			String requestUri, String host) {
		this.id = id;
		this.mac = mac;
		this.nonce = nonce;
		this.httpMethod = httpMethod;
		this.requestUri = requestUri;
		this.host = host;
	}

	public String getId() {
		return this.id;
	}

	public String getRequestUri() {
		return this.requestUri;
	}

	public String getMac() {
		return this.mac;
	}

	public String getNonce() {
		return this.nonce;
	}

	public String getHttpMethod() {
		return this.httpMethod;
	}

	public String getHost() {
		return this.host;
	}


}
