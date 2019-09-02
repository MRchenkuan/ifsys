<template>
    <aside id="interfaceList" class="panel panel-default themesColor">
        <nav-path :navPath="navPath"></nav-path>
        <ul class="interfaceList list-group">
            <router-link tag="li" v-for="ifObj in pageInfo.list" :to="{path:'/view/'+ifObj.id}" :class='ifObj.id==currentId? "selected" :"" ' class="list-group-item " :title="ifObj.ifName">
                <span class="interfaceListItem">{{ifObj.id}} {{ifObj.ifName}}</span>
                <span title="非幂等" v-if="ifObj.isIdempotent=='N'" class="label-tag label-fx">fx</span>
                <span title="不在测试状态" v-if="ifObj.inAutoTest=='N'" class="label-tag label-autotest"><span class="glyphicon glyphicon-flash"> </span></span>
                <span title="已关注" v-if="ifObj.followed" class="label-tag label-follow"><span class="glyphicon glyphicon-eye-open"> </span></span>
            </router-link>
        </ul>
        <page-helper :pageInfo="pageInfo" :nextLink="nextLink" :prevLink="prevLink"></page-helper>
    </aside>
</template>
<script>
    import navPath from './navpath.vue';
    import pageHelper from './pageHelper.vue';
    import api from '../../../lib/api.js';
    import utils from '../../../lib/utils.js';
    import urlParam from '../../../lib/urlParam';
    import {alertEvent} from '../../../lib/globalEvent';

    export default{
        name:'interface-list',
        components:{
          navPath,pageHelper
        },
        data(){
            return this.$store.state.interfaceList
        },
        methods:{
            // 获取接口列表方法
            showInterfaceList(pid,sysId,proId,pageNum){
                urlParam.setListStatus(pid,sysId,proId,pageNum);
                var self = this;
                // 初始获取接口列表
                api.ifsys.post("getallifsys.do",{
                    "ifName":"",
                    "ifProId":urlParam.ifProId,
                    "ifSysId":urlParam.ifSysId,
                    "pageNum":urlParam.pageNum,
                    'pid':urlParam.PID
                },function(data){
                    var pageInfo =  data['pageInfo'];
                    self.pageInfo = pageInfo;
                    self.nextLink = "/"+["list",urlParam.PID,urlParam.ifSysId,urlParam.ifProId,Math.max(pageInfo['pageNum'],pageInfo['nextPage'])].join("/");
                    self.prevLink =  "/"+["list",urlParam.PID,urlParam.ifSysId,urlParam.ifProId,Math.max(1,pageInfo['prePage'])].join("/");
                    self.ifObj = pageInfo.list[0];
//                    self.currentId = pageInfo.list[0].id;
                },function (data) {
                    alertEvent.$emit('alert',data['rspCd']+"-"+data['rspInf'])
                });
            }
        },
        created(){
            var pageNum=this.$route.params.pageNum;
            var pid=this.$route.params.pid;
            var proId=this.$route.params.proId;
            var sysId=this.$route.params.sysId;
            this.showInterfaceList(pid,sysId,proId,pageNum)
//            this.$store.state.interfaceList = this;
        },
        watch: {
            // 如果路由有变化，会再次执行该方法
            "$route": function (route) {
                var pageNum=route.params.pageNum;
                var pid=route.params.pid;
                var proId=route.params.proId;
                var sysId=route.params.sysId;
                if(["pages","projects","modules"].indexOf(route.name)>=0){
                    // 只有切换到上述三个路由才会导致组件更新
                    this.showInterfaceList(pid,sysId,proId,pageNum)
                }

                // 实时更新页面标题
                document.title = this.$store.getters.navPath.sysName+">"+this.$store.getters.navPath.proName;

            }
        }
    }
</script>

<style lang="sass" rel="stylesheet/scss">
    #interfaceList{
        border-top:2px solid #25AEB3;
        padding: 0;
        margin: 0;
        width: 300px;
        position: fixed;
        left: 0;
        top: 60px;
        bottom: 3rem;
        max-height: 647px;
        overflow-x: hidden;
        .interfaceList{
            position: absolute;
            top: 29px;
            bottom: 4.5rem;
            right: 0;
            left: 0;
        }
        .list-group{

            padding:0;
            overflow-y: auto;
            .list-group-item{
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-size: 12px;
                transition: all .1s ease;
                padding: 5px 10px;
                &:hover{
                    color:#fff;
                    background: #25AEB3;
                    transition: none;
                }
                &.selected{
                    color:#fff;
                    background: #25AEB3;
                    transition: all .3s ease;
                }
            }
        }
        .pagination{
            padding: 0;margin: auto
        }
    }

    .label-fx{
        color: #fff;
        background: #a4626f;
        padding: 1px 2px 2px !important;
        text-decoration: line-through;
    }
    .label-follow{
        color:#fff;
        background: #7193a4;
    }
    .label-autotest{
        color: #fff;
        background: #a4626f;
    }
    .label-tag{
        margin-left: 2px;
        border-radius: 5px;
        padding: 0 2px;
        float: right;
        line-height: 1;
    }
</style>