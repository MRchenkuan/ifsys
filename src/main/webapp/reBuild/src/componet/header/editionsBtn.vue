<template>
    <div>
        <button v-if="editorController.btnSwitchEdition.visible" @click="switchInterfaceEditon" id="btn-switchEditon" class="btn btn-theme"  data-toggle="dropdown">
            <span class="glyphicon glyphicon glyphicon-time"></span> 历史版本
        </button>
        <ul v-if="editorController.btnSwitchEdition.visible" class="hisEditions dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
            <li class="hisEdition" v-if="editions.length==0" role="presentation">
                <a class="normalTextColor" role="menuitem" tabindex="-1" href="#">
                    <h5>
                        <span class="glyphicon glyphicon-flag"></span>
                        <span class="version">v{{ifObj.ifVersionNo}} 当前版本</span>
                    </h5>
                    <h6>
                        <span class="username">{{ifObj.designer}}</span>
                        <span class="time">{{ifObj.ifCreateTime}}</span>
                    </h6>
                </a>
            </li>
            <li class="hisEdition" v-for="edition in editions" role="presentation" @click="showInterfaceEdition(edition)">
                <a class="normalTextColor" role="menuitem" tabindex="-1" href="#">
                    <h5>
                        <span class="glyphicon glyphicon-flag"></span>
                        <span class="version">v{{edition.ifVerNo}}</span>
                    </h5>
                    <h6>
                        <span class="username">{{edition.userName}}</span>
                        <span class="time">{{edition.ts}}</span>
                    </h6>
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
    export default{
        name:"editions-btn",
        props:['ifObj'],
        data(){
            return {
                editorController:{
                    btnEdit:{
                        visible:false
                    },
                    btnController:{
                        visible:false
                    },
                    btnSwitchEdition:{
                        visible:false
                    }
                },
            }
        }
    }
</script>