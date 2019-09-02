<template>
    <div class="dropdown">
        <button class="btn btn-theme" id="dLabel" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <span class="glyphicon glyphicon-cog"></span> 项目
            <span class="caret"></span>
        </button>
        <div class="panel panel-default dropdown-menu" aria-labelledby="dLabel">
            <!-- Default panel contents -->
            <div class="panel-body">
                <span>项目成员({{memberList.length}})</span>
                <hr>
                <a v-for="member in memberList" class="member">{{member.userName}}</a>
                <a class="badge" @click="addMember">添加成员 +</a>
            </div>
            <hr>
            <!--<button class="btn btn-theme btn-block">返回所有项目</button>-->
            <a class="text-center back-off-btn" href="./projects.html">返回所有项目 <span class="glyphicon glyphicon-share-alt"></span></a>
        </div>
        <member-adder ref="memberAdder"></member-adder>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss" scoped>
    .dropdown-menu{
        padding: 0;
    }
    hr{
         margin-top: 0;
         margin-bottom: 5px;
    }
    .dropdown-menu{
        min-width: 300px;
        box-shadow: 0 0 10px #ccc;
    }
    .member{
        color: #25aeb3;
        padding-right: 5px;
        font-size: 12px;
        font-weight: 100;
    }
    .back-off-btn{
        display: block;
        color:grey;
        padding: 5px;
        &:hover{
            color:#25aeb3
        }
    }
</style>
<script>
    import api from '../../../lib/api.js';
    import urlParam from '../../../lib/urlParam.js';
    import memberAdder from './memberAdder.vue';
    export default{
        name:"project-info-btn",
        data(){
            return {
                memberList : []
            }
        },
        methods:{
            addMember(){
                let memberAdder = this.$refs.memberAdder;
                memberAdder.show();
            },
            init(){
                // 初始化invoker插件
                api.ifsys.post("getMemberList.do",{
                    'pid':urlParam.PID
                },(data)=> {
                    this.memberList
                            = this.$store.state.header.projectInfoBtn.memberList
                            = data["userInfos"];
                })
            }
        },
        components:{
            memberAdder
        },
        created(){
            this.init();
        },
        watch:{
            "$route":function (route) {
                var pid= route.params.pid;
                if(pid){
                    this.init()
                }
            }
        }

    }
</script>