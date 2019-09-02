<template>
    <!-- 接口明细容器-->
    <div id="interfaceDetail_container" :style="'display: '+visible?'block':'none'" class="themesColor">
        <!--基本信息-->
        <basic-info id="basicInfo" ref="basicInfo"></basic-info>
        <!--返回码模板-->
        <return-code-list id="returnCodeList" :returnCodeList="returnCodeList" :ifId="basicInfo.ifId"></return-code-list>
        <!-- 头部参数列表 -->
        <header-list id="reqHeaders" type="REQ"></header-list>
        <!-- 请求参数模板 -->
        <params id="reqBody" type="REQ" :fieldList="reqFieldList" :paramsBody="reqBody"></params>
        <!-- 头部参数列表 -->
        <header-list id="rspHeaders" type="RSP"></header-list>
        <!-- 响应参数模板 -->
        <params id="rspBody" type="RSP" :fieldList="rspFieldList" :paramsBody="rspBody"></params>
        <!-- 关注列表 -->
        <invoker-list id="invokerList"></invoker-list>
        <!-- 变更记录 -->
        <change-logs id="changeLogs"></change-logs>

        <button id="btn-deleteIf" class="btn btn-danger btn-sm" @click="deleteInterface">
            <span class="glyphicon glyphicon-trash"></span> 删除接口
        </button>

        <nav-anchor></nav-anchor>
    </div><!-- 接口明细容器结束-->
</template>
<style>
    #interfaceDetail_container{
        width: 600px;
    }
    #btn-deleteIf{
        opacity: .3;width: 50%;
        margin-left: 25%;
    }
    #btn-deleteIf:hover{
        opacity: 1;
        text-decoration: line-through;
    }
</style>
<script>
    import basicInfo from './basicInfo.vue';
//    import changeLogs from './changeLogs.vue';
//    import invokerList from './invokerList.vue';
//    import params from './params.vue';
//    import returnCodeList from './returnCodeList.vue';
//    import headerList from './headers.vue';
//    import navAnchor from './navAnchor.vue';
    import api from '../../../../lib/api.js';

//    const basicInfo = resolve => require(['./basicInfo.vue'], resolve)
    const changeLogs = resolve => require(['./changeLogs.vue'], resolve)
    const invokerList = resolve => require(['./invokerList.vue'], resolve)
    const returnCodeList = resolve => require(['./returnCodeList.vue'], resolve)
    const headerList = resolve => require(['./headers.vue'], resolve)
    const params = resolve => require(['./params.vue'], resolve)
    const navAnchor = resolve => require(['./navAnchor.vue'], resolve)

    export default{
        name:"interface-viewer",
        data(){
            var self = this;
            return {
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
                    ifCreateTime:"未知",
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
                mockLinkShow:false,
                realLinkShow:false,
                pro:{},
                system:{},
                uiInfo:{
                    reqFieldsViewType:null,// CND CNT TWD
                    rspFieldsViewType:null// CND CNT TWD
                }
            };
        },
        components:{
            basicInfo,
            changeLogs,
            invokerList,
            params,
            returnCodeList,
            navAnchor,
            headerList
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
                return data[index]
            },
            deleteInterface:function(){
                var ifId = this.$refs.basicInfo.basicInfo.ifId;
                var ifName = this.$refs.basicInfo.basicInfo.ifName;
                if(!ifId)return;

                this.$store.dispatch('confirm',{
                    msg:"正在删除接口:["+ifId+"]"+ifName+"?",
                    cb:()=>{
                        this.$store.state.shader.show("正在删除接口:["+ifId+"]"+ifName+" ");
                        api.ifsys.post("deleteInterFaceById.do",{
                            "interFaceInfo": {
                                "id": ifId
                            }
                        },(data)=>{
                            this.$store.state.shader.hide();
                            location.reload()
                        });
                    }
                })
            },
            // 更新接口信息
            updateInfo(ifId){
                if(ifId){
                    // 更新基本信息
                    basicInfo.methods.updateBasicInfo(ifId);
                    // 返回字段/返回码信息
                    this.updateInterfaceDetail(ifId)
                    // 更新接口栏接口底色
                    this.$store.dispatch("selectInterface",ifId)
                }
            },
            // 更新接口基本信息
            updateInterfaceDetail(ifId){
                var self = this;
                api.ifsys.post("getInterfaceDetail.do", {"ifId":ifId}, function (data) {
                    var detail = data['detail'];
                    // 返回码显示
                    self.returnCodeList = detail['rspCds'];
                    // 字符串参数显示
                    self.reqBody = detail['reqJsonStr'];
                    self.rspBody = detail['rspJsonStr'];
                    // 渲染参数列表 - 设置纯json字符串到展示框
                    var reqList= detail['reqFields'];
                    var rspList= detail['rspFields'];

                    // 设置当前接口数据
                    self.$store.state.returnCodeList = detail['rspCds'];
                    self.$store.state.reqList = detail['reqFields'];
                    self.$store.state.rspList = detail['rspFields'];

                    // 设置备份数据
                    self.$store.state.backupFields.reqFields = detail['reqFields'];
                    self.$store.state.backupFields.resFields = detail['rspFields'];
                    self.$store.state.backupFields.returnCodeFields = detail['rspCds'];


                    // 渲染参数列表 - parent
                    transformFieldListParent(reqList);
                    transformFieldListParent(rspList);


                    self.reqFieldList = reqList;
                    self.rspFieldList = rspList;
                });
            }
        },
        created(){
            var ifId = this.$route.params.ifId;
            if(ifId){
                this.updateInfo(ifId);
            }
        },
        updated(){
            var tops = [];
            try{
                // 防止切换时找不到元素
                tops.push(document.querySelector("#basicInfo").offsetTop)
                tops.push(document.querySelector("#returnCodeList").offsetTop)
                tops.push(document.querySelector("#reqHeaders").offsetTop)
                tops.push(document.querySelector("#reqBody").offsetTop)
                tops.push(document.querySelector("#rspHeaders").offsetTop)
                tops.push(document.querySelector("#rspBody").offsetTop)
                tops.push(document.querySelector("#invokerList").offsetTop)
                tops.push(document.querySelector("#changeLogs").offsetTop)
            }catch (e){
                tops = [0,0,0,0,0,0,0,0]
            }
            // 将各个模块的高度信息缓存到store，用以判断滚动导航的位置
            this.$store.state.contentElementsTops = tops
        },
        watch: {
            // 如果路由有变化，会再次执行该方法
            "$route": function (route) {
                var ifId = route.params.ifId;
                if(ifId){
                    this.updateInfo(ifId);
                }
            }
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
</script>