package com.gigold.pay.ifsys.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.core.exception.PendingException;
import com.gigold.pay.ifsys.RequestDto.*;
import com.gigold.pay.ifsys.ResponseDto.*;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.service.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gigold.pay.framework.base.SpringContextHolder;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.util.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
public class InterFaceController extends BaseController {
	@Autowired
	InterFaceService interFaceService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	InterFaceSysService interFaceSysService;
	@Autowired
	InterFaceProService interFaceProService;
	@Autowired
	InterFaceFieldService interFaceFieldService;
	@Autowired
	ReturnCodeService returnCodeService;
	@Autowired
	InterFaceEditionService interFaceEditionService;
	@Autowired
	InterFaceInvokerService interFaceInvokerService;

	/**
	 * @param interFaceService
	 *            the interFaceService to set
	 */
	public void setInterFaceService(InterFaceService interFaceService) {
		this.interFaceService = interFaceService;
	}

	/**
	 * @param userInfoService
	 *            the userInfoService to set
	 */
	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	/**
	 * @param interFaceSysService
	 *            the interFaceSysService to set
	 */
	public void setInterFaceSysService(InterFaceSysService interFaceSysService) {
		this.interFaceSysService = interFaceSysService;
	}

	/**
	 * @param interFaceProService
	 *            the interFaceProService to set
	 */
	public void setInterFaceProService(InterFaceProService interFaceProService) {
		this.interFaceProService = interFaceProService;
	}

	/**
	 * @param interFaceFieldService
	 *            the interFaceFieldService to set
	 */
	public void setInterFaceFieldService(InterFaceFieldService interFaceFieldService) {
		this.interFaceFieldService = interFaceFieldService;
	}

	/**
	 * 根据Id获取接口明细信息 用户接口修改页
	 */
	@Authority(TypeRole.Visitor)
	@RequestMapping(value = "/queryInterFaceById")
	public @ResponseBody
    InterFaceByIdResponseDto getInterFaceById(@RequestBody InterFaceRequestDto qdto, HttpSession session) {
		InterFaceByIdResponseDto dto = new InterFaceByIdResponseDto();
		InterFaceInfo interFaceInfo = qdto.getInterFaceInfo();
		if (interFaceInfo == null || interFaceInfo.getId() == 0) {
			dto.setRspCd(CodeItem.IF_ID_FAILURE);
			return dto;
		}

		// 获取操作者信息
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		// 获取接口信息
		interFaceInfo = interFaceService.getInterFaceById(qdto.getInterFaceInfo());
		if (interFaceInfo == null) {
			dto.setRspCd(CodeItem.IF_FAILURE);
			dto.setRspInf("接口 "+qdto.getInterFaceInfo().getId()+" 不存在");
			return dto;
		}
		// 获取所属系统信息
		InterFaceSysTem interFaceSysTem = (InterFaceSysTem) SpringContextHolder.getBean(InterFaceSysTem.class);
		interFaceSysTem.setId(interFaceInfo.getIfSysId());
		interFaceSysTem = interFaceSysService.getSysInfoById(interFaceSysTem);
		if (interFaceSysTem == null) {
			dto.setRspCd(CodeItem.IF_FAILURE);
			dto.setRspInf("系统 "+qdto.getInterFaceInfo().getIfSysId()+" 不存在");
			return dto;
		}
		// 获取所属产品信息
		InterFacePro interFacePro = (InterFacePro) SpringContextHolder.getBean(InterFacePro.class);
		interFacePro.setId(interFaceInfo.getIfProId());
		interFacePro = interFaceProService.getProInfoById(interFacePro);
		if (interFacePro == null) {
			dto.setRspCd(CodeItem.IF_FAILURE);
			dto.setRspInf("产品 "+qdto.getInterFaceInfo().getIfProId()+" 不存在");
			return dto;
		}
		// 获取设计者信息
		UserInfo userInfo = (UserInfo) SpringContextHolder.getBean(UserInfo.class);
		userInfo.setId(interFaceInfo.getUid());
		userInfo = userInfoService.getUser(interFaceInfo.getUid());
		if (userInfo == null) {
			userInfo = new UserInfo();
			userInfo.setUserName("未知");
			userInfo.setId(0);
		}
		// 获取接口定义字段信息
		InterFaceField interFaceField = (InterFaceField) SpringContextHolder.getBean(InterFaceField.class);
		interFaceField.setIfId(interFaceInfo.getId());
		List<InterFaceField> fieldList = interFaceFieldService.getFieldByIfId(interFaceField);
		if (fieldList == null) {
			dto.setRspCd(CodeItem.IF_FAILURE);
			return dto;
		}
		
		// 获取关注者信息
		List<InterFaceInvoker> invokers = interFaceInvokerService.getInvokerListByIfId(interFaceInfo.getId());
		// 设置本人是否关注
		boolean isFollow=false;
		for(InterFaceInvoker invoker : invokers){
			if(user!=null && user.getId() == invoker.getuId()){
				isFollow = true;break;
			}
		}
		// 设置本人是否关注
		dto.setFollow(isFollow);
		// 设置接口信息
		dto.setInterFaceInfo(interFaceInfo);
		// 设置接口所属系统信息
		dto.setSystem(interFaceSysTem);
		// 设置接口所属产品信息
		dto.setPro(interFacePro);
		// 设置接口设计者信息
		dto.setUserInfo(userInfo);
		// 设置接口关注者信息
		dto.setInvokers(invokers);
		// 设置接口请求、响应字段信息
		dto.setFieldList(fieldList);
		dto.setRspCd(SysCode.SUCCESS);
		return dto;
	}

	/**
	 * 
	 * Title: queryInterFaceByPage<br/>
	 * Description: 查询接口列表<br/>
	 */
	@Deprecated
	@Authority(TypeRole.Admin)
	@RequestMapping("/queryByCondition")
	public @ResponseBody
	InterFacePageResponseDto queryInterFaceByPage(
			@RequestBody InterFaceFuzzyQueryRequestDto qdto) {
		int pageSize = Integer.parseInt(
				SystemPropertyConfigure.getProperty(Constant.SYSTEMPARAM_PAGESIZE, String.valueOf(Constant.PAGE_SIZE)));
		PageHelper.startPage(qdto.getPageInfo().getPageNum(), pageSize);
		List<InterFaceInfo> list = interFaceService.queryInterFaceByPage(qdto.getInterFaceInfo());

		InterFacePageResponseDto dto = new InterFacePageResponseDto();
		if (list != null) {
			PageInfo<InterFaceInfo> pageInfo = new PageInfo<>(list);
			dto.setPageInfo(pageInfo);
			dto.setRspCd(SysCode.SUCCESS);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;
	}

	/**
	 * 新增接口基本信息
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@Notice
	@RequestMapping("/addInterface")
	public @ResponseBody
	InterFaceResponseDto addInterface(@RequestBody InterFaceRequestDto qdto, HttpSession session) {
		InterFaceResponseDto dto = new InterFaceResponseDto();
		InterFaceInfo interFaceInfo = qdto.getInterFaceInfo();
		List<Integer> invokerList = qdto.getInvokerList();

		//参数校验
		if(interFaceInfo==null){
			dto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			return dto;
		}
		// 参数校验
		if(invokerList==null){
			invokerList = new ArrayList<>();
		}
		// 参数校验
		if(StringUtil.isBlank(interFaceInfo.getIfName())
				||StringUtil.isBlank(interFaceInfo.getIfProtocol())
				||StringUtil.isBlank(interFaceInfo.getIfUrl())
				||0==interFaceInfo.getIfSysId()
			    ||0==interFaceInfo.getIfProId()){
			dto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			return dto;
		}
		// 操作者判断
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		if (user != null) {
			interFaceInfo.setIfCreateBy(user.getId());
		}
		interFaceInfo.setIfCreateTime(new Timestamp((new Date()).getTime()));
		// 新增接口
		boolean flag = interFaceService.addInterFace(interFaceInfo);
		// 获取描述信息
		String remark = interFaceInfo.getIfDesc();
		if(StringUtil.isBlank(remark))remark="提醒关注";
		// 新增关注
		try{
			interFaceInvokerService.addInterFaceInvokers(interFaceInfo.getId(),invokerList,remark);
		}catch (Exception e){
			e.printStackTrace();
		}

		if (flag) {
			dto.setRspCd(SysCode.SUCCESS);
			dto.setInterFaceInfo(interFaceInfo);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}

		return dto;

	}

	/**
	 * 批量新增接口基本信息
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description="新增接口时触发",event="onAdd")
	@RequestMapping("/addInterfaces.do")
	public @ResponseBody
	ResponseDto addInterfaces(@RequestBody InterFacesRequestDto qdto, HttpSession session) {

		ResponseDto reDto = new ResponseDto();
		reDto.setRspCd(SysCode.SUCCESS);
		try{
			List<InterFaceInfo> ifInfos = qdto.getInterFacesInfo();
			InterFaceRequestDto interFaceRequestDto = new InterFaceRequestDto();
			for(InterFaceInfo ifInfo:ifInfos){
				try {
					interFaceRequestDto.setInterFaceInfo(ifInfo);
					addInterface(interFaceRequestDto,session);
				}catch (Exception e){
					debug("添加单个接口失败"+ifInfo.getIfName());
					reDto.setRspCd(SysCode.SYS_FAIL);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			reDto.setRspCd(SysCode.SYS_FAIL);
		}
		return 	reDto;

	}

	/**
	 * 删除接口
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Authority(TypeRole.Member)
	@ChangesLog(description="删除时触发",event="onDelete")
	@RequestMapping("/deleteInterFaceById")
	public @ResponseBody InterFaceResponseDto deleteInterFaceById(@RequestBody InterFaceRequestDto qdto, HttpSession session) {
		InterFaceResponseDto dto = new InterFaceResponseDto();
		InterFaceInfo interFaceInfo=qdto.getInterFaceInfo();
		if(interFaceInfo==null||interFaceInfo.getId()==0){
			dto.setRspCd(CodeItem.IF_ID_FAILURE);
			return dto;
		}
		boolean flag = interFaceService.deleteInterFaceById(qdto.getInterFaceInfo());
		if (flag) {
			dto.setRspCd(SysCode.SUCCESS);

		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;

	}

	/**
	 * 修改接口基本信息
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Deprecated
	@Authority(TypeRole.Member)
	@ChangesLog(description="更新时触发",event="onUpdate")
	@RequestMapping("/updateInterFace")
	public @ResponseBody InterFaceResponseDto updateInterFace(@RequestBody InterFaceRequestDto qdto, HttpSession session) {
		InterFaceInfo interFaceInfo = qdto.getInterFaceInfo();
		InterFaceResponseDto dto = new InterFaceResponseDto();
		List<Integer> invokerList = qdto.getInvokerList();
		// 参数校验
		if(invokerList==null){
			invokerList = new ArrayList<>();
		}
		
		if(interFaceInfo==null||interFaceInfo.getId()==0){
			dto.setRspCd(CodeItem.IF_ID_FAILURE);
			return dto;
		}
		boolean flag = interFaceService.updateInterFace(interFaceInfo);

		// 获取描述信息
		String remark = interFaceInfo.getIfDesc();
		if(StringUtil.isBlank(remark))remark="提醒关注";
		interFaceInvokerService.addInterFaceInvokers(interFaceInfo.getId(),invokerList,remark);
		
		if (flag) {
			dto.setRspCd(SysCode.SUCCESS);
			dto.setInterFaceInfo(interFaceInfo);
		} else {
			dto.setRspCd(CodeItem.IF_FAILURE);
		}
		return dto;

	}

	/**
	 * 分页获取系统下的接口信息
	 * @author chenkuan
	 * @date 2017/02/10
	 * @return 返回接口信息列表
	 */
	@Authority(TypeRole.Visitor)
	@RequestMapping("/getallifsys.do")
	public @ResponseBody
	IfSysMockRspDto getAllIfSys(@RequestBody IfSysMockPageDto dto , HttpSession session) {
		IfSysMockRspDto reDto = new IfSysMockRspDto();
		int curPageNum = dto.getPageNum();
		PageInfo<InterFaceInfo> pageInfo = null;
		PageHelper.startPage(curPageNum, Integer.parseInt(SystemPropertyConfigure.getProperty("sys.pageSize")));
		InterFaceInfo interFaceInfo = null;
		try {
			interFaceInfo = createBO(dto, InterFaceInfo.class);
		} catch (PendingException e) {
			debug("转换bo异常");
			e.printStackTrace();
		}

		if(dto.getPid()<=0){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspCd("请提交项目ID");
			return reDto;
		}
		List<InterFaceInfo> list = null;

		try{
			// 操作者判断
			UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
			int userId = 0;
			if (user != null) {
				userId = user.getId();
			}

			list = interFaceService.getAllIfSys(userId,interFaceInfo);
			for(InterFaceInfo eachIfInfo : list){
				// 加入重组的 mocklist,去掉一些无用信息
				List<Map> mockidlist  = interFaceService.getInterfaceMocksById(eachIfInfo.getId());
				// 若判断没有用例,则同样设置不在测试中
				if(mockidlist.size()<=0)eachIfInfo.setInAutoTest("N");
				eachIfInfo.setMockidList(mockidlist);
			}
			pageInfo = new PageInfo<>(list);
			reDto.setPageInfo(pageInfo);
			reDto.setRspCd(SysCode.SUCCESS);
		}catch (Exception e){
			reDto.setRspCd(SysCode.SYS_FAIL);
		}

		return reDto;
	}

	/**
	 * 统一保存接口详情
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Authority(TypeRole.Member)
	@ChangesLog
	@Notice
	@RequestMapping("/saveInterfaceDetail.do")
	public @ResponseBody
	InterFaceDetailRspDto saveInterfaceDetail(@RequestBody InterFaceDetailReqDto dto, HttpSession session) {
		InterFaceDetailRspDto reDto = new InterFaceDetailRspDto();
		UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
		int userId = user!=null?user.getId():0;

		if (!dto.validate()){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("请求参数错误");
			return reDto;
		}
		// 接口基本信息
		InterFaceInfo interFaceInfo = dto.getInterFaceInfo();
		// 接口id
		int ifId = dto.getIfId();
		List<Integer> invokerList = dto.getInvokerList();
		//参数校验
		if(interFaceInfo==null){
			reDto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			reDto.setRspInf("interfaceInfo未提交");
			return reDto;
		}
		// 设置接口ID
		if(ifId>0)
		interFaceInfo.setId(ifId);
		// 参数校验
		if(invokerList==null){
			invokerList = new ArrayList<>();
		}
		// 参数校验
		if(StringUtil.isBlank(interFaceInfo.getIfName())
				||StringUtil.isBlank(interFaceInfo.getIfProtocol())
				||StringUtil.isBlank(interFaceInfo.getIfUrl())
				||0==interFaceInfo.getIfSysId()
				||0==interFaceInfo.getIfProId()){
			reDto.setRspCd(CodeItem.NEDD_ITEM_FAILURE);
			reDto.setRspInf("基本信息不全");
			return reDto;
		}


		// 操作者判断
		if (user != null) {
			interFaceInfo.setIfCreateBy(user.getId());
		}
		interFaceInfo.setIfCreateTime(new Timestamp((new Date()).getTime()));
		// 判断新增还是更新
		boolean flag,isCreate;
		if (ifId > 0) {
			// 更新接口
			isCreate = false;
			flag = interFaceService.updateInterFace(interFaceInfo);
		}else{
			// 新增接口
			isCreate = true;
			flag = interFaceService.addInterFace(interFaceInfo);
			ifId = interFaceInfo.getId();
		}

		if(ifId<=0){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("接口基本信息保存失败");
			return reDto;
		}
		// 获取描述信息
		String remark = interFaceInfo.getIfDesc();
		if(StringUtil.isBlank(remark))remark="提醒关注";
		// 新增关注
		try{
			interFaceInvokerService.addInterFaceInvokers(ifId,invokerList,remark);
		}catch (Exception e){
			e.printStackTrace();
		}

		// 保存大三元
		List<IFFields> reqFields = dto.getReqFields();
		List<IFFields> rspFields = dto.getRspFields();
		List<ReturnCode> returnCodes = dto.getReturnCodes();

		IFFieldsReqDto ifFields = new IFFieldsReqDto();
		ifFields.setIfId(ifId);
		InterFaceEdition edition;
		InterFaceDetail detail = null;
		try {
			if (!dto.isAdvance() || isCreate) {
				// 常规更新
				String summary = saveInterfaceDetail(ifId, reqFields, rspFields, returnCodes);
				// 保存版本
				edition = interFaceEditionService.saveEdition(ifId, summary, userId);
			} else {
				// 推进更新
				for (IFFields field : reqFields) {field.setId(0);}
				for (IFFields field : rspFields) {field.setId(0);}
				for (ReturnCode code : returnCodes) {code.setId(0);}
				String summary = saveInterfaceDetail(ifId, reqFields, rspFields, returnCodes);
				// 保存版本
				edition = interFaceEditionService.advanceEdition(ifId, summary, userId);
			}
			detail = interFaceEditionService.getInterfaceDetailByEdition(ifId, edition.getIfVerNo());
			reDto.setRspCd(SysCode.SUCCESS);
			reDto.setEdition(edition);
			reDto.setDetail(detail);
		}catch (Exception e){
			e.printStackTrace();
		}
		return reDto;
	}

	/**
	 * 统一获取接口详情
	 * @author chenkuan
	 * @date 2016/11/06
	 */
	@Authority(TypeRole.Visitor)
	@RequestMapping("/getInterfaceDetail.do")
	public @ResponseBody
	InterFaceDetailRspDto getInterfaceDetail(@RequestBody InterFaceDetailReqDto dto, HttpSession session) {
		InterFaceDetailRspDto reDto = new InterFaceDetailRspDto();
		int ifId = dto.getIfId();
		String version = dto.getVersion();
		if(ifId<0){
			reDto.setRspCd(SysCode.SYS_FAIL);
			reDto.setRspInf("请提交接口ID");
		}

		if(StringUtil.isEmpty(version)){
			// version 为空的情况
			// 查询当前接口version
			InterFaceInfo interFaceInfo = new InterFaceInfo();
			interFaceInfo.setId(ifId);
			interFaceInfo = interFaceService.getInterFaceById(interFaceInfo);
			version = interFaceInfo.getIfVersionNo();
		}

		// version 已不为空的情况
		InterFaceDetail ifDetail = interFaceEditionService.getInterfaceDetailByEdition(ifId, version);
		reDto.setDetail(ifDetail);
		reDto.setRspCd(SysCode.SUCCESS);

		return reDto;
	}


	/**
	 * 保存接口细节,然后返回 json 摘要
	 * @param ifId 接口id
	 * @param reqFields 请求参数列表
	 * @param rspFields 返回参数列表
	 * @param returnCodes 返回码列表
     * @return json摘要
	 * @author chenkuan
	 * @date 2016/11/09
     */
	private String saveInterfaceDetail(int ifId, List<IFFields> reqFields, List<IFFields> rspFields, List<ReturnCode> returnCodes){
		int count;
		List<Integer> reqFieldId = new ArrayList<>();
		List<Integer> rspFieldId = new ArrayList<>();
		List<Integer> returnCodeId = new ArrayList<>();
		IFFieldsReqDto ifFields = new IFFieldsReqDto();
		ifFields.setIfId(ifId);

		// TODO: 2016/11/2 事务控制
		// 入参
		ifFields.setFieldType("1");
		ifFields.setFieldsList(reqFields);
		reqFieldId = interFaceFieldService.updateIFFields(ifFields);

		// 出参
		ifFields.setFieldType("2");
		ifFields.setFieldsList(rspFields);
		rspFieldId = interFaceFieldService.updateIFFields(ifFields);

		// 返回码
		count = returnCodeService.updateIfRspCode(returnCodes,ifId);
		if(count>0){
			for(ReturnCode returnCode :returnCodes){
				returnCodeId.add(returnCode.getId());
			}
		}
		// TODO: 2016/11/2 事务控制-结束

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("req",reqFieldId);
		jsonObject.put("rsp",rspFieldId);
		jsonObject.put("rspCd",returnCodeId);
		return jsonObject.toString();
	}

}
