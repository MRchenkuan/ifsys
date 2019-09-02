/**
 * Title: ReturnCodeController.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.controller;

import java.util.List;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.RequestDto.IFFieldsReqDto;
import com.gigold.pay.ifsys.RequestDto.ReturnCodeReqDto;
import com.gigold.pay.ifsys.ResponseDto.IFFieldsRspDto;
import com.gigold.pay.ifsys.ResponseDto.RetrunCodeAddRspDto;
import com.gigold.pay.ifsys.ResponseDto.RetrunCodeRspDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.bo.TypeRole;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.core.exception.PendingException;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.bo.ReturnCode;

import javax.servlet.http.HttpSession;

/**
 * Title: ReturnCodeController<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月5日上午9:53:18
 *
 */
@Controller
public class ReturnCodeController extends BaseController {
	@Autowired
	private ReturnCodeService returnCodeService;

	/**
	 * @return the returnCodeService
	 */
	public ReturnCodeService getReturnCodeService() {
		return returnCodeService;
	}

	/**
	 * @param returnCodeService the returnCodeService to set
	 */
	public void setReturnCodeService(ReturnCodeService returnCodeService) {
		this.returnCodeService = returnCodeService;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping("/addrspcd.do")
	public @ResponseBody
    RetrunCodeAddRspDto addRetrunCode(@RequestBody ReturnCodeReqDto dto) {
		debug("调用addRetrunCode");
		RetrunCodeAddRspDto rdto = new RetrunCodeAddRspDto();
		String rcode = dto.validation();
		if (!"00000".equals(rcode)) {
			rdto.setRspCd(rcode);
			return rdto;
		}
		ReturnCode returnCode = null;
		try {
			returnCode = createBO(dto, ReturnCode.class);
		} catch (PendingException e) {
			rdto.setRspCd(CodeItem.IF_FAILURE);
			debug("创建BO失败");
			e.printStackTrace();
			return rdto;
		}
		returnCode = returnCodeService.addRetrunCode(returnCode);
		if (returnCode != null) {
			rdto.setId(returnCode.getId());
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
		}
		return rdto;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping("/delrspcdbyifid.do")
	public @ResponseBody
	ResponseDto deleteReturnCodeByIfId(@RequestBody ReturnCodeReqDto dto) {
		debug("调用deleteReturnCodeByIfId");
		ResponseDto rdto = new ResponseDto();
		int ifId = dto.getIfId();
		if (ifId == 0) {
			rdto.setRspCd(CodeItem.IF_ID_FAILURE);
			return rdto;
		}
		boolean flag = returnCodeService.deleteReturnCodeByIfId(ifId);
		if (flag) {
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
		}
		return rdto;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description="删除时触发",event="onDelete")
	@RequestMapping("/delrspcdbyid.do")
	public @ResponseBody ResponseDto deleteReturnCodeById(@RequestBody ReturnCodeReqDto dto, HttpSession session) {
		debug("调用deleteReturnCodeById");
		ResponseDto rdto = new ResponseDto();
		int id = dto.getId();
		if (id == 0) {
			rdto.setRspCd(CodeItem.ID_FAILURE);
			return rdto;
		}
		boolean flag = returnCodeService.deleteReturnCodeById(id);
		if (flag) {
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
		}
		return rdto;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping("/updaterspcdbyid.do")
	public @ResponseBody ResponseDto updateReturnCodeById(@RequestBody ReturnCodeReqDto dto) {
		debug("调用updateReturnCodeById");
		ResponseDto rdto = new ResponseDto();
		String rcode = dto.validation();
		if (!"00000".equals(rcode)) {
			rdto.setRspCd(rcode);
			return rdto;
		}
		ReturnCode returnCode = null;
		try {
			returnCode = createBO(dto, ReturnCode.class);
		} catch (PendingException e) {
			rdto.setRspCd(CodeItem.IF_FAILURE);
			debug("创建BO失败");
			e.printStackTrace();
			return rdto;
		}
		boolean flag = returnCodeService.updateReturnCodeById(returnCode);
		if (flag) {
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
		}
		return rdto;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping("/getrspcdbyifid.do")
	public @ResponseBody
    RetrunCodeRspDto getReturnCodeByIfId(@RequestBody ReturnCodeReqDto dto) {
		debug("调用getReturnCodeByIfId");
		RetrunCodeRspDto rdto = new RetrunCodeRspDto();
		int ifId = dto.getIfId();
		if (ifId == 0) {
			rdto.setRspCd(CodeItem.IF_ID_FAILURE);
			return rdto;
		}
		List<ReturnCode> list = returnCodeService.getReturnCodeByIfId(ifId);
		if (list != null) {
			rdto.setList(list);
			rdto.setRspCd(SysCode.SUCCESS);
		} else {
			rdto.setRspCd(CodeItem.IF_FAILURE);
		}
		return rdto;
	}


	/**
	 * 批量增加接口返回码
	 * @param qdto 请求参数
	 * @return 返回参数
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description="更新时触发",event="onUpdate")
	@RequestMapping(value = "/updateIFRspCode.do")
	public @ResponseBody ResponseDto updateIFRspCode(@RequestBody IFFieldsReqDto qdto , HttpSession session) {
		IFFieldsRspDto reDto = new IFFieldsRspDto();

		List<ReturnCode> rspCodeList = qdto.getRspCodeList();
		int ifId = qdto.getIfId();
		int count;
		try{
			// 批量储存rspcode
			count = returnCodeService.updateIfRspCode(rspCodeList,ifId);
			rspCodeList = returnCodeService.getReturnCodeByIfId(ifId);
			reDto.setIfId(qdto.getIfId());
			reDto.setRspCodeList(rspCodeList);
			// 重新设置储存结果
			if(count>0){
				reDto.setRspCd(SysCode.SUCCESS);
			}else{
				reDto.setRspInf(SysCode.INSERT_RESULT_0);
			}
		}catch (Exception e){
			reDto.setRspCd(CodeItem.IF_FAILURE);
		}

		return reDto;

	}
}
