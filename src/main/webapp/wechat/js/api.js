/**
 * Created by chenkuan on 2016/12/23.
 */
(function (w){

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
                fn(data)
            },
            error:function(data){
                failed(data)
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

    API.fn.get = function(url,paramstr,fn,err,failed){

        var request =  $.ajax({
            url: this.urlHost + url +"?"+paramstr,
            type: "get",
            success: function(data){
                fn(data)
            },
            error:function(data){
                failed(data)
            },
            //dataType: 'jsonp',
            xhrFields: {
                withCredentials: true
            },
            crossDomain: true,
            contentType: "application/json"
        });
    };

    // w.api = new API("http://teamsupport.sqbj.com:8051/ifsys2/weChat/");
    w.api = new API("../");
    // w.api = new API("http://localhost:8051/ifsys/");

    w.autotest_api = new API("http://teamsupport.sqbj.com:8051/autotest/autotest/")

})(window);