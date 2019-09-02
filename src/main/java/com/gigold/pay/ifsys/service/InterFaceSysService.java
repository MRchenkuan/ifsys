package com.gigold.pay.ifsys.service;

import java.util.ArrayList;
import java.util.List;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.bo.InterFacePro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.ifsys.bo.InterFaceSysTem;
import com.gigold.pay.ifsys.dao.InterFaceSystemDao;

@Service
public class InterFaceSysService extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	InterFaceSystemDao interFaceSystemDao;

	/**
	 * @return the interFaceSystemDao
	 */
	public InterFaceSystemDao getInterFaceSystemDao() {
		return interFaceSystemDao;
	}

	/**
	 * @param interFaceSystemDao
	 *            the interFaceSystemDao to set
	 */
	public void setInterFaceSystemDao(InterFaceSystemDao interFaceSystemDao) {
		this.interFaceSystemDao = interFaceSystemDao;
	}

	/**
	 * 获取指定项目下所有的系统
	 * @author 陈宽
	 * @date 2016-02-10
	 */
	public List<InterFaceSysTem> getAllSysInfo(int pid) {
		List<InterFaceSysTem> list = null;
		try {
			list = interFaceSystemDao.getAllSysInfo(pid);
		} catch (Exception e) {
			debug("调用 interFaceSystemDao.getAllSysInfo 发生异常");
		}
		return list;
	}

	/**
	 * 
	 * Title: getSysInfoById<br/>
	 * Description: 根据系统Id获取系统的信息<br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月17日下午1:53:28
	 *
	 * @param interFaceSystem
	 * @return
	 */
	public InterFaceSysTem getSysInfoById(InterFaceSysTem interFaceSystem) {
		InterFaceSysTem interFaceSysTem = null;
		try {
			interFaceSysTem = interFaceSystemDao.getSysInfoById(interFaceSystem);
		} catch (Exception e) {
			debug("调用 interFaceSystemDao.getSysInfoById 发生异常");
		}
		return interFaceSysTem;
	}


	/**
	 * 获取本人关注的系统列表
	 * @author chenkuan
	 * @param id
	 * @return
     */
	public List<InterFaceSysTem> getSysInfoByFollowed(int id) {
		return interFaceSystemDao.getSysInfoByFollowed(id);
	}

	/**
	 * 获取本人设计的系统列表
	 * @author chenkuan
	 * @param id
	 * @return
     */
	public List<InterFaceSysTem> getSysInfoByDesigned(int id) {
		return interFaceSystemDao.getSysInfoByDesigned(id);
	}

	/**
	 * 获取所有接口的系统列表
	 * @return
     */
	public List<InterFaceSysTem> getSysInfoByAllInterface() {
		return interFaceSystemDao.getSysInfoByAllInterface();
	}

	/**
	 * 新增系统
	 * @return
	 */
    public int updateInterfaceSys(InterFaceSysTem sys) {
    	return interFaceSystemDao.updateInterfaceSys(sys);
    }

	/**
	 * 新增产品
	 * @param proObj
	 * @return
	 */
	public int updateInterfacePro(InterFacePro proObj) {
		return interFaceSystemDao.updateInterfacePro(proObj);
	}
}
