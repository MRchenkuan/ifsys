package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac
 * on 2017/2/9.
 */
public class PrivilegeInfo extends Domain implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private int id;
    private String privilegeName,privilegeDesc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getPrivilegeDesc() {
        return privilegeDesc;
    }

    public void setPrivilegeDesc(String privilegeDesc) {
        this.privilegeDesc = privilegeDesc;
    }
}
