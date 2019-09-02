<template>
    <div class="panel panel-default ifInfoBlock">
        <h5 class="themesColor"><span class="glyphicon glyphicon-calendar"></span> 变更记录:<span style="float: right" class="label label-default change-log-switch" @click="toggleChangeLog()"><span class="glyphicon glyphicon-eye-open"></span> 展开/收缩</span></h5>
        <div style='overflow:hidden;' v-show="changeLogsShow">
            <div v-for="changelog in changeLogs" class="change-log-box">
                <h5 class="oper"><span class="glyphicon glyphicon-user"></span> {{changelog.oper}}</h5>
                <div v-for="changes in changelog.changesList">
                    <p>
                        <span v-if="changes.optionType=='新增'" class='changes-optionType create'>新增</span>
                        <span v-if="changes.optionType=='删除'" class='changes-optionType delete'>删除</span>
                        <span v-if="changes.optionType=='修改'" class='changes-optionType mod'>修改</span>
                        了 {{changes.changeType || "接口"}}<span class="ts">{{changes.ts}}</span>
                    </p>
                    <p class="text-center" v-if="changes.prmVal || changes.nowVal">
                        '{{changes.prmVal}}' → '{{changes.nowVal}}'
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import api from '../../../../lib/api.js';
    import utils from  '../../../../lib/utils'
    export default {
        name:'change-logs',
        data(){
            return {
                changeLogs:[],
                changeLogsShow:false
            }
        },
        methods:{
            getChangeLogs(){
                let ifId = this.$route.params.ifId;
                if(ifId<0)return ;
                // 变更记录
                api.ifsys.post("getInterfaceChanges.do", {"ifId":ifId}, (data)=> {
                    this.changeLogs = data['feedsList'];
                });
            },
            toggleChangeLog(){
                this.changeLogsShow = !this.changeLogsShow
            }
        },
        created(){
            this.getChangeLogs()
        },
        watch:{
            "$route":function () {
                this.getChangeLogs()
            }
        }
    }
</script>

<style lang="sass" rel="stylesheet/scss">
    .change-log-switch{
        background: #607d8b;
    }

    .change-log-box {
        .ts {
            float: right
        }
        .oper {
            background: #87afc3;
            color: #fff;
            padding: 1px 5px;
            border-radius: 5px;
            font-weight: 100;
        }
    }
    .changes-optionType{
        padding: 1px 5px;
        color: #fff;
        background: #aaaaaa;
        border-radius: 5px;
        &.create{
            background: #55b959;
        }
        &.delete{
            background: #ff6653;
        }
        &.mod{
            background: #25AEB3;
        }
    }
</style>