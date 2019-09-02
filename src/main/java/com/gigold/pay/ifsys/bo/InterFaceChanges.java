package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;

import java.io.Serializable;

/**
 * Created by chenkuan
 * on 16/6/29.
 */
public class InterFaceChanges extends Domain implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    int userId,ifId;
    String optionType,changeType,prmVal,nowVal,ts;
    String userName,ifName;

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public int getIfId() {
        return ifId;
    }

    public void setIfId(int ifId) {
        this.ifId = ifId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getPrmVal() {
        return prmVal;
    }

    public void setPrmVal(String prmVal) {
        this.prmVal = prmVal;
    }

    public String getNowVal() {
        return nowVal;
    }

    public void setNowVal(String nowVal) {
        this.nowVal = nowVal;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
