package com.gigold.pay.ifsys.aop.notice;

import com.gigold.pay.ifsys.RequestDto.InterFaceInvokersReqDto;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class NoticeAroundController extends BaseNotice {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Autowired
	private InterFaceChangesService interFaceChangesService;
	@Autowired
	private InterFaceService interFaceService;
	@Autowired
	private InterFaceFieldService interFaceFieldService;
	@Autowired
	private ReturnCodeService returnCodeService;

	@Autowired
	private NoticeMailSendService noticeMailSendService;

	@Autowired
	private UserInfoService userInfoService;
//	/**
//	 * 添加接口关注触发
//	 * @param joinPoint
//	 * @return
//	 */
//	@Around("controllerAspect()")
//	public Object addInvokersNotice(ProceedingJoinPoint joinPoint) {
//
//		// 获取返回值
//		Object returnVal = null;
//		try {
//			returnVal = joinPoint.proceed();
//		}catch (Throwable throwable) {
//			throwable.printStackTrace();
//		}
//
//		// 监听特定类
//		List<String> includeClass = Collections.singletonList(
//				"com.gigold.pay.ifsys.controller.InterFaceInvokerController"
//		);
//		String targetName = joinPoint.getTarget().getClass().getName();
//
//		if(includeClass.contains(targetName)){
//			Object argsObj = joinPoint.getArgs()[0];
//			HttpSession session = (HttpSession) joinPoint.getArgs()[1];
//			// 获取当前线程操作者的信息
//			UserInfo oper = getSessionUser(session);
//
//			if(!(argsObj instanceof InterFaceInvokersReqDto))return returnVal;
//
//			try {
//				// 获取用户id
//				List<Integer> uids = ((InterFaceInvokersReqDto) argsObj).getuIds();
//				// 获取接口ID
//				long ifId = ((InterFaceInvokersReqDto) argsObj).getIfId();
//				// 获取用户信息列表
//				List<UserInfo> recivers = new ArrayList<>();
//				for (int uid : uids) {
//					UserInfo reciver = userInfoService.getUser(uid);
//					recivers.add(reciver);
//				}
//				// 获取接口完整信息
//				InterFaceInfo ifInfo = new InterFaceInfo();
//				ifInfo.setId(((int) ifId));
//				ifInfo = interFaceService.getInterFaceById(ifInfo);
//				// 获取评论信息
//				String remark = ((InterFaceInvokersReqDto) argsObj).getRemarks();
//
//				noticeMailSendService.sendInvokerNotice(ifInfo, oper, recivers,remark);
//			}catch (Exception e){
//				e.printStackTrace();
//			}
//
//		}
//
//		return returnVal;
//	}
//

}
