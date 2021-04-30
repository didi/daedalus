/* eslint-disable */
/** 运行输入键组队自定义表单控件 */
import React, { Fragment, Component } from 'react';
import { Row, Col, Icon, Input, message, Tooltip, Dropdown, Menu } from 'antd';
import './index.scss';
import _ from 'lodash';
const { TextArea } = Input;

class KeyValueOption extends Component {

  keyValue = value => {
    const { leftName, rightName, disabled } = this.props;
    return value.map((item, i) => (
      <Row key={i} style={{ marginBottom: i === value.length - 1 ? 0 : 10 }}>
        <Col span={disabled ? 11 : 10}>
          <Input
            placeholder={disabled ? null : `请输入${leftName}`}
            onChange={e => this.changeName(e, item.id, value)}
            value={item.display}
            disabled={disabled}
          />
        </Col>
        <Col
          span={disabled ? 12 : 10}
          className={'keyValueName'}
        >
          <TextArea
            placeholder={disabled ? null : `请输入${rightName}`}
            onChange={e => this.changeValue(e, item.id, value)}
            value={item.value}
            className="wordBreak"
            autoSize={{ minRows: 1, maxRows: 4 }}
            disabled={disabled}
          />
        </Col>
        {
          !disabled && <Col span={4} className="keyValueValue">
          {
            i === value.length - 1 &&
            <Tooltip placement="top" title='添加额外变量'>
              <Icon
                type="plus"
                className="icon parentAdd"
                onClick={() => this.addname(value)}
              />
            </Tooltip>
          }
          <Tooltip placement="top" title='删除额外变量'>
            <Icon
              type="close"
              className="icon parentDelete"
              onClick={() => this.deletename(item.id, value)}
            />
          </Tooltip>
          <Dropdown
            overlay={
              <Menu onClick={() => this.addChildrenName(item.id)}>
                <Menu.Item>添加关联变量</Menu.Item>
              </Menu>
            }
            placement="bottomLeft"
          >
            <Icon type="more" className="cursor" />
          </Dropdown>
        </Col>
        }
        {item.extraVars && item.extraVars.length > 0 && this.keyValueChildren(item.id, item.extraVars)}
      </Row>
    ))
  }
  keyValueChildren = (parentsId, value) => {
    const { disabled } = this.props;
    return value.map((item, i) => (
      <Col span={24}>
      <Row key={i} style={{ marginBottom: i === value.length - 1 ? 0 : 10 }}>
        <Col span={disabled ? 10 : 9} style={{ marginLeft: '4%' }}>
          <Input
            placeholder={disabled ? null : `请输入变量名`}
            onChange={e => this.changeName(e, item.id, value, 'children', parentsId)}
            value={item.name}
            disabled={disabled}
          />
        </Col>
        <Col
          span={disabled ? 11 : 9}
          className={'keyValueName'}
          style={{ marginLeft: '4%' }}
        >
          <TextArea
            placeholder={disabled ? null : `请输入变量值`}
            onChange={e => this.changeValue(e, item.id, value, 'children', parentsId)}
            value={item.value}
            className="wordBreak"
            autoSize={{ minRows: 1, maxRows: 4 }}
            disabled={disabled}
          />
        </Col>
        {
          !disabled && <Col span={4} className="keyValueValue">
          {
            i === value.length - 1 &&
            <Tooltip placement="top" title='添加关联变量'>
              <Icon
                type="plus"
                className="icon chidlrenAdd"
                onClick={() => this.addname(value, 'children', parentsId)}
              />
            </Tooltip>
          }
          <Tooltip placement="top" title='删除关联变量'>
            <Icon
              type="close"
              className="icon childrenDelete"
              onClick={() => this.deletename(item.id, value, 'children', parentsId)}
            />
          </Tooltip>
        </Col>
        }
      </Row>
      </Col>
    ))
  }
  // 添加name、value组
  addname = (value, type, parentsId) => {
    let allList = _.cloneDeep(this.props.value);
    const list = _.cloneDeep(value);
    let lastList = list[list.length - 1];
    let isComplete = type === 'children' ? lastList.name && lastList.value : lastList.display && lastList.value;
    if (isComplete) {
      list.push(type === 'children' ? {id: new Date().getTime()} : {id: new Date().getTime(), children: []});
      if(type === 'children') {
        allList.forEach(ele => {
          if(ele.id === parentsId){
            ele.extraVars = list;
          }
        })
      } else {
        allList = list;
      }
      this.props.onChange(allList);
    } else {
      message.info('填写完整，方可继续添加');
    }
  }
  // 添加子级name、value组
  addChildrenName = id => {
    const list = _.cloneDeep(this.props.value);
    let extraVars = list.find(item => item.id === id).extraVars || [];
    extraVars.push({ id: new Date().getTime() });
    list.forEach(item => {
      if (item.id === id) item.extraVars = extraVars;
    });
    this.props.onChange([...list]);
  }
  // 删除name、value组
  deletename = (key, value, type, parentsId) => {
    let allList = _.cloneDeep(this.props.value);
    let list = [];
    if (value.length <= 1) {
      list = type === 'children' ? [] : [{ id: new Date().getTime() }];
    } else {
      list = value.filter(it => it.id !== key);
    }
    if(type === 'children') {
      allList.forEach(ele => {
        if(ele.id === parentsId){
          ele.extraVars = list;
        }
      })
    } else {
      allList = list;
    }
    this.props.onChange(allList);
  }
  // 改变value
  changeValue = (e, key, value, type, parentsId) => {
    let allList = _.cloneDeep(this.props.value);
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) ele.value = e.target.value;
    });
    if(type === 'children') {
      allList.forEach(ele => {
        if(ele.id === parentsId){
          ele.extraVars = list;
        }
      })
    } else {
      allList = list;
    }
    this.props.onChange(allList);
  }
  // 改变key
  changeName = (e, key, value, type, parentsId) => {
    let allList = _.cloneDeep(this.props.value);
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key){
        type === 'children' ? ele.name = e.target.value : ele.display = e.target.value;
      }
    });
    if(type === 'children') {
      allList.forEach(ele => {
        if(ele.id === parentsId){
          ele.extraVars = list;
        }
      })
    } else {
      allList = list;
    }
    this.props.onChange(allList);
  }

  render() {
    const { value, leftName, rightName, disabled } = this.props;
    let leftSpan = disabled ? 11 : 10;
    let rightSpan = disabled ? 12 : 10;
    return (
      <Fragment>
        {value && value.length > 0 &&
        <Row className="keyValueTitle">
          <Col span={leftSpan}>{leftName}</Col>
          <Col span={rightSpan} className="rightName">{rightName}</Col>
        </Row>
        }
        {this.keyValue(value)}
      </Fragment>
    );
  }
}

export default KeyValueOption;