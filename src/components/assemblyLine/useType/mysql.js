import React, { Component } from 'react';
import { Form, Select, Tooltip } from 'antd';
// import CodeMirrors from '../../public/codeMirror';
import Mapping from '../../public/mapping';
import MonacoEditors from '../../public/monacoEditors';
const { oneLineLayout } = Mapping;
const { Option } = Select;

export default class Mysql extends Component {

  render() {
    const { getFieldDecorator, isEdit, node, insTypeList, disabled } = this.props;
    // const data = { table1: [''], table2: [''], table3: [''] };
    return (
      <React.Fragment>
        <Form.Item label="数据库" {...oneLineLayout} >
          {getFieldDecorator('instanceId', {
            rules: [{ required: true, message: '请选择数据库！' }],
            initialValue: isEdit && node ? node.instanceId : undefined,
          })(
            <Select
              style={{ width: '100%' }}
              placeholder={disabled ? null : '请选择MYSQL数据库'}
              disabled={disabled}
            >
              {
                insTypeList && insTypeList.map(item => (
                  <Option key={item.id} value={item.id}>
                    <Tooltip placement="top" title={item.database}>
                      <span style={{ width: '100%', display: 'block' }}>{item.name}<span style={{ color: '#bfbfbf' }}>({item.ip}:{item.port})</span></span>
                    </Tooltip>
                  </Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="SQL" {...oneLineLayout} >
          {getFieldDecorator('sql', {
            rules: [{ required: true, message: '请填写SQL！' }],
            initialValue: isEdit && node ? node.sql : '',
          })(
            <MonacoEditors
              language="sql"
              readOnly={disabled ? true : false}
            />
          )}
        </Form.Item>
      </React.Fragment>
    );
  }
}