package com.gigold.pay.ifsys.aop.notice;

import com.gigold.pay.ifsys.service.InterFaceChangesService;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import com.gigold.pay.ifsys.service.InterFaceService;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class NoticeBeforeController extends BaseNotice {
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
	 * 设置线程操作者
	 */
	@Before("controllerAspect()")
	public void setSessionTreadLocal(JoinPoint joinPoint) {
		List<String> includeClass = Arrays.asList(
				"com.gigold.pay.ifsys.controller.InterFaceInvokerController",
				"com.gigold.pay.ifsys.controller.InterFaceController",
				"com.gigold.pay.ifsys.controller.addInterFaceInvoker");
		String targetName = joinPoint.getTarget().getClass().getName();
		if(includeClass.contains(targetName)){
			HttpSession session = (HttpSession) joinPoint.getArgs()[1];
			setSessionUser(session);
		}
	}

}
