<template>
    <li>
        <button class="btn btn-theme" @click="createInterface"><span class="glyphicon glyphicon-certificate"></span> 创建接口</button>
        <button type="button" class="btn btn-theme dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
        <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
            <li role="presentation" @click="createInterfaces"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-sort-by-attributes"></span> 批量添加接口</a></li>
        </ul>
    </li>
</template>
<style scoped>
    li{margin-left: 15px}
</style>
<script>
    export default{
        name:"create-btn",
        date(){
            return {}
        },
        methods:{
            createInterface:function () {
                this.$router.push("/creator")
            },
            createInterfaces:function () {
                console.log("批量创建借口")
            }
        }
    }
</script>