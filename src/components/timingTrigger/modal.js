import React, { Component, Fragment } from 'react';
import { Form, Modal, Select, Button, InputNumber, Input, Checkbox, Radio, Spin, message, Divider } from 'antd';
import Mapping from '../public/mapping';
import request from '@/util/request';
import FormatPicker from '../public/formatPicker';
import Cron from './cron';
const { fiveLineLayout } = Mapping;
const { Option } = Select;
const { TextArea } = Input;

class TriggerModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      lineDetail: [], // 当前选择流水线详情
      envList: [],
      spinLoading: false,
    };
  }
  componentWillReceiveProps(next) {
    if (this.props.record !== next.record && next.record.pipelineId) {
      this.getLineDetail(next.record.pipelineId);
    }
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
          this.setState({ loading: false, spinLoading: false, envList: [], });
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
  // 取消编辑新建
  handleCancel = form => {
    this.props.closeModal();
    form.resetFields();
    this.setState({
      lineDetail: [],
      envList: [],
      spinLoading: false
    });
  }
  // 确认编辑新建
  handleOk = form => {
    form.validateFields((error, value) => {
      if (error) return;
      let params = { ...value };
      if (this.props.record) {
        params.id = this.props.record.id;
      }
      this.setState({ loading: true });
      request(`/schedule/save`, {
        method: 'POST',
        body: { ...params }
      }).then(res => {
        if (res.success === true) {
          message.success(this.props.modalTitle === '创建定时任务' ? '创建成功' : '更新成功');
          this.handleCancel(form);
          this.props.getTableList();
        } else {
          message.error(res.msg);
        }
        this.setState({ loading: false });
      });
    });
  }

  render() {
    const { visible, modalTitle, record, lineList } = this.props;
    const { loading, lineDetail, envList, spinLoading } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <Fragment>
        <Modal
          title={modalTitle}
          visible={visible}
          closable={false}
          width="800px"
          footer={
            <Fragment>
              <Button onClick={() => this.handleCancel(this.props.form)}>取消</Button>
              <Button
                onClick={() => this.handleOk(this.props.form)}
                type="primary"
                loading={loading}
                disabled={spinLoading}
              >
                确定
              </Button>
            </Fragment>
          }
        >
          <Form.Item label="选择流水线" {...fiveLineLayout} >
            {getFieldDecorator('pipelineId', {
              rules: [{ required: true, message: '请选择流水线' }],
              initialValue: modalTitle !== '创建定时任务' ? record.pipelineId : undefined
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
              >
                {
                  lineList && lineList.length > 0 && lineList.map(item => (
                    <Option key={item.id} value={item.id}>{item.name}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
          <Form.Item label="定时时间" {...fiveLineLayout} >
            {getFieldDecorator('cronRule', {
              rules: [{ required: true, message: '请输入cron' }],
              initialValue: modalTitle !== '创建定时任务' ? record.cronRule : '0 0 0 * * ?'
            })(
              <Cron />
            )}
          </Form.Item>
          {(lineDetail.length > 0 || envList.length > 0) && <Divider>流水线详情</Divider>}
          <Spin tip="Loading..." spinning={spinLoading}>
            {
              envList.length > 0 && <Form.Item label="环境选择" {...fiveLineLayout} >
                {getFieldDecorator('env', {
                  rules: [{ required: true, message: '请选择环境' }],
                  initialValue: modalTitle !== '创建定时任务' ? record.env : undefined
                })(
                  <Select
                    placeholder="请选择环境选择"
                    style={{ width: '100%' }}
                    showSearch
                    allowClear
                    optionFilterProp="children"
                    filterOption={(input, option) =>
                      option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
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
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : it.defaultValue
                    })(
                      <Input placeholder="请输入..." />
                    )}
                  </Form.Item>
                  }
                  {it.inputType === 'NUMBER_INPUT' && show &&
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : it.defaultValue
                    })(
                      <InputNumber placeholder="请输入..." style={{ width: '100%' }} />
                    )}
                  </Form.Item>
                  }
                  {it.inputType === 'TEXTAREA' && show &&
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : it.defaultValue
                    })(
                      <TextArea
                        placeholder="请输入..."
                        style={{ width: '100%' }}
                        autoSize={{ minRows: 3 }}
                      />
                    )}
                  </Form.Item>
                  }
                  {it.inputType === 'SELECT' && show &&
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请选择！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : it.defaultValue || undefined
                    })(
                      <Select
                        style={{ width: '100%' }}
                        placeholder="请选择..."
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
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请选择！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : (it.options.length > 0 && it.options[0].value) ? it.options[0].value : undefined
                    })(
                      <Radio.Group>
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
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请选择！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : undefined
                    })(
                      <Checkbox.Group>
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
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : undefined
                    })(
                      <FormatPicker type="DATE_PICKER" />
                    )}
                  </Form.Item>
                  }
                  {it.inputType === 'TIME_PICKER' && show &&
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : undefined
                    })(
                      <FormatPicker type="TIME_PICKER" />
                    )}
                  </Form.Item>
                  }
                  {it.inputType === 'DATE_TIME_PICKER' && show &&
                  <Form.Item label={it.label} {...fiveLineLayout}>
                    {getFieldDecorator(`inputs.${it.name}`, {
                      rules: [{ required: it.required, message: '请输入！' }],
                      initialValue: record && record.inputs && record.inputs[it.name] ? record.inputs[it.name] : undefined
                    })(
                      <FormatPicker type="DATE_TIME_PICKER" />
                    )}
                  </Form.Item>
                  }
                </Fragment>;
              })
            }
          </Spin>
        </Modal>
      </Fragment>
    );
  }
}

export default Form.create()(TriggerModal);