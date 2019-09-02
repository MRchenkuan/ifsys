<template>
    <div class="ifInfoBlock panel-default panel" >
        <h5 class="themesColor panel-body" style="overflow: visible">
            <span><span class="glyphicon glyphicon-th-large"></span> 导入IFML</span>
            <button class="saveBtn btn btn-default btn-xs" @click="saveText()">保存IFML</button>
            <span class="switches btn-group btn-group-xs">
                <button :class="theme=='light'?'active':''" @click="light" class="btn btn-default">浅</button>
                <button :class="theme=='heavy'?'active':''" @click="heavy" class="btn btn-default">深</button>
            </span>
        </h5>

        <code-editor ref="editor" class="codeEditor" :callbacks="callbacks" :theme="theme" ></code-editor>
        <if-displayer ref='displayer' class="displayer"></if-displayer>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss" scoped>
    .ifInfoBlock{
        overflow: hidden;
        margin: 0;
        .saveBtn{
            float: right;
        }
        .switches{
            float: right;
            margin-right: 1rem;
        }
        .displayer,.codeEditor{
            float: left;
            width: 50%;
            height: 100%;
            box-sizing: border-box;
            border-top:1px solid #cccccc;
        }
    }
</style>
<script>
    import Utils from '../../../../lib/utils'
    var setCookie = Utils.setCookie;
    import urlParam from '../../../../lib/urlParam';
    const codeEditor = resolve => require(['../../../../modules/codeEditor/codeEditor.vue'], resolve)
    const ifDisplayer = resolve => require(['../displayer/displayer.vue'], resolve)
    export default{
        name:"ifml-importer",
        data(){
            return {
                ifml:"",
                cursorPos:0,
                theme:urlParam.ifmlImporterTheme,
                callbacks : {
                    change: ()=> {
                        let displayer = this.$refs.displayer;
                        let editor = this.$refs.editor;
                        displayer.setText(editor.getText())
                    }
                }
            }
        },
        methods:{
            saveText(){
                var text = this.$refs.editor.getText();
                console.log(text)
            },
            light(){
                this.$refs.editor.setLight();
                this.theme = "light";
                setCookie("ifmlImporterTheme","light");
            },
            heavy(){
                this.$refs.editor.setHeavy();
                this.theme = "heavy";
                setCookie("ifmlImporterTheme","heavy");
            },

        },
        components:{
            codeEditor,
            ifDisplayer
        },
    }
</script>