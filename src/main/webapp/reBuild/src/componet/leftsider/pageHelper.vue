<template>
    <ul id="interfaceList_pager" class="pager">
        <router-link tag="li" :to="{path:prevLink}" id="prePage" :data-pageNum="pageInfo.prePage" :class='pageInfo.hasPreviousPage?"":"disabled"'><a>前一页</a></router-link>
        <li><a href="#">{{pageInfo.pageNum}}/{{pageInfo.pages}}</a></li>
        <router-link tag="li" :to="{path:nextLink}" id="nextPage" :data-pageNum="pageInfo.nextPage" :class='pageInfo.hasNextPage?"":"disabled"'><a>后一页</a></router-link>
    </ul>
</template>

<script>
    export default{
        name:"page-helper",
        props:['pageInfo','nextLink','prevLink'],
//        data(){
//            console.log(this.pageInfo)
//            return {
//                pageInfo:this.pageInfo
//            }
//        }
    }
</script>

<style lang="css">
    #interfaceList_pager{
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 10px;
        margin: 0;
    }
</style>