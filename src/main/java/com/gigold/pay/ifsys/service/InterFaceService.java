package com.gigold.pay.ifsys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.annoation.ChangesLog;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.dao.InterFaceDao;

@Service
public class InterFaceService extends AbstractService {

    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
    InterFaceDao interFaceDao;
    @Autowired
	InterFaceInvokerDao interFaceInvokerDao;

    /**
	 * @return the interFaceDao
	 */
	public InterFaceDao getInterFaceDao() {
		return interFaceDao;
	}

	/**
	 * @param interFaceDao the interFaceDao to set
	 */
	public void setInterFaceDao(InterFaceDao interFaceDao) {
		this.interFaceDao = interFaceDao;
	}

	/**
     * 
     * Title: getInterFaceById<br/>
     * Description:根据ID查询接口信息<br/>
     * 
     * @author xb
     * @date 2015年10月8日上午11:12:48
     * @return
     */
    public InterFaceInfo getInterFaceById(InterFaceInfo interFaceInfo) {
    	InterFaceInfo interFace=null;
    	try{
    		interFace=interFaceDao.getInterFaceById(interFaceInfo);
    	}catch(Exception e){
    		debug("调用 interFaceDao.getInterFaceById 发生异常");
    	}
        return interFace;
    }

    /**
     * 
     * Title: getAllInterFace<br/>
     * 分页获取所有接口信息: <br/>
     * @author xb
     * @date 2015年10月15日下午12:46:05
     *
     * @return
     */
    public List<InterFaceInfo> queryInterFaceByPage(InterFaceInfo interFaceInfo) {
    	List<InterFaceInfo> list=null;
    	try{
    		list=interFaceDao.queryInterFaceByPage(interFaceInfo);
    	}catch(Exception e){
    		debug("调用 interFaceDao.queryInterFaceByPage 发生异常");
    	}
        return list;
    }
    
    
    
    /**
     * 
     * Title: addInterFace<br/>
     * 添加接口: <br/>
     * @author xb
     * @date 2015年10月15日下午12:46:44
     *
     * @param interFaceInfo
     * @return
     */
    @ChangesLog
	public boolean addInterFace(InterFaceInfo interFaceInfo) {
    	boolean flag=false;
    	try{
    		int count=interFaceDao.addInterFace(interFaceInfo);
    		if(count>0){
    			flag=true;
    		}
    	}catch(Exception e){
    		debug("调用 interFaceDao.addInterFace 发生异常");
    	}
        return flag;
    }
   /**
    * 
    * Title: deleteInterFaceById<br/>
    * 删除接口: <br/>
    * @author xb
    * @date 2015年10月15日下午12:47:06
    *
    * @param interFaceInfo
    * @return
    */
   public boolean deleteInterFaceById(InterFaceInfo interFaceInfo) {
    	boolean flag=false;
    	try{
			int ifId = interFaceInfo.getId();
			// 删除接口用例 , 以下三条删除尽量放到事务中
			int count = interFaceDao.deleteMockByIfId(ifId);
			// 删除用例关系
			count += interFaceDao.deleteMockReferByIfId(ifId);
			// 删除接口
    		count += interFaceDao.deleteInterFaceById(interFaceInfo);
    		if(count>0){
    			flag=true;
    		}
    	}catch(Exception e){
			e.printStackTrace();
    		debug("调用 interFaceDao.deleteInterFaceById 发生异常");
    	}
        return flag;
   }
	/**
	 *
	 * Title: updateInterFace<br/>
	 * 修改接口: <br/>
	 * @author xb
	 * @date 2015年10月15日下午12:47:26
	 *
	 * @param interFaceInfo
	 * @return
	 */
	public boolean updateInterFace(InterFaceInfo interFaceInfo) {
		boolean flag=false;
		try{
			int count=interFaceDao.updateInterFace(interFaceInfo);
			if(count>0){
				flag=true;
			}
		}catch(Exception e){
			debug("调用 interFaceDao.updateInterFace 发生异常");
		}
		return flag;
	}

	/**
	 * 获取所有接口信息
	 * @param requestUserId 操作者
	 * @param interFaceInfo 接口信息
	 * @return 接口信息列表
     */
	public List<InterFaceInfo> getAllIfSys(int requestUserId,InterFaceInfo interFaceInfo) {
		List<InterFaceInfo> list = null;
		try {
			list = interFaceDao.getAllIfSys(interFaceInfo);
			List<InterFaceInvoker>invokers = interFaceInvokerDao.getInvokerByUserId(requestUserId);
			// 关注列表
			List<Integer> ifIds = new ArrayList<>();
			// 获取用户关注列表
			for(InterFaceInvoker invoker : invokers){
				int ifId = (int) invoker.getIfFollowedId();
				if(!ifIds.contains(ifId)){
					ifIds.add(ifId);
				}
			}
			//加入关注标记
			for(InterFaceInfo info :list){
				if(ifIds.contains(info.getId())){
					info.setFollowed(true);
				}else{
					info.setFollowed(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}

	/**
	 * 获取接口下的mockid
	 * @param ifId 接口ID
	 * @return 返回ID列表
	 */
	public List<Map> getInterfaceMocksById(int ifId) {
		List<Map> list = null;
		try{
			list = interFaceDao.getInterfaceMocksById(ifId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据用户ID获取用户关注的接口列表
	 * @param id
	 * @param interFaceInfo
	 * @return
	 */
	public List<InterFaceInfo> getFollowedInterfaces(int id, InterFaceInfo interFaceInfo) {
		List<InterFaceInfo> infoList = new Page<>();
		int ifSysId = interFaceInfo.getIfSysId();
		int ifProId = interFaceInfo.getIfProId();
		System.out.println("请求关注接口所使用的id:"+id);
		// 获取用户关注列表
		infoList = interFaceDao.getFollowedInterfaces(id,ifSysId,ifProId);
		for(InterFaceInfo info : infoList){
			info.setFollowed(true);
		}
		return infoList;
	}
}
