/**
 * Title: InterFaceInvokerAddDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.RequestDto;

import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.SysCode;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Title: InterFaceInvokerAddDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月23日下午3:29:39
 *
 */
public class InterFaceInvokersReqDto extends RequestDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private long ifId;
	private List<Integer> uIds;
	private String remarks;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getIfId() {
		return ifId;
	}

	public void setIfId(long ifId) {
		this.ifId = ifId;
	}

	public List<Integer> getuIds() {
		return uIds;
	}

	public void setuIds(List<Integer> uIds) {
		this.uIds = uIds;
	}

	public boolean validate(){
		return uIds != null && uIds.size() > 0;
	}
}
