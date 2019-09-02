package com.gigold.pay.ifsys.controller;

import com.alibaba.druid.util.StringUtils;
import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.dao.InterFaceDao;
import com.gigold.pay.ifsys.service.InterFaceFieldService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/7/6.
 */
@Controller
public class InterFaceDebugController extends BaseController{
    @Autowired
    InterFaceFieldService interFaceFieldService;
    @Autowired
    InterFaceDao interFaceDao;

    /**
     * 接口mock模拟输出
     * @return 返回接口信息列表
     */
    @RequestMapping(value = "/interfaceDebug.do")
    public @ResponseBody
    ResponseDto interfaceDebug(@RequestParam String ifUrl, @RequestParam int sys, HttpServletResponse hsr){
//    public @ResponseBody JSONObject interfaceDebug(@RequestParam String ifUrl){
        System.out.println(ifUrl);
        System.out.println(sys);
        ResponseDto reDto = new ResponseDto();

        // 初始化请求参数
        InterFaceField interFaceField = new InterFaceField();
        interFaceField.setFieldFlag("2");
        int ifId=0;
        try{// 根据接口URL获取接口ID
            InterFaceInfo interFaceInfo = interFaceDao.getInterfaceByUrl(ifUrl,sys);
            ifId = interFaceInfo.getId();
        }catch (Exception e){e.printStackTrace();}
        interFaceField.setIfId(ifId);

        System.out.println(ifId);
        StringBuilder ss = new StringBuilder();
        ss.append("{");

        List<InterFaceField> rlist = interFaceFieldService.getFirstReqFieldByIfId(interFaceField);
        if(rlist!=null){
            proJSON(ss, rlist, interFaceField.getFieldCheck());
        }
        String jsonStr = ss.toString().replaceAll(",\\}", "}").replaceAll(",\\]", "]");
        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        JSONObject jsonObject = new JSONObject();

        PrintWriter writer;

        try {
            jsonObject = JSONObject.fromObject(jsonStr);
            if(!jsonObject.containsKey("rspCd"))
            jsonObject.put("rspCd","00000");
            writer = hsr.getWriter();
            writer.write(jsonObject.toString());
            return null;
        }catch (Exception e){
            reDto.setRspCd(SysCode.SYS_FAIL);
            List<Map> list = new ArrayList<>();
            Map<String,String> map = new HashMap<>();
            map.put("EXCEPTION",e.getMessage());
            map.put("JSONString",jsonStr);
            map.put("SOLUTIONS","返回值可能不符合JSON格式,建议检查");
            list.add(map);
            reDto.setDataes(list);
            return reDto;
        }
    }



    /**
     * 将接口请求部分解析成JSON字符串
     */
    public void proJSON(StringBuilder ss, List<InterFaceField> list, String parentFieldCheck) {
        int i = 0;

        InterFaceField ff = null;
        for (i = 0; i < list.size(); i++) {
            ff = list.get(i);
            List<InterFaceField> clist = interFaceFieldService.getFieldByparentId(ff);
            ss.append("\"" + ff.getFieldName() + "\":");
            if (clist != null && clist.size() > 0) {
                if ("4".equals(ff.getFieldCheck())) {
                    ss.append("[{");
                } else {
                    ss.append("{");
                }
                proJSON(ss, clist, ff.getFieldCheck());
            } else {
                String value = ff.getFieldReferValue();
                if(StringUtil.isEmpty(value))value="null";
                ss.append(value);
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

    public static void main(String[] arg){
        String json="{\"rspCd\":\"00000\",\"rspInf\":\"成功\",\"rspType\":\"0\",\"dataes\":[{\"RSP_CODE_DESC\":\"成功\",\"IF_ID\":80,\"COUNT\":145,\"RSP_CODE\":\"00000\",\"TS\":1462946493000}],\"rspData\":null,\"responseTm\":\"20160511140240\"}";
        JSONObject jsonObject = new JSONObject();
        jsonObject = JSONObject.fromObject(json);
        System.out.println(jsonObject.toString());

    }
}
