/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.InterFaceFeeds;
import com.gigold.pay.ifsys.bo.ReturnCode;

import java.util.List;
import java.util.Map;


/**
 * 请求dto 接口id和关系表
 */
public class IfChangesRspDto extends ResponseDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
    private List<InterFaceFeeds> feedsList;

    public List<InterFaceFeeds> getFeedsList() {
        return feedsList;
    }

    public void setFeedsList(List<InterFaceFeeds> feedsList) {
        this.feedsList = feedsList;
    }
}
