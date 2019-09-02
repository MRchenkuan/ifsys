/**
 * Created by chenkuan on 2016/11/28.
 */

(function($){
    var $frame,itemsBox;
    $.fn.searchBox = function(data,invokers) {
        // 下拉框容器
        itemsBox = document.createElement("div");

        var $this = $frame = $(this);
        $frame.userlistData = data;
        $frame.userlistBox = new DropBox(data);
        // 获取搜索历史
        $frame.searchHistory = getSearchHistory;

        if ($this.css("max-width") == "none") $this.css({
            maxWidth: $this.width()
        });

        // 初始化items框
        $this.itemsBox = itemsBoxInit($this);
        // 扩展: 可以给待选项设置初值
        if(invokers){
            // 然后重建数据
            var _data = {};
            data.some(function(it){
                _data[it.id] = it;
            });
            invokers.some(function(uid){
                var userObj = _data[uid];
                if(userObj)$frame.itemsBox.createItem(userObj["userName"],userObj["id"],userObj["email"]);
            })
        }
        $this.click(function () {
            // 初始化输入框
            var $inputEle = inputBoxInit($this);
            $this.append($inputEle);
            $inputEle.focus();
        });

        // 提交
        $this.getAllSelections = function(){
            var $items = $this.itemsBox.getAllItems();
            var ids = [];
            Array.prototype.some.call($items,function(it){
                ids.push(it.getAttribute("uid"))
            });
            return ids
        };
        // 设置默认节点
        $this.setInitItems = function(invokers){
            console.log(invokers)
            if(invokers){
                // 然后重建数据
                var _data = {};
                data.some(function(it){
                    _data[it.id] = it;
                });
                invokers.some(function(uid){
                    var userObj = _data[uid];
                    if(userObj)$frame.itemsBox.createItem(userObj["userName"],userObj["id"],userObj["email"]);
                })
            }
        };
        // 清空所有节点方法
        $this.cleanAllItems = function(){
            $this.itemsBox.cleanAllItems();
        };

        return $this;
    };
    /**
     * 初始化输入框
     * */
    function inputBoxInit($frame){
        // 创建输入框
        var inputEle = document.createElement("textarea");
        var $inputEle = $(inputEle);
        $frame.$inputEle = $inputEle;

        // 输入框点击事件
        $inputEle.click(function(e){
            e.stopPropagation();
            $frame.userlistBox.show();
        });

        //输入框失焦事件
        $inputEle.blur(function(){
            $inputEle.remove();
            setTimeout(function(){
                $frame.userlistBox.hide();
                $inputEle.focus()
            },250);
        });

        //输入框焦点事件
        $inputEle.focus(function(){
            $frame.css({
                height:$inputEle.outerHeight(true),
                width:$inputEle.outerWidth(true)
            });
            console.log("outerWidth",$inputEle.outerWidth(true))
        });

        // 监听方向键和回车键, js特点,先监听的先执行
        $inputEle.keydown(function(e){
            if(e.keyCode==13){
                //回车事件
                e.preventDefault(); //阻止默认事件
                $frame.userlistBox.select();
            }

            if(e.keyCode==38){
                //上键事件
                e.preventDefault();
                $frame.userlistBox.switchUp();
            }

            if(e.keyCode==40){
                //下键事件
                e.preventDefault();
                $frame.userlistBox.switchDown();
            }
        });


        // 输入框按键事件
        $inputEle.keyup(function(e){
            if(!$frame.userlistBox) $frame.userlistBox = new DropBox($frame.userlistData);
            // 排除回车,上下键
            if([13,38,40].indexOf(e.keyCode)<0){
                $frame.userlistBox.preSelectedIndex=0;
                $frame.userlistBox.search()
            }

        });

        // 重设输入框位置方法
        $inputEle.resize = function(){
            var $lastSpan = $frame.itemsBox.find("span").last();
            // 最后一个的位置
            var lastSpanPos = $lastSpan.position() ? $lastSpan.position(): {top:0,left:0};
            // 计算新位置
            lastSpanPos.left += (Math.max(0,$lastSpan.outerWidth(true)) + 5);
            lastSpanPos.top += $lastSpan.length>0?(parseInt($lastSpan.css("margin-top")) + parseInt($lastSpan.css("border-top-width")) + parseInt($lastSpan.css("padding-top"))):5;


            // 每行编辑区最小容限
            var isTooShort = $frame.innerWidth() - lastSpanPos.left <= 20;

            // 点击样式
            $inputEle.css({
                width:isTooShort ? $frame.outerWidth(true) : $frame.outerWidth(true) ,
                paddingLeft:isTooShort ? 5 :lastSpanPos.left,
                paddingTop:isTooShort ? (lastSpanPos.top + $lastSpan.outerHeight(true)) : lastSpanPos.top,
                overflow:"hidden",
                height:"auto",
                border:"none",
                float:"left"
            });
            $inputEle.attr("wrap","off");
            $inputEle.attr("autocomplete","off");

        };

        // 初始化输入框大小
        $inputEle.resize();

        return $inputEle;
    }


    /**
     * 下拉框构建类
     * @param data [{userName:"chenkuan",id:"23",email:"393667111@qq.com"}]
     * @constructor
     */
    function DropBox(data){
        // 设置备选节点
        this.preSelectedIndex = 0;

        var ul = document.createElement("ul");
        var $ul = $(ul);
        $ul.addClass("FreeSearch-DropBox");
        $ul.css({
            width:$frame.innerWidth(),
            left:$frame.position().left,
            top:$frame.position().top+$frame.innerHeight()
        });
        // 委托点击事件
        $ul.delegate("li","click",function(e){
            e.stopPropagation();
            //被选中的item
            var ele = e.currentTarget;
            var $ele = $(ele);
            if($ele.attr("name") && $ele.attr("uid") && $ele.attr("email"))
                $frame.itemsBox.createItem($ele.attr("name"),$ele.attr("uid"),$ele.attr("email"));
            $frame.userlistBox.hide();
            $ele.remove()
        });

        $frame.append(ul);

        // 查询方法
        this.search = function(){
            var context = $frame.$inputEle.val();
            console.log(context);
            // 清空结果
            $ul.empty();
            // 重置位置
            $ul.css({
                width:$frame.outerWidth(true),
                left:$frame.position().left,
                top:$frame.position().top + $frame.outerHeight(true)
            });
            var result = [];
            $frame.userlistData.some(function(it){
                if(it['email'].indexOf(context.trim())!=0 )return;
                if(!Array.prototype.every.call($frame.itemsBox.getAllItems(),function(span){
                        return span.getAttribute("uid") != it["id"]
                    }))return;

                result.push(it)
            });

            // 限制20个显示, 若没有记录则显示历史记录
            if(result.length>20 || result.length==0){
                result = [];
                var _result = [];
                var hisObj = $frame.searchHistory();
                for(var key in hisObj){
                    if(hisObj.hasOwnProperty(key)){
                        result.unshift({
                            userName:hisObj[key].name,
                            id:hisObj[key].uid,
                            email:hisObj[key].email
                        })
                    }
                }

                // 去除已选的
                result.some(function(it){
                    if(!Array.prototype.every.call($frame.itemsBox.getAllItems(),function(span){
                            return span.getAttribute("uid") != it["id"]
                        }))return;
                    _result.push(it)
                });
                result = _result;
            }

            // 将查询结果数据缓存
            $frame.userlistBox.result = result;
            // 绘制list
            result.some(function(it,id) {
                var li = document.createElement("li");
                li.innerHTML = it["userName"];
                li.setAttribute("uid", it['id']);
                if(id==$frame.userlistBox.preSelectedIndex)li.setAttribute("style","background:#c5c5c5");
                li.setAttribute("name", it['userName']);
                li.setAttribute("email", it['email']);
                ul.appendChild(li);
            });
            $ul.show();

            // 滚动到合适的位置,使备选项居中
            var $beSelectLi = $ul.find("li").eq($frame.userlistBox.preSelectedIndex);
            if($beSelectLi.length>=1){
                $ul.scrollTop($beSelectLi.position().top-$ul.height()/3);
            }else{
                $ul.append((function(){
                    var noticeLi = document.createElement("li");
                    var $noticeLi = $(noticeLi);
                    $noticeLi.css({
                        color:"grey"
                    });
                    $noticeLi.text("~~没有更多搜索结果~~");
                    return $(noticeLi)
                })())
            }
        };

        // 重设位置方法
        this.reset = function(x,y,w){
            $ul.css({
                width:w,
                left:x,
                top:y
            });
            return this;
        };

        // 盒子展示
        this.show = function(){
            $ul.show();
        };

        // 隐藏盒子
        this.hide = function(){
            $ul.hide();
            // 重置备选序号
            $frame.userlistBox.preSelectedIndex=0;
        };

        // 将当前搜索接口存入cookie
        this.setCookie = function(){
            // todo
        };

        //选中当前节点
        this.select = function(){
            var self = this;
            var $li = $ul.find("li");
            if($li.length<=0)return;
            // 设置当前备选项的下限,以免最下元素被选导致备选序号为空的情况.
            self.preSelectedIndex = Math.min($li.length-1,self.preSelectedIndex);
            console.log(self.preSelectedIndex);
            // 获取备选项
            var $ele = $li.eq(self.preSelectedIndex);
            // 创建已选项
            if($ele.attr("name") && $ele.attr("uid") && $ele.attr("email"))
                $frame.itemsBox.createItem($ele.attr("name"),$ele.attr("uid"),$ele.attr("email"));
            // 重设输入框大小
            $frame.$inputEle.resize();
            // 移除已选元素
            $ele.remove();
            // 移除后当前备选项序号-1
            self.preSelectedIndex--;
            // 设置当前备选项的上限,以免最上元素被选导致备选序号为空的情况.
            self.preSelectedIndex = Math.max(0,self.preSelectedIndex);
            // 重新获取最新的下拉框
            $frame.userlistBox.search();
        };


        // 将备选节点上移
        this.switchUp = function(){
            if(this.preSelectedIndex<1){
                var userList = $frame.userlistBox.result;
                if(!userList)userList = [];
                this.preSelectedIndex = userList.length-1;
            }else{
                this.preSelectedIndex--
            }
            $frame.userlistBox.search();
        };

        // 将被选节点下移
        this.switchDown = function(){
            var userList = $frame.userlistBox.result;
            if(!userList)userList = [];
            if(this.preSelectedIndex >= userList.length-1){
                this.preSelectedIndex = 0
            }else{
                this.preSelectedIndex++
            }
            $frame.userlistBox.search();
        };
    }

    /**f
     *  被选中列表
     *
     **/
    function itemsBoxInit($frame){
        var $itemsBox = $(itemsBox);
        $itemsBox.css({
            width:$frame.width(),
            position:"absolute",
            zIndex:"999"
        });

        // 创建item方法
        $itemsBox.createItem = function(name,id,email){
            var item = document.createElement("span");
            var text = document.createElement("span");
            var closeBtn = document.createElement("span");
            var $item = $(item);
            var $text = $(text);
            var $closeBtn = $(closeBtn);

            //item 基本结构定义
            $item.attr("uid",id);
            $item.attr("email",email);
            $item.attr("name",name);
            $item.addClass("FreeSearch-items");
            $item.css({
                "max-width":parseInt($frame.css("max-width")) - 10
            });

            // 文案按钮 基本结构定义
            $text.text(name);
            $text.addClass("FreeSearch-items-text");

            // 关闭按钮 基本结构定义
            $closeBtn.text("-");
            $closeBtn.addClass("FreeSearch-items-closeBtn");
            $closeBtn.click(function(){
                $item.remove()
            });
            // 挂载
            $itemsBox.append($item);
            $item.append($text);
            $item.append($closeBtn);

            setSearchHistory($item);


            // 更新目标框样式
            var newHeight=$item.outerHeight(true)+$item.position().top+5;
            var width = Math.max($frame.outerWidth(true),($item.position().left + $item.outerWidth(true)));
            $frame.css({
                height:newHeight,
                width:width
            });

            // 同时要更新下拉框列表位置
            $frame.userlistBox.reset($frame.position().left,$frame.position().top + $frame.outerHeight(true),$frame.width())

        };
        $frame.append($itemsBox);

        // 提交方法
        $itemsBox.getAllItems = function(){
            return $itemsBox.children()
        };
        // 清空所有方法
        $itemsBox.cleanAllItems = function(){
            $itemsBox.children().remove();
        };

        return $itemsBox
    }

    /**
     * 从cookie中获取历史记录
     */
    function getSearchHistory(){
        var cookieString = getCookie("FreeSearch-his");
        if(cookieString){
            return JSON.parse(cookieString);
        }else{
            return {}
        }
    }

    /**
     * 向cookie中设置历史记录
     * @param $item
     */
    function setSearchHistory($item){
        var allHis = getSearchHistory();
        var id = $item.attr("uid");
        var name = $item.attr("name");
        var email = $item.attr("email");
        try{
            delete allHis[id];
        }catch (ignore){
            console.log(ignore)
        }
        allHis["id"+id] = {
            uid:id,
            name:name,
            email:email
        };
        setCookie("FreeSearch-his",JSON.stringify(allHis));
    }


    /**
     * 其他
     * @param name
     * @returns {null}
     */
    function getCookie(name)
    {
        var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
        if(arr=document.cookie.match(reg))
            return unescape(arr[2]);
        else
            return null;
    }
    /**
     * 其他
     * @param name
     * @param value
     * @returns {null}
     */
    function setCookie(name,value)
    {
        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
    }
})($)