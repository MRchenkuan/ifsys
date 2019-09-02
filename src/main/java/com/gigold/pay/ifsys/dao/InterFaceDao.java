package com.gigold.pay.ifsys.dao;

import java.util.List;
import java.util.Map;

import com.gigold.pay.ifsys.bo.InterFaceInfo;
import org.apache.ibatis.annotations.Param;

public interface InterFaceDao {
	/**
	 * 根据Id获得接口信息
	 */
	public InterFaceInfo getInterFaceById(InterFaceInfo interFaceInfo);

	/**
	 * 根据产品Id获得接口信息
	 * 
	 * @param id
	 * @return
	 */
	public List<InterFaceInfo> getInterFaceByProId(int id);


	/**
	 * 获得所有接口信息－－分页
	 * 
	 * @return
	 */
	public List<InterFaceInfo> getAllInterFaceByPage();
	/**
	 * 
	 * Title: queryInterFaceByPage<br/>
	 * 模糊查询 分页: <br/>
	 * @author xb
	 * @date 2015年10月15日下午12:56:37
	 *
	 * @return
	 */
	public List<InterFaceInfo> queryInterFaceByPage( InterFaceInfo interFaceInfo);
	


	/**
	 * 修改接口信息
	 * 
	 * @param interFaceInfo
	 * @return
	 */
	public int updateInterFace(InterFaceInfo interFaceInfo);

	/**
	 * 新增接口信息
	 * 
	 * @param interFaceInfo
	 * @return
	 */
	public int addInterFace(InterFaceInfo interFaceInfo);

	/**
	 * 根据Id删除接口信息
	 * 
	 * @param id
	 * @return
	 */
	public int deleteInterFaceById(InterFaceInfo interFaceInfo);

	/**
	 * 获取所有的接口信息
	 * @param interFaceInfo
	 * @return
     */
	List<InterFaceInfo> getAllIfSys(InterFaceInfo interFaceInfo);

	/**
	 * 获取接口下的 mockid
	 * @param ifId 接口ID
	 * @return 用例列表
	 */
	List<Map> getInterfaceMocksById(int ifId);

	/**
	 * 根据接口id删除mock数据
	 * @param ifId 接口ID
	 * @return 返回删除的行数
     */
	int deleteMockByIfId(int ifId);

	/**
	 * 删除接口下用例的依赖关系
	 * @param ifId 要删除的接口id
	 * @return 返回删除行数
     */
	int deleteMockReferByIfId(int ifId);

	InterFaceInfo getInterfaceByUrl(@Param("url") String url, @Param("pro") int pro);

	/**
	 * 查询用户关注的接口列表
	 *
	 *@param uId  @return
     */
	List<InterFaceInfo> getFollowedInterfaces(@Param("uId") int uId, @Param("ifSysId")int ifSysId, @Param("ifProId")int ifProId);

	/**
	 * 查询用户创建的接口列表
	 *
	 *@param uId  @return
     */
	List<InterFaceInfo> getCreatedInterfaces(@Param("uId") int uId, @Param("ifSysId")int ifSysId, @Param("ifProId")int ifProId);
}
