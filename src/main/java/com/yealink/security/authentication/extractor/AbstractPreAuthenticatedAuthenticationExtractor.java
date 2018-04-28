package com.yealink.security.authentication.extractor;

import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

/**
 * 授权信息提取器
 *
 */
public abstract class AbstractPreAuthenticatedAuthenticationExtractor
		implements PreAuthenticatedAuthenticationExtractor {

	protected Map<String, String> splitToMap(String data) {
		return new CaseInsensitiveMap(Splitter.on(",").trimResults()
				.withKeyValueSeparator(Splitter.on("=").trimResults().limit(2)).split(data));
	}

	protected String getValue(Map<String, String> map, String name) {
		return StringUtils.strip(map.get(name), "\"");
	}

}
