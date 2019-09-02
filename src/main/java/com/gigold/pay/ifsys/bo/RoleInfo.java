package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;

import java.io.Serializable;

/**
 * Created by mac
 * on 2017/2/15.
 */
public class RoleInfo extends Domain implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private int id;
    private String roleName;
    private String privList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPrivList() {
        return privList;
    }

    public void setPrivList(String privList) {
        this.privList = privList;
    }
}
