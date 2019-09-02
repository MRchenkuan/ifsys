<template>
    <div id="ifml_editor" style="outline: none;min-height: 600px" contenteditable="true"></div>
</template>
<script>

    export default {
        name:"code-editor",
        props:['theme','callbacks'],
        data(){
            return {
                editor:null,
                text:"",
            }
        },
        methods:{
            getText(){
                return this.editor.getValue()
            },
            setHeavy(){
                this.editor.setTheme("ace/theme/idle_fingers");
            },
            setLight(){
                this.editor.setTheme("ace/theme/github");
            },
        },
        mounted(){
            let callbacks = this.$props.callbacks;
            this.editor = ace.edit("ifml_editor");
            this.editor.session.setMode("ace/mode/yaml");
            this.editor.setTheme(this.$props.theme=='heavy'?"ace/theme/idle_fingers":"ace/theme/github");
            this.editor.setValue("#填写ifml"); // or session.setValue
            this.editor.getSession().on('change', (e)=> {
                callbacks.change()
            });
        }
    }

</script>
