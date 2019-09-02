package com.gigold.pay.ifsys.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenkuan
 * on 2017/2/15.
 */
public enum TypeRole {

    // 项目管理员
    Admin(0,1,2,3,4,5),

    // 项目成员
    Member(0,1,2,3),

    // 项目访客
    Visitor(0,1,2),

    // 普通用户
    User(0,1),

    //任意用户
    Anyone(0);

    private List<Integer> privileges;

    TypeRole(int... prvs) {
        privileges = new ArrayList<>();
        for(int prv :prvs){
            privileges.add(prv);
        }
    }

    public List<Integer> getPrivilege(){
        return privileges;
    }
}

