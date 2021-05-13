# Client

可以在代码中调用流水线

1. 引入依赖：

`<dependency>
    <groupId>com.didichuxing.daedalus</groupId>
    <artifactId>daedalus-client</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>`

2. 使用

流水线无输入：
`DaedalusClient.call(流水线id)`

流水线有输入：
`DaedalusClient.call(流水线id,参数,环境)`

