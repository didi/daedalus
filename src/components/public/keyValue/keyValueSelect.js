/** 运行输入-关联值 */
import React, { Fragment, Component } from 'react';
import { Icon, Select } from 'antd';
import _ from 'lodash';
const { Option } = Select;

class KeyValueSelect extends Component {
  state = {

  }
  // 删除
  delete = (id, value) => {
    let list = [];
    if (value.length <= 1) {
      list = [{ id: new Date().getTime() }];
    } else {
      list = value.filter(it => it.id !== id);
    }
    this.props.onChange(list);
  }
  // 添加
  add = value => {
    const list = _.cloneDeep(value);
    list.push({ id: new Date().getTime() });
    this.props.onChange(list);
  }
  // 改变左边
  changeTarget = (e, key, value) => {
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) {
        ele.targetOptions = e;
        ele.showOnOptions = ele.showOnOptions || [];
      }
    });
    this.props.onChange(list);
  }
  // 改变右边
  changeShow = (e, key, value) => {
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) {
        ele.targetOptions = ele.targetOptions || [];
        ele.showOnOptions = e;
      }
    });
    this.props.onChange(list);
  }


  render() {
    let { value, disabled, record, runInputOptions, options } = this.props;
    options = (options || record.options).filter(item => item.value && item.display);
    return (
      <Fragment>
        {
          value && value.length > 0 && value.map((item, i) => (
            <div style={{ marginBottom: value.length > 1 && i !== value.length - 1 ? '10px' : '0' }} key={i}>
              <Select
                style={{ width: '44%' }}
                mode="multiple"
                placeholder="请选择关联值"
                disabled={disabled}
                value={item.targetOptions}
                onChange={e => this.changeTarget(e, item.id, value)}
              >
                {
                  options && options.length > 0 && options.map(item => (
                    <Option key={item.id} value={item.value}>{item.display}</Option>
                  ))
                }
              </Select>
              <Select
                style={{ width: '44%', margin: '0px 10px' }}
                mode="multiple"
                placeholder="请选择关联选项"
                disabled={disabled}
                value={item.showOnOptions}
                onChange={e => this.changeShow(e, item.id, value)}
              >
                {
                  runInputOptions && runInputOptions.length > 0 && runInputOptions.map(item => (
                    <Option key={item.id} value={item.value}>{item.display}</Option>
                  ))
                }
              </Select>
              {
                !disabled && <span>
                  {
                    i === value.length - 1 && <Icon
                      type="plus-circle"
                      theme="filled"
                      style={{ marginRight: '4px', fontSize: '16px' }}
                      onClick={() => this.add(value)}
                    />
                  }
                  <Icon
                    type="minus-circle"
                    theme="filled"
                    style={{ fontSize: '16px' }}
                    onClick={() => this.delete(item.id, value)}
                  />
                </span>
              }
            </div>
          ))
        }
      </Fragment>
    );
  }
}

export default KeyValueSelect;