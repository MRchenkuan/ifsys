package com.gigold.pay.ifsys.util;

import com.alibaba.druid.util.StringUtils;
import com.gigold.pay.ifsys.RequestDto.InterFaceDetailReqDto;
import com.gigold.pay.ifsys.bo.InterFaceField;
import com.gigold.pay.ifsys.bo.ReturnCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenkuan
 * on 2017/1/5.
 */
public class JSONUtil {
    /**
     * list 转字符串 - 入口
     * @param rspFields 所有节点
     * @param rspCdList 所有返回码
     * @param needRspCd 是否需要加入返回码
     * @return
     */
    public static String proJSON(List<InterFaceField> rspFields, List<ReturnCode> rspCdList, boolean needRspCd){
        StringBuilder ss = new StringBuilder();
        ss.append("{");
        //设置响应字段 返回码
        if (needRspCd) {
            ss.append("\"rspCd\"").append(":\"").append(getRspCode(rspCdList)).append("\" /*返回码*/");
        }
        if(rspFields !=null){
            ss.append(",");
            List<InterFaceField> firstLevelList = new ArrayList<>();
            for(InterFaceField rspField:rspFields){
                if(rspField.getParentId()<=0){
                    firstLevelList.add(rspField);
                }
            }
            proJSON(ss, rspFields,firstLevelList, "0");
        }else{
            ss.append("}");
        }
        String jsonStr = ss.toString().replaceAll(",\\}", "}").replaceAll(",\\]", "]");
        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);

        return ForMatJSONStr.format(jsonStr);
    }

    /**
     * list 转字符串 -- 实现
     * @param ss 输出字符
     * @param allList 当前递归深度的节点集
     * @param parentFieldCheck 父节点的类型
     */
    private static void proJSON(StringBuilder ss, List<InterFaceField> allList,List<InterFaceField> currentList, String parentFieldCheck) {
        int i = 0;
        InterFaceField ff = null;
        for (i = 0; i < currentList.size(); i++) {
            ff = currentList.get(i);
            // 当前节点的子节点
            List<InterFaceField> clist = getFiledByParentId(allList,ff.getId());
            ss.append("\"").append(ff.getFieldName()).append("\":");
            if (clist != null && clist.size() > 0) {
                if ("4".equals(ff.getFieldCheck())) {
                    ss.append("[{");
                } else {
                    ss.append("{" + "/*").append(ff.getFieldDesc()).append("*/\n");
                }
                proJSON(ss,allList, clist, ff.getFieldCheck());
            } else {
                ss.append("").append(getFieldValueString(ff)).append(" /*").append(ff.getFieldDesc()).append("*/");
                if (i < currentList.size() - 1) {
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
     * list 转字符串 - 根据当前节点ID取子节点
     * @param list 所有节点
     * @param parentId 父节点ID
     * @return
     */
    private static List<InterFaceField> getFiledByParentId(List<InterFaceField> list,int parentId){
        List<InterFaceField> children = new ArrayList<>();
        if(list==null)return children;
        for(InterFaceField field : list){
            if(field.getParentId() == parentId){
                children.add(field);
            }
        }
        return children;
    };

    /**
     * 序列化rspCode
     * @param rspCdList
     * @return
     */
    private static String getRspCode(List<ReturnCode> rspCdList) {
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
    private static String getFieldValueString(InterFaceField ff){
        String type = ff.getFieldCheck();
        String value = ff.getFieldReferValue();
        switch (type){
            case "1":value = "\""+value+"\"";break;
            case "2":if(!isNumeric(value))value = "\""+value+"\"";break;
            case "3":value = "\"对象\"";break;
            case "4":value = "\"对象列表\"";break;
            case "5":if(!("true".equals(value)))value="false";break;
            case "6":value="null";break;
            case "7":break;
            default:value="\""+value+"\"";break;
        }
        return value;
    }

    /**
     * 判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 将请求参数转换为对象
     * @param c
     * @param <T>
     * @return
     */
    public static  <T>T getRequestObject(String reqStr, Class<T> c) {
        Gson g = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new AdapterIntegerDefault0())
                .registerTypeAdapter(int.class, new AdapterIntegerDefault0())
                .registerTypeAdapter(Double.class, new AdapterDoubleDefault0())
                .registerTypeAdapter(double.class, new AdapterDoubleDefault0())
                .registerTypeAdapter(Long.class, new AdapterLongDefault0())
                .registerTypeAdapter(long.class, new AdapterLongDefault0())
                .create();
        if(reqStr==null) try {
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return g.fromJson(reqStr, c);
    }


    public static void main(String[] args) {
        String a = RandomStringUtils.randomNumeric(4);
        System.out.println(a);
    }
}

