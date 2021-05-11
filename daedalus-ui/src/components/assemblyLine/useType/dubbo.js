import React, { Component, Fragment } from 'react';
import { Form, Input, Select, Radio, InputNumber } from 'antd';
// import Selects from '../../public/selsect';
import InputMentions from '../../public/inputMentions/index';
import Mapping from '../../public/mapping';
import KeyValueForm from '../../public/keyValue/keyValueForm';
const { oneLineLayout, fourLineLayout } = Mapping;
const { Option } = Select;

export default class Dubbo extends Component {

  render() {
    const { getFieldDecorator, getFieldValue, isEdit, node, sideAllSelectData, insTypeList, disabled } = this.props;
    return (
      <Fragment>
        <Form.Item label="接口调用类型" {...oneLineLayout} >
          {getFieldDecorator('dubboType', {
            initialValue: (isEdit && node) ? (node !== undefined ? node.dubboType : 'REGISTER') : 'REGISTER'
          })(
            <Radio.Group disabled={disabled}>
              <Radio value="REGISTER">注册中心</Radio>
              <Radio value="DIRECT">直连</Radio>
            </Radio.Group>
          )}
        </Form.Item>
        {
          getFieldValue('dubboType') === 'REGISTER' ? <Fragment>
            <Form.Item label="注册中心" {...oneLineLayout} >
              {getFieldDecorator('register', {
                rules: [{ required: true, message: '请选择注册中心！' }],
                initialValue: (isEdit && node) ? (node !== undefined ? node.register : undefined) : undefined
              })(
                <Select
                  style={{ width: '100%' }}
                  placeholder={disabled ? null : '请选择注册中心'}
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
            <Form.Item label="service.json" {...oneLineLayout} >
              {getFieldDecorator('serviceJson', {
                initialValue: (isEdit && node) ? node.serviceJson : ''
              })(
                <Input placeholder={disabled ? null : '请输入service.json'} disabled={disabled} />
              )}
            </Form.Item>
          </Fragment> :
          <Fragment>
            <Form.Item label="IP" {...oneLineLayout} >
              {getFieldDecorator('ip', {
                rules: [{ required: true, message: '请填写ip！' }],
                initialValue: (isEdit && node) ? node.ip : ''
              })(
                // <Input placeholder={disabled ? null : '请填写ip'} disabled={disabled}/>
                <InputMentions
                  optionList={sideAllSelectData}
                  placeholder={disabled ? null : '请填写ip'}
                  disabled={disabled}
                />
              )}
            </Form.Item>
            <Form.Item label="端口" {...oneLineLayout} >
              {getFieldDecorator('port', {
                rules: [{ required: true, message: '请填写端口！' }],
                initialValue: (isEdit && node) ? node.port : ''
              })(
                <InputNumber
                  placeholder={disabled ? null : '请输入端口号'}
                  style={{ width: '100%' }}
                  disabled={disabled}
                />
              )}
            </Form.Item>
          </Fragment>
        }

        <Form.Item label="接口定义" {...oneLineLayout} >
          {getFieldDecorator('dubboInterface', {
            rules: [{ required: true, message: '请填写接口定义！' }],
            initialValue: (isEdit && node) ? node.className : ''
          })(
            <InputMentions
              disabled={disabled}
              placeholder={disabled ? null : '请填写接口定义'}
              optionList={sideAllSelectData}
            />
          )}
        </Form.Item>
        <Form.Item label="group" {...oneLineLayout} >
          {getFieldDecorator('group', {
            initialValue: (isEdit && node) ? node.group : '',
          })(
            // <Input placeholder={disabled ? null : '缺省为空'} disabled={disabled}/>
            <InputMentions
              optionList={sideAllSelectData}
              placeholder={disabled ? null : '缺省为空'}
              disabled={disabled}
            />
          )}
        </Form.Item>
        <Form.Item label="version" {...oneLineLayout} >
          {getFieldDecorator('version', {
            initialValue: (isEdit && node) ? node.version : '',
          })(
            <Input
              placeholder={disabled ? null : '请填写version！'}
              disabled={disabled}
            />
          )}
        </Form.Item>
        <Form.Item label="方法" {...oneLineLayout} >
          {getFieldDecorator('method', {
            rules: [{ required: true, message: '请填写方法！' }],
            initialValue: (isEdit && node) ? node.method : '',
          })(
            // <Input placeholder={disabled ? null : '请填写方法'} disabled={disabled}/>
            <InputMentions
              optionList={sideAllSelectData}
              placeholder={disabled ? null : '请填写方法'}
              disabled={disabled}
            />
          )}
        </Form.Item>
        <div style={{ marginLeft: '16.5%' }}>
          <Form.Item label="" {...fourLineLayout} >
            {getFieldDecorator('params', {
              rules: [{
                required: false,
                validator(rule, value, callback) {
                  if (
                    (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.type && item.value)) ||
                    (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].type === '' && value[0].value === '')))
                  ) {
                    callback();
                  } else {
                    callback('请输入完整params');
                  }
                }
              }],
              initialValue: (isEdit && node) ? (node !== undefined ? node.params : [{ id: new Date().getTime() }]) : [{ id: new Date().getTime() }]
            })(
              <KeyValueForm
                subordinate="params"
                leftName="参数类型"
                rightName="参数值"
                disabled={disabled}
              />
            )}
          </Form.Item>
        </div>
        <div style={{ marginLeft: '16.5%' }}>
          <Form.Item label="" {...fourLineLayout} >
            {getFieldDecorator('attachments', {
              rules: [{
                required: false,
                validator(rule, value, callback) {
                  if (
                    (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.name && item.value)) ||
                    (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].value === '')))
                  ) {
                    callback();
                  } else {
                    callback('请输入完整attachments');
                  }
                }
              }],
              initialValue: (isEdit && node) ? (node !== undefined && node.attachments ? node.attachments : [{ id: new Date().getTime() }]) : [{ id: new Date().getTime() }]
            })(
              <KeyValueForm
                subordinate="attachments"
                leftName="Attachments（Key）"
                rightName="Attachments（Value）"
                disabled={disabled}
              />
            )}
          </Form.Item>
        </div>
      </Fragment>
    );
  }
}