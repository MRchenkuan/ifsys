/**
 * Title: InterFaceSystemDao.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.dao;

import java.util.List;

import com.gigold.pay.ifsys.bo.InterFacePro;
import com.gigold.pay.ifsys.bo.InterFaceSysTem;

/**
 * Title: InterFaceSystemDao<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author xb
 * @date 2015年10月12日上午9:41:32
 *
 */
public interface InterFaceSystemDao {
    /**
     * 当前项目下所有的系统
     * @param pid
     * @return
     */
    public List<InterFaceSysTem> getAllSysInfo(int pid);

    public InterFaceSysTem getSysInfoById(InterFaceSysTem interFaceSystem);
    public List<InterFaceSysTem> queryTest(InterFaceSysTem interFaceSystem);

    /**
     * 获取用户关注的系统列表
     * @param userId
     * @return
     */
    List<InterFaceSysTem> getSysInfoByFollowed(int userId);

    /**
     * 获取用户设计的系统列表
     * @param userId
     * @return
     */
    List<InterFaceSysTem> getSysInfoByDesigned(int userId);


    /**
     * 获取全部系统列表
     * @return
     */
    List<InterFaceSysTem> getSysInfoByAllInterface();


    /**
     * 根据系统名创建系统
     * @param sys
     * @return
     */
    int updateInterfaceSys(InterFaceSysTem sys);

    /**
     * 根据产品名称创建产品
     * @param proObj
     * @return
     */
    int updateInterfacePro(InterFacePro proObj);
}
