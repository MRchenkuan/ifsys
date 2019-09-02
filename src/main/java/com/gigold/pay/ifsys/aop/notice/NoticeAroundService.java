package com.gigold.pay.ifsys.aop.notice;

import com.gigold.pay.ifsys.RequestDto.InterFaceInvokersReqDto;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.*;
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
public class NoticeAroundService extends BaseNotice {
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
	/**
	 * 添加接口关注触发
	 * @param joinPoint
	 * @return
	 */
	@Around("serviceAspect()")
	public Object addInvokersNotice(ProceedingJoinPoint joinPoint) {

		// 获取返回值
		Object returnVal = null;
		try {
			returnVal = joinPoint.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		try {
			// 获取类名
			String targetName = joinPoint.getTarget().getClass().getName();

			// 获取方法名
			Signature signature = joinPoint.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			String methodName = method.getName();

			// 监听特定类
			List<String> includeClass = Collections.singletonList(
					"com.gigold.pay.ifsys.service.InterFaceInvokerService"
			);
			if (!includeClass.contains(targetName)) return returnVal;


			/**
			 * 批量增加关注者事件
			 */
			if ("addInterFaceInvokers".equalsIgnoreCase(methodName)) {
				long ifId = (long) joinPoint.getArgs()[0];
				List uids = (List) joinPoint.getArgs()[1];
				String remark = String.valueOf(joinPoint.getArgs()[2]);

				// 获取当前线程操作者信息
				int userId = SessionTreadLocal.getUserId();
				UserInfo oper = userInfoService.getUser(userId);
				if (oper == null) {
					System.out.println("获取操作者信息失败,userId:" + userId);
					return returnVal;
				}

				try {
					// 获取用户信息列表
					List<UserInfo> recivers = new ArrayList<>();
					if (returnVal instanceof List)
						for (Object invoker : (List) returnVal) {
							int uid = ((InterFaceInvoker) invoker).getuId();
							UserInfo reciver = userInfoService.getUser(uid);
							recivers.add(reciver);
						}

					// 获取接口完整信息
					InterFaceInfo ifInfo = new InterFaceInfo();
					ifInfo.setId(((int) ifId));
					ifInfo = interFaceService.getInterFaceById(ifInfo);

					//发送邮件
					noticeMailSendService.sendInvokerNotice(ifInfo, oper, recivers, remark);
					// 发送微信
					weChatNoticeService.sendInvokerNotice(ifInfo, oper, recivers, remark);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/**
			 * 主动关注接口事件
			 */
			if ("followInterface".equalsIgnoreCase(methodName)) {
				int uid = (int) joinPoint.getArgs()[0];
				int ifId = (int) joinPoint.getArgs()[1];
				String remark = String.valueOf(joinPoint.getArgs()[2]);

				try {
					// 获取发件人
					UserInfo follower = userInfoService.getUser(uid);
					// 获取关注结果

					// 获取接口完整信息
					InterFaceInfo ifInfo = new InterFaceInfo();
					ifInfo.setId((ifId));
					ifInfo = interFaceService.getInterFaceById(ifInfo);
					// 获取接口作者信息
					UserInfo author = userInfoService.getUser(ifInfo.getIfCreateBy());

					// 发送邮件
					noticeMailSendService.sendfollowedNotice(ifInfo, follower, author, remark);
					// 发送微信
					weChatNoticeService.sendfollowedNotice(ifInfo, follower, author, remark);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		// 无条件返回
		return returnVal;
	}


}
