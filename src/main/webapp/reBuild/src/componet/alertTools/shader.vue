<!-- 界面遮罩 -->
<template>
    <div class="modal fade " id="shader">
        <div class="modal-dialog modal-sm" style="margin-top: 280px">
            <div class="modal-content">
                <div class="modal-body themesColor text-center">{{msg}}
                    <span class="glyphicon glyphicon-asterisk"></span>
                    <span class="glyphicon glyphicon-asterisk"></span>
                    <span class="glyphicon glyphicon-asterisk"></span>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>
</template>


<script>
    import $ from '../../../static/js/3rd/jquery.js';
    export default{
        data(){
            return {
                isShow:false,
                shader:null,
                msg:""
            }
        },
        created(){
            this.$store.state.shader = this;
        },
        mounted(){
          this.shader =  $("#shader");
        },
        methods:{
            show:function(msg){
                this.msg="";
                if(msg)
                    this.msg = msg;
                this.shader.modal("show")
            },
            hide:function(){
                this.shader.modal("hide")
            }
        },
    }
</script>