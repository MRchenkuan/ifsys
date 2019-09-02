package com.gigold.pay.ifsys.controller;

import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.RequestDto.InterFaceDetailReqDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceEditionsRspDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.InterFaceEdition;
import com.gigold.pay.ifsys.bo.TypeRole;
import com.gigold.pay.ifsys.service.InterFaceEditionService;
import com.gigold.pay.ifsys.util.TimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class InterFaceEditionController extends BaseController {
	@Autowired
	InterFaceEditionService interFaceEditionService;

	/**
	 * 根据接口ID获取接口版本信息
	 * @return
	 */
	@Authority(TypeRole.Visitor)
	@RequestMapping("/getEditionsByIfId.do")
	public
	@ResponseBody
	InterFaceEditionsRspDto getEditionsByIfId(@RequestBody InterFaceDetailReqDto dto) {
		InterFaceEditionsRspDto reDto = new InterFaceEditionsRspDto();

		int ifId = dto.getIfId();
		if(ifId<=0){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("请提交接口ID");
		}
		List<InterFaceEdition> editions = interFaceEditionService.getEditionsByIfId(ifId);
		// 时间格式转换
		for(InterFaceEdition edition : editions){
			String ts = edition.getTs();
			ts = TimeFormatUtil.timeToMoment(ts);
			edition.setTs(ts);
		}
		reDto.setEditions(editions);
		reDto.setRspCd(SysCode.SUCCESS);
		return reDto;
	}
}
