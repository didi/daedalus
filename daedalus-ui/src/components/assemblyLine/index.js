/* eslint-disable */
import React, { Component, Fragment } from 'react';
import { Link } from 'react-router-dom';
import { Button, Popconfirm, Icon,  Form, Tooltip, message, Spin } from 'antd';
import { Prompt } from 'react-router-dom';
import { connect, } from 'react-redux';
import Sortable from 'sortablejs';
import lodash from 'lodash';
import request from '@/util/request';
import XMind from './nodeCanvas';
import VariableDrawer from './variableDrawer';
import AssemblyLineModal from './assemblyLineModal';
import intialValueAction from '../../store/action/intialValueAction';
import DebugMoadl from './debugModal';
import Mapping from '../public/mapping';
const { inputTypeList, locationList, valueAllTypeList } = Mapping; // valueTypeList,

class AssemblyLine extends Component {
  constructor(props) {
    super(props);
    sessionStorage.removeItem('nodeData');
  }
  state = {
    visible: false, // 是否展示抽屉
    getNodeLoading: false,
    title: '',
    inputType: '',
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
    ], // 左侧数据
    record: {}, // 当前点击的数据
    modalVisible: this.props.match.params.type === 'add' ? true : false,  // 新建编辑流水线弹框
    switchValue: false, // 多环境支持
    modalTitle: '新建流水线',
    modalData: {},  // 弹框数据
    back: true, // 是否检测路由变化
    choiceEnvData: [],
    nodeData: {},
    loading: false,
    deleteLoading: false,
    sideAllData: {
      envList: [],
      inputVars: [],
      globalVars: [],
      extractVars: [],
    },  // 左侧所有数据
    typeList: [], // 时间格式列表
    selType: null,
    debugVisible: false, // 调试弹框
  };

  componentDidMount() {
    window.addEventListener('beforeunload', this.beforeunload);
    if (this.props.match.params.type !== 'add') {
      this.getNodeData();
    }
    var el = document.getElementById('group');
    Sortable.create(el, {
      animation: 150,
      filter: '.btn',
      onEnd: e => {
        let a = lodash.cloneDeep(this.state.sideData);
        let newIndex = e.newIndex;
        let oldIndex = e.oldIndex;
        let groupList = lodash.cloneDeep(a[0].groupList);
        let temp = groupList.splice(oldIndex, 1)[0];
        groupList.splice(newIndex, 0, temp);
        a[0].groupList = lodash.cloneDeep(groupList);
        this.setState({ sideData: lodash.cloneDeep(a) })
      }
    });
  }
  componentWillUnmount() {
    // 销毁拦截判断是否离开当前页面
    window.removeEventListener('beforeunload', this.beforeunload);
  }
  // 监听刷新、更换路由、关闭浏览器事件
  beforeunload = e => {
    let confirmationMessage = '系统可能不会保存您所做的更改?';
    (e || window.event).returnValue = confirmationMessage;
    return confirmationMessage;
  }
  // prompt文案，如不写完面，路径就会发生变化
  promptMessage = () => {
    let confirmationMessage = '系统可能不会保存您所做的更改?';
    return confirmationMessage;
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
        const { sideAllData } = this.state;
        if (res.data.data.length > 0) {
          res.data.data.map(item => {
            sideAllData.envList.push({
              key: item.envVarName,
              value: item.envVarName,
              name: '环境变量'
            });
          });
        }
        this.setState({ choiceEnvData: res.data, sideAllData });
      }
    });
  }
  // 获取节点数据
  getNodeData = () => {
    this.setState({ getNodeLoading: true });
    request(`/pipeline/detail`, {
      method: 'GET',
      params: {
        pipelineId: this.props.match.params.type.split('-')[1]
      }
    }).then(res => {
      if (res.success === true) {
        const { sideAllData } = this.state;
        res.data.variable.inputVars.map(item => {
          sideAllData.inputVars.push({
            key: item.name + item.inputType,
            value: item.name,
            name: '运行输入'
          });
          if (item.options.length > 0) {
            item.options.map((ele, i) => {
              ele.id = i;
              return ele;
            });
          }
          return item;
        });
        res.data.variable.globalVars.map((item, i) => {
          sideAllData.globalVars.push({
            // key: item.name + item.valueType,
            key: item.name + i,
            value: item.name,
            name: '全局变量'
          });
        });
        const sideData = [
          {
            group: '运行输入',
            groupId: '1',
            groupList: res.data.variable.inputVars
          },
          {
            group: '全局变量',
            groupId: '2',
            groupList: res.data.variable.globalVars
          },
        ];

        let nodeDatas = {};
        if (res.data.flow) {
          res.data.flow.nodes = res.data.flow.steps;
          delete res.data.flow.steps;
          nodeDatas = res.data.flow;
          nodeDatas.nodes.map(item => {
            item.extractVars.map(ele => {
              sideAllData.extractVars.push({
                key: item.id,
                value: ele.name,
                name: '提取变量'
              });
            });
          });
        }
        if (res.data.envSupport) {
          this.getEnvDetail(res.data.envGroupId);
        }
        this.setState({
          modalData: res.data,
          nodeData: nodeDatas,
          switchValue: res.data.envSupport,
          sideData,
          sideAllData
        });
      }
      this.setState({ getNodeLoading: false });
    });
  }
  // 输入形式
  inputTypeChange = value => {
    let typeList = [];
    if (value === 'DATE_PICKER') {
      typeList = [{ desc: 'yyyyMMdd', name: 'yyyyMMdd' }];
    } else if (value === 'TIME_PICKER') {
      typeList = [{ desc: 'HH:mm:SS', name: 'HHmmSS' }];
    } else {
      typeList = this.props.dateFormat;
    }
    this.setState({ inputType: value, typeList });
    this.props.form.setFieldsValue({
      dateFormat: undefined,
      options: (value === 'SELECT' || value === 'RADIO' || value === 'CHECKBOX') ? [{ id: new Date().getTime() }] : undefined,
    });
  }
  // 删除组里的项
  onDelGroupItem = record => {
    const { sideData, groupId, sideAllData } = this.state;
    sideData.forEach(item => {
      if (item.groupId === (record.groupId || groupId)) {
        item.groupList.forEach((ele, i) => {
          if (ele.name === record.name) {
            item.groupList.splice(i, 1);
          }
        });
      }
    });
    const filterGroupList = sideData.find(it => it.groupId === groupId).groupList;
    if (groupId === '1') {
      sideAllData.inputVars = [];
      filterGroupList.map(it => {
        sideAllData.inputVars.push({
          key: it.name + it.inputType,
          value: it.name,
          name: '输入变量'
        });
      });
    } else {
      sideAllData.globalVars = [];
      filterGroupList.map(it => {
        sideAllData.globalVars.push({
          key: it.name,
          value: it.name,
          name: '全局变量'
        });
      });
    }
    this.setState({ sideData, sideAllData }, this.onClose());
  }
  // 展示编辑抽屉
  showDrawer = (title, groupId, record, i) => {
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
      visible: true,
      title,
      groupId,
      record,
      typeList
    });
    if (title.indexOf('编辑') > -1) {
      this.setState({ inputType: record.inputType });
    }
  };
  // 关闭抽屉
  onClose = () => {
    this.setState({
      visible: false,
      title: '',
      inputType: '',
      groupId: '',
    });
    this.props.form.resetFields();
  };
  // 确认
  onOk = () => {
    const { sideData, groupId, record, title, sideAllData } = this.state;
    this.props.form.validateFields((error, value) => {
      if (!error) {
        if (title.indexOf('编辑') > -1) {
          sideData.forEach(side => {
            if (side.groupId === groupId) {
              side.groupList.forEach(ele => {
                for (let k in value) {
                  if (ele.name === record.name) {
                    ele[k] = value[k];
                    // if (value.options[value.options.length - 1].display && value.options[value.options.length - 1].name) {
                    //   ele.options = value.options;
                    // }
                    if(value.optionRelations && value.optionRelations.length === 1 && (Object.keys(value.optionRelations[0]).length === 1 || value.optionRelations.every(item => item.targetOptions && item.targetOptions.length === 0 && item.showOnOptions && item.showOnOptions.length === 0))){
                      ele.optionRelations = [];
                    }
                  }
                }
              });
            }
          });
          const filterGroupList = sideData.find(it => it.groupId === groupId).groupList;
          if (groupId === '1') {
            sideAllData.inputVars = [];
            filterGroupList.map(it => {
              sideAllData.inputVars.push({
                key: it.name + it.inputType,
                value: it.name,
                name: '输入变量'
              });
            });
          } else {
            sideAllData.globalVars = [];
            filterGroupList.map(it => {
              sideAllData.globalVars.push({
                key: it.name,
                value: it.name,
                name: '全局变量'
              });
            });
          }
          this.setState({ sideData }, () => this.onClose());
        } else {
          let obj = { ...value };
          obj.groupId = groupId;
          obj.id = new Date().getTime();
          // if (value.options.length > 0 && value.options.some(ele => ele.display !== undefined)) {
          //   obj.options = value.options;
          // }
          if(value.optionRelations && value.optionRelations.length === 1 && (Object.keys(value.optionRelations[0]).length === 1 || value.optionRelations.every(item => item.targetOptions && item.targetOptions.length === 0 && item.showOnOptions && item.showOnOptions.length === 0))){
            obj.optionRelations = [];
          }
          sideData.forEach(side => {
            if (side.groupId === groupId) {
              side.groupList.push({ ...obj });
              if (groupId === '1') {
                sideAllData.inputVars = [];
                side.groupList.map(it => {
                  sideAllData.inputVars.push({
                    key: it.name + it.inputType,
                    value: it.name,
                    name: '输入变量'
                  });
                });
              } else {
                sideAllData.globalVars = [];
                side.groupList.map(it => {
                  sideAllData.globalVars.push({
                    key: it.name,
                    value: it.name,
                    name: '全局变量'
                  });
                });
              }
            }
          });
          this.setState({ sideData, sideAllData }, () => { this.onClose() });
        }
      }
    });
  }
  // 设置基本信息
  setUpInfo = () => {
    this.setState({ modalVisible: true, modalTitle: '编辑流水线' });
  }
  // 基本信息取消弹框x
  handleCancel = form => {
    this.setState({
      modalVisible: false,
      switchValue: false,
      // modalTitle: '新建流水线'
    }, () => {
      form.resetFields();
      if (this.props.match.params.type === 'add' && this.state.modalTitle === '新建流水线') {
        this.setState({ back: false }, () => {
          this.props.history.push('/workbench');
        });
      }
    });
    if (this.state.modalTitle === '新建流水线') this.setState({ modalData: {}});
  }
  // 基本信息弹框确认
  handleOk = form => {
    form.validateFields((error, value) => {
      if (!error) {
        const modalData = { ...value };
        const { sideAllData } = this.state;
        sideAllData.envList = [];
        const envFilterList = this.props.envList.find(x => modalData.envGroupId === x.id) || { data: [] };
        envFilterList.data.length > 0 && envFilterList.data.map(item => {
          sideAllData.envList.push({
            key: item.envVarName,
            value: item.envVarName,
            name: '环境变量'
          });
        });
        form.resetFields();
        this.setState({
          modalVisible: false,
          modalData,
          choiceEnvData: modalData.envSupport ? this.props.envList.find(x => modalData.envGroupId === x.id) : [],
          sideAllData
        });
      }
    });
  }
  // 多环境支持
  switchChange = checked => {
    this.setState({ switchValue: checked });
  }
  // 返回button
  back = () => {
    this.setState({ back: false }, () => {
      this.props.history.push('/workbench');
    });
  }
  // 删除多余字段
  deleteAttribute = obj => {
    delete obj.labelCfg;
    delete obj.size;
    delete obj.style;
    delete obj.type;
    delete obj.x;
    delete obj.y;
    delete obj.controlPoints;
    delete obj.endPoint;
    delete obj.startPoint;
    delete obj.sourceNode;
    delete obj.targetNode;
    return obj;
  }
  // 保存button
  submit = () => {
    this.setState({ loading: true });
    let gtoupInfo = {
      inputVars: this.state.sideData[0].groupList,
      globalVars: this.state.sideData[1].groupList
    };
    const allData = { ...this.state.modalData };
    allData.variable = gtoupInfo;
    allData.flow = JSON.parse(sessionStorage.getItem('nodeData'));
    if (allData.flow) {
      allData.flow.steps = allData.flow.nodes;
      delete allData.flow.nodes;
      allData.flow.steps.forEach(item => this.deleteAttribute(item));
      allData.flow.edges.forEach(item => this.deleteAttribute(item));
    }
    if (this.props.match.params.type === 'add') {
      request(`/pipeline/create`, {
        method: 'POST',
        body: {
          ...allData
        }
      }).then(res => {
        if (res.success === true) {
          this.setState({ back: false });
          message.success('创建成功');
          this.props.history.push(`/function/${res.data}`);
        } else {
          message.error(res.msg);
        }
        this.setState({ loading: false });
      });
    } else {
      delete allData.createTime;
      delete allData.updateTime;
      request(`/pipeline/update`, {
        method: 'POST',
        body: {
          ...allData,
          id: this.props.match.params.type.split('-')[1],
        }
      }).then(res => {
        if (res.success === true) {
          this.setState({ back: false });
          message.success('修改成功');
        } else {
          message.error(res.msg);
        }
        this.setState({ loading: false });
      });
    }
  }
  // 删除流水线
  deleteLine = () => {
    this.setState({ deleteLoading: true });
    request(`/pipeline/delete?pipelineId=${this.props.match.params.type.split('-')[1]}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        this.setState({ back: false, deleteLoading: false });
        message.success('删除成功');
        this.props.history.push(`/workbench`);
      } else {
        message.error(res.msg);
      }
    });
  }
  // 查看变量位置
  position = (e, variable) => {
    this.setState({ selType: variable === this.state.selType ? null : variable })
    e.stopPropagation();
  }
  // 调试
  debug = () => {
    this.setState({ debugVisible: true });
  }
  // 关闭调试
  closeDebugModal = () => {
    this.setState({ debugVisible: false });
  }

  render() {
    const {
      visible, title, inputType, sideData, record, modalVisible, switchValue, modalTitle, modalData, back, choiceEnvData,
      nodeData, loading, deleteLoading, sideAllData, getNodeLoading, typeList, debugVisible
    } = this.state;
    // let valueType = [];
    // if (title.indexOf('输入') > -1) {
    //   valueType = valueAllTypeList;
    // } else {
    //   valueType = valueTypeList;
    // }
    return (
      <Spin spinning={getNodeLoading}>
        <Prompt
          message={this.promptMessage()}
          when={back}
        />
        <div className="edit">
          <div className="title">
            <h2>{this.props.match.params.type === 'add' ? '新建流水线' : '编辑流水线'}{modalData.name ? ':' + modalData.name : ''}</h2>
            <div>
              <Popconfirm
                title="确认返回？未保存数据将会清除掉?"
                onConfirm={this.back}
                okText="确认"
                cancelText="取消"
              >
                <Button>返回</Button>
              </Popconfirm>
              {
                this.props.match.params.type.split('-')[0] === 'edit' &&
                <Button onClick={() => {
                  this.props.history.push(`/function/${this.props.match.params.type.split('-')[1]}`)}
                }>去运行</Button>
              }
              <Button onClick={this.setUpInfo}>设置基本信息</Button>
              <Button type="primary" onClick={this.debug}>调试</Button> 
              <Button type="primary" onClick={this.submit} loading={loading}>保存</Button>
              {
                this.props.match.params.type !== 'add' &&
                <Popconfirm
                  title="确认删除么?"
                  onConfirm={this.deleteLine}
                  okText="是"
                  cancelText="否"
                >
                  <Button type="danger" loading={deleteLoading}>删除</Button>
                </Popconfirm>
              }
            </div>
          </div>
          <div style={{ background: '#e8e8e8', padding: '10px 20px', margin: '10px 20px' }}>
            如需使用运行输入、全局变量、提取变量、环境变量中的变量，请在任意位置用
            <span style={{ color: 'red' }}>{`#{变量名}`}</span> 方式使用。
          </div>
          <div className="content">
            <div className="allGroups">
              {
                sideData.map(item => (
                  <div className="group" key={item.groupId}>
                    <div className="groupTitle">
                      <h3>{item.group}</h3>
                      {item.groupList.length > 0 && <Icon
                        type="plus-circle"
                        theme="filled"
                        className="plus-circle"
                        onClick={() => {
                          this.showDrawer(item.group.indexOf('输入') > -1 ? '添加运行输入' : '添加全局变量', item.groupId)
                        }}
                      />}
                    </div>
                    <div id={item.group.indexOf('输入') > -1 ? 'group' : 'allGlobal'}>
                    {
                      item.groupList.length > 0 ? item.groupList.map((ele, i) => (
                        <div
                          className="groupContent"
                          key={ele.name}
                          onClick={() => {
                            this.showDrawer(item.group.indexOf('输入') > -1 ? `${ele.label} - 输入编辑` : `${ele.name} - 全局变量编辑`, item.groupId, ele, i)
                          }}
                        >
                          <div className="name">
                            <span>{ele.name}</span>
                            <a onClick={e => this.position(e, ele.name)}>
                              <span class="iconfont icon-location" title='使用定位'></span>
                            </a>
                          </div>
                          <div className="remarks">
                            <span>{ele.value || ele.label}</span>
                            {item.group.indexOf('输入') > -1 && <span>{inputTypeList.find(x => x.key === ele.inputType).value}</span>}
                          </div>
                        </div>
                      )) :
                      <div className="btn">
                        <Button
                          icon="plus"
                          onClick={() => { this.showDrawer(item.group.indexOf('输入') > -1 ? '添加运行输入' : '添加全局变量', item.groupId) }}
                        >{item.group.indexOf('输入') > -1 ? '添加运行输入' : '添加全局变量'}</Button>
                      </div>
                    }
                    </div>
                  </div>
                ))
              }
              {
                Object.keys(nodeData).length > 0 &&
                nodeData.nodes.length > 0 &&
                !nodeData.nodes.every(it => it.extractVars.filter(ele => (ele.name && ele.location) !== undefined).length === 0) &&
                <div className="group">
                  <div className="groupTitle">
                    <h3>提取变量</h3>
                  </div>
                  {
                    nodeData.nodes.map(item => (
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
                            <div className="name">
                              <span>{ele.name}</span>
                              <a onClick={e => this.position(e, ele.name)}>
                                <span class="iconfont icon-location" title='使用定位'></span>
                              </a>
                            </div>
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
                          <div className="name">
                              <span>{item.envVarName}</span>
                              <a onClick={e => this.position(e, item.envVarName)}>
                                <span class="iconfont icon-location" title='使用定位'></span>
                              </a>
                            </div>
                          <div className="remarks">{item.envVarDesc}</div>
                        </div>
                      </Tooltip>;
                    })
                  }
                </div>
              }
            </div>
            <XMind
              onCallback={() => {
                const nodeData = sessionStorage.getItem('nodeData') ? JSON.parse(sessionStorage.getItem('nodeData')) : {};
                this.setState({ nodeData }, () => {
                  const { sideAllData } = this.state;
                  sideAllData.extractVars = [];
                  nodeData && nodeData.nodes.length > 0 && nodeData.nodes.map(item => {
                    item.extractVars.filter(it => (it.name && it.location) !== undefined).map(ele => {
                      sideAllData.extractVars.push({
                        key: item.id,
                        value: ele.name,
                        name: '提取变量'
                      });
                    });
                  });
                  this.setState({ sideAllData });
                });
              }}
              selType={this.state.selType}
              type={this.props.match.params.type}
              data={this.state.nodeData}
              sideAllData={sideAllData}
            />
          </div>
        </div>
        <VariableDrawer
          visible={visible}
          title={title}
          record={record}
          sideData={sideData}
          nodeData={nodeData}
          inputTypeChange={this.inputTypeChange}
          typeList={typeList}
          form={this.props.form}
          inputType={inputType}
          disable={false}
          onClose={this.onClose}
          onOk={this.onOk}
          onDelGroupItem={this.onDelGroupItem}
        />
        <AssemblyLineModal
          type={this.props.match.params.type}
          modalVisible={modalVisible}
          switchValue={switchValue}
          modalTitle={modalTitle}
          modalData={modalData}
          handleCancel={this.handleCancel}
          handleOk={this.handleOk}
          switchChange={this.switchChange}
        />
        <DebugMoadl
          visible={debugVisible}
          inputVars={this.state.sideData[0].groupList}
          closeDebugModal={this.closeDebugModal}
          envData={choiceEnvData}
          info={modalData}
          sideData={sideData}
        />
      </Spin>
    );
  }
}
export default connect((state) => ({
  dateFormat: state.initialValueObj.dateFormat,
  envList: state.initialValueObj.envList,
}), {
  ...intialValueAction,
})(
  Form.create()(AssemblyLine)
);