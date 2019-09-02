package com.gigold.pay.ifsys.RequestDto;


import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

/**
 * Created by chenkuan
 * on 2017/2/20.
 */
public class VerifyInfoReqDto extends RequestDto{
    private static final long serialVersionUID = 1L;
    private String target;// 需要验证的对象
    private String type; // 验证类型

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean validate() throws OtherExceptionCollect {
        return true;
    }
}
