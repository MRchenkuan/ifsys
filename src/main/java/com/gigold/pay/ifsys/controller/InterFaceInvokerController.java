/**
 * Title: InterFaceInvokerController.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.RequestDto.InterFaceInvokerReqDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceInvokersReqDto;
import com.gigold.pay.ifsys.ResponseDto.IfFollowRspDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceInvokerAddResDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceInvokerResDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.TypeRole;
import com.gigold.pay.ifsys.service.UserInfoService;
import com.gigold.pay.ifsys.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.base.SpringContextHolder;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.core.exception.PendingException;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.InterFaceInvokerService;

/**
 * Title: InterFaceInvokerController<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月23日下午3:27:08
 *
 */
@Controller
public class InterFaceInvokerController extends BaseController {
	@Autowired
	InterFaceInvokerService interFaceInvokerService;
	@Autowired
	UserInfoService userInfoService;

	/**
	 * @return the interFaceInvokerService
	 */
	public InterFaceInvokerService getInterFaceInvokerService() {
		return interFaceInvokerService;
	}

	/**
	 * @param interFaceInvokerService the interFaceInvokerService to set
	 */
	public void setInterFaceInvokerService(InterFaceInvokerService interFaceInvokerService) {
		this.interFaceInvokerService = interFaceInvokerService;
	}

	/**
	 * 用户关注接口
	 *
	 * @param dto
	 * @param session
	 * @return
	 */
	@Deprecated
	@Notice("addInterFaceInvoker")
	@RequestMapping("/addinvoker.do")
	public
	@ResponseBody
	InterFaceInvokerAddResDto addInterFaceInvoker(@RequestBody InterFaceInvokerReqDto dto,
												  HttpSession session) {
		InterFaceInvokerAddResDto reDto = new InterFaceInvokerAddResDto();
		String rspCode = dto.vaildate();
		if (!SysCode.SUCCESS.equals(rspCode)) {
			reDto.setRspCd(rspCode);
			return reDto;
		}
		// 在session中取uid
		UserInfo userInfo = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if (userInfo == null) {
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("用户未登录");
			return reDto;
		}
		InterFaceInvoker invoker = null;
		try {
			invoker = createBO(dto, InterFaceInvoker.class);
		} catch (PendingException e) {
			debug("创建BO失败");
			e.printStackTrace();
		}

		if (invoker != null)
			invoker.setuId(userInfo.getId());

		// 添加关注信息
		invoker = interFaceInvokerService.addInterFaceInvoker(invoker);
		if (invoker != null) {
			reDto.setInvoker(invoker);
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setRspInf("关注成功");
		} else {
			reDto.setRspCd(CodeItem.IF_FAILURE);
			reDto.setRspInf("关注失败");
		}

		return reDto;

	}

	/**
	 * 获取接口关注列表
	 *
	 * @param dto
	 * @return
	 */
	@Deprecated
	@RequestMapping("/getinvokerlist.do")
	public
	@ResponseBody
	InterFaceInvokerResDto getInvokerListByFollowId(@RequestBody InterFaceInvokerReqDto dto) {
		debug("调用 getInvokerListByFollowId");
		InterFaceInvokerResDto rdto = new InterFaceInvokerResDto();
		if (dto.getIfFollowedId() == 0) {
			rdto.setRspCd(CodeItem.FLLOW_IF_ID_FAILURE);
			return rdto;
		}
		InterFaceInvoker invoker = (InterFaceInvoker) SpringContextHolder.getBean(InterFaceInvoker.class);
		invoker.setIfFollowedId(dto.getIfFollowedId());
		List<InterFaceInvoker> list = interFaceInvokerService.getInvokerList(invoker);
		if (list != null) {
			rdto.setList(list);
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
			rdto.setRspInf("获取失败");
		}

		return rdto;
	}

	/**
	 * 用户主动取消关注
	 *
	 * @param dto
	 * @param session
	 * @return
	 */
	@RequestMapping("/deleteinvoker.do")
	public
	@ResponseBody
	ResponseDto deleteInterFaceInvoker(@RequestBody InterFaceInvokerReqDto dto,
									   HttpSession session) {
		debug("调用 deleteInterFaceInvoker");
		ResponseDto rdto = new ResponseDto();
		// 在session中取uid
		UserInfo userInfo = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if (userInfo == null) {
			rdto.setRspCd(SysCode.SYS_FAIL);
			rdto.setRspInf("用户未登录");
			return rdto;
		}

		// 取消关注信息
		boolean flag = interFaceInvokerService.deleteInvoker(dto.getId());
		if (flag) {
			rdto.setRspCd(SysCode.SUCCESS);
			rdto.setRspInf("取消关注成功");
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
			rdto.setRspInf("关注失败");
		}

		return rdto;

	}

	/**
	 * 将用户加入关注列表
	 *
	 * @param dto
	 * @param session
	 * @return
	 */
	@Deprecated
	@Notice("addInterFaceInvokers")
	@RequestMapping("/addInterFaceInvokers.do")
	public
	@ResponseBody
	ResponseDto addInterFaceInvokers(@RequestBody InterFaceInvokersReqDto dto, HttpSession session) {
		ResponseDto rdto = new ResponseDto();

		// 在session中取uid
		UserInfo self = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if (self == null) {
			rdto.setRspCd(SysCode.SYS_FAIL);
			rdto.setRspInf("用户未登录");
			return rdto;
		}

		if (!dto.validate()) {
			rdto.setRspInf("参数校验错误");
			return rdto;
		}

		long ifId = dto.getIfId();
		String remarks = dto.getRemarks();
		List<Integer> uIds = dto.getuIds();

		interFaceInvokerService.addInterFaceInvokers(ifId, uIds, remarks);
		rdto.setRspCd(SysCode.SUCCESS);
		return rdto;

	}

	@Authority(TypeRole.Member)
	@Notice
	@RequestMapping("/followInterface.do")
	public
	@ResponseBody
	IfFollowRspDto followInterface(@RequestBody Map<String,Object> param, HttpSession session) {
		IfFollowRspDto reDto = new IfFollowRspDto();
		try {
			// 获取接口ID
			int ifId = 0;
			if (param.containsKey("ifId") && (int) param.get("ifId") > 0) {
				Object ifIdO = param.get("ifId");
				if(ifIdO instanceof Integer)
					ifId = (int) ifIdO;
				if(ifIdO instanceof String)
					ifId = Integer.parseInt(String.valueOf(ifIdO));
			} else {
				reDto.setRspCd(SysCode.SYS_FAIL);
				reDto.setRspInf("没有提交接口ID");
				return reDto;
			}

			// 获取关注信息
			String remark;
			if (param.containsKey("remark") && StringUtil.isNotBlank(String.valueOf(param.get("remark")))) {
				remark = String.valueOf(param.get("remark"));
			} else {
				remark = "主动关注";
			}

			// 获取用户信息
			UserInfo self = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			if (self == null) {
				reDto.setRspCd(SysCode.SYS_FAIL);
				reDto.setRspInf("用户未登录");
				return reDto;
			}

			// 添加关注
			boolean resulte = interFaceInvokerService.followInterface(self.getId(), ifId, remark);
			// 设置结果
			reDto.setFollow(resulte);
			reDto.setRspCd(SysCode.SUCCESS);
			// 返回结果
			return reDto;
		}catch (Exception e){
			e.printStackTrace();
			return reDto;
		}
	}
}