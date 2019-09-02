package com.gigold.pay.ifsys.service;

import com.alibaba.druid.util.StringUtils;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.dao.InterFaceDao;
import com.gigold.pay.ifsys.dao.InterFaceEditionDao;
import com.gigold.pay.ifsys.dao.InterFaceFieldDao;
import com.gigold.pay.ifsys.dao.ReturnCodeDao;
import com.gigold.pay.ifsys.util.ForMatJSONStr;
import com.gigold.pay.ifsys.util.JSONUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 接口版本控制服务
 */
@Service
public class InterFaceEditionService extends AbstractService {

    @Autowired
    InterFaceEditionDao interFaceEditionDao;
    @Autowired
    InterFaceDao interFaceDao;
    @Autowired
    InterFaceFieldDao interFaceFieldDao;
    @Autowired
    ReturnCodeDao returnCodeDao;
    /**
     * 更新版本信息
     * @param ifId
     * @param summary
     * @param userId
     */
    public InterFaceEdition saveEdition(int ifId, String summary, int userId) {
        InterFaceEdition edition = null;
        try {
            // 查询版本信息
            InterFaceInfo ifInfo = new InterFaceInfo();
            ifInfo.setId(ifId);
            ifInfo = interFaceDao.getInterFaceById(ifInfo);
            String ifVersion = ifInfo.getIfVersionNo();
            // 构建版本信息
            edition = new InterFaceEdition();
            edition.setIfId(ifId);
            edition.setIfVerNo(ifVersion);
            edition.setIfVerDetail(summary);
            edition.setUserId(userId);
            // 比较差异
            InterFaceEdition oldEdition = interFaceEditionDao.getIfEditionByVerNo(edition);
            // 保存
            interFaceEditionDao.saveEdition(edition);
            // 删除不存在的字段
            minusEditions(oldEdition,edition);
        }catch (Exception e){
            e.printStackTrace();
        }
        return edition;
    }

    /**
     * 根据接口ID获取其所有的版本信息
     * @author chenkuan
     * @date 2017-1-4
     */
    public List<InterFaceEdition> getEditionsByIfId(int ifId){
        List<InterFaceEdition> editions = new ArrayList<>();
        try {
            editions = interFaceEditionDao.getEditionsByIfId(ifId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return editions;
    }
    /**
     * 比较并删除不存在的版本信息
     * @author chenkuan
     * @date 2016/11/09
     * @param oldEdition
     * @param newEdition
     */
    private void minusEditions(InterFaceEdition oldEdition, InterFaceEdition newEdition) {
        if(oldEdition==null)return;
        String summaryOld = oldEdition.getIfVerDetail();
        String summaryNew = newEdition.getIfVerDetail();

        List<Integer> oldFields = new ArrayList<>();
        List<Integer> oldRspCds = new ArrayList<>();
        List<Integer> newFields = new ArrayList<>();
        List<Integer> newRspCds = new ArrayList<>();
        try{
            // 老成员提取
            JSONObject jsonObject = JSONObject.fromObject(summaryOld);
            JSONArray reqIds = jsonObject.getJSONArray("req");
            JSONArray rspIds = jsonObject.getJSONArray("rsp");
            JSONArray rspCdIds = jsonObject.getJSONArray("rspCd");
            reqIds.addAll(rspIds);
            oldFields.addAll(reqIds);
            oldRspCds.addAll(rspCdIds);

            // 新成员提取
            jsonObject = JSONObject.fromObject(summaryNew);
            reqIds = jsonObject.getJSONArray("req");
            rspIds = jsonObject.getJSONArray("rsp");
            rspCdIds = jsonObject.getJSONArray("rspCd");
            reqIds.addAll(rspIds);
            newFields.addAll(reqIds);
            newRspCds.addAll(rspCdIds);

            // TODO: 2016/11/10 比较新老
            oldFields.removeAll(newFields);
            oldRspCds.removeAll(newRspCds);
            Integer[] _oldFields = oldFields.toArray(new Integer[oldFields.size()]);
            Integer[] _oldRspCds = oldRspCds.toArray(new Integer[oldRspCds.size()]);
            if(_oldFields.length>0) interFaceFieldDao.deleteFields(_oldFields);
            if(_oldRspCds.length>0) returnCodeDao.deleteReturnCodes(_oldRspCds);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    /**
     * 推进并新增版本信息
     * @param ifId
     * @param summary
     * @param userId
     */
    public InterFaceEdition advanceEdition(int ifId, String summary, int userId) {
        InterFaceEdition edition=null;
        try {
            // 推进版本信息
            InterFaceInfo ifInfo = new InterFaceInfo();
            ifInfo.setId(ifId);
            ifInfo = interFaceDao.getInterFaceById(ifInfo);
            String ifVersion = ifInfo.getIfVersionNo();
            ifVersion = String.valueOf(Integer.parseInt(ifVersion)+1);
            ifInfo.setIfVersionNo(ifVersion);
            interFaceDao.updateInterFace(ifInfo);

            // 保存版本信息
            edition = new InterFaceEdition();
            edition.setIfId(ifId);
            edition.setIfVerNo(ifVersion);
            edition.setIfVerDetail(summary);
            edition.setUserId(userId);
            interFaceEditionDao.saveEdition(edition);
        }catch (Exception e){
            e.printStackTrace();
        }
        return edition;
    }

    /**
     * 根据接口ID和版本号获取接口详情
     * @param ifId
     * @param versionNo
     * @author chenkuan
     * @date 2016/11/09
     * @return
     */
    public InterFaceDetail getInterfaceDetailByEdition(int ifId,String versionNo){
        InterFaceDetail interFaceDetail = new InterFaceDetail();
        interFaceDetail.setIfId(ifId);
        interFaceDetail.setIfEdition(versionNo);

        InterFaceEdition interFaceEdition = new InterFaceEdition();
        interFaceEdition.setIfId(ifId);interFaceEdition.setIfVerNo(versionNo);
        interFaceEdition = interFaceEditionDao.getIfEditionByVerNo(interFaceEdition);

        List<InterFaceField> rspFields= new ArrayList<>(),reqFields =new ArrayList<>();
        List<ReturnCode> rspCodes = new ArrayList<>();

        if("1".equals(versionNo) && interFaceEdition==null){
            // 当查询版本不存在,切当前所查版本为初始版本时,返回全部
            // 当前接口返回码
            rspCodes = returnCodeDao.getReturnCodeByIfId(ifId);
            System.out.println(rspCodes);
            // 当前接口字段
            InterFaceField interFaceField = new InterFaceField();
            interFaceField.setIfId(ifId);
            List<InterFaceField> ifFieldses = interFaceFieldDao.getFieldByIfId(interFaceField);
            System.out.println(ifFieldses);
            for(InterFaceField field : ifFieldses){
                if("1".equals(field.getFieldFlag()))reqFields.add(field);
                if("2".equals(field.getFieldFlag()))rspFields.add(field);
            }
        }else if(interFaceEdition == null){
            System.out.println("interFaceEdition == null;退出");
            return null;
        }else{
            // 否则返回指定版本
            String ifSummary = interFaceEdition.getIfVerDetail();
            JSONObject jsonObject = JSONObject.fromObject(ifSummary);

            // 请求参数
            if(jsonObject.containsKey("req")){
                JSONArray reqIds = jsonObject.getJSONArray("req");
                reqFields = getFieldsByIds(reqIds);
            }

            // 返回参数
            if(jsonObject.containsKey("rsp")){
                JSONArray rspIds = jsonObject.getJSONArray("rsp");
                rspFields = getFieldsByIds(rspIds);
            }

            // 返回码
            if(jsonObject.containsKey("rspCd")){
                JSONArray rspCdIds = jsonObject.getJSONArray("rspCd");
                for(Object _rspCdId :rspCdIds){
                    int rspCdId=0;
                    try {
                        rspCdId = (int) _rspCdId;
                    }catch (Exception e){
                        debug("摘要信息中返回码ID,不为整型");
                    }

                    if(rspCdId>0){
                        ReturnCode rspCode;
                        rspCode = returnCodeDao.getReturnCodeById(rspCdId);
                        if(rspCode!=null){
                            rspCodes.add(rspCode);
                        }
                    }
                }
            }
        }

        //请求参数序列化
        String rspJsonStr = JSONUtil.proJSON(rspFields,rspCodes,true);
        //返回参数序列化
        String reqJsonStr = JSONUtil.proJSON(reqFields,rspCodes,false);
        System.out.println(rspJsonStr);
        // 转换字段Bean格式
        List<IFFields> reqIFFields = new ArrayList<>();
        for(InterFaceField feild:reqFields){
            IFFields feildReList = new IFFields();
            feildReList.setId(feild.getId());
            feildReList.setType(feild.getFieldCheck());
            feildReList.setNote(feild.getFieldDesc());
            feildReList.setMock(feild.getFieldReferValue());
            feildReList.setK(feild.getFieldName());
            feildReList.setReq(feild.isFieldReq());
            feildReList.setParent(String.valueOf(feild.getParentId()));// 暂存为ID
            feildReList.setFeildFlag(feild.getFieldFlag());// 暂存为ID
            reqIFFields.add(feildReList);
        }
        List<IFFields> rspIFFields = new ArrayList<>();
        for(InterFaceField feild:rspFields){
            IFFields feildReList = new IFFields();
            feildReList.setId(feild.getId());
            feildReList.setType(feild.getFieldCheck());
            feildReList.setNote(feild.getFieldDesc());
            feildReList.setMock(feild.getFieldReferValue());
            feildReList.setK(feild.getFieldName());
            feildReList.setReq(feild.isFieldReq());
            feildReList.setParent(String.valueOf(feild.getParentId()));// 暂存为ID
            feildReList.setFeildFlag(feild.getFieldFlag());// 暂存为ID
            rspIFFields.add(feildReList);
        }

        // 构造数据
        interFaceDetail.setReqFields(reqIFFields);
        interFaceDetail.setRspFields(rspIFFields);
        interFaceDetail.setRspCds(rspCodes);
        interFaceDetail.setReqJsonStr(reqJsonStr);
        interFaceDetail.setRspJsonStr(rspJsonStr);

        return interFaceDetail;
    }

    /**
     * @author Json数组转字段对象列表
     * @param reqIds
     * @return
     */
    private List<InterFaceField> getFieldsByIds(JSONArray reqIds){
        List<InterFaceField> reqFields = new ArrayList<>();
        for(Object _reqId :reqIds){
            int reqId=0;
            try {
                reqId = (int) _reqId;
            }catch (Exception e){
                debug("摘要信息中请求参数ID,不为整型");
            }

            if(reqId>0){
                InterFaceField field = new InterFaceField();
                field.setId(reqId);
                field = interFaceFieldDao.getFieldById(field);
                if(field!=null){
                    reqFields.add(field);
                }
            }
        }
        return reqFields;
    }

}
