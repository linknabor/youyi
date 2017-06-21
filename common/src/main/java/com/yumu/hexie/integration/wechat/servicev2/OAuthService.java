package com.yumu.hexie.integration.wechat.servicev2;


import com.yumu.hexie.integration.wechat.entity.AccessTokenOAuth;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.entity.user.UserWeiXin;
import com.yumu.hexie.integration.wechat.util.WeixinUtilV2;

/**
 * oAuth服务
 */
public class OAuthService {
	/**
	 * 通过oauth获取用户详细信息
	 */
	public static String GET_USER_INFO_OAUTH = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/**
	 * 获取oauth网页认证的token
	 */
	public static String GET_ACCESS_TOKEN_OAUTH = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	/**
	 * 获取Access_Token（oAuth认证,此access_token与基础支持的access_token不同）
	 * 
	 * @param code
	 *            用户授权后得到的code
	 * @return AccessTokenOAuth对象
	 */
	private static AccessTokenOAuth getOAuthAccessToken(String code, String appId, String appSecret, String accessToken) {
		String url = GET_ACCESS_TOKEN_OAUTH.replace("APPID", appId).replace("SECRET", appSecret)
				.replace("CODE", code);
		WechatResponse jsonObject = WeixinUtilV2.httpsRequest(url, "POST", null, accessToken);
		AccessTokenOAuth accessTokenOAuth = null;
		if (null != jsonObject&&jsonObject.getErrcode() == 0) {
			accessTokenOAuth = new AccessTokenOAuth();
			accessTokenOAuth.setAccessToken(jsonObject.getAccess_token());
			accessTokenOAuth.setExpiresIn(jsonObject.getExpires_in());
			accessTokenOAuth.setRefreshToken(jsonObject.getRefresh_token());
			accessTokenOAuth.setOpenid(jsonObject.getOpenid());
			accessTokenOAuth.setScope(jsonObject.getScope());
		}
		return accessTokenOAuth;
	}

	/**
	 * 通过oauth获取用户详细信息
	 * 
	 * @param token
	 * @param openid
	 * @return UserWeiXin对象
	 */
	public static UserWeiXin getUserInfoOauth(String code, String appId, String appSecret, String accessToken) {
		UserWeiXin user = null;
		AccessTokenOAuth auth = getOAuthAccessToken(code, appId, appSecret, accessToken);
		if (auth != null) {
			String url = GET_USER_INFO_OAUTH.replace("ACCESS_TOKEN", auth.getAccessToken())
					.replace("OPENID", auth.getOpenid());
			WechatResponse jsonObject = WeixinUtilV2.httpsRequest(url, "POST", null, accessToken);
			if (null != jsonObject&&jsonObject.getErrcode() == 0){
				user = new UserWeiXin();
				
				user.setOpenid(jsonObject.getOpenid());
				user.setNickname(jsonObject.getNickname());
				user.setSex(jsonObject.getSex());
				user.setCity(jsonObject.getCity());
				user.setCountry(jsonObject.getCountry());
				user.setProvince(jsonObject.getProvince());
				user.setLanguage(jsonObject.getLanguage());
				user.setPrivilege(jsonObject.getPrivilege());
				user.setHeadimgurl(jsonObject.getHeadimgurl());
			}
		}
		return user;
	}
}
