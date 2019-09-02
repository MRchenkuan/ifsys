package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.RoleInfo;
import com.gigold.pay.ifsys.bo.UserInfo;

import java.util.List;

public class UserResDto extends ResponseDto {
 
	private UserInfo userInfo;
	private List<RoleInfo> roleList;

	public List<RoleInfo> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<RoleInfo> roleList) {
		this.roleList = roleList;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
}
