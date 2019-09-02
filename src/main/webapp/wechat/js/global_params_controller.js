/**
 * Created by mac on 2017/2/7.
 */
(function($,w){
    w.GetQueryString = function(name){
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = w.location.search.substr(1).match(reg);
         if(r!=null)return  decodeURI(r[2]); return null;
    };
})(jQuery,window)