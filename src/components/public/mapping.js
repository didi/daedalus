/* eslint-disable */
/** 固定值映射 */

// 环境表单配置、实例表单配置
const envOrExampleLayout = {
  labelCol: {
    xs: { span: 3 }
  },
  wrapperCol: {
    xs: { span: 21 }
  }
};
// 流水线表单配置
const oneLineLayout = {
  labelCol: {
    xs: { span: 4 }
  },
  wrapperCol: {
    xs: { span: 19 }
  }
};
const twoLineLayout = {
  labelCol: {
    xs: { span: 6 }
  },
  wrapperCol: {
    xs: { span: 17 }
  }
};
const threeLineLayout = {
  labelCol: {
    xs: { span: 12 }
  },
  wrapperCol: {
    xs: { span: 11 }
  }
};
const fourLineLayout = {
  labelCol: {
    xs: { span: 1 }
  },
  wrapperCol: {
    xs: { span: 22 }
  }
};
const fiveLineLayout = {
  labelCol: {
    xs: { span: 5 }
  },
  wrapperCol: {
    xs: { span: 18 }
  }
};
// 流水线-输入格式
const inputTypeList = [
  { key: 'INPUT', value: '输入框' },
  { key: 'NUMBER_INPUT', value: '数字输入框' },
  { key: 'TEXTAREA', value: '文本框' },
  { key: 'SELECT', value: '下拉框' },
  { key: 'RADIO', value: '单选框' },
  // { key: 'CHECKBOX', value: '多选框' },
  { key: 'DATE_PICKER', value: '日期选择' },
  { key: 'TIME_PICKER', value: '时间选择' },
  { key: 'DATE_TIME_PICKER', value: '日期时间选择' }
];
// 流水线-参数类型
const valueTypeList = [
  { key: 'STRING', value: '字符串' },
  { key: 'NUMBER', value: '数字' },
  { key: 'BOOLEAN', value: '布尔' },
];
const valueAllTypeList = [
  { key: 'STRING', value: '字符串' },
  { key: 'NUMBER', value: '数字' },
  { key: 'BOOLEAN', value: '布尔' },
  { key: 'ARRAY', value: '数组' }
];
// 流水线步骤-更多菜单
const moreMenuList = [
  { key: '执行条件', value: '执行条件' },
  { key: '执行延迟', value: '执行延迟' },
  { key: '别名', value: '别名' },
  { key: '前置脚本', value: 'Pre-Step Script (步骤前置脚本)' },
  { key: '后置脚本', value: 'Post-Step Script (步骤后置脚本)' },
  { key: '变量提取', value: '变量提取' }
];
// 流水线-操作类型
const operationType = ['SET', 'GET', 'SETEX', 'EXISTS', 'DEL', 'HMGET', 'HGETALL', 'EXPIRE', 'MGET'];
// 变量提取下拉
const locationList = [
  { key: 'RESULT', value: 'Result' },
  { key: 'HTTP_HEADER', value: 'Header' },
  { key: 'ATTACHMENT', value: 'Attachement' },
];
// 请求body类型
const bodyTypeList = [
  {
    key: 'JSON',
    value: 'application/json',
  },
  {
    key: 'FORM_URLENCODED',
    value: 'application/x-www-form-urlencoded',
  },
  {
    key: 'TEXT',
    value: 'text/plain',
  },
  {
    key: 'FORM_DATA',
    value: 'multipart/form-data',
  }
];

export default {
  envOrExampleLayout,
  oneLineLayout,
  twoLineLayout,
  threeLineLayout,
  fourLineLayout,
  fiveLineLayout,
  inputTypeList,
  valueTypeList,
  moreMenuList,
  operationType,
  locationList,
  valueAllTypeList,
  bodyTypeList
};
