<template>
    <!-- 系统产品添加面板 -->
    <div id="createSysAndPro" class="modal fade">
        <div class="modal-dialog" style="margin-top: 150px">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title">新增系统、产品</h4>
                </div>
                <div class="table modal-body">
                    <template v-if="!isPro">
                        <input class="form-control text-center"  style="margin: 0 auto;width: 60%;" placeholder="系统名称" v-model="sysName">
                    </template>
                    <template v-if="isPro">
                        <div class="input-group" style="margin:0 auto">
                            <input class="form-control text-center" disabled placeholder="要添加的系统名称" v-model="sysName" style="width: 50%;float: left">
                            <input class="form-control text-center" placeholder="填写产品名称" v-model="proName" style="width: 50%;float: left">
                        </div>
                    </template>
                    <input class="form-control text-center" style="margin: 1rem auto;width: 60%;" placeholder="描述" v-model="remark">
                    <h6 class="text-danger text-center">{{information}}</h6>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-theme" @click="doAddSysPro">确定添加</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    var jQuery,$;
    jQuery = $ = require('../../../static/js/3rd/jquery.js');
    import api from '../../../lib/api';
    import {alertEvent} from '../../../lib/globalEvent';
    import urlParam from '../../../lib/urlParam';

    export default {
        name:"createSysAndProFrame",
        data(){
            return {
                modal:null,
                sysName:"",
                isPro:false,
                sysId:0,
                proName:"",
                information:"",
                remark:"",
            }
        },
        mounted(){
          this.modal =  $("#createSysAndPro");
        },
        methods: {
            show: function () {
                if(this.modal)
                    this.modal.modal("show")
                this.$parent.isListShow=false;
            },
            doAddSysPro: function () {
                var sysList = this.$parent;
                var self = this ;
                var isPro = self.isPro;
                var proName = self.proName;
                var sysId = self.sysId;
                var remark = self.remark;
                var sysName = self.sysName;

                // 新增产品
                if (isPro) {
                    if (sysId < 0) return self.information = "系统ID为空";
                    if (!proName) return self.information = "请填写产品名";
                    if (!remark) return self.information = "请填写产品描述";
                    // 提示

                    alertEvent.$emit('confirm',"是否在《" + sysName + "》下新增产品:" + proName, function () {
                        //,然后发送请求
                        api.ifsys.post("addInterfaceSystem.do",{
                            pid:urlParam.PID,
                            sysId:sysId,
                            proName:proName,
                            remark:remark
                        },function (data) {
                            sysList.sysList = data["sysList"];
                            sysList.allProList = data["proList"];
                            self.modal.modal("hide")
                        },function (data) {
                            self.information = data['rspInf']
                        })
                    }, function () {
                        return false;
                    })
                }

                // 新增系统
                if (!isPro) {
                    if (!sysName) return self.information = "请填写系统名";
                    if (!remark) return self.information = "请填写系统描述";
                    // 提示
                    alertEvent.$emit('confirm',"是否新增系统:" + sysName, function () {
                        //,然后发送请求
                        api.ifsys.post("addInterfaceSystem.do",{
                            pid:urlParam.PID,
                            sysName:sysName,
                            remark:remark
                        },function (data) {
                            sysList.sysList = data["sysList"];
                            sysList.allProList = data["proList"];
                            self.modal.modal("hide")
                        },function (data) {
                            self.information = data['rspInf']
                        })
                    }, function () {
                        return false;
                    })
                }
            }
        }
    }
</script>