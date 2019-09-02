<template>
    <div v-show="headers.length>0" class="panel panel-default ifInfoBlock">
        <h5 class="themesColor">
            <template v-if="type=='REQ'"><span class="glyphicon glyphicon-cloud-upload"></span> 请求头(Request Headers):</template>
            <template v-if="type=='RSP'"><span class="glyphicon glyphicon-cloud-download"></span> 响应头(Response Headers):</template>
        </h5>
        <table class="table table-bordered">
            <tbody>
            <tr>
                <th>HEADER</th>
                <th>说明</th>
                <th>示例</th>
                <th v-if="type=='REQ'">必须</th>
            </tr>
            <tr v-for="header in headers">

            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
    import api from '../../../../lib/api.js';
    import utils from  '../../../../lib/utils'
    export default {
        name:'header-list',
        props:['type'],
        data(){
            return {
                headers:[]
            }
        },
        methods:{},
    }
</script>

<style lang="sass" rel="stylesheet/scss" scoped>
    table.table.table-bordered{
        border-top: 1px solid #ddd;
    }
</style>