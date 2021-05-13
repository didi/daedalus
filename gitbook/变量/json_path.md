### JSON Path

支持的json path如下：

| JSONPATH                  | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| $                         | 根对象，例如$.name                                           |
| [num]                     | 数组访问，其中num是数字，可以是负数。例如$[0].leader.departments[-1].name |
| [num0,num1,num2...]       | 数组多个元素访问，其中num是数字，可以是负数，返回数组中的多个元素。例如$[0,3,-2,5] |
| [start:end]               | 数组范围访问，其中start和end是开始小表和结束下标，可以是负数，返回数组中的多个元素。例如$[0:5] |
| [start:end :step]         | 数组范围访问，其中start和end是开始小表和结束下标，可以是负数；step是步长，返回数组中的多个元素。例如$[0:5:2] |
| [?(key)]                  | 对象属性非空过滤，例如$.departs[?(name)]                     |
| [key > 123]               | 数值类型对象属性比较过滤，例如$.departs[id >= 123]，比较操作符支持=,!=,>,>=,<,<= |
| [key = '123']             | 字符串类型对象属性比较过滤，例如$.departs[name = '123']，比较操作符支持=,!=,>,>=,<,<= |
| [key like 'aa%']          | 字符串类型like过滤， 例如$.departs[name like 'sz*']，通配符只支持% 支持not like |
| [key rlike 'regexpr']     | 字符串类型正则匹配过滤， 例如departs[name like 'aa(.)*']， 正则语法为jdk的正则语法，支持not rlike |
| [key in ('v0', 'v1')]     | IN过滤, 支持字符串和数值类型 例如: $.departs[name in ('wenshao','Yako')] $.departs[id not in (101,102)] |
| [key between 234 and 456] | BETWEEN过滤, 支持数值类型，支持not between 例如: $.departs[id between 101 and 201] $.departs[id not between 101 and 201] |
| length() 或者 size()      | 数组长度。例如$.values.size() 支持类型java.util.Map和java.util.Collection和数组 |
| keySet()                  | 获取Map的keySet或者对象的非空属性名称。例如$.val.keySet() 支持类型：Map和普通对象 不支持：Collection和数组（返回null） |
| .                         | 属性访问，例如$.name                                         |
| ..                        | deepScan属性访问，例如$..name                                |
| *                         | 对象的所有属性，例如$.leader.*                               |
| ['key']                   | 属性访问。例如$['name']                                      |
| ['key0','key1']           | 多个属性访问。例如$['id','name']                             |