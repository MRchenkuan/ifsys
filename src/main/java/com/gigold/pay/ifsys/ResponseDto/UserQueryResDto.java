package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;

import java.util.List;

public class UserQueryResDto extends ResponseDto {
 
	private List<?> userInfos;

	public List<?> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<?> userInfos) {
		this.userInfos = userInfos;
	}
}
