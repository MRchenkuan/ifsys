/**
 * Title: InterFaceProDao.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.dao;

import java.util.List;

import com.gigold.pay.ifsys.bo.InterFacePro;

/**
 * Title: InterFaceProDao<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xb
 * @date 2015年10月12日上午9:41:46
 *
 */
public interface InterFaceProDao {

    /**
     * 获取当前项目所有系统下所有的产品信息
     * @param pid
     * @return
     */
    public List<InterFacePro> getAllProInfo(int pid);

    public InterFacePro getProInfoById(InterFacePro interFacePro);

    public List<InterFacePro> getProInfoBySysId(InterFacePro interFacePro);
}
