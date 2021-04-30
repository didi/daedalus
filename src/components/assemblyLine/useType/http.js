/* eslint-disable */
import React, { Component, Fragment } from 'react';
import { Form, Input, Select, Radio, message, Collapse, Col } from 'antd';
import InputMentions from '../../public/inputMentions/index';
import ExportHttpModal from './exportHttpModal';
import CurlParser from '../../public/curlParser/CurlParser';
import Mapping from '../../public/mapping';
import KeyValueForm from '../../public/keyValue/keyValueForm';
import JsonEditor from '../../public/jsonEdit';
import ViewJsonEditor from '../../public/jsonEdit/viewJsonEdit';
import request from '@/util/request';
import '../index.scss';
const { Panel } = Collapse;
const { oneLineLayout, fourLineLayout, bodyTypeList } = Mapping;
const { Option } = Select;
const { TextArea } = Input;
let defaultList = [{ id: new Date().getTime() }];

export default class HttpType extends Component {

  constructor(props) {
    super(props);
    this.state = {
      showExportModal: false,
      curlInfo: {},
      cookiesValue: false, // http cookie当前是什么 false：键值对 true： 文本
    };
  }
  componentWillReceiveProps(next) {
    // 判断当前是否在运行页面
    let isFunction =  window.location.href.indexOf('/function') > -1;
    // 判断当前是否为流水线编辑
    let isLineEdit = window.location.href.indexOf('/assemblyLine') > -1 && this.props.isEdit;
    if (this.props.node !== next.node || isFunction) {
      let haveCookie = next.node.cookieText || next.node.cookieText === '' ? true : false;
      this.setState({ cookiesValue: haveCookie });
    }
    if(this.props.node !== next.node && isLineEdit && next.node.instanceId){
      request(`/instance/detail`, {
        method: 'GET',
        params: { instanceId: next.node.instanceId }
      }).then(res => {
        if (res.success === true) {
          let record = res.data;
          this.setState({ cookiesValue: record.cookieText || record.cookieText === '' ? true : false });
        }
      })
    }
  }
  // http cookie 文本键值对切换
  cookieChange = type => {
    this.setState({ cookiesValue: type ? true : !this.state.cookiesValue });
  }
  // 确认导入
  exportHttpOk = exportForm => {
    const { setFieldsValue } = this.props;
    exportForm.validateFields((error, value) => {
      if (error) return;
      let curlInfo = {};
      let curlInfoArray = new CurlParser().parse(value.exportContent);
      if (curlInfoArray) {
        curlInfo.url = curlInfoArray.url;
        curlInfo.method = curlInfoArray.method;
        curlInfo.bodyString = curlInfoArray.bodyString;
        curlInfo.query = curlInfoArray.query ? Object.keys(curlInfoArray.query).map((urlParms, index) => {
          return { id: index, name: urlParms, value: curlInfoArray.query[urlParms] };
        }) : defaultList;
        let headers = [];
        let cookieText = '';
        let bodyType = undefined;
        if (curlInfoArray.headers && curlInfoArray.headers._list) {
          for (let i = 0; i < curlInfoArray.headers._list.size; i++) {
            if (curlInfoArray.headers._list.get(i)[0] !== 'Cookie') {
              headers.push({ id: i, name: curlInfoArray.headers._list.get(i)[0], value: curlInfoArray.headers._list.get(i)[1] });
            } else {
              cookieText = curlInfoArray.headers._list.get(i)[1];
            }
            if (curlInfoArray.headers._list.get(i)[0] === 'Content-Type') {
              bodyType = bodyTypeList.find(it => it.value === curlInfoArray.headers._list.get(i)[1].split(';')[0]).key;
            }
          }
        }
        curlInfo.headers = headers.length > 0 ? headers : defaultList;
        curlInfo.cookieText = cookieText;
        curlInfo.bodyType = bodyType;
        let formData = [];
        if (curlInfoArray.body) {
          for (let i = 0; i < curlInfoArray.body.size; i++) {
            formData.push({ id: i, name: curlInfoArray.body.get(i).key, value: curlInfoArray.body.get(i).value });
          }
        }
        curlInfo.formData = formData;
        this.cookieChange('export');
        this.setState({ curlInfo }, () => {
          setTimeout(() => {
            setFieldsValue({
              urlType: 'INPUT',
            }, () => {
              setFieldsValue({
                method: curlInfo.method,
                url: curlInfo.url.split('?')[0],
                headers: curlInfo.headers,
                urlParams: curlInfo.query,
                cookieText: curlInfo.cookieText,
              }, () => {
                setFieldsValue({
                  bodyType: curlInfo.bodyType,
                }, () => {
                  if (curlInfo.bodyType === 'JSON' || curlInfo.bodyType === 'TEXT') {
                    setFieldsValue({
                      body: curlInfo.bodyString,
                    });
                    this.httpEditor.updateValue(curlInfo.bodyString);
                  } else {
                    setFieldsValue({
                      formData: curlInfo.formData.length > 0 ? curlInfo.formData : defaultList,
                    });
                  }
                });
              });
            });
          });
        }, 500);
        this.exportHttpCancel();
      } else {
        message.error('请输入合理CURL');
      }
    });
  }
  // 取消导入
  exportHttpCancel = () => {
    this.setState({ showExportModal: false, });
  }
  // varStrToArr = str => {
  //   if (!str) {
  //     return [];
  //   }
  //   return str.split(/(#{[^#{}]+})/).filter(str => str !== '');
  // }
  // url源切换
  urlTypeChange = e => {
    if (e.target.value === 'SELECT' && this.props.node.instanceId) {
      this.instanceIdChange(this.props.node.instanceId);
    }
  }
  // 模板选择
  instanceIdChange = value => {
    const { setFieldsValue } = this.props;
    request(`/instance/detail`, {
      method: 'GET',
      params: {
        instanceId: value
      }
    }).then(res => {
      if (res.success === true) {
        let record = res.data;
        this.setState({ cookiesValue: record.cookieText || record.cookieText === '' ? true : false });
        record.headers = record.headers.length > 0 ? record.headers : defaultList;
        record.urlParams = record.urlParams.length > 0 ? record.urlParams : defaultList;
        setTimeout(() => {
          setFieldsValue({
            method: record.method,
            url: record.url,
            headers: record.headers,
            urlParams: record.urlParams,
          }, () => {
            if (record.cookieText) {
              setFieldsValue({ cookieText: record.cookieText });
            } else {
              setFieldsValue({ cookies: record.cookies.length > 0 ? record.cookies : defaultList });
            }
            if (record.bodyType) {
              setFieldsValue({
                bodyType: record.bodyType
              }, () => {
                if (record.bodyType === 'JSON' || record.bodyType === 'TEXT') {
                  setFieldsValue({ body: record.body });
                  this.httpEditor.updateValue(record.body);
                } else {
                  setFieldsValue({ formData: record.formData.length > 0 ? record.formData : defaultList });
                }
              });
            }
          });
        }, 500);
      }
    });
  }

  render() {
    let { getFieldDecorator, getFieldValue, isEdit, node, sideAllSelectData, insTypeList, disabled } = this.props;
    let href = window.location.href.indexOf('/exampleManage/http') > -1;
    let href1 = window.location.href.indexOf('/function') > -1;
    let isDisabled = href ? false : disabled || getFieldValue('urlType') === 'SELECT';
    const { showExportModal, cookiesValue } = this.state;
    const customPanelStyle = {
      background: 'white',
      borderRadius: 4,
      border: 0,
    };
    return (
      <Fragment>
        {!href && !href1 &&
        <div style={{ textAlign: 'right' }}>
          <a onClick={() => this.setState({ showExportModal: true })}>导入</a>
        </div>}
        <Collapse
          defaultActiveKey={['基础请求信息']}
          className="httpCollapse"
          bordered={false}
        >
          <Panel header="基础请求信息" key="基础请求信息" style={customPanelStyle}>
            {
              !href && <Form.Item label="URL源选择项" {...oneLineLayout} >
                {getFieldDecorator('urlType', {
                  initialValue: isEdit && node ? node.urlType : 'INPUT'
                })(
                  <Radio.Group disabled={disabled} onChange={e => this.urlTypeChange(e)}>
                    <Radio value="INPUT">输入</Radio>
                    <Radio value="SELECT">选择</Radio>
                  </Radio.Group>
                )}
              </Form.Item>
            }
            {
              getFieldValue('urlType') === 'SELECT' && !href &&
              <Form.Item label="模板" {...oneLineLayout} >
                {getFieldDecorator('instanceId', {
                  rules: [{ required: true, message: '请选择模板！' }],
                  initialValue: isEdit && node ? node.instanceId : undefined
                })(
                  <Select
                    style={{ width: '100%' }}
                    placeholder="请选择模板"
                    allowClear
                    showSearch
                    optionFilterProp="children"
                    filterOption={(input, option) =>
                      option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                    disabled={disabled}
                    onChange={value => this.instanceIdChange(value)}
                  >
                    {
                      insTypeList && insTypeList.map(item => (
                        <Option key={item.id} value={item.id}>{item.name}</Option>
                      ))
                    }
                  </Select>
                )}
              </Form.Item>
            }
            <Form.Item label="Method" {...oneLineLayout} style={{ marginTop: href ? 24 : 0 }}>
              {getFieldDecorator('method', {
                rules: [{ required: true, message: '请选择Method！' }],
                initialValue: isEdit && node ? node.method : undefined
              })(
                <Select
                  style={{ width: '100%' }}
                  placeholder="请选择Http Method"
                  disabled={isDisabled}
                >
                  <Option key="GET" value="GET">GET</Option>
                  <Option key="POST" value="POST">POST</Option>
                  <Option key="PUT" value="PUT">PUT</Option>
                  <Option key="DELETE" value="DELETE">DELETE</Option>
                </Select>
              )}
            </Form.Item>
            <Form.Item label="URL" {...oneLineLayout} >
              {getFieldDecorator('url', {
                rules: [{ required: true, message: '请填写URL！' }],
                initialValue: isEdit && node ? node.url : undefined
              })(
                <InputMentions disabled={isDisabled} optionList={sideAllSelectData} placeholder="请填写URL！"/>
                // <Input disabled={isDisabled} />
              )}
            </Form.Item>
            <Form.Item label="超时时间" {...oneLineLayout} >
              {getFieldDecorator('timeout', {
                initialValue: isEdit && node ? node.timeout : '10000'
              })(
                <Input
                  placeholder="默认10s"
                  style={{ width: '90%', marginRight: 10 }}
                  disabled={isDisabled}
                />
              )}
                ms
            </Form.Item>
          </Panel>
        </Collapse>
        <Collapse
          defaultActiveKey={['URL Params']}
          className="httpCollapse"
          bordered={false}
        >
          <Panel header="URL Params" key="URL Params" style={customPanelStyle}>
            <Form.Item label="" {...fourLineLayout} >
              {getFieldDecorator('urlParams', {
                rules: [{
                  required: false,
                  validator(rule, value, callback) {
                    if (
                      (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.name && item.value)) ||
                      (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].value === '')))
                    ) {
                      callback();
                    } else {
                      callback('请输入完整urlParams');
                    }
                  }
                }],
                initialValue: isEdit && node ? node.urlParams : defaultList
              })(
                <KeyValueForm
                  subordinate="headers"
                  leftName="URL Parameter"
                  rightName="Value"
                  disabled={isDisabled}
                />
              )}
            </Form.Item>
          </Panel>
        </Collapse>
        {getFieldValue('method') !== 'GET' && <Collapse
          defaultActiveKey={['请求Body']}
          className="httpCollapse"
          bordered={false}
        >
          <Panel header="请求Body" key="请求Body" style={customPanelStyle}>
            <Form.Item label="Body类型" {...oneLineLayout} >
              {getFieldDecorator('bodyType', {
                initialValue: isEdit && node && node.bodyType ? node.bodyType : 'JSON'
              })(
                <Radio.Group disabled={isDisabled}>
                  {
                    bodyTypeList.map(item => (
                      <Radio key={item.key} value={item.key} style={{ width: '40%' }}>{item.value}</Radio>
                    ))
                  }
                </Radio.Group>
              )}
            </Form.Item>
            <div style={{ marginBottom: 24 }}>
              <Col span={4} style={{ textAlign: 'right' }}>
                <span style={{ color: 'rgba(0, 0, 0, 0.85)', marginRight: 8 }}>Content-Type: </span>
              </Col>
              {(getFieldValue('bodyType') || node.bodyType) && bodyTypeList.find(item => item.key === (getFieldValue('bodyType') || node.bodyType)).value}
            </div>
            {
              getFieldValue('bodyType') === 'JSON' &&
              <Form.Item label="" {...fourLineLayout} >
                {getFieldDecorator('body', {
                  initialValue: isEdit && node && node.body ? node.body : ''
                })(
                  isDisabled ?
                    <ViewJsonEditor onBind={ref => this.httpEditor = ref} /> :
                    <JsonEditor onBind={ref => this.httpEditor = ref} />
                )}
              </Form.Item>
            }
            {
              getFieldValue('bodyType') === 'TEXT' &&
              <Form.Item label="" {...fourLineLayout} >
                {getFieldDecorator('body', {
                  initialValue: isEdit && node && node.body ? node.body : ''
                })(
                  <TextArea
                    style={{ wordBreak: 'break-all' }}
                    autoSize={{ minRows: 3, maxRows: 6 }}
                    disabled={isDisabled}
                  />
                )}
              </Form.Item>
            }
            {
              (getFieldValue('bodyType') === 'FORM_URLENCODED' || getFieldValue('bodyType') === 'FORM_DATA') &&
              <Form.Item label="" {...fourLineLayout} >
                {getFieldDecorator('formData', {
                  rules: [{
                    required: false,
                    validator(rule, value, callback) {
                      if (
                        (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.name && item.value)) ||
                        (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].value === '')))
                      ) {
                        callback();
                      } else {
                        callback('请输入完整bodyType');
                      }
                    }
                  }],
                  initialValue: isEdit && node && node.formData ? node.formData : defaultList
                })(
                  <KeyValueForm
                    subordinate="formData"
                    leftName="Name"
                    rightName="Value"
                    disabled={isDisabled}
                  />
                )}
              </Form.Item>
            }
          </Panel>
        </Collapse>}
        <Collapse
          defaultActiveKey={['Header']}
          className="httpCollapse"
          bordered={false}
        >
          <Panel header="Header" key="Header" style={customPanelStyle}>
            <Form.Item label="" {...fourLineLayout} >
              {getFieldDecorator('headers', {
                rules: [{
                  required: false,
                  validator(rule, value, callback) {
                    if (
                      (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.name && item.value)) ||
                      (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].value === '')))
                    ) {
                      callback();
                    } else {
                      callback('请输入完整Header');
                    }
                  }
                }],
                initialValue: isEdit && node && node.headers ? node.headers : defaultList
              })(
                <KeyValueForm
                  subordinate="headers"
                  leftName="Name"
                  rightName="Value"
                  disabled={isDisabled}
                />
              )}
            </Form.Item>
          </Panel>
        </Collapse>
        <Collapse
          defaultActiveKey={['cookie']}
          className="httpCollapse"
          bordered={false}
        >
          <Panel
            key="cookie"
            style={customPanelStyle}
            header={
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Cookies</span>
                {!isDisabled && <a
                  onClick={event => {
                    event.stopPropagation();
                    this.cookieChange();
                  }}
                >
                  {cookiesValue === true || (href && node.cookieText !== undefined) ? 'Name-Value编辑' : '文本编辑'}
                </a>}
              </div>
            }
          >
            {
              cookiesValue === true || (href && node.cookieText !== undefined) ?
                <Form.Item label="" {...fourLineLayout} >
                  {getFieldDecorator('cookieText', {
                    initialValue: isEdit && node ? node.cookieText : ''
                  })(
                    <TextArea
                      style={{ wordBreak: 'break-all' }}
                      autoSize={{ minRows: 3, maxRows: 6 }}
                      placeholder="如：a=1&b=2"
                      disabled={isDisabled}
                    />
                  )}
                </Form.Item> :
                <Form.Item label="" {...fourLineLayout} >
                  {getFieldDecorator('cookies', {
                    rules: [{
                      required: false,
                      validator(rule, value, callback) {
                        if (
                          (value.length > 0 && value.every(item => Object.keys(item).length >= 3) && value.every(item => item.name && item.value)) ||
                          (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].value === '')))
                        ) {
                          callback();
                        } else {
                          callback('请输入完整cookies');
                        }
                      }
                    }],
                    initialValue: isEdit && node && node.cookies ? node.cookies : defaultList
                  })(
                    <KeyValueForm
                      subordinate="cookies"
                      leftName="Name"
                      rightName="Value"
                      disabled={isDisabled}
                    />
                  )}
                </Form.Item>
            }
          </Panel>
        </Collapse>
        {showExportModal &&
        <ExportHttpModal
          exportHttpOk={this.exportHttpOk}
          exportHttpCancel={this.exportHttpCancel}
        />
        }
      </Fragment>
    );
  }
}