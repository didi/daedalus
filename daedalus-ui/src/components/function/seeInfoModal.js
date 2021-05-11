/* eslint-disable */
import React, { Component } from 'react';
import { Input, Select, Form, Modal, Radio, Switch, Icon, Tooltip } from 'antd';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import Mapping from '../public/mapping';
const { fiveLineLayout } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

class seeInfoModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      allPerson: [],
    };
  }

  render() {
    const { modalData } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { allPerson } = this.state;
    const bizLines = this.props.bizLine || [];
    const tagList = this.props.tag || [];
    const envList = this.props.envList || [];
    return (
      <Modal
        title="基本信息"
        visible={true}
        width="700px"
        footer={null}
        onCancel={this.props.handleCancel}
      >
        <Form.Item label="流水线名称" {...fiveLineLayout} >
          {getFieldDecorator('name', {
            rules: [{ required: true, message: '请输入流水线名称' }],
            initialValue: modalData.name
          })(
            <Input placeholder="请输入流水线名称" disabled />
          )}
        </Form.Item>
        <Form.Item label="业务线" {...fiveLineLayout} >
          {getFieldDecorator('bizLine', {
            rules: [{ required: true, message: '请选择业务线' }],
            initialValue: modalData.bizLine
          })(
            <Select
              style={{ width: '100%' }}
              placeholder="请选择业务线"
              disabled
            >
              {
                bizLines && bizLines.length > 0 && bizLines.map(item => (
                  <Option key={item.code} value={item.code}>{item.name}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item
          label={
            <span>
              <span>多环境支持</span>
              <Tooltip title="开启后，可以使用环境组中的变量，运行时可以选择不同环境（预发、测试等）">
                <Icon type="question-circle" style={{ fontSize: '15px', marginLeft: 4, cursor: 'pointer' }} />
              </Tooltip>
            </span>
          }
          {...fiveLineLayout}
        >
          {getFieldDecorator('envSupport', {
            initialValue: modalData.envSupport,
            valuePropName: 'checked'
          })(
            <Switch
              checkedChildren="开"
              unCheckedChildren="关"
              disabled
            />
          )}
        </Form.Item>
        {
          modalData.envSupport && <Form.Item label="环境组选择" {...fiveLineLayout} >
            {getFieldDecorator('envGroupId', {
              rules: [{ required: true, message: '请选择环境组' }],
              initialValue: modalData.envGroupId
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择环境组"
                allowClear
                showSearch
                optionFilterProp="children"
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }
                disabled
              >
                {
                  envList && envList.map(item => (
                    <Option key={item.id} value={item.id}>{item.name}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        }
        {
          !modalData.envSupport && <Form.Item label="运行环境" {...fiveLineLayout} >
            {getFieldDecorator('online', {
              rules: [{ required: true, message: '请选择运行环境' }],
              initialValue: modalData.online
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择运行环境"
                disabled
              >
                <Option key="true" value={true}>线上</Option>
                <Option key="false" value={false}>线下</Option>
              </Select>
            )}
          </Form.Item>
        }
        <Form.Item label="运行权限" {...fiveLineLayout} >
          {getFieldDecorator('visible', {
            initialValue: String(modalData.permission.visible)
          })(
            <Radio.Group disabled>
              <Radio value="0">所有人</Radio>
              <Radio value="1">创建人</Radio>
              {/*<Radio value="2">选择人</Radio>*/}
            </Radio.Group>
          )}
        </Form.Item>
        {/*
          String(modalData.permission.visible) === '2' && <Form.Item label="运行权限人" {...fiveLineLayout} >
            {getFieldDecorator('permission.runners', {
              initialValue: modalData.permission.runners,
            })(
              <Select
                showSearch
                allowClear
                mode="multiple"
                style={{ width: '100%' }}
                filterOption={false}
                disabled
              >
                {allPerson && allPerson.map(item => (
                  <Option key={item.userName} value={item.eMail.split('@')[0]}>
                    {item.userName + '(' + item.eMail.split('@')[0] + ')'}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
        */}
        <Form.Item label="编辑权限" {...fiveLineLayout} >
          {getFieldDecorator('editable', {
            initialValue: String(modalData.permission.editable)
          })(
            <Radio.Group disabled>
              <Radio value="0">所有人</Radio>
              <Radio value="1">创建人</Radio>
              {/*<Radio value="2">选择人</Radio>*/}
            </Radio.Group>
          )}
        </Form.Item>
        {/*
          String(modalData.permission.editable) === '2' && <Form.Item label="编辑权限人" {...fiveLineLayout} >
            {getFieldDecorator('permission.editors', {
              rules: [{ required: true, message: '请选择编辑权限人！' }],
              initialValue: modalData.permission.editors,
            })(
              <Select
                showSearch
                allowClear
                mode="multiple"
                style={{ width: '100%' }}
                filterOption={false}
                disabled
              >
                {allPerson && allPerson.map(item => (
                  <Option key={item.userName} value={item.eMail.split('@')[0]}>
                    {item.userName + '(' + item.eMail.split('@')[0] + ')'}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
        */}
        <Form.Item label="标签" {...fiveLineLayout} >
          {getFieldDecorator('tags', {
            initialValue: modalData.tags
          })(
            <Select
              mode="tags"
              style={{ width: '100%' }}
              placeholder="请选择标签"
              disabled
            >
              {
                tagList && tagList.length > 0 && tagList.map(item => (
                  <Option key={item} value={item}>{item}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="备注" {...fiveLineLayout} >
          {getFieldDecorator('remark', {
            initialValue: modalData.remark
          })(
            <TextArea
              autoSize={{ minRows: 3 }}
              placeholder="请填写本流水线用户、用法、解释等"
              disabled
            />
          )}
        </Form.Item>
      </Modal>
    );
  }
}

export default connect((state) => ({
  bizLine: state.initialValueObj.bizLine,
  tag: state.initialValueObj.tag,
  envList: state.initialValueObj.envList,
}), intialValueAction)(
  Form.create()(seeInfoModal)
);