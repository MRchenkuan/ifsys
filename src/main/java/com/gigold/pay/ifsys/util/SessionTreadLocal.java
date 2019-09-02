package com.gigold.pay.ifsys.util;


/**
 * Created by chenkuan
 * on 16/7/1.
 */
public class SessionTreadLocal {
    private static final ThreadLocal<Integer> userIdThreadLocal = new ThreadLocal<>();

    public static int getUserId() {
        System.out.println("userIdThreadLocal:"+userIdThreadLocal);
        System.out.println("value:"+userIdThreadLocal.get());
        int userId = userIdThreadLocal.get();
        if(userId<=0){
            (new Exception("异常:调用过程中未发现用户ID")).printStackTrace();
        }
        return userId;
    }

    public static void setUserId(int userId){
        System.out.println("userIdThreadLocal:"+userIdThreadLocal);
        System.out.println("setting value:"+userId);
        userIdThreadLocal.set(userId);
    }
}
