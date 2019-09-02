package com.gigold.pay.ifsys.aop.notice;

import com.alibaba.druid.sql.visitor.functions.Function;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

public class BaseNotice extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	// Service层切点
	@Pointcut("@annotation(com.gigold.pay.ifsys.annoation.Notice)")
	public void serviceAspect() {
		debug("service 层通知");
	}

	// Controller层切点
	@Pointcut("@annotation(com.gigold.pay.ifsys.annoation.Notice)")
	public void controllerAspect() {
		debug("Controller 层通知");
	}


	/**
	 * 记录当前线程操作者ID
	 * @param session
     */
	public void setSessionUser(HttpSession session){
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		int userId = user.getId();
		// 记录线程session
		SessionTreadLocal.setUserId(userId);
	}

	/**
	 * 获取当前操作者用户
	 * @param session
	 * @return
     */
	public UserInfo getSessionUser(HttpSession session){
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if(user==null){
			debug("未设置SessionTreadLocal");
		}
		return user;
	}
}
