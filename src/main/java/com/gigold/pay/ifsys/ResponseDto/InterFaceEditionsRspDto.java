package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.InterFaceEdition;
import java.util.List;

/**
 * Created by chenkuan
 * on 2017/1/5.
 */
public class InterFaceEditionsRspDto extends ResponseDto {
    private List<InterFaceEdition> editions;

    public List<InterFaceEdition> getEditions() {
        return editions;
    }

    public void setEditions(List<InterFaceEdition> editions) {
        this.editions = editions;
    }
}
