package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.cache.CacheService;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.bo.WxUserInfo;
import com.gigold.pay.ifsys.dao.UserInfoDao;
import com.gigold.pay.ifsys.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.aspectj.bridge.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.gigold.pay.ifsys.util.CoreHttpUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeChatService extends AbstractService{

	@Value("${wechart.token:im0FeGNaAAWaej1O9}")
	String token;
	@Value("${wechart.EncodingAESKey:66NZDDlSN37JO0bWqfCLe9FIysBewaHfFMSVO5Uu95d}")
	String EncodingAESKey;
	@Value("${wechart.CorpID:wxfd7cac6fd0fe13fe}")
	String CorpID;
	@Value("${wechart.corpsecret:PvMMQ9n475NpdoN7gzc2d3AqRbeeW8DVNy9WZ_wxVq0vrb6JDKhKkNF_zqJs2kFh}")
	String corpsecret;

	@Value("${wechart.AppId:wxc4ea8fd4363886bd}")
	String appId;
	@Value("${wechart.AppSecret:fd2b5a1ea66c9da464da5483e1cf2a2c}")
	String appSecret;

	public static final String WeChat_NAMESPACE = "WECHAT";

	public static final String WeChat_KEY = "WECHATKEY";

	private static final String WeChat_Token_Url= "https://qyapi.weixin.qq.com/cgi-bin/gettoken?";

	private static final String WeChat_SendMsg_Url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";

	private static final String Wechat_UserInfo_Url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?";

	private static final String Wechat_Authorize_Url = "https://open.weixin.qq.com/connect/oauth2/authorize?";

	@Autowired
	CacheService cacheService;

	@Autowired
	UserInfoDao userInfoDao;
	/**
	 * 获取会话用户
	 * @param session
	 * @return
     */
	public UserInfo getUserInfo(String code,HttpSession session) {
		UserInfo user = null;
		try {
			// 从会话获取用户
			user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			System.out.println("user"+user);
		}catch (Exception e){
			e.printStackTrace();
		}

		if(user==null || user.getId()<=0){
			// 若用户不存在,则重新获取用户
			try{
				String access_token=getAccessToken();
				System.out.println("access_token"+access_token);
				String userId = getWxUserId(code,access_token);
				// 此userid是微信端的userid,对应的是后台的loginkey
				user = userInfoDao.getUserByLoginKey(userId);
				if(user == null || user.getId()<=0){
					return null;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * 从微信获取userId
	 * @param code
	 * @param access_token
     * @return
     */
	public String getWxUserId(String code, String access_token){
		StringBuilder bf =  new StringBuilder();
		bf.append(Wechat_UserInfo_Url).append("access_token=").append(access_token).append("&code=").append(code);
		try {
			System.out.println("req:"+bf.toString());
			String res = CoreHttpUtils.get(bf.toString(),null);
			System.out.println("res:"+res);
			//解析
			if(res.indexOf("UserId")>0){
				Gson gs = new Gson();
				HashMap mp = gs.fromJson(res, HashMap.class);
				String userId = (String) mp.get("UserId");
				System.out.println("userId:"+userId);
				return userId;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 回调模式启用接口
	 * @param msg_signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
     * @return
     */
	public String callBackModeAuthorized(String msg_signature, String timestamp, String nonce, String echostr){

		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, EncodingAESKey, CorpID);
			String sEchoStr; //需要返回的明文
			sEchoStr = wxcpt.VerifyURL(msg_signature, timestamp,
					nonce, echostr);
			System.out.println("verifyurl echostr: " + sEchoStr);
			// 验证URL成功，将sEchoStr返回
			return echostr;
		} catch (Exception e) {
			//验证URL失败，错误原因请查看异常
			e.printStackTrace();
		}


		System.out.println(msg_signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);
		return "";
	}

	/**
	 * 向目标用户发送微信消息
	 * @param msgContent
	 * @param target
     */
	public void sendWeChatMsg(String msgContent,String target){
		System.out.println("开始发送微信:"+msgContent);
		try{
			//从缓存获取令牌
			String access_token = getAccessToken();
			//请求出去
			String sendMsgUrl = WeChat_SendMsg_Url+access_token;
			System.out.println("sendMsgUrl:"+sendMsgUrl);
			String resSend=CoreHttpUtils.post(sendMsgUrl, createTextMap(msgContent ,target));
			System.out.println("发送结果:"+resSend);
		}catch(Exception ex){
			warn("发送:【"+msgContent+"】失败！", ex);
		}
	}

	/**
	 * 获取会话令牌方法
	 * @return 返回会话令牌
     */
	private String getAccessToken() throws IOException { // 异常直接外抛,不需要继续执行
		String access_token="";
		//从缓存获取令牌
		access_token=(String) cacheService.get(WeChat_KEY, WeChat_NAMESPACE);
		System.out.println("access_token:"+access_token);
		if( StringUtil.isEmpty(access_token)){
			StringBuilder bf  = new StringBuilder();
			bf.append(WeChat_Token_Url).append("corpid=").append(CorpID).append("&corpsecret=").append(corpsecret);

			System.out.println("URL:"+bf.toString());
			String res = CoreHttpUtils.get(bf.toString(),null);
			System.out.println("结果:"+res);
			//解析
			if(res.indexOf("access_token")>0){
				Gson gs = new Gson();
				HashMap mp = gs.fromJson(res, HashMap.class);
				access_token= (String) mp.get("access_token");
				System.out.println("access_token:"+access_token);
				int time = ((Double)mp.get("expires_in")).intValue();
				cacheService.put(WeChat_KEY, access_token,time, TimeUnit.SECONDS, WeChat_NAMESPACE);
			}

		}

		return access_token;
	}


	/**
	 * 向目标发送图文消息
	 * @param articles
	 * @param target
     */
	public void sendWeChatMsg(HashMap articles,String target){
		try{
			//从缓存获取令牌
			String wechatKey=(String) cacheService.get(WeChat_KEY, WeChat_NAMESPACE);
			System.out.println("wechatKey:"+wechatKey);
			if( StringUtil.isEmpty(wechatKey)){
				StringBuilder bf  = new StringBuilder();
				bf.append(WeChat_Token_Url).append("corpid=").append(CorpID).append("&corpsecret=").append(corpsecret);

				System.out.println("URL:"+bf.toString());
				String res = CoreHttpUtils.get(bf.toString(),null);
				System.out.println("结果:"+res);
				//解析
				if(res.indexOf("access_token")>0){
					Gson gs = new Gson();
					HashMap mp = gs.fromJson(res, HashMap.class);
					wechatKey= (String) mp.get("access_token");
					System.out.println("token:"+wechatKey);
					int time = ((Double)mp.get("expires_in")).intValue();
					cacheService.put(WeChat_KEY, wechatKey,time, TimeUnit.SECONDS, WeChat_NAMESPACE);
				}

			}
			//请求出去
			String sendMsgUrl = WeChat_SendMsg_Url+wechatKey;
			System.out.println("sendMsgUrl:"+sendMsgUrl);
			String resSend=CoreHttpUtils.post(sendMsgUrl, createNewsMap(articles ,target));
			System.out.println("发送结果:"+resSend);
		}catch(Exception ex){
			warn("发送:【图文消息】失败！", ex);
		}
	}

	/**
	 * 向目标发送图文消息
	 * @param title
	 * @param description
	 * @param url
	 * @param picurl
     * @param target
     */
	public void sendWeChatMsg(String title, String description ,String url , String picurl ,String target){
		try{
			HashMap<String,String> articles = new HashMap<>();
			articles.put("title",title);
			articles.put("description",description);
			articles.put("url",url);
			articles.put("picurl",picurl);
			sendWeChatMsg(articles,target);
		}catch(Exception ex){
			warn("发送:【图文消息】失败！", ex);
		}
	}

	/**
	 * 向所有用户发送微信消息
	 * @param msgContent;
     */
	public void sendWeChatMsg(String msgContent) {
		sendWeChatMsg(msgContent,"@all");
	}

	/**
	 * 向所有用户发送图文消息
	 * @param title
	 * @param Description
	 * @param URL
     * @param PIC_URL
     */
	public void sendWeChatMsg(String title, String Description ,String URL , String PIC_URL) {
		sendWeChatMsg(title, Description ,URL , PIC_URL ,"@all");
	}

	/**
	 * 文本消息
	 * @param content;
	 * @param target;
     * @return ;
     */
	private String createTextMap(String content ,String target){
		HashMap<String, Object> sms2 = new HashMap<String, Object>();
		sms2.put("touser","@"+target);//目标用户
		sms2.put("toparty", "@all");//目标组
		sms2.put("totag", "@all");
		sms2.put("totag", "@all");
		sms2.put("msgtype","text");//消息类型
		sms2.put("agentid",2); //消息发送者ID
		HashMap<String, Object> sms3 = new HashMap<String, Object>();
		sms3.put("content",content);
		sms2.put("text", sms3);
		sms2.put("safe","0");
		String msg = (new GsonBuilder().disableHtmlEscaping().create()).toJson(sms2);
		System.out.println(msg);
		return msg;
	}

	/**
	 * 图文消息
	 * @param content;
	 * @param target;
	 * @return ;
	 */
	private String createNewsMap(HashMap content , String target){
		HashMap<String, Object> sms2 = new HashMap<String, Object>();
		sms2.put("touser",target);//目标用户
		sms2.put("toparty", "@all");//目标组
		sms2.put("totag", "@all");
		sms2.put("totag", "@all");
		sms2.put("msgtype","news");//消息类型
		sms2.put("agentid",2); //消息发送者ID
		HashMap<String, Object> sms3 = new HashMap<String, Object>();
		List<Map> articles = new ArrayList<>();
		articles.add(content);
		sms3.put("articles",articles);
		sms2.put("news", sms3);
		String msg = (new GsonBuilder().disableHtmlEscaping().create()).toJson(sms2);
		System.out.println(msg);
		return msg;
	}

	/**
	 * 包装微信打开目标地址
	 * @param targetPageUrl 目标地址
	 * @param state 参数
     * @return 实际地址
     */
	public String wrapWeChatOpenUrl(String targetPageUrl,String state) throws UnsupportedEncodingException {
		return Wechat_Authorize_Url+
				"appid="+CorpID+
				"&redirect_uri="+targetPageUrl+
				"&response_type=code" +
				"&scope=snsapi_base" +
				"&state=" + state +
				"#wechat_redirect";
	}

	/**
	 * 包装微信打开目标地址
	 * @param targetPageUrl 目标地址
	 * @param params 列表型参数
	 * @return 实际地址
	 */
	public String wrapWeChatOpenUrl(String targetPageUrl,Map<String,String> params) throws UnsupportedEncodingException {
		String state = "";
		for(String paramKey : params.keySet()){
			String paramVal = params.get(paramKey);
			state += "-"+paramKey+"::"+paramVal;
		}
		return Wechat_Authorize_Url+
				"appid="+CorpID+
				"&redirect_uri="+targetPageUrl+
				"&response_type=code" +
				"&scope=snsapi_base" +
				"&state=" + state +
				"#wechat_redirect";
	}
}
