import React, { Component } from 'react';
import { Form, Input, Select } from 'antd';
import Mapping from '../../public/mapping';
const { oneLineLayout, operationType } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

export default class Redis extends Component {

  render() {
    const { getFieldDecorator, isEdit, node, insTypeList, disabled } = this.props;
    return (
      <React.Fragment>
        <Form.Item label="数据库" {...oneLineLayout} >
          {getFieldDecorator('instanceId', {
            rules: [{ required: true, message: '请选择数据库！' }],
            initialValue: isEdit && node ? node.instanceId : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : '请选择Redis'}
              disabled={disabled}
            >
              {
                insTypeList && insTypeList.map(item => (
                  <Option key={item.id} value={item.id}>
                    <span style={{ width: '100%', display: 'block' }}>{item.name}<span style={{ color: '#bfbfbf' }}>({item.ip}:{item.port})</span></span>
                  </Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="操作类型" {...oneLineLayout} >
          {getFieldDecorator('operationType', {
            rules: [{ required: true, message: '请选择操作类型！' }],
            initialValue: isEdit && node ? node.operationType : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : '请选择命令类型'}
              disabled={disabled}
            >
              {
                operationType && operationType.map(item => (
                  <Option key={item} value={item}>{item}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="命令" {...oneLineLayout} >
          {getFieldDecorator('command', {
            rules: [{ required: true, message: '请填写命令！' }],
            initialValue: isEdit && node ? node.command : '',
          })(
            <TextArea
              autoSize={{ minRows: 5 }}
              placeholder={disabled ? null : '请输入完整Redis命令'}
              disabled={disabled}
            />
          )}
        </Form.Item>
      </React.Fragment>
    );
  }
}