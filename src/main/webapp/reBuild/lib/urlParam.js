/**
 * Created by chenkuan on 2017/2/28.
 */
'use strict';
import Utils from './utils'
var setCookie = Utils.setCookie;
var getCookie = Utils.getCookie;

var urlParam = {};

urlParam.setListStatus= function(pid,sysId,proId,pageNum){
    // 初始化查询条件参数,重设cookie
    setCookie("PID",pid||getCookie("PID")||"0");
    var ckProIdKey = getCookie("PID")+"-ifProId";
    var ckSysIdKey = getCookie("PID")+"-ifSysId";
    setCookie(ckProIdKey,proId||getCookie(ckProIdKey)||"0");
    setCookie(ckSysIdKey,sysId||getCookie(ckSysIdKey)||"0");
    setCookie("reqFieldsViewType",getCookie("reqFieldsViewType")||"CNT");
    setCookie("rspFieldsViewType",getCookie("rspFieldsViewType")||"CNT");
    setCookie("ifmlImporterTheme",getCookie("ifmlImporterTheme")||"light");
    var pageNumKey = getCookie(ckSysIdKey)+"-"+getCookie(ckProIdKey)+"-pageNum";
    setCookie(pageNumKey,Math.max(1,pageNum||getCookie(pageNumKey)||1));

    urlParam.pageNum = getCookie(pageNumKey)||1; // 分页号
    urlParam.PID = getCookie("PID")||"0";// 项目号
    urlParam.ifProId = getCookie(ckProIdKey)||"0";// 产品号
    urlParam.ifSysId = getCookie(ckSysIdKey)||"0"; // 系统号
    urlParam.rspFieldsViewType = getCookie("rspFieldsViewType")||"CNT";
    urlParam.reqFieldsViewType = getCookie("reqFieldsViewType")||"CNT";
    urlParam.ifmlImporterTheme = getCookie("ifmlImporterTheme")||"light";
};

// 初始化查询条件参数,重设cookie
urlParam.setListStatus();

export default urlParam;