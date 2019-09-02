/**
 * Created by chenkuan on 2016/12/23.
 */
'use strict';
function API(urlHost){
    this.urlHost = urlHost;
}

API.fn = API.prototype = {};

/**
 * post工具
 * @param url 接口地址
 * @param param 请求参数
 * @param fn 成功回调
 * @param err 错误回调
 * @param failed 失败回调
 */
API.fn.post = function(url,param,fn,err,failed){
    var request =  $.ajax({
        url: this.urlHost + url,
        type: "post",
        success: function(data){
            // fn(data)
            if (data['rspCd'] == "00000") {
                fn(data)
            }else if(data['rspCd'] == "30002"){
                alertEvent.$emit("会话已失效,请重新登录!",function(){
                    location.href="../../index.html"
                });
            }else if(data['rspCd'] == "D0001"){
                location.href=".unauthorized.html"
            }else{
                if(err){
                    err(data)
                }else{
                    alertEvent.$emit(data['rspCd']+"-"+data['rspInf']);
                }
            }

        },
        error:function(data){
            if(failed){
                failed(data)
            }else{
                alertEvent.$emit(data);
            }
        },
        data: JSON.stringify(param),
        //dataType: 'jsonp',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        contentType: "application/json"
    });
};

// // 自动测试平台
window.autotest = new API("http://teamsupport.sqbj.com:8051/autotest/autotest/")

// // 测试环境
window.api = new API("http://teamsupport.sqbj.com:8051/ifsys/");
window.project = new API("http://teamsupport.sqbj.com:8051/ifsys/project/");

// // // 线上环境
// window.api = new API("../../");
// window.project = new API("../../project/");
