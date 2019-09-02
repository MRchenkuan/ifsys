package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.bo.Member;
import com.gigold.pay.ifsys.bo.ProjectInfo;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.dao.ProjectDao;
import com.gigold.pay.ifsys.dao.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目相关的服务都放在这
 * @author chenkuan
 * 20160209
 */
@Service
public class ProjectService extends AbstractService {
	@Autowired
	ProjectDao projectDao;
	@Autowired
	UserInfoDao userInfoDao;

	/**
	 * 根据用户获取项目列表
	 * @param userId
	 * @return
	 */
	public List<ProjectInfo> getProjectByUser(int userId){
		// 获取用户的有关项目基本信息
		List<ProjectInfo> projectList = projectDao.getProjectByUser(userId);

		for(ProjectInfo projectInfo : projectList){
			// 加入每个项目的项目成员信息
			List<Member> members = projectDao.getProjectMembers(projectInfo);
			projectInfo.setMembers(members);
			// 加入每个项目的管理者信息
			UserInfo master = userInfoDao.getUser(projectInfo.getMasterId());
			projectInfo.setMaster(master);
		}
		return projectList;
	}

	/**
	 * 获取项目成员列表
	 * @param projectInfo
	 * @return
	 */
	public List<Member> getMembersByProject(ProjectInfo projectInfo){
		List<Member> members = new ArrayList<>();
		try {
			members = projectDao.getProjectMembers(projectInfo);
		}catch (Exception e){
			e.printStackTrace();
		}
		return members;
	};

	/**
	 * 添加用户到成员列表
	 * @param userList
	 * @param pid
	 * @param memberType
	 * @return
	 */
	public boolean addMembersToProject(List<UserInfo> userList, int pid, int memberType) {
		boolean flag = false;
		int count = 0;
		try{
			for(UserInfo user : userList){
				count += projectDao.addMembersToProject(user.getId(),pid,memberType);
			}
			if(count>0){
				flag=true;
			}
		}catch (Exception e){
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}

	/**
	 * 	 * 添加用户到成员列表
	 * @param user
	 * @param pid
	 * @param memberType
	 * @return
	 */
	public boolean addMembersToProject(UserInfo user, int pid, int memberType) {
		boolean flag = false;
		try{
			int count = 0;
			count = projectDao.addMembersToProject(user.getId(),pid,memberType);
			if(count>0){
				flag=true;
			}
		}catch (Exception e){
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}

}
