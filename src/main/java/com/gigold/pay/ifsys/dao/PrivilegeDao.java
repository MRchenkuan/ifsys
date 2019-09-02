package com.gigold.pay.ifsys.dao;

import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.PrivilegeInfo;
import com.gigold.pay.ifsys.bo.ProjectInfo;
import com.gigold.pay.ifsys.bo.RoleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrivilegeDao {
	/**
	 * 获取用户在项目中的角色
	 * @param projectInfo
	 * @return
	 */
	List<RoleInfo> getAllRoleByProject(@Param("userId") int userId, @Param("projectId") int projectId);

	/**
	 * 根据用户权限ID列表获取权限列表
	 * @param privs
	 * @return
	 */
	List<PrivilegeInfo> getPrivilegeInfosById(@Param("privs") List<String> privs);
}
