<template>
    <div class="panel panel-default ifInfoBlock">
        <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" @click="toggleReturnCodeDisplay('DEF')" :class='returnCodeDisplay=="DEF"?"active":""'>
                <a class="themesColor">
                    <span class="glyphicon glyphicon-book"></span> 返回码定义：
                </a>
            </li>
            <li role="presentation" @click="toggleReturnCodeDisplay('HIS')" :class='returnCodeDisplay=="HIS"?"active":""'>
                <a class="themesColor">
                    <span class="glyphicon glyphicon-dashboard"></span> 其他返回码：
                </a>
            </li>
        </ul>

        <div v-if="returnCodeDisplay=='DEF'" style="margin-top: 10px">
            <p v-for="returnCode in returnCodeList" class="basicInfo" :data-rspCdId="returnCode.id">
                <span class="code">{{returnCode.rspCode}}</span>
                <span>{{returnCode.rspCodeDesc}}</span>
            </p>
        </div>


        <table v-if="returnCodeDisplay=='HIS'" class="table table-bordered" >
            <tbody>
            <tr>
                <th>返回码</th>
                <th>定义</th>
                <th>出现次数</th>
                <th>最近出现</th>
            </tr>
            <tr v-for="returnCodeHis in returnCodeHistories">
                <td>{{returnCodeHis.RSP_CODE}}</td>
                <td>{{returnCodeHis.RSP_CODE_DESC}}</td>
                <td>{{returnCodeHis.COUNT}}</td>
                <td>{{returnCodeHis.TS}}</td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
    import api from '../../../../lib/api.js';
    import utils from  '../../../../lib/utils'
    export default {
        name:'return-code-list',
        props:['returnCodeList',"ifId"],
        data(){
            return {
                returnCodeDisplay:'DEF',
                returnCodeHistories:[]
            }
        },
        methods:{
            toggleReturnCodeDisplay(type){
                this.returnCodeDisplay = type;
            },
            getReturnCodeHistories(){
                let ifId = this.$route.params.ifId;
                if(ifId<0)return ;
                // 历史返回码信息
                api.autotest.post("getHistoricalReturnCode.do", {"ifId":ifId}, (data) => {
                    this.returnCodeHistories = data['dataes'];
                    this.returnCodeHistories.forEach(function(it){
                        it.TS = utils.tmStampToTime(it.TS)
                    })
                });
            }
        },
        created(){
            this.getReturnCodeHistories()
        },
        watch:{
            "$route":function () {
                this.getReturnCodeHistories()
            }
        }
    }
</script>

<style lang="sass" rel="stylesheet/scss">
    .basicInfo span{color:#666}
    .basicInfo label,.basicInfo .glyphicon{color:#aaa}
    .basicInfo .code{margin: 0 10px;}
    ul[role='tablist']{
        padding: 5px 0 0 5px;
    }
</style>