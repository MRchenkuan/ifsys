// alert框
var alertTool = new Vue({
    el:"#alert",
    data:{
        message:"提醒",
        title:"提醒",
        alertWidget:$("#alert"),
        trueButtonText:"确定",
        falseButtonText:"取消",
        trueButtonVisible:true,
        falseButtonVisible:true,
        trueButtonAction:null,
        falseButtonAction:null,
        alert:function(message,actionTrue){
            if(message)alertTool.message = message;
            alertTool.falseButtonVisible = false;
            alertTool.trueButtonVisible = true;
            alertTool.falseButtonAction = null;
            alertTool.trueButtonAction = null;
            alertTool.alertWidget.modal('show');
            if(actionTrue)alertTool.trueButtonAction = actionTrue;

        },
        confirm:function(message,actionTrue,actionFalse){
            alertTool.falseButtonVisible = true;
            alertTool.trueButtonVisible = true;
            if(!(actionTrue))alert("confirm组件未定义确认事件");
            if(message)alertTool.message = message;
            alertTool.trueButtonAction = actionTrue;
            if(alertTool.falseButtonAction){
                alertTool.falseButtonAction= actionFalse;
            }else{
                alertTool.falseButtonAction = function(){alertTool.alertWidget.modal('hide')}
            }
            alertTool.alertWidget.modal('show');
        }
    },
    methods:{

        dismiss:function(){
            alertTool.alertWidget.modal('hide');
        },
        trueButton:function(){
            if(alertTool.trueButtonAction)alertTool.trueButtonAction();
            alertTool.alertWidget.modal('hide');
        },
        falseButton:function(){
            if(alertTool.falseButtonAction)alertTool.falseButtonAction();
            alertTool.alertWidget.modal('hide');
        }
    }
});

var my_projects =  new Vue({
    el:"#my_projects",
    data:{
        projects:[],
        userInfo:{}
    },
    computed:{
        channel:function () {
            switch(this.userInfo.channel){
                case 1:return "内部注册";break;
                case 2:return "微信注册";break;
                default :return "未知注册方式["+this.userInfo.channel+"]";break
            }
        }
    }
});

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


// 纯对象深度拷贝方法
function deepCopyObject(source) {
    var result={};
    for (var key in source) {
        if(source.hasOwnProperty(key))
        result[key] = typeof source[key]==='object'? deepCopyObject(source[key]): source[key];
    }
    return result;
}
// 纯数组对象数组深度拷贝方法
function deepCopyArrayObject(list){
    var result = [];
    list.forEach(function(it){
        result.push(deepCopyObject(it))
    })
    return result;
}

function tmStampToTime(tmStamp){
    var dateObj = new Date(tmStamp);
    return dateObj.getFullYear()+"-"+
        ("00"+(dateObj.getMonth()+1)).substr(-2)+"-"+
        ("00"+(dateObj.getDay()+1)).substr(-2,2)+" "+
        ("00"+dateObj.getHours()).substr(-2,2)+":"+
        ("00"+dateObj.getMinutes()).substr(-2,2)+":"+
        ("00"+dateObj.getSeconds()).substr(-2,2)
}
