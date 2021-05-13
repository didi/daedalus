# MYSQL变量提取

在Mysql类型的Step中执行select语句返回值都是一个对象数组：



### Example1:

SQL: `select id from table1 where x_id =1;`

返回 `[{"id":578712552132298385}]`

如果想使用id，变量提取的路径为：`[0].id`