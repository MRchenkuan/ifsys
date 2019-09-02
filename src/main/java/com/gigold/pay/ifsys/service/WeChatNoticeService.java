package com.gigold.pay.ifsys.service;

import com.gigold.pay.ifsys.util.Constant;
import org.springframework.stereotype.Service;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.ifsys.bo.InterFaceChanges;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by chenkuan
 * on 2016/10/21.
 */
@Service
public class WeChatNoticeService extends MailSendService {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    @Autowired
    WeChatService weChatService;
    @Autowired
    InterFaceInvokerDao interFaceInvokerDao;

    /**
     * 发送被关注提醒邮件
     * @param ifInfo 被关注接口信息
     * @param follower 关注者信息
     * @param author 接口作者
     * @param remark 备注
     */
    public void sendfollowedNotice(InterFaceInfo ifInfo, UserInfo follower, UserInfo author, String remark){

        try {
            String nick = follower.getUserName();
            String authorAcct = author.getLoginName();

            HashMap<String, String> article = new HashMap<>();
            // 接口标题
            article.put("title", nick + " 关注了【" + ifInfo.getIfName() + "】");
            // 接口基本信息
            String content = nick + "关注了你的接口:"
                    + "\n" + "接口名：[" + ifInfo.getIfName()+ "]"
                    + "\n" + "设计者：[" + ifInfo.getDesignName()+ "]"
                    + "\n" + "地址名：[" + ifInfo.getIfUrl()+ "]"
                    + "\n" + "所属系统：" + "[" + ifInfo.getSysName() + " » " + ifInfo.getProName() + "]"
                    + "\n\n\t" + "“" + remark +"”";

            // 封装
            article.put("description", content);
            // 包装rul
            article.put("url", weChatService.wrapWeChatOpenUrl(Constant.URL_WECHAT_FOLLOW_PAGE,"toInterface::"+ifInfo.getId()));
            // 发送
            weChatService.sendWeChatMsg(article, authorAcct);
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
        try {
            // 获取收件人
            String target = "";
            // 去重
            recivers = signifiedReceivers(recivers);
            for(UserInfo reciver : recivers) {
                target += reciver.getLoginName() + " ";
            }
            target = target.trim().replace(" ","|");
            // 初始化基本信息
            HashMap<String, String> article = new HashMap<>();
            // 接口标题
            article.put("title", oper.getUserName() + " 提醒你关注【" + ifInfo.getIfName() + "】");
            // 接口基本信息
            String content = oper.getUserName() + "提醒你关注接口:"
                    + "\n" + "接口名：[" + ifInfo.getIfName()+ "]"
                    + "\n" + "设计者：[" + ifInfo.getDesignName()+ "]"
                    + "\n" + "地址名：[" + ifInfo.getIfUrl()+ "]"
                    + "\n" + "所属系统：" + "[" + ifInfo.getSysName() + " » " + ifInfo.getProName() + "]"
                    + "\n\n\t" + "“" + remark +"”";

            // 封装
            article.put("description", content);
            // 包装rul
            article.put("url", weChatService.wrapWeChatOpenUrl(Constant.URL_WECHAT_FOLLOW_PAGE,"toInterface::"+ifInfo.getId()));
            // 发送
            weChatService.sendWeChatMsg(article, target);
        }catch (Exception e){
            e.printStackTrace();
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

        try {
            // 获取收件人
            String target = "";
            // 去重
            receivers = signifiedReceivers(receivers);
            for(UserInfo reciver : receivers) {
                target += reciver.getLoginName() + " ";
            }
            target = target.trim().replace(" ","|");
            // 初始化基本信息
            HashMap<String, String> article = new HashMap<>();
            // 接口标题
            article.put("title", "【接口变更】"+ifInfo.getIfName());
            // 接口基本信息
            String content = "你关注的接口发生了变更:";
            // 接口变更信息
            content += "\n";
            for(InterFaceChanges ifChange : ifChangesList){
                content += "\n\t"+oper.getUserName() + " " + ifChange.getOptionType() +"了 "+ ifChange.getChangeType();
                content += "\n\t\t" + ifChange.getPrmVal() + " → " +ifChange.getNowVal();
            }
            content +="\n";
            // 接口基本信息
            content += "\n" + "接口名：[" + ifInfo.getIfName()+ "]"
                    + "\n" + "设计者：[" + ifInfo.getDesignName()+ "]"
                    + "\n" + "地址名：[" + ifInfo.getIfUrl()+ "]"
                    + "\n" + "所属系统：" + "[" + ifInfo.getSysName() + " » " + ifInfo.getProName() + "]";
            // 封装
            article.put("description", content);
            // 包装rul
            article.put("url", weChatService.wrapWeChatOpenUrl(Constant.URL_WECHAT_FOLLOW_PAGE,"toInterface::"+ifInfo.getId()));
            // 发送
            weChatService.sendWeChatMsg(article, target);
        }catch (Exception e){
            e.printStackTrace();
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
        // 去重
        receivers.add(oper);
        receivers = signifiedReceivers(receivers);
        if("删除".equals(theChanges.getOptionType()) && StringUtil.isEmpty(theChanges.getChangeType())){
            try {
                // 获取收件人
                String target = "";
                // 去重
                receivers = signifiedReceivers(receivers);
                for(UserInfo reciver : receivers) {
                    target += reciver.getLoginName() + " ";
                }
                target = target.trim().replace(" ","|");
                // 初始化基本信息
                HashMap<String, String> article = new HashMap<>();
                // 接口标题
                article.put("title", "【接口删除】"+ifInfo.getIfName());
                // 接口基本信息
                String content = oper.getUserName() + " 删除了你关注的接口:";
                // 接口基本信息
                content += "\n" + "接口名：[" + ifInfo.getIfName()+ "]"
                        + "\n" + "设计者：[" + ifInfo.getDesignName()+ "]"
                        + "\n" + "地址名：[" + ifInfo.getIfUrl()+ "]"
                        + "\n" + "所属系统：" + "[" + ifInfo.getSysName() + " » " + ifInfo.getProName() + "]";
                // 封装
                article.put("description", content);
                // 包装rul
                article.put("url", weChatService.wrapWeChatOpenUrl(Constant.URL_WECHAT_FOLLOW_PAGE,"toInterface::"+ifInfo.getId()));
                // 发送
                weChatService.sendWeChatMsg(article, target);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(StringUtil.isNotEmpty(theChanges.getChangeType())){
            try {
                // 获取收件人
                String target = "";
                // 去重
                receivers = signifiedReceivers(receivers);
                for(UserInfo reciver : receivers) {
                    target += reciver.getLoginName() + " ";
                }
                target = target.trim().replace(" ","|");
                // 初始化基本信息
                HashMap<String, String> article = new HashMap<>();
                // 接口标题
                article.put("title", "【接口变更】"+ifInfo.getIfName());
                // 接口基本信息
                String content = "你关注的接口发生了变更:";
                // 接口变更信息
                content += "\n";
                content += "\n\t"+oper.getUserName() + " " + theChanges.getOptionType() +"了 "+ theChanges.getChangeType();
                content += "\n\t\t" + theChanges.getPrmVal() + " → " + theChanges.getNowVal();
                content += "\n";
                // 接口基本信息
                content += "\n" + "接口名：[" + ifInfo.getIfName()+ "]"
                        + "\n" + "设计者：[" + ifInfo.getDesignName()+ "]"
                        + "\n" + "地址名：[" + ifInfo.getIfUrl()+ "]"
                        + "\n" + "所属系统：" + "[" + ifInfo.getSysName() + " » " + ifInfo.getProName() + "]";
                // 封装
                article.put("description", content);
                // 包装rul
                article.put("url", weChatService.wrapWeChatOpenUrl(Constant.URL_WECHAT_FOLLOW_PAGE,"toInterface::"+ifInfo.getId()));
                // 发送
                weChatService.sendWeChatMsg(article, target);
            }catch (Exception e){
                e.printStackTrace();
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
     * 编码过滤方法
     * @param content
     * @return
     */
    private String urlEncodePure(String content){
        try {
            content = URLEncoder.encode(content, "UTF-8");
            content = URLDecoder.decode(content,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }


}
