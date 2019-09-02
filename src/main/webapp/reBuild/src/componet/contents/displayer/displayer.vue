<template>
    <div class="frame">
        <p>here is displayer </p>
        <div id="editor_basicInfo" class="ifInfoBlock panel-default panel" >
            <h5 class="themesColor panel-body" style="overflow: visible">
                <span><span class="glyphicon glyphicon-th-large"></span> 基本信息</span>
            </h5>
            <h3>{{info.title}}</h3>
            <h4 class="badge">{{info.url}}</h4>
        </div>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss" scoped>
    .frame{
        padding: 1rem;
    }
</style>
<script>
    import jsyaml from '../../../../lib/jsyaml';

    export default{
        data(){
            return {
                text:"",
                info:{
                    title:"",
                    url:"",
                    description:"",
                    version:"",
                    methods:[],
                    rspCodes:[],
                    request:{
                        desc:"请求部分",
                        type:"json",
                        body:""
                    },
                    response:{
                        desc:"响应部分",
                        type:"xml",
                        body:""
                    }
                }
            }
        },
        methods:{
            setText(text){
                let self = this;
                this.text = text;
                var _info = {}
                try{
                    _info = jsyaml.load(text)||_info;
                    console.log(_info)
                }catch (e){
                    console.log(e)
                }

                for(var k in self.info){
                    if(self.info.hasOwnProperty(k))
                    self.info[k] = _info[k]||self.info[key];
                }

//                this.info.title = _info.title||this.info.title;
//                this.info.url = _info.url||this.info.url;
//                this.info.description = _info.description||this.info.description;
//                this.info.title = _info.title||this.info.title;
//                this.info.title = _info.title||this.info.title;
//                this.info.title = _info.title||this.info.title;
//                this.info.title = _info.title||this.info.title;
//                this.info.title = _info.title||this.info.title;
            }
        }
    }
</script>