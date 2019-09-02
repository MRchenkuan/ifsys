package com.gigold.pay.ifsys.RequestDto;

import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.ReturnCode;

import java.util.List;

/**
 * Created by chenkuan
 * on 2016/11/2.
 */
public class InterFaceDetailReqDto extends RequestDto {
    private int ifId;
    private String version;
    private InterFaceInfo interFaceInfo;
    private List<IFFields> reqFields,rspFields;
    private List<ReturnCode> returnCodes;
    private List<Integer> invokerList;
    private boolean advance=false;

    public List<Integer> getInvokerList() {
        return invokerList;
    }

    public void setInvokerList(List<Integer> invokerList) {
        this.invokerList = invokerList;
    }

    public InterFaceInfo getInterFaceInfo() {
        return interFaceInfo;
    }

    public void setInterFaceInfo(InterFaceInfo interFaceInfo) {
        this.interFaceInfo = interFaceInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIfId() {
        return ifId;
    }

    public void setIfId(int ifId) {
        this.ifId = ifId;
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

    public List<ReturnCode> getReturnCodes() {
        return returnCodes;
    }

    public void setReturnCodes(List<ReturnCode> returnCodes) {
        this.returnCodes = returnCodes;
    }

    public boolean isAdvance() {
        return advance;
    }

    public void setAdvance(boolean advance) {
        this.advance = advance;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return reqFields != null && rspFields != null && returnCodes != null;
    }
}
