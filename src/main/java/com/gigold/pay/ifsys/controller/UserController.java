package com.gigold.pay.ifsys.controller;

import javax.servlet.http.HttpSession;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.constant.SessionConstant;
import com.gigold.pay.ifsys.RequestDto.InviteReqDto;
import com.gigold.pay.ifsys.RequestDto.UserIReqDto;
import com.gigold.pay.ifsys.RequestDto.UserQueryReqDto;
import com.gigold.pay.ifsys.ResponseDto.UserDetailResDto;
import com.gigold.pay.ifsys.ResponseDto.UserQueryResDto;
import com.gigold.pay.ifsys.ResponseDto.UserResDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.dao.UserInfoDao;
import com.gigold.pay.ifsys.service.PrivilegeService;
import com.gigold.pay.ifsys.service.ProjectService;
import com.gigold.pay.ifsys.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.service.UserInfoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class UserController extends BaseController {
	@Autowired
	UserInfoService userInfoService;

	@Autowired
	ProjectService projectService;
	@Autowired
	PrivilegeService privilegeService;

	/**
	 * @return the userInfoService
	 */
	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	/**
	 * @param userInfoService the userInfoService to set
	 */
	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@RequestMapping(value = "/login.do")
	public @ResponseBody
	UserResDto login(@RequestBody UserIReqDto rdto, HttpSession sessin) {
		UserResDto dto = new UserResDto();
		// 验证请求参数合法性
		String code = rdto.validation();
		// 没有通过则返回对应的返回码
		if (!"00000".equals(code)) {
			dto.setRspCd(code);
			return dto;
		}
		UserInfo user = userInfoService.login(rdto.getUserInfo());
		if (user == null) {
			dto.setRspCd(CodeItem.IF_FAILURE);
			dto.setRspInf("用户名或者密码错误");
		} else {
			sessin.setAttribute(Constant.LOGIN_KEY, user);
			// 新版本登录
			sessin.setAttribute(SessionConstant.GIGOLD_AUTH, Constant.SESSION_VALUE_GIGOLD_AUTH);
			sessin.setAttribute(SessionConstant.GIGOLD_USR_NO, user.getId());

			dto.setUserInfo(user);
			dto.setRspCd(SysCode.SUCCESS);
		}

		return dto;
	}

	/**
	 * 获取项目成员列表
	 * @param rdto
     * @return
     */
	@Authority(TypeRole.Visitor)
	@RequestMapping(value = "/getMemberList.do")
	public @ResponseBody
	UserQueryResDto getMemberList(@RequestBody UserQueryReqDto rdto, HttpSession session) {
		UserQueryResDto resDto = new UserQueryResDto();
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if(user==null || user.getId()<=0){
			resDto.setRspCd(CodeItem.NOT_LOGIN);
			resDto.setRspCd("用户未登录");
			return resDto;
		}
		List<?> userInfos;
		try{
			//判断用户是否在项目中
			boolean isUserAsMember = false;
			List<ProjectInfo> projects = projectService.getProjectByUser(user.getId());
			for(ProjectInfo project :projects){
				if(project.getId() == rdto.getPid()){
					isUserAsMember = true;
				}
			}
			// 若没有权限则退出
			if(!isUserAsMember){
				resDto.setRspCd(CodeItem.NOT_PERMITED);
				resDto.setRspCd("没有权限");
				return resDto;
			}

			ProjectInfo projectInfo = new ProjectInfo();
			projectInfo.setId(rdto.getPid());
			userInfos = projectService.getMembersByProject(projectInfo);


			resDto.setUserInfos(userInfos);
			resDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			resDto.setRspCd(SysCode.SYS_FAIL);
		}
		return resDto;
	}

	/**
	 * 获取当前用户信息
	 * @date 2017-02-21
	 * @return
	 */
	@RequestMapping(value = "/whoAmI.do")
	public @ResponseBody
	UserDetailResDto whoAmI(@RequestBody Map<String,Integer> rmap, HttpSession session) {
		UserDetailResDto dto = new UserDetailResDto();
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		UserDetail userDetail = null;
		try{
			if(user!=null) userDetail = userInfoService.getUserDetail(user);
			if(rmap==null || !rmap.containsKey("pid")){
				System.out.println(user);
				if(user!=null){
					dto.setRspCd(SysCode.SUCCESS);
					dto.setUserInfo(userDetail);
				}else{
					dto.setRspCd(CodeItem.NOT_LOGIN);
				}
				// 系统异常
				return dto;
			}else {
				int pid = rmap.get("pid");
				List<RoleInfo> roleInfos = privilegeService.getAllRoleByProject(user.getId(),pid);
				dto.setUserInfo(userDetail);
				dto.setRoleList(roleInfos);
				dto.setRspCd(SysCode.SUCCESS);
				return dto;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return dto;
	}


	/**
	 * 邀请用户加入项目
	 */
	@Authority(TypeRole.Admin)
	@RequestMapping(value = "/addProjectMember.do")
	public @ResponseBody ResponseDto addProjectMember(@RequestBody InviteReqDto dto, HttpSession session){
		ResponseDto resDto = new ResponseDto();

        // 输入校验
        if(!dto.validate()){
			resDto.setRspCd(CodeItem.INVAILD_PARM_FAILURE);
			return resDto;
		}

		// 初始化输入数据
		int pid=dto.getPid();
		String email = dto.getEmail();
        int userId = dto.getUserId();
		String memberType =dto.getMemberType();
        int role = 2;
        if("visitor".equalsIgnoreCase(memberType)){role=2;}
        if("member".equalsIgnoreCase(memberType)){role=1;}


        // 判定添加方式
        if(userId>0){
            // 当userid存在时,直接添加,跳过邮箱添加方式
            List<UserInfo> userList = new ArrayList<>();
            UserInfo user = userInfoService.getUser(userId);
            if(user!=null && user.getId()>0){
                projectService.addMembersToProject(user,pid,role);
                userList.add(user);
            }
            resDto.setDataes(userList);
        }else{
            // 邮箱添加方式
            List<UserInfo> userList = userInfoService.getUserByEmail(email);
            if(userList.size()>0){
                // 直接添加
                boolean flag = projectService.addMembersToProject(userList,pid,role);
                if(flag){
                    resDto.setDataes(userList);
                }
            }else{
                // 否则发送邮件通知
                // todo 发送邀请邮件
            }
        }
        resDto.setRspCd(SysCode.SUCCESS);
        return resDto;
	}

	/**
	 * 邀请用户加入项目
	 */
	@RequestMapping(value = "/searchUsers.do")
	public @ResponseBody ResponseDto searchUsers(@RequestBody Map<String,String> rmap, HttpSession session){
		ResponseDto resDto = new ResponseDto();

        if(!rmap.containsKey("key")){
            resDto.setRspCd(CodeItem.INVAILD_PARM_FAILURE);
            return resDto;
        }
        String key = rmap.get("key");
        List<UserInfo> userInfoList = userInfoService.searchUsersByKeyWords(key);
        List<Map> userList = new ArrayList<>();

        //过滤属性
        for(UserInfo userInfo:userInfoList){
            Map<String,Object> user = new HashMap<>();
            user.put("id",userInfo.getId());
            user.put("email",userInfo.getEmail());
            user.put("userName",userInfo.getUserName());
            user.put("avtSrc",userInfo.getAvtSrc());
            userList.add(user);
        }
        resDto.setRspCd(SysCode.SUCCESS);
        resDto.setDataes(userList);
		return resDto;
	}

}
