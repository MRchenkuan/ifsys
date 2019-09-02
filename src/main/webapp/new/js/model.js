// 初始化全局参数
var urlParam = {};
// 初始化查询条件参数,重设cookie
setCookie("PID",GetQueryString("PID")||getCookie("PID")||"0");
var ckProIdKey = getCookie("PID")+"-ifProId";
var ckSysIdKey = getCookie("PID")+"-ifSysId";
setCookie(ckProIdKey,GetQueryString("ifProId")||getCookie(ckProIdKey)||"0");
setCookie(ckSysIdKey,GetQueryString("ifSysId")||getCookie(ckSysIdKey)||"0");
setCookie("reqFieldsViewType",getCookie("reqFieldsViewType")||"CNT");
setCookie("rspFieldsViewType",getCookie("rspFieldsViewType")||"CNT");
var pageNumKey = getCookie(ckSysIdKey)+"-"+getCookie(ckProIdKey)+"-pageNum";
setCookie(pageNumKey,Math.max(1,GetQueryString("pageNum")||getCookie(pageNumKey)||1));

urlParam.pageNum = getCookie(pageNumKey)||1; // 分页号
urlParam.PID = getCookie("PID")||"0";// 项目号
urlParam.ifProId = getCookie(ckProIdKey)||"0";// 产品号
urlParam.ifSysId = getCookie(ckSysIdKey)||"0"; // 系统号
urlParam.ifId = GetQueryString("ifId");

// 批量添加接口框
var interfacesCreateModal = new Vue({
    el:"#interfacesCreateModal",
    data:{
        isShow:false,
        modal:$("#interfacesCreateModal")
    },
    methods:{
        show:function(){
            interfacesCreateModal.modal.modal("show")
        }
    }
});

// 系统产品添加框
var createSysAndProFrame = new Vue({
    el:"#createSysAndPro",
    data:{
        modal:$("#createSysAndPro"),
        sysName:"",
        isPro:false,
        sysId:0,
        proName:"",
        information:"",
        remark:"",
    },
    methods: {
        show: function () {
            createSysAndProFrame.modal.modal("show")
        },
        doAddSysPro: function () {
            var isPro = createSysAndProFrame.isPro;
            var proName = createSysAndProFrame.proName;
            var sysId = createSysAndProFrame.sysId;
            var remark = createSysAndProFrame.remark;
            var sysName = createSysAndProFrame.sysName;

            // 新增产品
            if (isPro) {
                if (sysId < 0) return createSysAndProFrame.information = "系统ID为空";
                if (!proName) return createSysAndProFrame.information = "请填写产品名";
                if (!remark) return createSysAndProFrame.information = "请填写产品描述";
                // 提示
                alertTool.confirm("是否在《" + sysName + "》下新增产品:" + proName, function () {
                    //,然后发送请求
                    api.post("addInterfaceSystem.do",{
                        pid:urlParam.PID,
                        sysId:sysId,
                        proName:proName,
                        remark:remark
                    },function (data) {
                        sysList.sysList = data["sysList"];
                        sysList.allProList = data["proList"];
                        createSysAndProFrame.modal.modal("hide")
                    },function (data) {
                        createSysAndProFrame.information = data['rspInf']
                    })
                }, function () {
                    return false;
                })
            }

            // 新增系统
            if (!isPro) {
                if (!sysName) return createSysAndProFrame.information = "请填写系统名";
                if (!remark) return createSysAndProFrame.information = "请填写系统描述";
                // 提示
                alertTool.confirm("是否在新增系统:" + sysName, function () {
                    //,然后发送请求
                    api.post("addInterfaceSystem.do",{
                        pid:urlParam.PID,
                        sysName:sysName,
                        remark:remark
                    },function (data) {
                        sysList.sysList = data["sysList"];
                        sysList.allProList = data["proList"];
                        createSysAndProFrame.modal.modal("hide")
                    },function (data) {
                        createSysAndProFrame.information = data['rspInf']
                    })
                }, function () {
                    return false;
                })
            }
        }
    }
});
// 系统切换对象
var sysList = new Vue({
    el: '#switchList',
    data: {
        defaultText: "切换系统",
        allProList:[],
        sysList: [],
        proList:[],
        proListStyle:"",
        sysId:urlParam.ifSysId,
        proId:urlParam.ifProId,
        canEditSys:false,
        memberRoles:[]
    },
    methods:{
        showProList:function(sysId){
            sysList.sysId = sysId;
        },
        createSys:function () {
            createSysAndProFrame.isPro = false;
            createSysAndProFrame.sysName = "";
            createSysAndProFrame.remark = "";
            createSysAndProFrame.information = "";
            createSysAndProFrame.show();
        },
        createPro:function (sysId) {
            createSysAndProFrame.isPro = true;
            var sysName = "";
            Array.prototype.some.call(sysList.sysList,function (it) {
                if(it.id==sysId){
                    sysName = it.sysName
                }
            });
            createSysAndProFrame.sysName = sysName;
            createSysAndProFrame.sysId = sysId;
            createSysAndProFrame.proName = "";
            createSysAndProFrame.remark = "";
            createSysAndProFrame.information = "";
            createSysAndProFrame.show()
        }
    }
});
// 遮罩框
var shader = new Vue({
    el:"#shader",
    data:{
        isShow:false,
        shader:$("#shader"),
        msg:""
    },
    methods:{
        show:function(msg){
            shader.msg="";
            if(msg)
            shader.msg = msg;
            shader.shader.modal("show")
        },
        hide:function(){
            shader.shader.modal("hide")
        }
    }
});
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
// 导航条对象
var nav = new Vue({
    el:"#nav",
    data:{
        btnCreateIf:{
            visible:true
        },
        editorController:{
            btnEdit:{
                visible:false
            },
            btnController:{
                visible:false
            },
            btnSwitchEdition:{
                visible:false
            }
        },
        saveBtnStatues:"",
        editions:[],
        ifObj:{
            id:"0",
            ifName:"",
            ifCreateTime:"加载中",
            ifVersionNo:"加载中"
        }
    },
    methods:{
        // 初始化编辑框
        initEditor:function(){
            interfaceDetail_container_editor.basicInfo={
                ifId:"",
                ifName:"",
                sysName:"加载中",
                proName:"加载中",
                ifType:"POST",
                ifProtocol:"HTTP",
                returnType:"JSON",
                ifVersionNo:"",
                isIdempotent:"N",
                ifUrl:"",
                ifSysId:sysList.sysId,
                ifProId:sysList.proId,
                ifDesc:""
            };
            interfaceDetail_container_editor.returnCodeList=[
                {id:"",rspCode:'00000',rspCodeDesc:'成功'},
                {id:"",rspCode:'30002',rspCodeDesc:'用户未登录'}
            ];
            // 构建关注者插件明细
            interfaceDetail_container_editor.setInvokers([]);

            interfaceDetail_container_editor.fieldList=[];
            interfaceDetail_container_editor.rspFields=[];
            interfaceDetail_container_editor.reqFields =[];
            interfaceDetail_container_editor.reqFields =[];
            interfaceDetail_container_editor.rspSummaryBak =[];
            interfaceDetail_container_editor.reqSummaryBak=[];
            interfaceDetail_container_editor.rspFieldIndex={};
            interfaceDetail_container_editor.reqFieldIndex={};
        },
        showInterfaceEdition:function(edition){
            console.log(edition)
            alertTool.alert("查看功能还未准备好")
        },
        createInterface:function(){
            showIfEditorBox();
            this.initEditor();
        },
        createInterfaces:function(){
            //alertTool.alert("批量添加功能还未准备好")
            interfacesCreateModal.show();
        },
        saveInterfaceChanges:function(isAdvance){
            // 校验输入
            var ifId = interfaceDetail_container_editor.basicInfo.ifId;
            var ifName = interfaceDetail_container_editor.basicInfo.ifName;
            isAdvance = ifId>0 && isAdvance=='true';
            var ifDesc = interfaceDetail_container_editor.basicInfo.ifDesc;
            var ifSysId = interfaceDetail_container_editor.basicInfo.ifSysId;
            var ifProId = interfaceDetail_container_editor.basicInfo.ifProId;
            var ifProtocol = interfaceDetail_container_editor.basicInfo.ifProtocol;
            var ifType = interfaceDetail_container_editor.basicInfo.ifType;
            var ifUrl = interfaceDetail_container_editor.basicInfo.ifUrl;
            var isIdempotent = interfaceDetail_container_editor.basicInfo.isIdempotent;
            var returnType = interfaceDetail_container_editor.basicInfo.returnType;
            var invokerList = interfaceDetail_container_editor.freeSearch.getAllSelections();

            var returnCodeList = interfaceDetail_container_editor.returnCodeList;
            var reqFieldsList = interfaceDetail_container_editor.reqFields;
            var rspFieldsList = interfaceDetail_container_editor.rspFields;
            if(!ifName)return alertTool.alert("请填写接口名");
            if(!ifUrl)return alertTool.alert("请填写接口路径");
            if(!ifSysId ||ifSysId<=0)return alertTool.alert("所属系统未选择");
            if(!ifProId ||ifProId<=0)return alertTool.alert("所属产品未选择");
            if(!ifProtocol)return alertTool.alert("接口协议未选择");
            if(!ifType)return alertTool.alert("请求方式未选择");
            if(!isIdempotent)return alertTool.alert("请选择接口幂等性");
            if(!returnType)return alertTool.alert("请选择返回值类型");
            var msg="";
            if(ifId){
                msg = (isAdvance?"确认修改并推进接口:":"确认修改接口:")+"\n\t"+ifName+"?";
            }else{
                msg = "新增接口:\n\t"+ifName+"?";
            }

            alertTool.confirm(msg,function(){
                nav.saveBtnStatues = "disabled";
                saveInterface();
            });


            // 保存接口流程
            function saveInterface(){
                // 提交
                var ifDetail = {
                    ifId:ifId*1,
                    interFaceInfo:{
                        ifName:ifName,
                        ifDesc:ifDesc,
                        ifSysId:ifSysId,
                        ifProId:ifProId,
                        ifProtocol:ifProtocol,
                        ifType:ifType,
                        ifUrl:ifUrl,
                        isIdempotent:isIdempotent,
                        returnType:returnType
                    },
                    returnCodes:returnCodeList,
                    reqFields:reqFieldsList,
                    rspFields:rspFieldsList,
                    invokerList:invokerList,
                    advance:isAdvance
                };
                // 加入蒙版
                shader.show("正在保存");
                api.post("saveInterfaceDetail.do",ifDetail,function(data){
                    var detail = data["detail"];
                    nav.saveBtnStatues="";// 保存按钮状态复原
                    // 加入蒙版
                    shader.hide();
                    // 做个假的
                    if(!ifId)
                    interfaceList.pageInfo.list.unshift({
                        id:detail.ifId,
                        ifName:ifName,
                        isIdempotent:isIdempotent
                    });
                    //补充新增效果
                    alertTool.alert("保存成功:-D",function(){
                        //showIfDetailBox();
                        // todo 请求新增的接口并展示
                    })
                })
            }

        },
        returnOutOfEditor:function(){
            alertTool.confirm("正在编辑接口,是否退出?",function(){showIfDetailBox()},function(){return false})
        }
    }
});

// 面包屑导航
var nav_path = new Vue({
    el: '#nav_path',
    data: {
        sysName:"",
        proName:"",
        sysId:0,
        proId:0
    }
});
// feed对象
var indexPage = new Vue({
    el:"#indexPage",
    data:{
        visible:true,
        feedList:[]
    }
});
// 接口列表对象
var interfaceList = new Vue({
    el:"#interfaceList",
    data:{
        pageInfo:{},
        currentId:""
    },
    methods:{
        getCurrentSelected:function(){
            return nav.ifObj
        },
        showInterfaceDetail:function(ifObj){
            indexPage.visible = false;// 隐藏首页
            // 确认是否退出编辑界面
            if(interfaceDetail_container_editor.visible && nav.ifObj){
                alertTool.confirm("正在编辑接口,是否退出?",function(){
                    showIfDetailBox();
                    openInterfaceDetail();
                },function(){
                    return false
                });
            }else{
                openInterfaceDetail();
            }

            //打开接口详情方法
            function openInterfaceDetail(){
                // 传递当前操作的接口到导航条,并设置点击样式
                nav.ifObj = ifObj;interfaceList.currentId = ifObj.id;
                // 改变url地址
                history.pushState({},"","?ifId="+ifObj.id);
                // 清空原有信息
                interfaceDetail_container.basicInfo = {};
                interfaceDetail_container.reqFieldList = [];
                interfaceDetail_container.rspFieldList = [];

                // 展示接口细节
                showIfDetailBox();
                var ifId = ifObj.id;
                //var ifName = ifObj.ifName;
                // 接口基本信息
                api.post("queryInterFaceById.do", {"interFaceInfo": {"id": ifId}}, function (data) {
                    var interFaceInfo = data['interFaceInfo'];
                    var pro = data['pro'];
                    var system = data['system'];
                    var fieldList = data['fieldList'];
                    var follow = data['follow'];
                    var userInfo = data['userInfo'];
                    var invokers = data['invokers'];
                    var ifName = data['interFaceInfo'].ifName;

                    // 基本信息
                    interfaceDetail_container.basicInfo = {
                        id:ifId,
                        ifId: ifId,
                        ifName: ifName,
                        designer: interFaceInfo.designName,
                        sysName: interFaceInfo.sysName,
                        proName: interFaceInfo.proName,
                        ifType: interFaceInfo.ifType,
                        ifProtocol: interFaceInfo.ifProtocol,
                        returnType: interFaceInfo.returnType,
                        ifVersionNo: interFaceInfo.ifVersionNo,
                        isIdempotent: interFaceInfo.isIdempotent,
                        ifUrl: interFaceInfo.ifUrl,
                        ifProId: interFaceInfo.ifProId,
                        ifSysId: interFaceInfo.ifSysId,
                        ifDesc:interFaceInfo.ifDesc,
                        ifCreateTime:tmStampToTime(interFaceInfo.ifCreateTime).substr(0,10)
                    };
                    // 设置顶部信息
                    nav.ifObj = interfaceDetail_container.basicInfo;
                    interfaceDetail_container.pro = pro;
                    interfaceDetail_container.sys = system;
                    interfaceDetail_container.invokers = invokers;

                });

                // 返回字段/返回码信息
                api.post("getInterfaceDetail.do", {"ifId":ifId}, function (data) {
                    var detail = data['detail'];
                    // 返回码显示
                    interfaceDetail_container.returnCodeList = detail['rspCds'];
                    // 字符串参数显示
                    interfaceDetail_container.reqBody = detail['reqJsonStr'];
                    interfaceDetail_container.rspBody = detail['rspJsonStr'];
                    // 渲染参数列表 - 设置纯json字符串到展示框
                    var reqList= detail['reqFields'];
                    var rspList= detail['rspFields'];

                    interfaceDetail_container_editor.fieldsList = reqList + rspList;
                    interfaceDetail_container.rspFields = detail['rspFields'];
                    interfaceDetail_container.reqFields = detail['reqFields'];

                    //console.log("rspJson",rspJson);
                    // 渲染参数列表 - parent
                    transformFieldListParent(reqList);
                    transformFieldListParent(rspList);

                    // 提前渲染编辑框
                    interfaceDetail_container_editor.rspFields = rspList;
                    interfaceDetail_container_editor.reqFields = reqList;
                    interfaceDetail_container_editor.reRenderReqFields();
                    interfaceDetail_container_editor.reRenderRspFields();
                    // 渲染参数列表 - jsonstr
                    interfaceDetail_container.rspBodyPure = interfaceDetail_container_editor.stringifyFields(2);
                    interfaceDetail_container.reqBodyPure = interfaceDetail_container_editor.stringifyFields(1);
                    // 存到 contaier
                    interfaceDetail_container.reqFieldList = reqList;
                    interfaceDetail_container.rspFieldList = rspList;
                });
                // 历史返回码信息
                autotest_api.post("getHistoricalReturnCode.do", {"ifId":ifId}, function (data) {
                    interfaceDetail_container.returnCodeHistories = data['dataes'];
                    interfaceDetail_container.returnCodeHistories.forEach(function(it){
                        it.TS = tmStampToTime(it.TS)
                    })
                });

                // 变更记录
                api.post("getInterfaceChanges.do", {"ifId":ifId}, function (data) {
                    interfaceDetail_container.changeLogs = data['feedsList'];
                });
                // 接口版本列表展示
                api.post("getEditionsByIfId.do",{ifId:ifId},function(data){
                    nav.editions = data['editions']||[];
                })
            }
        }
    }
});

var interfaceDetail_container = new Vue({
    el:"#interfaceDetail_container",
    data:{
        visible:true,
        basicInfo:{
            ifId:"",
            ifName:"加载中",
            designer:"加载中",
            sysName:"加载中",
            proName:"加载中",
            ifType:"加载中",
            ifProtocol:"加载中",
            returnType:"加载中",
            ifVersionNo:"加载中",
            isIdempotent:"加载中",
            ifUrl:"加载中",
            ifProId:0,
            ifSysId:0,
            ifDesc:"加载中",
            ifCreateTime:"未知"
        },
        returnCodeList:[],
        returnCodeHistories:[],
        returnCodeDisplay:"DEF",
        reqBody:"加载中",
        rspBody:"加载中",
        reqBodyPure:"加载中",
        rspBodyPure:"加载中",
        reqFieldList:[],// 用于参数列表第二种视图
        rspFieldList:[],// 用于参数列表第二种视图
        fieldsList:[],
        changeLogs:[],
        changeLogsShow:false,
        invokeListShow:false,
        mockLinkShow:false,
        realLinkShow:false,
        pro:{},
        system:{},
        invokers:[],
        uiInfo:{
            reqFieldsViewType:getCookie("reqFieldsViewType"),// CND CNT TWD
            rspFieldsViewType:getCookie("rspFieldsViewType")// CND CNT TWD
        }
    },
    methods:{
        typeDic:function(index){
            var data = {
                1:"STRING",
                2:"NUMBER",
                3:"OBJ",
                4:"LIST[OBJ]",
                5:"BOOL",
                6:"NULL",
                7:"ARRAY"
            };
            //var data = {
            //    1:"字符串",
            //    2:"数字",
            //    3:"对象",
            //    4:"对象列表",
            //    5:"布尔值",
            //    6:"null",
            //    7:"数组"
            //};
            return data[index]
        },
        deleteInterface:function(){
            var ifId = interfaceDetail_container.basicInfo.ifId;
            var ifName = interfaceDetail_container.basicInfo.ifName;
            if(!ifId)return;
            alertTool.confirm("正在删除接口:["+ifId+"]"+ifName+"?",function(){
                shader.show("正在删除接口:["+ifId+"]"+ifName+" ");
                api.post("deleteInterFaceById.do",{
                    "interFaceInfo": {
                        "id": ifId
                    }
                },function(data){
                    shader.hide();
                    location.href = "./main.html"
                });
            },function(){
                return false;
            })
        },

        editInterfaceDetail : function(){
            // 编辑框重新取值,因为会导致绑定
            // 初始化编辑框
            showIfEditorBox();
            nav.initEditor();

            // 渲染基本信息
            interfaceDetail_container_editor.basicInfo = deepCopyObject(interfaceDetail_container.basicInfo);

            // 渲染返回码明细
            interfaceDetail_container_editor.returnCodeList = deepCopyArrayObject(interfaceDetail_container.returnCodeList);
            interfaceDetail_container_editor.returnCodeListBak = deepCopyArrayObject(interfaceDetail_container.returnCodeList);

            // 构建关注者插件明细
            interfaceDetail_container_editor.setInvokers(interfaceDetail_container.invokers);

            // 渲染参数列表 - 获取字段明细
            var reqFieldList = deepCopyArrayObject(interfaceDetail_container.reqFields);
            var rspFieldList = deepCopyArrayObject(interfaceDetail_container.rspFields);
            // 渲染参数列表 - 渲染parent
            transformFieldListParent(reqFieldList);
            transformFieldListParent(rspFieldList);
            interfaceDetail_container_editor.reqFields = reqFieldList;
            interfaceDetail_container_editor.rspFields = rspFieldList;
            // 渲染参数列表 - 备份修改之前的摘要
            interfaceDetail_container_editor.reqSummaryBakUp();
            interfaceDetail_container_editor.rspSummaryBakUp();
            // 渲染参数列表 - 重新渲染节点
            interfaceDetail_container_editor.reRenderReqFields();
            interfaceDetail_container_editor.reRenderRspFields();
        },
        switchView : function(fieldFlag,type){
            console.log(fieldFlag,type)
            if(fieldFlag==1){
                interfaceDetail_container.uiInfo.reqFieldsViewType=type;
                setCookie("reqFieldsViewType",type||'CNT');
            }
            if(fieldFlag==2){
                interfaceDetail_container.uiInfo.rspFieldsViewType=type;
                setCookie("rspFieldsViewType",type||'CNT');
            }
        },
        toggleLinkShow:function(type){
            if(type=="real")$("#realUrl").toggle();
            if(type=="mock")$("#debugUrl").toggle();
        },
        toggleChangeLog:function(){
            interfaceDetail_container.changeLogsShow = !interfaceDetail_container.changeLogsShow
        },
        toggleInvokeList:function(){
            interfaceDetail_container.invokeListShow = !interfaceDetail_container.invokeListShow
        },
        toggleReturnCodeDisplay:function(symbol){
            if(symbol=="DEF"){}
            if(symbol=="HIS"){}
            interfaceDetail_container.returnCodeDisplay = symbol

        }
    }
});
// 侧边导航
var right_side_nav = new Vue({
    el:"#right-side-nav",
    data:{
        current:"basicInfo",
        visible:true
    }
});
// 接口编辑框
var interfaceDetail_container_editor = new Vue({
    el:"#interfaceDetail_container_editor",
    data:{
        visible:false,
        basicInfo:{
            ifId:"",
            ifName:"测试测试",
            sysName:"加载中",
            proName:"加载中",
            ifType:"POST",
            ifProtocol:"HTTP",
            returnType:"JSON",
            ifVersionNo:"",
            isIdempotent:"加载中",
            ifUrl:"",
            ifSysId:1,
            ifProId:1,
            ifDesc:"加载中"
        },
        proList:[],
        sysList:[],
        returnCodeList:[
            {id:"",rspCode:'00000',rspCodeDesc:'成功'},
            {id:"",rspCode:'30002',rspCodeDesc:'用户未登录'}
        ],
        fieldList:[],
        rspFields:[],
        reqFields :[],
        returnCodeListBak:[], // 返回码备份
        rspSummaryBak : [],// 参数摘要备份
        reqSummaryBak:[],// 参数摘要备份
        rspFieldIndex : {},// 返回参数字段路径映射关系
        reqFieldIndex : {},// 请求参数字段路径映射关系
        colors : ["#b33c39","#6a6fff","#53bb57","#ff1cdc","#5ab4a6","#ada662"],
        freeSearch:{},
        // 返回码列表Json化
        stringifyRspCdList : function(){
            var obj = {};
            this.returnCodeList.some(function(it){
                obj[it.rspCode] = it.rspCodeDesc;
            });
            return JSON.stringify(obj,null,3);
        },
        // 参数列表JSON化
        stringifyFields : function(fieldFlag) {
            // 当前所有参数列表
            var nowList;
            if(fieldFlag=="1")nowList = this.reqFields;
            if(fieldFlag=="2")nowList = this.rspFields;
            if(!nowList){
                // 当前字段列表不存在时的逻辑
                return "/*不存在list*/{}";
            }
            var value = {};
            // 1.由于参数列表可能发生修改,json的组织,依赖于parent,需要重新组织parent
            reorganizeParent(nowList);
            // 2.组织json
            relatedList2JSON(value,nowList,nowList);
            return JSON.stringify(value,null,3)
        }
    },
    methods:{
        // 包装,并重设返回参数列表
        reRenderRspFields:function(){
            var rspFields = interfaceDetail_container_editor.rspFields;
            renderLevelOfFields(rspFields,2);
            rspFields.forEach(function(it){
                if(it.hasOwnProperty("_level")) {
                    // 确认缩进
                    var indent = it._level.split("-").length-2;
                    it._indent = indent;
                    // 确认颜色
                    it._color = interfaceDetail_container_editor.colors[indent];
                }else{
                    console.log("渲染失败,渲染前,请确认field对象已经加入了level")
                }
            });
            // 触发重绘
            interfaceDetail_container_editor.rspFields = rspFields;
        },
        // 包装,并重设请求参数列表
        reRenderReqFields:function(){
            var reqFields = interfaceDetail_container_editor.reqFields;
            renderLevelOfFields(reqFields,1);
            reqFields.forEach(function(it){
                if(it.hasOwnProperty("_level")) {
                    // 确认缩进
                    var indent = it._level.split("-").length-2;
                    it._indent = indent;
                    // 确认颜色
                    it._color = interfaceDetail_container_editor.colors[indent];
                }else{
                    console.log("渲染失败,渲染前,请确认field对象已经加入了level")
                }
            });
            // 触发重绘
            interfaceDetail_container_editor.reqFields = reqFields;
        },
        // 备份请求数据;
        reqSummaryBakUp : function(){
            //// 不重复备份
            //if(interfaceDetail_container_editor.reqSummaryBak.length>0)return;
            console.log("备份请求数据")
            interfaceDetail_container_editor.reqSummaryBak = [];
            interfaceDetail_container_editor.reqFields.forEach(function(item){
                var obj = {
                    "id":item.id,
                    "parent":item.parent,
                    "k":item.k,
                    "note":item.note
                };
                interfaceDetail_container_editor.reqSummaryBak.push(obj)
            })
        },
        // 备份响应数据;
        rspSummaryBakUp : function(){
            //// 不重复备份
            //if(interfaceDetail_container_editor.rspSummaryBak.length>0)return;
            console.log("备份响应数据")
            interfaceDetail_container_editor.rspSummaryBak = [];
            interfaceDetail_container_editor.rspFields.forEach(function(item){
                var obj = {
                    "id":item.id,
                    "parent":item.parent,
                    "k":item.k,
                    "req":item.req,
                    "note":item.note
                }
                interfaceDetail_container_editor.rspSummaryBak.push(obj)
            })
        },
        // 返回码删除
        deleteCode:function(index){
            var nowList = this.returnCodeList;
            nowList.$remove(nowList[index]);
        },
        // 新增一条空返回码
        addNewRspCd : function(){
            var nowList = this.returnCodeList;
            var newObj= {
                id:"",
                ifId:interfaceDetail_container_editor.basicInfo.ifId,
                rspCode:'',
                rspCodeDesc:''};
            var editingCode = {};
            var ifexist = nowList.some(function(it){
                 if(it.rspCode == newObj.rspCode || it.rspCodeDesc == newObj.rspCodeDesc){
                     editingCode = it;
                     return true;
                 }else{
                     return false;
                 }
            });
            if(!ifexist){
                this.returnCodeList.unshift(newObj);
            }else{
                return alertTool.alert("请完善编辑中的返回码:\n\trspCd["+editingCode.rspCode+"]"+" rspInf["+editingCode.rspCodeDesc+"]")
            }
        },
        /**
         * 新增子节点
         * @param fieldFlag
         * @param field
         * @param fieldFlag
         */
        addNewChildField :function(field,fieldFlag) {
            // 获取当前节点level
            var fieldLevel = field._level;
            addChildFieldByLevel(fieldLevel,fieldFlag)

        },
        // 新增同级节点
        addNewSiblingField:function(field,fieldFlag){

            var currentLevel = field._level;
            var currentLevelArr = currentLevel.split("-");
            currentLevelArr.pop();
            var parentLevel = currentLevelArr.join("-");
            addChildFieldByLevel(parentLevel,fieldFlag)
        },
        // 新增根节点
        addRootField:function(fieldFlag){
           addChildFieldByLevel("0",fieldFlag);
        },
        // 字段删除方法
        deleteFields : function(field,fieldFlag){
            var parentLevel = field._level;
            console.log("parentLevel",parentLevel);
            var fields;
            if(fieldFlag ==1){
                fields = interfaceDetail_container_editor.reqFields;
            }
            if(fieldFlag ==2){
                fields = interfaceDetail_container_editor.rspFields;
            }
            if(field){
                // 删除本身和子节点
                // 逆序遍历的原因在于
                // 由于js的原因,正序遍历会导致索引变化使遍历中断
                for(var i=fields.length;i>0 ; i--){
                    var it = fields[i-1];
                    var currentLevel = it._level;
                    if(currentLevel.indexOf(parentLevel) == 0){
                        fields.$remove(it); // 索引变了
                    }else{
                    }
                }

            }
        },
        // 校验节点输入项
        checkField:function(field,fieldFlag){
            //if(!field.k){
            //    alert("当前节点为空值")
            //}
        },
        // 校验当前类型参数是否存在重复
        fieldsRepeatCheck : function(feildFlag){
            // 当前列表
            var nowList;
            if(feildFlag=="1")nowList = this.reqFields;
            if(feildFlag=="2")nowList = this.rspFields;
            if(!nowList){
                // 当前字段列表不存在时的逻辑
                // todo 弹框等处理
                return false;
            }
            // 比较行数
            var operLine = 0;
            // 重复对象
            var repeatItem;
            // 参照行数
            var comparedLine=0;
            var ifRepeat = nowList.some(function(operateItem,id){
                // 当前被比较的行数自增
                operLine++;
                //重复次数 [1,1<<]
                var repeatCount =0;
                // 参照行数
                var _comparedLine=0;
                // 重复次数该是1 ,发现相同则应该大于1
                return nowList.some(function (compareItem) {
                    // 当前参照的行数自增
                    _comparedLine++;
                    // 转存
                    comparedLine = _comparedLine;
                    // 比较二元是否一致
                    if (isSameLevel(operateItem._level, compareItem._level) && operateItem.k == compareItem.k)
                        repeatCount++;

                    // 发现发现重复则
                    if(repeatCount > 1){
                        // 确定重复对象
                        repeatItem = compareItem;
                        return true;
                    }else{
                        return false;
                    }
                });
            });
            return ifRepeat?{
                compareLine:operLine,
                comparedLine:comparedLine,
                repeatItem:repeatItem
            }:false;
        },
        /**
         * 设置接口初始关注者
         * @param invokers
         */
        setInvokers: function(invokers){
            var freeSearch = interfaceDetail_container_editor.freeSearch;
            if(!freeSearch){
                console.log("freeSearch:用户列表不存在时重新获取");
                // 当接口列表不存在时重新获取
                api.post("getAvlUserList.do",{},function(data){
                    interfaceDetail_container_editor.freeSearch = $("#editor_invokers_textarea").searchBox(data["userInfos"])
                });
                freeSearch = interfaceDetail_container_editor.freeSearch;
            }
            freeSearch.cleanAllItems();
            var _invokers=[];
            invokers.forEach(function(it){_invokers.push(it.uId)})
            freeSearch.setInitItems(_invokers);
        }
    }
});
// 弹出框控件
var editor_modal_box = new Vue({
    el:"#editor_modal_box",
    data:{
        title:"导入返回码",
        type:"RSP_CODE",
        jsonStr:"",
        jsonObj:null,
        isJsonValid:"unchecked",// true false unchecked
        JSONCheck:function($display,$editor){
            var self = this;
            var jsonObj;
            try{
                jsonObj=eval("("+self.jsonStr+")");
                $display.JSONView(jsonObj).show();
                $editor.hide();
                editor_modal_box.isJsonValid = "true";
            }catch (e){
                $display.text(self.jsonStr).show();
                $editor.hide();
                console.log("json格式化失败");
                editor_modal_box.isJsonValid="false";
            }
            editor_modal_box.jsonObj = jsonObj;
        },
         fillJSONAsItems:function(){
            if(editor_modal_box.jsonObj){
                //todo 实现导入
                switch (editor_modal_box.type){
                    case "RSP_CODE":
                        try{
                            var existCode = interfaceDetail_container_editor.returnCodeList;
                            var JSONObj = eval("("+editor_modal_box.jsonStr+")");
                            var nowCode = [];
                            for(var codekey in JSONObj){
                                if(JSONObj.hasOwnProperty(codekey)){
                                    var code = {
                                        id:"",
                                        ifId : interfaceDetail_container_editor.basicInfo.ifId,
                                        rspCode:codekey,
                                        rspCodeDesc:JSONObj[codekey]
                                    };
                                    nowCode.push(code);
                                }
                            }

                            nowCode.forEach(function(now){
                                var now_code = now.rspCode;
                                var isExist = existCode.some(function(prm){
                                    var prm_code = prm.rspCode;
                                    var prm_id = prm.id;
                                    var prm_ifId = prm.ifId;
                                    // 发现已有,则将prm更新
                                    if(now_code == prm_code){
                                        now.id = prm_id;
                                        now.ifId = prm_ifId;
                                        return true;
                                    }else {
                                        return false;
                                    }
                                });
                                // 进一步判断是否与初值相同
                                if(!isExist){
                                    interfaceDetail_container_editor.returnCodeListBak.forEach(function(bak){
                                        if(now_code == bak.rspCode){
                                            now.id = bak.id;
                                            now.ifId = bak.ifId;
                                        }
                                    })
                                }
                            });
                            interfaceDetail_container_editor.returnCodeList = nowCode;
                        }catch(e){
                            console.log(e)
                        }
                        break;
                    case "REQ_JSON":
                        modalBoxJsonImport(1)
                        break;
                    case "RSP_JSON":
                        modalBoxJsonImport(2)
                        break;
                    default:
                        return alertTool.alert("导入出错,无法区分导入目标");
                        break;
                }
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

// 取url参数
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = location.search.substr(1).match(reg);
    if(r!=null)return  decodeURI(r[2]); return null;
}

/**
 * 将关系型List转成Json对象, JSON2relatedList 互为逆函数
 * 算法为:
 * 1.将当前节点点优先设置
 * 2.将剩余节点重复1,若节点数减少到0,则3,否则报错
 * 3.返回对象
 * @param obj 空对象,输出值
 * @param fieldList 递归列表,初始值为全量原始列表,dynamic
 * @param _fieldList 全量原始列表 fixed
 */
function relatedList2JSON(obj,fieldList,_fieldList){
    var listLength = fieldList.length;
    var literateItems = [];
    var _literateItems = [];
    fieldList.forEach(function(it,id,ar){

        // 若本身就是根节点
        if(it.parent == ""){
            // 直接赋值
            obj[it.k] = getMockObj(it.mock);
        }else{
            // 剩余节点
            literateItems.push(it);
        }

        // 获取关联父节点的
        literateItems.forEach(function(it){
            var paths = it.parent.split(".");
            var _obj = obj;
            // 确认父节点的类型
            var parentType = getParentTypeByPath(_fieldList,it.parent);
            // 根据path
            // 逐级寻找父节点的引用
            while (paths.length>0) {
                var key = paths.shift();// 父节点
                if (!(_obj[key] instanceof Object)) {
                    // 找到但不为对象的时候,直接初始化为对象
                    if (parentType == 4) { // ArrayObject
                        _obj[key] = [{}];
                    } else {// Object
                        _obj[key] = {};
                    }
                }
                if (_obj[key] instanceof Array) { // ArrayObject
                    _obj = _obj[key][0]
                } else {// Object
                    _obj = _obj[key]
                }

            }

            // 找到引用直接赋值
            if(_obj instanceof Array){ // ArrayObject
                _obj[0][it.k] = getMockObj(it.mock);
            }else{ // Object
                _obj[it.k] = getMockObj(it.mock);
            }
        });

        // 判断是否发生了无限递归
        if(_literateItems.length > listLength){
            return false;
        }

        // 最终余下的
        if(_literateItems.length>0){
            relatedList2JSON(obj,_literateItems,_fieldList)
        }

    });
}

/**
 * 将对象化的Json 转成关系型LIST 与 relatedList2JSON 互为逆函数
 * @param JSONObj
 * { parent:"", k:"", mock:"列表", type:1, _level : "1-1" }
 */
function JSON2relatedList(JSONObj){
    var colors = interfaceDetail_container_editor.colors
    var list = [];
    var typeList = {
        arrayObject:4,
        object:3,
        string:1,
        number:2,
        boolean:5,
        nul:6,
        array:7
    };
    return getList("",list,JSONObj,0);
    /**
     * 根据对象化的Json 递归获取 列表化的json
     * @param parent 进入时的统一parent
     * @param list
     * @param JSONObj
     * @param level 用于描述层级关系
     * @return 数据结构
     */
    function getList(parent , list,JSONObj , level){
        var seed= 1;//每个元素的内部ID

        // 若当前待遍历对象是字符串则直接退出
        if(typeof JSONObj == "string") return list;

        for(var k in JSONObj){
            if(!k)continue; // 防止k值为空的情况
            // 确定层级
            var _level = level +"-"+(seed++);
            var indent = _level.split("-").length-2;
            var note ="";
            var v = JSONObj[k];
            var field;
            var type = 1;
            // 下一层的parent
            var nextParent = parent==""?k:(parent + "." + k);
            // 判断当前值类型
            if(v instanceof Array){
                // 确定层级
                // 数组
                // 判断数组类型
                type = typeof v[0] == "object" ? typeList.arrayObject : typeList.array;
                field = {
                    parent:parent,
                    k:k,
                    mock:(type==typeList.array?"['a','b','c']":"[{...},{...},{...}]"),
                    type:type,
                    note:note,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                console.log(field)
                list.push(field);
                getList(nextParent,list,v[0],_level);
            }else if(v instanceof Object){
                // 对象
                field = {
                    parent:parent,
                    k:k,
                    mock:"对象",
                    note:note,
                    type:typeList.object,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                list.push(field);
                getList(nextParent,list,v,_level);
            }else{
                // 其他值
                var mock="";
                if(typeof v == "string"){// 字符串
                    type = typeList.string;
                    mock = v;
                }
                if(typeof v == "number"){ // 数字
                    type = typeList.number;
                    mock = v*1;
                }
                if(typeof v == "boolean"){// 布尔值
                    type = typeList.boolean;
                    mock = v.toString();
                }
                if( v === null){ // 空值
                    type = typeList.nul;
                    mock="null";
                }
                // 常量
                field = {
                    parent:parent,
                    k:k,
                    note:note,
                    mock:mock,
                    type:type,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                list.push(field);
            }
        }
        return list
    }
}


// mock数据对象化
function getMockObj(str){
    try{
        var mock=JSON.parse(str);
        if(mock instanceof Object){
            mock = mock.toString();
        }
        return mock;
    }catch (e){
        return str;
    }
}
// 根据父节点path获取父节点类型
function getParentTypeByPath(list,str){
    var keyMap = {};
    // 每个listItem的全路径
    list.forEach(function(it){
        if(it.parent){
            keyMap[it.parent+"."+it.k] = it;
        }else{
            keyMap[it.k] = it;
        }
    });
    try{
        return keyMap[str].type;
    }catch (e){
        console.log(str);
    }
}

/**
 * 已知parent的前提下
 * todo 出现只能遍历第一层的BUG,需要关注
 * 包装field 加入level ,
 * 同时维护层级路径列表{"0-1-1-1-1":{length:,keypath:}}
 * @param fieldList
 * @param fieldFlag
 */
function renderLevelOfFields(fieldList,fieldFlag){

    // 保存层级关系对象
    if(fieldFlag == 1) {
        // 包装 level
        interfaceDetail_container_editor.reqFieldIndex={}
        wrap(fieldList, interfaceDetail_container_editor.reqFieldIndex);
    }

    if(fieldFlag == 2){
        // 包装 level
        interfaceDetail_container_editor.rspFieldIndex={}
        wrap(fieldList, interfaceDetail_container_editor.rspFieldIndex);
    }
    // 排序
    fieldList.sort(function(x,y){
        if(x._level > y._level)return 1;
        if(x._level == y._level)return 0;
        if(x._level < y._level)return -1;
    });
    /**
     *
     * @param fieldList
     * @param parentLevelMap 父节点的层级 列表
     * @param level 当前层级
     */
    function wrap(fieldList,parentLevelMap){
        if(fieldList.length<=0)return false
        var literateItems = []
        fieldList.forEach(function(field){
            if(!field.hasOwnProperty("_level")){
                // 节点当前k和父k
                var keyPath,parent = field.parent;
                if(parent ==""){
                    keyPath = field.k;
                }else{
                    keyPath = parent +"."+field.k;
                }

                // 初始化字典
                if(!parentLevelMap.hasOwnProperty("")){
                    parentLevelMap[""] = {length:0, level:"0"}
                }

                if(parentLevelMap.hasOwnProperty(parent)){
                    // 维护根节点高度
                    parentLevelMap[parent].length++;
                    // 维护下级节点字典
                    parentLevelMap[keyPath] = {
                        length:0,
                        level:parentLevelMap[parent].level + "-" +parentLevelMap[parent].length
                    };
                    // 设置当前节点level
                    field._level = parentLevelMap[parent].level +"-"+parentLevelMap[parent].length;
                }else{
                    literateItems.push(field);
                }
            }
        });

        if(fieldList.length<=literateItems.length){
            // 无限递归 退出
            console.log("如下节点找不到父节点,渲染可能有问题");
            console.log(fieldList)
            return false;
        }

        if(literateItems.length > 0){ // todo 很有可能是此行判断有问题,导致无法进行递归
            wrap(literateItems , parentLevelMap)
        }
    }
}

/**
 * 计算当前层级关系下,子节点应该的取值
 * 如:
 * 0-1-1-1 下的节点
 * 0-1-1-1-10
 * 0-1-1-1-55
 * 最新的子节点取值应该是
 * 0-1-1-1-56
 * 具体算法为:
 * 1.找到与正则 0-1-1-1-\d* 相匹配的元素
 * 2.取出最后一个数字,拼到数组
 * 3.Math.max + 1
 * @param level
 * @param fieldList
 * @returns {number}
 */
function getChildIndexByLevel(level,fieldList){
    var childrenIndexes = [];
    var  reg = new RegExp("^"+level+'-(\\d+)$','gi');
    fieldList.forEach(function(field){
        reg.lastIndex = 0;// 重置游标
        var $level = field._level;
        var result = reg.exec($level);
        if(result && result[1]){
            childrenIndexes.push(result[1])
        }
    });
    var maxIndex = 0;
    if(childrenIndexes.length>0){
        maxIndex = Math.max.apply( Math, childrenIndexes );
    }

    return maxIndex + 1;
}

/**
 * 比较两个层级是否在同一层
 * 比如处于同一层:
 * 0-1-1-1-5
 * 0-1-1-1-50
 * 不同一层
 * 0-1-1-1-5
 * 0-1-1-2-5
 * 具体算法:
 * 1.分割两个路径到数组
 * 2.出栈尾部序号
 * 3.比较前缀序列是否相等
 * @param level
 * @param compareLevel
 */
function isSameLevel(level,compareLevel){
    var arrLevel = level.split("-");
    var arrCompareLevel = compareLevel.split("-")

    arrLevel.pop();
    arrCompareLevel.pop();

    return arrLevel.join("-") == arrCompareLevel.join("-");

}

/**
 * 已知level的前提下
 * 参数列表重组paren方法
 * 具体算法:
 *
 * 根据部分parent ,组织其他的parent
 * @param fieldList
 */
function reorganizeParent(fieldList){

    var completedList = [];
    // 清空已有的parent
    fieldList.forEach(function(field) {
        field.parent = "";
    });

    reorg(fieldList);
    //fieldList = completedList;

    function reorg(fieldList){
        if(fieldList.length<=0)return;
        // 剩余列表
        var leftList = [];
        fieldList.forEach(function(field){
            var current_level = field._level; // 0-*
            var parent_level = getParentLevelString(current_level);// 0
            // 老规矩,确认根节点名
            if(parent_level == "0"){
                field.parent = ""
                completedList.push(field);
            }else{
                leftList.push(field);
            }
        });

        // 剩余节点根据parent_level来进行递归
        leftList.forEach(function(it){
            var parent_level = getParentLevelString( it._level); //0-*
            // 剩余节点与已存节点比较
            var isExist = completedList.some(function(existIt){
                if(parent_level == existIt._level){
                    it.parent = (existIt.parent=="")?existIt.k:existIt.parent+"."+existIt.k;
                    completedList.push(it)
                    return true
                }else{
                    return false;
                }
            });

            // 当前节点在已有的节点中,找不到父节点,
            // 则放到下一轮迭代中
            if(!isExist){
                leftList.push(it);
            }

        });

        // 无限递归判断
        if(fieldList.length <= leftList.length){
            console.log("reorganizeParent 无限递归");
            console.log(leftList)
            return false;
        }

        if(leftList>0) {
            reorg(leftList)
        }
    }

}

/**
 * 获取当前level的父级
 * @param currentLevel
 * @returns {string}
 */
function getParentLevelString(currentLevel){
    var arr = currentLevel.split("-");
    arr.pop();
    return arr.join("-")
};


/**
 * 根据当前层级新增子节点
 * @param fieldLevel
 * @param fieldFlag
 */
function addChildFieldByLevel(fieldLevel,fieldFlag){
    // 获取映射关系表
    var fieldList;
    if (fieldFlag == "1") {
        interfaceDetail_container_editor.reRenderReqFields();
        fieldList = interfaceDetail_container_editor.reqFields;
    }
    if (fieldFlag == "2") {
        interfaceDetail_container_editor.reRenderRspFields();
        fieldList = interfaceDetail_container_editor.rspFields;
    }
    // 获取当前节点length
    var childIndex = getChildIndexByLevel(fieldLevel,fieldList);
    var childLevel = fieldLevel + "-" + childIndex;
    var colors = interfaceDetail_container_editor.colors;
    var indent = childLevel.split("-").length-2;

    // 新建对象并初始化
    var newField = {};
    newField._level = childLevel;
    newField._color = colors[indent];
    newField._indent = indent;
    // 设置默认属性
    newField.id = "";
    newField.k = "";
    newField.parent = ""; // 必须
    newField.mock = "null";
    newField.type = "6"; // 默认为null
    newField.note = "请添加说明"; // 必须
    newField.req = true;


    var editingCode = {};
    var editingLineNumber = 0;
    // 空校验
    var isEmpty = fieldList.some(function (it,id) {
        // 判断提交是否和初始空值相重复
        // 即:level前缀相同,并且参数名相同
        if (isSameLevel(it._level, newField._level) && it.k == newField.k) {
            editingCode = it;
            editingLineNumber = id+1;
            return true;
        } else {
            return false;
        }
    });
    // 重复校验
    var isRepeat = interfaceDetail_container_editor.fieldsRepeatCheck(fieldFlag);

    // 插入
    if (isEmpty) {
        alertTool.alert("第 "+editingLineNumber+" 行未定义参数:\n\n\t参数:" + editingCode.k + "\n\t说明:" + editingCode.note)
    } else if (isRepeat) {
        alertTool.alert("第 " + isRepeat.comparedLine + " 行与第 " + isRepeat.compareLine + " 行参数重复:\n\n\t参数:" + isRepeat.repeatItem.k + "\n\t说明:" + isRepeat.repeatItem.note);
    } else {
        if (fieldFlag == "1") {
            interfaceDetail_container_editor.reqFields.push(newField);
            interfaceDetail_container_editor.reRenderReqFields();

        }
        if (fieldFlag == "2") {
            interfaceDetail_container_editor.rspFields.push(newField);
            interfaceDetail_container_editor.reRenderRspFields();
        }
    }
}

/**
 * 导入参数方法
 * @param fieldFlag
 */
function modalBoxJsonImport(fieldFlag){
    var JSONObj = eval("("+editor_modal_box.jsonStr+")");
    // 编辑框中,列表化的field数据
    var fieldList = JSON2relatedList(JSONObj);
    // 已有的field数据
    var existFieldList;
    if(fieldFlag ==1)existFieldList=interfaceDetail_container_editor.reqFields;
    if(fieldFlag ==2)existFieldList=interfaceDetail_container_editor.rspFields;
    // 原始数据
    var prmSummaryBak;
    if(fieldFlag ==1) prmSummaryBak = interfaceDetail_container_editor.reqSummaryBak;
    if(fieldFlag ==2) prmSummaryBak = interfaceDetail_container_editor.rspSummaryBak;
    // 去重
    var filteredField = [];
    fieldList.forEach(function(now){
        var now_parent = now.parent.replace(" ","");
        var now_k = now.k.replace(" ","");
        var isExist = existFieldList.some(function(prm,id,ar){
            var prm_parent = prm.parent.replace(" ","");
            var prm_k = prm.k.replace(" ","");
            var prm_req = prm.req;
            var prm_note = prm.note;
            // 存在相等的
            if(prm_parent == now_parent && prm_k == now_k){
                //相等则更新,顺便除空格 xxxxxxx 不能用原始信息,因为路径已经发生改变 x.x
                now.id = prm.id;// ID不变
                now.req = prm_req; // 勾选状态不变
                now.note = prm_note; // 字段说明不变
                // 保存已有的字段
                filteredField.push(now);
                return true
            }else{
                return false;
            }
        });
        // now 为新增的字段
        if(!isExist){
            // 进一步判断,新增字段是否和原始数据存在一致
            // 此操作主要是为了避免同一字段改来改来改去最后ID丢失的情况
            var prmExist = prmSummaryBak.some(function(primItem){
                if(primItem.parent == now_parent &&  primItem.k.replace(" ","")== now_k){
                    now.id = primItem.id;
                    now.req = primItem.req;
                    now.note = primItem.note;
                    filteredField.push(now);
                    return true
                }else{
                    return false
                }
            });
            // 不相等则收集传出
            if(!prmExist){
                now.id="";
                filteredField.push(now);
            }

        }
    });
    // 去重结束
    // 重新渲染
    if(fieldFlag==1){
        interfaceDetail_container_editor.reqFields = filteredField;
        interfaceDetail_container_editor.reRenderReqFields();
    }
    if(fieldFlag==2){
        interfaceDetail_container_editor.rspFields = filteredField;
        interfaceDetail_container_editor.reRenderRspFields();
    }
}

/**
 * 将ID组织形式的字段层级关系,转换为keyPath组织形式
 * @param fieldList
 */
function transformFieldListParent(fieldList){
    var idKeyPathMap = {};

    trans(fieldList);

    function trans(fieldList){
        if(fieldList.length<=0)return;
        var childList = [];
        var leftList = [];

        //根节点组织
        fieldList.forEach(function(it){
            if(it.parent == "0"){
                idKeyPathMap[it.id] = "" + it.k;
                it.parent = "";
            }else{
                childList.push(it)
            }
        });

        // 子节点组织
        childList.forEach(function(it){
            var parentId = it.parent;
            if(idKeyPathMap.hasOwnProperty(parentId)){
                it.parent = idKeyPathMap[parentId];
                idKeyPathMap[it.id] = it.parent+"."+it.k
            }else{
                leftList.push(it)
            }
        });

        if(leftList.length>=fieldList.length){
            console.log("transformFieldListParent 可能存在无线递归")
            console.log("idKeyPathMap:")
            console.log(idKeyPathMap)
            leftList.forEach(function(it,id){
                console.log(id,it.parent,it.k)
            })
            return false;
        }

        if(leftList.length>0){
            trans(leftList)
        }
    }

}


// 展示编辑框
function showIfEditorBox(){
    indexPage.visible = false; // 首页隐藏
    right_side_nav.visible=false;// 侧边条隐藏
    nav.editorController.btnController.visible = true;// 编辑按钮组件亮起
    interfaceDetail_container.visible = false;// 详情框隐藏
    interfaceDetail_container_editor.visible = true; // 编辑框出现
    nav.editorController.btnEdit.visible =false;// 编辑按钮隐藏
    nav.editorController.btnSwitchEdition.visible =false;// 隐藏版本切换按钮
    nav.btnCreateIf.visible =false;// 接口创建按钮
}
// 展示详情框
function showIfDetailBox(){
    indexPage.visible = false; // 首页隐藏
    nav.editorController.btnController.visible = false; //编辑组件隐藏
    right_side_nav.visible=true;// 侧边条出现
    interfaceDetail_container.visible = true;// 详情框隐藏
    interfaceDetail_container_editor.visible = false; // 编辑框出现
    nav.editorController.btnEdit.visible = true; // 编辑按钮出现
    nav.editorController.btnSwitchEdition.visible =true;// 展示版本切换按钮
    nav.btnCreateIf.visible =true;// 接口创建按钮
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
