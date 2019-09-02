package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.ifsys.bo.UserDetail;


public class UserDetailResDto extends UserResDto {
	private UserDetail userInfo;
	public UserDetailResDto(){
		this.userInfo = (UserDetail) super.getUserInfo();
	}

	@Override
	public UserDetail getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserDetail userInfo) {
		this.userInfo = userInfo;
	}
}
