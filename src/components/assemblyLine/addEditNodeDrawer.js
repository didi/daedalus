/* eslint-disable */
/* 编辑流水线 */
import React, { Component, Fragment } from 'react';
import { Drawer, Button, Form, Input, Select, Row, Col, Menu, Dropdown, Icon, Popconfirm, InputNumber } from 'antd';
import Groovy from './useType/groovy';
import Es from './useType/es';
import Redis from './useType/redis';
import Mysql from './useType/mysql';
import Dubbo from './useType/dubbo';
import HttpType from './useType/http';
import Import from './useType/import';
import Notice from './useType/notice';
// import CodeMirrors from '../public/codeMirror';
import { connect } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import Mapping from '../public/mapping';
import ExtractKeyValueFrom from '../public/keyValue/extractKeyValueForm';
// import Selects from '../public/selsect';
import InputMentions from '../public/inputMentions/index';
import MonacoEditors from '../public/monacoEditors';
import './index.scss';
const { oneLineLayout, twoLineLayout, fourLineLayout, moreMenuList } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

class AddEditNodeDrawer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      node: {},
      selectValue: '', // 判断当前条件是否是正则匹配
      moreMenuChoise: [], // 编辑中根据当前数据判断跟多菜单选择了啥
    };
  }

  componentWillReceiveProps(next) {
    if (this.props.node !== next.node) {
      const { node } = next;
      const moreMenuChoise = [];
      if (node.condition && node.condition.rules && node.condition.rules.length > 0) moreMenuChoise.push('执行条件');
      if (node.delay) moreMenuChoise.push('执行延迟');
      if (node.alias) moreMenuChoise.push('别名');
      if (node.preStepScript) moreMenuChoise.push('前置脚本');
      if (node.postStepScript) moreMenuChoise.push('后置脚本');
      if (node.extractVars && node.extractVars.length > 0 && node.extractVars[node.extractVars.length - 1].location && node.extractVars[node.extractVars.length - 1].name) moreMenuChoise.push('变量提取');

      this.setState({
        selectValue: node.condition && node.condition.rules && node.condition.rules.length > 0 ? node.condition.rules[0].operator : '',
        moreMenuChoise,
        node
      });
    }
  }
  // 编辑弹框种更改选择条件
  selectChange = value => {
    this.setState({ selectValue: value });
  }
  // 更多菜单点击
  handleMenuClick = e => {
    const { moreMenuChoise, node } = this.state;
    let list = [...moreMenuChoise];
    let alreadyHaveMenu = list.some(item => e.key === item);
    if (alreadyHaveMenu) {
      list.forEach((x, i) => {
        if (x === e.key) {
          list.splice(i, 1);
          if (x === '变量提取') {
            this.setState({ node: { ...node, extractVars: [{ id: new Date().getTime() }] }});
            this.props.form.setFieldsValue({ extractVars: [] });
          }
        }
      });
    } else {
      list.push(e.key);
    }
    this.setState({ moreMenuChoise: list });
  }

  deopdowm = () => {
    const { childVisible, useAddItemName, data, title, insTypeList, sideAllData, allList, type } = this.props;
    const { selectValue, moreMenuChoise, node } = this.state;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = this.props.form;
    let defauteId = [{ id: new Date().getTime() }];
    // 指向当前节点的所有线
    let targetList = data.edges.filter(item => item.target === node.id);
    // 当前节点指向的所有线
    let sourceList = data.edges.filter(item => item.source === node.id);
    // 根据指向节点列表查询node数据
    const nodeList = [];
    sourceList.length > 0 && sourceList.forEach(item => {
      data.nodes.forEach(ele => {
        if (ele.id === item.target) {
          nodeList.push(ele);
        }
      });
    });
    const isAddHaveNode = useAddItemName !== '连接步骤' && useAddItemName !== '删除线';
    // 禁用删除
    let disabled = (targetList.length === 0 && sourceList.length >= 2) || (sourceList.length > 1 && targetList.length > 1);
    // 是否为编辑
    let isEdit = title && title.indexOf('编辑') > 0;
    const sideAllSelectData = [];
    sideAllData && Object.keys(sideAllData).forEach(it => {
      sideAllSelectData.push(...sideAllData[it]);
    });
    let useTypeParams = {
      setFieldsValue,
      getFieldDecorator,
      getFieldValue,
      isEdit,
      node,
      insTypeList,
      sideAllSelectData,
      disabled: false,
    };
    // 指向当前节点、当前节点和当前节点指向的所有线
    const edgeAllList = [];
    if (node.id) edgeAllList.push(node.id);
    data.edges.map(item => {
      if (item.target === node.id) edgeAllList.push(item.source);
      if (item.source === node.id) edgeAllList.push(item.target);
      return item;
    });
    return childVisible && <Drawer
      width={800}
      closable={false}
      visible={childVisible}
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>{title}</span>
          <span>
            {
              isEdit && <Popconfirm
                title="是否删除？"
                placement="bottomRight"
                onConfirm={() => { this.props.deleteNode(node, this.props.form) }}
                okText="是"
                cancelText="否"
              >
                <a disabled={disabled}>
                  <Icon
                    type="delete"
                    style={{ cursor: 'pointer', color: disabled ? 'rgba(0, 0, 0, .25)' : 'black' }}
                  />
                </a>
              </Popconfirm>
            }
            {
              isAddHaveNode &&
              <Dropdown
                overlay={
                  <Menu onClick={e => this.handleMenuClick(e)}>
                    {
                      moreMenuList.map(item => (
                        <Menu.Item key={item.key}>{moreMenuChoise.some(menu => item.key === menu) ? '取消' : '添加'} {item.value}</Menu.Item>
                      ))
                    }
                  </Menu>
                }
                placement="bottomLeft"
              >
                <a style={{ marginLeft: 20 }}>更多</a>
              </Dropdown>
            }
          </span>
        </div>
      }
    >
      {
        isAddHaveNode ? <Fragment>
          <Form.Item label="步骤名称" {...oneLineLayout} >
            {getFieldDecorator('label', {
              rules: [{ required: true, message: '请填写步骤名称' }],
              initialValue: (isEdit && node) ? node.name : '',
            })(
              <Input placeholder="请填写步骤名称" />
            )}
          </Form.Item>
          {
            moreMenuChoise.some(menu => menu === '执行条件') &&
            <Fragment>
              <Row>
                <Col span={16}>
                  <Form.Item label="执行条件" {...twoLineLayout} >
                    {getFieldDecorator('variable', {
                      initialValue: isEdit && node.condition && node.condition.rules ? (node.condition.rules.length > 0 ? node.condition.rules[0].variable : '') : '',
                    })(
                      // <Selects optionList={sideAllSelectData} placeholder="请输入参数名" />
                      <InputMentions optionList={sideAllSelectData} placeholder="请输入参数名" />
                    )}
                  </Form.Item>
                </Col>
                <Col span={7}>
                  <Form.Item label="" {...fourLineLayout} >
                    {getFieldDecorator('operator', {
                      initialValue: isEdit && node && node.condition && node.condition.rules ? (node.condition.rules.length > 0 ? node.condition.rules[0].operator : undefined) : undefined,
                    })(
                      <Select
                        style={{ width: '100%' }}
                        placeholder="请选择判断条件"
                        onChange={value => this.selectChange(value)}
                      >
                        {
                          this.props.operatorList && this.props.operatorList.map(item => (
                            <Option key={item.name} value={item.name}>
                              {item.desc}
                            </Option>
                          )
                          )}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item label={selectValue === 'REGEX' ? '正则表达式' : '值'} {...oneLineLayout} >
                {getFieldDecorator(selectValue === 'REGEX' ? 'regex' : 'value', {
                  initialValue: isEdit && node.condition && node.condition.rules && node.condition.rules.length > 0 ? (selectValue === 'REGEX' ? node.condition.rules[0].regex || '' : node.condition.rules[0].value || '') : '',
                })(
                  selectValue === 'REGEX' ? <Input placeholder="请输入值" /> :
                  // <Selects optionList={sideAllSelectData} placeholder="请输入值" />
                  <InputMentions optionList={sideAllSelectData} placeholder="请输入值" />
                )}
              </Form.Item>
            </Fragment>
          }
          {
            moreMenuChoise.some(menu => menu === '执行延迟') &&
            <Form.Item label="执行延迟" {...oneLineLayout} >
              {getFieldDecorator('delay', {
                initialValue: isEdit ? node.delay : 0,
              })(
                <InputNumber
                  min={0}
                  placeholder="请输入..."
                  formatter={value => `${value}ms`}
                  parser={value => value.replace('ms', '')}
                  style={{ width: '100%' }}
                />
              )}
            </Form.Item>
          }
          {
            moreMenuChoise.some(menu => menu === '别名') &&
            <Form.Item label="别名" {...oneLineLayout} >
              {getFieldDecorator('alias', {
                initialValue: isEdit ? node.alias : '',
              })(
                <Input placeholder="请输入..." />
              )}
            </Form.Item>
          }
          {
            moreMenuChoise.some(menu => menu === '前置脚本') &&
            <Form.Item label="前置脚本" {...oneLineLayout} >
              {getFieldDecorator('preStepScript', {
                initialValue: isEdit && node.preStepScript ? node.preStepScript : 'var vars = context.vars;',
              })(
                <MonacoEditors
                  language="javascript"
                />

              )}
            </Form.Item>
          }
          {useAddItemName === 'HTTP' && <HttpType {...useTypeParams} />}
          {useAddItemName === 'DUBBO' && <Dubbo {...useTypeParams} />}
          {useAddItemName === 'MYSQL' && <Mysql {...useTypeParams} />}
          {useAddItemName === 'REDIS' && <Redis {...useTypeParams} />}
          {useAddItemName === 'ES' && <Es {...useTypeParams} />}
          {useAddItemName === 'GROOVY' && <Groovy {...useTypeParams} />}
          {useAddItemName === 'IMPORT' && <Import {...useTypeParams} type={type} />}
          {useAddItemName === 'NOTICE' && <Notice {...useTypeParams} />}
          {
            moreMenuChoise.some(menu => menu === '变量提取') &&
            <Form.Item label="" {...fourLineLayout} style={{ marginLeft: '16.5%' }}>
              {getFieldDecorator('extractVars', {
                rules: [{
                  required: false,
                  validator(rule, value, callback) {
                    if (
                      (value.length > 0 && value.every(item => Object.keys(item).length >= 4) && value.every(item => item.name && item.location)) ||
                      (value.length === 1 && (Object.keys(value[0]).length === 1 || (value[0].name === '' && value[0].location === '')))
                    ) {
                      callback();
                    } else {
                      callback('请输入完整的变量提取');
                    }
                  }
                }],
                initialValue: isEdit && node.extractVars ? node.extractVars : defauteId
              })(
                <ExtractKeyValueFrom
                  allList={allList}
                  useAddItemName={useAddItemName}
                />
              )}
            </Form.Item>
          }
          {
            moreMenuChoise.some(menu => menu === '后置脚本') &&
            <Form.Item label="后置脚本" {...oneLineLayout} >
              {getFieldDecorator('postStepScript', {
                initialValue: isEdit && node.postStepScript ? node.postStepScript : 'var vars = context.vars;',
              })(
                <MonacoEditors
                  language="javascript"
                />
              )}
            </Form.Item>
          }
          <Form.Item label="步骤说明" {...oneLineLayout} >
            {getFieldDecorator('remark', {
              initialValue: isEdit ? node.remark : '',
            })(
              <TextArea
                autoSize={{ minRows: 3 }}
                placeholder="请填写步骤说明"
                style={{ marginBottom: '50px' }}
              />
            )}
          </Form.Item>
        </Fragment> :
        (
          useAddItemName !== '删除线' ? <Form.Item label={'已有节点'} {...oneLineLayout} >
            {getFieldDecorator('haveNode', {
              rules: [{ required: true, message: '请选择' }],
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择..."
              >
                {data.nodes.map(item => (
                  <Option key={item.id} value={item.id} disabled={edgeAllList.filter(edge => edge === item.id).length > 0}>
                    {item.name}
                    <span style={{ color: '#BFBFBF' }}>({item.stepType})</span>
                  </Option>
                )
                )}
              </Select>
            )}
          </Form.Item> :
          <Form.Item label={'已连节点'} {...oneLineLayout} >
            {getFieldDecorator('deleEdge', {
              rules: [{ required: true, message: '请选择' }],
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择..."
              >
                {
                  nodeList.map(item => (
                    <Option key={item.id} value={item.id} disabled={item.id === data.nodes[0].id}>
                      {item.name}<span style={{ color: '#BFBFBF' }}>({item.stepType})</span>
                    </Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        )
      }
      <div className="drawewButton">
        {
          !isEdit ? <Button onClick={() => this.props.onChildrenDrawerClose(this.props.form)}>取消</Button> :
          <Button onClick={() => { this.props.onClose(this.props.form) }} style={{ marginRight: 20 }}>取消</Button>
        }
        {
          !isEdit ? <Button onClick={() => this.props.addNode(this.props.form)} type="primary" style={{ marginLeft: 20 }}>确认</Button> :
          <Button onClick={() => { this.props.editOk(this.props.form, node.id) }} type="primary">确认</Button>
        }
      </div>
    </Drawer>;
  }

  render() {
    const { addVisible, event, data, node, title, childVisible } = this.props;
    // 指向当前节点的所有线
    let targetList = data.edges.filter(item => item.target === node.id);
    // 一层抽屉展示数据
    let stepList = [
      {
        title: '接口',
        list: [
          {
            name: 'HTTP',
            desc: 'HTTP接口调用能力'
          },
          {
            name: 'DUBBO',
            desc: 'DUBBO接口调用能力'
          },
        ]
      },
      {
        title: '数据库',
        list: [
          {
            name: 'MYSQL',
            desc: 'MYSQL能力'
          },
          {
            name: 'REDIS',
            desc: 'REDIS能力'
          },
          // {
          //   name: 'ES',
          //   desc: 'ES能力'
          // },
        ]
      },
      {
        title: '脚本',
        list: [
          {
            name: 'GROOVY',
            desc: '运行Groovy脚本'
          },
        ]
      },
      {
        title: '其他',
        list: [
          {
            name: 'EMPTY',
            desc: '用于填充、整理流水线'
          },
          {
            name: 'IMPORT',
            desc: '用于导入其他流水线'
          },
          {
            name: 'NOTICE',
            desc: '通知'
          },
        ]
      }
    ];
    // 判断当前是否已有节点（如无则不显示连接到已有节点）
    if (event.target && data.nodes.length >= 2) {
      stepList.push({
        title: '步骤连线',
        list: [
          {
            name: '连接步骤',
            desc: '连接到已有步骤'
          }
        ]
      });
      // if (targetList.length >= 1 && node.id !== data.nodes[0].id) {
      if (targetList.length >= 1) {
        stepList[stepList.length - 1].list.push({
          name: '删除线',
          desc: '删除连接到当前步骤的线'
        });
      }
    }
    return (
      (title && title.indexOf('编辑') > 0) ? this.deopdowm() : <Drawer
        title="添加步骤"
        placement="right"
        closable={false}
        visible={addVisible}
        width={childVisible ? 800 : 700}
        className="addDrawer"
        onClose={() => this.props.onClose(this.props.form)}
      >
        {
          stepList.map((item, index) => (
            <div style={{ marginBottom: index === stepList.length - 1 ? '60px' : '20px' }} key={index}>
              <h3 style={{ borderBottom: '1px solid', paddingBottom: '10px' }}>{item.title}</h3>
              {
                item.list.map((ele, i) => (
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={i}>
                    <div>
                      <div style={{ fontWeight: '600', color: 'black' }}>{ele.name}</div>
                      <div style={{ fontSize: 12 }}>{ele.desc}</div>
                    </div>
                    <Button onClick={() => { this.props.showChildDrawer(item, i) }} type="primary">使用</Button>
                  </div>
                ))
              }
            </div>
          ))
        }
        {this.deopdowm()}
        {/* 下方确认按钮 */}
        <div className="drawewButton">
          <Button onClick={() => { this.props.onClose(this.props.form) }}>
            取消
          </Button>
        </div>
      </Drawer>
    );
  }
}

export default connect((state) => ({
  operatorList: state.initialValueObj.operatorList,
}), {
  ...intialValueAction
})(Form.create()(AddEditNodeDrawer));