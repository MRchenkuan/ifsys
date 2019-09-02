<template>
    <div>
        <div id="interfaceDetail_container_editor_operations" class="btn-group">
            <button id="btn-saveIfChanges" @click="saveInterfaceChanges" type="button" :class="saveBtnClass" class="btn btn-success"><span :class="saveBtnClass=='disabled'?'glyphicon glyphicon-asterisk':'glyphicon glyphicon-flash'"></span>快速保存</button>
            <button type="button" :class="saveBtnClass" class="btn btn-success dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
            <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                <li role="presentation" @click="saveInterfaceChanges('true')"><a href="javascript:void(0)"><span class="glyphicon glyphicon-time"></span> 保存并推进版本</a></li>
            </ul>
        </div>
        <button id="btn-returnOutEditor" @click="returnOutOfEditor" type="button" class="btn btn-warning">返回 <span class="glyphicon glyphicon-share-alt"></span></button>
    </div>
</template>
<script>
    export default{
        name:"saveIfBtn",
        data(){
            return {
                saveBtnClass:this.$store.state.header.editorController.saveBtnClass
            }
        },
        methods:{
            saveInterfaceChanges(isAdvance){
                this.$store.dispatch("saveInterfaceChanges",isAdvance)
            },
            returnOutOfEditor(){
                this.$store.dispatch("returnOutOfEditor")
            }
        }
    }
</script>