package com.gigold.pay.ifsys.dao;

import java.util.List;
import java.util.Map;

import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.InterFaceField;

public interface InterFaceFieldDao {
    /**
     * 
     * Title: getFirstFieldByIfId<br/>
     * 获取某个接口请求部分的一级请求字段
     * 
     * @author xb
     * @date 2015年10月10日下午3:13:44
     *
     * @param interFaceField
     * @return
     */
    public List<InterFaceField>  getFirstReqFieldByIfId(InterFaceField interFaceField);

    /**
     ** 批量更新接口字段,返回接口id
     */
    public long updateIFField(int ifId, int fieldType, IFFields ifFieldsList);

    /**
     * 
     * Title: getFirstResFieldByIfId<br/>
     * 获取某个接口响应部分的一级响应字段
     * 
     * @author xb
     * @date 2015年10月10日下午3:19:52
     *
     * @param interFaceField
     * @return
     */
    public List<InterFaceField>  getFirstResFieldByIfId(InterFaceField interFaceField);

    /**
     * 
     * Title: getFieldByparentId<br/>
     * 根据parentId获取字段
     * 
     * @author xb
     * @date 2015年10月10日下午3:25:53
     *
     * @param interFaceField
     * @return
     */
    public List<InterFaceField> getFieldByparentId(InterFaceField interFaceField);
    
    /**
     * 
     * Title: getFieldByIfId<br/>
     * 获取接口所有字段: <br/>
     * @author xb
     * @date 2015年10月13日下午3:21:18
     *
     * @param interFaceField
     * @return
     */
    public List<InterFaceField> getFieldByIfId(InterFaceField interFaceField);

    /**
     * @author chenkuan
     * @param interFaceField
     * @return
     */
    InterFaceField getFieldById(InterFaceField interFaceField);
    
    /**
     * 
     * Title: addInterFaceField<br/>
     * 添加字段: <br/>
     * @author xb
     * @date 2015年10月12日上午11:27:46
     *
     * @param interFaceField
     * @return
     */
    public int addInterFaceField(InterFaceField interFaceField);
    
    /**
     * 
     * Title: deleteFieldByLevel<br/>
     * 通过层级码删除字段: <br/>
     * @author xb
     * @date 2015年10月13日下午5:00:42
     *
     * @param interFaceField
     * @return
     */
    public int deleteFieldByLevel(InterFaceField interFaceField);
   /**
    * 
    * Title: updateInterFaceField<br/>
    * 修改: <br/>
    * @author xb
    * @date 2015年10月14日上午11:33:49
    *
    * @param interFaceField
    * @return
    */
   public int updateInterFaceField(InterFaceField interFaceField);


    /**
     * 获取接口参数类型列表
     * @return
     */
    List<Map> getFeildDicTypeList();

    /**
     * 根据feildid获取feild对象
     * @param feildId
     * @return
     */
    InterFaceField getInterfaceFieldById(int feildId);

    /**
     * @author chenkuan
     * 批量删除接口字段
     * @param ids
     */
    void deleteFields(Integer[] ids);
}
