package com.yealink.security.authentication.extractor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public class PreAuthenticatedAuthenticationExtractorManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private List<PreAuthenticatedAuthenticationExtractor> extractors = new ArrayList<>();

	public void append(PreAuthenticatedAuthenticationExtractor extractor) {
		extractors.add(extractor);
	}

	public Authentication extractAuthentication(String authentication, HttpServletRequest request)
			throws AuthenticationException {
		Assert.notNull(authentication, "authentication cannot be null.");
		int spaceIndex = authentication.indexOf(" ");
		if (spaceIndex > -1) {
			String prefix = authentication.substring(0, spaceIndex);
			String value = authentication.substring(spaceIndex).trim();
			logger.debug("Extract authentication,prefix:{},value:{}", prefix, value);
			for (PreAuthenticatedAuthenticationExtractor extractor : extractors) {
				if (extractor.getPrefix().equalsIgnoreCase(prefix)) {
					return extractor.extractAuthentication(value, request);
				}
			}
		}
		return null;
	}

}
