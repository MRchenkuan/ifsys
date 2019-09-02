/**
 * Title: InterFacePro.java<br/>
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

/**
 * 接口版本对象
 *
 */

@Component
@Scope("prototype")
public class InterFaceEdition extends Domain implements Serializable {
    private int ifId,userId;
    private String ifVerNo;
    private String ifVerDetail;
    private String status;
    private String ts;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIfId() {
        return ifId;
    }

    public void setIfId(int ifId) {
        this.ifId = ifId;
    }

    public String getIfVerNo() {
        return ifVerNo;
    }

    public void setIfVerNo(String ifVerNo) {
        this.ifVerNo = ifVerNo;
    }

    public String getIfVerDetail() {
        return ifVerDetail;
    }

    public void setIfVerDetail(String ifVerDetail) {
        this.ifVerDetail = ifVerDetail;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
