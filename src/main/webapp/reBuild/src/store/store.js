/**
 * Created by chenkuan on 2017/4/10.
 */
var Vue = require('Vue');
import Vuex from 'vuex';
Vue.use(Vuex);
import {alertEvent} from '../../lib/globalEvent';
import router from "../router/router"
import urlParam from "../../lib/urlParam"

const store = new Vuex.Store({
    state: {
        // 当前接口信息
        interfaceInfo:{
            basicInfo:{},
            reqHeaders:[],
            resHeaders:[]
        },
        // 侧边栏接口信息
        interfaceList:{
            pageInfo:{
                list:[]
            },
            currentId:0,
            nextLink:"",
            prevLink:"",
            ifObj:{
                id:0,
                ifName:""
            },
            navPath:{
                sysId:0,
                proId:0,
                sysName:'',
                proName:''
            }
        }, // 侧边栏组件
        sysList:[], // 当前项目系统列表
        allProList:[],// 当前项目产品列表
        returnCodeList:[], // 当前接口的返回码列表
        reqList:[], // 当前接口的请求参数列表
        rspList:[], // 当前接口的返回参数列表,
        invokers:[],
        header:{ // 头部操作区状态
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
                },
                saveBtnClass: "",
                editions: [],
                ifObj: {
                    id: "0",
                    ifName: "",
                    ifCreateTime: "加载中",
                    ifVersionNo: "加载中"
                },
            },
            projectInfoBtn:{
                memberList:[]
            }
        },
        jsonImporter:{
            handler:null,
            modal:null,
            jsonView:null,
            type:null,
            dates:[],
            isJsonValid:{
                   style:"", // has-success has-error
                   text:"请填写JSON",
                state:undefined
               },
            jsonStr:"",
            jsonObj:null,
            title : "",
            displayer:null,
            editor:null
        },
        backupFields:{
            reqFields:[],
            resFields:[],
            returnCodeFields:[],
        },
        shader:null,// 全局遮罩
        editor:null,// 接口编辑器
        // 接口创建数据
        creatorData:new InitCreatorData(),
        // 全局参数控制对象
        urlParam : urlParam,
        // 侧边栏滚动组件
        navAnchor:null,
        // 内容区域的jq对象
        content$:null,
        // 内容区域的各模块位置
        contentElementsTops:[],
        contentCurrentTop:0,
        toast:{
            visible:false,
            msg:""
        },
        yamlEditor:{
            value:""
        }
    },
    mutations: {
        updateInterfaceInfo(state,interfaceInfo){
            state.interfaceInfo = interfaceInfo;
        },
        // 打开编辑框
        open_editor(state,doInit){
            // 显示保存和返回按钮
            state.header.editorController.btnController.visible = true;
            // 隐藏创建接口按钮
            state.header.btnCreateIf.visible = false;
            // 进行编辑框初始化
            if(doInit){
                state.creatorData = new InitCreatorData()
            }
        },
        // 关闭接口编辑框
        close_editor(state){
            // 显示接口创建按钮
            state.header.btnCreateIf.visible = true;
            // 隐藏保存和返回按钮
            state.header.editorController.btnController.visible = false;
        },
        // 将列表数据填充到JSON框
        fillDatesToJsonExporter(state,handler){
            var jsonImporter = state.jsonImporter;

            // 返回参数导入按钮
            jsonImporter.handler = handler;
            jsonImporter.type = handler.type;
            // jsonImporter.dates = handler.fieldList;
            jsonImporter.jsonStr = "/*请导入标准的JSON:*/\n"+handler.stringifyFields();

            jsonImporter.title="输入参数 JSON 导入";
            jsonImporter.modal.modal("show");
        },
        // 校验JSON导入
        checkJSONImporter(state){
            var self = state.jsonImporter;
            var jsonObj;
            try {
                jsonObj = eval("(" + self.jsonStr + ")");
                self.displayer.JSONView(jsonObj).show();
                self.editor.hide();
                self.isJsonValid.style = "has-success";
                self.isJsonValid.text = "校验成功";
                self.isJsonValid.state = true;
            } catch (e) {
                self.displayer.text(self.jsonStr).show();
                self.editor.hide();
                self.isJsonValid.style = "has-error";
                self.isJsonValid.text = "Json格式有误";
                self.isJsonValid.state = false;
            }
            self.jsonObj = jsonObj;
        },
        fillInterfaceListWith(state,pageInfo){
            state.interfaceList.pageInfo = pageInfo;
            state.interfaceList.nextLink = "/"+["list",urlParam.PID,urlParam.ifSysId,urlParam.ifProId,Math.max(pageInfo['pageNum'],pageInfo['nextPage'])].join("/");
            state.interfaceList.prevLink =  "/"+["list",urlParam.PID,urlParam.ifSysId,urlParam.ifProId,Math.max(1,pageInfo['prePage'])].join("/");
            state.interfaceList.ifObj = pageInfo.list[0];
        }
    },
    actions:{
        alert(context,param){
            if(typeof param =="string"){
                alertEvent.$emit('alert',param)
            }else{
                alertEvent.$emit(param.msg,param.act);
            }

        },
        confirm(context,param){
            alertEvent.$emit('confirm',param.msg,param.cb);
        },
        fillDatesToJsonExporter(context,handler){
            if(!context.state.jsonImporter.modal){
                alertEvent.$emit('alert',"导入框暂未实例化");
                return;
            }
            // 导入数据
            context.commit("fillDatesToJsonExporter",handler);
            // 校验数据
            context.commit("checkJSONImporter");
        },
        // 接口保存时触发
        savingInterface(context){
            context.state.shader.show("正在保存");
        },
        // 接口保存完毕触发
        savedInterface(context,{ifSummary,isCreate}){
            context.state.shader.hide();
            if (isCreate) {
                // todo 新增侧边栏接口
                context.state.interfaceList.pageInfo.list.unshift(ifSummary);
            }
            alertEvent.$emit('alert',"保存成功:-D", function () {
                router.push("/view/"+ifSummary.id)
            });
        },

        // 接口保存方法
        saveInterfaceChanges(context,isAdvance){
            context.state.editor.saveInterfaceChanges(isAdvance);
        },
        returnOutOfEditor(context){
            router.push("/view/"+context.state.interfaceInfo.basicInfo.ifId);
        },
        reFreshPath(context){
            var state =  context.state;
            // 更新path导航
            var navPath = state.interfaceList.navPath;
            navPath.proId = urlParam.ifProId;
            navPath.sysId = urlParam.ifSysId;

            state.sysList.some((it,id)=>{
                if(it.id==navPath.sysId){
                    navPath.sysName = it.sysName
                }
            });
            state.allProList.some((it,id)=>{
                if(it.id==navPath.proId){
                    navPath.proName = it.proName
                }
            });
        },
        viewerScrollTo(context,top) {
            context.state.content$.animate({
                scrollTop:top
            },200)
        },
        // 根据选定接口进行界面着色
        selectInterface(context,ifId){
            context.state.interfaceList.currentId = ifId
        },
        toast(context,msg){
            context.state.toast.msg=msg;
            context.state.toast.visible=true;
        },
        closeToast(context){
            context.state.toast.visible = false;
        }
    },
    getters:{
        interfaceInfo(state){
            return {
                basicInfo:state.interfaceInfo.basicInfo,
                returnCodeList:state.returnCodeList,
                reqList:state.reqList,
                resList:state.rspList,
            }
        },
        creatorInterfaceInfo(state){
            return {
                basicInfo:state.creatorData.interfaceInfo.basicInfo,
                returnCodeList:state.creatorData.returnCodeList,
                reqList:state.creatorData.reqList,
                resList:state.creatorData.rspList,
            }
        },
        shader(state){
            return state.shader
        },
        currentSysId(){
            return urlParam.ifSysId
        },
        currentProId(){
            return urlParam.ifProId
        },
        creatorData(state){
            var basicInfo = state.creatorData.interfaceInfo.basicInfo;
            // 取值时先判定系统产品id是否选择，否则指定为当前
            if(basicInfo.ifSysId==0 || basicInfo.ifProId==0){
                basicInfo.ifSysId = urlParam.ifSysId;
                basicInfo.ifProId = urlParam.ifProId;
            }
            return state.creatorData
        },
        navPath(state){
            // 更新path导航
            var navPath = state.interfaceList.navPath;
            navPath.proId = urlParam.ifProId;
            navPath.sysId = urlParam.ifSysId;

            state.sysList.some((it,id)=>{
                if(it.id==navPath.sysId){
                    navPath.sysName = it.sysName
                }
            });
            state.allProList.some((it,id)=>{
                if(it.id==navPath.proId){
                    navPath.proName = it.proName
                }
            });
            return navPath
        }
    }
});

/**
 * 创建新的接口初始数据
 * @returns {{interfaceInfo: {basicInfo: {id: number, ifId: number, ifName: string, designer: string, sysName: string, proName: string, ifType: string, ifProtocol: string, returnType: string, ifVersionNo: string, isIdempotent: string, ifUrl: string, ifProId: number, ifSysId: number, ifDesc: string, invokers: Array}}, returnCodeList: *[], reqList: Array, rspList: Array}}
 * @constructor
 */
function InitCreatorData() {
    return {
        interfaceInfo:{
            basicInfo:{
                id:0,
                ifId: 0,
                ifName: "",
                designer: "",
                sysName: "",
                proName: "",
                ifType:"POST",
                ifProtocol: "HTTP",
                returnType: "JSON",
                ifVersionNo: "",
                isIdempotent: "N",
                ifUrl: "",
                ifProId: 0,
                ifSysId: 0,
                ifDesc:"",
                invokers : []
            }
        },
        returnCodeList:[
            {id:"",rspCode:'00000',rspCodeDesc:'成功'},
            {id:"",rspCode:'30002',rspCodeDesc:'用户未登录'}
        ],
        reqList:[],
        rspList:[],
    }
}

export default store