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
import com.gigold.pay.ifsys.bo.InterFaceSysTem;

public class InterFaceSysRequestDto extends RequestDto {
    private InterFaceSysTem InterFaceSysTem;

    /**
     * @return the interFaceSysTem
     */
    public InterFaceSysTem getInterFaceSysTem() {
        return InterFaceSysTem;
    }

    /**
     * @param interFaceSysTem
     *            the interFaceSysTem to set
     */
    public void setInterFaceSysTem(InterFaceSysTem interFaceSysTem) {
        InterFaceSysTem = interFaceSysTem;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
