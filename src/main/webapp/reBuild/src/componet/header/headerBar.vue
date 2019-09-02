<template>
    <nav id="nav" class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="#">独孤九剑接口管理系统 <span id="loadingTag"
                                                                         class="glyphicon glyphicon-refresh"
                                                                         style="display: none"></span></a>
            </div>

            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <!-- 搜索控件 -->
                <search-bar></search-bar>
                <!-- 其他按钮 -->
                <ul v-cloak class="nav navbar-nav">
                    <!-- 系统切换按钮 -->
                    <li class="navbar-btn">
                        <button id="btn-switchList" class="btn btn-theme" type="button" @click="toggleSysList()">
                            <span class="glyphicon glyphicon-tower"></span>&nbsp;切换系统&nbsp;<span class="caret"></span>
                        </button>
                        <sys-list ref="sysList"></sys-list>
                    </li>
                    <!-- 接口创建按钮 -->
                    <create-btn v-cloak v-if="btnCreateIf.visible" class="navbar-btn btn-group"></create-btn>
                    <!-- 接口保存按钮 -->
                    <li class="navbar-btn" id="interfaceControl" style="margin-left: 15px">
                        <saveIfBtn v-if="editorController.btnController.visible"></saveIfBtn>
                    </li>
                    <!-- 接口版本按钮 -->
                    <li class="navbar-btn" style="margin-left: 15px">
                        <editions-btn></editions-btn>
                    </li>
                    <li class="navbar-btn" style="margin-left: 15px">
                        <project-info-btn></project-info-btn>
                    </li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>
</template>

<script>
    const searchBar = resolve => require(['./searchBar.vue'], resolve)
    const sysList = resolve => require(['./sysList.vue'], resolve)
    const createBtn = resolve => require(['./createBtn.vue'], resolve)
    const saveIfBtn = resolve => require(['./saveBtn.vue'], resolve)
    const editionsBtn = resolve => require(['./editionsBtn.vue'], resolve)
    const projectInfoBtn = resolve => require(['./projectInfoBtn.vue'], resolve)

//    import searchBar from './searchBar.vue'
//    import sysList from './sysList.vue'
//    import createBtn from './createBtn.vue'
//    import saveIfBtn from './saveBtn.vue'
//    import editionsBtn from './editionsBtn.vue'

    export default {
        name: "header-bar",
        components: {
            sysList,
            searchBar,
            createBtn,
            saveIfBtn,
            editionsBtn,
            projectInfoBtn
        },
        data(){
            return {
                btnCreateIf: this.$store.state.header.btnCreateIf,
                editorController: this.$store.state.header.editorController,
                saveBtnStatues: this.$store.state.header.saveBtnStatues,
                editions: this.$store.state.header.editions,
                ifObj: this.$store.state.header.ifObj
            }
        },
        methods: {
            // 初始化编辑框
            toggleSysList: function () {
//                    sysList.methods.toggle();
                this.$refs.sysList.toggle();
            }
        }
    }
</script>
<style lang="sass" rel="stylesheet/scss">
    #nav {
        height: 40px;
        width: 100%;
        margin: 0;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 9999;
    }

    #nav_path {
        top: 0;
        position: absolute;
        width: 100%;
        z-index: 99;
        padding: 0;
    }

    #nav_path .breadcrumb {
        margin: 0;
        /*padding: 11px 15px;*/
    }

    .navbar-default {
        background-image: linear-gradient(to bottom, #40cbd0 0, #25aeb3 100%);
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#40cbd0', endColorstr='#25aeb3', GradientType=0);
        filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
    }

    .navbar-default .navbar-nav > li > a, .navbar-default .navbar-brand {
        color: #fff;
        text-shadow: 0 1px 0 rgba(6, 6, 6, 0.25);
        &:hover{
            color:#fff
        }
    }

    .navbar-default .navbar-nav > .open > a, .navbar-default .navbar-nav > .open > a:hover, .navbar-default .navbar-nav > .open > a:focus {
        color: #258488;
    }
</style>
