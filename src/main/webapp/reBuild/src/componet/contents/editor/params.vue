<template>
    <div id="editor_reqBody" class="panel panel-default ifInfoBlock">
        <h5 class="themesColor panel-body">
            <span class="glyphicon" :class="paramGlyphicon"></span> {{paramText}}
            <span class="btn-group btn-group-xs addItems">
                <button @click="addRootField" title="增加根节点" class="btn btn-default">
                    <span class="glyphicon glyphicon-plus"></span>
                </button>
                <button @click="addFieldFromJSON" class="btn btn-default">
                    <span class="glyphicon glyphicon-edit"></span> 导入JSON
                </button>
            </span>
        </h5>
        <table v-if="fieldList.length>0" class=" table table-bordered">
            <tbody>
            <tr>
                <th>参数</th>
                <th>MOCK</th>
                <th>说明</th>
                <th>类型</th>
                <th v-if="hasReqField">必须</th>
            </tr>
            <tr class="field-code-editor" v-for="(field,$i) in fieldList">
                <td class="field-input td-operations" :style="'padding-left:'+(field._indent)*30+'px' +';color:'+ field._color">
                    <input class="field-key-input" type="text" v-model="field.k">
                    <span class="td-operation">
                        <button title="增加同级节点" class="btn btn-sm btn-success" @click='addNewSiblingField(field)'><span class="glyphicon glyphicon-align-justify"></span></button>
                        <button title="增加子节点" class="btn btn-sm btn-success" @click='addNewChildField(field)'><span class="glyphicon glyphicon-indent-left"></span></button>
                        <button title="删除节点" class="btn btn-sm btn-danger" @click="deleteFields(field)"><span class="glyphicon glyphicon-minus"></span></button>
                    </span>
                </td>
                <td class="field-input">
                    <input type="text" v-model="field.mock">
                </td>
                <td class="field-input"><input type="text" v-model="field.note" placeholder="暂未填写说明"></td>

                <td class="field-input">
                    <select v-model="field.type">
                        <option value="1">string</option>
                        <option value="2">number</option>
                        <option value="3">object</option>
                        <option value="4">list[obj]</option>
                        <option value="5">boolean</option>
                        <option value="6">null</option>
                        <option value="7">array</option>
                    </select>
                    <span class="lineNum" v-if="!hasReqField">{{$i+1}}</span>
                </td>
                <td class="td-operations" v-if="hasReqField">
                    <label class="checkbox"><input type="checkbox" v-model="field.req"></label>
                    <span class="lineNum">{{$i+1}}</span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</template>
<style lang="sass" rel="stylesheet/scss">
    /* 参数导入编辑框 */
    .field-code-editor {
        .field-input {
            padding: 0 5px;
            line-height: 28px;
            input {
                width: 100%;
                line-height: 28px;
                border: none;
            }
            &:last-child {
                position: relative;
            }
        }
    }

    .td-operation {
        position: absolute;
        left: 99%;
        top: 0;
        width: 120px;
        display: none;
        button {
            margin: 0 1px;
            float: left;
        }
    }

    .td-operations {
        position: relative;
    }

    .td-operations:hover .td-operation {
        display: block;
    }

    .field-key-input{
        border-left:1px dotted #ccc!important
    }

    .field-level-0{padding-left:0;color: #3d4e57;}
    .field-level-1{padding-left:30px;color: #6a6fff;}
    .field-level-2{padding-left:60px;color: #53bb57;}
    .field-level-3{padding-left:90px;color: #ff1cdc;}
    .field-level-4{padding-left:120px;color: #5ab4a6;}
    .field-level-5{padding-left:150px;color: #ada662;}

</style>
<script>
    import fieldsTools from '../../../../lib/fieldsTools'
    export default{
        props: ['type','initType'],
        data(){
            var data = {
                // 缩进颜色
                colors: ["#b33c39", "#6a6fff", "#53bb57", "#ff1cdc", "#5ab4a6", "#ada662"]
            };
            var isCreator = this.initType == 'creator';

            if (this.type == 'REQ') {
                data.fieldList = isCreator ? this.$store.state.creatorData.reqList : this.$store.state.reqList;
                data.hasReqField = true;
                data.paramText = "请求实例(Request Body):";
                data.paramGlyphicon = "glyphicon-cloud-download";
                data.prmSummaryBak = isCreator ? [] : this.$store.state.backupFields.reqFields;

            }

            if (this.type == 'RES') {
                data.fieldList = isCreator ? this.$store.state.creatorData.rspList : this.$store.state.rspList;
                data.hasReqField = false;
                data.paramText = "响应实例(Response Body):";
                data.paramGlyphicon = "glyphicon-cloud-upload";
                data.prmSummaryBak = isCreator ? [] : this.$store.state.backupFields.resFields;
            }

            return data
        },
        watch:{
            fieldList:function (fields) {
                var isCreator = this.$props.initType == 'creator';

                // 产生变更时，更改列表数据
                if(isCreator){
                    if (this.type == 'REQ') this.$store.state.creatorData.reqList = fields;
                    if (this.type == 'RES') this.$store.state.creatorData.rspList = fields;
                }else {
                    if (this.type == 'REQ') this.$store.state.reqList = fields;
                    if (this.type == 'RES') this.$store.state.rspList = fields;
                }

            }
        },
        methods: {
            checkField(field) {

            },

            // 参数列表JSON化
            stringifyFields : function() {
                // 当前所有参数列表
                var nowList = this.fieldList;
                if(!nowList){
                    // 当前字段列表不存在时的逻辑
                    return "/*不存在list*/{}";
                }
                var value = {};
                // 1.由于参数列表可能发生修改,json的组织,依赖于parent,需要重新组织parent
                fieldsTools.reorganizeParent(nowList);
                // 2.组织json
                fieldsTools.relatedList2JSON(value,nowList,nowList);
                return JSON.stringify(value,null,3)
            },
            addRootField(){
                this.addChildFieldByLevel("0");
            },
            addNewSiblingField(field){
                var currentLevel = field._level;
                var currentLevelArr = currentLevel.split("-");
                currentLevelArr.pop();
                var parentLevel = currentLevelArr.join("-");
                this.addChildFieldByLevel(parentLevel)
            },
            addNewChildField(field){
                // 获取当前节点level
                var fieldLevel = field._level;
                this.addChildFieldByLevel(fieldLevel)
            },

            addFieldFromJSON(){
                var self = this;
                self.$store.dispatch('fillDatesToJsonExporter',this);
            },
            // 新增子节点
            addChildFieldByLevel(fieldLevel){
                // 获取映射关系表
                var fieldList;
                this.reRenderFields();
                fieldList = this.fieldList;

                // 获取当前节点length
                var childIndex = this.getChildIndexByLevel(fieldLevel);
                var childLevel = fieldLevel + "-" + childIndex;
                var colors = this.colors;
                var indent = childLevel.split("-").length - 2;

                // 新建对象并初始化
                var newField = {};
                newField._level = childLevel;
                newField._color = colors[indent];
                newField._indent = indent;
                // 设置默认属性
                newField.id = "";
                newField.k = "";
                newField.parent = ""; // 必须
                newField.mock = "null";
                newField.type = "6"; // 默认为null
                newField.note = ""; // 必须
                newField.req = true;


                var editingCode = {};
                var editingLineNumber = 0;
                // 空校验
                var isEmpty = fieldList.some(function (it, id) {
                    // 判断提交是否和初始空值相重复
                    // 即:level前缀相同,并且参数名相同
                    if (fieldsTools.isSameLevel(it._level, newField._level) && it.k == newField.k) {
                        editingCode = it;
                        editingLineNumber = id + 1;
                        return true;
                    } else {
                        return false;
                    }
                });
                // 重复校验
                var hasRepeat = this.fieldsRepeatCheck();

                // 插入
                if (isEmpty) {
                    this.$store.dispatch('alert',"第 " + editingLineNumber + " 行未定义参数:\n\n\t参数:" + editingCode.k + "\n\t说明:" + editingCode.note)
                } else if (hasRepeat) {
                    this.$store.dispatch('alert',"第 " + hasRepeat.comparedLine + " 行与第 " + hasRepeat.compareLine + " 行参数重复:\n\n\t参数:" + hasRepeat.repeatItem.k + "\n\t说明:" + hasRepeat.repeatItem.note);
                } else {
                    this.fieldList.push(newField);
                    this.reRenderFields();
                }
            },
            deleteFields(field){
                var parentLevel = field._level;
                var fields = this.fieldList;
                if(field){
                    // 删除本身和子节点
                    // 逆序遍历的原因在于
                    // 由于js的原因,正序遍历会导致索引变化使遍历中断
                    for(var i=fields.length;i>0 ; i--){
                        var it = fields[i-1];
                        var currentLevel = it._level;
                        if(currentLevel.indexOf(parentLevel) == 0){
                            fields.splice(i-1,1);
                        }else{
                        }
                    }

                }
            },

            /**
             *
             * 包装,并重设返回参数列表
             * */
            reRenderFields(){
                var colors = this.colors;
                var fields = this.fieldList;
                this.renderLevelOfFields(fields);
                fields.forEach(function (it) {
                    if (it.hasOwnProperty("_level")) {
                        // 确认缩进
                        var indent = it._level.split("-").length - 2;
                        it._indent = indent;
                        // 确认颜色
                        it._color = colors[indent];
                    } else {
                        console.log("渲染失败,渲染前,请确认field对象已经加入了level")
                    }
                });
                // 触发重绘
                this.fieldList = fields;
            },


            /**
             *  校验当前类型参数是否存在重复
             *  返回校验结果
             *  */
            fieldsRepeatCheck(){
                // 当前列表
                var nowList = this.fieldList;
                if (!nowList) {
                    // 当前字段列表不存在时的逻辑
                    // todo 弹框等处理
                    return false;
                }
                // 比较行数
                var operLine = 0;
                // 重复对象
                var repeatItem;
                // 参照行数
                var comparedLine = 0;
                var ifRepeat = nowList.some(function (operateItem, id) {
                    // 当前被比较的行数自增
                    operLine++;
                    //重复次数 [1,1<<]
                    var repeatCount = 0;
                    // 参照行数
                    var _comparedLine = 0;
                    // 重复次数该是1 ,发现相同则应该大于1
                    return nowList.some(function (compareItem) {
                        // 当前参照的行数自增
                        _comparedLine++;
                        // 转存
                        comparedLine = _comparedLine;
                        // 比较二元是否一致
                        if (fieldsTools.isSameLevel(operateItem._level, compareItem._level) && operateItem.k == compareItem.k)
                            repeatCount++;

                        // 发现发现重复则
                        if (repeatCount > 1) {
                            // 确定重复对象
                            repeatItem = compareItem;
                            return true;
                        } else {
                            return false;
                        }
                    });
                });
                return ifRepeat ? {
                    compareLine: operLine,
                    comparedLine: comparedLine,
                    repeatItem: repeatItem
                } : false;
            },


            /**
             * 已知parent的前提下
             * todo 出现只能遍历第一层的BUG,需要关注
             * 包装field 加入level ,
             * 同时维护层级路径列表{"0-1-1-1-1":{length:,keypath:}}
             * @param fieldList
             */
            renderLevelOfFields(fieldList){
                var self = this;
                // 初始化层级关系
                self.fieldIndexes = {};
                // 包装 level
                wrap(fieldList, self.fieldIndexes);
                // 排序
                fieldList.sort(function (x, y) {
                    if (x._level > y._level)return 1;
                    if (x._level == y._level)return 0;
                    if (x._level < y._level)return -1;
                });

                function wrap(fieldList, parentLevelMap) {
                    if (fieldList.length <= 0)return false
                    var literateItems = []
                    fieldList.forEach(function (field) {
                        if (!field.hasOwnProperty("_level")) {
                            // 节点当前k和父k
                            var keyPath, parent = field.parent;
                            if (parent == "") {
                                keyPath = field.k;
                            } else {
                                keyPath = parent + "." + field.k;
                            }

                            // 初始化字典
                            if (!parentLevelMap.hasOwnProperty("")) {
                                parentLevelMap[""] = {length: 0, level: "0"}
                            }

                            if (parentLevelMap.hasOwnProperty(parent)) {
                                // 维护根节点高度
                                parentLevelMap[parent].length++;
                                // 维护下级节点字典
                                parentLevelMap[keyPath] = {
                                    length: 0,
                                    level: parentLevelMap[parent].level + "-" + parentLevelMap[parent].length
                                };
                                // 设置当前节点level
                                field._level = parentLevelMap[parent].level + "-" + parentLevelMap[parent].length;
                            } else {
                                literateItems.push(field);
                            }
                        }
                    });

                    if (fieldList.length <= literateItems.length) {
                        // 无限递归 退出
                        console.log("如下节点找不到父节点,渲染可能有问题");
                        console.log(fieldList);
                        return false;
                    }

                    if (literateItems.length > 0) { // todo 很有可能是此行判断有问题,导致无法进行递归
                        wrap(literateItems, parentLevelMap)
                    }
                }
            },

            /**
             * 计算当前层级关系下,子节点应该的取值
             * 如:
             * 0-1-1-1 下的节点
             * 0-1-1-1-10
             * 0-1-1-1-55
             * 最新的子节点取值应该是
             * 0-1-1-1-56
             * 具体算法为:
             * 1.找到与正则 0-1-1-1-\d* 相匹配的元素
             * 2.取出最后一个数字,拼到数组
             * 3.Math.max + 1
             * @param level
             * @param fieldList
             * @returns {number}
             */
            getChildIndexByLevel(level){
                var fieldList = this.fieldList;
                var childrenIndexes = [];
                var reg = new RegExp("^" + level + '-(\\d+)$', 'gi');
                fieldList.forEach(function (field) {
                    reg.lastIndex = 0;// 重置游标
                    var $level = field._level;
                    var result = reg.exec($level);
                    if (result && result[1]) {
                        childrenIndexes.push(result[1])
                    }
                });
                var maxIndex = 0;
                if (childrenIndexes.length > 0) {
                    maxIndex = Math.max.apply(Math, childrenIndexes);
                }

                return maxIndex + 1;
            }
        },
        created(){
            this.reRenderFields();
        }
    }
</script>