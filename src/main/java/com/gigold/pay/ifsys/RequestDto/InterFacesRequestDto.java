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
import com.gigold.pay.ifsys.bo.InterFaceInfo;

import java.util.List;

public class InterFacesRequestDto extends RequestDto {
    private List<InterFaceInfo> interFacesInfo;

    public List<InterFaceInfo> getInterFacesInfo() {
        return interFacesInfo;
    }

    public void setInterFacesInfo(List<InterFaceInfo> interFacesInfo) {
        this.interFacesInfo = interFacesInfo;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return false;
    }
}
