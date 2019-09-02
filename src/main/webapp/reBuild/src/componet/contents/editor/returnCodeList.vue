<template>
    <div id="editor_returnCodeList" class="panel panel-default ifInfoBlock">
        <h5 class="themesColor panel-body" style="overflow: hidden">
            <span class="glyphicon glyphicon-book"></span> 返回码定义：
            <span class="btn-group addItems btn-group-xs">
                    <button @click="addNewRspCd" title="增加返回码" class="btn btn-default" ><span class="glyphicon glyphicon-plus"></span></button>
                    <button @click="addNewRspCdFromJson" id="addRspCdJson" class="btn btn-default"><span class="glyphicon glyphicon-edit"></span> 批量添加</button>
                </span>
        </h5>
        <table id="editor_returnCodeList_data" class="table table-bordered">
            <tbody>
                <tr v-for="(returnCode,i) in returnCodeList">
                    <td><input placeholder="请填写返回码" type="text" title="code" class="" v-model="returnCode.rspCode"></td>
                    <td><input placeholder="请填写rspInf" type="text" title="info" class="" v-model="returnCode.rspCodeDesc"></td>
                    <td style="line-height: 30px"><button title="删除返回码" class="btn btn-danger btn-xs" @click="deleteCode(i)" ><span class="glyphicon glyphicon-minus"></span></button></td>
                </tr>
            </tbody>
        </table>
    </div>

</template>
<style lang="sass" rel="stylesheet/scss">
    #editor_returnCodeList_data {
        td{
            padding: 0;
            text-align: center;
        }
        button{
            /*width: 100%;*/
            float: none;
        }
        input {
            width: 100%;
            line-height: 28px;
            border: none;
            text-align: center;
        }
    }
</style>
<script>

    export default{
        data(){
            var isCreator = this.$props.initType == 'creator';

            return {
                returnCodeList: isCreator ? this.$store.state.creatorData.returnCodeList:this.$store.state.returnCodeList,
                prmSummaryBak : isCreator ? [] : this.$store.state.backupFields.returnCodeFields,
                type:"RSP_CODE"
            }
        },
        props:['initType'],
        methods:{
            addNewRspCd(){
                var self = this;
                var nowList = self.returnCodeList;
                var newObj= {
                    id:"",
                    ifId:self.$store.state.interfaceInfo.basicInfo.ifId,
                    rspCode:'',
                    rspCodeDesc:''};
                var editingCode = {};
                var ifexist = nowList.some(function(it){
                    if(it.rspCode == newObj.rspCode || it.rspCodeDesc == newObj.rspCodeDesc){
                        editingCode = it;
                        return true;
                    }else{
                        return false;
                    }
                });
                if(!ifexist){
                    this.returnCodeList.unshift(newObj);
                }else{
                    return this.$store.dispatch('alert',"请完善编辑中的返回码:\n\trspCd["+(editingCode.rspCode||"空")+"]"+" rspInf["+(editingCode.rspCodeDesc||"空")+"]");
                }
            },
            addNewRspCdFromJson(){
                var self = this;
                self.$store.dispatch('fillDatesToJsonExporter',this);
            },
            deleteCode(index){
                var nowList = this.returnCodeList;
                this.returnCodeList = nowList.slice(0,index).concat(nowList.slice(index+1))
            },
            // 返回码列表Json化
            stringifyFields : function(){
                var obj = {};
                this.returnCodeList.some(function(it){
                    obj[it.rspCode] = it.rspCodeDesc;
                });
                return JSON.stringify(obj,null,3);
            }
        }
    }
</script>