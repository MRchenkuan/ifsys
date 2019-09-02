package com.gigold.pay.ifsys.controller;

import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.ResponseDto.IfChangesRspDto;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.InterFaceFeeds;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.InterFaceChangesService;
import com.gigold.pay.ifsys.util.ChangesUtil;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.TimeFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenkuan
 * on 16/7/6.
 */
@Controller
public class InterFaceChangesController extends BaseController{
    @Autowired
    InterFaceChangesService interFaceChangesService;

    /**
     * 分页获取系统下的接口信息
     * @return 返回接口信息列表
     */
    @RequestMapping("/getIfsysFeed.do")
    public @ResponseBody
    IfChangesRspDto getIfsysFeed(@RequestBody Map<String,Integer> map, HttpSession session) {
        IfChangesRspDto reDto = new IfChangesRspDto();
        List<InterFaceChanges> changeList = new ArrayList<>();
        List<InterFaceFeeds> feedsList = new ArrayList<>();

        // 判断用户登录
        UserInfo user = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
        if(user==null || user.getId()<=0){
            reDto.setRspCd(CodeItem.NOT_LOGIN);
            reDto.setRspCd("用户未登录");
            return reDto;
        }


        int limit = 1000,pid = 0;
        if(map.containsKey("limit")){
            limit = map.get("limit");
        }
        if(map.containsKey("pid")){
            pid = map.get("pid");
        }
        if(pid<=0){
            reDto.setRspCd(SysCode.PARA_NULL);
            reDto.setRspInf("参数错误:pid");
            return reDto;
        }
        // 获取原始变更
        try {
            changeList = interFaceChangesService.getRecentChanges(pid,limit,user.getId());
        }catch (Exception e){
            e.printStackTrace();
        }

        // 重组
        try {

            // 遍历序号
            int index  = 0;
            for (InterFaceChanges changes : changeList) {
                // 时刻化时间
                String ts = changes.getTs();
                ts = TimeFormatUtil.timeToMoment(ts);
                changes.setTs(ts);

                // 当前feed列表大小
                int feedSize = feedsList.size();
                InterFaceFeeds feed;

                // 装在feed
                if (feedSize <= 0) {
                    index = 0;
                    feed = new InterFaceFeeds();
                    // 设置第一条的用户名
                    feed.setOper(changes.getUserName());
                    // 设置第一条的变更列表
                    List<InterFaceChanges> _changeList = new ArrayList<>();
                    // 设置第一条的时间戳
                    feed.setTs(ts);

                    _changeList.add(changes);
                    feed.setChangesList(_changeList);
                    feedsList.add(feed);

                } else {

                    // 最后一个feed
                    InterFaceFeeds lastFeed = getLastEle(feedsList);

                    // 最后一个change
                    InterFaceChanges lastChange = getLastEle(lastFeed.getChangesList());


                    if (StringUtil.equals(lastFeed.getOper(), changes.getUserName())
                            && StringUtil.equals(lastFeed.getTs(), changes.getTs())) {
                        // feed用户/ 时间 相同

                        String changeType = changes.getChangeType();
                        String lastChangeType = lastChange.getChangeType();
                        String changeOptionType = changes.getOptionType();

                        if(StringUtil.isEmpty(changeType))changeType="";
                        if(StringUtil.isEmpty(lastChangeType))lastChangeType="";

                        if(
                            (lastChangeType.startsWith("<返回>参数")
                            || lastChangeType.startsWith("<请求>参数")
                            || lastChangeType.endsWith(" 项参数修改"))
                            && (changeType.startsWith("<返回>参数")
                            || changeType.startsWith("<请求>参数"))
                            && (StringUtil.equals(lastChange.getOptionType(),changes.getOptionType()))
                                ){
                            // 返回参数开头
                            lastChange.setChangeType(changeType+" ,等 "+String.valueOf(++index+1)+" 项参数修改");
                        }else{// 常规
                            index = 0;
                            lastFeed.getChangesList().add(changes);
                        }

                    } else {
                        // feed用户/ 时间 不同
                        index = 0;
                        feed = new InterFaceFeeds();
                        // 设置第一条的用户名
                        feed.setOper(changes.getUserName());
                        // 设置第一条的变更列表
                        List<InterFaceChanges> _changeList = new ArrayList<>();
                        // 设置第一条的时间戳
                        feed.setTs(ts);

                        _changeList.add(changes);
                        feed.setChangesList(_changeList);
                        feedsList.add(feed);

                    }

                }
            }

            reDto.setFeedsList(feedsList);
            reDto.setRspCd(SysCode.SUCCESS);
        }catch (Exception e){
            reDto.setRspCd(SysCode.SYS_FAIL);
            e.printStackTrace();
        }
        return reDto;
    }

    /**
     * 分页获取系统下的接口信息
     * @return 返回接口信息列表
     */
    @RequestMapping("/getInterfaceChanges.do")
    public @ResponseBody
    IfChangesRspDto getInterfaceChanges(@RequestBody Map<String,Integer> map) {
        IfChangesRspDto reDto = new IfChangesRspDto();
        List<InterFaceChanges> changeList = new ArrayList<>();
        List<InterFaceFeeds> feedsList = new ArrayList<>();
        if(!map.containsKey("ifId")){
            reDto.setRspCd(SysCode.SYS_FAIL);
            reDto.setRspInf("请提交接口ID");
            return reDto;
        }
        int ifId = map.get("ifId");
        // 获取原始变更
        try {
            changeList = interFaceChangesService.getChangesByIfid(ifId);
        }catch (Exception e){
            e.printStackTrace();
        }

        // 重组
        try {
            feedsList = ChangesUtil.reformatInterfaceChanges(changeList);
            reDto.setFeedsList(feedsList);
            reDto.setRspCd(SysCode.SUCCESS);
        }catch (Exception e){
            reDto.setRspCd(SysCode.SYS_FAIL);
            e.printStackTrace();
        }
        return reDto;
    }


    /**
     * 获取列表中最后一个元素
     * @param list
     * @param <T>
     * @return
     */
    private <T>T getLastEle(List<T> list){
        if(list!=null && list.size()>0){
            return list.get(list.size() - 1);
        }else{
            return null;
        }
    }
}
