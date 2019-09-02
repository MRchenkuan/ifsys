<template>
    <!--侧边栏导航-->
    <div id="right-side-nav" class="right-side-nav">
        <ul v-show="visible">
            <li :class="scrollIndex==0?'active':''" @click="jump('#basicInfo')"><span class="glyphicon glyphicon-info-sign"></span> 基本信息</li>
            <li :class="scrollIndex==1?'active':''" @click="jump('#returnCodeList')"><span class="glyphicon glyphicon-book"></span> 返回码</li>
            <li v-if="reqHeader.length>0" :class="scrollIndex==2?'active':''" @click="jump('#reqHeaders')"><span class="glyphicon glyphicon-cloud-upload"></span> 请求Headers</li>
            <li :class="scrollIndex==3?'active':''" @click="jump('#reqBody')"><span class="glyphicon glyphicon-cloud-upload"></span> 请求参数</li>
            <li v-if="resHeader.length>0" :class="scrollIndex==4?'active':''" @click="jump('#rspHeaders')"><span class="glyphicon glyphicon-cloud-download"></span> 响应Headers</li>
            <li :class="scrollIndex==5?'active':''" @click="jump('#rspBody')"><span class="glyphicon glyphicon-cloud-download"></span> 响应参数</li>
            <li :class="scrollIndex==6?'active':''" @click="jump('#invokerList')"><span class="glyphicon glyphicon-calendar"></span> 关注列表</li>
            <li :class="scrollIndex==7?'active':''" @click="jump('#changeLogs')"><span class="glyphicon glyphicon-blackboard"></span> 变更记录</li>
            <!--<li class='{{current=="settings"?"active":""}}'><a href="#settings"><span class="glyphicon glyphicon-cog"></span> 设置</a></li>-->
            <li><a href=""></a></li>
            <li name="interfaceDetail_container" @click="jump('#interfaceDetail_container')"><span class="glyphicon glyphicon-arrow-up"></span> 回到顶部</li>
        </ul>
    </div>
</template>

<script>
    export default{
        name:"nav-anchor",
        data(){
            return {
                visible:true,
                scrollIndex:0
            }
        },
        created(){
            this.$store.state.navAnchor = this;
        },
        methods:{
            jump:function (selector) {
                this.$store.dispatch("viewerScrollTo",document.querySelector(selector).offsetTop)
            },
            scrollTo(id){
                this.scrollIndex = id;
            }
        },
        watch:{
            "$store.state.contentCurrentTop":function (top) {
                let offset = 100;
                let tops = this.$store.state.contentElementsTops;
                tops.some((it,id)=>{
                   if(top<=it+offset){
                       this.scrollTo(id);
                       return true
                   }
                });
            }
        },
        computed:{
            reqHeader(){
                return this.$store.state.interfaceInfo.reqHeaders
            },
            resHeader(){
                return this.$store.state.interfaceInfo.resHeaders
            }
        }
    }
</script>
<style lang="sass" rel="stylesheet/scss">
    #right-side-nav{
        position: fixed;
        top: 100px;
        left: 940px;
        font-size: 12px;
        ul{
            margin: 0;
            padding: 0;
            float: left;
            li{
                color: #aaaaaa;
                padding: 5px 15px;
                list-style: none;
                border-left: 2px double #fff;
                &:hover,&.active{
                    color: #25AEB3;
                    border-left: 2px double #25AEB3;
                    cursor: pointer;
                }
            }
        }
    }
</style>