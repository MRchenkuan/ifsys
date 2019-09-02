<template>
    <div id="switchList" class="switchList">
        <div id="sysList" class="list-group" v-show="isListShow">
            <div style="max-height: 400px;overflow-y: auto">
                <a v-for="sys in sysList" href="#" class="list-group-item list-group-item-theme" @mouseover="showProList(sys.id,sys.sysName)">
                    <a v-if="sysList.length<=0" class="list-group-item disabled text-center" style="font-weight: 900"><- 空 -></a>
                    <span class="text">{{sys.sysName}}</span>
                    <span style="font-size: 12px;float: right" class="glyphicon glyphicon-chevron-right"></span>
                </a>
            </div>
            <a v-if="canEditSys" class="canEditSys list-group-item list-group-item-theme" @click="createSys"><span class="glyphicon glyphicon-plus"></span> 新建系统</a>
        </div>

        <div id="proList" class="list-group" style="" v-show="isListShow">
            <div style="max-height: 400px;overflow-y: auto" @click="closeSelf" >
                <a v-show="isProListEmpty" class="list-group-item disabled text-center" style="font-weight: 900"><- 空 -></a>
                <router-link tag="a" v-for="pro in proList" :to="{path:'/modules/'+pro.sysId+'/'+pro.id}" class="list-group-item">{{pro.proName}}</router-link>
            </div>
            <a v-if="canEditSys" class="canEditSys list-group-item list-group-item-theme" @click="createPro(sysId)"><span class="glyphicon glyphicon-plus"></span> 新建产品</a>
        </div>
        <createSysAndProFrame ref="createSysAndProFrame"></createSysAndProFrame>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss">
    #switchList{
        position: relative;
        #sysList{
            z-index:99;
            position:absolute;
            left:0;
            width: 12rem;
            .list-group-item{
                line-height: 1.2rem;
                padding: .5rem;
                .text{
                    line-height: 1.2rem;
                    width: 8rem;
                    display: inline-block;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                }
            }
        }

        #sysList,#proList{
            box-shadow: 0 0 10px #ccc;
            border-bottom: 2px solid #2cb6bb;
            border-radius: 7px;
            overflow: hidden;
            .list-group-item{
                transition: all .2s ease;
                .glyphicon-chevron-right{
                    color:#2cb6bb;
                }
                &:hover{
                    .glyphicon-chevron-right{
                        color:#fff;
                    }
                    color: #fff;
                    background-color: #2cb6bb;
                    transition: none;
                }
                border-radius: 0 ;
            }
            .canEditSys{
                color:#2cb6bb;
                cursor:pointer;
                &:hover{
                    color: #fff;
                    background-color: #2cb6bb;
                    transition: none;
                }
                border-radius: 0
            }
        }
        #proList{
            z-index:98;
            position:absolute;
            left:12rem;
            width: 12rem;
            div{
                overflow-x: hidden;
            }
            .list-group-item{
                padding: .5rem;
                line-height: 1.2rem;
                width: 12rem;
                display: inline-block;
                white-space: nowrap;
                text-overflow: ellipsis;
            }
        }
    }
</style>
<script>
    import api from '../../../lib/api';
    import urlParam from '../../../lib/urlParam';
    import createSysAndProFrame from './createSysAndProFrame';
    var jQuery,$;
    jQuery = $ = require('../../../static/js/3rd/jquery.js');

    var sysList;
    export default  {
            name:"sys-list",
            data(){
                sysList = this;
                return {
                    defaultText: "切换系统",
                    allProList:[],
                    sysList: [],
                    proList:[],
                    sysId:0,
                    proId:0,
                    canEditSys:false,
                    memberRoles:[],
                    isListShow:false,
                    isProListEmpty:true,// 子列表是否为空，若为空，则会显示一个空标签
                    sysName:"",
                    proName:""
                }
            },
            components:{
                createSysAndProFrame
            },
            created(){
                this.initSystemList()
            },
            methods:{
                initSystemList:function () {
//                    var sysList = this;
                    api.ifsys.post("getAllSysInfo.do",{
                        type:"all",
                        pid:urlParam.PID
                    },(data)=>{
                        this.sysList = data['sysList'];
                        this.allProList = data['proList'];
                        this.memberRoles = data['memberRoles'];
                        // 缓存系统数据
                        this.$store.state.sysList = this.sysList;
                        this.$store.state.allProList = this.allProList;
                        this.$store.state.memberRoles = this.memberRoles;

                        // 角色权限功能初始化
                        this.memberRoles.some( (it)=> {
                            // 系统列表编辑权限
                            if(it.id==0){
                                this.canEditSys = true;
                            }
                        });
                        document.title = this.$store.getters.navPath.sysName+">"+this.$store.getters.navPath.proName;
                    });
                },
                closeSelf:function () {
                    this.isListShow = false;
                },
                showProList:function(sysId){
                    var proList = [];
                    sysList.allProList.some(function (it) {
                        if(it.sysId == sysId){
                            proList.push(it);
                        }
                    });
                    this.isProListEmpty = proList.length<=0;
                    this.proList = proList;
                    //// 绑定系统列表事件
                    this.sysId = sysId
                },
                toggle:function(){
                    this.isListShow = !this.isListShow
                },
                createSys:function () {
                    this.isListShow=false;
                    var createSysAndProFrame = this.$refs.createSysAndProFrame;
                    createSysAndProFrame.isPro = false;
                    createSysAndProFrame.sysName = "";
                    createSysAndProFrame.remark = "";
                    createSysAndProFrame.information = "";
                    createSysAndProFrame.show();
                },
                createPro:function (sysId) {
                    console.log(sysId)
                    var createSysAndProFrame = this.$refs.createSysAndProFrame;
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
            },
            watch:{
                "$route":function (route) {
                    var pid= route.params.pid;
                    if(pid){
                        urlParam.setListStatus(pid,false,false,false)
                        this.initSystemList()
                    }
                }
            }
        }

    var sysListDisapearTimer;
    $(document).delegate("#sysList .list-group-item",'mouseenter',function(e) {
        var $proList = $("#proList");
        // 产品列表展示
        var a = e.currentTarget;
        window.$proList = $proList;
        $proList.css({
            top:Math.max(0,14+$(a).position().top - $proList.height()/2)
        })
    }).delegate("#btn-switchList",'mouseenter mousemove',function(e){
        // 系统列表展示
        e.stopPropagation();
        clearTimeout(sysListDisapearTimer)
        sysList.isListShow = true;
    }).delegate("#switchList",'mouseenter mousemove click',function(e){
        // 阻止冒泡
        e.stopPropagation();
        clearTimeout(sysListDisapearTimer);
    }).bind("mousemove",function(e){
        clearTimeout(sysListDisapearTimer);
        sysListDisapearTimer = setTimeout(function () {
            sysList.isListShow = false;
            clearTimeout(sysListDisapearTimer)
        }, 50)
    });
</script>