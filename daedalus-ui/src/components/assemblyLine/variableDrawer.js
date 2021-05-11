/* 流水线变量抽屉 */
import React, { Component, Fragment } from 'react';
import { Drawer, Form, Input, Select, Icon, Tooltip, Popconfirm, Button } from 'antd';
import Mapping from '../public/mapping';
import KeyValueOption from '../public/keyValue/keyValueOption';
import KeyValueSelect from '../public/keyValue/keyValueSelect';
const { fourLineLayout, inputTypeList, fiveLineLayout } = Mapping; // valueTypeList, valueAllTypeList,
const { TextArea } = Input;
const { Option } = Select;

class VariableDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {

    };
  }

  render() {
    const { visible, record, sideData, nodeData, typeList, title, form, inputType, disabled } = this.props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;
    let runInputOptions = [];
    let runInput = sideData[0].groupList.filter(item => item.inputType === 'SELECT' || item.inputType === 'RADIO'); // 运行输入为select活radio的集合
    if (disabled) {
      if (record.dependencyInputName) {
        runInputOptions = runInput.find(it => it.name === record.dependencyInputName).options;
      }
    } else {
      if (getFieldValue('dependencyInputName') || (title.indexOf('编辑') > -1 && record.dependencyInputName)) {
        runInputOptions = runInput.find(it => it.name === (getFieldValue('dependencyInputName') || record.dependencyInputName)).options;
      }
    }
    let isDisabled = disabled || title.indexOf('编辑') > -1;
    // let valueType = [];
    // if (manageDrawerTitle.indexOf('输入') > -1) {
    //   valueType = valueAllTypeList;
    // } else {
    //   valueType = valueTypeList;
    // }
    return (
      <Drawer
        placement="right"
        closable={false}
        visible={visible}
        width={800}
        onClose={disabled ? this.props.onClose : null}
        title={
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{title}</span>
            <span style={{ marginRight: 25 }}>
              {
                title.indexOf('编辑') !== -1 && <Popconfirm
                  title="是否删除？"
                  placement="bottomRight"
                  onConfirm={() => this.props.onDelGroupItem(record)}
                  okText="是"
                  cancelText="否"
                >
                  <Icon type="delete" />
                </Popconfirm>
              }
            </span>
          </div>
        }
      >
        {
          title.indexOf('输入') > -1 && <Form.Item label={
            <span>
          表单文本&nbsp;
              <Tooltip title="运行时表单文本">
                <Icon type="question-circle" />
              </Tooltip>
            </span>
          } {...fiveLineLayout} >
            {getFieldDecorator('label', {
              rules: [{ required: true, message: '请输入!' }],
              initialValue: isDisabled ? record.label : ''
            })(
              <Input placeholder="请输入表单文本" disabled={disabled} />
            )}
          </Form.Item>
        }
        <Form.Item label={(
          <span>
          变量名&nbsp;
            <Tooltip title={
              <Fragment>
                <div>1、变量名只能以字母、数字和下划线组成</div>
                <div>2、Step中可以用{`#{变量名}`}的方式引用变量</div>
              </Fragment>
            }>
              <Icon type="question-circle" />
            </Tooltip>
          </span>
        )} {...fiveLineLayout} >
          {getFieldDecorator('name', {
            rules: [{
              required: true,
              // message: '请填写...',
              validator(rule, value, callback) {
                const groupList = [...sideData[0].groupList, ...sideData[1].groupList];
                let tempLs = groupList && groupList.filter(item => item.name === value);
                let extractList = [];
                nodeData && nodeData.nodes && nodeData.nodes.length > 0 && nodeData.nodes.map(item => (
                  item.extractVars.filter(it => (it.name && it.location) !== undefined).map(ele => (
                    extractList.push(ele)
                  ))
                ));
                let extractIsHave = extractList.filter(item => item.name === value);
                let regexp = new RegExp(/^[a-zA-Z\\_][0-9a-zA-Z\\_]*$/);
                if (value) {
                  if (regexp.test(value)) {
                    if (title.indexOf('编辑') > -1 && value === record.name ? tempLs.length > 1 : tempLs.length > 0 || extractIsHave.length > 0) {
                      callback('变量名已重复，请重新填写！');
                    } else {
                      callback();// 必须写
                    }
                  } else {
                    callback('请输入正确格式！');
                  }
                } else {
                  callback('请输入...');
                }
              }
            }],
            initialValue: isDisabled ? record.name : ''
          })(
            <Input placeholder="请输入变量名" disabled={disabled} />
          )}
        </Form.Item>
        {
          title.indexOf('输入') > -1 && <Form.Item label="输入形式" {...fiveLineLayout}>
            {getFieldDecorator('inputType', {
              initialValue: isDisabled ? record.inputType : 'INPUT'
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择输入形式"
                onChange={this.props.inputTypeChange}
                disabled={disabled}
              >
                {
                  inputTypeList.map(item => (
                    <Option key={item.key} value={item.key}>{item.value}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        }
        {
          (inputType === 'SELECT' || inputType === 'RADIO' || inputType === 'CHECKBOX') &&
          <Form.Item label="" {...fourLineLayout} style={{ marginLeft: '21%' }}>
            {getFieldDecorator('options', {
              rules: [{
                validator(rule, value, callback) {
                  let rules1 = value.length > 0 && value.every(item => item.display && item.value);
                  let rules2 = value.length === 1 && (value.every(item => item.display && item.value) || Object.keys(value[0]).length === 1);
                  if (rules1 || rules2) {
                    callback();
                  } else {
                    callback('请填写完整');
                  }
                }
              }],
              initialValue: isDisabled && record.options && record.options.length > 0 ? record.options : [{ id: new Date().getTime() }]
            })(
              <KeyValueOption
                subordinate="options"
                leftName="显示值"
                rightName="真实值"
                disabled={disabled}
              />
            )}
          </Form.Item>
        }
        {
          (inputType === 'DATE_PICKER' || inputType === 'TIME_PICKER' || inputType === 'DATE_TIME_PICKER') &&
          <Form.Item label="格式选择" {...fiveLineLayout}>
            {getFieldDecorator('dateFormat', {
              rules: [{ required: true, message: '请选择!' }],
              initialValue: isDisabled ? record.dateFormat : undefined
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择格式"
                disabled={disabled}
              >
                {
                  typeList && typeList.map(item => (
                    <Option key={item.desc} value={item.name}>{item.desc}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        }
        {
          title.indexOf('全局变量') > -1 && <Form.Item label="变量值" {...fiveLineLayout}>
            {getFieldDecorator('value', {
              rules: [{ required: true, message: '请输入!' }],
              initialValue: isDisabled ? record.value : ''
            })(
              <Input placeholder="变量值" disabled={disabled} />
            )}
          </Form.Item>
        }
        {/* <Form.Item label={title.indexOf('输入') > -1 ? '参数类型' : '变量类型'} {...fiveLineLayout} >
        {getFieldDecorator('valueType', {
          initialValue: isDisabled ? record.valueType : 'STRING'
        })(
          <Select
            style={{ width: '100%' }}
            placeholder="请选择输入类型"
            disabled={disabled}
          >
            {
              valueType.map(item => (
                <Option key={item.key} value={item.key}>{item.value}</Option>
              ))
            }
          </Select>
        )}
          </Form.Item> */}
        {title.indexOf('输入') > -1 && !(inputType === 'DATE_PICKER' || inputType === 'TIME_PICKER' || inputType === 'DATE_TIME_PICKER' || inputType === 'RADIO') &&
        <Form.Item label="默认值" {...fiveLineLayout}>
          {getFieldDecorator('defaultValue', {
            initialValue: isDisabled ? record.defaultValue : ''
          })(
            <Input placeholder="请输入默认值" disabled={disabled} />
          )}
        </Form.Item>
        }
        {title.indexOf('输入') > -1 &&
        <Form.Item label="是否必填" {...fiveLineLayout}>
          {getFieldDecorator('required', {
            initialValue: isDisabled && record.required !== undefined ? record.required : true
          })(
            <Select
              style={{ width: '100%' }}
              placeholder="请选择输入类型"
              disabled={disabled}
            >
              <Option key="true" value={true}>是</Option>
              <Option key="false" value={false}>否</Option>
            </Select>
          )}
        </Form.Item>
        }
        {title.indexOf('输入') > -1 &&
        <Form.Item label="依赖组件" {...fiveLineLayout}>
          {getFieldDecorator('dependencyInputName', {
            initialValue: isDisabled && record.dependencyInputName ? record.dependencyInputName : undefined
          })(
            <Select
              style={{ width: '100%' }}
              placeholder="请选择依赖组件"
              allowClear
              disabled={disabled}
              onChange={() => setFieldsValue({ dependencyOptions: undefined, optionRelations: [{ id: new Date().getTime() }] })}
            >
              {
                runInput.length > 0 && runInput.map((item, i) => (
                  <Option key={i} value={item.name} disabled={record !== undefined && item.name === record.name}>{item.label}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        }
        {title.indexOf('输入') > -1 && (getFieldValue('dependencyInputName') || (isDisabled && record.dependencyInputName)) &&
        <Form.Item label="依赖组件值" {...fiveLineLayout}>
          {getFieldDecorator('dependencyOptions', {
            // rules: [{ required: getFieldValue('dependencyInputName') ? true : false, message: '请选择!' }],
            initialValue: isDisabled && record.dependencyOptions ? record.dependencyOptions : undefined
          })(
            <Select
              style={{ width: '100%' }}
              mode="multiple"
              placeholder="请选择依赖组件值"
              disabled={disabled}
            >
              {
                runInputOptions.length > 0 && runInputOptions.map(item => (
                  <Option key={item.id} value={item.value}>{item.display}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        }
        {title.indexOf('输入') > -1 && (getFieldValue('dependencyInputName') || (isDisabled && record.dependencyInputName)) && (inputType === 'SELECT' || inputType === 'RADIO' || inputType === 'CHECKBOX') &&
        <Form.Item label="关联值" {...fiveLineLayout}>
          {getFieldDecorator('optionRelations', {
            rules: [{
              validator(rule, value, callback) {
                let rules1 = value.length > 0 && value.every(item => item.targetOptions && item.targetOptions.length > 0 && item.showOnOptions && item.showOnOptions.length > 0);
                let rules2 = value.length === 1 && (value.every(item => item.targetOptions && item.targetOptions.length === 0 && item.showOnOptions && item.showOnOptions.length === 0) || Object.keys(value[0]).length === 1);
                if (rules1 || rules2) {
                  callback();
                } else {
                  callback('请填写完整');
                }
              }
            }],
            initialValue: isDisabled && record.optionRelations && record.optionRelations.length > 0 ? record.optionRelations : [{ id: new Date().getTime() }]
          })(
            <KeyValueSelect
              record={record}
              options={getFieldValue('options')}
              runInputOptions={runInputOptions}
            />
          )}
        </Form.Item>}
        <Form.Item label={title.indexOf('输入') > -1 ? '参数说明' : '变量说明'} {...fiveLineLayout} >
          {getFieldDecorator('remark', {
            initialValue: isDisabled ? record.remark : ''
          })(
            <TextArea
              autoSize={{ minRows: 3 }}
              placeholder={title.indexOf('输入') > -1 ? '请输入参数说明' : '请输入备注'}
              style={{ marginBottom: 40 }}
              disabled={disabled}
            />
          )}
        </Form.Item>
        {!disabled && <div className="drawewButton">
          <Button onClick={this.props.onClose} style={{ marginRight: 20 }}>取消</Button>
          <Button onClick={this.props.onOk} type="primary">确认</Button>
        </div>}
      </Drawer>
    );
  }
}
export default VariableDrawer;