<template>
    <div id="main">
        <router-view name="leftSide"></router-view>
        <div id="content" v-on:scroll="scroll">
            <router-view name="content"></router-view>
        </div>
        <toast></toast>
    </div>
</template>

<script>
    import interfaceViewer from './contents/viewer/viewer.vue';
    import interfaceEditor from './contents/editor/editor.vue';
    import toast from './alertTools/toast.vue';
    var jQuery,$;
    jQuery = $ = require('../../static/js/3rd/jquery.js');

    export default{
        name: 'main-frame',
        components: {
            interfaceViewer,
            interfaceEditor,
            toast
        },
        mounted(){
            this.$store.state.content$ = $("#content")
        },
        methods:{
            scroll(e){
                this.$store.state.contentCurrentTop = e.target.scrollTop
            }
        }
    }

</script>

<style>
    #main{
        margin-top:55px;
        position: relative;
    }
    #navTabs{
        width: 600px;
        height: 29px;
        background: #fff;
        z-index: 99;
    }
    #content{
        position: fixed;
        overflow-y: auto;
        top:60px;
        left:320px;
        right: 0;
        bottom: 0;
        padding-bottom: 50px;
    }
</style>