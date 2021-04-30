import { NODE_DATA } from '../action/actionType';

const initState = {
  nodeData: {},
};

function setNodeDataReducer(state = initState, action) {
  switch (action.type) {
    case NODE_DATA:
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
}

export default setNodeDataReducer;