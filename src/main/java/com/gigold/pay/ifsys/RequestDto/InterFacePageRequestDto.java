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
import com.gigold.pay.ifsys.bo.MyPageInfo;

public class InterFacePageRequestDto extends RequestDto {
    private MyPageInfo pageInfo;
    /**
     * @return the pageInfo
     */
    public MyPageInfo getPageInfo() {
        return pageInfo;
    }

    /**
     * @param pageInfo the pageInfo to set
     */
    public void setPageInfo(MyPageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
