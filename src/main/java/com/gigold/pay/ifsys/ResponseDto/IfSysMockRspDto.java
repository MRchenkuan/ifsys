/**
 * Title: IfSysMockRspDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.github.pagehelper.PageInfo;

/**
 * Title: IfSysMockRspDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午1:36:56
 *
 */
public class IfSysMockRspDto extends ResponseDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	PageInfo<InterFaceInfo> pageInfo;

	/**
	 * @return the pageInfo
	 */
	public PageInfo<InterFaceInfo> getPageInfo() {
		return pageInfo;
	}

	/**
	 * @param pageInfo
	 *            the pageInfo to set
	 */
	public void setPageInfo(PageInfo<InterFaceInfo> pageInfo) {
		this.pageInfo = pageInfo;
	}

}
