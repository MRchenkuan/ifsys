package com.gigold.pay.ifsys.dao;

import com.gigold.pay.ifsys.bo.Member;
import com.gigold.pay.ifsys.bo.ProjectInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目相关的接口都在这
 * @author chenkuan
 * @date 20160209
 */
public interface ProjectDao {

	/**
	 * 根据用户获取其所在的项目信息
	 * @author chenkuan
	 * @date 20160209
 	 */
	List<ProjectInfo> getProjectByUser(int userId);

	/**
	 * 获取项目的成员列表
	 * @param projectInfo
	 * @return
	 */
	List<Member> getProjectMembers(ProjectInfo projectInfo);

	/**
	 * 判断用户是否在项目中
	 * @param projectInfo
	 * @return
	 */
    int isUserAsMemberIn(ProjectInfo projectInfo);

	/**
	 * 增加项目成员到项目
	 * @return
	 */
    int addMembersToProject(@Param("userId") int userId,@Param("pid") int pid,@Param("role") int role);
}
