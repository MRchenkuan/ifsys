package com.gigold.pay.ifsys.ResponseDto;

import java.util.List;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.InterFacePro;
import com.gigold.pay.ifsys.bo.InterFaceSysTem;
import com.gigold.pay.ifsys.bo.RoleInfo;

public class InterFaceSysResponseDto extends ResponseDto {

    private List<InterFaceSysTem> sysList;
    private List<InterFacePro> proList;
    private List<RoleInfo> memberRoles;

    public List<RoleInfo> getMemberRoles() {
        return memberRoles;
    }

    public void setMemberRoles(List<RoleInfo> memberRoles) {
        this.memberRoles = memberRoles;
    }

    /**
     * @return the sysList
     */
    public List<InterFaceSysTem> getSysList() {
        return sysList;
    }

    /**
     * @param sysList
     *            the sysList to set
     */
    public void setSysList(List<InterFaceSysTem> sysList) {
        this.sysList = sysList;
    }

    public List<InterFacePro> getProList() {
        return proList;
    }

    public void setProList(List<InterFacePro> proList) {
        this.proList = proList;
    }
}
