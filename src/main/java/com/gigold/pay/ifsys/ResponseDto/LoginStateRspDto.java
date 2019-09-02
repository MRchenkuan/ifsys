package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.bo.WxUserInfo;

/**
 * Created by chenkuan
 * on 2017/2/20.
 */
public class LoginStateRspDto extends ResponseDto{
    private static final long serialVersionUID = 1L;
    private String ssid;
    private UserInfo userInfo;
    private WxUserInfo wxUserInfo;

    public WxUserInfo getWxUserInfo() {
        return wxUserInfo;
    }

    public void setWxUserInfo(WxUserInfo wxUserInfo) {
        this.wxUserInfo = wxUserInfo;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
