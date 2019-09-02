<template>
    <div class="ifInfoBlock panel-default panel">
        <h3 class="panel-body">
            <span id="ifTitle" class="themesColor">[{{basicInfo.ifId}}] {{basicInfo.ifName}}</span>
            <router-link tag="span" replace :to="{path:'/view/'+basicInfo.ifId+'/edit'}" id="btn-editIf" @click="editInterfaceDetail" >
                    <span class="glyphicon glyphicon-cog"></span> 编辑接口
            </router-link>
        </h3>
        <table class="table table-striped table-bordered">
            <tbody>
                <tr>
                <td colspan="3" style="font-weight: 900" >
                    <span class="glyphicon glyphicon-link"></span> 路径名：
                    <span id="ifUrl" data-toggle="tooltip" data-placement="top" :title="basicInfo.ifUrl">{{basicInfo.ifUrl}}</span>
                </td>
                </tr>
                <tr>
                    <td><span class="glyphicon glyphicon-user"></span> 设计者：<span id="designer">{{basicInfo.designer}}</span></td>
                    <td><span class="glyphicon glyphicon-globe"></span> 请求方式：<span id="ifType">{{basicInfo.ifType}}</span></td>
                    <td><span class="glyphicon glyphicon-flag"></span> 当前版本：<span id="ifVersionNo">V{{basicInfo.ifVersionNo}}</span><span class="glyphicon glyphicon-time"></span></td>

                </tr>
                <tr>
                    <td><span class="glyphicon glyphicon-th-large"></span> 所属系统：<span id="sysName">{{basicInfo.sysName}}</span></td>
                    <td><span class="glyphicon glyphicon-download-alt"></span> 返回类型：<span id="returnType">{{basicInfo.returnType}}</span></td>
                    <td><span class="glyphicon glyphicon-leaf"></span> 幂等性：<span id="isIdempotent">{{basicInfo.isIdempotent=='Y'?"幂等":"非幂等"}}</span></td>
                </tr>
                <tr>
                    <td><span class="glyphicon glyphicon-th"></span> 所属产品：<span id="proName">{{basicInfo.proName}}</span></td>
                    <td><span class="glyphicon glyphicon-transfer"></span> 接口协议：<span id="ifProtocol">{{basicInfo.ifProtocol}}</span></td>
                    <td><span class="glyphicon glyphicon-tree-deciduous"></span> 创建时间：<span id="ifCreateTime">{{basicInfo.ifCreateTime}}</span></td>
                </tr>
                <tr>
                    <td colspan="3"><span class="glyphicon glyphicon-cog"></span> 获取MOCK：
                        <a @click="toggleLinkShow('mock')" style="color: #666;cursor: pointer"><span class="glyphicon glyphicon-eye-open"></span>展开/收缩</a>
                        <span id="debugUrl" v-show="debugUrlShow">
                            <a class="get-view link-active" :href="'../interfaceDebug.do?sys='+basicInfo.ifProId+'&ifUrl='+basicInfo.ifUrl" target="_blank">
                                <i>{{originHost}}/interfaceDebug.do?sys={{basicInfo.ifProId}}&ifUrl=</i>{{basicInfo.ifUrl}}
                            </a>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><span class="glyphicon glyphicon-link"></span> 真实地址：
                        <a @click="toggleLinkShow('real')" style="color: #666;cursor: pointer"><span class="glyphicon glyphicon-eye-open"></span>展开/收缩</a>
                        <span id="realUrl" v-show="realUrlShow">
                            <a class="get-view link-active" :href="pro.addressUrl + basicInfo.ifUrl" target="_blank">
                                <i>{{pro.addressUrl}}</i>{{basicInfo.ifUrl}}
                            </a>
                        </span>
                    </td>
                </tr>
            </tbody>
        </table>
        <div>
            <pre class="normalTextColor" style="border-bottom: none;border-radius: 0 0 4px 4px"><span class="glyphicon glyphicon-comment"></span> 说明:<br>{{basicInfo.ifDesc}}</pre>
        </div>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss">
    #btn-editIf{
        font-size: 12px;
        cursor: pointer;
        vertical-align: middle;
        padding: 0 30px;
        color:#25aeb3;
        &:hover{
             text-decoration: underline;
         }
    };
    #ifTitle{
        width: 460px;
        float: left;
    }
</style>
<script>
    import api from '../../../../lib/api.js';
    import {interfaceEvent} from '../../../../lib/globalEvent';
    import utils from '../../../../lib/utils';
    var self;

    export default {
        name:'basic-info',
        data(){
            self = this;
            return {
                ifId:"",
                basicInfo:{},
                pro:{},
                sys:{},
                originHost:"",
                debugUrlShow:false,
                realUrlShow:false
            }
        },
        methods:{
            editInterfaceDetail:()=>{},
            toggleLinkShow(type){
                if(type=="real")this.realUrlShow = !this.realUrlShow;
                if(type=="mock")this.debugUrlShow = !this.debugUrlShow;
            },
            updateBasicInfo(ifId){
                // 接口基本信息
                api.ifsys.post("queryInterFaceById.do", {"interFaceInfo": {"id": ifId}}, function (data) {
                    var interFaceInfo = data['interFaceInfo'];
                    var pro = data['pro'];
                    var system = data['system'];
                    var fieldList = data['fieldList'];
                    var follow = data['follow'];
                    var userInfo = data['userInfo'];
                    var invokers = data['invokers'];
                    var ifName = data['interFaceInfo'].ifName;

                    // 基本信息
                    self.basicInfo = {
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
                        ifCreateTime:utils.tmStampToTime(interFaceInfo.ifCreateTime).substr(0,10),
                    };

                    // 缓存接口基本数据
                    self.$store.state.interfaceInfo.basicInfo = {
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
                        ifCreateTime:utils.tmStampToTime(interFaceInfo.ifCreateTime).substr(0,10),
                        invokers : invokers
                    };
                    // 缓存关注者
                    self.$store.state.invokers = invokers;

                    // 设置顶部信息
                    self.pro = pro;
                    self.sys = system;
                });
            }
        }
    }
</script>