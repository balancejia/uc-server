package com.yealink.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yealink.common.exception.UCException;
import com.yealink.model.BearerTokenForm;
import com.yealink.model.MacTokenForm;
import com.yealink.model.RefreshTokenForm;
import com.yealink.security.authentication.model.UCAccount;
import com.yealink.security.authentication.model.UCBearerToken;
import com.yealink.security.authentication.model.UCMacToken;
import com.yealink.security.authentication.service.impl.UCBearerTokenService;
import com.yealink.security.authentication.service.impl.UCMacTokenService;
import com.yealink.security.authentication.token.PreAuthenticatedBearerTokenAuthentication;
import com.yealink.security.authentication.token.PreAuthenticatedMacTokenAuthentication;
import com.yealink.service.IUserService;

@RestController
@RequestMapping("/api/v1")
public class TokensController {

	@Resource
	private IUserService userService;

	@Resource(name = "mac_token_service")
	private UCMacTokenService uCMacTokenService;

	@Resource(name = "bearer_token_service")
	private UCBearerTokenService uCBearerTokenService;

	@RequestMapping(value = "/tokens", method = RequestMethod.POST)
	public UCMacToken login(@Valid @RequestBody UCAccount ucAccount, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		if(userService.checkAD(ucAccount)){
			return uCMacTokenService.createToken(ucAccount);
		}
		if (userService.validUserPassword(ucAccount)) {
			return uCMacTokenService.createToken(ucAccount);
		}
		throw new UCException("UNAUTHORIZED", "uc.token.unauthorized", HttpStatus.UNAUTHORIZED);
	}

	@RequestMapping(value = "/tokens/mac/{access_token}/valid", method = RequestMethod.POST)
	public UCMacToken validMacToken(@PathVariable("access_token") String accessToken,
			@RequestBody MacTokenForm macTokenForm) {
		PreAuthenticatedMacTokenAuthentication preAuthenticatedAuthentication = new PreAuthenticatedMacTokenAuthentication(
				accessToken, macTokenForm.getMac(), macTokenForm.getNonce(), macTokenForm.getHttpMethod(),
				macTokenForm.getRequestUri(), macTokenForm.getHost());
		return uCMacTokenService.verifyToken(preAuthenticatedAuthentication);
	}

	@RequestMapping(value = "/tokens/mac/{refresh_token}/refresh", method = RequestMethod.POST)
	public UCMacToken refreshMacToken(HttpServletRequest request, @PathVariable("refresh_token") String refreshToken,
			@Valid @RequestBody RefreshTokenForm tokenForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		UCMacToken token = new UCMacToken();
		token.setAccessToken(tokenForm.getAccessToken());
		token.setUserId(tokenForm.getUserId());
		token.setRefreshToken(refreshToken);
		return uCMacTokenService.refreshToken(token);
	}

	@RequestMapping(value = "/tokens/bearer/{bearer_token}/valid", method = RequestMethod.POST)
	public UCBearerToken validBearerToken(@PathVariable("bearer_token") String bearerToken,
			@Valid @RequestBody BearerTokenForm bearerTokenForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		if (!bearerToken.equals(bearerTokenForm.getBearerToken())) {
			throw new UCException("INVALID_REQUEST", "uc.token.accessToken.invalid", HttpStatus.BAD_REQUEST);
		}
		
		PreAuthenticatedBearerTokenAuthentication preAuthenticatedAuthentication = new PreAuthenticatedBearerTokenAuthentication(
				 bearerTokenForm.getBearerToken(), bearerTokenForm.getUserId());
		
		return uCBearerTokenService.verifyToken(preAuthenticatedAuthentication);
	}

	@RequestMapping(value = "/tokens/bearer/{refresh_token}/refresh", method = RequestMethod.POST)
	public UCBearerToken refreshBearerToken(@PathVariable("refresh_token") String refreshToken,
			@Valid @RequestBody RefreshTokenForm tokenForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		UCBearerToken token = new UCBearerToken();
		token.setBearerToken(tokenForm.getAccessToken());
		token.setUserId(tokenForm.getUserId());
		token.setRefreshToken(refreshToken);
		return uCBearerTokenService.refreshToken(token);
	}

	@RequestMapping(value = "/bearer_tokens", method = RequestMethod.POST)
	public UCBearerToken getBearerTokens(@Valid @RequestBody UCAccount ucAccount, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new UCException("INVALID_REQUEST", bindingResult.getAllErrors().get(0).getDefaultMessage(),
					HttpStatus.BAD_REQUEST);
		}
		if (userService.validUserPassword(ucAccount)) {
			return uCBearerTokenService.createToken(ucAccount);
		}
		throw new UCException("UNAUTHORIZED", "uc.token.unauthorized", HttpStatus.UNAUTHORIZED);
	}
}
