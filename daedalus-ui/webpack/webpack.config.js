var devServer = require('webpack-dev-server')
var webpack = require('webpack')
var path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
module.exports = (env) => {
  return {
    devtool: 'source-map',
    externals: {
      jquery: 'jQuery',
    },
    entry: {
      main: path.resolve(__dirname, '../src/index.js'),
      react: ['react', 'react-dom', 'react-router'],
      antd: ['antd'],
    },
    output: {
      path: path.resolve(__dirname, '../build/'),
      filename: 'js/[name].js', //注意这里，用[name]可以自动生成路由名称对应的js文件
      publicPath: '/',
      chunkFilename: 'js/[name].js',
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '../'),
      },
    },
    // devServer: {
    //   historyApiFallback: true,
    //   host: '0.0.0.0',
    //   port: 4000,
    //   inline: true,
    //   hot: true,
    // },
    module: {
      rules: [
        {
          test: /\.css$/,
          loader: ['style-loader', 'css-loader'],
        },
        {
          test: /\.scss$/,
          loader: ['style-loader', 'css-loader', 'sass-loader'],
        },
        {
          test: /\.less$/,
          use: [
            {
              loader: 'style-loader',
            },
            {
              loader: 'css-loader', // translates CSS into CommonJS
            },
            {
              loader: 'less-loader', // compiles Less to CSS
              options: {
                modifyVars: {
                  'font-size-base': '16px',
                  'btn-height-base': '40px',
                  'input-height-base': '40px',
                },
                javascriptEnabled: true,
              },
            },
          ],
        },
        { test: /\.(ttf|eot|svg|woff|woff2)$/, loader: 'url-loader' }, // 处理 字体文件的 loader
        {
          test: /\.(png|svg|jpg|gif)$/,
          loader: 'url-loader',
          options: {
            limit: 10000,
            name: 'img/[name].[ext]',
          },
        },
        {
          test: /\.(js|jsx)$/,
          loader: 'babel-loader',
          exclude: /node_modules/,
        },
      ],
    },
    // optimization: {
    // 	splitChunks: {
    // 			cacheGroups: {
    // 					commons: {
    // 							test: /[\\/]node_modules[\\/]/,
    // 							name: "vendor",
    // 							chunks: "all"
    // 					}
    // 			}
    // 	}
    // },
    plugins: [
      // new webpack.optimize.ModuleConcatenationPlugin(),
      // new webpack.optimize.CommonsChunkPlugin({
      //   name:["react", "antd"],
      //   minChunks:2
      // }),
      new HtmlWebpackPlugin({
        filename: './index.html',
        template: path.resolve(__dirname, '../public/index.html'),
        chunks: ['react', 'antd', 'main'],
      }),
      new MonacoWebpackPlugin([
        'javascript', 'mysql', 'redis', 'typescript', 'java', 'sql'
      ]),
    ],
  }
}
