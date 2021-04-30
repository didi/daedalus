/** 变量提取键组队 */
import React, { Fragment, Component } from 'react';
import { Row, Col, Icon, Input, Select, message, Tooltip, Form, AutoComplete } from 'antd';
import { connect, } from 'react-redux';
import intialValueAction from '../../../store/action/intialValueAction';
import Mapping from '../mapping';
import './index.scss';
import _ from 'lodash';
const { threeLineLayout, locationList } = Mapping;
const { Option } = Select;

class ExtractKeyValue extends Component {

  // 删除name、value组
  deletename = (key, itemList) => {
    let list = [];
    if (itemList.length <= 1) {
      list = [{ id: new Date().getTime() }];
    } else {
      list = itemList.filter(it => it.id !== key);
    }
    // let selfFilter = list.filter(it => it.name === itemList.find(it => it.id === key).name);
    // if (selfFilter.length > 1) {
    //   list.forEach(ele => {
    //     if (ele.name === e.target.value) ele.showError = true;
    //   });
    // }
    // // 与外面不变的值作对比
    // this.props.allList.map(it => {
    //   list.forEach(ele => {
    //     if (ele.name === it.value) {
    //       ele.showError = true;
    //     }
    //   });
    //   return it;
    // });

    this.props.onChange([...list]);
  }
  // 添加name、value组
  addname = itemList => {
    let list = _.cloneDeep(itemList);
    if (!list.some(it => it.hasOwnProperty('showError'))) {
      list.push({ id: new Date().getTime() });
      this.props.onChange([...list]);
    } else {
      message.info('填写正确后，方可继续添加');
    }
  }
  // 变量提取 提取点
  extractPointChange = (location, key) => {
    const { value } = this.props;
    let list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) {
        ele.location = location;
        ele.path = '';
      }
    });
    this.props.onChange([...list]);
  }
  // 变量提取 变量点
  variableNameChange = (e, key) => {
    const { value, allList } = this.props;
    let list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) ele.name = e.target.value;
    });
    // 判断自己本身的list是否有相同值
    let selfFilter = list.filter(it => it.name === e.target.value);
    if (selfFilter.length >= 2) {
      list.forEach(ele => {
        if (ele.name === e.target.value) ele.showError = true;
      });
    } else {
      list.forEach(ele => {
        delete ele.showError;
      });
    }
    // 与外面不变的值作对比
    allList.map(it => {
      list.forEach(ele => {
        if (ele.name === it.value) {
          ele.showError = true;
        }
      });
      return it;
    });
    this.props.onChange([...list]);
  }
  // 变量提取 变量路径
  variablePathChange = (e, key) => {
    const { value } = this.props;
    let list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) ele.path = typeof e === 'string' ? e : e.target.value;
    });
    this.props.onChange([...list]);
  }

  render() {
    const { value, useAddItemName } = this.props;
    return (
      <Fragment>
        <Row className="extract">
          <Col span={24}>
            <Form.Item label="变量提取" {...threeLineLayout} className="extractTitle">
              <Tooltip placement="top" title={
                <Fragment>
                  <div>1、提取点数据为单一结果时(如数字、字符串)，变量路径可直接为空</div>
                  <div>2、提取点数据为对象时，变量路径填写json path</div>
                </Fragment>
              }>
                <Icon type="question-circle" theme="filled" />
              </Tooltip>
            </Form.Item>
          </Col>
        </Row>
        {value.map((item, i) => (
          <Row key={item.id} className="m_b_10">
            <Col span={6}>
              <Select
                style={{ width: '96%' }}
                placeholder="提取点"
                allowClear
                onChange={value => this.extractPointChange(value, item.id)}
                value={item.location}
              >
                {
                  locationList && locationList.map(item => (
                    <Option key={item.key} value={item.key}>{item.value}</Option>
                  ))
                }
              </Select>
            </Col>
            <Col span={7}>
              <Input
                style={{ marginLeft: '-1%' }}
                placeholder="变量名"
                onChange={e => this.variableNameChange(e, item.id)}
                value={item.name}
              />
              {item.showError && <span className="promptMsg">变量名已重复，请重新输入...</span>}
            </Col>
            <Col span={7}>
              {
                item.location !== 'HTTP_HEADER' ?
                  <Input
                    placeholder={item.location && item.location !== 'RESULT' ? 'header name' : '变量路径'}
                    className="headerName"
                    onChange={e => this.variablePathChange(e, item.id)}
                    value={item.path}
                  /> :
                  <AutoComplete
                    placeholder={'header name'}
                    className="headerName"
                    onChange={e => this.variablePathChange(e, item.id)}
                    value={item.path}
                  >
                    <Option key={'Set-Cookie'} value={'Set-Cookie'}>Set-Cookie</Option>
                    <Option key={'Content-Type'} value={'Content-Type'}>Content-Type</Option>
                  </AutoComplete>
              }
            </Col>
            <Col span={2} className="keyValueValue operating">
              {
                i === value.length - 1 && <Icon
                  type="plus-circle"
                  theme="filled"
                  className="icon"
                  onClick={() => this.addname(value)}
                />
              }
              <Icon
                type="minus-circle"
                theme="filled"
                className="icon"
                onClick={() => this.deletename(item.id, value)}
              />
            </Col>
          </Row>
        ))}
        {
          useAddItemName === 'MYSQL' &&
          <div className="sqlPrompt">
            SQL返回结果是<span className="promptBig">数组对象</span>，{`例如： [{"id":578712552132298385}] ，提取id时变量路径为[0].id`}
          </div>
        }
      </Fragment>
    );
  }
}

export default connect((state) => ({
  dubboParamType: state.initialValueObj.dubboParamType,
}), intialValueAction)(ExtractKeyValue);