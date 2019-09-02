package com.gigold.pay.ifsys.aop.changeslog;

import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.RequestDto.IFFieldsReqDto;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.service.InterFaceChangesService;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import com.gigold.pay.ifsys.service.InterFaceService;
import com.gigold.pay.ifsys.service.ReturnCodeService;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.SessionTreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
/**
 * 记录变更日志
 *
 */
public class ChangesLogBeforeService extends BaseAspect {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Autowired
	private InterFaceChangesService interFaceChangesService;
	@Autowired
	private InterFaceService interFaceService;
	@Autowired
	private InterFaceFieldService interFaceFieldService;
	@Autowired
	private ReturnCodeService returnCodeService;

	@Before("serviceAspect()")
	public void doBeforeService(JoinPoint joinPoint){
		String targetName = joinPoint.getTarget().getClass().getName();
		Object argsObj = joinPoint.getArgs()[0];
		String event = ChangesUtil.getEvent(joinPoint);
		// 获取操作者ID
		int userId = SessionTreadLocal.getUserId();
		/**
		 * 接口参数修改事件触发
		 */
		if("com.gigold.pay.ifsys.service.InterFaceFieldService".equalsIgnoreCase(targetName)){
			// 获取参数基本类型
			List<Map> dicList = interFaceFieldService.getFeildDicTypeList();
			Map<String,String> dicMap = new HashMap<>();
			for(Map dic :dicList){
				String id = String.valueOf(dic.get("id"));
				String name = String.valueOf(dic.get("name"));
				String desc =  String.valueOf(dic.get("desc"));
				if(StringUtil.isNotEmpty(id)){
					dicMap.put(id,desc+"["+name+"]");
				}
			}

			// 监听
			// 参数更新时
			if ("onUpdate".equals(event)) {
				try {
					int ifId=0,ifFieldId=0;
					String fieldFlag,fieldType,fieldName,parent,fieldDesc,fieldMock;
					InterFaceField newField=null,oldField=null;

					if(argsObj instanceof  IFFieldsReqDto){
						IFFieldsReqDto ifFieldsReqDto = ((IFFieldsReqDto) argsObj);
						List<IFFields> fieldsList = ifFieldsReqDto.getFieldsList();
						fieldFlag = ifFieldsReqDto.getFieldType();
						ifId = ifFieldsReqDto.getIfId();
						// 变更表
						List<InterFaceChanges> changesList = new ArrayList<>();
						for(IFFields ifFields : fieldsList) {
							// 逐个初始化 IFFields 对象为 InterFaceField 对象
							newField = new InterFaceField();
							ifFieldId= ifFields.getId();
							fieldName = ifFields.getK();
							fieldType = ifFields.getType();
							parent = ifFields.getParent();
							fieldDesc = ifFields.getNote();
							fieldMock = ifFields.getMock();

							newField.setId(ifFieldId);
							newField.setIfId(ifId);
							newField.setFieldCheck(fieldType);
							newField.setFieldFlag(fieldFlag);
							newField.setFieldName(fieldName);
							newField.setFieldDesc(fieldDesc);
							newField.setFieldReferValue(fieldMock);

							// 获取老参数
							if(ifFieldId>0) {
								oldField = interFaceFieldService.getInterfaceFieldById(ifFieldId);
							}else{
								oldField = null;
							}

							// 比较新老参数变更
							List<InterFaceChanges> currentChanges = ChangesUtil.compareIfFieldListChanges(oldField, newField,dicMap, userId,ifId);
							if(currentChanges!=null && currentChanges.size()>0)changesList.addAll(currentChanges);

						}

						// 记录变更
						if (changesList.size() > 0){
							interFaceChangesService.recordChanges(changesList);
						}

					}else{
						debug("更新操作参数不符合切点要求");
					}

				}catch (Exception e){
					e.printStackTrace();
				}

			}

			// 删除时
			if ("onDelete".equals(event)) {
				try {
					int ifId=0,ifFieldId=0;
					String fieldType,fieldName="空",fieldFlag;
					InterFaceField newField=new InterFaceField(),oldField=null;
					// 获取新参数信息
					if(argsObj instanceof  InterFaceField){
						newField = ((InterFaceField) argsObj);
						fieldType = newField.getFieldType();
						ifId = newField.getIfId();
						ifFieldId= newField.getId();
						fieldName = newField.getFieldName();
					} else{
						debug("删除操作参数不符合切点要求");
					}

					// 记录变更
					fieldFlag = newField.getFieldFlag();
					if("1".equals(fieldFlag))fieldFlag="请求";
					if("2".equals(fieldFlag))fieldFlag="返回";
					InterFaceChanges changes = new InterFaceChanges();
					changes.setIfId(ifId);
					changes.setUserId(userId);
					changes.setOptionType("删除");
					changes.setChangeType("<"+fieldFlag+">参数 \""+fieldName+"\" 及其子节点");

					// 记录变更
					interFaceChangesService.recordChanges(changes);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}


		/**
		 * 返回码变更
		 */
		if("com.gigold.pay.ifsys.service.ReturnCodeService".equalsIgnoreCase(targetName)) {
			// 删除时
			if ("onDelete".equals(event) && argsObj instanceof Integer) {
				int rspId = (Integer)argsObj;
				ReturnCode returnCode = returnCodeService.getReturnCodeById(rspId);
				InterFaceChanges changes = new InterFaceChanges();
				if(returnCode!=null){
					changes.setIfId(returnCode.getIfId());
					changes.setUserId(userId);
					changes.setOptionType("删除");
					changes.setChangeType("返回码:\""+returnCode.getRspCode()+"\"["+returnCode.getRspCodeDesc()+"]");
				}
				interFaceChangesService.recordChanges(changes);

			}

			// 更新/新增时
			if ("onUpdate".equals(event) && argsObj instanceof List) {
				List<ReturnCode> returnCodeListNew = (List<ReturnCode>)argsObj;
				int ifId = (Integer)joinPoint.getArgs()[1];

				if(returnCodeListNew.size()>0){
					// 初始化返回码的接口ID
					for(ReturnCode returnCode : returnCodeListNew) {
						returnCode.setIfId(ifId);
					}

					// 比较新老变更
					List<InterFaceChanges> changesList = new ArrayList<>();
					for(ReturnCode returnCodeNew : returnCodeListNew) {
						int rspId = returnCodeNew.getId();
						// 获取原始值
						ReturnCode returnCodeOld = returnCodeService.getReturnCodeById(rspId);

						// 获取变更
						List<InterFaceChanges> currentChanges = ChangesUtil.compareReturnCodeListChanges(returnCodeOld, returnCodeNew, userId, ifId);

						// 若有变更则记录
						if (currentChanges != null && currentChanges.size() > 0) changesList.addAll(currentChanges);
					}

					// 入库
					if (changesList.size() > 0){
						interFaceChangesService.recordChanges(changesList);
					}


				}
			}
		}
	}
}
