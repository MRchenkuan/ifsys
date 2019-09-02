<template>
    <!-- alert界面提示 -->
    <div class="modal" id="alert" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm" style="margin-top: 250px">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">{{title}}</h4>
                </div>
                <pre class="alertText modal-body">{{message}}</pre>
                <div class="modal-footer">
                    <button v-if="falseButtonVisible" type="button" class="btn btn-default" @click="falseButton">{{falseButtonText}}</button>
                    <button v-if="trueButtonVisible" type="button" class="btn btn-theme" @click="trueButton">{{trueButtonText}}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="sass" rel="stylesheet/scss">
    #alert{
        z-index: 9999;
        .alertText{
            background: #fff;
            overflow: hidden;
            text-align: left;
            padding: 20px;
            margin:0;
            border:none;
            font-family: Consolas,"Liberation Mono",Menlo,Courier,monospace;
        }
    }
</style>

<script>
    import $ from '../../../static/js/3rd/jquery.js';
    import {alertEvent} from '../../../lib/globalEvent';
    var alertTool;
    export default{
        created(){
            // 弹出框
            alertEvent.$on('alert',(message, actionTrue)=>{
                alertTool.alert(message, actionTrue)
            });

            // 确认框
            alertEvent.$on('confirm',(message, actionTrue, actionFalse)=>{
                alertTool.confirm(message, actionTrue, actionFalse)
            });
        },
        mounted(){
            // 挂载之后才能确定弹出框实体
            this.alertWidget = $("#alert");
        },
        data(){
            alertTool = this;
            return {
                message: "提醒",
                title: "提醒",
                alertWidget: null,
                trueButtonText: "确定",
                falseButtonText: "取消",
                trueButtonVisible: true,
                falseButtonVisible: true,
                trueButtonAction: null,
                falseButtonAction: null,
            }
        },
        methods:{
            alert: function (message, actionTrue) {
                if (message)alertTool.message = message;
                alertTool.falseButtonVisible = false;
                alertTool.trueButtonVisible = true;
                alertTool.falseButtonAction = null;
                alertTool.trueButtonAction = null;
                alertTool.alertWidget.modal('show');
                if (actionTrue)alertTool.trueButtonAction = actionTrue;
            },
            confirm: function (message, actionTrue, actionFalse) {
                alertTool.falseButtonVisible = true;
                alertTool.trueButtonVisible = true;
                if (!(actionTrue))alert("confirm组件未定义确认事件");
                if (message)alertTool.message = message;
                alertTool.trueButtonAction = actionTrue;
                if (alertTool.falseButtonAction) {
                    alertTool.falseButtonAction = actionFalse;
                } else {
                    alertTool.falseButtonAction = function () {
                        alertTool.alertWidget.modal('hide')
                    }
                }
                alertTool.alertWidget.modal('show');
            },
            dismiss:function(){
                alertTool.alertWidget.modal('hide');
            },
            trueButton:function(){
                if(alertTool.trueButtonAction)alertTool.trueButtonAction();
                alertTool.alertWidget.modal('hide');
            },
            falseButton:function(){
                if(alertTool.falseButtonAction)alertTool.falseButtonAction();
                alertTool.alertWidget.modal('hide');
            }
        }
    }

</script>