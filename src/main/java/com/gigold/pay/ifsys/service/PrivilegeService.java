package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.web.interceptor.BusinessDelegate;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.dao.InterFaceDao;
import com.gigold.pay.ifsys.dao.InterFaceSystemDao;
import com.gigold.pay.ifsys.dao.PrivilegeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目权限服务
 * @author chenkuan
 * 20160210
 */
@Service
public class PrivilegeService extends AbstractService{

	@Autowired
	PrivilegeDao privilegeDao;
	@Autowired
	InterFaceDao interFaceDao;
	@Autowired
	InterFaceSystemDao interFaceSystemDao;
	/**
	 * 获取用户相对于项目的权限
	 * @param userId
	 * @param projectId
	 * @return
	 */
	public List<PrivilegeInfo> getUserPrivilege(int userId, int projectId) {
		// 取用户在项目中的所有角色
		List<RoleInfo> roleInfos = privilegeDao.getAllRoleByProject(userId, projectId);
		// 去重然后获取权限列表
		List<String> privs = new ArrayList<>();
		for (RoleInfo roleInfo : roleInfos) {
			String privString = roleInfo.getPrivList();
			String[] privstrs = privString.split(",");
			privs.addAll(Arrays.asList(privstrs));
		}

		List<PrivilegeInfo> privileges = new ArrayList<>();
		if(privs.size()>0)
		privileges = privilegeDao.getPrivilegeInfosById(privs);
		return privileges;
	}

	/**
	 * 获取用户相对于接口的权限
	 * @param userId
	 * @param ifId
	 * @return
	 */
	public List<PrivilegeInfo> getUserPrivilege(int userId, InterFaceInfo interFaceInfo) {
		interFaceInfo = interFaceDao.getInterFaceById(interFaceInfo);
		int pid = interFaceInfo.getPid();
		return getUserPrivilege(userId,pid);
	}

	/**
	 * 获取用户相对于系统
	 * @param userId
	 * @param ifId
	 * @return
	 */
    public List<PrivilegeInfo> getUserPrivilegeBySysId(int userId, int sysId) {
    	InterFaceSysTem system = new InterFaceSysTem();
		system.setId(sysId);
		system = interFaceSystemDao.getSysInfoById(system);
		int pid = system.getIfProjectId();
		return getUserPrivilege(userId,pid);

    }

	/**
	 * 返回角色列表
	 * @param id
	 * @param pid
	 * @return
	 */
	public List<RoleInfo> getAllRoleByProject(int id, int pid) {
		return privilegeDao.getAllRoleByProject(id, pid);
	}
}
