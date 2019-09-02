/**
 * Created by chenkuan on 2016/12/23.
 */
(function (w){
    //var $loadingTag = $("#loadingTag")
    //var requestStack = [];
    //setInterval(function(){
    //    var isPending = requestStack.some(function(it){
    //        if(it.readyState==3||it.readyState==2){
    //            return true
    //        }else{
    //            return false
    //        }
    //    });
    //    if(isPending){
    //        $loadingTag.show();
    //    }else{
    //        $loadingTag.hide();
    //    }
    //},1000);
    w.getCookie = function(name) {
        var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
        if(arr=document.cookie.match(reg))
            return decodeURI(arr[2]);
        else
            return null;
    };

    w.setCookie = function(name,value)
    {
        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        document.cookie = name + "="+ encodeURI(value) + ";expires=" + exp.toGMTString();
    };


    function API(urlHost){
        this.urlHost = urlHost;
    }
    API.fn = API.prototype = {};

    API.fn.post = function(url,param,fn,err,failed){
        var request =  $.ajax({
            url: this.urlHost + url,
            type: "post",
            success: function(data){
                // fn(data)
                if (data['rspCd'] == "00000") {
                    fn(data)
                }else if(data['rspCd'] == "30002"){
                    alertTool.alert("会话已失效,请重新登录!",function(){
                        location.href="./index.html"
                    });
                }else if(data['rspCd'] == "D0001"){
                    location.href="./unauthorized.html"
                }else{
                    if(err){
                        err(data)
                    }else{
                        alertTool.alert(data['rspCd']+"-"+data['rspInf']);
                    }
                }
            },
            error:function(data){
                if(failed){
                    failed(data)
                }else{
                    alertTool.alert(data);
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
    // 自动测试平台
    w.autotest_api = new API("http://teamsupport.sqbj.com:8051/autotest/autotest/")

    // 线上环境
    w.api = new API("../");
    w.project_api = new API("../project/");


    // // 测试环境
    // w.api = new API("http://teamsupport.sqbj.com:8051/ifsys/");
    // w.project_api = new API("http://teamsupport.sqbj.com:8051/ifsys/project/");

    // 本地环境
    // w.api = new API("http://localhost:8051/ifsys/");


})(window);