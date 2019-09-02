package com.gigold.pay.ifsys.dao;

import java.util.List;

import com.gigold.pay.ifsys.bo.UserDetail;
import com.gigold.pay.ifsys.bo.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoDao {

	UserInfo getUser(int id);

	List<UserInfo> getList();

	int addUser(UserInfo user);
	
	UserInfo login(UserInfo user);

	/**
	 * 获取所有的用户列表
	 * @return
     */
	List<?> getAvlUserList();

	/**
	 * 通过用户登录名获取用户信息
	 * @param loginName
	 * @return
     */
	UserInfo getUserByLoginKey(String loginName);


	/**
	 * 根据微信openid和appid查找用户
	 * @param user
	 * @return
	 */
	UserInfo getUserByWxOpenId(UserInfo user);

	/**
	 * 根据微信openid和appid创建用户
	 * @param user
	 * @return
	 */
	int createUserWithWeChatOpenId(UserInfo user);

	/**
	 * 更新用户信息
	 * @param userInfo
	 * @return
	 */
	int updateUserInfo(UserInfo userInfo);

	/**
	 * 根据用户邮箱获取用户
	 * @param email
	 * @return
	 */
    List<UserInfo> getUserByEmail(String email);

	/**
	 * 根据关键字搜索用户
	 * @param key
	 * @return
	 */
	List<UserInfo> searchUsersByKeyWords(String key);
}
