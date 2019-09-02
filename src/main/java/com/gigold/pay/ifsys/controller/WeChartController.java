package com.gigold.pay.ifsys.controller;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.cache.CacheService;
import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.core.exception.PendingException;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.framework.web.constant.SessionConstant;
import com.gigold.pay.ifsys.RequestDto.IfSysMockPageDto;
import com.gigold.pay.ifsys.RequestDto.VerifyInfoReqDto;
import com.gigold.pay.ifsys.RequestDto.WechatLoginReqDto;
import com.gigold.pay.ifsys.ResponseDto.*;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.InterFaceSysTem;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.bo.WxUserInfo;
import com.gigold.pay.ifsys.service.*;
import com.gigold.pay.ifsys.util.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/weChat/")
public class WeChartController extends BaseController {

	@Autowired
	WeChatService weChatService;
	@Autowired
	WxLoginService wxLoginService;
	@Autowired
	InterFaceService interFaceService;
	@Autowired
	InterFaceSysService interFaceSysService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	CacheService cacheService;
	@Autowired
	CaptchaService captchaService;

	@Value("${wechart.AppId:wxc4ea8fd4363886bd}")
	String appId;

	private static int cache_expire_time = 7200 ;// ms

	private static final String NAMESPACE_USER_INFO = "NAMESPACE_USER_INFO";
	private static final String NAMESPACE_WX_USER_INFO = "NAMESPACE_WX_USER_INFO";

	/**
	 * 微信鉴权接口
	 * @author chenkuan
     * @return
     */
	@RequestMapping(value = "/callBackModeAuthorized.do",method = RequestMethod.GET)
	public ResponseDto callBackModeAuthorized(@RequestParam String msg_signature,
										@RequestParam String timestamp,
										@RequestParam String nonce,
										@RequestParam String echostr,HttpServletResponse hsr) {
		ResponseDto reDto = new ResponseDto();
		PrintWriter writer;
		try {
			String echoStr = weChatService.callBackModeAuthorized(msg_signature,timestamp,nonce,echostr);
			if(StringUtil.isNotBlank(echoStr)){
				writer = hsr.getWriter();
				writer.write(echoStr);
				return null;
			}
			return null;
		}catch (Exception e){
			e.printStackTrace();
			return reDto;
		}
	}

	/**
	 * 接口列表获取接口
	 * @author chenkuan
	 * @return
	 */
	@RequestMapping(value = "/getInterfaceList.do")
	public @ResponseBody
	IfSysMockRspDto getInterfaceListByCondition (@RequestBody IfSysMockPageDto dto, HttpSession session) {
		IfSysMockRspDto reDto = new IfSysMockRspDto();
		// 翻页工具
		int curPageNum = dto.getPageNum();
		PageInfo<InterFaceInfo> pageInfo = null;
		PageHelper.startPage(curPageNum, Integer.parseInt(SystemPropertyConfigure.getProperty("sys.pageSize")));
		InterFaceInfo interFaceInfo = null;
		try {
			interFaceInfo = createBO(dto, InterFaceInfo.class);
		} catch (PendingException e) {
			debug("转换bo异常");
			e.printStackTrace();
			return reDto;
		}

		List<InterFaceInfo> list;
		try{
			// 获取用户信息
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);

			if(user == null || user.getId()<=0){
				reDto.setRspCd(WXCodeItem.NOT_AUTHORIZED);
				reDto.setRspInf("找不到用户信息");
				return reDto;
			}
			// 设置查询条件为当前用户
			interFaceInfo.setIfCreateBy(user.getId());
			// 获取查询结果
			list = interFaceService.getAllIfSys(user.getId(),interFaceInfo);
			for(InterFaceInfo eachIfInfo : list){
				// 加入重组的 mocklist,去掉一些无用信息
				List<Map> mockidlist  = interFaceService.getInterfaceMocksById(eachIfInfo.getId());
				eachIfInfo.setMockidList(mockidlist);
			}
			pageInfo = new PageInfo<>(list);
			reDto.setPageInfo(pageInfo);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		return reDto;
	}


	/**
	 * 接口列表获取接口
	 * @author chenkuan
	 * @return
	 */
	@RequestMapping(value = "/getFollowedIfList.do")
	public @ResponseBody
	IfSysMockRspDto getFollowedIfList (@RequestBody IfSysMockPageDto dto, HttpSession session) {
		IfSysMockRspDto reDto = new IfSysMockRspDto();
		// 翻页工具
		int curPageNum = dto.getPageNum();
		PageInfo<InterFaceInfo> pageInfo = null;
		PageHelper.startPage(curPageNum, Integer.parseInt(SystemPropertyConfigure.getProperty("sys.pageSize")));
		InterFaceInfo interFaceInfo = null;
		try {
			interFaceInfo = createBO(dto, InterFaceInfo.class);
		} catch (PendingException e) {
			debug("转换bo异常");
			e.printStackTrace();
			return reDto;
		}
		List<InterFaceInfo> list;
		try{
			// 获取用户信息
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);

			if(user == null || user.getId()<=0){
				reDto.setRspCd(WXCodeItem.NOT_AUTHORIZED);
				reDto.setRspInf("找不到用户信息");
				return reDto;
			}
			// 获取查询结果
			list = interFaceService.getFollowedInterfaces(user.getId(),interFaceInfo);
//			list = interFaceService.getFollowedInterfaces(23);


			System.out.println(list);
			pageInfo = new PageInfo<>(list);
			reDto.setPageInfo(pageInfo);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		return reDto;
	}

	/**
	 * 系统列表获取接口
	 * @author chenkuan
	 * @return
	 */
	@RequestMapping(value = "/getSystemInfo.do")
	public @ResponseBody
	InterFaceSysResponseDto getSystemInfo (@RequestBody Map<String,String> dto, HttpSession session) {
		InterFaceSysResponseDto reDto = new InterFaceSysResponseDto();
		// 参数校验
		if(!dto.containsKey("type")){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("type 参数未提交");
			return reDto;
		}
		String type = dto.get("type");
		System.out.println(type);

		try {
			List<InterFaceSysTem> interFaceSysTemList = new Page<>();
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			if(user==null || user.getId()<=0){
				reDto.setRspCd(SysCode.SYS_FAIL);
				reDto.setRspInf("用户身份无法确定");
				return reDto;
			}
			if ("followed".equals(type)) {
				interFaceSysTemList = interFaceSysService.getSysInfoByFollowed(user.getId());
//				interFaceSysTemList = interFaceSysService.getSysInfoByFollowed(23);
			}

			if ("designed".equals(type)) {
				interFaceSysTemList = interFaceSysService.getSysInfoByDesigned(user.getId());
			}

			if("all".equals(type)){
				interFaceSysTemList = interFaceSysService.getSysInfoByAllInterface();
			}

			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setSysList(interFaceSysTemList);
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("查询系统信息出现异常");
		}

		return reDto;
	}


	/**
	 * 通过成组的信息尝试登陆
	 * @param dto
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/wxLogin.do")
	public @ResponseBody
	WxUserInfoRspDto wxLogin (@RequestBody WechatLoginReqDto dto, HttpSession session) {
		WxUserInfoRspDto reDto = new WxUserInfoRspDto();
		// 首先确认会话
		WxUserInfo wxUserInfo = (WxUserInfo) session.getAttribute(SessionKey.WX_USER_INFO);
		UserInfo userInfo = (UserInfo) session.getAttribute(SessionKey.SYS_USER_INFO);
		String clientSessionId = (String) session.getAttribute(SessionKey.CLIENT_SSID);

		if(wxUserInfo==null){
			reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
			reDto.setRspInf("登陆失败");
			return reDto;
		}

		// 参数校验
		if(!dto.validateLoginParam()){
			reDto.setRspCd(CodeItem.INVAILD_PARM_FAILURE);
			reDto.setRspInf("登陆失败");
			return reDto;
		}

		// 登陆的方式
		String loginType = dto.getLoginType();


		// 直接登陆
		if("directly".equalsIgnoreCase(loginType)
				||(userInfo!=null
				&& userInfo.getId()>0
				&& StringUtil.isNotEmpty(userInfo.getEmail()))){
			if(userInfo==null||userInfo.getId()<=0){
				reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
				reDto.setRspInf("内部用户不存在");
				return reDto;
			}

			if(StringUtil.isEmpty(userInfo.getEmail())){
				reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
				reDto.setRspInf("用户邮箱未完善");
				return reDto;
			}
			// PC端登陆
			cacheService.put(clientSessionId,userInfo,cache_expire_time, TimeUnit.SECONDS, NAMESPACE_USER_INFO);
			reDto.setRspCd(SysCode.SUCCESS);
			return reDto;
		}


		// 通过邮箱登陆
		if("email".equalsIgnoreCase(loginType)){
			String userEmail = dto.getRegisterEmail();
			String userEmailCap = dto.getRegisterEmailCapture();
			String realCap = (String) session.getAttribute(SessionKey.EMAIL_CAPTURE);
			String realUserEmail = (String) session.getAttribute(SessionKey.LOGIN_EMAIL);
			// 验证邮箱验证码和邮箱本身
			if(StringUtil.equalsIgnoreCase(realCap,userEmailCap)
					&& StringUtil.equalsIgnoreCase(userEmail,realUserEmail)){
				try {

					UserInfo user = userInfoService.createUserWithWechatUserInfo(appId,realUserEmail, wxUserInfo);
					// PC端登陆
					cacheService.put(clientSessionId,user,cache_expire_time, TimeUnit.SECONDS, NAMESPACE_USER_INFO);
					reDto.setRspCd(SysCode.SUCCESS);
					return reDto;
				}catch (Exception e){
					e.printStackTrace();
				}
			}else{
				// 不通过则返回
				reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
				reDto.setRspInf("验证码错误!");
				return reDto;
			}
		}

		// 通过绑定账号登陆
		if("bindAcct".equalsIgnoreCase(loginType)){
			String gigoldAcct = dto.getBindAcct(); // 提交的吉高账号
			String gigoldAcctCap = dto.getBindAcctCapture(); // 提交的验证码
			String realAcctCap = (String) session.getAttribute(SessionKey.INNER_EMAIL_CAPTURE); // 正确的验证码
			String realGidAcct = (String) session.getAttribute(SessionKey.LOGIN_INNER_ACCT); // 对比的账号

			UserInfo innerUser = userInfoService.getUserByLoginName(realGidAcct);
			if(innerUser==null ||innerUser.getId()<=0){
				// 不通过则返回
				reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
				reDto.setRspInf("用户不存在!");
				return reDto;
			}
			// 验证真实验证码和吉高账户
			if(StringUtil.equalsIgnoreCase(realAcctCap,gigoldAcctCap)
					&& StringUtil.equalsIgnoreCase(gigoldAcct,realGidAcct)){
				// 关联吉高账号
				innerUser.setWxOpenId(wxUserInfo.getOpenid());
				innerUser.setAvtSrc(wxUserInfo.getHeadimgurl());
				innerUser.setCountry(wxUserInfo.getCountry());
				innerUser.setProvince(wxUserInfo.getProvince());
				innerUser.setCity(wxUserInfo.getCity());
				innerUser.setWxAppId(appId);
				int count = userInfoService.updateUserInfo(innerUser);
				// PC端登陆
				if(count>0){
					cacheService.put(clientSessionId,innerUser,cache_expire_time, TimeUnit.SECONDS, NAMESPACE_USER_INFO);
					reDto.setRspCd(SysCode.SUCCESS);
				}else{
					reDto.setRspCd(SysCode.SYS_FAIL);
					reDto.setRspCd("用户绑定过程失败");
				}

				return reDto;
			}else{
				// 不通过则返回
				reDto.setRspCd(WXCodeItem.LOGIN_FAILE);
				reDto.setRspInf("验证码错误!");
				return reDto;
			}

		}

		return reDto;

	}

	/**
	 * 通过扫码获取用户的微信用户信息和系统用户信息
	 * @param dto
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getWxUserStatus.do")
	public @ResponseBody
	WxUserInfoRspDto getWxUserStatus (@RequestBody WechatLoginReqDto dto, HttpSession session) {

		WxUserInfoRspDto reDto = new WxUserInfoRspDto();

		// 根据CODE取用户信息
		try {
			// 微信请求code
			String code = dto.getCode();
			String clientSessionId = dto.getSsid();

			// 获取微信用户信息
			WxUserInfo wxUserInfo = wxLoginService.getWxUserInfoByCode(code);
			// 存到session
			session.setAttribute(SessionKey.WX_USER_INFO,wxUserInfo);
			reDto.setWxUserInfo(wxUserInfo);

			// 获取系统用户信息
			String userOpenId = wxUserInfo.getOpenid();
			// 根据userOpenId查找用户
			UserInfo userInfo = userInfoService.getUserByWxOpenId(appId,userOpenId);
			// 存到session
			session.setAttribute(SessionKey.SYS_USER_INFO,userInfo);
			reDto.setSysUserInfo(userInfo);

			// clientSessionId
			session.setAttribute(SessionKey.CLIENT_SSID,clientSessionId);
			cacheService.put(clientSessionId,wxUserInfo,cache_expire_time, TimeUnit.SECONDS, NAMESPACE_WX_USER_INFO);


			reDto.setRspCd(SysCode.SUCCESS);
			return reDto;
		}catch (Exception e){
			reDto.setRspCd(WXCodeItem.INFO_GET_FAILE);
			reDto.setRspInf("获取用户信息失败");
			e.printStackTrace();
		}

		return reDto;
	}

	/**
	 * 邮箱验证接口
	 * @param dto
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/sendVerifyInformation.do")
	public @ResponseBody
	VerifyInfoRspDto sendVerifyInformation (@RequestBody VerifyInfoReqDto dto, HttpSession session) {
		VerifyInfoRspDto reDto = new VerifyInfoRspDto();
		String type = dto.getType();
		String target = dto.getTarget();
		if(StringUtil.isEmpty(type)){
			reDto.setRspCd(SysCode.PARA_NULL);
			reDto.setRspInf("type参数不能为空");
			return reDto;
		}

		// 验证邮箱
		if("email".equalsIgnoreCase(type)){
			String captcha = RandomStringUtils.randomNumeric(4);
			boolean flag = captchaService.sendEmailCaptcha(target,captcha);
			if(flag){
				session.setAttribute(SessionKey.LOGIN_EMAIL,target);
				session.setAttribute(SessionKey.EMAIL_CAPTURE,captcha);
				reDto.setRspCd(SysCode.SUCCESS);
				reDto.setTarget(target);
				reDto.setType("email");
				return reDto;
			}
		}

		//验证吉高账号
		if("gidAcct".equalsIgnoreCase(type)){
			String captcha = RandomStringUtils.randomNumeric(4);
			// 根据loginName 获取用户
			UserInfo user = userInfoService.getUserByLoginName(target);
			// 验证内部账号
			if(user == null || user.getId()<=0){
				reDto.setRspCd(SysCode.LIST_IS_NULL);
				reDto.setRspInf("内部账号不存在,请检查输入");
				return reDto;
			}
			String email = user.getEmail();

			boolean flag = captchaService.sendEmailCaptcha(email,captcha);

			if(flag && StringUtil.isNotEmpty(email)){
				session.setAttribute(SessionKey.LOGIN_INNER_ACCT,target);
				session.setAttribute(SessionKey.INNER_EMAIL_CAPTURE,captcha);
				reDto.setRspCd(SysCode.SUCCESS);
				reDto.setTarget(user.getEmail());
				reDto.setType("gidAcct");
				return reDto;
			}
		}

		reDto.setRspCd(SysCode.SYS_FAIL);
		reDto.setRspInf("邮件发送失败,请检查填写是否正确");
		return reDto;
	}

	/**
	 * pc登陆页轮训手机端登陆状态接口
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getWxLoginState.do")
	public @ResponseBody
	LoginStateRspDto sendVerifyInformation (HttpSession session) {
		LoginStateRspDto reDto = new LoginStateRspDto();
		String ssid = session.getId();
		// 返回SSID 提供给二维码产生
		reDto.setSsid(ssid);
		UserInfo userInfo = (UserInfo) cacheService.get(ssid,NAMESPACE_USER_INFO);
		WxUserInfo wxUserInfo = (WxUserInfo) cacheService.get(ssid,NAMESPACE_WX_USER_INFO);

		if(wxUserInfo!=null && StringUtil.isNotEmpty(wxUserInfo.getOpenid())){
			reDto.setWxUserInfo(wxUserInfo);
		}

		if(userInfo !=null && userInfo.getId()>0){
			session.setAttribute(Constant.LOGIN_KEY, userInfo);
			// 新版本登录
			session.setAttribute(SessionConstant.GIGOLD_AUTH, Constant.SESSION_VALUE_GIGOLD_AUTH);
			session.setAttribute(SessionConstant.GIGOLD_USR_NO, userInfo.getId());
			userInfo.setWxOpenId(null);
			userInfo.setWxAppId(null);
			reDto.setUserInfo(userInfo);
		}
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}
}
