package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;

/**
 * Created by chenkuan
 * on 2017/2/20.
 */
public class VerifyInfoRspDto extends ResponseDto {
    private static final long serialVersionUID = 1L;
    private String target; // 被验证的对象
    private String type ;//被验证的类型

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
}

