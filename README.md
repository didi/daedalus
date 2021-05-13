# Daedalus

## 什么是Daedalus（数据工厂）

日常开发、测试过程中的数据构造一直是一件比较麻烦的事情，每个人都有自己收藏的CURL命令、Swagger地址、SQL语句等等，部分功能有传统的数据工厂支持，但是需要前后端开发。

Daedalus就是为了解决这些问题而生，实现快速创建数据构造流程，数据构造流程的可视化、线上化、持久化、标准化。

Daedalus是一个平台，不包含任何数据构造流程，所有流程都需要用户手动创建。

## 如何做

Daedalus通过抽象数据构造过程，提供HTTP接口调用、Dubbo接口调用、发送MQ、MYSQL操作、Redis操作、Groovy脚本等基本能力（持续增加中），通过将这些能力编排为流水线实现数据构造场景。

同时流水线支持用户输入、多环境、全局变量，输出提取变量、条件选择、各种插件等功能辅助支持各种逻辑实现。


## 功能描述

* 组件式构建流水线，支持分支、条件等
* 支持dubbo、http、mysql、redis、email通知、groovy脚本等能力
* 自动表单渲染  
* 多环境支持
* 内置函数变量  
* 定时执行
* 流水线复用
* 目录管理
* 流水线调试
* 运行记录、运行日志
* 复制、分享
* ......

## 如何使用

### 环境依赖
mac/linux/windows
maven
java 1.8
mongoDB

### 步骤
1.下载

`git clone https://github.com/didi/daedalus.git`

2.修改配置
* application-*.properties中的mongodb地址，smtp服务相关信息

3.编译打包
*进入目录执行 mvn clean package -DskipTests

4.运行

`java -jar daedalus-server.jar`

5.打开 http://localhost:8080


## 部署

为了解决环境隔离问题（如线上环境和线下环境），Daedalus支持多环境部署，由主环境进行调度。

