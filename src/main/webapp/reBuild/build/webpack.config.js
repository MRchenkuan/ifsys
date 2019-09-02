// nodejs 中的path模块
var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var config = require('../config/index');
var projectRoot = path.resolve(__dirname, '../');

module.exports = {
    // 入口文件，path.resolve()方法，可以结合我们给定的两个参数最后生成绝对路径，最终指向的就是我们的index.js文件
    entry:  {
        index:path.resolve(config.build.root,'src/main.js'),
        // 抽出公共库
        vendor:['Vue','vue-router','vuex']
    },
    // 输出配置
    output: {
        // bundle.js的输出路径
        path: path.resolve(config.build.root,'dist/'),
        // 页面中的引用路径 
        publicPath:  '',
        filename: 'static/js/[name].[hash].js',
        // filename: 'static/js/[name].[chunkhash].js',
        chunkFilename:"static/js/[id].[chunkhash].js"
    },
    resolve: {
        extensions: ['', '.js', '.vue', '.scss', '.less'],
        alias: {
            ace: path.resolve(config.build.root,'modules/codeEditor/ace')
        }
    },
    module: {

        loaders: [
            {
                test: /\.css$/,
                loader:  ExtractTextPlugin.extract("style-loader","css-loader"),
                name:"css/[name].[hash:7].css"
            },
            // 使用vue-loader 加载 .vue 结尾的文件
            {
                test: /\.vue$/,
                loader: 'vue'
            },
            {
                test: /\.js$/,
                loader: 'babel',
                include:projectRoot,
                exclude: /node_modules/
            },
            {
                test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
                loader: 'url',
                query: {
                    limit: 10000,
                    name: 'static/img/[name].[hash:7].[ext]',
                    publicPath:"../../",
                }
            },
            {
                test: /\.json$/,
                loader: 'json'
            },
            {
                test:/\.(woff|eot|ttf)\??.*$/,
                loader: 'url',
                query:{
                    limit:10000,
                    name:"static/font/[name].[hash:7].[ext]",
                    publicPath:"../../",
                }
            },
            //解析.scss文件,对于用 import 或 require 引入的sass文件进行加载，以及<style lang="sass">...</style>声明的内部样式进行加载
            {
                test: /\.scss$/,
                loader: ExtractTextPlugin.extract("style", 'css!sass') //这里用了样式分离出来的插件，如果不想分离出来，可以直接这样写 loader:'style!css!sass'
            }
        ]
    },
    // 转化成es5的语法
    babel: {
        presets: ['es2015'],
        plugins: ['transform-runtime']
    },

    plugins: [
        new HtmlWebpackPlugin({
            filename: './main.html',
            template: path.resolve(config.build.root,'./src/main.html'),
            inject: true
        }),
        new HtmlWebpackPlugin({
            filename: './index.html',
            template: path.resolve(config.build.root,'./src/index.html'),
            inject: false
        }),
        new HtmlWebpackPlugin({
            filename: './projects.html',
            template: path.resolve(config.build.root,'./src/projects.html'),
            inject: false
        }),
        new HtmlWebpackPlugin({
            filename: './unauthorized.html',
            template: path.resolve(config.build.root,'./src/unauthorized.html'),
            inject: false
        }),
        new ExtractTextPlugin('static/css/[name].[hash].css'),
        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor' // 指定一个希望作为公共包的入口
        }),
        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            }
        })
    ],
    // externals: {
    //     'Vue': 'Vue'
    // }

}