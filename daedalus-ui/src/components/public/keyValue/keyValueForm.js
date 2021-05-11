/* eslint-disable */
/** 键组队自定义表单控件 */
import React, { Fragment, Component } from 'react';
import { Row, Col, Icon, Input, Select, message, AutoComplete, Tooltip, Modal, Button } from 'antd';
import { connect, } from 'react-redux';
import intialValueAction from '../../../store/action/intialValueAction';
import './index.scss';
import { JsonEditor as Editor } from 'jsoneditor-react';
import 'jsoneditor-react/es/editor.min.css';
import _ from 'lodash';
const { Option } = Select;
const { TextArea } = Input;

class KeyValueForm extends Component {
  state = {
    visible: false,
    jsonValue: '',
    josnId: undefined,
  }
  // 删除name、value组
  deletename = (key, value) => {
    let list = [];
    if (value.length <= 1) {
      list = [{ id: new Date().getTime() }];
    } else {
      list = value.filter(it => it.id !== key);
    }
    this.props.onChange([...list]);
  }
  // 添加name、value组
  addname = value => {
    const list = _.cloneDeep(value);
    list.push({ id: new Date().getTime() });
    this.props.onChange([...list]);
  }
  // 改变value
  changeValue = (e, key, value) => {
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) ele.value = e.target.value;
    });
    this.props.onChange([...list]);
  }
  // 改变key
  changeName = (e, key, value, subordinate) => {
    const list = _.cloneDeep(value);
    list.forEach(ele => {
      if (ele.id === key) {
        if (subordinate === 'params') {
          ele.type = e;
        }else {
          ele.name = e.target.value;
        }
      }
    });
    this.props.onChange([...list]);
  }
  // modalOk
  modalOk = () => {
    // 获取编辑区内填写的值
    // let text = this.editorNode.jsonEditor.getText();
    // text = text.replace(/\\n/g, '').replace(/\\/g, '').replace(/\ +/g,"");
    const list = _.cloneDeep(this.props.value);
    list.forEach(ele => {
      if (ele.id === this.state.josnId) {
        ele.value = this.state.jsonValue;
      }
    });
    this.props.onChange([...list]);
    this.setState({ visible: false, jsonValue: '' })
  }
  // 校验json
  checkValue = value => {
    try {
      JSON.parse(value);
    } catch (e) {
      return value;
    }
    return JSON.parse(value);
  }

  render() {
    const { value, leftName, rightName, subordinate, disabled } = this.props;
    const dubboParamType = this.props.dubboParamType || [];
    let leftSpan = disabled ? 12 : 11;
    let rightSpan = disabled ? 12 : 11;
    return (
      <Fragment>
        {value && value.length > 0 &&
        <Row className="keyValueTitle">
          <Col span={leftSpan}>{leftName}</Col>
          <Col span={rightSpan} className="rightName">{rightName}</Col>
        </Row>
        }
        {value && value.map((item, i) => (
          <Row key={i} style={{ marginBottom: i === value.length - 1 ? 0 : 10 }}>
            <Col span={leftSpan}>
              {
                subordinate === 'params' ?
                  <AutoComplete
                    onChange={e => this.changeName(e, item.id, value, subordinate)}
                    style={{ width: '100%' }}
                    placeholder={disabled ? null : `请输入${leftName}`}
                    value={item.type}
                    disabled={disabled}
                  >
                    {
                      dubboParamType.length > 0 && dubboParamType.map(item => (
                        <Option key={item.name} value={item.name}>{item.name}</Option>
                      ))
                    }
                  </AutoComplete> :
                  <Input
                    placeholder={disabled ? null : `请输入${leftName}`}
                    onChange={e => this.changeName(e, item.id, value, subordinate)}
                    value={item.name}
                    disabled={disabled}
                  />
              }
            </Col>
            <Col
              span={rightSpan}
              className={'keyValueName'}
            >
              {
                subordinate === 'params' ?
                  <Input
                    placeholder={disabled ? null : `请输入${rightName}`}
                    onChange={e => this.changeValue(e, item.id, value)}
                    value={item.value}
                    addonAfter={
                      <Tooltip title="切换至JSON编辑器">
                        <Icon type="edit" onClick={() => this.setState({ visible: true, jsonValue: item.value, josnId:item.id })} />
                      </Tooltip>
                    }
                    disabled={disabled}
                  /> :
                  <TextArea
                    placeholder={disabled ? null : `请输入${rightName}`}
                    onChange={e => this.changeValue(e, item.id, value)}
                    value={item.value}
                    className="wordBreak"
                    autoSize={{ minRows: 1, maxRows: 4 }}
                    disabled={disabled}
                  />
              }
            </Col>
            {
              !disabled && <Col span={2} className="keyValueValue">
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
          }
          </Row>
        ))}
        {this.state.visible &&
          <Modal
            title={'JSON编辑器'}
            visible={true}
            onCancel={() => this.setState({ visible: false })}
            width={700}
            footer={disabled ? null : 
              <div>
                <Button onClick={() => this.setState({ visible: false })} style={{ marginRight: 20 }}>取消</Button>
                <Button onClick={() => this.modalOk()} type="primary">确认</Button>
              </div>
            }
          >
            <Editor
              value={this.checkValue(this.state.jsonValue || '')}
              mode={'code'}
              allowedModes={['code', 'form', 'text', 'tree', 'view', 'preview']}
              history={true}
              search={true}
              onChange={e => {
                this.setState({ jsonValue: JSON.stringify(e) });
              }}
              onError={err => {
                if (err) {
                  message.error('输入错误');
                }
              }}
              ref={editorNode => this.editorNode = editorNode}
            />
          </Modal>
        }
      </Fragment>
    );
  }
}

export default connect((state) => ({
  dubboParamType: state.initialValueObj.dubboParamType,
}), intialValueAction)(KeyValueForm);