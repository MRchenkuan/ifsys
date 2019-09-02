package com.gigold.pay.ifsys.service;


import com.gigold.pay.framework.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService extends AbstractService {
	@Autowired
	MailSendService mailSendService;

	public boolean sendEmailCaptcha(String email, String captcha){
		//设置邮件基本信息
		mailSendService.setSubject("微信登陆邮箱验证");
		mailSendService.setTemplateName("emailCaptchaNotice.vm");// 设置的邮件模板
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("captcha",captcha);
			// 设置收件人地址
			mailSendService.setTo(Collections.singletonList(email));
			// 发送邮件
			mailSendService.sendWithTemplateForHTML(model,"独孤九剑系统");
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
