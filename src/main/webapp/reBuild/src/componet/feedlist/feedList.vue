<template>
    <div v-cloak :style="'display:'+ visible?'block':'none'">
        <div id="feedList">
            <div v-for="feed in feedList" class="block">
                <h4 class="oper">{{feed.oper}}<span class="ts">{{feed.ts}}</span></h4>
                <p  v-for="detail in feed.changesList">
                    <span v-if='detail.optionType == "修改"' class="mod">{{detail.optionType}}</span>
                    <span v-if='detail.optionType == "新增"' class="new">{{detail.optionType}}</span>
                    <span v-if='detail.optionType == "删除"' class="del">{{detail.optionType}}</span> 了
                    <span><router-link :to="{path:'/view/'+detail.ifId}">[{{detail.ifId}} {{detail.ifName}}] </router-link> </span>
                    <span v-if="detail.changeType">的 {{detail.changeType}}</span>
                </p>
            </div>
        </div>
    </div>
</template>

<script>
    import api from '../../../lib/api.js';
    import urlParam from '../../../lib/urlParam';

    export default{
        name:"feed-list",
        data(){
            return{
                feedList:[]
            }
        },
        created(){
            this.getRecentFeeds()
        },
        methods:{
            getRecentFeeds(){
                // 获取feed信息
                api.ifsys.post("getIfsysFeed.do",{
                    'pid':urlParam.PID,
                    limit:200
                },(data)=>{
                    this.feedList = data['feedsList']
                });
            }
        }
    }
</script>
<style lang="sass" rel="stylesheet/scss">
    #feedList {
        width: 800px;
        float: left;
        margin-left: 15px;
        .ts{float: right}
        .block{
            border-bottom: 1px solid #ccc;
            margin-bottom: 15px;
        }
        .mod{color:#2cb6bb!important;}
        .new{color:#53bb57}
        .del{color:#ff4e44}
        .ifName{color: #2cb6bb}
    }
</style>