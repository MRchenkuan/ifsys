/**
 * Title: InterFaceField.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.service.AbstractService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 接口请求格式
 */
@Component
@Scope("prototype")
public class IFFields extends Domain implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    private int id;
    private String parent,k,mock,type,note,feildFlag;
    boolean req;
//        {id:"",parent:"",k:"a",mock:"1",type:"1",note:"描述字段",req:"req是否必须"},


    public boolean isReq() {
        return req;
    }

    public void setReq(boolean req) {
        this.req = req;
    }

    public String getFeildFlag() {
        return feildFlag;
    }

    public void setFeildFlag(String feildFlag) {
        this.feildFlag = feildFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
