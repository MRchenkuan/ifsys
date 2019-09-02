/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.RequestDto;

import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.bo.InterFaceInfo;

import java.util.List;

public class WechatLoginReqDto extends RequestDto {
    private String code;
    private String ssid;
    private String state;
    private String registerEmail; // 用来绑定的邮箱
    private String registerEmailCapture; // 邮箱验证码

    private String bindPhone; // 用来绑定的手机号
    private String bindPhoneCapture; // 手机号验证码

    private String bindAcct; // 通过绑定公司邮箱
    private String bindAcctCapture; // 公司邮箱验证码

    private String loginType;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getBindAcct() {
        return bindAcct;
    }

    public void setBindAcct(String bindAcct) {
        this.bindAcct = bindAcct;
    }

    public String getBindAcctCapture() {
        return bindAcctCapture;
    }

    public void setBindAcctCapture(String bindAcctCapture) {
        this.bindAcctCapture = bindAcctCapture;
    }


    public String getRegisterEmail() {
        return registerEmail;
    }

    public void setRegisterEmail(String registerEmail) {
        this.registerEmail = registerEmail;
    }

    public String getRegisterEmailCapture() {
        return registerEmailCapture;
    }

    public void setRegisterEmailCapture(String registerEmailCapture) {
        this.registerEmailCapture = registerEmailCapture;
    }

    public String getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(String bindPhone) {
        this.bindPhone = bindPhone;
    }

    public String getBindPhoneCapture() {
        return bindPhoneCapture;
    }

    public void setBindPhoneCapture(String bindPhoneCapture) {
        this.bindPhoneCapture = bindPhoneCapture;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }

    public boolean validateLoginParam(){
        switch (loginType){
            case "email":
                return StringUtil.isNotEmpty(registerEmail);
            case "bindAcct":
                return StringUtil.isNotEmpty(bindAcct);
            case "directly":
                return true;
            default:
                return false;
        }
    }
}
