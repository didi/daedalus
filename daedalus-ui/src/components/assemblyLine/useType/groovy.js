import React, { Component } from 'react';
import { Form } from 'antd';
// import CodeMirrors from '../../public/codeMirror';
import Mapping from '../../public/mapping';
import MonacoEditors from '../../public/monacoEditors';
const { oneLineLayout } = Mapping;

export default class Groovy extends Component {

  render() {
    const { getFieldDecorator, isEdit, node, disabled } = this.props;
    return (
      <Form.Item label="Groovy脚本" {...oneLineLayout} >
        {getFieldDecorator('script', {
          rules: [{ required: true, message: '请填写脚本！' }],
          initialValue: isEdit && node ? node.script : '//所有的变量，通过vars.变量名 获取变量\ndef vars = context.vars',
        })(
          <MonacoEditors
            language="java"
            readOnly={disabled ? true : false}
          />
        )}
      </Form.Item>
    );
  }
}