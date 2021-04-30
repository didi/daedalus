import { combineReducers } from 'redux';
import getInitialValueReducer from './getInitialValue';
import setNodeDataReducer from './setNodeDataReducer';

const reducer = combineReducers({
  initialValueObj: getInitialValueReducer,
  nodeDataObj: setNodeDataReducer
});

export default reducer;