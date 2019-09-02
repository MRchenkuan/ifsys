<template>
    <div class="panel panel-default ifInfoBlock">
        <h5 class="themesColor panel-body"><span class="glyphicon glyphicon-calendar"></span> 关注列表:
            <span style="float: right" class="label label-default change-log-switch" @click="toggleInvokeList()"><span class="glyphicon glyphicon-eye-open"></span> 展开/收缩</span></h5>
        <table class="table table-bordered" v-show="invokeListShow">
            <tbody>
                <tr>
                    <th>关注者</th>
                    <th>说明</th>
                    <th>关注时间</th>
                </tr>
                <tr v-for="invoke in invokers">
                    <td>{{invoke.userName}}</td>
                    <td>{{invoke.remark}}</td>
                    <td>{{invoke.tmFollow}}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
    export default {
        name:'invoker-list',
        data(){
            return {
                invokeListShow:true,
            }
        },
        methods:{
            toggleInvokeList(){
                this.invokeListShow = !this.invokeListShow
            },
        },
        computed:{
            invokers(){
                // 直接放到data里居然关联不上，恶心
                return this.$store.state.invokers
            }
        }
    }
</script>