package com.gigold.pay.ifsys.aop.changeslog;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.ifsys.RequestDto.InterFaceDetailReqDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceRequestDto;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.service.InterFaceChangesService;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import com.gigold.pay.ifsys.service.InterFaceService;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class ChangesLogBeforeController extends BaseAspect {
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
	 * 记录线程操作者
	 * @param joinPoint 切点
	 */
	@Before("controllerAspect()")
	public void setThreadLocal(JoinPoint joinPoint) {
		String targetName = joinPoint.getTarget().getClass().getName();
		/**
		 * 接口/接口参数/返回码修改时记录session
		 */
		System.out.println("当前的 targetName 为:"+targetName);
		if("com.gigold.pay.ifsys.controller.InterFaceFieldController".equalsIgnoreCase(targetName)
				|| "com.gigold.pay.ifsys.controller.ReturnCodeController".equalsIgnoreCase(targetName)
				|| "com.gigold.pay.ifsys.controller.InterFaceController".equalsIgnoreCase(targetName)
				){
			HttpSession session = (HttpSession) joinPoint.getArgs()[1];
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			int userId = user!=null?user.getId():0;
			// 记录线程session
			SessionTreadLocal.setUserId(userId);
		}
	}

	/**
	 * 接口修改事件触发
	 */
	@Before("controllerAspect()")
	public void doBeforeUpdate(JoinPoint joinPoint) {
		if(joinPoint.getArgs().length<2)return;
		Object argsObj = joinPoint.getArgs()[0];
		if(!(argsObj instanceof InterFaceRequestDto))return;
		String event = ChangesUtil.getEvent(joinPoint);
		if(!"onUpdate".equals(event))return;

		HttpSession session = (HttpSession) joinPoint.getArgs()[1];
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		InterFaceInfo newInterfaceInfo = new InterFaceInfo();
		InterFaceInfo oldInterfaceInfo = new InterFaceInfo();
		try {
			int userId = 0;
			if (user != null) userId = user.getId();
			// 获取新接口信息
			newInterfaceInfo = ((InterFaceRequestDto) argsObj).getInterFaceInfo();
			int ifId = newInterfaceInfo.getId();
			// 获取老接口信息
			oldInterfaceInfo.setId(ifId);
			oldInterfaceInfo = interFaceService.getInterFaceById(oldInterfaceInfo);

			List<InterFaceChanges> changesList = null;
			// 比较新老接口变更
			if (oldInterfaceInfo != null) {
				changesList = ChangesUtil.compareInterFaceChanges(oldInterfaceInfo, newInterfaceInfo, userId,ifId);
			}

			// 记录变更
			if (changesList != null && changesList.size() > 0)
				interFaceChangesService.recordChanges(changesList);

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Before("controllerAspect()")
	public void doBeforeDelete(JoinPoint joinPoint) {
		if(joinPoint.getArgs().length<2)return;
		Object argsObj = joinPoint.getArgs()[0];
		if(!(argsObj instanceof InterFaceRequestDto))return;
		String event = ChangesUtil.getEvent(joinPoint);
		if(!"onDelete".equals(event))return;

		HttpSession session;
		UserInfo user=null;
		try {
			session = (HttpSession) joinPoint.getArgs()[1];
			user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		}catch (Exception e){
			debug("找不到session参数");
		}
		InterFaceInfo newInterfaceInfo = new InterFaceInfo();
		try {
			int userId = 0;
			if (user != null) userId = user.getId();
			// 获取新接口信息
			newInterfaceInfo = ((InterFaceRequestDto) argsObj).getInterFaceInfo();

			// 获取老接口信息
			int ifId = newInterfaceInfo.getId();
			InterFaceInfo interFaceInfo = interFaceService.getInterFaceById(newInterfaceInfo);
			String ifName = interFaceInfo.getIfName();
			if(ifId>0){
				List<InterFaceChanges> changesList = new ArrayList<>();
				InterFaceChanges interFaceChanges = new InterFaceChanges();
				interFaceChanges.setUserId(userId);
				interFaceChanges.setIfId(ifId);
				interFaceChanges.setOptionType("删除");
				changesList.add(interFaceChanges);
				// 记录变更
				interFaceChangesService.recordChanges(changesList);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
