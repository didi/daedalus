import { BIZLINE, TAG, OPERATOR, DUBBO_PARAM_TYPE, DATE_FORMAT, ENV_GROUP_LiST } from './actionType';

const intialValueAction = {
  // 业务线
  setBizLine(payload) {
    return { type: BIZLINE, payload };
  },
  // 标签
  setTag(payload) {
    return { type: TAG, payload };
  },
  // 执行条件
  setOperator(payload) {
    return { type: OPERATOR, payload };
  },
  // dubboParamType
  setDubboParamType(payload) {
    return { type: DUBBO_PARAM_TYPE, payload };
  },
  // 日期格式
  setDateFormat(payload) {
    return { type: DATE_FORMAT, payload };
  },
  // 环境组信息
  setEnvGroup(payload) {
    return { type: ENV_GROUP_LiST, payload };
  },
};

export default intialValueAction;