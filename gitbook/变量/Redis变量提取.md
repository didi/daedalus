# Redis变量提取

Redis命令的返回结果与命令有关。

- `get`，返回结构直接是一个字符串

- `hget` `hmget` 等操作hash的返回结果是一个对象，需要用到json path来提取