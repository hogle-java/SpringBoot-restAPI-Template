package com.handcoding.restapi.api.oauth2;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.handcoding.restapi.api.EndpointAPI;
import com.handcoding.restapi.component.CommonComponent;
import com.handcoding.restapi.domain.OAuth2CheckTokenVO;
import com.handcoding.restapi.domain.OAuth2TokenVO;
import com.handcoding.restapi.domain.OAuth2VO;

import retrofit2.Call;
import retrofit2.Response;

/**
 * OAuth2.0 API
 * @author 이승환
 * @version 2018.02.02 v1.0
 */
@Component
public class OAuth2API extends EndpointAPI<OAuth2>{
	
	@Autowired
	private CommonComponent common;
	
	@Override
	protected OAuth2 create() {
		return retrofit.create(OAuth2.class);
	}
	
	/**
	 * AccessToken 생성
	 * @param oAuth2VO
	 * @return
	 */
	public OAuth2TokenVO getAccessToken(OAuth2VO oAuth2VO) {
		String authorization = "Basic ";
		// Authorization 헤더 client_id:cilent_secret base64인코딩
		byte[] base64 = (oAuth2VO.getClient_id()+":"+oAuth2VO.getClient_secret()).getBytes();
		authorization += Base64.getEncoder().encodeToString(base64);
		Call<OAuth2TokenVO> call = this.create().getAccessToken(authorization, oAuth2VO.getGrant_type());
		try {
			Response<OAuth2TokenVO> resopnse = call.execute();
			if(resopnse.isSuccessful()) {
				OAuth2TokenVO oAuth2TokenVO = resopnse.body();
				common.getHandlerToken().setAuthorization(authorization, oAuth2TokenVO.getAccess_token());
				return oAuth2TokenVO;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * AccessToken 체크
	 * @param token
	 * @return
	 */
	public OAuth2CheckTokenVO checkAccessToken(String accessToken) {
		String authorization = common.getHandlerToken().getAuthorization(accessToken);
		Call<OAuth2CheckTokenVO> call = this.create().checkAccessToken(authorization, accessToken);
		try {
			Response<OAuth2CheckTokenVO> resopnse = call.execute();
			if(resopnse.isSuccessful()) {
				return resopnse.body();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
