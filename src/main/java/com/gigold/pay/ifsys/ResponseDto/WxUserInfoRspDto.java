/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.bo.WxUserInfo;


/**
 * 微信扫码获取用户信息接口
 */
public class WxUserInfoRspDto extends ResponseDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
    private WxUserInfo wxUserInfo;
    private UserInfo sysUserInfo;

    public UserInfo getSysUserInfo() {
        return sysUserInfo;
    }

    public void setSysUserInfo(UserInfo sysUserInfo) {
        this.sysUserInfo = sysUserInfo;
    }

    public WxUserInfo getWxUserInfo() {
        return wxUserInfo;
    }

    public void setWxUserInfo(WxUserInfo wxUserInfo) {
        this.wxUserInfo = wxUserInfo;
    }
}
