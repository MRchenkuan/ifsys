/**
 * Title: InterFaceField.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * 接口详情对象
 */

@Component
@Scope("prototype")
public class InterFaceDetail extends Domain implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private int ifId,modifier;
    private String updateTime;
    private String ifEdition;
    private List<IFFields> reqFields,rspFields;
    private List<ReturnCode> rspCds;
    private String reqJsonStr,rspJsonStr;

    public String getReqJsonStr() {
        return reqJsonStr;
    }

    public void setReqJsonStr(String reqJsonStr) {
        this.reqJsonStr = reqJsonStr;
    }

    public String getRspJsonStr() {
        return rspJsonStr;
    }

    public void setRspJsonStr(String rspJsonStr) {
        this.rspJsonStr = rspJsonStr;
    }

    public int getIfId() {
        return ifId;
    }

    public void setIfId(int ifId) {
        this.ifId = ifId;
    }

    public String getIfEdition() {
        return ifEdition;
    }

    public void setIfEdition(String ifEdition) {
        this.ifEdition = ifEdition;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<IFFields> getReqFields() {
        return reqFields;
    }

    public void setReqFields(List<IFFields> reqFields) {
        this.reqFields = reqFields;
    }

    public List<IFFields> getRspFields() {
        return rspFields;
    }

    public void setRspFields(List<IFFields> rspFields) {
        this.rspFields = rspFields;
    }

    public List<ReturnCode> getRspCds() {
        return rspCds;
    }

    public void setRspCds(List<ReturnCode> rspCds) {
        this.rspCds = rspCds;
    }
}
