package com.gigold.pay.ifsys.service;

import java.util.*;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.RequestDto.IFFieldsReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.ifsys.bo.InterFaceField;
import com.gigold.pay.ifsys.dao.InterFaceFieldDao;

@Service
public class InterFaceFieldService extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	InterFaceFieldDao interFaceFieldDao;

	/**
	 * @return the interFaceFieldDao
	 */
	public InterFaceFieldDao getInterFaceFieldDao() {
		return interFaceFieldDao;
	}

	/**
	 * @param interFaceFieldDao
	 *            the interFaceFieldDao to set
	 */
	public void setInterFaceFieldDao(InterFaceFieldDao interFaceFieldDao) {
		this.interFaceFieldDao = interFaceFieldDao;
	}

	/**
	 * 获取接口所有根节点字段
	 */
	public List<InterFaceField> getFirstReqFieldByIfId(InterFaceField interFaceField) {
		List<InterFaceField> list = null;
		try {
			list = interFaceFieldDao.getFirstReqFieldByIfId(interFaceField);
		} catch (Exception e) {
			debug("调用 interFaceFieldDao.getFirstReqFieldByIfId 出现异常");
		}
		return list;
	}

	/**
	 * 
	 * Title: getFieldByparentId<br/>
	 * Description: 获取某个字段的所有子字段<br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月17日下午4:20:53
	 *
	 * @param interFaceField
	 * @return
	 */

	public List<InterFaceField> getFieldByparentId(InterFaceField interFaceField) {
		List<InterFaceField> list = null;
		try {
			list = interFaceFieldDao.getFieldByparentId(interFaceField);
		} catch (Exception e) {
			debug("调用 interFaceFieldDao.getFieldByparentId 出现异常");
		}
		return list;
	}
    /**
     * 
     * Title: getFieldByIfId<br/>
     * Description: 获取接口所有的字段<br/>
     * @author xiebin
     * @date 2015年12月17日下午4:22:54
     *
     * @param interFaceField
     * @return
     */
	public List<InterFaceField> getFieldByIfId(InterFaceField interFaceField) {
		List<InterFaceField> list = null;
		try {
			list = interFaceFieldDao.getFieldByIfId(interFaceField);
		} catch (Exception e) {
			debug("调用 interFaceFieldDao.getFieldByIfId 出现异常");
		}
		return list;
	}
    /**
     * 
     * Title: addInterFaceField<br/>
     * Description: 新增一个字段<br/>
     * @author xiebin
     * @date 2015年12月17日下午4:24:35
     *
     * @param interFaceField
     * @return
     */
	@ChangesLog(description = "批量增加接口字段",event = "onUpdate")
	public boolean addInterFaceField(InterFaceField interFaceField) {
		boolean flag=false;
		List<InterFaceField> list =null;
		try{
			InterFaceField ifField = interFaceFieldDao.getFieldById(interFaceField);
			if (ifField != null) {
				interFaceField.setLevel(ifField.getLevel() + new Date().getTime() + "-" + ifField.getId() + "-");
			} else {
				interFaceField.setLevel(interFaceField.getParentId() + "-" + new Date().getTime() + "-");
			}
			
			int count=interFaceFieldDao.addInterFaceField(interFaceField);
			System.out.println("返回值:"+count);
			if(count>0){
				flag= true;
			}
		}catch(Exception e){
			debug("调用 interFaceFieldDao.getFieldById出现异常");
		}
		return flag;
		
	}

	/**
	 * 批量保存接口的字段信息
	 * @param ifFieldsReqDto 请求
	 * @return 返回是否成功
     */
	@ChangesLog(description = "批量增加接口字段",event = "onUpdate")
	public List<Integer> updateIFFields(IFFieldsReqDto ifFieldsReqDto) {
		List<Integer> fieldIdList=new ArrayList<>(); // 保存后的ID列表
		try{
			int ifId = ifFieldsReqDto.getIfId();
			String fieldType = ifFieldsReqDto.getFieldType();
			List<IFFields> feildlist = ifFieldsReqDto.getFieldsList();

			// TODO: 2016/11/3   /* 开始进行批量操作 */
			Map<String, Integer> _parentMap = new HashMap<>();
			while (feildlist.size()>0){
				feildlist = saveFeilds(ifId,fieldType,feildlist,_parentMap);
			}
			// TODO: 2016/11/3   /* 结束批量操作 */

			// 返回保存的ID列表
			for(String feild : _parentMap.keySet()){
				if(_parentMap.containsKey(feild)){
					fieldIdList.add(_parentMap.get(feild));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			debug("调用 interFaceFieldDao.getFieldById出现异常");
		}
		return fieldIdList;

	}

	/**
	 * 循环存储字段方法
	 * @param ifId 目标接口id
	 * @param fieldType 入参还是出参
	 * @param feildlist 参数表
	 * @param parentMap 父级id表
	 * @return 返回剩余节点
     * @throws Exception
     */
	public List<IFFields> saveFeilds(int ifId, String fieldType , List<IFFields> feildlist, Map<String, Integer> parentMap) throws Exception {
		// 确定进入时列表大小,防止死循环
		int listSize = feildlist.size();

		// 进行一轮存储
		List<IFFields> _otherLevel = new ArrayList<>(); // 存放最终剩余节点
		for(IFFields ifField: feildlist){
			String parentKey = ifField.getParent();
			String currntKey = ifField.getK();
			String currntType = ifField.getType();
			boolean isReq = ifField.isReq();
			if(parentMap.containsKey(parentKey)||StringUtil.isEmpty(parentKey)){
				// 若父节点存在,或者本身就是父节点,则存储当前节点
				int parentId = StringUtil.isEmpty(parentKey)?0:parentMap.get(parentKey);
				// 构建一条field
				InterFaceField interFaceField = new InterFaceField();
				// 若传入了id则设置id
				if(ifField.getId()>=0) interFaceField.setId(ifField.getId());
				interFaceField.setFieldName(currntKey);
				interFaceField.setFieldDesc(ifField.getNote());
				interFaceField.setParentId(parentId);
				interFaceField.setFieldCheck(currntType);
				interFaceField.setIfId(ifId);
				interFaceField.setFieldFlag(fieldType);
				interFaceField.setFieldReq(isReq);
				interFaceField.setFieldReferValue(ifField.getMock());
				// 储存
				this.addInterFaceField(interFaceField);
				int fieldId = interFaceField.getId();
				// 拿到储存后的id
				String fullKey = StringUtil.isEmpty(parentKey)?currntKey:parentKey+"."+currntKey;
				parentMap.put(fullKey,fieldId);
			}else{
				// 若父节点不存在,则将放到下一轮
				_otherLevel.add(ifField);
			}
		}

		// 返回时比较列表大小,防止死循环 
		if(_otherLevel.size() >= listSize){
			throw new Exception("字段可能循环引用");
		}else{
			return _otherLevel;
		}
	}

	/**
	 *
	 * Title: deleteFieldByLevel<br/>
	 * Description: <br/>
	 * @author xiebin
	 * @date 2015年12月17日下午4:35:40
	 *
	 * @param interFaceField
	 * @return
	 */
	@ChangesLog(description = "批量增加接口字段",event = "onDelete")
	public boolean deleteFieldByLevel(InterFaceField interFaceField) {
		boolean flag=false;
		try{
			int count=interFaceFieldDao.deleteFieldByLevel(interFaceField);
			if(count>0){
				flag= true;
			}
		}catch(Exception e){
			debug("调用 interFaceFieldDao.deleteFieldByLevel出现异常");
		}
		return flag;
	}



   /**
    * 
    * Title: updateInterFaceField<br/>
    * Description: 修改接口字段<br/>
    * @author xiebin
    * @date 2015年12月17日下午4:47:05
    *
    * @param interFaceField
    * @return
    */
   public boolean updateInterFaceField(InterFaceField interFaceField) {
		boolean flag=false;
		try{
			int count=interFaceFieldDao.updateInterFaceField(interFaceField);
			if(count>0){
				flag= true;
			}
		}catch(Exception e){
			debug("调用 interFaceFieldDao.updateInterFaceField出现异常");
		}
		return flag;
	}

	public List<Map> getFeildDicTypeList(){
		List<Map> list = null;
		try{
			list = interFaceFieldDao.getFeildDicTypeList();
		}catch (Exception e){
			e.getStackTrace();
		}
		return list;
	}

	/**
	 * 根据feildid获取feild对象
	 * @param feildId
	 * @return
     */
	public InterFaceField getInterfaceFieldById(int feildId) {
		InterFaceField interFaceField = new InterFaceField();
		try {
			interFaceField = interFaceFieldDao.getInterfaceFieldById(feildId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return interFaceField;
	}

	/**
	 * 批量删除字段
	 * @param ids
     */
	public void deleteFields(Integer[] ids){
		interFaceFieldDao.deleteFields(ids);
	}
}
