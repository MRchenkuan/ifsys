(function () {
    // 设置页面标题
    setTitle(globalParam.pageTitle);
    // body对象
    var $body = $("body");
    // 信息提示框对象
    var information = $("#information");
    // 接口访问地址
    var dataUrl = "";
    if (urlParam.state == 'followed') {
        dataUrl = "weChat/getFollowedIfList.do";
    } else if (urlParam.state == 'designed') {
        dataUrl = "weChat/getInterfaceList.do";
    } else if (urlParam.state = 'all'){
        dataUrl = "getallifsys.do";
    } else{
        information.Message("无法确定访问页面,state为:" + urlParam.state, 2000, "warning")
    }

    // 获取接口列表
    $.ajax({
        url: urlHost + dataUrl,
        type: "post",
        success: function (data) {

            if (data['rspCd'] == "00000") {
                data = data['pageInfo'];
                console.log(data);
                if (!data['list'] || data['list'].length <= 0) {
                    information.Message("暂无你的记录,换个系统试试!", 2000,"warning");
                    urlParam.ifSysId=0;
                    urlParam.ifProId=0;
                }


                // 格式化时间
                data["list"].some(function(it){
                    it['ifCreateTime'] = tmStampToTime(it['ifCreateTime'])
                });
                // 渲染
                $("#interfaceList").html(template("tpl_interfaceList", data));
            } else {
                information.Message(data['rspInf'], 3000, "warning");
            }
        },
        data: JSON.stringify({
            "ifName": "",
            "ifProId": urlParam.ifProId,
            "ifSysId": urlParam.ifSysId,
            "pageNum": urlParam.pageNum
        }),
        contentType: "application/json"
    });

    // 获取系统信息
    $.ajax({
        url: urlHost + "weChat/getSystemInfo.do",
        type: "post",
        success: function (data) {
            if (data['rspCd'] == "00000") {
                var sysList = data['sysList'];
                // 设置标题
                sysList.some(function (it) {
                    if (it.id == urlParam.ifSysId) {
                        setTitle(globalParam.pageTitle+">"+it.sysName);
                        globalParam.sysName = it.sysName;
                        if(globalParam.isSystemSwitched) information.Message("切换到:\""+it.sysName+"\"",3000);
                    }
                });
                if (!sysList) {
                    information.Message("获取系统信息失败!", 3000, "warning");
                }
                if (sysList.length <= 0) {
                    information.Message("很遗憾!在\""+globalParam.pageTitle+"\"中,你没有任何记录!", 2000,"warning");
                }

                $("#selectSystem").html(template("tpl_selectSystem", data));
            } else {
                information.Message("[" + data['rspCd'] + "]" + data['rspInf'], 3000, "warning");
            }
        },
        data: JSON.stringify({
            "type": urlParam.state
        }),
        contentType: "application/json"
    });

    //绑定翻页事件
    $body.delegate("#nxt,#prv", "click", function (e) {
        var li = e.currentTarget;
        var pageNum = li.getAttribute("data-pageNum");
        if (pageNum>0) {
            urlParam.pageNum = pageNum;
            reLocation();
        } else {
            console.log("页码为:" + pageNum);
            return false;
        }
    });

    //切换系统
    $body.delegate("#systemList>li", "click", function (e) {
        var li = e.currentTarget;
        var ifSysId = li.getAttribute("data-ifSysId");
        var ifSysName = li.getAttribute("data-ifSysName");
        if (ifSysId>0) {
            urlParam.ifSysId = ifSysId;
            urlParam.pageNum = 1;
            reLocation();
        } else {
            console.log("系统ID为:" + ifSysId);
            return false;
        }
    });

    //绑定接口详情获取事件
    $body.delegate(".interfaceListItem","click",function(e){
        var li = e.currentTarget;
        var ifId = li.getAttribute("data-ifId");
        rendInterfaceDetail(ifId);
    });

    //绑定关注取消关注接口
    $body.delegate(".followed-eye","click",function(e) {
        e.stopPropagation()
        var span = e.currentTarget;
        var $span = $(span);
        // 不可用状态,退出
        if($span.hasClass("disabled"))return;
        // 可用状态,记录当前class
        var _class = $span.attr("class");
        // 设置过渡class
        $span.removeClass().addClass("glyphicon glyphicon-asterisk followed-eye blind disabled");
        // 接口ID
        var ifId = span.getAttribute("data-ifId");
        // 接口名
        var ifName = span.getAttribute("data-ifName");
        // 设置定时清除过渡,并还原class
        var timeId = setTimeout(function(){
            $span.removeClass().addClass(_class);
        },10000);
        $.ajax({
            url: urlHost + "followInterface.do",
            type: "post",
            success: function (data) {
                // 清除定时器
                clearTimeout(timeId);
                if (data['rspCd'] == "00000") {
                    //data-ifName="{{ item.ifName }}"
                    if(data['follow']){
                        information.Message("已关注:"+ifName,2500,'success');
                        // 更新样式
                        $span.removeClass().addClass("glyphicon glyphicon-eye-open followed-eye followed")
                    }else{
                        information.Message("取消关注:"+ifName,2500,'warning');
                        // 更新样式
                        $span.removeClass().addClass("glyphicon glyphicon-eye-close followed-eye blind")
                    }
                } else {
                    // 还原样式
                    $span.removeClass().addClass(_class);
                    information.Message("关注接口调用失败[" + data['rspCd'] + "]" + data['rspInf'], 2000, "warning");
                }
            },
            error: function (xhr, err, e) {
                information.Message(err.toString(), 2000, "danger");
            },
            data: JSON.stringify({
                "ifId": ifId*1,
                "remark":"微信关注"
            }),
            contentType: "application/json"
        });
    });

    // 首次打开页面提示用户
    if (globalParam.isFirstLoad) {
        // 获取用户本身
        $.ajax({
            url: urlHost + "whoAmI.do",
            type: "post",
            success: function (data) {
                if (data['rspCd'] == "00000") {
                    data = data['userInfo'];
                    if (data['userName']) {
                        var username = data['userName'];
                        information.Message(username + ",欢迎回来", 1500);
                    }
                } else {
                    information.Message(data['rspInf'], 1500, "warning");
                }
            },
            error: function (xhr, err, e) {
                information.Message(err.toString(), 2000, "danger");
            },
            data: JSON.stringify({}),
            contentType: "application/json"
        });
    }

    //定向打开接口详情
    if(globalParam.toInterface && globalParam.toInterface>0){
        rendInterfaceDetail(globalParam.toInterface);
        $('#modal_interfaceDetail').modal('show')
    }

    // 设置接口信息框高度
    $("#modal_interfaceDetail_content").css({
        maxHeight:globalParam.screenSize.h * 0.85,
        overflow:"scroll"
    });

    //私有方法
    // 接口详情渲染方法
    function rendInterfaceDetail (ifId){
        // 获取接口基本信息
        $.ajax({
            url: urlHost + "queryInterFaceById.do",
            type: "post",
            success: function (data) {
                if (data['rspCd'] == "00000") {
                    $("#modal_interfaceDetail_title").html(template("tpl_modal_interfaceDetail_title",data));
                    $("#interfaceBaseInfo").html(template("tpl_interfaceBaseInfo",data));
                } else {
                    information.Message("详情接口[" + data['rspCd'] + "]" + data['rspInf'], 2000, "warning");
                }
            },
            data: JSON.stringify({"interFaceInfo":{"id":ifId}}),
            contentType: "application/json"
        });
        // 获取返回码信息
        $.ajax({
            url: urlHost + "getIFFields.do",
            type: "post",
            success: function (data) {
                if (data['rspCd'] == "00000") {
                    $("#returnCodeArea").html(template("tpl_returnCodeArea",data));
                } else {
                    information.Message("返回码查询接口[" + data['rspCd'] + "]" + data['rspInf'], 2000, "warning");
                }
            },
            data: JSON.stringify({"ifId":ifId,"fieldType":1}),
            contentType: "application/json"
        });
        // 获取请求参数
        $.ajax({
            url: urlHost + "getInterFaceFieldsJson.do",
            type: "post",
            success: function (data) {
                if (data['rspCd'] == "00000") {
                    $("#reqBody").html(template("tpl_reqBody",data));
                } else {
                    information.Message("请求参数获取接口[" + data['rspCd'] + "]" + data['rspInf'], 2000, "warning");
                }
            },
            data: JSON.stringify({
                "interFaceField": {
                    "fieldFlag": "1",
                    "ifId": ifId
                }
            }),
            contentType: "application/json"
        });

        // 获取返回参数
        $.ajax({
            url: urlHost + "getInterFaceFieldsJson.do",
            type: "post",
            success: function (data) {
                if (data['rspCd'] == "00000") {
                    $("#rspBody").html(template("tpl_rspBody",data));
                } else {
                    information.Message("返回参数获取接口[" + data['rspCd'] + "]" + data['rspInf'], 2000, "warning");
                }
            },
            data: JSON.stringify({
                "interFaceField": {
                    "fieldFlag": "2",
                    "ifId": ifId
                }
            }),
            contentType: "application/json"
        });
    }

})()