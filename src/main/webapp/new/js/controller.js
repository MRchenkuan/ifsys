/**
 * Created by chenkuan on 2016/12/23.
 */
var $body = $("body");
var $interfaceDetail = $("#interfaceDetail");

var $basicInfo = $("#basicInfo");
var $reqBody = $("#reqBody");
var $rspBody = $("#rspBody");
var $returnCodeList = $("#returnCodeList");
var $changeLogs = $("#changeLogs");
var $editor_modal_box_value = $("#editor_modal_box_value"),
    $editor_modal_box = $("#editor_modal_box"),
    $editor_modal_box_content = $("#editor_modal_box_content"),
    $switchList = $("#switchList"),
    $btnSwitchList = $("#btn-switchList"),
    $sysListFrame = $("#sysListFrame"),
    $proList = $("#proList"),
    $sysList = $("#sysList"),
    $ifSearchInput = $("#ifSearchInput");
var $versionBox = $("#versionLogs");

if(!(urlParam.PID && urlParam.PID>0))location='./projects.html';


// 初始获取系统信息
nav.btnCreateIf.visible = false;
api.post("getAllSysInfo.do",{
    type:"all",
    pid:urlParam.PID
},function(data){
    interfaceDetail_container_editor.sysList = sysList.sysList = data['sysList'];
    interfaceDetail_container_editor.proList = sysList.allProList = data['proList'];
    sysList.memberRoles = data['memberRoles'];
    nav_path.sysId = urlParam.ifSysId;
    nav_path.proId = urlParam.ifProId;

    // 角色权限功能初始化
    sysList.memberRoles.some(function (it) {
        // 系统列表编辑权限
        if(it.id==0){
            sysList.canEditSys = true;
        }
    });

    if(data['sysList'].length>0){
        nav.btnCreateIf.visible = true;
    }

    // 系统信息获取
    sysList.sysList.some(function(it){
        if(it.id == nav_path.sysId){
            nav_path.sysName = it.sysName;
        }
    });

    // 产品信息获取
    sysList.allProList.some(function(it){
        if(it.id == nav_path.proId){
            nav_path.proName = it.proName;
        }
    });
    document.title = nav_path.sysName+">"+nav_path.proName;
});
// 如果url标示了要打开指定接口
if(urlParam.ifId){
    api.post("queryInterFaceById.do",{"interFaceInfo":{"id":urlParam.ifId}},function(data){
        // 展示基本信息
        interfaceList.showInterfaceDetail({
            id:urlParam.ifId,
            ifName:data['interFaceInfo'].ifName
        });
        // 展示面包屑导航
        nav_path.sysId=data.system.id;
        nav_path.sysName=data.system.sysName;
        nav_path.proId=data.pro.id;
        nav_path.proName=data.pro.proName;
        document.title = nav_path.sysName+">"+nav_path.proName;
        // 记录当前系统产品
        if(data.pro.id>0&&data.system.id>0){
            setCookie(urlParam.PID+"-ifProId",data.pro.id);
            setCookie(urlParam.PID+"-ifSysId",data.system.id);
        }
        // 展示接口列表
        api.post("getallifsys.do",{
            "ifName":"",
            "ifProId":urlParam.ifProId||0,
            "ifSysId":urlParam.ifSysId||0,
            "pageNum":1,
            'pid':urlParam.PID
        },function(data){
            interfaceList.pageInfo = data['pageInfo']
        });
    });
}

// 初始化invoker插件
api.post("getMemberList.do",{
    'pid':urlParam.PID
},function(data){
    interfaceDetail_container_editor.freeSearch = $("#editor_invokers_textarea").searchBox(data["userInfos"])
});

// 初始获取接口列表
api.post("getallifsys.do",{
    "ifName":"",
    "ifProId":urlParam.ifProId||0,
    "ifSysId":urlParam.ifSysId||0,
    "pageNum":urlParam.pageNum||1,
    'pid':urlParam.PID
},function(data){
    interfaceList.pageInfo = data['pageInfo']
});

// 获取feed信息
api.post("getIfsysFeed.do",{
    'pid':urlParam.PID,
    limit:200
},function(data){
    indexPage.feedList = data['feedsList']
});

// 二级菜单绑定
$body.delegate(".sysListItem>li>a","mouseover",function(e){
    var a = e.currentTarget;
    var sysId = a.getAttribute("data-sysId");
    sysList.proList = [];
    sysList.allProList.some(function(item){
        if(sysId == item.sysId)sysList.proList.push(item)
    });
    sysList.proListStyle = ";display:block";
    sysList.proListStyle += ";top:"+a.offsetTop+"px;left:"+(a.parentNode.offsetLeft + a.clientWidth) +"px";
    // 从产品列表选取当前系统下的列表
});

// 接口列表点击事件
$body.delegate(".sysListItem>li>a","click",function(e){
    var a = e.currentTarget;
    var sysId = a.getAttribute("data-sysId");
    sysList.proList = [];
    sysList.allProList.some(function(item){
        if(sysId == item.sysId)sysList.proList.push(item)
    });
    sysList.proListStyle = ";display:block";
    sysList.proListStyle += ";top:"+a.offsetTop+"px;left:"+(a.parentNode.offsetLeft + a.clientWidth) +"px";
});

// 翻页 interfaceList_pager
$body.delegate("#nextPage,#prePage","click",function(e) {
    var li = e.currentTarget;
    var pageNum = li.getAttribute("data-pageNum");
    if(!$(li).hasClass("disabled")){
        api.post("getallifsys.do",{
            "ifName":"",
            "ifProId":urlParam.ifProId||0,
            "ifSysId":urlParam.ifSysId||0,
            "pageNum":pageNum,
            'pid':urlParam.PID
        },function(data){
            interfaceList.pageInfo = data['pageInfo'];
            setCookie(pageNumKey,pageNum)
        });
    }
});

// right_side_nav 侧边导航滚动事件
$interfaceDetail.scroll(function(e){
    var scrollTop = $interfaceDetail.scrollTop()+100;

    // 计算区间
    var basicInfoSection = {s:$basicInfo.position().top,e:$basicInfo.position().top+$basicInfo.height()};
    var reqBodySection = {s:$reqBody.position().top,e:$reqBody.position().top+$reqBody.height()};
    var rspBodySection = {s:$rspBody.position().top,e:$rspBody.position().top+$rspBody.height()};
    var returnCodeListSection = {s:$returnCodeList.position().top,e:$returnCodeList.position().top+$returnCodeList.height()};
    var changeLogsListSection = {s:$changeLogs.position().top,e:$changeLogs.position().top+$changeLogs.height()};

    // 计算位置
    if(scrollTop>=basicInfoSection.s && scrollTop<basicInfoSection.e)right_side_nav.current="basicInfo";
    if(scrollTop>=reqBodySection.s && scrollTop<reqBodySection.e)right_side_nav.current="reqBody";
    if(scrollTop>=rspBodySection.s && scrollTop<rspBodySection.e)right_side_nav.current="rspBody";
    if(scrollTop>=returnCodeListSection.s && scrollTop<returnCodeListSection.e)right_side_nav.current="returnCodeList";
    if(scrollTop>=changeLogsListSection.s && scrollTop<changeLogsListSection.e)right_side_nav.current="changeLogs";
});



// 绑定json导入入口按钮
$body.delegate("#addRspCdJson",'click',function(){
    // 绑定返回码导入按钮
    editor_modal_box.isJsonValid = "unchecked";
    editor_modal_box.title="导入返回码";
    editor_modal_box.type = "RSP_CODE";
    editor_modal_box.jsonStr = "/*请填写标准的JSON:*/\n"+interfaceDetail_container_editor.stringifyRspCdList();
    editor_modal_box.JSONCheck($editor_modal_box_content,$editor_modal_box_value)
}).delegate("#addReqJson",'click',function(){
    // 返回参数导入按钮
    editor_modal_box.isJsonValid = "unchecked";
    editor_modal_box.title="输入参数 JSON 导入";
    editor_modal_box.type = "REQ_JSON";
    editor_modal_box.jsonStr = "/*请导入标准的JSON:*/\n"+interfaceDetail_container_editor.stringifyFields("1");
    editor_modal_box.JSONCheck($editor_modal_box_content,$editor_modal_box_value)
}).delegate("#addRspJson",'click',function(){
    // 返回参数导入按钮
    editor_modal_box.isJsonValid = "unchecked";
    editor_modal_box.title="输出参数 JSON 导入";
    editor_modal_box.type = "RSP_JSON";
    editor_modal_box.jsonStr = "/*请导入标准的JSON:*/\n"+interfaceDetail_container_editor.stringifyFields("2");
    editor_modal_box.JSONCheck($editor_modal_box_content,$editor_modal_box_value)
});

// 弹出框json格式化校验
$body.delegate("#editor_modal_box_content",'click',function() {
    $editor_modal_box_content.hide();
    $editor_modal_box_value.show().focus();
}).delegate("#editor_modal_box_value",'blur',function() {
    editor_modal_box.JSONCheck($editor_modal_box_content,$editor_modal_box_value);
});

// json导入动作按钮
$body.delegate("#importJson",'click',function(){
    editor_modal_box.JSONCheck($editor_modal_box_content,$editor_modal_box_value);
    if(editor_modal_box.isJsonValid == "true" ){
        editor_modal_box.fillJSONAsItems();
        $editor_modal_box.modal('hide')
    }
});

//// 绑定系统列表事件
var sysListdispearTimer;
$body.delegate("#sysList .list-group-item",'mouseenter',function(e) {
    // 产品列表展示
    var a = e.currentTarget;
    $proList.css({
        marginLeft:$sysList.width(),
        top:Math.max(0,14+$(a).position().top - $proList.height()/2)
    })
}).delegate("#btn-switchList",'mouseenter mousemove',function(e){
    // 系统列表展示
    e.stopPropagation();
    clearTimeout(sysListdispearTimer)
    $sysListFrame.show();
}).delegate("#switchList",'mouseenter mousemove click',function(e){
    // 阻止冒泡
    e.stopPropagation();
    clearTimeout(sysListdispearTimer);
}).bind("mousemove",function(e){
    clearTimeout(sysListdispearTimer);
    sysListdispearTimer = setTimeout(function () {
        $sysListFrame.hide();
        clearTimeout(sysListdispearTimer)
    }, 50)
});

// 回车键搜全局
$body.delegate("#ifSearchInput",'keydown',function(e) {
    if(e.keyCode==13){
        var keywords =  $ifSearchInput.val();
        if(!keywords)return;
        // 初始获取接口列表
        api.post("getallifsys.do",{
            "ifName":keywords,
            "ifProId":0,
            "ifSysId":0,
            "pageNum":1,
            'pid':urlParam.PID
        },function(data){
            interfaceList.pageInfo = data['pageInfo'];
            nav_path.sysId=0;
            nav_path.proId=0;
        });
    }
});
// 点击按钮搜全局
$body.delegate("#globalSearch1,#globalSearch2",'click',function(e) {
    var keywords =  $ifSearchInput.val();
    if(!keywords)return;
    // 初始获取接口列表
    api.post("getallifsys.do",{
        "ifName":keywords,
        "ifProId":0,
        "ifSysId":0,
        "pageNum":1,
        'pid':urlParam.PID
    },function(data){
        interfaceList.pageInfo = data['pageInfo']
        nav_path.sysId=0;
        nav_path.proId=0;
    });
});

// 点击按钮搜当前系统
$body.delegate("#scopeSearch",'click',function(e) {
    var keywords =  $ifSearchInput.val();
    if(!keywords)return;
    // 初始获取接口列表
    api.post("getallifsys.do",{
        "ifName":keywords,
        "ifProId":urlParam.ifProId||0,
        "ifSysId":urlParam.ifSysId||0,
        "pageNum":1,
        'pid':urlParam.PID
    },function(data){
        interfaceList.pageInfo = data['pageInfo'];
        nav_path.sysId=urlParam.ifProId||0;
        nav_path.proId=urlParam.ifSysId||0;
    });
});

// 系统列表等其他隐藏事件
$body.click(function(){
    $sysListFrame.hide();
});

// 版本更新记录
$(document).ready(function () {
    var prmVersion = getCookie("version") *1;
    var nowVersion = $versionBox.attr("data-version")*1;
    console.log(nowVersion,prmVersion)
    if(nowVersion > prmVersion){
        $versionBox.modal("show");
        setCookie("version",nowVersion)
    }else if(nowVersion && !prmVersion){
        $versionBox.modal("show");
        setCookie("version",nowVersion)
    }
});