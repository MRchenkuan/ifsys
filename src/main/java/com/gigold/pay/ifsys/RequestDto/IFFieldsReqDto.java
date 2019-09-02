/**
 * Title: QueryDemoRequestDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.ifsys.RequestDto;

import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;
import com.gigold.pay.ifsys.bo.IFFields;
import com.gigold.pay.ifsys.bo.ReturnCode;

import java.util.List;
import java.util.Map;


/**
 * 请求dto 接口id和关系表
 */
public class IFFieldsReqDto extends RequestDto {
    /** serialVersionUID */
	private static final long serialVersionUID = 1L;

    private int ifId;
    private String fieldType; // 1请求字段,2返回字段
    private List<IFFields> fieldsList;
    private List<ReturnCode> rspCodeList;

    public List<ReturnCode> getRspCodeList() {
        return rspCodeList;
    }

    public void setRspCodeList(List<ReturnCode> rspCodeList) {
        this.rspCodeList = rspCodeList;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public int getIfId() {
        return ifId;
    }

    public void setIfId(int ifId) {
        this.ifId = ifId;
    }

    public List<IFFields> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<IFFields> fieldsList) {
        this.fieldsList = fieldsList;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
