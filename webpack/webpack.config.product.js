var path = require('path')
var webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
// const CleanWebpackPlugin = require('clean-webpack-plugin');
var UglifyJsPlugin = require('uglifyjs-webpack-plugin')
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
var definePlugin = new webpack.DefinePlugin({
  'process.env': {
    NODE_ENV: '"production"',
  },
})
const Version = new Date().getTime()
module.exports = {
  externals: {
    jquery: 'jQuery',
  },
  entry: {
    main: path.resolve(__dirname, '../src/index.js'),
    vendors: ['react', 'react-dom', 'react-router', 'antd'],
  },
  output: {
    path: path.resolve(__dirname, '../build/'),
    filename: 'js/[name].[chunkhash].min.js', //注意这里，用[name]可以自动生成路由名称对应的js文件
    publicPath: '/',
    chunkFilename: 'js/[name].[chunkhash].min.js',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, '../'),
    },
  },
  devtool: 'cheap-module-source-map',
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
  optimization: {
    splitChunks: {
      chunks: 'initial',
      // cacheGroups:{
      //   vendors: {
      //     test: /node_modules\//,
      //     name: 'vendor',
      //     priority: 10,
      //     enforce: true,
      //   }
      // },
    },

    runtimeChunk: true,
    minimizer: [
      new OptimizeCssAssetsPlugin({}), // 压缩 css,使用minimizer会自动取消webpack的默认配置，所以记得用UglifyJsPlugin
      new UglifyJsPlugin({
        // 压缩 js
        uglifyOptions: {
          ecma: 6,
          cache: true,
          parallel: true,
        },
      }),
    ],
  },

  plugins: [
    new CleanWebpackPlugin(),
    new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),
    new OptimizeCssAssetsPlugin({
      assetNameRegExp: /\.optimize\.css$/g,
      cssProcessor: require('cssnano'),
      cssProcessorOptions: { safe: true, discardComments: { removeAll: true } },
      canPrint: true,
    }),
    new HtmlWebpackPlugin({
      filename: './index.html',
      hash: true,
      template: path.resolve(__dirname, '../public/index.product.html'),
      // chunks: ['main','react','ant']
    }),
  ],
}
