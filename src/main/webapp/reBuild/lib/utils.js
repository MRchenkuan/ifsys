/**
 * Created by chenkuan on 2017/2/28.
 */
// 取Cookie
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return decodeURI(arr[2]);
    else
        return null;
}

// 取Cookie
function setCookie(name,value)
{
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ encodeURI(value) + ";expires=" + exp.toGMTString();
}

// 取url参数
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = location.search.substr(1).match(reg);
    if(r!=null)return  decodeURI(r[2]); return null;
}

// 时间戳转换
function tmStampToTime(tmStamp){
    var dateObj = new Date(tmStamp);
    return dateObj.getFullYear()+"-"+
        ("00"+(dateObj.getMonth()+1)).substr(-2)+"-"+
        ("00"+(dateObj.getDate())).substr(-2,2)+" "+
        ("00"+dateObj.getHours()).substr(-2,2)+":"+
        ("00"+dateObj.getMinutes()).substr(-2,2)+":"+
        ("00"+dateObj.getSeconds()).substr(-2,2)
}

// 纯数组对象数组深度拷贝方法
function deepCopyArrayObject(list){
    var result = [];
    list.forEach(function(it){
        result.push(deepCopyObject(it))
    })
    return result;
}

// 纯对象深度拷贝方法
function deepCopyObject(source) {
    var result={};
    for (var key in source) {
        if(source.hasOwnProperty(key))
            result[key] = typeof source[key]==='object'? deepCopyObject(source[key]): source[key];
    }
    return result;
}


export default {
    getCookie:getCookie,
    setCookie:setCookie,
    GetQueryString:GetQueryString,
    tmStampToTime,
    deepCopyArrayObject,
    deepCopyObject
}