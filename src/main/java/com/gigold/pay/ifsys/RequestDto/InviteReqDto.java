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
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.ReturnCode;
import com.gigold.pay.ifsys.util.RegxUtils;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求dto 接口id和关系表
 */
public class InviteReqDto extends RequestDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
    private int pid;
    private String email;
    private int userId;
    private String memberType;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }


    @Override
    public boolean validate() throws OtherExceptionCollect {
        return pid > 0 && StringUtil.isNotEmpty(email) && StringUtil.isNotEmpty(memberType) && RegxUtils.isEmail(email);
    }
}
