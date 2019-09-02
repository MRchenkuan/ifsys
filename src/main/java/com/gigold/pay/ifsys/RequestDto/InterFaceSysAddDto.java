package com.gigold.pay.ifsys.RequestDto;

import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;
import com.gigold.pay.framework.util.common.StringUtil;

/**
 * Created by chenkuan
 * on 2017/2/22.
 */
public class InterFaceSysAddDto extends RequestDto{
    int pid,sysId;
    String proName;
    String sysName;
    String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getSysId() {
        return sysId;
    }

    public void setSysId(int sysId) {
        this.sysId = sysId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public boolean isSysAdding(){
        return this.pid>0
                && this.sysId<=0
                && StringUtil.isNotEmpty(this.sysName)
                && StringUtil.isEmpty(this.proName);
    }

    public boolean isProAdding(){
        return this.pid>0
                &&this.sysId>0
                && StringUtil.isNotEmpty(this.proName);
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
