/* eslint-disable */
import React, { Component, Fragment } from 'react';
import { Form, Select, InputNumber, Input, Checkbox, Radio, Spin, Divider } from 'antd';
import request from '@/util/request';
import FormatPicker from '../../public/formatPicker';
import Mapping from '../../public/mapping';
const { oneLineLayout } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

export default class IMPORT extends Component {
  state = {
    lineList: [],
    lineDetail: [],
    envList: [],
    spinLoading: false,
  }
  componentDidMount() {
    this.getQueryList();
  }
  componentWillReceiveProps(next) {
    if (this.props.node !== next.node && next.node.pipelineId || window.location.href.indexOf('#/function') > -1) {
      this.getLineDetail(next.node.pipelineId);
    }
  }
  // 获取最新创建
  getQueryList = () => {
    request(`/pipeline/list`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 9999,
      }
    }).then(res => {
      if (res.success === true) {
        if (this.props.type !== 'add' && window.location.hash.indexOf('/assemblyLine') > -1) {
          res.data = res.data.filter(item => item.id !== this.props.type.split('-')[1]);
        }
        this.setState({ lineList: res.data });
      }
    });
  }
  // 查询选择流水线详情
  getLineDetail = id => {
    this.setState({ getNodeLoading: true, spinLoading: true });
    request(`/pipeline/detail`, {
      method: 'GET',
      params: {
        pipelineId: id
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ lineDetail: res.data.variable.inputVars ? res.data.variable.inputVars : [] });
        if (res.data.envGroupId) {
          this.getEnvDetail(res.data.envGroupId);
        } else {
          this.setState({ envList: [], spinLoading: false });
        }
      }
    });
  }
  // 获取环境详情
  getEnvDetail = id => {
    request(`/env/detail`, {
      method: 'GET',
      params: {
        envGroupId: id
      }
    }).then(res => {
      if (res.success === true) {
        let envData = res.data.data[0];
        delete envData.envVarDesc;
        delete envData.envVarName;
        delete envData.key;
        delete envData.bizLine;
        envData = Object.keys(envData);
        this.setState({
          envList: envData,
          spinLoading: false
        });
      }
    });
  }

  render() {
    const { getFieldDecorator, isEdit, node, getFieldValue } = this.props;
    const { lineList, lineDetail, envList, spinLoading } = this.state;
    let isDisable = window.location.href.indexOf('#/function') > -1;
    return (
      <Fragment>
        <Form.Item label="选择流水线" {...oneLineLayout}>
          {getFieldDecorator('pipelineId', {
            rules: [{ required: true, message: '请选择流水线' }],
            initialValue: isEdit && node ? node.pipelineId : undefined
          })(
            <Select
              placeholder="请选择流水线"
              style={{ width: '100%' }}
              showSearch
              allowClear
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              onChange={value => this.getLineDetail(value)}
              disabled={isDisable}
            >
              {
                lineList.length > 0 && lineList.map(item => (
                  <Option key={item.id} value={item.id}>{item.name}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        {(lineDetail.length > 0 || envList.length > 0) && <Divider>流水线详情</Divider>}
        <Spin tip="Loading..." spinning={spinLoading}>
          {
            envList.length > 0 && <Form.Item label="环境选择" {...oneLineLayout} >
              {getFieldDecorator('env', {
                rules: [{ required: true, message: '请选择环境' }],
                initialValue: isEdit && node ? node.env : undefined
              })(
                <Select
                  placeholder={isDisable ? '' : '请选择环境选择'}
                  style={{ width: '100%' }}
                  showSearch
                  allowClear
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                  disabled={isDisable}
                >
                  {
                    envList.map(item => {
                      return <Option key={item} value={item}>{item}</Option>;
                    })
                  }
                </Select>
              )}
            </Form.Item>
          }
          {
            lineDetail.length > 0 && lineDetail.map((it, i) => {
              let show = true; // 是否有判断条件显示
              let options = it.optionRelations && it.optionRelations.length > 0 ? [] : it.options; // 关联选项
              if (it.dependencyInputName && it.dependencyOptions.length > 0) {
                show = it.dependencyOptions.indexOf(getFieldValue(`inputs.${it.dependencyInputName}`)) > -1;
              }
              if (it.optionRelations && it.optionRelations.length > 0) {
                it.optionRelations.map(item => {
                  if (item.showOnOptions.indexOf(getFieldValue(`inputs.${it.dependencyInputName}`)) > -1) {
                    options.push(...it.options.filter(option => item.targetOptions.indexOf(option.value) > -1));
                  }
                  return item;
                });
              }
              if (options && options.length === 0) options = it.options;
              return <Fragment key={i}>
                {it.inputType === 'INPUT' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : it.defaultValue
                  })(
                    <Input
                      placeholder={isDisable ? '' : '请输入...'}
                      disabled={isDisable}
                    />
                  )}
                </Form.Item>
                }
                {it.inputType === 'NUMBER_INPUT' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : it.defaultValue
                  })(
                    <InputNumber
                      placeholder={isDisable ? '' : '请输入...'}
                      style={{ width: '100%' }}
                      disabled={isDisable}
                    />
                  )}
                </Form.Item>
                }
                {it.inputType === 'TEXTAREA' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : it.defaultValue
                  })(
                    <TextArea
                      placeholder={isDisable ? '' : '请输入...'}
                      style={{ width: '100%' }}
                      autoSize={{ minRows: 3 }}
                      disabled={isDisable}
                    />
                  )}
                </Form.Item>
                }
                {it.inputType === 'SELECT' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请选择！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : it.defaultValue || undefined
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={isDisable ? '' : '请输入...'}
                      disabled={isDisable}
                      allowClear
                      showSearch
                    >
                      {
                        options && options.length > 0 && options.map((item, i) => (
                          <Option key={i} value={item.value}>{item.display}</Option>
                        ))
                      }
                    </Select>
                  )}
                </Form.Item>
                }
                {it.inputType === 'RADIO' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请选择！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : (it.options.length > 0 && it.options[0].value) ? it.options[0].value : undefined
                  })(
                    <Radio.Group disabled={isDisable}>
                      {
                        options && options.length > 0 && options[0].value && options.map((item, i) => (
                          <Radio key={i} value={item.value}>{item.display}</Radio>
                        ))
                      }
                    </Radio.Group>
                  )}
                </Form.Item>
                }
                {it.inputType === 'CHECKBOX' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请选择！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : undefined
                  })(
                    <Checkbox.Group disabled={isDisable}>
                      {
                        options && options.length > 0 && options.map((item, i) => (
                          <Checkbox key={i} value={item.value}>{item.display}</Checkbox>
                        ))
                      }
                    </Checkbox.Group>
                  )}
                </Form.Item>
                }
                {it.inputType === 'DATE_PICKER' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : undefined
                  })(
                    <FormatPicker type="DATE_PICKER" disabled={isDisable} />
                  )}
                </Form.Item>
                }
                {it.inputType === 'TIME_PICKER' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : undefined
                  })(
                    <FormatPicker type="TIME_PICKER" disabled={isDisable} />
                  )}
                </Form.Item>
                }
                {it.inputType === 'DATE_TIME_PICKER' && show &&
                <Form.Item label={it.label} {...oneLineLayout}>
                  {getFieldDecorator(`inputs.${it.name}`, {
                    rules: [{ required: it.required, message: '请输入！' }],
                    initialValue: node && node.inputs && node.inputs[it.name] ? node.inputs[it.name] : undefined
                  })(
                    <FormatPicker type="DATE_TIME_PICKER" disabled={isDisable} />
                  )}
                </Form.Item>
                }
              </Fragment>;
            })
          }
        </Spin>
        {(lineDetail.length > 0 || envList.length > 0) && <Divider></Divider>}
      </Fragment>
    );
  }
}