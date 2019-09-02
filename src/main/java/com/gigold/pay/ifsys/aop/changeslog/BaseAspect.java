package com.gigold.pay.ifsys.aop.changeslog;

import com.gigold.pay.framework.service.AbstractService;
import org.aspectj.lang.annotation.Pointcut;

import java.text.SimpleDateFormat;

public class BaseAspect extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	// Service层切点
	@Pointcut("@annotation(com.gigold.pay.ifsys.annoation.ChangesLog)")
	public void serviceAspect() {
	}

	// Controller层切点
	@Pointcut("@annotation(com.gigold.pay.ifsys.annoation.ChangesLog)")
	public void controllerAspect() {
		debug("进入controller层切点");
	}

	public final static SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat hms = new SimpleDateFormat("HHmmss");
//	/**
//	 *
//	 * Title: getServiceMethodDescription<br/>
//	 * 获取方法注解的描述信息: <br/>
//	 *
//	 * @author xiebin
//	 * @date 2015年10月21日下午2:16:28
//	 *
//	 * @param joinPoint
//	 * @return
//	 */
//	public String getServiceMethodDescription(JoinPoint joinPoint) {
//		String targetName = joinPoint.getTarget().getClass().getName();
//		String methodName = joinPoint.getSignature().getName();
//		Object[] arguments = joinPoint.getArgs();
//		Class<?> targetClass = null;
//		try {
//			targetClass = Class.forName(targetName);
//		} catch (ClassNotFoundException e) {
//			debug("找不到存在的类名");
//		}
//		Method[] methods = new Method[0];
//		if(targetClass!=null){
//		methods = targetClass.getMethods();
//		}
//		String description = "";
//		for (Method method : methods) {
//			if (method.getName().equals(methodName)) {
//				Class<?>[] clazzs = method.getParameterTypes();
//				if (clazzs.length == arguments.length) {
//					//description = method.getAnnotation(OperatorServiceLog.class).description();
//					break;
//				}
//			}
//		}
//		return description;
//	}
}
