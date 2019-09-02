package com.gigold.pay.ifsys.util;

import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.bo.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/7/6.
 */
public class ChangesUtil {
    /**
     * 比较InterfaceInfo的方法
     * @param infoOld
     * @param infoNew
     * @param userId
     * @param ifId
     * @return
     */
    public static List<InterFaceChanges> compareInterFaceChanges(InterFaceInfo infoOld, InterFaceInfo infoNew, int userId, int ifId){
        List<InterFaceChanges> changesList = new ArrayList<>();

        InterFaceChanges changes;
        if(!StringUtil.equals(infoOld.getIfUrl(),infoNew.getIfUrl())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("接口地址");
            changes.setPrmVal(infoOld.getIfUrl());
            changes.setNowVal(infoNew.getIfUrl());
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(infoOld.getIfType(),infoNew.getIfType())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("请求方式");
            changes.setPrmVal(infoOld.getIfType());
            changes.setNowVal(infoNew.getIfType());
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(infoOld.getIfName(),infoNew.getIfName())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("接口名");
            changes.setPrmVal(infoOld.getIfName());
            changes.setNowVal(infoNew.getIfName());
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(infoOld.getReturnType(),infoNew.getReturnType())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("返回类型");
            changes.setPrmVal(infoOld.getReturnType());
            changes.setNowVal(infoNew.getReturnType());
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(infoOld.getIfProtocol(),infoNew.getIfProtocol())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("接口类型");
            changes.setPrmVal(infoOld.getIfUrl());
            changes.setNowVal(infoNew.getIfUrl());
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(infoOld.getIsIdempotent(),infoNew.getIsIdempotent())){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("幂等性");
            changes.setPrmVal("Y".equals(infoOld.getIsIdempotent())?"幂等":"非幂等");
            changes.setNowVal("Y".equals(infoNew.getIsIdempotent())?"幂等":"非幂等");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        return changesList;
    }


    /**
     * 比较接口参数的方法
     *
     * 特别说明:
     * 此方法中,若同时修改 参数名,参数说明,参数类型,参数例子时
     * 三者变更会按顺序储存,所以在非变更字段的,原始值的取值上,
     * 后一项修改会取前一项的新值
     * 所以比较顺序不能变
     *
     * author chenkuan 2016-07-06
     */
    public static List<InterFaceChanges> compareIfFieldListChanges(InterFaceField oldField, InterFaceField newField, Map<String, String> dicMap, int userId, int ifId) {
        List<InterFaceChanges> changesList = new ArrayList<>();
        InterFaceChanges changes;

        // 表示新增
        if(oldField==null){
            String newFieldFlag = newField.getFieldFlag();
            if("1".equals(newFieldFlag))newFieldFlag="请求";
            if("2".equals(newFieldFlag))newFieldFlag="返回";

            changes = new InterFaceChanges();
            changes.setOptionType("新增");
            changes.setUserId(userId);
            changes.setChangeType("<"+newFieldFlag+">参数:\""+newField.getFieldName()+"\"["+newField.getFieldDesc()+"]");
            changes.setIfId(ifId);
            changesList.add(changes);
            return changesList;
        }

//		// 表示比较其他节点
//		if(oldField.getParentId() != newField.getParentId()){
//			int oi = oldField.getParentId();
//			int ni = newField.getParentId();
//			String oiName=String.valueOf(oi);
//			String niName=String.valueOf(ni);
//
//			changes = new InterFaceChanges();
//			changes.setChangeType("参数父节点");
//			changes.setPrmVal(oiName);
//			changes.setNowVal(niName);
//			changes.setUserId(userId);
//			changes.setIfId(ifId);
//			changesList.add(changes);
//		}

        String oldFieldName = oldField.getFieldName();
        String oldFieldDesc = oldField.getFieldDesc();
        String newFieldName = newField.getFieldName();
        String newFieldDesc = newField.getFieldDesc();
        String oldFieldFlag = oldField.getFieldFlag();
        String newFieldFlag = newField.getFieldFlag();

        if("1".equals(oldFieldFlag))oldFieldFlag="请求";
        if("2".equals(oldFieldFlag))oldFieldFlag="返回";
        if("1".equals(newFieldFlag))newFieldFlag="请求";
        if("2".equals(newFieldFlag))newFieldFlag="返回";

        if(!StringUtil.equals(oldFieldFlag,newFieldFlag)){
            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType(oldFieldFlag+"参数");
            changes.setNowVal(newFieldFlag+"参数");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(oldField.getFieldName(),newField.getFieldName())){

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType(newFieldFlag+"参数名");
            changes.setPrmVal("\""+oldFieldName+"\"["+oldFieldDesc+"]");
            changes.setNowVal("\""+newFieldName+"\"["+oldFieldDesc+"]");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(oldField.getFieldDesc(),newField.getFieldDesc())){
            String oi = oldField.getFieldDesc();
            String ni = newField.getFieldDesc();

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType(newFieldFlag+"参数 \""+newFieldName+"\" 的说明");
            changes.setPrmVal("["+oldFieldDesc+"]");
            changes.setNowVal("["+newFieldDesc+"]");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        if(!StringUtil.equals(oldField.getFieldCheck(),newField.getFieldCheck())){
            String oi = oldField.getFieldCheck();
            String ni = newField.getFieldCheck();
            String oiName=dicMap.get(oi);
            String niName=dicMap.get(ni);

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType(newFieldFlag+"参数 \""+newFieldName+"\" 的类型");
            changes.setPrmVal(oiName);
            changes.setNowVal(niName);
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }



        if(!StringUtil.equals(oldField.getFieldReferValue(),newField.getFieldReferValue())){
            String oi = oldField.getFieldReferValue();
            String ni = newField.getFieldReferValue();

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType(newFieldFlag+"参数 \""+newFieldName+"\" 的示例");
            changes.setPrmVal(oi);
            changes.setNowVal(ni);
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        return changesList;
    }

    public static String getEvent(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        System.out.println(method.getName());
        return method.getAnnotation(ChangesLog.class).event();
    }

    /**
     * 比较返回码变更
     * @param userId
     * @param ifId
     * @return
     *
     * 特别说明:
     * 此方法中,若同时修改接口ID,返回码,返回码描述时,
     * 三者变更会按顺序储存,所以在非变更字段的,原始值的取值上,
     * 后一项修改会取前一项的新值
     * 所以比较顺序不能变
     *
     * author chenkuan 2016-07-06
     */
    public static List<InterFaceChanges> compareReturnCodeListChanges(ReturnCode oldReturnCode, ReturnCode newReturnCode, int userId, int ifId) {
        List<InterFaceChanges> changesList = new ArrayList<>();
        InterFaceChanges changes;

        // 表示新增
        if(oldReturnCode==null){
            changes = new InterFaceChanges();
            changes.setOptionType("新增");
            changes.setUserId(userId);
            changes.setChangeType("返回码:\""+newReturnCode.getRspCode()+"\"["+newReturnCode.getRspCodeDesc()+"]");
            changes.setIfId(ifId);
            changesList.add(changes);
            return changesList;
        }

        String oldRspCode = oldReturnCode.getRspCode();
        String oldRspDesc = newReturnCode.getRspCodeDesc();
        String newRspCode = newReturnCode.getRspCode();
        String newRspDesc = newReturnCode.getRspCodeDesc();


        // 返回码
        if(!StringUtil.equals(oldReturnCode.getRspCode(),newReturnCode.getRspCode())){
            String oi = oldReturnCode.getRspCode();
            String ni = newReturnCode.getRspCode();

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("返回码值");
            changes.setPrmVal("\""+oldRspCode+"\"["+oldRspDesc+"]");
            changes.setNowVal("\""+newRspCode+"\"["+oldRspDesc+"]");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        // 返回码描述
        if(!StringUtil.equals(oldReturnCode.getRspCodeDesc(),newReturnCode.getRspCodeDesc())){

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("返回码描述");
            changes.setPrmVal("\""+newRspCode+"\"["+oldRspDesc+"]");
            changes.setNowVal("\""+newRspCode+"\"["+newRspDesc+"]");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        // 接口ID
        if(oldReturnCode.getIfId()!=newReturnCode.getIfId()){
            int oi = oldReturnCode.getIfId();
            int ni = newReturnCode.getIfId();

            changes = new InterFaceChanges();
            changes.setOptionType("修改");
            changes.setChangeType("返回码\""+newRspCode+"\"["+newRspDesc+"] 的接口ID");
            changes.setPrmVal("["+String.valueOf(oi)+"]");
            changes.setNowVal("["+String.valueOf(ni)+"]");
            changes.setUserId(userId);
            changes.setIfId(ifId);
            changesList.add(changes);
        }

        return changesList;
    }

    /**
     * 将变更记录从列表形式转换为树形结构
     * @param changeList
     * @return
     */
    public static List<InterFaceFeeds> reformatInterfaceChanges(List<InterFaceChanges> changeList){
        List<InterFaceFeeds> feedsList = new ArrayList<>();

        // 重组
        try {
            for (InterFaceChanges changes : changeList) {

                int feedSize = feedsList.size();
                InterFaceFeeds feed;
                if (feedSize <= 0) {
                    feed = new InterFaceFeeds();
                    // 设置第一条的用户名
                    feed.setOper(changes.getUserName());
                    // 初始化第一条的变更列表
                    List<InterFaceChanges> _changeList = new ArrayList<>();
                    _changeList.add(changes);
                    feed.setChangesList(_changeList);
                    feedsList.add(feed);
                } else {
                    InterFaceFeeds lastFeed = feedsList.get(feedSize - 1);
                    // 若跟最近一条feed用户相同,则归入同一个feed
                    if (StringUtil.equals(lastFeed.getOper(), changes.getUserName())) {
                        lastFeed.getChangesList().add(changes);
                    } else {// 若不相同,则新建一个feed,并初始化
                        feed = new InterFaceFeeds();
                        // 设置第一条的用户名
                        feed.setOper(changes.getUserName());

                        // 初始化第一条的变更列表
                        List<InterFaceChanges> _changeList = new ArrayList<>();
                        _changeList.add(changes);
                        feed.setChangesList(_changeList);
                        feedsList.add(feed);

                    }

                }
            }
            return feedsList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return feedsList;
    }
}
