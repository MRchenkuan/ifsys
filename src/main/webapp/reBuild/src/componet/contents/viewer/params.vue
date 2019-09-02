<template>
    <div class="panel panel-default ifInfoBlock">
        <h5 class="themesColor">
            <template v-if="type=='REQ'"><span class="glyphicon glyphicon-cloud-upload"></span> 请求实例(Request Body):</template>
            <template v-if="type=='RSP'"><span class="glyphicon glyphicon-cloud-download"></span> 响应实例(Response Body):</template>
            <div class="jsonview-switch btn-group btn-group-xs">
                <button type="button" class="btn btn-default" :class="uiInfo.fieldsViewType=='CND'?'active':''" @click="switchView('CND')">{;}</button>
                <button type="button" class="btn btn-default" :class="uiInfo.fieldsViewType=='CNT'?'active':''" @click="switchView('CNT')">
                    <span class="glyphicon glyphicon-indent-left"></span>
                </button>
            </div>
        </h5>

        <template v-if="uiInfo.fieldsViewType=='CND'">
            <pre>{{paramsBody || "无"}}</pre>
        </template>

        <template v-if="uiInfo.fieldsViewType=='CNT'">
            <pre>{{getFieldsPureString(fieldList) || "无"}}</pre>
            <table class="table table-bordered">
                <tbody>
                    <tr>
                        <th>参数名</th>
                        <th>说明</th>
                        <th>类型</th>
                        <th v-if="type=='REQ'">必须</th>
                    </tr>
                    <tr v-for="field in getRenderedFields(fieldList)">
                        <td  :style="'padding-left:'+(5+(field._indent)*30) +'px;color:'+field._color">{{field.k}}</td>
                        <td>{{field.note}}</td>
                        <td>{{typeDic(field.type)}}</td>
                        <td v-if="type=='REQ'">{{field.req?"是":"否"}} <span v-if="!field.req" class="symbol1 themesColor"></span></td>
                    </tr>
                </tbody>
            </table>
        </template>
    </div>
</template>

<script>
    import fieldsTools from '../../../../lib/fieldsTools';
    import utils from '../../../../lib/utils';
    import urlParam from '../../../../lib/urlParam';

    export default {
        name:'params',
        props:['fieldList','paramsBody','type'],
        data(){
            var self = this;
            return {
                rspBodyPure:"",
                uiInfo:{
                    fieldsViewType:self.type=="REQ"?urlParam.reqFieldsViewType:urlParam.rspFieldsViewType
                },
                paramsBodyPure:""
            }
        },
        methods:{
            typeDic:function(index){
                var data = {
                    1:"STRING",
                    2:"NUMBER",
                    3:"OBJ",
                    4:"LIST[OBJ]",
                    5:"BOOL",
                    6:"NULL",
                    7:"ARRAY"
                };
                return data[index]
            },
            switchView : function(type){
                var self = this;
                self.uiInfo.fieldsViewType=type;
                if(self.type=="REQ"){
                    utils.setCookie("reqFieldsViewType",type||'CNT');
                }else{
                    utils.setCookie("rspFieldsViewType",type||'CNT');
                }

            },
            getFieldsPureString(fieldList){
                // 深度拷贝对象，防止对象递归时触发渲染
                var _fieldList = utils.deepCopyArrayObject(fieldList);
                return fieldsTools.stringifyFields(_fieldList);
            },
            getRenderedFields(fieldList){
                var _fieldList = utils.deepCopyArrayObject(fieldList);
                fieldList =  fieldsTools.addStyle2Fields(_fieldList);
                return fieldList;
            }
        }
    }
</script>

<style>
    .jsonview-switch{
        float: right;
    }
    .jsonview-switch button{
        color:#607d8b;
    }
    .necessary,.symbol1{position: relative}
    .necessary:before,.symbol1:before{
        content: "*";
        color: #ff4e44;
        font-size: 12px;
        position: absolute;
        margin-left: 5px;
        position: absolute;
        top: -7px;
        right: -9px;
    }
    .symbol1:before{color:#31babf}
    .btn:focus{outline: 0!important;}
</style>