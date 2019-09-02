package com.gigold.pay.ifsys;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.util.common.DateUtil;
import com.gigold.pay.ifsys.RequestDto.InterFaceDetailReqDto;
import com.gigold.pay.ifsys.RequestDto.InterFaceRequestDto;
import com.gigold.pay.ifsys.annoation.Notice;
import com.gigold.pay.ifsys.bo.*;
import com.gigold.pay.ifsys.controller.*;
import com.gigold.pay.ifsys.dao.InterFaceFieldDao;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;
import com.gigold.pay.ifsys.dao.ReturnCodeDao;
import com.gigold.pay.ifsys.service.*;
import com.gigold.pay.ifsys.util.Constant;
import org.jmock.auto.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/*Beans.xml"})

public class DebugApi {
    @Autowired
    ProjectService projectService;
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    NoticeMailSendService noticeMailSendService;
    @Autowired
    InterFaceController interFaceController;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserController userController;
    @Test
    public void addInterFaceFieldTest(){
//        captchaService.sendEmailCaptcha("chenkuan@sqbj.com","6547");
        Map<String,String> map = new HashMap<>();
        map.put("key","chen");
        ResponseDto a = userController.searchUsers(map, null);
        System.out.println(a.getDataes());
    }
}
