package com.gigold.pay.ifsys.ResponseDto;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.ifsys.bo.*;

import java.util.List;

/**
 * Created by chenkuan
 * on 2016/11/2.
 */
public class InterFaceDetailRspDto extends ResponseDto {
    private InterFaceEdition edition;
    private InterFaceDetail detail;

    public InterFaceDetail getDetail() {
        return detail;
    }

    public void setDetail(InterFaceDetail detail) {
        this.detail = detail;
    }

    public InterFaceEdition getEdition() {
        return edition;
    }

    public void setEdition(InterFaceEdition edition) {
        this.edition = edition;
    }
}
