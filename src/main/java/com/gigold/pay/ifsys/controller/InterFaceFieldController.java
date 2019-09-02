package com.gigold.pay.ifsys.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.RequestDto.IFFieldsReqDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceFieldReqDto;
import com.gigold.pay.ifsys.ResponseDto.IFFieldsRspDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceFieldResAddDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceFieldResJsonDto;
import com.gigold.pay.ifsys.ResponseDto.InterFaceFieldResListDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.TypeRole;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.bo.InterFaceField;
import com.gigold.pay.ifsys.bo.ReturnCode;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import com.gigold.pay.ifsys.util.ForMatJSONStr;

import javax.servlet.http.HttpSession;

@Controller
public class InterFaceFieldController extends BaseController {
	@Autowired
	InterFaceFieldService interFaceFeildService;
	@Autowired
	ReturnCodeService returnCodeService;

	/**
	 * @return the interFaceFeildService
	 */
	public InterFaceFieldService getInterFaceFeildService() {
		return interFaceFeildService;
	}

	/**
	 * @param interFaceFeildService the interFaceFeildService to set
	 */
	public void setInterFaceFeildService(InterFaceFieldService interFaceFeildService) {
		this.interFaceFeildService = interFaceFeildService;
	}

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

	/**
	 * 
	 * Title: getRspCode<br/>
	 * Description: 获取接口对应的返回码信息<br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月8日上午9:32:03
	 *
	 * @param ifId
	 * @return
	 */
	public String getRspCode(int ifId) {
		List<ReturnCode> rspCdList = returnCodeService.getReturnCodeByIfId(ifId);
		StringBuilder sbui = new StringBuilder();
		if (rspCdList != null) {
			int size = rspCdList.size();
			int i = 0;
			for (ReturnCode rspCd : rspCdList) {
				sbui.append(rspCd.getRspCode()).append(":").append(rspCd.getRspCodeDesc());
				if (i < size - 1) {
					sbui.append("; ");
				}
				i++;
			}
		}
		return sbui.toString();

	}

	/**
	 * 
	 * Title: getReqestInfoByIfId<br/>
	 * 根据接口ID获取接口请求部分的信息 转为JSON格式的字符串: <br/>
	 * 
	 * @author xb
	 * @date 2015年10月12日上午9:31:30
	 *
	 * @param qdto
	 * @return getRequestJson
	 */
	@Deprecated
	@Authority(TypeRole.Visitor)
	@RequestMapping(value = "/getInterFaceFieldsJson.do")
	public @ResponseBody
	InterFaceFieldResJsonDto getReqestInfoByIfId(@RequestBody InterFaceFieldReqDto qdto) {
		InterFaceField interFaceField = qdto.getInterFaceField();
		InterFaceFieldResJsonDto dto = new InterFaceFieldResJsonDto();
		if(interFaceField==null){
			dto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			return dto;
		}
		if(StringUtil.isBlank(interFaceField.getFieldFlag())||interFaceField.getIfId()==0){
			dto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			return dto;
		}
		if(!"1".equals(interFaceField.getFieldFlag())&&!"2".equals(interFaceField.getFieldFlag())){
			dto.setRspCd(CodeItem.INVAILD_PARM_FAILURE);
			return dto;
		}
		StringBuilder ss = new StringBuilder();
		ss.append("{");
		//设置响应字段 返回码
		if ("2".equals(interFaceField.getFieldFlag())) {
			ss.append("\"rspCd\"").append(":\"").append(getRspCode(interFaceField.getIfId())).append("\" /*返回码*/,");
		}
		List<InterFaceField> rlist = interFaceFeildService.getFirstReqFieldByIfId(interFaceField);
		if(rlist!=null){
		    proJSON(ss, rlist, interFaceField.getFieldCheck());
		}
		String jsonStr = ss.toString().replaceAll(",\\}", "}").replaceAll(",\\]", "]");
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
		
		dto.setRspCd(SysCode.SUCCESS);
		dto.setJsonStr(ForMatJSONStr.format(jsonStr));
		return dto;

	}

	/**
	 * 老版接口 即将废弃
	 * @param qdto
	 * @return
     */
	@Deprecated
	@Authority(TypeRole.Visitor)
	@RequestMapping(value = "/addInterFaceField.do")
	public @ResponseBody
	InterFaceFieldResAddDto addInterFaceField(@RequestBody InterFaceFieldReqDto qdto) {
		InterFaceFieldResAddDto dto = new InterFaceFieldResAddDto();
		InterFaceField interFaceField = qdto.getInterFaceField();
		boolean flag = interFaceFeildService.addInterFaceField(interFaceField);
		if (flag) {
			dto.setInterFaceField(interFaceField);
			dto.setRspCd(SysCode.SUCCESS);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;

	}

	@Deprecated
	@Authority(TypeRole.Visitor)
	@RequestMapping(value = "/getFieldByIfId.do")
	public @ResponseBody
	InterFaceFieldResListDto getFieldByIfId(@RequestBody InterFaceFieldReqDto qdto) {
		InterFaceFieldResListDto dto = new InterFaceFieldResListDto();
		InterFaceField interFaceField = qdto.getInterFaceField();
		List<InterFaceField> list = interFaceFeildService.getFieldByIfId(interFaceField);
		if (list != null) {
			dto.setList(list);
			dto.setRspCd(SysCode.SUCCESS);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;

	}

	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description = "删除参数",event = "onDelete")
	@RequestMapping(value = "/deleteFieldByLevel.do")
	public @ResponseBody
	ResponseDto deleteFieldByLevel(@RequestBody InterFaceFieldReqDto qdto , HttpSession session) {
		ResponseDto dto = new ResponseDto();
		InterFaceField interFaceField = qdto.getInterFaceField();
		int FeildId = interFaceField.getId();
		interFaceField = interFaceFeildService.getInterfaceFieldById(FeildId);
		boolean flag = interFaceFeildService.deleteFieldByLevel(interFaceField);
		if (flag) {
			dto.setRspCd(SysCode.SUCCESS);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;
	}

	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping(value = "/updateInterFaceField.do")
	public @ResponseBody ResponseDto updateInterFaceField(@RequestBody InterFaceFieldReqDto qdto) {
		ResponseDto dto = new ResponseDto();
		InterFaceField interFaceField = qdto.getInterFaceField();
		boolean flag = interFaceFeildService.updateInterFaceField(interFaceField);
		if (flag) {
			dto.setRspCd(SysCode.SUCCESS);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;

	}

	/**
	 * 
	 * Title: proJSON<br/>
	 * 将接口请求部分解析成JSON字符串: <br/>
	 * 
	 * @author xb
	 * @date 2015年10月12日上午9:32:51

	 */
	public void proJSON(StringBuilder ss, List<InterFaceField> list, String parentFieldCheck) {
		int i = 0;

		InterFaceField ff = null;
		for (i = 0; i < list.size(); i++) {
			ff = list.get(i);
			List<InterFaceField> clist = interFaceFeildService.getFieldByparentId(ff);
			ss.append("\"" + ff.getFieldName() + "\":");
			if (clist != null && clist.size() > 0) {
				if ("4".equals(ff.getFieldCheck())) {
					ss.append("[{");
				} else {
					ss.append("{" + "/*" + ff.getFieldDesc() + "*/\n");
				}
				proJSON(ss, clist, ff.getFieldCheck());
			} else {
				ss.append("" + ff.getFieldReferValue() + " /*" + ff.getFieldDesc() + "*/");
				if (i < list.size() - 1) {
					ss.append(",");
				}
			}
		}

		if (i > 0) {
			if (!StringUtils.isEmpty(parentFieldCheck) && parentFieldCheck.equals("4")) {
				ss.append("}]");
			} else {
				ss.append("}");
			}
			ss.append(",");
		}

	}


	/**
	 * 批量增加接口字段关系
	 * @param qdto 请求参数
	 * @return 返回参数
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description = "获取线程session",event = "onUpdate")
	@RequestMapping(value = "/updateIFFields.do")
	public @ResponseBody ResponseDto updateIFFields(@RequestBody IFFieldsReqDto qdto, HttpSession session) {
		IFFieldsRspDto reDto = new IFFieldsRspDto();
		List<Integer> idList = interFaceFeildService.updateIFFields(qdto);
		InterFaceField interFaceField = new InterFaceField();

		//重新查询出储存结果
		interFaceField.setIfId(qdto.getIfId());
		List<InterFaceField> fieldsList = interFaceFeildService.getFieldByIfId(interFaceField);
		List<IFFields> iFFields = new ArrayList<>();
		for(InterFaceField feild:fieldsList){
			IFFields feildReList = new IFFields();
			feildReList.setId(feild.getId());
			feildReList.setType(feild.getFieldCheck());
			feildReList.setNote(feild.getFieldDesc());
			feildReList.setMock(feild.getFieldReferValue());
			feildReList.setK(feild.getFieldName());
			feildReList.setParent(String.valueOf(feild.getParentId()));// 暂存为ID
			iFFields.add(feildReList);
		}

		if (idList!=null) {
			// 重新设置储存结果
			reDto.setFieldsList(iFFields);
			reDto.setIfId(qdto.getIfId());
			reDto.setFieldType(qdto.getFieldType());
			reDto.setRspCd(SysCode.SUCCESS);
		} else {
			reDto.setRspCd(CodeItem.IF_FAILURE);
		}
		return reDto;

	}

	/**
	 * 批量增加接口字段关系
	 * @param qdto 请求参数
	 * @return 返回参数
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@RequestMapping(value = "/getIFFields.do")
	public @ResponseBody
	IFFieldsRspDto getIFFields(@RequestBody Map<String,Integer> qdto) {
		IFFieldsRspDto reDto = new IFFieldsRspDto();// 构建返回
		InterFaceField interFaceField = new InterFaceField(); // 构建接口属性对象
		int ifId;
		String fieldType;
		try{
			ifId = qdto.get("ifId");
			if(ifId<=0) throw new Exception("ifId必须是存在的接口id");
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(CodeItem.IF_FAILURE);
			reDto.setRspInf(e.getMessage());
			return reDto;
		}

		//重新查询出储存结果
		interFaceField.setIfId(ifId);
		List<InterFaceField> fieldsList = interFaceFeildService.getFieldByIfId(interFaceField);
		// 查询出返回码的结果
		List<ReturnCode> rspCodeList = returnCodeService.getReturnCodeByIfId(ifId);
		List<Map> rspDicList = interFaceFeildService.getFeildDicTypeList();
		List<IFFields> iFFields = new ArrayList<>();
		for(InterFaceField feild:fieldsList){
			IFFields feildReList = new IFFields();
			feildReList.setId(feild.getId());
			feildReList.setType(feild.getFieldCheck());
			feildReList.setNote(feild.getFieldDesc());
			feildReList.setMock(feild.getFieldReferValue());
			feildReList.setK(feild.getFieldName());
			feildReList.setParent(String.valueOf(feild.getParentId()));// 暂存为ID
			feildReList.setFeildFlag(feild.getFieldFlag());// 暂存为ID
			iFFields.add(feildReList);
		}
		// 重新设置储存结果
		reDto.setRspDicList(rspDicList);
		reDto.setFieldsList(iFFields);
		reDto.setIfId(ifId);
		reDto.setRspCodeList(rspCodeList);
		reDto.setRspCd(SysCode.SUCCESS);

		return reDto;

	}


	/**
	 * 批量增加接口字段关系
	 * @return 返回参数
	 */
	@RequestMapping(value = "/getFeildDicTypeList.do")
	public @ResponseBody
	ResponseDto getFeildDicTypeList() {
		ResponseDto reDto = new ResponseDto();
		List<Map> feildDic;
		try{
			feildDic = interFaceFeildService.getFeildDicTypeList();
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
			return reDto;
		}
		reDto.setDataes(feildDic);
		return reDto;

	}

}
