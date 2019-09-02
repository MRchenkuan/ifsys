package com.gigold.pay.ifsys.aop.changeslog;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.ifsys.ResponseDto.InterFaceResponseDto;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.service.InterFaceChangesService;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import com.gigold.pay.ifsys.service.InterFaceService;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class ChangesLogAroundController extends BaseAspect {
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

	/**
	 * 返回时触发事件
	 * @param joinPoint
	 * @return
	 */
	@Around("controllerAspect()")
	public Object doAroundController(ProceedingJoinPoint joinPoint){
		String targetName = joinPoint.getTarget().getClass().getName();
		String event = ChangesUtil.getEvent(joinPoint);

		// 获取返回值
		Object returnVal = null;
		try {
			returnVal = joinPoint.proceed();
		}catch (Throwable throwable) {
			throwable.printStackTrace();
		}


		// 接口修改事件触发
		try {
			if ("com.gigold.pay.ifsys.controller.InterFaceController".equalsIgnoreCase(targetName)) {
				HttpSession session = (HttpSession) joinPoint.getArgs()[1];
				UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
				int userId = 0;

				// 更新时
				if ("onAdd".equals(event)) {
					try {
						if (user != null) userId = user.getId();
						// 记录线程session
						SessionTreadLocal.setUserId(userId);
						System.out.println(userId);
						// 能否确定接口信息实例
						if (returnVal instanceof InterFaceResponseDto) {
							InterFaceResponseDto interFaceResponseDto = (InterFaceResponseDto) returnVal;
							InterFaceInfo interfaceInfo = interFaceResponseDto.getInterFaceInfo();
							int ifId = interfaceInfo.getId();
							String ifName = interfaceInfo.getIfName();

							// 记录变更
							InterFaceChanges changes = new InterFaceChanges();
							changes.setOptionType("新增");
							changes.setIfId(ifId);
							changes.setUserId(userId);

							// 入库变更
							interFaceChangesService.recordChanges(changes);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return returnVal;
	}
}
