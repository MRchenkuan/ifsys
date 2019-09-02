package com.gigold.pay.ifsys.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gigold.pay.ifsys.RequestDto.InterFaceProRequestDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceSysAddDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceSysRequestDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceSysResponseDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.service.InterFaceProService;
import com.gigold.pay.ifsys.service.PrivilegeService;
import com.gigold.pay.ifsys.util.Constant;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.service.InterFaceSysService;

import javax.servlet.http.HttpSession;

@Controller
public class InterFaceSysController extends BaseController{
    @Autowired
    InterFaceSysService interFaceSysService;
    @Autowired
    InterFaceProService interFaceProService;
    @Autowired
    PrivilegeService privilegeService;

    
    /**
	 * @return the interFaceSysService
	 */
	public InterFaceSysService getInterFaceSysService() {
		return interFaceSysService;
	}

	/**
	 * @param interFaceSysService the interFaceSysService to set
	 */
	public void setInterFaceSysService(InterFaceSysService interFaceSysService) {
		this.interFaceSysService = interFaceSysService;
	}

    /**
     * 根据ID获取系统信息
     * @param qdto
     * @return
     */
    @Deprecated
	@RequestMapping(value = "/getSysInfoById.do")
    public @ResponseBody
    InterFaceSysResponseDto getSysInfoById(@RequestBody InterFaceSysRequestDto qdto) {
        InterFaceSysTem interFaceSystem = qdto.getInterFaceSysTem();
        InterFaceSysResponseDto dto = new InterFaceSysResponseDto();
        List<InterFaceSysTem> rlist = new ArrayList<InterFaceSysTem>();
        interFaceSystem = interFaceSysService.getSysInfoById(interFaceSystem);
        if (interFaceSystem != null) {
            rlist.add(interFaceSystem);
            dto.setSysList(rlist);
            dto.setRspCd(SysCode.SUCCESS);
        } else {
            dto.setRspCd(CodeItem.IF_FAILURE);
        }

        return dto;

    }


    /**
     * 获取项目的系统产品列表
     * @author chenkuan
     * @return
     */
    @Authority(TypeRole.Visitor)
    @RequestMapping(value = "/getAllSysInfo.do")
    public @ResponseBody InterFaceSysResponseDto getAllSysInfo(@RequestBody HashMap map,HttpSession session) {
        InterFaceSysResponseDto dto = new InterFaceSysResponseDto();
        int pid=0;
        try{
            pid = Integer.parseInt(String.valueOf(map.get("pid")));
            if(pid<=0)throw new Exception("参数格式错误");
        }catch (Exception e){
            e.printStackTrace();
            dto.setRspCd(SysCode.SYS_FAIL);
            dto.setRspInf("参数格式错误");
            return dto;
        }
        // 判断权限
        UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
        List<RoleInfo> roleInfos = privilegeService.getAllRoleByProject(user.getId(),pid);

        List<InterFaceSysTem> rlist= interFaceSysService.getAllSysInfo(pid);
        List<InterFacePro> proList = interFaceProService.getAllProInfo(pid);
        if (rlist != null) {
            dto.setSysList(rlist);
            dto.setProList(proList);
            dto.setMemberRoles(roleInfos);
            dto.setRspCd(SysCode.SUCCESS);
        } else {
            dto.setRspCd(CodeItem.IF_FAILURE);
        }

        return dto;

    }

    /**
     * 获取项目的系统产品列表
     * @author chenkuan
     * @return
     */
    @Authority(TypeRole.Admin)
    @RequestMapping(value = "/addInterfaceSystem.do")
    public @ResponseBody InterFaceSysResponseDto addInterfaceSystem(@RequestBody InterFaceSysAddDto rdto, HttpSession session) {
        InterFaceSysResponseDto reDto = new InterFaceSysResponseDto();
        int pid = rdto.getPid();
        String remark = rdto.getRemark();

        if(rdto.isSysAdding()){
            InterFaceSysTem sysObj = new InterFaceSysTem();
            sysObj.setSysName(rdto.getSysName());
            sysObj.setIfProjectId(pid);
            sysObj.setSysDesc(remark);
            int count = interFaceSysService.updateInterfaceSys(sysObj);
            if(count<=0){
                reDto.setRspCd(SysCode.SYS_FAIL);
                reDto.setRspInf("数据插入失败");
                return reDto;
            }
            List<InterFaceSysTem> rlist= interFaceSysService.getAllSysInfo(pid);
            List<InterFacePro> proList = interFaceProService.getAllProInfo(pid);
            reDto.setSysList(rlist);
            reDto.setProList(proList);
            reDto.setRspCd(SysCode.SUCCESS);
            return reDto;
        }

        if(rdto.isProAdding()){
            int sysId = rdto.getSysId();
            InterFacePro proObj = new InterFacePro();
            proObj.setSysId(sysId);
            proObj.setProName(rdto.getProName());
            proObj.setProDesc(remark);
            int count = interFaceSysService.updateInterfacePro(proObj);
            if(count<=0){
                reDto.setRspCd(SysCode.SYS_FAIL);
                reDto.setRspInf("数据插入失败");
                return reDto;
            }
            List<InterFaceSysTem> rlist= interFaceSysService.getAllSysInfo(pid);
            List<InterFacePro> proList = interFaceProService.getAllProInfo(pid);
            reDto.setSysList(rlist);
            reDto.setProList(proList);
            reDto.setRspCd(SysCode.SUCCESS);
            return reDto;
        }
        reDto.setRspCd(SysCode.SYS_FAIL);
        reDto.setRspInf("系统错误,无法确定是新增系统还是产品");
        return reDto;
    }

}
