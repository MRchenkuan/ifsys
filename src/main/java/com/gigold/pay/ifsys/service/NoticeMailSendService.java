package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.SysCode;
import com.gigold.pay.framework.util.common.DateUtil;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;
import com.gigold.pay.ifsys.util.ChangesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chenkuan
 * on 2016/10/21.
 */
@Service
public class NoticeMailSendService extends MailSendService {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    @Autowired
    MailSendService mailSendService;
    @Autowired
    InterFaceInvokerDao interFaceInvokerDao;
    @Autowired
    InterFaceChangesService interFaceChangesService;

    /**
     * 发送被关注提醒邮件
     * @param ifInfo 被关注接口信息
     * @param follower 关注者信息
     * @param author 接口作者
     * @param remark 备注
     */
    public void sendfollowedNotice(InterFaceInfo ifInfo, UserInfo follower, UserInfo author, String remark){
        String nick=follower.getUserName();
        //设置邮件基本信息
        mailSendService.setSubject("【关注了你的接口】"+ifInfo.getIfName());
        mailSendService.setTemplateName("followNotice.vm");// 设置的邮件模板
        mailSendService.setFrom(follower.getUserName()); // 设置发件人

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("operName", follower.getUserName());
            model.put("reciverName", author.getUserName());
            model.put("remark", remark);
            model.put("ifId", ifInfo.getId());
            model.put("ifName", ifInfo.getIfName());
            model.put("ifDsName", ifInfo.getDesignName());
            model.put("ifUrl", ifInfo.getIfUrl());
            model.put("ifsys", ifInfo.getSysName());
            model.put("ifpro", ifInfo.getProName());
            // 获取关注列表
            List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifInfo.getId());
            model.put("invokerList",invokerList);
            // 设置收件人地址
            mailSendService.setTo(Collections.singletonList(author.getEmail()));
            // 发送邮件
            mailSendService.sendWithTemplateForHTML(model,nick+"(IFSYS)");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 发送关注通知邮件
     * @param ifInfo 被关注接口信息
     * @param oper 操作者信息
     * @param recivers 收件人
     * @param remark 备注
     */
    public void  sendInvokerNotice(InterFaceInfo ifInfo, UserInfo oper, List<UserInfo> recivers, String remark){
        String nick=oper.getUserName();
        //设置邮件基本信息
        mailSendService.setSubject("【提醒关注】"+ifInfo.getIfName());
        mailSendService.setTemplateName("invokeNotice.vm");// 设置的邮件模板
        mailSendService.setFrom(oper.getUserName()); // 设置发件人

        for(UserInfo reciver : recivers) {
            try {
                Map<String, Object> model = new HashMap<>();
                model.put("operName", oper.getUserName());
                model.put("reciverName", reciver.getUserName());
                model.put("remark", remark);
                model.put("ifId", ifInfo.getId());
                model.put("ifName", ifInfo.getIfName());
                model.put("ifDsName", ifInfo.getDesignName());
                model.put("ifUrl", ifInfo.getIfUrl());
                model.put("ifsys", ifInfo.getSysName());
                model.put("ifpro", ifInfo.getProName());
                // 获取关注列表
                List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifInfo.getId());
                model.put("invokerList",invokerList);
                // 设置收件人地址
                mailSendService.setTo(Collections.singletonList(reciver.getEmail()));
                // 发送邮件
                mailSendService.sendWithTemplateForHTML(model,nick+"(IFSYS)");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 发送变更通知邮件 - 所有变更
     * @param oper 操作者
     * @param receivers 收件人
     * @param ifInfo
     * @param ifChangesList 所有变更
     */
    public void sendChangesNotice(UserInfo oper, List<UserInfo> receivers, InterFaceInfo ifInfo, List<InterFaceChanges> ifChangesList){
        // 单个变更调用模板
        if(ifChangesList.size()==1){
            sendChangesNotice(oper,receivers,ifInfo,ifChangesList.get(0));
            return;
        }

        //调用多个模板
        String nick=oper.getUserName();
        mailSendService.setSubject("【接口变更】"+ifInfo.getIfName());
        mailSendService.setTemplateName("changesNotice.vm");// 设置的邮件模板

        // 发件
        for(UserInfo reciver : receivers) {
            try {
                Map<String, Object> model = new HashMap<>();
                model.put("operName", oper.getUserName());
                model.put("reciverName", reciver.getUserName());
                model.put("ifId", ifInfo.getId());
                model.put("ifName", ifInfo.getIfName());
                model.put("ifDsName", ifInfo.getDesignName());
                model.put("ifUrl", ifInfo.getIfUrl());
                model.put("ifsys", ifInfo.getSysName());
                model.put("ifpro", ifInfo.getProName());
                model.put("ifChangesList" , ifChangesList);
                // 获取关注列表
                List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifInfo.getId());
                model.put("invokerList",invokerList);
                // 设置收件人地址
                mailSendService.setTo(Collections.singletonList(reciver.getEmail()));
                // 发送邮件
                mailSendService.sendWithTemplateForHTML(model,nick+"(IFSYS)");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送变更通知邮件 - 单个变更
     * @param oper 操作者
     * @param receivers 收件人
     * @param ifInfo
     * @param theChanges
     */
    public void sendChangesNotice(UserInfo oper, List<UserInfo> receivers, InterFaceInfo ifInfo, InterFaceChanges theChanges){
        String nick=oper.getUserName();
        // 去重
        receivers.add(oper);
        receivers = signifiedReceivers(receivers);
        if("删除".equals(theChanges.getOptionType()) && StringUtil.isEmpty(theChanges.getChangeType())){
            //接口删除则调用删除模板
            mailSendService.setSubject("【接口删除】"+ifInfo.getIfName());
            mailSendService.setTemplateName("deleteNotice.vm");// 设置的邮件模板
            // 发件
            for(UserInfo reciver : receivers) {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("operName", oper.getUserName());
                    model.put("reciverName", reciver.getUserName());
                    model.put("ifId", ifInfo.getId());
                    model.put("ifName", ifInfo.getIfName());
                    model.put("ifDsName", ifInfo.getDesignName());
                    model.put("ifUrl", ifInfo.getIfUrl());
                    model.put("ifsys", ifInfo.getSysName());
                    model.put("ifpro", ifInfo.getProName());
                    // 获取关注列表
                    List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifInfo.getId());
                    model.put("invokerList",invokerList);
                    // 设置收件人地址
                    mailSendService.setTo(Collections.singletonList(reciver.getEmail()));
                    // 发送邮件
                    mailSendService.sendWithTemplateForHTML(model,nick+"(IFSYS)");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if(StringUtil.isNotEmpty(theChanges.getChangeType())){
            //接口修改则调用删除模板
            mailSendService.setSubject("【接口变更】"+ifInfo.getIfName());
            mailSendService.setTemplateName("changesNotice.vm");// 设置的邮件模板
            List<InterFaceChanges> changesList = new ArrayList<>();
            changesList.add(theChanges);
            // 发件
            for(UserInfo reciver : receivers) {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("operName", oper.getUserName());
                    model.put("reciverName", reciver.getUserName());
                    model.put("ifId", ifInfo.getId());
                    model.put("ifName", ifInfo.getIfName());
                    model.put("ifDsName", ifInfo.getDesignName());
                    model.put("ifUrl", ifInfo.getIfUrl());
                    model.put("ifsys", ifInfo.getSysName());
                    model.put("ifpro", ifInfo.getProName());
                    model.put("ifChangesList" , changesList);
                    // 获取关注列表
                    List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifInfo.getId());
                    model.put("invokerList",invokerList);
                    // 设置收件人地址
                    mailSendService.setTo(Collections.singletonList(reciver.getEmail()));
                    // 发送邮件
                    mailSendService.sendWithTemplateForHTML(model,nick+"(IFSYS)");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            //接口新增则调用删除模板
            // // TODO: 2016/10/23 新增模板调用
        }

    }

    /**
     * 收件人去重方法
     * @param recivers 收件人
     * @return 去重后的收件人
     */
    private List<UserInfo> signifiedReceivers(List<UserInfo> recivers) {
        List<UserInfo> _recivers = new ArrayList<>();
        List<Integer> _reciversId = new ArrayList<>();
        for(UserInfo reciver : recivers){
            try {
                if (_reciversId.contains(reciver.getId())) continue;
                _reciversId.add(reciver.getId());
                _recivers.add(reciver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return _recivers;
    }

    /**
     * 发送每日变更邮件
     */
    public void sendDailyReport(){
        // 获取昨天日期
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);//让日期-1
        Date yesterday = calendar.getTime();
        int newCount = 0,delCount = 0,modCount;
        List<Integer> _modList = new ArrayList<>();
        // 项目 id
        int pid = 1;
        // 获取昨天的记录
        List<InterFaceChanges> changes = interFaceChangesService.getDailyChanges(pid,yesterday);
        for(InterFaceChanges change:changes){
            if(StringUtil.isEmpty(change.getChangeType())){
                if("新增".equals(change.getOptionType())){
                    newCount++;
                }
                if("删除".equals(change.getOptionType())){
                    delCount++;
                }
            }else{
                if(!_modList.contains(change.getIfId()))_modList.add(change.getIfId());
            }
        }
        // 被修改的接口总数
        modCount = _modList.size();
        // 日期
        String dateString = DateUtil.getNewFormatDateString(yesterday).substring(0,10);
        // 格式化的数据
        List<InterFaceFeeds> detail = ChangesUtil.reformatInterfaceChanges(changes);

        // 发送邮件
        //调用多个模板
        mailSendService.setSubject("【独孤九剑每日报告】");
        mailSendService.setTemplateName("changesDailyReport.vm");// 设置的邮件模板
        // 发件
        try {
            // 数据
            Map<String, Object> model = new HashMap<>();
            model.put("dateString", dateString);
            model.put("modCount", modCount);
            model.put("newCount", newCount);
            model.put("delCount", delCount);
            model.put("details" ,detail);

            // 设置收件人地址
            String observer = SystemPropertyConfigure.getProperty("mail.dailyReport.observer");
            System.out.println(observer);
            List<String> recivers = Arrays.asList(observer.split(","));
            mailSendService.setTo(recivers);
            // 发送邮件
            mailSendService.sendWithTemplateForHTML(model);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
