/* eslint-disable */
import React, { Component, Fragment } from 'react';
import G6 from '@antv/g6';
import { Link } from 'react-router-dom';
import { Drawer, Form, Input, Select, Row, Col,  Icon, Tooltip, InputNumber, Modal } from 'antd';
import request from '@/util/request';
// import Selects from '../public/selsect';
import InputMentions from '../public/inputMentions/index';
// import CodeMirrors from '../public/codeMirror';
import MonacoEditors from '../public/monacoEditors';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import Mapping from '../public/mapping';
import ViewJsonEditor from '../public/jsonEdit/viewJsonEdit';
import Import from '../assemblyLine/useType/import';
import VariableDrawer from '../assemblyLine/variableDrawer';
import Groovy from '../assemblyLine/useType/groovy';
import Redis from '../assemblyLine/useType/redis';
import Es from '../assemblyLine/useType/es';
import Mysql from '../assemblyLine/useType/mysql';
import Dubbo from '../assemblyLine/useType/dubbo';
import HttpType from '../assemblyLine/useType/http';
import Notice from '../assemblyLine/useType/notice';
const { oneLineLayout, twoLineLayout, threeLineLayout, fourLineLayout, inputTypeList, locationList } = Mapping; // valueTypeList, valueAllTypeList,
const { TextArea } = Input;
const { Option } = Select;

class SeeAssemblyLine extends Component {
  constructor(props) {
    super(props);
    this.state = {
      manageRecord: {}, // 管理中点击record
      sideData: [
        {
          group: '运行输入',
          groupId: '1',
          groupList: [],
        },
        {
          group: '全局变量',
          groupId: '2',
          groupList: [],
        }
      ],
      manageVisible: false,
      manageDrawerTitle: '', // 管理抽屉标题
      manageList: [], // 输入管理键值对
      nodeVisible: false,
      nodeTitle: '',
      node: {},
      moreMenuChoise: [],
      typeList: [], // 时间格式列表
      visible: false,
      jsonValue: undefined,
    };
  }
  componentDidMount() {
    this.getNodeData();
    this.getSideData();
  }
  // 获取节点数据
  getNodeData = () => {
    this.setState({
      data: {
        nodes: this.props.lineDetail.flow ? this.props.lineDetail.flow.steps : [],
        edges: this.props.lineDetail.flow ? this.props.lineDetail.flow.edges : [],
      }
    }, () => { if (this.state.data.nodes.length > 0) this.drawNode(this.state.data); });
  }
  // 获取实例数据
  getInsType = type => {
    if (type !== '连接步骤' || type !== '连接步骤' || type !== 'GROOVY' || type !== 'DDMQ') {
      if (type === 'DUBBO') type = 'REGISTRY';
      request(`/instance/list`, {
        method: 'GET',
        params: {
          insType: type,
          page: 0,
          pageSize: 9999
        }
      }).then(res => {
        if (res.success === true) {
          this.setState({ insTypeList: res.data });
        }
      });
    }
  }
  // 获取侧边数据
  getSideData = () => {
    const sideData = [
      {
        group: '运行输入',
        groupId: '1',
        groupList: this.props.lineDetail.variable.inputVars
      },
      {
        group: '全局变量',
        groupId: '2',
        groupList: this.props.lineDetail.variable.globalVars
      },
    ];
    this.setState({ sideData });
  }
  // G6绘图
  drawNode = data => {
    // 绘图
    G6.registerNode('dom-node', {
      draw: (cfg, group) => {
        const shape = group.addShape('dom', {
          attrs: {
            width: cfg.size[0],
            height: cfg.size[1],
            html: `
            <div id='label-shape' style="background-color: #f2f2f2; border: 1px solid #e6e7eb; border-radius: 5px; border-top: 4px solid green; padding: 0 12px; cursor: pointer; position: relative;">
              <div style="margin:auto; padding:auto; color: #595959; padding: 6px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${cfg.name}</div>
              <div style="margin:auto; padding:auto; color: #ff4f3e; padding: 6px 0; border-top: 1px solid #e6e7eb; height:35px; text-align: right">${cfg.stepType || ''}</div>
            </div>
              `
          },
          name: 'label-shape',
          draggable: true
        });

        // 等同于节点的矩形
        group.addShape('rect', {
          attrs: {
            x: 0,
            y: 0,
            width: cfg.size[0],
            height: cfg.size[1],
            // stroke: 'black',
            fill: 'rgba(255, 255, 255, 0)',
            radius: [2, 4],
            cursor: 'pointer',
          },
          name: 'rect-shape',
        });
        return shape;
      },
    }, 'single-node');
    // 两手指滑动效果
    G6.registerBehavior('double-finger-drag-canvas', {
      getEvents: function getEvents() {
        return {
          wheel: 'onWheel',
        };
      },

      onWheel: function onWheel(ev) {
        if (ev.ctrlKey) {
          const canvas = graph.get('canvas');
          const point = canvas.getPointByClient(ev.clientX, ev.clientY);
          let ratio = graph.getZoom();
          if (ev.wheelDelta > 0) {
            ratio = ratio + ratio * 0.05;
          } else {
            ratio = ratio - ratio * 0.05;
          }
          graph.zoomTo(ratio, {
            x: point.x,
            y: point.y,
          });
        } else {
          const x = ev.deltaX || ev.movementX;
          const y = ev.deltaY || ev.movementY;
          graph.translate(-x, -y);
        }
        ev.preventDefault();
      },
    });
    // 下方Minimap
    const minimap = new G6.Minimap({
      size: [120, 90],
      className: 'minimap',
      // type: 'delegate',
    });

    // 配置
    const graph = new G6.Graph({
      container: 'mountNode', // String | HTMLElement，必须，在 Step 1 中创建的容器 id 或容器本身
      plugins: [minimap],
      renderer: 'svg',
      linkCenter: true,
      width: document.body.clientWidth > 1900 ? 1565 : 1160, // Number，必须，图的宽度
      height: document.body.clientHeight > 800 ? 800 : 630, // Number，必须，图的高度
      fitViewPadding: [20, 40, 50, 20],
      modes: {
        default: ['drag-canvas', 'double-finger-drag-canvas'], // drag-canvas 允许拖拽画布、zoom-canvas 放缩画布、drag-node 拖拽节点
      },
      // 边默认的属性
      defaultEdge: {
        type: 'cubic-horizontal',
        style: {    // 链接线的样式
          stroke: '#A3B1BF',
          lineAppendWidth: 3
        },
        labelCfg: {
          // autoRotate: true, // 文本根据边的方向旋转
          style: {
            stroke: 'white',
            lineWidth: 5,
          },
        },
      },
      // 节点默认的属性
      defaultNode: {
        type: 'dom-node',
        size: [160, 74],
        labelCfg: {
          autoRotate: true, // 边上的标签文本根据边的方向旋转
          style: {
            fill: 'black',
            fontSize: 14,
          }
        },
        style: {
          fill: 'white',
          stroke: 'black',
          radius: 5,
        }
      },
      layout: {
        type: 'dagre', // 布局类型
        rankdir: 'LR',    // 自左至右布局
        nodeSep: 0,      // 节点之间间距
        rankSep: 20,      // 每个层级之间的间距
        controlPoints: true, // 可选
        preventOverlap: true, // 设置防止重叠
      }
    });

    graph.data(data); // 读取 Step 2 中的数据源到图上
    graph.render(); // 渲染图
    // 点击节点
    graph.on('node:click', event => {
      const { item } = event;
      const node = item._cfg.model;
      const moreMenuChoise = [];
      if (node.condition.rules && node.condition.rules.length > 0) moreMenuChoise.push('执行条件');
      if (node.delay) moreMenuChoise.push('执行延迟');
      if (node.alias) moreMenuChoise.push('别名');
      if (node.preStepScript) moreMenuChoise.push('前置脚本');
      if (node.postStepScript) moreMenuChoise.push('后置脚本');
      if (node.extractVars.length > 0 && node.extractVars[0].location) moreMenuChoise.push('变量提取');
      if (node.headers) node.headers = this.addId(node.headers);
      if (node.urlParams) node.urlParams = this.addId(node.urlParams);
      if (node.formData) node.formData = this.addId(node.formData);
      if (node.cookies) node.cookies = this.addId(node.cookies);
      if (node.params) node.params = this.addId(node.params);
      this.setState({
        nodeVisible: true,
        nodeTitle: node.name + ' —— ' + node.stepType,
        node: node,
        moreMenuChoise,
      });
      this.getInsType(node.stepType);
    });
  }
  // 展示管理编辑抽屉
  showManageDrawer = (title, record) => {
    let typeList = [];
    if (record) {
      if (record.inputType === 'DATE_PICKER') {
        typeList = [{ desc: 'yyyyMMdd', name: 'yyyyMMdd' }];
      } else if (record.inputType === 'TIME_PICKER') {
        typeList = [{ desc: 'HH:mm:SS', name: 'HHmmSS' }];
      } else {
        typeList = this.props.dateFormat;
      }
    }
    this.setState({
      manageVisible: true,
      manageDrawerTitle: title,
      manageRecord: record,
      manageList: record.options !== undefined ? record.options : [],
      typeList,
    });
  };
  // 关闭管理抽屉
  onManageClose = () => {
    this.setState({
      manageVisible: false,
      manageDrawerTitle: '',
      manageRecord: {},
      manageList: [], // 提取点列表0
    });
  };
  // 节点抽屉关闭
  onNodeClose = () => {
    this.setState({
      nodeVisible: false,
    });
  }
  // 转换 数组转为字符串
  convert = params => {
    let str = '';
    params.forEach(item => {
      str += item;
    });
    return str;
  }
  // 如果数组为空，则默认给一条数据
  addId = list => {
    if (list.length > 0) {
      list.forEach((it, i) => {
        if (!it.hasOwnProperty('id')) {
          it.id = i;
        }
      });
    } else {
      list = [{ id: new Date().getTime() }];
    }
    return list;
  }

  render() {
    const {
      sideData, manageVisible, manageDrawerTitle, manageRecord, nodeVisible, nodeTitle, node, moreMenuChoise, insTypeList, typeList,
    } = this.state;
    const { getFieldDecorator, setFieldsValue, getFieldValue } = this.props.form;
    const nodeData = this.props.lineDetail.flow;
    const choiceEnvData = this.props.choiceEnvData;
    // let valueType = [];
    // if (manageDrawerTitle.indexOf('输入') > -1) {
    //   valueType = valueAllTypeList;
    // } else {
    //   valueType = valueTypeList;
    // }
    const haveSide = (this.props.lineDetail && this.props.lineDetail.variable.inputVars.length > 0) || (this.props.lineDetail && this.props.lineDetail.variable.globalVars.length > 0) || (choiceEnvData && choiceEnvData.data) || (nodeData && Object.keys(nodeData).length > 0 && nodeData.steps.length > 0 && !nodeData.steps.every(it => it.extractVars.filter(ele => (ele.name && ele.location) !== undefined).length === 0));
    let useTypeParams = {
      setFieldsValue,
      getFieldDecorator,
      getFieldValue,
      isEdit: true,
      node,
      insTypeList,
      sideAllSelectData: this.props.sideAllSelectData,
      disabled: true,
    };
    return (
      <Fragment>
        <div className="see">
          <div className="content">
            {haveSide &&
              <div className="allGroups">
                {
                  sideData.map(item => (
                    item.groupList.length > 0 && <div className="group" key={item.groupId}>
                      <div className="groupTitle">
                        <h3>{item.group}</h3>
                      </div>
                      {
                        item.groupList.map(ele => (
                          <div
                            className="groupContent"
                            key={ele.id}
                            onClick={() => { this.showManageDrawer(item.group.indexOf('输入') > -1 ? `${ele.label}输入` : `${ele.name}全局变量`, ele) }}
                          >
                            <div className="name">{ele.name}</div>
                            <div className="remarks">
                              <span>{ele.value || ele.label}</span>
                              {item.group.indexOf('输入') > -1 && <span>{inputTypeList.find(x => x.key === ele.inputType).value}</span>}
                            </div>
                          </div>
                        ))
                      }
                    </div>
                  ))
                }
                {
                  Object.keys(nodeData).length > 0 && nodeData.steps.length > 0 && !nodeData.steps.every(it => it.extractVars.filter(ele => (ele.name && ele.location) !== undefined).length === 0) && <div className="group">
                    <div className="groupTitle">
                      <h3>提取变量</h3>
                    </div>
                    {
                      nodeData.steps.map(item => (
                        item.extractVars.filter(it => (it.name && it.location) !== undefined).map((ele, i) => (
                          <Tooltip
                            placement="right"
                            title={
                              <Fragment>
                                <div>步骤名称：{item.name}</div>
                                <div>步骤类型：{item.stepType}</div>
                              </Fragment>
                            }
                            key={i}
                          >
                            <div className="groupContent">
                              <div className="name">{ele.name}</div>
                              <div className="remarks">
                                <span>{ele.path}</span>
                                <span>{ele.location ? locationList.find(x => x.key === ele.location).value : ''}</span>
                              </div>
                            </div>
                          </Tooltip>
                        ))
                      ))
                    }
                  </div>
                }
                {
                  choiceEnvData && choiceEnvData.data && <div className="group">
                    <div className="groupTitle">
                      <h3>环境变量</h3>
                      <Link to={'/envManage'} target="_blank">配置</Link>
                    </div>
                    {
                      choiceEnvData.data.map((item, i) => {
                        const keyList = Object.keys(item).filter(it => {
                          return it !== 'envVarName' && it !== 'envVarDesc' && it !== 'key' && it !== 'bizLine';
                        });
                        return <Tooltip
                          placement="right"
                          title={
                            keyList.map(key => (
                              <div key={key}>{key}：{item[key]}</div>
                            ))
                          }
                          key={i}
                        >
                          <div className="groupContent">
                            <div className="name">{item.envVarName}</div>
                            <div className="remarks">{item.envVarDesc}</div>
                          </div>
                        </Tooltip>;
                      })
                    }
                  </div>
                }
              </div>
            }

            <div style={{ float: 'right', width: haveSide ? 'calc(100% - 240px)' : '100%' }} id="mountNode"></div>
          </div>
        </div>
        <VariableDrawer
          visible={manageVisible}
          title={manageDrawerTitle}
          record={manageRecord}
          sideData={sideData}
          nodeData={nodeData}
          typeList={typeList}
          form={this.props.form}
          inputType={manageRecord.inputType}
          onClose={this.onManageClose}
          disabled={true}
        />
        <Drawer
          width={800}
          closable={false}
          visible={nodeVisible}
          title={nodeTitle}
          onClose={this.onNodeClose}
        >
          <Fragment>
            <Form.Item label="步骤名称" {...oneLineLayout} >
              {getFieldDecorator('label', {
                initialValue: node.name,
              })(
                <Input disabled />
              )}
            </Form.Item>
            {
              moreMenuChoise.some(menu => menu === '执行条件') &&
              <Fragment>
                <Row>
                  <Col span={16}>
                    <Form.Item label="执行条件" {...twoLineLayout} >
                      {getFieldDecorator('variable', {
                        initialValue: node.condition.rules ? node.condition.rules[0].variable : '',
                      })(
                        // <Selects optionList={this.props.sideAllSelectData} disabled placeholder="请输入参数名" />
                        <InputMentions optionList={this.props.sideAllSelectData} placeholder="请输入参数名" disabled />
                      )}
                    </Form.Item>
                  </Col>
                  <Col span={7}>
                    <Form.Item label="" {...fourLineLayout} >
                      {getFieldDecorator('operator', {
                        initialValue: node.condition.rules ? node.condition.rules[0].operator : undefined,
                      })(
                        <Select style={{ width: '100%' }} disabled>
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
                <Form.Item label={node.condition.rules[0].operator === 'REGEX' ? '正则表达式' : '值'} {...oneLineLayout} >
                  {getFieldDecorator(node.condition.rules[0].operator === 'REGEX' ? 'regex' : 'value', {
                    initialValue: node.condition.rules[0].operator === 'REGEX' ? node.condition.rules[0].regex : node.condition.rules[0].value,
                  })(
                    node.condition.rules[0].operator === 'REGEX' ? <Input placeholder="请输入值" /> :
                    // <Selects optionList={this.props.sideAllSelectData} disabled placeholder="值" />
                    <InputMentions optionList={this.props.sideAllSelectData} placeholder="值" disabled />
                  )}
                </Form.Item>
              </Fragment>
            }
            {
              moreMenuChoise.some(menu => menu === '执行延迟') &&
              <Form.Item label="执行延迟" {...oneLineLayout} >
                {getFieldDecorator('delay', {
                  initialValue: node.delay,
                })(
                  <InputNumber
                    formatter={value => `${value}ms`}
                    parser={value => value.replace('ms', '')}
                    style={{ width: '100%' }}
                    disabled
                  />
                )}
              </Form.Item>
            }
            {
              moreMenuChoise.some(menu => menu === '别名') &&
              <Form.Item label="别名" {...oneLineLayout} >
                {getFieldDecorator('alias', {
                  initialValue: node.alias,
                })(
                  <Input disabled />
                )}
              </Form.Item>
            }
            {
              moreMenuChoise.some(menu => menu === '前置脚本') &&
              <Form.Item label="前置脚本" {...oneLineLayout} >
                {getFieldDecorator('preStepScript', {
                  initialValue: node.preStepScript,
                })(
                  <MonacoEditors language="javascript" readOnly={true} />
                )}
              </Form.Item>
            }
            {node.stepType === 'HTTP' && <HttpType {...useTypeParams} />}
            {node.stepType === 'DUBBO' && <Dubbo {...useTypeParams} />}
            {node.stepType === 'MYSQL' && <Mysql {...useTypeParams} />}
            {node.stepType === 'REDIS' && <Redis {...useTypeParams} />}
            {node.stepType === 'ES' && <Es {...useTypeParams} />}
            {node.stepType === 'GROOVY' && <Groovy {...useTypeParams} />}
            {node.stepType === 'IMPORT' && <Import {...useTypeParams} />}
            {node.stepType === 'NOTICE' && <Notice {...useTypeParams} />}
            {
              moreMenuChoise.some(menu => menu === '变量提取') &&
              <Fragment>
                <Row>
                  <Col span={24}>
                    <Form.Item label="变量提取" {...threeLineLayout} style={{ marginBottom: 0, marginLeft: '16.5%' }}>
                      <Tooltip placement="top" title={
                        <Fragment>
                          <div>1、提取点数据为单一结果时(如数字、字符串)，变量路径可直接为空</div>
                          <div>2、提取点数据为对象时，变量路径填写json path</div>
                        </Fragment>
                      }>
                        <Icon type="question-circle" theme="filled" />
                      </Tooltip>
                    </Form.Item>
                  </Col>
                </Row>
                {node.extractVars.map((item, i) => (
                  <Row key={item.id} style={{ marginBottom: i === node.extractVars.length - 1 ? 24 : 10 }}>
                    <Col span={5} style={{ marginLeft: '16.5%' }}>
                      <Select
                        style={{ width: '95%' }}
                        allowClear
                        value={item.location}
                        disabled
                      >
                        {
                          locationList.map(item => (
                            <Option key={item.key} value={item.key}>{item.value}</Option>
                          ))
                        }
                      </Select>
                    </Col>
                    <Col span={7}>
                      <Input
                        value={item.name}
                        disabled
                      />
                    </Col>
                    <Col span={7}>
                      <Input
                        style={{ marginLeft: '2%' }}
                        value={item.path}
                        disabled
                      />
                    </Col>
                  </Row>
                ))}
              </Fragment>
            }
            {
              moreMenuChoise.some(menu => menu === '后置脚本') &&
              <Form.Item label="后置脚本" {...oneLineLayout} >
                {getFieldDecorator('postStepScript', {
                  initialValue: node.postStepScript,
                })(
                  <MonacoEditors language="javascript" readOnly={true} />
                )}
              </Form.Item>
            }
            <Form.Item label="步骤说明" {...oneLineLayout} >
              {getFieldDecorator('remark', {
                initialValue: node.remark,
              })(
                <TextArea
                  autoSize={{ minRows: 3 }}
                  style={{ marginBottom: '50px' }}
                  disabled
                />
              )}
            </Form.Item>
          </Fragment>
        </Drawer>
        {this.state.visible &&
          <Modal
            title={'JSON编辑器'}
            visible={true}
            onCancel={() => this.setState({ visible: false })}
            width={700}
            footer={null}
          >
            <ViewJsonEditor value={this.state.jsonValue} />
          </Modal>
        }
      </Fragment>
    );
  }
}
export default connect((state) => ({
  operatorList: state.initialValueObj.operatorList,
  dateFormat: state.initialValueObj.dateFormat,
  dubboParamType: state.initialValueObj.dubboParamType
}), intialValueAction)(
  Form.create()(SeeAssemblyLine)
);