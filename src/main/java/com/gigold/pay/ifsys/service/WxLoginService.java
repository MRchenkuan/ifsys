package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.cache.CacheService;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.bo.WxUserInfo;
import com.gigold.pay.ifsys.dao.UserInfoDao;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.CoreHttpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WxLoginService extends AbstractService{

	@Value("${wechart.AppId:wxc4ea8fd4363886bd}")
	String appId;
	@Value("${wechart.AppSecret:fd2b5a1ea66c9da464da5483e1cf2a2c}")
	String appSecret;

	public static final String WeChat_NAMESPACE = "WXLOGIN";

	public static final String WeChat_KEY = "WXLOGIN_KEY";

	private static final String WxLogin_Access_Token= "https://api.weixin.qq.com/sns/oauth2/access_token?"; // appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

	private static final String WxLogin_UserInfo = "https://api.weixin.qq.com/sns/userinfo?"; // access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN

	@Autowired
	CacheService cacheService;

	@Autowired
	UserInfoDao userInfoDao;



	/**
	 * 获取会话令牌方法
	 * @return 返回会话令牌
     */
	private HashMap getAccessToken(String code) throws IOException { // 异常直接外抛,不需要继续执行
		String access_token="";
		String openid = "";


		StringBuilder bf  = new StringBuilder();
		bf.append(WxLogin_Access_Token)
				.append("appid=").append(appId)
				.append("&secret=").append(appSecret)
				.append("&code=").append(code)
				.append("&grant_type=").append("authorization_code");
		System.out.println("获取AccessToken URL:"+bf.toString());
		String res = CoreHttpUtils.get(bf.toString(),null);
		System.out.println("结果:"+res);
		//解析
		if(res.indexOf("access_token")>0 && res.indexOf("openid")>0){
			Gson gs = new Gson();
			HashMap mp = gs.fromJson(res, HashMap.class);
			access_token= (String) mp.get("access_token");
			openid = (String) mp.get("openid");
			System.out.println("access_token:"+access_token);
			System.out.println("openid:"+openid);
			return mp;
		}else{
			throw new IOException("access_token获取失败,\naccess_token:"+access_token+";\nopenid:"+openid+"\n");
		}

	}

	/**
     * 通过CODE获取微信用户的信息
	 * @param code
     * @return
     */
	public WxUserInfo getWxUserInfoByCode(String code) throws IOException {
		WxUserInfo wxUserInfo = new WxUserInfo();
		String access_token,openid;

		// 获取用户access_token 和openid
		HashMap mp = getAccessToken(code);
		access_token= (String) mp.get("access_token");
		openid = (String) mp.get("openid");

		// 获取用户基本信息
		StringBuilder bf  = new StringBuilder();
		bf.append(WxLogin_UserInfo)
				.append("access_token=").append(access_token)
				.append("&openid=").append(openid)
				.append("&lang=").append("zh_CN");
		System.out.println("获取用户信息URL:"+bf.toString());
		String res = CoreHttpUtils.get(bf.toString(),null);
		System.out.println("结果:"+res);
		Gson gs = new Gson();
		wxUserInfo = gs.fromJson(res, WxUserInfo.class);
		return wxUserInfo;
	}
}
