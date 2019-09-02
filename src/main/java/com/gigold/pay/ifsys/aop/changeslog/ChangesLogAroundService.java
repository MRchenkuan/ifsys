package com.gigold.pay.ifsys.aop.changeslog;

import com.gigold.pay.ifsys.RequestDto.InterFaceRequestDto;
import com.gigold.pay.ifsys.aop.notice.BaseNotice;
import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.*;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class ChangesLogAroundService extends BaseNotice {
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
	private WeChatNoticeService weChatNoticeService;

	@Autowired
	private UserInfoService userInfoService;
//	/**
//	 * 添加接口关注触发
//	 * @param joinPoint
//	 * @return
//	 */
//	@Around("serviceAspect()")
//	public Object addInterFaceLog(ProceedingJoinPoint joinPoint) {
//
//		// 获取返回值
//		Object returnVal = null;
//		try {
//			returnVal = joinPoint.proceed();
//		} catch (Throwable throwable) {
//			throwable.printStackTrace();
//		}
//
//		try {
//			// 获取类名
//			String targetName = joinPoint.getTarget().getClass().getName();
//			// 获取方法名
//			Signature signature = joinPoint.getSignature();
//			MethodSignature methodSignature = (MethodSignature) signature;
//			Method method = methodSignature.getMethod();
//			String methodName = method.getName();
//			// 监听特定类
//			List<String> includeClass = Collections.singletonList(
//					"com.gigold.pay.ifsys.service.InterFaceService"
//			);
//			if (!includeClass.contains(targetName)) return returnVal;
//
//
//			/**
//			 * 批量增加关注者事件
//			 */
//			if ("addInterFace".equalsIgnoreCase(methodName)) {
//				Object argsObj = joinPoint.getArgs()[0];
//				if(!(argsObj instanceof InterFaceInfo))return returnVal;
//				try {
//					// 获取当前线程操作者信息
//					int userId = SessionTreadLocal.getUserId();
//					UserInfo oper = userInfoService.getUser(userId);
//					if (oper == null) {
//						System.out.println("获取操作者信息失败,userId:" + userId);
//						return returnVal;
//					}
//					// 获取接口信息
//					InterFaceInfo interFaceInfo = (InterFaceInfo) argsObj;
//					// 获取老接口信息
//					int ifId = interFaceInfo.getId();
//					if(ifId>0){
//						InterFaceChanges interFaceChanges = new InterFaceChanges();
//						interFaceChanges.setUserId(userId);
//						interFaceChanges.setIfId(ifId);
//						interFaceChanges.setOptionType("新增");
//						// 记录变更
//						interFaceChangesService.recordChanges(interFaceChanges);
//					}
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		// 无条件返回
//		return returnVal;
//	}


}
