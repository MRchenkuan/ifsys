<template>
    <!-- 系统产品添加面板 -->
    <div id="memberAdder" class="modal fade">
        <div class="modal-dialog" style="margin-top: 150px">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title"><span class="glyphicon glyphicon-user"></span> 添加成员</h4>
                </div>
                <div class="table modal-body">
                    <div class="form-control searchBar">
                        <input type="text" v-model="email" class="form-control" placeholder="请输入邀请用户的邮箱">
                        <div v-if="selectedUser" class="selectedUser form-control">
                            <div v-if="selectedUser.avtSrc" class="header " :style="'background-image:url('+selectedUser.avtSrc+')'"></div>
                            <div v-if="!selectedUser.avtSrc" class="header " style="background-color:grey"></div>
                            <span><{{selectedUser.email}}></span>
                            <span>{{selectedUser.userName}}</span>
                            <span class="glyphicon glyphicon-remove-sign text-danger" @click="removeUser"></span>
                        </div>
                    </div>

                    <ul class="list-group">
                       <li class="list-group-item" v-for="user in result" @click="select(user)">
                           <div v-if="user.avtSrc" class="header " :style="'background-image:url('+user.avtSrc+')'"></div>
                           <div v-if="!user.avtSrc" class="header " style="background-color:grey"></div>
                           <span><{{user.email}}></span>
                           <span>{{user.userName}}</span>
                       </li>
                    </ul>
                    <div class="memberType">
                        项目权限（不会告知用户）：
                        <label for="visitor">
                            <input type="radio" id="visitor" value="visitor" v-model="memberType">
                            访客
                        </label>
                        <span>&nbsp;</span>
                        <label for="member">
                            <input type="radio" id="member" value="member" v-model="memberType">
                            项目成员
                        </label>
                    </div>
                    <h6 v-show="information" class="text-danger text-center">{{information}}</h6>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-theme" @click="doAdd" :disabled="btnDisable" >发送邀请</button>
                </div>
            </div>
        </div>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss" scoped>
    .memberType{
        margin-top: 15px;
        margin-left: 12px;
    }
    .header{
        background-position: 50%;
        background-size: cover;
        background-repeat: no-repeat;
        height: 100%;
        width: 4rem;
        position: absolute;
        float: left;
        margin-right: 1rem;
        top: 0;
        left: 0;
    }
    .list-group-item{
        padding-left: 5rem;
    }
    .searchBar{
        position: relative;
        padding: 0;
        border:none;
        .selectedUser{
            position: absolute;
            top:0;
            left:0;
            padding-left: 5rem;
        }
    }
</style>
<script>
    var jQuery,$;
    jQuery = $ = require('../../../static/js/3rd/jquery.js');
    import api from '../../../lib/api';
    import {alertEvent} from '../../../lib/globalEvent';
    import urlParam from '../../../lib/urlParam';

    export default {
        name:"memberAdder",
        data(){
            return {
                modal:null,
                email:"",
                information:"",
                memberType:"visitor",
                btnDisable:false,
                result:[],
                selectedUser:null
            }
        },
        mounted(){
            this.modal =  $("#memberAdder");
        },
        methods: {
            show () {
                if(this.modal)
                    this.modal.modal("show");
                this.btnDisable = false;
            },
            doAdd(){
                this.email = this.email.trim();
                this.information="";
                this.btnDisable=true;
                if(this.selectedUser){
                    // 直接保存用户
                    console.log(this.selectedUser)
                }else if(this.email.match(/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/g)){
                    // 用户不存在，发送邮件
                    api.ifsys.post('addProjectMember.do',{},(data)=>{
                        console.log(data)
                    })
                    console.log(this.email)
                }else{
                    this.information="邮箱格式不正确"
                }
            },
            select(user){
                this.selectedUser = user;
                this.clearSearchBar()
            },
            search(key){
                if(key){
                    api.ifsys.post('searchUsers.do',{key:this.email}, (data)=> {
                        this.result = data['dataes']
                    })
                }else{
                    this.result = [];
                }

            },
            removeUser(){
                this.selectedUser = null;
            },
            clearSearchBar(){
                this.result=[];
                this.email=""
            }
        },
        watch:{
            email:function (newVal) {
                this.search(newVal);
            }
        }
    }
</script>