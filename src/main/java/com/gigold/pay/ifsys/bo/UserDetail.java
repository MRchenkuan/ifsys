package com.gigold.pay.ifsys.bo;

/**
 * Created by chenkuan on 2017/5/5.
 */
public class UserDetail extends UserInfo {
    private int
            createdCount=0,// 创建了多少个接口
            followedCount=0,// 关注数
            followededCount=0;//被多少人关注了

    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    public int getFollowedCount() {
        return followedCount;
    }

    public void setFollowedCount(int followedCount) {
        this.followedCount = followedCount;
    }

    public int getFollowededCount() {
        return followededCount;
    }

    public void setFollowededCount(int followededCount) {
        this.followededCount = followededCount;
    }
}
