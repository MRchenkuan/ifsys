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

public class InterFaceRequestDto extends RequestDto {
    private InterFaceInfo interFaceInfo;
    private List<Integer> invokerList;

    public List<Integer> getInvokerList() {
        return invokerList;
    }

    public void setInvokerList(List<Integer> invokerList) {
        this.invokerList = invokerList;
    }

    public InterFaceInfo getInterFaceInfo() {
        return interFaceInfo;
    }

    public void setInterFaceInfo(InterFaceInfo interFaceInfo) {
        this.interFaceInfo = interFaceInfo;
    }


    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
