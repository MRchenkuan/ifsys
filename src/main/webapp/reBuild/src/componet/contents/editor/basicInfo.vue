<template>
    <div id="editor_basicInfo" class="ifInfoBlock panel-default panel" >
        <h5 class="themesColor panel-body" style="overflow: visible">
            <span><span class="glyphicon glyphicon-th-large"></span> 基本信息</span>
            <button tag="button" id="importIfml" @click="importIfml" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon-edit"></span> 编辑IFML
            </button>
        </h5>
        <table class="table table-striped table-bordered">
            <tbody>
                <tr>
                    <td colspan="2"  style="font-weight: 900" >
                        <div class="input-group input-group-lg themesColor" style="width: 100%">
                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-tag"></span><span class="necessary"></span>
                            </span>
                            <input type="text" class="form-control themesColor" placeholder="填写接口名" v-model="basicInfo.ifName">
                        </div>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div class="input-group" style="width: 100%">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-link"></span><span class="necessary"></span>
                                </span>
                            <input type="text" class="form-control" placeholder="填写接口路径,不含host"  v-model="basicInfo.ifUrl">
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="input-group">
                            <span class="glyphicon glyphicon-globe"></span> 请求方式：
                            <select class="btn-group" v-model="basicInfo.ifType" title="method">
                                <option class="btn btn-default">POST</option>
                                <option class="btn btn-default">GET</option>
                                <option class="btn btn-default">PUT</option>
                                <option class="btn btn-default">DELETE</option>
                            </select>
                        </div>
                    </td>
                    <td>
                        <span class="glyphicon glyphicon-transfer"></span> 接口协议：
                        <select v-model="basicInfo.ifProtocol" title="method">
                            <option>HTTP</option>
                            <option>DUBBO</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span class="glyphicon glyphicon-th-large"></span> 所属系统：
                        <select v-model="basicInfo.ifSysId">
                            <option v-for="sys in sysList" :value="sys.id">{{sys.sysName}}</option>
                        </select>
                    </td>
                    <td>
                        <span class="glyphicon glyphicon-download-alt"></span> 返回类型：
                        <select v-model="basicInfo.returnType" title="method">
                            <option>JSON</option>
                            <option>文本</option>
                            <option>无</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span class="glyphicon glyphicon-th"></span> 所属产品：
                        <select v-model="basicInfo.ifProId">
                            <option v-for="pro in proList" :value="pro.id" v-if="pro.sysId == basicInfo.ifSysId" :selected="basicInfo.ifProId==pro.id?'selected':''">{{pro.proName}}</option>
                        </select>
                    </td>
                    <td>
                        <span class="glyphicon glyphicon-leaf"></span> 幂等性：
                        <select v-model="basicInfo.isIdempotent" title="method">
                            <option value="Y">幂等</option>
                            <option value="N">非幂等</option>
                        </select>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="panel-body">
            <span class="normalTextColor"><span class="glyphicon glyphicon-comment"></span> 说明:</span>
            <textarea style="min-height: 130px" class="form-control" v-model="basicInfo.ifDesc" title="remark">{{basicInfo.ifDesc}}</textarea>
        </div>
        <div class="panel-body">
            <span class="glyphicon glyphicon-user"></span>
            <span class="glyphicon glyphicon-user"></span> 添加关注：
            <div id="editor_invokers_textarea"></div>
        </div>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss">
    #editor_invokers_textarea{
        position: relative;
        height: 2.5rem;
        border:1px solid #cccccc;
        border-radius:3px;
    }

    #importIfml{
        float: right;
    }
</style>
<script>
    var jQuery,$;
    jQuery = $ = require('../../../../static/js/3rd/jquery.js');
    require('../../../../static/js/3rd/FreeSearch.js')(jQuery);

    import api from '../../../../lib/api.js';
    import urlParam from '../../../../lib/urlParam.js';

    export default {
        name:"basic-info",
        props:['initType'],
        data(){
            // 判断组件用途
            var isCreator = this.$props.initType == 'creator';
            return {
                basicInfo:isCreator?this.$store.getters.creatorData.interfaceInfo.basicInfo
                        :this.$store.state.interfaceInfo.basicInfo,
                freeSearch:null
            };
        },
        watch:{
            // 观测系统列表变化，触发产品列表变化
            "basicInfo.ifSysId":function (ifSysId) {
                var allProlist = this.proList;
                allProlist.some( (it) =>{
                    if(it.sysId == ifSysId){
                        this.basicInfo.ifProId = it.id;
                        return true;
                    }
                })
            }
        },

        computed:{
            proList:function () {
                return this.$store.state.allProList;
            },
            sysList:function () {
                return this.$store.state.sysList;
            }
        },
        mounted(){
            var self = this;
            // 初始化invoker插件
            api.ifsys.post("getMemberList.do",{
                'pid':urlParam.PID
            },function(data){
                self.freeSearch = $("#editor_invokers_textarea").searchBox(data["userInfos"]);
                self.setInvokers(self.basicInfo.invokers)
            });
        },
        methods:{
            /**
             * 设置接口初始关注者
             * @param invokers
             */
            setInvokers: function(invokers){
                var self = this;
                var freeSearch = self.freeSearch;
                if(!freeSearch){
                    console.log("freeSearch:用户列表不存在时重新获取");
                    // 当接口列表不存在时重新获取
                    api.ifsys.post("getMemberList.do",{},function(data){
                        self.freeSearch = $("#editor_invokers_textarea").searchBox(data["userInfos"])
                    });
                    freeSearch = self.freeSearch;
                }
                freeSearch.cleanAllItems();
                var _invokers=[];
                invokers.forEach(function(it){_invokers.push(it.uId)})
                freeSearch.setInitItems(_invokers);
            },
            getInvokers:function () {
                return this.freeSearch.getAllSelections()
            },

            importIfml(){
                this.$router.push("/ifml")

            }
        }
    }
</script>