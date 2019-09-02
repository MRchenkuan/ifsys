<template>
    <div v-cloak id="nav_path" class="list-group-item disabled">
        <ol v-if="sysId==0 && proId==0" class="breadcrumb">
            <li class="active">全部</li>
        </ol>

        <ol v-if="sysId!=0 && proId==0" class="breadcrumb">
            <li><router-link :to="{path:'/modules/0/0'}">全部</router-link></li>
            <li class="active" :title="sysName">{{sysName}}</li>
        </ol>

        <ol v-if="sysId!=0 && proId!=0" class="breadcrumb">
            <li><router-link :to="{path:'/modules/0/0'}">全部</router-link></li>
            <li :title="sysName"><router-link :to="{path:'/modules/'+sysId+'/'+0}">{{sysName}}</router-link></li>
            <li class="active" :title="proName">{{proName}}</li>
        </ol>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss">
    #nav_path{
        position: relative;
        padding: 0!important;
        border-top:none;
        border-left:none;
        border-right:none;
        margin: 0;
        .breadcrumb{
            padding: .5rem 1rem 0;
            height: 100%;
            width: 100%;
            display: inline-block;
            li{
                height: 100%;
                display: inline-block;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                &:nth-child(1){
                    max-width:10%;
                }
                &:nth-child(2),&:nth-child(3){
                    max-width:40%;
                }

            }
        }
    }
</style>
<script>
    import urlParam from '../../../lib/urlParam';
    export default{
        name:'nav-path',
        data(){
            return this.$store.getters.navPath;
        },
        watch: {
//            // 如果路由有变化，会再次执行该方法
            "$route": function (route) {
                if(["projects","modules"].indexOf(route.name)>=0){
                    // 只有切换到上述2个路由才会导致组件更新
                    this.$store.dispatch("reFreshPath")
                }
            },
        }

    }
</script>