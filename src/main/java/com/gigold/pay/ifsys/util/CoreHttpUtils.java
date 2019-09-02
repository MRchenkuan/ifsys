package com.gigold.pay.ifsys.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.gigold.pay.framework.core.log.BizLogger;
import com.google.gson.Gson;


/**
 * http工具类
 * @author shuzhan
 * @since 2016-05-03 11:53
 */
public class CoreHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(CoreHttpUtils.class);

    public static final int DEFAULT_TIMEOUT = 5000;

    public static String get(String requestUrl, Object body) throws IOException {
        return CoreHttpUtils.get(requestUrl, body, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String get(String requestUrl, Object body, String contentType) throws IOException {
        return CoreHttpUtils.get(requestUrl, body, contentType, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String get(String requestUrl, Object body, String encoding, int timeout) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, null, "UTF-8", DEFAULT_TIMEOUT,"GET");
    }

    public static String get(String requestUrl, Object body, String contentType, String encoding, int timeout)
            throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, contentType, "UTF-8", DEFAULT_TIMEOUT,"GET");
    }

    public static String get(String requestUrl, Map<String, String> headerMap, Object body) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, headerMap, body, null, "UTF-8", DEFAULT_TIMEOUT,"GET");
    }


    public static String post(String requestUrl, Object body) throws IOException {
        return CoreHttpUtils.post(requestUrl, body, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String post(String requestUrl, Object body, String contentType) throws IOException {
        return CoreHttpUtils.post(requestUrl, body, contentType, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String post(String requestUrl, Object body, String encoding, int timeout) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, null, "UTF-8", DEFAULT_TIMEOUT,"POST");
    }

    public static String post(String requestUrl, Object body, String contentType, String encoding, int timeout)
            throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, contentType, "UTF-8", DEFAULT_TIMEOUT,"POST");
    }

    public static String post(String requestUrl, Map<String, String> headerMap, Object body) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, headerMap, body, null, "UTF-8", DEFAULT_TIMEOUT,"POST");
    }

    public static String put(String requestUrl, Object body) throws IOException {
        return CoreHttpUtils.put(requestUrl, body, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String put(String requestUrl, Object body, String contentType) throws IOException {
        return CoreHttpUtils.put(requestUrl, body, contentType, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String put(String requestUrl, Object body, String encoding, int timeout) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, null, "UTF-8", DEFAULT_TIMEOUT,"PUT");
    }

    public static String put(String requestUrl, Object body, String contentType, String encoding, int timeout)
            throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, contentType, "UTF-8", DEFAULT_TIMEOUT,"PUT");
    }

    public static String put(String requestUrl, Map<String, String> headerMap, Object body) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, headerMap, body, null, "UTF-8", DEFAULT_TIMEOUT,"PUT");
    }

    public static String delete(String requestUrl, Object body) throws IOException {
        return CoreHttpUtils.delete(requestUrl, body, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String delete(String requestUrl, Object body, String contentType) throws IOException {
        return CoreHttpUtils.delete(requestUrl, body, contentType, "UTF-8", DEFAULT_TIMEOUT);
    }

    public static String delete(String requestUrl, Object body, String encoding, int timeout) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, null, "UTF-8", DEFAULT_TIMEOUT,"DELETE");
    }

    public static String delete(String requestUrl, Object body, String contentType, String encoding, int timeout)
            throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, null, body, contentType, "UTF-8", DEFAULT_TIMEOUT,"DELETE");
    }

    public static String delete(String requestUrl, Map<String, String> headerMap, Object body) throws IOException {
        return CoreHttpUtils.callHttp(requestUrl, headerMap, body, null, "UTF-8", DEFAULT_TIMEOUT,"DELETE");
    }

    public static String callHttp(String requestUrl, Map<String, String> headerMap, Object body, String contentTypeString,
                                  String encoding, int timeout,String method) throws IOException {
        String result = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            //Https访问方式时，需要增加SSL相关设置
            if (requestUrl.startsWith("https://")) {
                SSLContext sslContext = null;
                try {
                    sslContext = SSLContext.getInstance("TLS"); //或SSL
                    X509TrustManager[] xtmArray = new X509TrustManager[] { new MyX509TrustManager() };
                    sslContext.init(null, xtmArray, new java.security.SecureRandom());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (sslContext != null) {
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                }
                HttpsURLConnection.setDefaultHostnameVerifier(new myHostnameVerifier());
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置请求方式
            conn.setRequestMethod(method);
            //设置超时时间
            conn.setReadTimeout(timeout);
            //设置请求头部
            if (contentTypeString != null && !contentTypeString.isEmpty()) {
                conn.setRequestProperty("Content-Type", contentTypeString);
            } else {
                conn.setRequestProperty("Content-Type", "application/json");
            }
            if (headerMap != null) {
                for (Entry<String, String> entry : headerMap.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            String outputStr = null;
            if (body instanceof String) {
                outputStr = (String) body;
            } else {
                outputStr = new Gson().toJson(body);
            }
            BizLogger.info("发送短信内容:"+outputStr);
            String key = SystemPropertyConfigure.getProperty("md5.key.des");
            BizLogger.info("key:"+key);
            String keydes = SystemPropertyConfigure.getProperty("md5.key.des");
            BizLogger.info("keydes:"+keydes);
            String md5Value= ecodeByMD5(outputStr,key);
            BizLogger.info("加密结果:"+md5Value);
            conn.setRequestProperty("md5", md5Value);
            conn.setRequestProperty("sid", "586d6fe366e14699a0840dc5b5f2fe66!20160829112242");
            conn.connect();
            if (null != body) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes(encoding));
                outputStream.flush();
                outputStream.close();
            }
            //从输入流读取返回内容
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, encoding);
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            result = buffer.toString();

        } catch (IOException ex) {
            throw ex;
        } finally {
            //释放资源
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                logger.error("关闭流失败", e);
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                logger.error("关闭流失败", e);
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭流失败", e);
            }
            try {
                conn.disconnect();
            } catch (Exception e) {

            }
        }
        return result;
    }

    static class MyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * 重写一个方法
     */
    static class myHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    public static String ecodeByMD5(String originstr,String key) {
        originstr += key;

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    originstr.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (Exception e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static void main(String[] args) {
        Date dt = new Date();
        System.out.println(new Gson().toJson(dt));

        Map<String,List<Map<String,Object>>> sendSms = new  HashMap<String, List<Map<String,Object>>>();


        Map<String, String> sms = new HashMap<String, String>();
        sms.put("idNo", "456789");

        String monitorMobileNbr = "15050521925;15874984646";

        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        String[] mobileNbr = monitorMobileNbr.split(";");

        for(int i=0;i<mobileNbr.length;i++){
            Map<String, Object> smsObj = new HashMap<String, Object>();
            smsObj.put("templateId", 3801l);
            smsObj.put("mobile", mobileNbr[i]);
            smsObj.put("map", sms);

            list.add(smsObj);
        }
        sendSms.put("sendSms", list);
        System.out.println(":"+new Gson().toJson(sendSms));

        Map<String, String> sms1 = new HashMap<String, String>();
        sms1.put("communityId","1607");

        Map<String, Object> sms2 = new HashMap<String, Object>();
        sms2.put("touser","@all");
        sms2.put("toparty","@all");
        sms2.put("totag","@all");

        sms2.put("msgtype","text");

        sms2.put("totag","@all");
        sms2.put("agentid",0);

        Map<String, Object> sms3 = new HashMap<String, Object>();

        sms3.put("content","只能我给你们发信息 ，你们发不了。哈哈。。你们这群愚蠢的地球人");

        sms2.put("text", sms3);

        sms2.put("safe","0");
        try {

//			String rspdt = CoreHttpUtils.post("http://qa.api.shequbanjing.com/mapi/entranceguard/queryGuardList.do", sms1);
//			String rspdt = CoreHttpUtils.post("https://i.gigold.com/foundation/sms/sendSms.do", sendSms);
            String rspdt=	CoreHttpUtils.get("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wx5810cba4a24e33a4&corpsecret=vLYHXs-6ZFqEEZ8ljlrh-R1UztZ3XDfRh-UaXsSUlTw7Lc-0P_5_p2Zp7RluTcrY",null);
            System.out.println(rspdt);

//			String xx=CoreHttpUtils.post("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=cOWqsKYvFKLYbE8sSJPSCja4UW-Srl-BeS_Fcn7cLmKSFsZNjh-5SCuKhhwDCoyR", sms2);
//			System.out.println(xx);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
