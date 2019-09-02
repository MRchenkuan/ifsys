<template>
    <div v-show="visible" v-html="msg" class="toast-frame"></div>
</template>
<script>
    import {toast} from '../../../lib/globalEvent';

    export default{
        name:"toast",
        computed:{
            msg(){
                return this.$store.state.toast.msg;
            },
            visible(){
                return this.$store.state.toast.visible;
            }
        },
        data(){
            return {
                msg:"",
                visible:false,
            }
        },
        methods:{
            show(msg){
                this.visible = true;
                this.msg = msg
            },
            hide(){
                this.visible = false
            },
            appear(msg){
                this.show();
                setTimeout(()=>{
                    this.hide()
                },2000)
            },
        },
        created(){
            toast.$on("toast", (msg)=> {
                this.msg = msg;
                this.visible = true;
            });

            toast.$on("closeToast", ()=> {
                this.visible = false;
            });

            this.$store.state.toast = this;
        }
    }
</script>
<style>
    .toast-frame{
        background-color: #0f0f0f;
        opacity: .7;
        z-index: 9999;
        position: fixed;
        padding: 1rem;
        color: #fff;
        border-radius: .5rem;
        font-size: 2rem;
        top: 50%;
        left: 50%;
        min-width: 2rem;
        min-height: 2rem;
        transform: translate(-50%,-50%);
    }
    .toast-icon{
        font-size: 3rem;
        height: 3rem;
        width: 3rem;
        display: block;
        text-align: center;
    }
</style>