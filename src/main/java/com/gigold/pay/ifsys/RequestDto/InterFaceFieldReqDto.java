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
import com.gigold.pay.ifsys.bo.InterFaceField;

public class InterFaceFieldReqDto extends RequestDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private InterFaceField interFaceField;

    /**
     * @return the interFaceField
     */
    public InterFaceField getInterFaceField() {
        return interFaceField;
    }

    /**
     * @param interFaceField the interFaceField to set
     */
    public void setInterFaceField(InterFaceField interFaceField) {
        this.interFaceField = interFaceField;
    }


    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
