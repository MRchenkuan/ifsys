/**
 * Title: package-info.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
/**
 * Title: package-info<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author Devin
 * @date 2015年9月16日下午6:12:27
 *
 */
package com.gigold.pay.ifsys.controller;

class CodeItem {

	// 失败
	public static final String IF_FAILURE = "D0000";
	// 用户名或密码不能为空
	public static final String USER_PASS_ERROR = "D1001";

	// 返回码不能为空
	public static final String RSP_CODE_FAILURE = "D0002";
	// 返回码描述不能为空
	public static final String RSP_CODE_DESC_FAILURE = "D0003";
	// 所属接口ID不能为空
	public static final String IF_ID_FAILURE = "D0004";
	// ID不能为空
	public static final String ID_FAILURE = "D0005";
	// 关注者调用信息不能为空
    public static final String REMARK_FAILURE = "D0006";
    //被关注接口ID不能为空
    public static final String FLLOW_IF_ID_FAILURE = "D0007";
    //系统ID不能为空
    public static final String SYS_ID_FAILURE = "D0008";
    //请检查必填项是否已录入
    public static final String NEDD_ITEM_FAILURE = "D0009";
    //参数不合法
    public static final String INVAILD_PARM_FAILURE = "D0010";
	// 用户未登录
    public static final String NOT_LOGIN = "30002";
	// 权限不够
	public static final String NOT_PERMITED = "D0001";
}

class WXCodeItem {

	//用户未认证
	public static final String NOT_AUTHORIZED = "WX302";
	public static final String CLIENT_SESSION_LOST = "WX404";
	public static final String LOGIN_FAILE = "WX999";
	public static final String INFO_GET_FAILE = "WX990";
}

class SessionKey {

	//用户未认证
	public static final String WX_USER_OPEN_ID = "WX_USER_OPEN_ID";
	public static final String WX_USER_INFO = "WX_USER_INFO";
	public static final String SYS_USER_INFO = "SYS_USER_INFO";
	public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
	public static final String LOGIN_INNER_ACCT = "LOGIN_INNER_ACCT";
	public static final String EMAIL_CAPTURE = "EMAIL_CAPTURE";
	public static final String INNER_EMAIL_CAPTURE = "INNER_EMAIL_CAPTURE";
	public static final String CLIENT_SSID = "CLIENT_SSID";

}