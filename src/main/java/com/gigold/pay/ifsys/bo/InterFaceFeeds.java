package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkuan
 * on 16/6/29.
 */
public class InterFaceFeeds extends Domain implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String oper;
    private List<InterFaceChanges> changesList;
    private String ts;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public List<InterFaceChanges> getChangesList() {
        return changesList;
    }

    public void setChangesList(List<InterFaceChanges> changesList) {
        this.changesList = changesList;
    }

}
