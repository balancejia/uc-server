package com.yealink.security.authentication.model;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 包含授权信息的认证对象
 */
public abstract class AbstractAuthentication implements Authentication, CredentialsContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8580046718198235214L;
	private Object details;
	private final List<GrantedAuthority> authorities;
	private boolean authenticated = false;

	public AbstractAuthentication(List<? extends GrantedAuthority> authorities) {
		if (null == authorities) {
			this.authorities = AuthorityUtils.NO_AUTHORITIES;
			return;
		}
		this.authorities = (List<GrantedAuthority>) authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
	}

	@Override
	public void eraseCredentials() {
		eraseSecret(getCredentials());
		eraseSecret(getPrincipal());
		eraseSecret(details);
	}

	private void eraseSecret(Object secret) {
		if (secret instanceof CredentialsContainer) {
			((CredentialsContainer) secret).eraseCredentials();
		}
	}

	@Override
	public String getName() {
		if (getPrincipal() instanceof UserDetails) {
			return ((UserDetails) getPrincipal()).getUsername();
		}

		if (getPrincipal() instanceof Principal) {
			return ((Principal) getPrincipal()).getName();
		}

		return getPrincipal() == null ? "" : getPrincipal().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractAuthentication)) {
			return false;
		}

		AbstractAuthentication safAuthentication = (AbstractAuthentication) obj;

		if ((null == getDetails() && safAuthentication.getDetails() != null)
				|| (null != getDetails() && safAuthentication.getDetails() == null)) {
			return false;
		}

		if ((null == getCredentials() && safAuthentication.getCredentials() != null)
				|| (null != getCredentials() && safAuthentication.getCredentials() == null)) {
			return false;
		}

		if ((null == getPrincipal() && safAuthentication.getPrincipal() != null)
				|| (null != getPrincipal() && safAuthentication.getPrincipal() != null)) {
			return false;
		}

		if (!getAuthorities().equals(safAuthentication.getAuthorities())) {
			return false;
		}

		return isAuthenticated() == safAuthentication.isAuthenticated();
	}

	@Override
	public int hashCode() {
		int code = 31;

		for (GrantedAuthority authority : getAuthorities()) {
			code ^= authority.hashCode();
		}

		if (null != getPrincipal()) {
			code ^= getPrincipal().hashCode();
		}

		if (null != getCredentials()) {
			code ^= getCredentials().hashCode();
		}

		if (null != getDetails()) {
			code ^= getDetails().hashCode();
		}

		if (isAuthenticated()) {
			code ^= -37;
		}
		return code;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Principal: ").append(getPrincipal()).append("; ");
		sb.append("Credentials: [PROTECTED]; ");
		sb.append("Authenticated: ").append(isAuthenticated()).append("; ");
		sb.append("Details: ").append(getDetails()).append("; ");

		if (!authorities.isEmpty()) {
			sb.append("Granted Authorities: ");

			int i = 0;
			for (GrantedAuthority authority : authorities) {
				if (i++ > 0) {
					sb.append(", ");
				}

				sb.append(authority);
			}
		} else {
			sb.append("Not granted any authorities");
		}

		return sb.toString();
	}
}
