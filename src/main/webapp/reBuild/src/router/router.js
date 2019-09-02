var Vue = require('Vue');
import VueRouter from 'vue-router';
import store from '../store/store';
// import interfaceViewer from '../componet/contents/viewer/viewer.vue'
// import interfaceEditor from '../componet/contents/editor/editor.vue'
// import interfaceCreator from '../componet/contents/editor/creater.vue'
// import interfaceList from '../componet/leftsider/interfaceList.vue';
// import feedList from '../componet/feedlist/feedList.vue';


const interfaceViewer = resolve => require(['../componet/contents/viewer/viewer.vue'], resolve)
const interfaceEditor = resolve => require(['../componet/contents/editor/editor.vue'], resolve)
const interfaceCreator = resolve => require(['../componet/contents/editor/creater.vue'], resolve)
const interfaceList = resolve => require(['../componet/leftsider/interfaceList.vue'], resolve)
const feedList = resolve => require(['../componet/feedlist/feedList.vue'], resolve)
const ifmlImporter = resolve => require(['../componet/contents/editor/IfmlImporter.vue'], resolve)



Vue.use(VueRouter);

const routes = [
    {
        path: '',
        name:"home",
        components: {
            leftSide:interfaceList,
            content:feedList
        }
    },
    {
        path: '/',
        name:"home",
        components: {
            leftSide:interfaceList,
            content:feedList
        }
    },
    {
        path: '/view/:ifId',
        components: {
            content:interfaceViewer,
            leftSide:interfaceList
        },
    },
    {
        path: '/view/:ifId/edit',
        name:"editor",
        components: {
            content:interfaceEditor,
            leftSide:interfaceList
        },
        beforeEnter(to, from, next){
            var ifId = to.params.ifId;
            // 如果当前接口数据存在
            if(store.state.interfaceInfo
                && store.state.interfaceInfo.basicInfo
                &&  store.state.interfaceInfo.basicInfo.ifId==ifId){
                store.commit('open_editor');
                next();
            }else{
                store.commit('close_editor');
                next({path:'/view/'+ifId})
            }
        },
    },
    {
        path:"/view/:ifId/edit/ifml",
        components:{
            content:ifmlImporter,
            leftSide:interfaceList
        }
    },

    {
        path: '/creator',
        name:"creator",
        components: {
            content:interfaceCreator,
            leftSide:interfaceList
        },
        beforeEnter(to, from, next){
            store.commit('open_editor',true);
            next()
        },
    },
    {
        path: '/creator/ifml',
        components:{
            content:ifmlImporter,
            leftSide:interfaceList
        }
    },
    {
        path: '/list/:pid/:sysId/:proId/:pageNum',
        name:"pages",
        components: {
            leftSide:interfaceList,
            content:interfaceViewer
        }
    },
    {
        path: '/project/:pid',
        name:"projects",
        components: {
            leftSide:interfaceList,
            content:feedList
        }
    },
    {
        path: '/modules/:sysId/:proId',
        name:"modules",
        components:{
            leftSide:interfaceList,
            content:feedList
        },
    }
];

const router = new VueRouter({
    routes
});


// 全局钩子
router.beforeEach(function (to,from,next) {

    // 退出编辑器之前确认
    if(from.name==='editor' || from.name==='creator'){
        store.dispatch("confirm",{
            msg:"正在编辑接口，确认退出？",
            cb: () => {
                next();
                store.commit('close_editor')
            }
        })
    }else{
        next()
    }

});

export default router