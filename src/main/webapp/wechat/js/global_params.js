(function($,w){
    w.GetQueryString = function(name){
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = w.location.search.substr(1).match(reg);
         if(r!=null)return  decodeURI(r[2]); return null;
    };
    //初始参数
    var state = GetQueryString("state");

    //bootstrap alert扩展
    /**
     * 警告框
     * @param msg
     * @param delay
     * @param $target
     * @param style [danger,warning,success,info]
     * @constructor
     */
    $.fn.Message = function(msg,delay,style){
        console.log("ok");
        var $target = this;
        var allowStyle = ['danger','warning','success','info'];
        if(!style || allowStyle.indexOf(style)<0 )style="info";
        var ele = document.createElement("div");
        var $ele = $(ele);
        $ele.addClass("alert").addClass("alert-"+style).attr("role","alert").css({margin:0,display:"none",opacity:.9}).text(msg);
        $target.append($ele);
        $ele.slideDown(500).delay(delay).slideUp(500).queue(function(){
            $ele.off("click").remove();
        });
        // 信息框手动移除
        $ele.click(function(e) {
            e.stopPropagation();
            $ele.stop().slideUp(300).queue(function(){
                $ele.off("click").remove();
            });
        });
    };

    w.getCookie = function(name)
    {
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

    // 初始化全局参数和当前访问类型 followed designed all
    w.urlParam = {};
    w.urlParam.state = "followed" ;// 页面参数,followed 我关注的,designed 我设计的
    if(state == "followed" || state=="designed"|| state=="all")w.urlParam.state = state;
    w.urlParam.openTargetInterface = GetQueryString("state");
    // 初始化全局参数
    w.globalParam = {
        pageTitle:"独孤九剑系统",
        sysName:"全部",
        screenSize:{
            w:w.innerWidth,
            h:w.innerHeight
        },
        // 根据"首次打开页面不包含pageNum参数"来判定
        isFirstLoad:!GetQueryString("pageNum"),
        // 根据"切换系统时,ifSysId一定发生变化"来判定
        isSystemSwitched:GetQueryString("ifSysId") != getCookie(w.urlParam.state+".ifSysId"),
        // 根据state参数判断是否要打开指定接口
        toInterface:state.split("::")[0] == "toInterface" ? state.split("::")[1] : false
    };
    //初始化查询条件参数,重设cookie
    setCookie(w.urlParam.state+".pageNum",Math.max(1,GetQueryString("pageNum")||getCookie(w.urlParam.state+".pageNum")||1));
    setCookie(w.urlParam.state+".ifProId",GetQueryString("ifProId")||getCookie(w.urlParam.state+".ifProId")||"0");
    setCookie(w.urlParam.state+".ifSysId",GetQueryString("ifSysId")||getCookie(w.urlParam.state+".ifSysId")||"0");
    w.urlParam.pageNum = getCookie(w.urlParam.state+".pageNum")||1; // 分页号
    w.urlParam.ifProId = getCookie(w.urlParam.state+".ifProId")||"0";// 产品号
    w.urlParam.ifSysId = getCookie(w.urlParam.state+".ifSysId")||"0"; // 系统号

    // 初始化页面标题
    if(urlParam.state=='followed')globalParam.pageTitle = "我关注的接口";
    if(urlParam.state=='designed')globalParam.pageTitle = "我设计的接口";

    // 不记录历史的页面跳转方法
    w.reLocation = function(){
        var tempArr = [];
        for(var k in urlParam){
            if(urlParam.hasOwnProperty(k)){
                var v = urlParam[k];
                tempArr.push(k+"="+v);
            }
        }
        var tempStr = tempArr.join("&");
        var queryString = encodeURI(tempStr);
        location.replace("?"+queryString)
    };

    w.setTitle = function(title){
        var $body = $('body');
        document.title = title;
        var $iframe = $('<iframe style="display: none" src="css.css"></iframe>');
        $iframe.on('load',function() {
            setTimeout(function() {
                $iframe.off('load').remove();
            }, 0);
        }).appendTo($body);
    };
    w.tmStampToTime = function(tmStamp){
        var dateObj = new Date(tmStamp);
        return dateObj.getFullYear()+"-"+
            ("00"+dateObj.getMonth()).substr(-2)+"-"+
            ("00"+dateObj.getDay()).substr(-2)+" "+
            ("00"+dateObj.getHours()).substr(-2)+":"+
            ("00"+dateObj.getMinutes()).substr(-2)+":"+
            ("00"+dateObj.getSeconds()).substr(-2)
    }
})($,window);