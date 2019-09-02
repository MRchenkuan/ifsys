/**
 * Title: RetrunCodeRspDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.ProjectInfo;
import com.gigold.pay.ifsys.bo.ReturnCode;

import java.util.List;

/**
 * @author chenkuan
 * 2017-02-09
 */
public class ProjectInfoRspDto extends ResponseDto {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private List<ProjectInfo> projectInfo;

	public List<ProjectInfo> getProjectInfo() {
		return projectInfo;
	}

	public void setProjectInfo(List<ProjectInfo> projectInfo) {
		this.projectInfo = projectInfo;
	}
}
