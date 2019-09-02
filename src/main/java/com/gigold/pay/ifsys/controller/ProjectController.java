package com.gigold.pay.ifsys.controller;


import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.ResponseDto.ProjectInfoRspDto;
import com.gigold.pay.ifsys.bo.ProjectInfo;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.ProjectService;
import com.gigold.pay.ifsys.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/project/")
public class ProjectController extends BaseController {
	@Autowired
	ProjectService projectService;


	/**
	 * 获取当前用户所有的项目
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getAllProjectInfo.do")
	public @ResponseBody
	ProjectInfoRspDto getAllProjectInfo (HttpSession session) {
		ProjectInfoRspDto reDto = new ProjectInfoRspDto();
		try{
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			if(user==null || user.getId()<=0){
				reDto.setRspCd(CodeItem.NOT_LOGIN);
				reDto.setRspCd("用户未登录");
				return reDto;
			}
			List<ProjectInfo> projectInfos = projectService.getProjectByUser(user.getId());
			reDto.setProjectInfo(projectInfos);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.printStackTrace();
		}
		return reDto;
	}
}
