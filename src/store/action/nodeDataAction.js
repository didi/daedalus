import { NODE_DATA, } from './actionType';

const nodeDataAction = {
  // 流水线步骤
  setNodeData(payload) {
    return { type: NODE_DATA, payload };
  },
};

export default nodeDataAction;