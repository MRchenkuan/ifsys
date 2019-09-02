/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.InterFaceFeeds;

import java.util.List;


/**
 * 请求dto 接口id和关系表
 */
public class IfFollowRspDto extends ResponseDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
    private boolean follow;

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
