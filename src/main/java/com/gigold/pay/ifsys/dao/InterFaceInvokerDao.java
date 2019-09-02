/**
 * Title: InterFaceInvokerDao.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.dao;

import java.util.List;

import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserInfo;

/**
 * Title: InterFaceInvokerDao<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author xiebin
 * @date 2015年11月23日下午3:16:20
 *
 */
public interface InterFaceInvokerDao {
	/**
	 * 
	 * Title: addInterFaceInvoker<br/>
	 * Description: 新增接口关注者<br/>
	 * @author xiebin
	 * @date 2015年11月23日下午3:21:38
	 *
	 * @param invoker
	 * @return
	 */
	public int addInterFaceInvoker(InterFaceInvoker invoker);
	/**
	 * 
	 * Title: getInvokerList<br/>
	 * Description:获取接口关注列表 <br/>
	 * @author xiebin
	 * @date 2015年11月23日下午4:59:00
	 *
	 * @param invoker
	 * @return
	 */
	public List<InterFaceInvoker> getInvokerList(InterFaceInvoker invoker);
	
	/**
	 * 
	 * Title: getInvokerById<br/>
	 * Description:根据Id查询关注信息 <br/>
	 * @author xiebin
	 * @date 2015年12月11日下午5:36:29
	 ** @return
	 */
	public InterFaceInvoker getInvokerById(InterFaceInvoker interFaceInvoker);
	/**
	 * 
	 * Title: deleteInvoker<br/>
	 * Description: 取消关注<br/>
	 * @author xiebin
	 * @date 2015年12月21日下午2:42:37
	 *
	 * @param id
	 * @return
	 */
	public int deleteInvoker(long id);


	/**
	 * 根据接口id获取关注列表
	 */
	List<InterFaceInvoker> getInvokerListByIfId(long ifId);

	/**
	 * 根据用户id获取用户关注列表
	 * @return
     */
	List<InterFaceInvoker> getInvokerByUserId(int uId);

	/**
	 * 根据被关注的用户ID获取用户关注者的列表
	 * @param uId
	 * @return
	 */
	List<InterFaceInvoker> getInvokerListByFollowedUser(int uId);
}
