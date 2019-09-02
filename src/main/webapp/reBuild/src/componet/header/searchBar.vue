<template>
    <div class="navbar-form navbar-left">
        <div class="input-group">
            <input id="ifSearchInput" v-model="keywords" type="text" class="ifSearchInput form-control" :placeholder="placeHolder" @keydown.enter="searchGlobal">
            <div class="input-group-btn">
                <button id="globalSearch1" type="button" class="btn btn-theme innerShadowhite"><span class="glyphicon glyphicon-search" @click="searchGlobal"></span></button>
                <button type="button" class="btn btn-theme dropdown-toggle innerShadowhite" data-toggle="dropdown"><span class="caret"></span></button>
                <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                    <li id="globalSearch2" role="presentation" @click="searchGlobal"><a>全局搜索</a></li>
                    <li id="scopeSearch" role="presentation" @click="search"><a>当前系统</a></li>
                </ul>
            </div><!-- /btn-group -->
        </div><!-- /input-group -->
    </div>
</template>

<script>
    import api from '../../../lib/api.js';
    import utils from '../../../lib/utils.js';
    import urlParam from '../../../lib/urlParam';
    export default {
        name:"search-bar",
        data(){
            return{
                placeHolder:"ID、名称、地址、作者",
                keywords:""
            }
        },
        methods:{
            searchGlobal(){
                api.ifsys.post("getallifsys.do",{
                    "ifName":this.keywords,
                    "ifProId":0,
                    "ifSysId":0,
                    "pageNum":1,
                    'pid':urlParam.PID
                },(data)=>{
                    this.$store.state.interfaceList.navPath.proId=0;
                    this.$store.state.interfaceList.navPath.sysId=0;
                    this.$store.commit("fillInterfaceListWith",data['pageInfo']);
                });
            },
            search(){
                api.ifsys.post("getallifsys.do",{
                    "ifName":this.keywords,
                    "ifProId":urlParam.ifProId,
                    "ifSysId":urlParam.ifSysId,
                    "pageNum":1,
                    'pid':urlParam.PID
                },(data)=>{
                    this.$store.state.interfaceList.navPath.sysId = urlParam.ifSysId;
                    this.$store.state.interfaceList.navPath.proId = urlParam.ifProId;
                    this.$store.commit("fillInterfaceListWith",data['pageInfo']);
                });
            }
        }
    }
</script>
<style lang="css">
    .ifSearchInput{
        border-color: #24868a;
        color:#666;
    }
</style>