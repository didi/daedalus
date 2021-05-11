import React, { Component } from 'react';
import { Form, Input, Select } from 'antd';
import Mapping from '../../public/mapping';
const { oneLineLayout } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

export default class Es extends Component {

  render() {
    const { getFieldDecorator, isEdit, node, insTypeList, disabled } = this.props;
    return (
      <React.Fragment>
        <Form.Item label="ES" {...oneLineLayout} >
          {getFieldDecorator('instanceId', {
            rules: [{ required: true, message: '请选择ES！' }],
            initialValue: isEdit && node ? node.instanceId : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : '请选择ES'}
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
        <Form.Item label="查询方式" {...oneLineLayout} >
          {getFieldDecorator('queryType', {
            rules: [{ required: true, message: '请选择查询方式！' }],
            initialValue: isEdit && node ? node.queryType : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : 'SQL/Query DSL'}
              disabled={disabled}
            >
              <Option key="SQL" value="SQL">SQL</Option>
              <Option key="DSL" value="DSL">Query DSL</Option>
            </Select>
          )}
        </Form.Item>
        <Form.Item label="SQL/DSL" {...oneLineLayout} >
          {getFieldDecorator('sql', {
            rules: [{ required: true, message: '请填写SQL/DSL！' }],
            initialValue: isEdit && node ? node.sql : '',
          })(
            <TextArea
              autoSize={{ minRows: 3 }}
              placeholder={disabled ? null : '请输入SQL/DSL'}
              disabled={disabled}
            />
          )}
        </Form.Item>
      </React.Fragment>
    );
  }
}