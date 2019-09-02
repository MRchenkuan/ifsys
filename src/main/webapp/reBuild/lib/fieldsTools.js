
export default {
    stringifyFields,
    wrapFieldsLevel,
    addStyle2Fields,
    isSameLevel,
    JSON2relatedList,
    reorganizeParent,
    relatedList2JSON
}

/**
 * 将字段列表序列化
 * @returns {string}
 * @param fields
 */
function stringifyFields(fields) {
    if(!fields){
        // 当前字段列表不存在时的逻辑
        return "/*不存在list*/{}";
    }
    // 渲染层级
    wrapFieldsLevel(fields);
    var value = {};
    // // 1.在编辑界面，由于参数列表可能发生修改,而json的组织,依赖于parent,需要重新组织parent
    reorganizeParent(fields);
    // 2.组织json
    relatedList2JSON(value,fields,fields);
    // 序列化
    return JSON.stringify(value,null,3)
}


/**
 * 已知parent的前提下
 * todo 出现只能遍历第一层的BUG,需要关注
 * 包装field 加入level ,
 * 同时维护层级路径列表{"0-1-1-1-1":{length:,keypath:}}
 * @param fieldList
 */
function wrapFieldsLevel(fieldList){
    // 保存层级关系对象
    wrap(fieldList, {});

    // 排序
    fieldList.sort(function(x,y){
        if(x._level > y._level)return 1;
        if(x._level == y._level)return 0;
        if(x._level < y._level)return -1;
    });
    /**
     *
     * @param fieldList
     * @param parentLevelMap 父节点的层级 列表
     * @param level 当前层级
     */
    function wrap(fieldList,parentLevelMap){
        if(fieldList.length<=0)return false
        var literateItems = []
        fieldList.forEach(function(field){
            if(!field.hasOwnProperty("_level")){
                // 节点当前k和父k
                var keyPath,parent = field.parent;
                if(parent ==""){
                    keyPath = field.k;
                }else{
                    keyPath = parent +"."+field.k;
                }

                // 初始化字典
                if(!parentLevelMap.hasOwnProperty("")){
                    parentLevelMap[""] = {length:0, level:"0"}
                }

                if(parentLevelMap.hasOwnProperty(parent)){
                    // 维护根节点高度
                    parentLevelMap[parent].length++;
                    // 维护下级节点字典
                    parentLevelMap[keyPath] = {
                        length:0,
                        level:parentLevelMap[parent].level + "-" +parentLevelMap[parent].length
                    };
                    // 设置当前节点level
                    field._level = parentLevelMap[parent].level +"-"+parentLevelMap[parent].length;
                }else{
                    literateItems.push(field);
                }
            }
        });

        if(fieldList.length<=literateItems.length){
            // 无限递归 退出
            console.log("如下节点找不到父节点,渲染可能有问题");
            console.log(fieldList)
            return false;
        }

        if(literateItems.length > 0){ // todo 很有可能是此行判断有问题,导致无法进行递归
            wrap(literateItems , parentLevelMap)
        }
    }
}

/**
 * 将关系型List转成Json对象, JSON2relatedList 互为逆函数
 * 算法为:
 * 1.将当前节点点优先设置
 * 2.将剩余节点重复1,若节点数减少到0,则3,否则报错
 * 3.返回对象
 * @param obj 空对象,输出值
 * @param fieldList 递归列表,初始值为全量原始列表,dynamic
 * @param _fieldList 全量原始列表 fixed
 */
function relatedList2JSON(obj,fieldList,_fieldList){
    var listLength = fieldList.length;
    var literateItems = [];
    var _literateItems = [];
    fieldList.forEach(function(it,id,ar){

        // 若本身就是根节点
        if(it.parent == ""){
            // 直接赋值
            obj[it.k] = getMockObj(it.mock);
        }else{
            // 剩余节点
            literateItems.push(it);
        }

        // 获取关联父节点的
        literateItems.forEach(function(it){
            var paths = it.parent.split(".");
            var _obj = obj;
            // 确认父节点的类型
            var parentType = getParentTypeByPath(_fieldList,it.parent);
            // 根据path
            // 逐级寻找父节点的引用
            while (paths.length>0) {
                var key = paths.shift();// 父节点
                if (!(_obj[key] instanceof Object)) {
                    // 找到但不为对象的时候,直接初始化为对象
                    if (parentType == 4) { // ArrayObject
                        _obj[key] = [{}];
                    } else {// Object
                        _obj[key] = {};
                    }
                }
                if (_obj[key] instanceof Array) { // ArrayObject
                    _obj = _obj[key][0]
                } else {// Object
                    _obj = _obj[key]
                }

            }

            // 找到引用直接赋值
            if(_obj instanceof Array){ // ArrayObject
                _obj[0][it.k] = getMockObj(it.mock);
            }else{ // Object
                _obj[it.k] = getMockObj(it.mock);
            }
        });

        // 判断是否发生了无限递归
        if(_literateItems.length > listLength){
            return false;
        }

        // 最终余下的
        if(_literateItems.length>0){
            relatedList2JSON(obj,_literateItems,_fieldList)
        }

    });
}
/**
 * 将对象化的Json 转成关系型LIST 与 relatedList2JSON 互为逆函数
 * @param JSONObj
 * { parent:"", k:"", mock:"列表", type:1, _level : "1-1" }
 */
function JSON2relatedList(JSONObj){
    var colors = ["#b33c39", "#6a6fff", "#53bb57", "#ff1cdc", "#5ab4a6", "#ada662"];
    var list = [];
    var typeList = {
        arrayObject:4,
        object:3,
        string:1,
        number:2,
        boolean:5,
        nul:6,
        array:7
    };
    return getList("",list,JSONObj,0);
    /**
     * 根据对象化的Json 递归获取 列表化的json
     * @param parent 进入时的统一parent
     * @param list
     * @param JSONObj
     * @param level 用于描述层级关系
     * @return 数据结构
     */
    function getList(parent , list,JSONObj , level){
        var seed= 1;//每个元素的内部ID
        console.log(level)
        for(var k in JSONObj){
            if(!k)continue; // 防止k值为空的情况
            // 确定层级
            var _level = level +"-"+(seed++);// todo bug 此处+1位置不对
            var indent = _level.split("-").length-2;
            var note ="";
            var v = JSONObj[k];
            var field;
            var type = 1;
            // 下一层的parent
            var nextParent = parent==""?k:(parent + "." + k);
            // 判断当前值类型
            if(v instanceof Array){
                // 确定层级
                // 数组
                // 判断数组类型
                type = typeof v[0] == "object" ?typeList.arrayObject:typeList.array;
                field = {
                    parent:parent,
                    k:k,
                    mock:"列表",
                    type:type,
                    note:note,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                list.push(field);
                getList(nextParent,list,v[0],_level);
            }else if(v instanceof Object){
                // 对象
                field = {
                    parent:parent,
                    k:k,
                    mock:"对象",
                    note:note,
                    type:typeList.object,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                list.push(field);
                getList(nextParent,list,v,_level);
            }else{
                // 其他值
                var mock="";
                if(typeof v == "string"){// 字符串
                    type = typeList.string;
                    mock = v;
                }
                if(typeof v == "number"){ // 数字
                    type = typeList.number;
                    mock = v*1;
                }
                if(typeof v == "boolean"){// 布尔值
                    type = typeList.boolean;
                    mock = v.toString();
                }
                if( v === null){ // 空值
                    type = typeList.nul;
                    mock="null";
                }
                // 常量
                field = {
                    parent:parent,
                    k:k,
                    note:note,
                    mock:mock,
                    type:type,
                    _level : _level,
                    _indent:indent,
                    _color:colors[indent]
                };
                list.push(field);
            }
        }
        return list
    }
}

/**
 * 已知level的前提下
 * 参数列表重组paren方法
 * 具体算法:
 *
 * 根据部分parent ,组织其他的parent
 * @param fieldList
 */
function reorganizeParent(fieldList){

    var completedList = [];
    // 清空已有的parent
    fieldList.forEach(function(field) {
        field.parent = "";
    });

    reorg(fieldList);
    //fieldList = completedList;

    function reorg(fieldList){
        if(fieldList.length<=0)return;
        // 剩余列表
        var leftList = [];
        fieldList.forEach(function(field){
            var current_level = field._level; // 0-*
            var parent_level = getParentLevelString(current_level);// 0
            // 老规矩,确认根节点名
            if(parent_level == "0"){
                field.parent = ""
                completedList.push(field);
            }else{
                leftList.push(field);
            }
        });

        // 剩余节点根据parent_level来进行递归
        leftList.forEach(function(it){
            var parent_level = getParentLevelString( it._level); //0-*
            // 剩余节点与已存节点比较
            var isExist = completedList.some(function(existIt){
                if(parent_level == existIt._level){
                    it.parent = (existIt.parent=="")?existIt.k:existIt.parent+"."+existIt.k;
                    completedList.push(it)
                    return true
                }else{
                    return false;
                }
            });

            // 当前节点在已有的节点中,找不到父节点,
            // 则放到下一轮迭代中
            if(!isExist){
                leftList.push(it);
            }

        });

        // 无限递归判断
        if(fieldList.length <= leftList.length){
            console.log("reorganizeParent 无限递归");
            console.log(leftList)
            return false;
        }

        if(leftList>0) {
            reorg(leftList)
        }
    }

}

/**
 * 包装,并重设返回参数列表
 */
function addStyle2Fields(fields){
    // 加入level
    wrapFieldsLevel(fields);
    var colors = ["#b33c39","#6a6fff","#53bb57","#ff1cdc","#5ab4a6","#ada662"];
    fields.forEach(function(it){
        if(it.hasOwnProperty("_level")) {
            // 确认缩进
            var indent = it._level.split("-").length-2;
            it._indent = indent;
            // 确认颜色
            it._color = colors[indent];
        }else{
            console.log("渲染失败,渲染前,请确认field对象已经加入了level")
        }
    });
    return fields;
}


/**
 * 获取当前level的父级
 * @param currentLevel
 * @returns {string}
 */
function getParentLevelString(currentLevel){
    var arr = currentLevel.split("-");
    arr.pop();
    return arr.join("-")
}

/**
 * mock数据对象化
  * @param str
 * @returns {*}
 */
function getMockObj(str){
    try{
        var mock=JSON.parse(str);
        if(mock instanceof Object){
            mock = mock.toString();
        }
        return mock;
    }catch (e){
        return str;
    }
}

/**
 * 根据父节点path获取父节点类型
 */
function getParentTypeByPath(list,str){
    var keyMap = {};
    // 每个listItem的全路径
    list.forEach(function(it){
        if(it.parent){
            keyMap[it.parent+"."+it.k] = it;
        }else{
            keyMap[it.k] = it;
        }
    });
    try{
        return keyMap[str].type;
    }catch (e){
        console.log(str);
    }
}



/**
 * 比较两个层级是否在同一层
 * 比如处于同一层:
 * 0-1-1-1-5
 * 0-1-1-1-50
 * 不同一层
 * 0-1-1-1-5
 * 0-1-1-2-5
 * 具体算法:
 * 1.分割两个路径到数组
 * 2.出栈尾部序号
 * 3.比较前缀序列是否相等
 * @param level
 * @param compareLevel
 */
function isSameLevel(level,compareLevel){
    var arrLevel = level.split("-");
    var arrCompareLevel = compareLevel.split("-")

    arrLevel.pop();
    arrCompareLevel.pop();

    return arrLevel.join("-") == arrCompareLevel.join("-");

}

