import { BIZLINE, TAG, OPERATOR, DUBBO_PARAM_TYPE, DATE_FORMAT, ENV_GROUP_LiST  } from '../action/actionType';

const initState = {
  bizLine: [],
  tag: [],
  operatorList: [],
  dubboParamType: [],
  dateFormat: [],
  envList: [],
};

function getInitialValueReducer(state = initState, action) {
  switch (action.type) {
    case BIZLINE:
      return { ...state, ...action.payload };
    case TAG:
      return { ...state, ...action.payload };
    case OPERATOR:
      return { ...state, ...action.payload };
    case DUBBO_PARAM_TYPE:
      return { ...state, ...action.payload };
    case DATE_FORMAT:
      return { ...state, ...action.payload };
    case ENV_GROUP_LiST:
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
}

export default getInitialValueReducer;