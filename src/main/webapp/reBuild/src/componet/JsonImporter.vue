<template>
    <div id="editor_modal_box" class="modal fade">
        <div class="modal-dialog" style="margin-top: 65px;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title">{{title}}</h4>
                </div>
                <div class='modal-body form-group' :class='isJsonValid.style'>
                    <label class="control-label" v-model="isJsonValid.text">{{isJsonValid.text}}</label>
                    <pre id="editor_modal_box_content" style="display: none" class="form-control" @click="focusEditor"></pre>
                    <textarea id="editor_modal_box_value" class="form-control" title="文本域" v-model="jsonStr" @blur="JSONCheck">{{jsonStr}}</textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button id="importJson" type="button" class="btn btn-primary" @click="doImport">导入</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- 弹出框结束 -->
</template>
<script>
    // 弹出框控件
    import fieldsTools from '../../lib/fieldsTools'
    var jQuery,$;
    jQuery = $ = require('../../static/js/3rd/jquery.js');
    export default{
        name:"json-importer",
        mounted(){
            this.modal = $('#editor_modal_box');
            this.displayer = $('#editor_modal_box_content');
            this.editor = $('#editor_modal_box_value');
        },
        data(){
            return this.$store.state.jsonImporter;
        },
        watch:{
            "handler.fieldList":function () {
                this.$store.commit("checkJSONImporter")
            }
        },
        methods:{
            JSONCheck: function () {
                this.$store.commit("checkJSONImporter")
            },
            focusEditor(){
                this.displayer.hide();
                this.editor.show().focus();
            },
            doImport(){
                this.JSONCheck();
                if(this.isJsonValid.state == true ){
                    this.type =="RSP_CODE" ? this.fillJSONAsRspCds(): this.fillJSONAsItems();
                    this.modal.modal('hide')
                }
            },
            /**
             * 导入参数方法
             */
            fillJSONAsItems(){
                var handler = this.handler;
                var self = this;
                var JSONObj = eval("("+self.jsonStr+")");
                // 编辑框中,列表化的field数据
                var fieldList = fieldsTools.JSON2relatedList(JSONObj);
                // 已有的field数据
                var existFieldList=handler.fieldList;

                // 去重
                var filteredField = [];
                fieldList.forEach(function(now){
                    var now_parent = now.parent.replace(" ","");
                    var now_k = now.k.replace(" ","");
                    var isExist = existFieldList.some(function(prm,id,ar){
                        var prm_parent = prm.parent.replace(" ","");
                        var prm_k = prm.k.replace(" ","");
                        var prm_req = prm.req;
                        var prm_note = prm.note;
                        // 存在相等的
                        if(prm_parent == now_parent && prm_k == now_k){
                            //相等则更新,顺便除空格 xxxxxxx 不能用原始信息,因为路径已经发生改变 x.x
                            now.id = prm.id;// ID不变
                            now.req = prm_req; // 勾选状态不变
                            now.note = prm_note; // 字段说明不变
                            // 保存已有的字段
                            filteredField.push(now);
                            return true
                        }else{
                            return false;
                        }
                    });
                    // now 为新增的字段
                    if(!isExist){
                        // 进一步判断,新增字段是否和原始数据存在一致
                        // 此操作主要是为了避免同一字段改来改来改去最后ID丢失的情况
                        var prmExist = handler.prmSummaryBak.some(function(primItem){
                            if(primItem.parent == now_parent &&  primItem.k.replace(" ","")== now_k){
                                now.id = primItem.id;
                                now.req = primItem.req;
                                now.note = primItem.note;
                                filteredField.push(now);
                                return true
                            }else{
                                return false
                            }
                        });
                        // 不相等则收集传出
                        if(!prmExist){
                            now.id="";
                            filteredField.push(now);
                        }

                    }
                });
                // 重设参数列表
                handler.fieldList = filteredField;
                handler.reRenderFields();
            },
            /**
             * 导入返回码方法
             */
            fillJSONAsRspCds(){
                var handler = this.handler;
                var self = this;
                var existCode = handler.returnCodeList;

                var JSONObj = eval("("+self.jsonStr+")");
                var nowCode = [];
                for(var codekey in JSONObj){
                    if(JSONObj.hasOwnProperty(codekey)){
                        var code = {
                            id:"",
                            ifId : self.$store.state.interfaceInfo.basicInfo.ifId,
                            rspCode:codekey,
                            rspCodeDesc:JSONObj[codekey]
                        };
                        nowCode.push(code);
                    }
                }

                nowCode.forEach(function(now){
                    var now_code = now.rspCode;
                    var isExist = existCode.some(function(prm){
                        var prm_code = prm.rspCode;
                        var prm_id = prm.id;
                        var prm_ifId = prm.ifId;
                        // 发现已有,则将prm更新
                        if(now_code == prm_code){
                            now.id = prm_id;
                            now.ifId = prm_ifId;
                            return true;
                        }else {
                            return false;
                        }
                    });
                    // 进一步判断是否与初值相同
                    if(!isExist){
                        handler.prmSummaryBak.forEach(function(bak){
                            if(now_code == bak.rspCode){
                                now.id = bak.id;
                                now.ifId = bak.ifId;
                            }
                        })
                    }
                });
                handler.returnCodeList = nowCode;
            }
        }
    }
</script>
<style>
    #editor_modal_box textarea, #editor_modal_box_content {
        min-height: 420px;
    }

    .modal {
        z-index: 99999
    }
</style>