import React, { Component } from 'react';
import { Form, Input, Select } from 'antd';
import Mapping from '../../public/mapping';
const { oneLineLayout } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

export default class Notice extends Component {

  render() {
    const { getFieldDecorator, isEdit, node, disabled } = this.props;
    return (
      <React.Fragment>
        <Form.Item label="发送类型" {...oneLineLayout} >
          {getFieldDecorator('noticeType', {
            rules: [{ required: true, message: '请选择发送类型！' }],
            initialValue: isEdit && node ? node.noticeType : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : '请选择发送类型'}
              disabled={disabled}
            >
              <Option key={'EMAIL'} value={'EMAIL'}>E-mail</Option>
              {/*<Option key={'DCHAT'} value={'DCHAT'}>D-Chat</Option>*/}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="接收人" {...oneLineLayout} >
          {getFieldDecorator('receivers', {
            rules: [{ required: true, message: '请填写接收人！' }],
            initialValue: isEdit && node ? node.receivers : undefined,
          })(
            <Input
              placeholder={disabled ? null : '请填写接收人！'}
              disabled={disabled}
            />
          )}
        </Form.Item>
        <Form.Item label="发送内容" {...oneLineLayout} >
          {getFieldDecorator('noticeContent', {
            rules: [{ required: true, message: '请填写发送内容！' }],
            initialValue: isEdit && node ? node.noticeContent : '',
          })(
            <TextArea
              autoSize={{ minRows: 6 }}
              placeholder={disabled ? null : '请填写发送内容'}
              disabled={disabled}
            />
          )}
        </Form.Item>
      </React.Fragment>
    );
  }
}