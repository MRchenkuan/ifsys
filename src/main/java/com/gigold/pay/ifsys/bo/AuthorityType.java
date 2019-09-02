package com.gigold.pay.ifsys.bo;

/**
 * Created by chenkuan
 * on 2017/2/15.
 */
public enum AuthorityType {

    // 登录和权限都验证 默认
    Validate,

    // 不验证
    NoValidate,

    // 不验证权限
    NoAuthority;
}