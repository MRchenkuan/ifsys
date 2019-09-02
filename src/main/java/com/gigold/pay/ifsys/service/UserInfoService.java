package com.gigold.pay.ifsys.service;

import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.BaseController;
import com.gigold.pay.ifsys.bo.InterFaceInvoker;
import com.gigold.pay.ifsys.bo.UserDetail;
import com.gigold.pay.ifsys.bo.WxUserInfo;
import com.gigold.pay.ifsys.dao.InterFaceDao;
import com.gigold.pay.ifsys.dao.InterFaceInvokerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.dao.UserInfoDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoService {
	@Autowired
	UserInfoDao userInfoDao;
	@Autowired
	InterFaceDao interFaceDao;
	@Autowired
	InterFaceInvokerDao interFaceInvokerDao;

	/**
	 * @return the userInfoDao
	 */
	public UserInfoDao getUserInfoDao() {
		return userInfoDao;
	}

	/**
	 * @param userInfoDao
	 *            the userInfoDao to set
	 */
	public void setUserInfoDao(UserInfoDao userInfoDao) {
		this.userInfoDao = userInfoDao;
	}

	/**
	 * 获取用户信息
	 */
	public UserInfo getUser(int id) {
		UserInfo userInfo = null;
		try {
			userInfo = userInfoDao.getUser(id);
		} catch (Exception e) {
			userInfo = null;
		}
		return userInfo;
	}

	/**
	 * 
	 * Title: login<br/>
	 * Description: 登录<br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月17日上午11:51:22
	 *
	 * @param user
	 * @return
	 */
	public UserInfo login(UserInfo user) {
		UserInfo userInfo = null;
		try {
			userInfo = userInfoDao.login(user);
		} catch (Exception e) {
			userInfo = null;
		}
		return userInfo;
	}

	/**
	 * 
	 * Title: addUser<br/>
	 * Description: 添加用户信息<br/>
	 * @author xiebin
	 * @date 2015年12月17日上午11:51:38
	 *
	 * @param userInfo
	 * @return
	 */
	public boolean addUser(UserInfo userInfo) {
		boolean flag = false;
		try {
			int count = userInfoDao.addUser(userInfo);
			if (count > 0) {
				flag = true;
			}

		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取所有的用户列表
	 * @return
     */
	public List<?> getAvlUserList() {
		List<?> userInfos = null;
		try{
			userInfos = userInfoDao.getAvlUserList();
		}catch (Exception e){
			e.printStackTrace();
		}
		return userInfos;
	}

	/**
	 * 根据微信appid和用户openId获取用户
	 * @param appId
	 * @param userOpenId
	 * @return
	 */
    public UserInfo getUserByWxOpenId(String appId, String userOpenId) {
		UserInfo user = new UserInfo();
		user.setWxOpenId(userOpenId);
		user.setWxAppId(appId);
    	user = userInfoDao.getUserByWxOpenId(user);
		return user;
    }

	/**
	 * 根据微信openId创建用户
	 * @return
	 */
	public UserInfo createUserWithWechatUserInfo(String appId, String realUserEmail, WxUserInfo wxUserInfo) throws Exception {
		UserInfo user = new UserInfo();
		// 邮箱
		user.setEmail(realUserEmail);
		// openId
		user.setWxOpenId(wxUserInfo.getOpenid());
		// 登录名 填充为openId
		user.setLoginName(wxUserInfo.getOpenid());
		// 密码填充为openId
		user.setLoginPassword(wxUserInfo.getOpenid());
		// 用户头像
		user.setAvtSrc(wxUserInfo.getHeadimgurl());
		// 用户名 填充为用户昵称
		user.setUserName(wxUserInfo.getNickname());
		user.setCountry(wxUserInfo.getCountry());
		user.setCity(wxUserInfo.getCity());
		user.setProvince(wxUserInfo.getProvince());
		user.setWxAppId(appId);
		user.setChannel(2); // 微信
		int count = userInfoDao.createUserWithWeChatOpenId(user);

		if (count<=0){
			throw new Exception("创建用户失败");
		}
		return user;
	}

	/**
	 * 根据用户登陆账号获取用户
	 * @param loginName
	 * @return
	 */
    public UserInfo getUserByLoginName(String loginName) {
    	return userInfoDao.getUserByLoginKey(loginName);
    }

	/**
	 * 更新用户信息
	 * @param userInfo
	 */
	public int updateUserInfo(UserInfo userInfo) {
		if(userInfo.getId()<=0)return 0;
		return userInfoDao.updateUserInfo(userInfo);
	}

	/**
	 * 获取用户详情
	 * @param user
	 * @return
	 */
    public UserDetail getUserDetail(UserInfo user) {
    	UserDetail userDetail = new UserDetail();
		int createdCount=0,// 创建了多少个接口
				followedCount=0,// 关注数
				followededCount=0;//被多少人关注了
		int userId = user.getId();

		if(userId>0){
			user = userInfoDao.getUser(userId);
			userDetail.setId(user.getId());
			userDetail.setLoginName(user.getLoginName());
			userDetail.setEmail(user.getEmail());
			userDetail.setUserName(user.getUserName());
			userDetail.setPhone(user.getPhone());
			userDetail.setQq(user.getQq());
			userDetail.setPosition(user.getPosition());
			userDetail.setAvtSrc(user.getAvtSrc());
			userDetail.setCountry(user.getCountry());
			userDetail.setCity(user.getCity());
			userDetail.setProvince(user.getProvince());
			userDetail.setChannel(user.getChannel());
			userDetail.setRegistTime(user.getRegistTime());

			followedCount = interFaceDao.getFollowedInterfaces(userId,0,0).size();
			createdCount = interFaceDao.getCreatedInterfaces(userId,0,0).size();
			List<InterFaceInvoker> followededList = interFaceInvokerDao.getInvokerListByFollowedUser(userId);
			List<Integer> _temp =new ArrayList<>();
			for(InterFaceInvoker followeded : followededList){
				if(!_temp.contains(followeded.getuId()))
					_temp.add(followeded.getuId());
			}
			followededCount = _temp.size();
		}

		userDetail.setFollowedCount(followedCount);
		userDetail.setFollowededCount(followededCount);
		userDetail.setCreatedCount(createdCount);

		return userDetail;
    }

	/**
	 * 根据用户邮箱查询用户
	 * @param email
	 * @return
	 */
	public List<UserInfo> getUserByEmail(String email) {
		return userInfoDao.getUserByEmail(email);
	}

	/**
	 * 根据关键字搜索用户
	 * @return
	 */
	public List<UserInfo> searchUsersByKeyWords(String key) {
		if(StringUtil.isEmpty(key))return null;
		return userInfoDao.searchUsersByKeyWords(key+"%");
	}
}
