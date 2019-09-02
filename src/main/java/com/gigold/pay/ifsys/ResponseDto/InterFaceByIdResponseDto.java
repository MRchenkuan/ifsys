package com.gigold.pay.ifsys.ResponseDto;

import java.util.List;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.*;

public class InterFaceByIdResponseDto extends ResponseDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private InterFaceInfo interFaceInfo;
	private UserInfo userInfo;
	private InterFaceSysTem system;
	private InterFacePro pro;
    private List<InterFaceInvoker> invokers;
	private List<InterFaceField> fieldList;
    private boolean isFollow;

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public List<InterFaceInvoker> getInvokers() {
        return invokers;
    }

    public void setInvokers(List<InterFaceInvoker> invokers) {
        this.invokers = invokers;
    }

    /**
     * @return the userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo the userInfo to set
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @return the system
     */
    public InterFaceSysTem getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(InterFaceSysTem system) {
        this.system = system;
    }

    /**
     * @return the pro
     */
    public InterFacePro getPro() {
        return pro;
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(InterFacePro pro) {
        this.pro = pro;
    }

    /**
     * @return the fieldList
     */
    public List<InterFaceField> getFieldList() {
        return fieldList;
    }

    /**
     * @param fieldList the fieldList to set
     */
    public void setFieldList(List<InterFaceField> fieldList) {
        this.fieldList = fieldList;
    }

    public InterFaceInfo getInterFaceInfo() {
		return interFaceInfo;
	}

	public void setInterFaceInfo(InterFaceInfo interFaceInfo) {
		this.interFaceInfo = interFaceInfo;
	}

}
