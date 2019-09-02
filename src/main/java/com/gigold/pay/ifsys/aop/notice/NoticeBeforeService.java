package com.gigold.pay.ifsys.aop.notice;

import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.RequestDto.InterFaceInvokersReqDto;
import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.*;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gigold.pay.ifsys.service.InterFaceChangesService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class NoticeBeforeService extends BaseNotice {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Autowired
	private InterFaceChangesService interFaceChangesService;
	@Autowired
	private InterFaceService interFaceService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private NoticeMailSendService noticeMailSendService;
	@Autowired
	private InterFaceInvokerService interFaceInvokerService;
	@Autowired
	WeChatNoticeService weChatNoticeService;

	/**
	 * 变更通知
	 */
	@Before("serviceAspect()")
	public void changesNotice(JoinPoint joinPoint) {
		String targetName = joinPoint.getTarget().getClass().getName();

		/**
		 * 变更服务中
		 */
		if("com.gigold.pay.ifsys.service.InterFaceChangesService".equalsIgnoreCase(targetName)){
			debug("监听到事件");
			Object argsObj = joinPoint.getArgs()[0];
			int ifId = 0,userId = 0;
			List<InterFaceChanges> changesList = null;
			// 批量变更的通知
			if(argsObj instanceof List){
				debug("确定是批量变更");
				changesList = (List<InterFaceChanges>) argsObj;
				try {
					ifId = changesList.get(0).getIfId();
					userId = changesList.get(0).getUserId();
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			// 单个变更的通知
			if(argsObj instanceof InterFaceChanges){
				debug("确定是单个变更");
				InterFaceChanges theChanges = ((InterFaceChanges) argsObj);
				changesList = new ArrayList<>();
				changesList.add(theChanges);
				try {
					ifId = theChanges.getIfId();
					userId = theChanges.getUserId();
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			if(userId>0 && ifId>0 && changesList.size()>0) {
				// 获取接口信息和操作用户信息
				UserInfo oper = userInfoService.getUser(userId);
				InterFaceInfo interFaceInfo = new InterFaceInfo();
				interFaceInfo.setId(ifId);
				interFaceInfo = interFaceService.getInterFaceById(interFaceInfo);
				List<InterFaceInvoker> invokerList = interFaceInvokerService.getInvokerListByIfId(ifId);

				// 获取关注列表
				List<UserInfo> recivers = new ArrayList<>();
				for (InterFaceInvoker invoker : invokerList) {
					UserInfo _user = userInfoService.getUser(invoker.getuId());
					recivers.add(_user);
				}
				// 将作者加入列表
				UserInfo _author = userInfoService.getUser(interFaceInfo.getIfCreateBy());
				recivers.add(_author);
				// 发送变更通知邮件
				noticeMailSendService.sendChangesNotice(oper, recivers, interFaceInfo, changesList);
				// 发送微信
				weChatNoticeService.sendChangesNotice(oper, recivers, interFaceInfo, changesList);
			}
		}

	}

}
