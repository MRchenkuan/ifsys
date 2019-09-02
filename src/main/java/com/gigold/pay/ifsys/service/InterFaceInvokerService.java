/**
 * Title: InterFaceInvokerService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.service;

import java.util.*;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.ifsys.annoation.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;

/**
 * Title: InterFaceInvokerService<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月23日下午3:22:52
 *
 */
@Service
public class InterFaceInvokerService extends AbstractService {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	InterFaceInvokerDao interFaceInvokerDao;

	/**
	 * @return the interFaceInvokerDao
	 */
	public InterFaceInvokerDao getInterFaceInvokerDao() {
		return interFaceInvokerDao;
	}

	/**
	 * @param interFaceInvokerDao
	 *            the interFaceInvokerDao to set
	 */
	public void setInterFaceInvokerDao(InterFaceInvokerDao interFaceInvokerDao) {
		this.interFaceInvokerDao = interFaceInvokerDao;
	}

	/**
	 * 
	 * Title: addInterFaceInvoker<br/>
	 * Description: 新增接口关注者<br/>
	 * 
	 * @author xiebin
	 * @date 2015年11月23日下午3:26:25
	 *
	 * @param invoker
	 * @return
	 */
	public InterFaceInvoker addInterFaceInvoker(InterFaceInvoker invoker) {
		InterFaceInvoker interFaceInvoker = null;
		int count = -1;
		try {
			count = interFaceInvokerDao.addInterFaceInvoker(invoker);
			if (count > 0) {
				interFaceInvoker = invoker;
			}
		} catch (Exception e) {
			debug("数据库异常   添加接口关注者失败");
		}
		return interFaceInvoker;

	}

	/**
	 * 
	 * Title: getInvokerList<br/>
	 * Description: 获取接口关注列表<br/>
	 * 
	 * @author xiebin
	 * @date 2015年11月23日下午5:01:01
	 *
	 * @param invoker
	 * @return
	 */
	public List<InterFaceInvoker> getInvokerList(InterFaceInvoker invoker) {
		List<InterFaceInvoker> list = null;
		try {
			list = interFaceInvokerDao.getInvokerList(invoker);
		} catch (Exception e) {
			debug("数据库异常   获取关注列表失败");
		}
		return list;
	}

	/**
	 * 根据接口id获取关注列表
	 */
	public List<InterFaceInvoker> getInvokerListByIfId(long ifId) {
		List<InterFaceInvoker> list = null;
		try {
			list = interFaceInvokerDao.getInvokerListByIfId(ifId);
		} catch (Exception e) {
			debug("数据库异常   获取关注列表失败");
		}
		return list;
	}

	/**
	 * 
	 * Title: deleteInvoker<br/>
	 * Description: 取消关注<br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月21日下午2:44:24
	 *
	 * @param id
	 * @return
	 */
	public boolean deleteInvoker(long id) {
		boolean flag = false;
		int count = -1;
		try {
			count = interFaceInvokerDao.deleteInvoker(id);
			if (count > 0) {
				flag = true;
			}
		} catch (Exception e) {
			debug("数据库异常   取消接口关注者失败");
		}
		return flag;

	}

	/**
	 * 批量修改接口关注者
	 * @param ifId
	 * @param uIds
	 * @param remarks
     * @return
     */
	@Notice("addInterFaceInvokers")
	public List<InterFaceInvoker> addInterFaceInvokers(long ifId, List<Integer> uIds, String remarks) {
		List<InterFaceInvoker> invokerList = new ArrayList<>();
		try {
			// 接口已有的关注列表
			List<InterFaceInvoker> list = getInvokerListByIfId(ifId);
			// 关注用户与关注关系
			Map<Integer,Long> idInvokerMap = new HashMap<>();
			for(InterFaceInvoker invokers : list){
				idInvokerMap.put(invokers.getuId(),invokers.getId());
			}

			// 关注关系去重
			List<Integer> uidlist = new ArrayList<>();
			for (InterFaceInvoker invoker : list) {
				uidlist.add(invoker.getuId());
			}

			// 增加新关注
			for (Integer uId : uIds) {
				if (uidlist.contains(uId)) continue; // 已关注则跳过
				InterFaceInvoker invoker = new InterFaceInvoker();
				invoker.setIfFollowedId(ifId);
				invoker.setuId(uId);
				invoker.setRemark(remarks);
				invoker.setStatus("Y");
				addInterFaceInvoker(invoker);

				//缓存
				invokerList.add(invoker);
			}

			// 差集 - 被删除的关注
			uidlist.removeAll(uIds);
			for(Integer deleteUid : uidlist){
				deleteInvoker(idInvokerMap.get(deleteUid));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return invokerList;
	}

	/**
	 * 用户主动关注接口
	 * @param uid
	 * @param ifId
	 * @param remark
	 */
	@Notice
	public boolean followInterface(int uid, int ifId, String remark) {
		List<InterFaceInvoker> invokerList = interFaceInvokerDao.getInvokerListByIfId(ifId);
		for(InterFaceInvoker invoker : invokerList){
			// 遇到关注
			if(uid == invoker.getuId()){
				try{
					interFaceInvokerDao.deleteInvoker(invoker.getId());
				}catch (Exception e){
					e.printStackTrace();
				}
				return false;
			}
		}
		// 若执行到最后则增加关注
		InterFaceInvoker invoker = new InterFaceInvoker();
		invoker.setIfFollowedId(ifId);
		invoker.setuId(uid);
		invoker.setRemark(remark);
		addInterFaceInvoker(invoker);
		return true;
	}
}
