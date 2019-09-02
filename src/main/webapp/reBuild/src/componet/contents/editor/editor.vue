<template>
    <!-- 接口明细编辑容器-->
    <div v-cloak id="interfaceDetail_container_editor">
        <basic-info ref="basicInfo"></basic-info>
        <!--返回码模板-->
        <return-code-list></return-code-list>
        <!-- 请求参数模板 -->
        <params type="REQ"></params>
        <!-- 响应参数模板 -->
        <params type="RES"></params>
    </div>
</template>

<script>


//    import basicInfo from './basicInfo.vue';
//    import params from './params.vue';
//    import returnCodeList from './returnCodeList.vue';
//    import api from '../../../../lib/api.js';

    const basicInfo = resolve => require(['./basicInfo.vue'], resolve)
    const params = resolve => require(['./params.vue'], resolve)
    const returnCodeList = resolve => require(['./returnCodeList.vue'], resolve)
    const api = resolve => require(['../../../../lib/api.js'], resolve)



    export default{
        name:"editor",
        components:{
            basicInfo,
            params,
            returnCodeList
        },
        data(){
            return {}
        },
        mounted(){
            this.$store.state.editor = this;
        },
        methods:{
            getEditedInterfaceInfo(){
                return this.$store.getters.interfaceInfo;
            },

            saveInterfaceChanges:function(isAdvance){
                var self = this;
                var interfaceInfo = this.getEditedInterfaceInfo();
                var basicInfo =  interfaceInfo.basicInfo;
                var returnCodeList =  interfaceInfo.returnCodeList;
                var reqList =  interfaceInfo.reqList;
                var resList =  interfaceInfo.resList;

                // 校验输入
                var ifId = basicInfo.ifId;
                var ifName = basicInfo.ifName;
                isAdvance = ifId>0 && isAdvance=='true';
                var ifDesc = basicInfo.ifDesc;
                var ifSysId = basicInfo.ifSysId;
                var ifProId = basicInfo.ifProId;
                var ifProtocol = basicInfo.ifProtocol;
                var ifType = basicInfo.ifType;
                var ifUrl = basicInfo.ifUrl;
                var isIdempotent = basicInfo.isIdempotent;
                var returnType = basicInfo.returnType;
                var invokerList = this.$refs.basicInfo.getInvokers();


                if(!ifName)return this.$store.dispatch("alert","请填写接口名");
                if(!ifUrl)return this.$store.dispatch("alert","请填写接口路径");
                if(!ifSysId ||ifSysId<=0)return this.$store.dispatch("alert","所属系统未选择");
                if(!ifProId ||ifProId<=0)return this.$store.dispatch("alert","所属产品未选择");
                if(!ifProtocol)return this.$store.dispatch("alert","接口协议未选择");
                if(!ifType)return this.$store.dispatch("alert","请求方式未选择");
                if(!isIdempotent)return this.$store.dispatch("alert","请选择接口幂等性");
                if(!returnType)return this.$store.dispatch("alert","请选择返回值类型");
                var msg="";
                if(ifId){
                    msg = (isAdvance?"确认修改并推进接口:":"确认修改接口:")+"\n\t"+ifName+"?";
                }else{
                    msg = "新增接口:\n\t"+ifName+"?";
                }

                this.$store.dispatch('confirm',{
                    msg:msg,
                    cb:function () {
                        saveInterface()
                    }
                });


                // 保存接口流程
                function saveInterface(){
                    // 提交
                    var ifDetail = {
                        ifId:ifId,
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
                        reqFields:reqList,
                        rspFields:resList,
                        invokerList:invokerList,
                        advance:isAdvance
                    };

                    self.$store.dispatch("savingInterface");
                    api.ifsys.post("saveInterfaceDetail.do",ifDetail,function(data){
                        var detail = data["detail"];
                        // 保存按钮状态复原
                        self.$store.dispatch("savedInterface",{
                            ifSummary:{
                                id:detail.ifId,
                                ifName:ifName,
                                isIdempotent:isIdempotent
                            },
                            isCreate:!ifDetail.ifId
                        })
                    })
                }
            },
        },
//        created(){
//            this.$store.commit('open_editor');
//        },
//        beforeDestroy(){
//            this.$store.commit('close_editor')
//        }
    }
</script>

<style>
    #interfaceDetail_container_editor{
        width: 600px;
    }
    .ifInfoBlock .checkbox input{margin: 0;padding: 0}
    .ifInfoBlock .checkbox{margin: 0;padding: 0}
</style>