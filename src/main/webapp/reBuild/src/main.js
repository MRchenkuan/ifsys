/**
 * Created by mac on 2017/2/23.
 */
var Vue = require('Vue');

// var jQuery,$;
// var jQuery = resolve => require(['../static/js/3rd/jquery.js'], resolve)
// var $ = jQuery;

var jQuery = require('../static/js/3rd/jquery.js');

require('../static/bootstrap/js/bootstrap.min.js')(jQuery);
require('../static/js/3rd/FreeSearch.js')(jQuery);
require('../static/js/3rd/jquery.jsonview.js')(jQuery);

// css 引入
require('../static/bootstrap/css/bootstrap.min.css');
require('../static/bootstrap/css/bootstrap-theme.min.css');
require('../static/css/3rd/jquery.jsonview.css');
require('../static/css/3rd/FreeSearch.css');
require('../static/css/defined.css');

// 引入其他组件
const mainFrame = resolve => require(['./componet/main.vue'], resolve)
const headerBar = resolve => require(['./componet/header/headerBar.vue'], resolve)
const alert = resolve => require(['./componet/alertTools/alert.vue'], resolve)
const JsonImporter = resolve => require(['./componet/JsonImporter.vue'], resolve)
const shader = resolve => require(['./componet/alertTools/shader.vue'], resolve)

// import mainFrame from "./componet/main.vue";
// import headerBar from './componet/header/headerBar.vue';
// import alert from './componet/alertTools/alert.vue';
// import JsonImporter from './componet/JsonImporter.vue'
// import shader from './componet/alertTools/shader.vue'
//

// 使用路由
import router from './router/router';

// vuex 组件
import store from './store/store';

Vue.config.keyCodes = {
    v: 86,
    f1: 112,
    mediaPlayPause: 179,
    up: [38, 87],
    tab:9
}

// 实例化弹出框
new Vue({
    el:"#alert",
    components:{
        alert
    },
    store,
    render :(createElement) => createElement(alert)
});

// 实例化主页
new Vue({
    el:"#main",
    components:{
        mainFrame
    },
    router,
    store,
    render :(createElement) => createElement(mainFrame)
});


// 实例化导航
new Vue({
    el:"#nav",
    components:{
        headerBar
    },
    router,
    store,
    render :(createElement) => createElement(headerBar)
});

// 实例化Json导入工具
new Vue({
    el:"#JsonImporter",
    store,
    render :(createElement) => createElement(JsonImporter)
});

// 遮罩框
new Vue({
    el:"#shader",
    components:{
        headerBar
    },
    store,
    router,
    render :(createElement) => createElement(shader)
});

