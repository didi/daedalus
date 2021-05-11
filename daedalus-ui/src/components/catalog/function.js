/* eslint-disable */
import React, { Component, Fragment } from 'react';
import {
  Button, Tabs, Row, Col, Select, Form, Input, Checkbox, Radio, Table, Divider, Pagination, ConfigProvider,
  Tree, Icon, message, InputNumber, Empty, Spin, Switch, Tag
} from 'antd';
import { withRouter } from 'react-router-dom';
import zhCN from 'antd/es/locale/zh_CN';
import QRCode from 'qrcode.react';
import SeeAssemblyLine from '../function/seeAssemblyLine';
import request from '@/util/request';
import FormatPicker from '../public/formatPicker';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import SeeInfoModal from '../function/seeInfoModal';
const { TextArea } = Input;
const { TabPane } = Tabs;
const { Option } = Select;
const { TreeNode } = Tree;
const oneItemLayout = {
  labelCol: {
    xs: { span: 5 }
  },
  wrapperCol: {
    xs: { span: 15 }
  }
};

class Function extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: '1',
      showTabActiveKey: '1',
      showBottomTags: true,
      tableList: [],
      historyLoading: false, // 历史记录是否获取完成
      total: 0,
      pageSize: 10,
      pageIndex: 1,
      record: {}, // 当前history操作的一条数据
      outputType: 'text', // 输出类型
      searchValue: '', // 日志搜索
      logList: [], // 提交以后获取的日志数据
      logloading: false, // 提交以后是否获取完日志数据
      autoExpandParent: true,
      expandedKeys: [],
      itemLog: [],  // 运行日志当前点击步骤日志
      logResult: '',  // 运行日志当前点击步骤结果
      submitLoading: false,
      outputData: '',
      favoriteLoading: false,
      editPermit: false,
      logId: '',
      selectedKeys: [], // 日志tree列表当前所点击
      modalVisible: false,
      switchChecked: true, // 步骤 是:筛选 否:全部
    };
  }
  componentWillReceiveProps(nextProps) {
    if (this.props.id !== nextProps.id) {
      this.setState({ 
        activeKey: '1',
        showTabActiveKey: '1',
        logList: [],
        tableList: [],
        showBottomTags: true,
        outputType: 'text',
        total: 0,
        pageSize: 10,
        pageIndex: 1,
        record: {},
        searchValue: '',
        logList: [],
        logResult: '',
        itemLog: [],
        logId: '',
        selectedKeys: [],
        modalVisible: false,
        switchChecked: true, // 步骤 是:筛选 否:全部
      }, () => {
        this.getHistoryList(nextProps.id);
        this.getPermit(nextProps.id);
      });
    }
  }
  componentDidMount() {
    this.getHistoryList(this.props.id);
    this.getPermit(this.props.id);
  }
  // 是否有编辑权限
  getPermit = () => {
    request(`/pipeline/permit`, {
      method: 'GET',
      params: {
        pipelineId: this.props.id
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ editPermit: res.data });
      }
    });
  }
  // 获取历史记录的tabe数据
  getHistoryList = id => {
    this.setState({ historyLoading: true });
    request(`/log/list`, {
      method: 'GET',
      params: {
        pipelineId: id || this.props.id,
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ tableList: res.data, total: res.total });
      }
      this.setState({ historyLoading: false });
    });
  }
  // 获取日志数据
  getJournalData = () => {
    if (this.state.logId) {
      this.setState({ logloading: true, selectedKeys: [] });
      request(`/log/detail`, {
        method: 'GET',
        params: {
          logId: this.state.logId,
        }
      }).then(res => {
        if (res.success === true) {
          this.setState({
            logList: res.data.stepLogs
          });
        }
        this.setState({ logloading: false });
      });
    }
  }
  // 最上层tag发生变化
  onChange = key => {
    this.setState({ activeKey: key });
    if (key === '2') {
      this.setState({ showBottomTags: false });
    } else {
      this.setState({ showBottomTags: true });
    }
  }
  // 下面tag发生变化（输出、历史记录、日志）
  showTabOnChange = key => {
    this.setState({ showTabActiveKey: key, switchChecked: true });
    if (key !== '2') this.setState({ outputType: 'text' });
    if (key === '3') this.getJournalData();
    if (key !== '3') this.setState({ selectedKeys: [] });
  }
  // 重置
  reset = () => {
    this.setState({
      showTabActiveKey: '1',
      record: {},
      logId: '',
    });
    this.props.form.resetFields();
  }
  // 提交
  submit = () => {
    this.props.form.validateFields((error, value) => {
      if (Object.keys(value).length > 0) {
        Object.keys(value).map(item => {
          if (!value[item]) {
            value[item] = undefined;
          }
        });
      }
      if (error) return;
      if ((!this.props.lineDetail.lineDetail.envSupport || this.props.currEnv)) {
        this.setState({ submitLoading: true });
        request(`/dispatch/call`, {
          method: 'POST',
          body: {
            pipelineId: this.props.id,
            inputs: {
              ...value
            },
            env: this.props.currEnv
          }
        }).then(res => {
          this.setState({
            submitLoading: false,
            logId: res.data ? res.data.logId : '',
          });
          this.getHistoryList();
          if (res.success === true) {
            this.setState({
              outputData: res.data.result,
              showTabActiveKey: '2',
            });
          } else {
            message.error(res.msg);
            if (res.data && res.data.logId) {
              this.setState({ showTabActiveKey: '3' });
              this.getJournalData();
            }
          }
        });
      } else {
        message.error('请到环境管理添加环境或关闭多环境支持！');
      }
    });
  }
  // table 显示
  getColumn = () => {
    const { formData } = this.props.lineDetail;
    let column = [];
    formData.length > 0 && formData.map(it => {
      column.push({
        dataIndex: it.name,
        title: it.label,
        render: (t, record) => {
          if(record.inputs) {
            let a = it.options.filter(item => item.value === record.inputs[it.name]);
            return <div>{a.length > 0 ? a[0].display : record.inputs[it.name]}</div>;
          }
          return ''
        }
      });
    });

    let column2 = [{
      dataIndex: 'pipelineId',
      title: '备注',
      render: (t, record) => {
        return <div>{record.inputs ? record.inputs['remark'] : ''}</div>;
      }
    }, {
      dataIndex: 'usernameCN',
      title: '使用人',
    },
    {
      dataIndex: 'createTime',
      title: '使用时间',
    },
    {
      dataIndex: 'id',
      title: '操作',
      fixed: 'right',
      render: (t, record) => {
        return (
          <div>
            <a onClick={() => this.historyUse(record)} disabled={record.inputs ? false : true}>使用</a>
            <Divider type="vertical" />
            <a onClick={() => this.historyFunction(record)}>运行</a>
            <Divider type="vertical" />
            <a onClick={() => this.historyProceedUse(record)} disabled={!(record.executeStatus && record.executeStatus === 'FAILED')}>继续运行</a>
            <Divider type="vertical" />
            <a onClick={() => this.seeLog(record)}>查看日志</a>
          </div>
        );
      }
    }];
    return [...column, ...column2];
  }
  // 历史记录使用
  historyUse = record => {
    this.props.form.setFieldsValue(record.inputs);
  }
  // 历史记录运行
  historyFunction = record => {
    this.setState({ record: record.inputs, submitLoading: true });
    request(`/dispatch/call`, {
      method: 'POST',
      body: {
        pipelineId: this.props.id,
        inputs: {
          ...record.inputs
        },
        env: this.props.currEnv
      }
    }).then(res => {
      this.setState({
        submitLoading: false,
        logId: res.data ? res.data.logId : '',
      });
      this.getHistoryList();
      if (res.success === true) {
        this.setState({
          outputData: res.data.result,
          showTabActiveKey: '2',
        });
      } else {
        if (res.data.logId) {
          this.setState({ showTabActiveKey: '3' });
          this.getJournalData();
        }
        message.error(res.msg);
      }
    });
  }
  // 历史记录继续运行
  historyProceedUse = record => {
    this.setState({ record: record.inputs, submitLoading: true });
    request(`/dispatch/callResume?logId=${record.id}`, {
      method: 'POST',
    }).then(res => {
      this.setState({
        submitLoading: false,
        logId: res.data ? res.data.logId : '',
      });
      this.getHistoryList();
      if (res.success === true) {
        this.setState({
          outputData: res.data.result,
          showTabActiveKey: '2',
        });
      } else {
        if(res.data.logId){
          this.setState({showTabActiveKey: '3'});
          this.getJournalData();
        }
        message.error(res.msg);
      }
    });
  }
  // 历史记录运行
  seeLog = record => {
    this.setState({
      showTabActiveKey: '3',
      logList: record.stepLogs
    });
  }
  // 历史分页器发生变化
  onChangePagination = (page, pageSize) => {
    this.setState({ pageIndex: page, pageSize }, () => {
      this.getHistoryList();
    });
  }
  // 历史分页器 pageSize 变化的回调
  onShowSizeChange = (current, pageSize) => {
    this.setState({ pageIndex: current, pageSize });
  }
  // 输出类型
  outputType = e => {
    this.setState({ outputType: e.target.value });
  }
  // 树形图
  tree = data => {
    return data.map(item => {
      let color = '';
      if (item.stepStatus === 'RUN_SUCCESS') color = '#08d608';
      if (item.stepStatus === 'RUN_FAILED') color = '#fb5d5d';
      return <TreeNode key={item.stepId} title={<span style={{ color: color }}>{item.stepName}</span>} switcherIcon={this.icon(item.stepStatus, color)} />;
    });
  }
  icon = (type, color) => {
    switch (type) {
      case 'NOT_RUN':
        return <Icon type="clock-circle" style={{ color: color }} />;
      case 'SKIPED':
        return <Icon type="minus-circle" style={{ color: color }} />;
      case 'RUN_SUCCESS':
        return <Icon type="check-circle" style={{ color: color }} />;
      default:
        return <Icon type="close-circle" style={{ color: color }} />;
    }
  }
  // 树形图每条显示隐藏
  onExpand = expandedKeys => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  };
  // 搜索框鼠标按下事件
  onPressEnter = e => {
    if (e.keyCode === 13) {
      this.onSearch(e.target.value);
    }
  }
  // 日志搜索
  onSearch = value => {
    // 所有数据
    const dataList = [];
    const generateList = data => {
      for (let i = 0; i < data.length; i++) {
        const node = data[i];
        dataList.push({ key: node.id, title: node.title });
        if (node.children) {
          generateList(node.children);
        }
      }
    };
    generateList(this.state.logList);
    // 获取当前有的父元素key值
    const getParentKey = (key, tree) => {
      let parentKey;
      for (let i = 0; i < tree.length; i++) {
        const node = tree[i];
        if (node.children) {
          if (node.children.some(item => item.id === key)) {
            parentKey = JSON.stringify(node.id);
          } else if (getParentKey(key, node.children)) {
            parentKey = getParentKey(key, node.children);
          }
        }
      }
      return parentKey;
    };
    // 所有符合的集合
    const expandedKeys = dataList.map(item => {
      if (item.title.indexOf(value) > -1) {
        return getParentKey(item.key, this.state.logList);
      }
      return null;
    })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    this.setState({
      expandedKeys,
      searchValue: value,
      autoExpandParent: true,
    });
  }
  // 树节点点击事件(有return警告)
  onSelect = value => {
    if (value.length > 0) {
      this.state.logList.map(item => {
        if (item.stepId === value[0]) {
          this.setState({
            itemLog: item.logs,
            logResult: item.stepResponse ? item.stepResponse.result : '',
            selectedKeys: value
          });
        }
      });
    }
  }
  isJson = text => {
    try {
      JSON.parse(text);
    } catch (e) {
      return false;
    }
    return true;
  }
  // 关闭查看基本信息
  handleCancel = () => {
    this.setState({ modalVisible: false });
  }
   // 收藏
   favorites = () => {
    const { lineDetail } = this.props.lineDetail;
    this.setState({ favoriteLoading: true });
    request(`/user/collect?isCollect=${!lineDetail.collect}&pipelineId=${lineDetail.id}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        lineDetail.collect = !lineDetail.collect;
        if (lineDetail.collect) {
          message.success('收藏成功');
        } else {
          message.success('取消收藏成功');
        }
        this.setState({ favoriteLoading: false });
        this.props.favoritesChange(lineDetail);
      } else {
        message.error(res.msg);
      }
    });
  }
  // 步骤筛选
  switchChange = checked => {
    this.setState({ switchChecked: checked });
  }

  render() {
    const { choiceEnvData, envData, formData, sideAllSelectData, lineDetail } = this.props.lineDetail;
    const bizList = this.props.bizLine || [];
    const {
      activeKey, showTabActiveKey, tableList, total, pageIndex, pageSize, record, outputType, logList, favoriteLoading,
      showBottomTags, submitLoading, outputData, historyLoading, itemLog, logResult, logloading, selectedKeys, editPermit,
      modalVisible, switchChecked,
    } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <div className="function">
        <ConfigProvider locale={zhCN}>
          <div className="header">
            <div>
              <span className="title">{lineDetail.name}</span>
              <a>业务线：{bizList.length > 0 && bizList.find(it => it.code === lineDetail.bizLine).name}</a>
              {lineDetail.creatorCN && <a style={{ marginRight: 30 }}>创建人：{lineDetail.creatorCN}</a>}
              {
                lineDetail.tags && lineDetail.tags.length > 0 && lineDetail.tags.slice(0, 5).map((item, i) => {
                  const tagColor = ['#f50', '#2db7f5', '#87d068', '#108ee9', 'skyblue'];
                  return <Tag color={tagColor[i]} key={i}>{item}</Tag>;
                })
              }
            </div>
            <div>
              {/*<Button onClick={() => this.props.history.push('/workbench')}>返回</Button>
              <Tooltip placement="top" title={'复制当前流水线地址'}>
                <Button onClick={() => this.share()}>分享</Button>
              </Tooltip>
              <Popconfirm placement="top" title="是否生成当前流水线副本" onConfirm={() => this.copy()} okText="确认" cancelText="取消">
                <Button>复制</Button>
            </Popconfirm>*/}
              <Button onClick={() => this.setState({ modalVisible: true })}>查看基本信息</Button>
              <Button onClick={() => this.favorites()} type={lineDetail.collect ? 'default' : 'primary'} loading={favoriteLoading}>{lineDetail.collect ? '已收藏' : '收藏'}</Button>
              {editPermit && <Button type="primary" onClick={() => this.props.history.push(`/assemblyLine/edit-${this.props.id}`)}>编辑</Button>}
            </div>
          </div>
          <Row>
            <Col span={24}>
              <Tabs
                onChange={this.onChange}
                activeKey={activeKey}
                type="card"
                tabBarExtraContent={
                  (activeKey === '1' && lineDetail.envSupport) ?
                    <div style={{ width: '300px', display: 'flex', alignItems: 'center' }}>
                      <div style={{ width: '55px' }}>环境：</div>
                      <Select
                        style={{ width: '100%' }}
                        placeholder="选择环境"
                        value={this.props.currEnv}
                        onChange={e => this.props.envChange(e, this.props.id)}
                      >
                        {
                          envData.length > 0 && envData.map(item => (
                            <Option key={item} value={item}>{item}</Option>
                          ))
                        }
                      </Select>
                    </div> : null
                }
              >
                <TabPane tab="运行" key="1" className="tabpane">
                  {lineDetail.remark && <div style={{ background: '#e8e8e8', padding: '10px 20px', marginBottom: 30 }}>{lineDetail.remark}</div>}
                  {
                    formData.length > 0 && formData.map((it, i) => {
                      let show = true; // 是否有判断条件显示
                        let options = it.optionRelations && it.optionRelations.length > 0 ? [] : it.options; // 关联选项
                        if(it.dependencyInputName && it.dependencyOptions.length > 0){
                        show = it.dependencyOptions.indexOf(getFieldValue(it.dependencyInputName)) > -1;
                      }
                      if(it.optionRelations && it.optionRelations.length > 0){
                          it.optionRelations.map(item => {
                          if(item.showOnOptions.indexOf(getFieldValue(it.dependencyInputName))> -1){
                            options.push(...it.options.filter(option => item.targetOptions.indexOf(option.value) > -1));
                          }
                        })
                      }
                      if(options && options.length === 0) options = it.options;
                      return <Fragment key={i}>
                        {it.inputType === 'INPUT' && show &&
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请输入！' }],
                            initialValue: record && record[it.name] ? record[it.name] : it.defaultValue
                          })(
                            <Input placeholder="请输入..." />
                          )}
                        </Form.Item>
                        }
                        {it.inputType === 'NUMBER_INPUT' && show &&
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请输入！' }],
                            initialValue: record && record[it.name] ? record[it.name] : it.defaultValue
                          })(
                            <InputNumber placeholder="请输入..." style={{ width: '100%' }} />
                          )}
                        </Form.Item>
                        }
                        {it.inputType === 'TEXTAREA' && show &&
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请输入！' }],
                            initialValue: record && record[it.name] ? record[it.name] : it.defaultValue
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
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请选择！' }],
                            initialValue: record && record[it.name] ? record[it.name] : it.defaultValue || undefined
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
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请选择！' }],
                            initialValue: record && record[it.name] ? record[it.name] : (it.options.length > 0 && it.options[0].value) ? it.options[0].value : undefined
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
                        <Form.Item label={it.label} {...oneItemLayout}>
                          {getFieldDecorator(it.name, {
                            rules: [{ required: it.required, message: '请选择！' }],
                            initialValue: record && record[it.name] ? record[it.name] : undefined
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
                          <Form.Item label={it.label} {...oneItemLayout}>
                            {getFieldDecorator(it.name, {
                              rules: [{ required: it.required, message: '请输入！' }],
                              initialValue: record && record[it.name] ? record[it.name] : undefined
                            })(
                              <FormatPicker type="DATE_PICKER" />
                            )}
                          </Form.Item>
                        }
                        {it.inputType === 'TIME_PICKER' && show &&
                          <Form.Item label={it.label} {...oneItemLayout}>
                            {getFieldDecorator(it.name, {
                              rules: [{ required: it.required, message: '请输入！' }],
                              initialValue: record && record[it.name] ? record[it.name] : undefined
                            })(
                              <FormatPicker type="TIME_PICKER" />
                            )}
                          </Form.Item>
                        }
                        {it.inputType === 'DATE_TIME_PICKER' && show &&
                          <Form.Item label={it.label} {...oneItemLayout}>
                            {getFieldDecorator(it.name, {
                              rules: [{ required: it.required, message: '请输入！' }],
                              initialValue: record && record[it.name] ? record[it.name] : undefined
                            })(
                              <FormatPicker type="DATE_TIME_PICKER" />
                            )}
                          </Form.Item>
                        }
                      </Fragment>;
                    })
                  }
                  <Form.Item label={'备注'} {...oneItemLayout}>
                    {getFieldDecorator('remark', {
                      initialValue: record && record.remark ? record.remark : ''
                    })(
                      <Input placeholder="请输入备注" />
                    )}
                  </Form.Item>
                  {
                    formData.length > 0 ? <Fragment>
                      <Button type="primary" style={{ marginLeft: '35%', marginRight: '30px' }} onClick={this.submit} loading={submitLoading}>提交</Button>
                      <Button onClick={this.reset}>重置</Button>
                    </Fragment> : <Button type="primary" onClick={this.submit} loading={submitLoading} style={{ marginLeft: '35%' }}>运行</Button>
                  }
                </TabPane>
                {
                  Object.keys(lineDetail).length > 0 && lineDetail.flow && lineDetail.flow.steps.length > 0 && <TabPane tab="流水线" key="2">
                    <SeeAssemblyLine
                      lineDetail={lineDetail}
                      choiceEnvData={choiceEnvData}
                      sideAllSelectData={sideAllSelectData}
                    />
                  </TabPane>
                }
              </Tabs>
            </Col>
          </Row>
          {showBottomTags &&
          <Tabs
            onChange={this.showTabOnChange}
            activeKey={showTabActiveKey}
            type="card"
            style={{ marginTop: 20 }}
          >
            <TabPane tab={<div onClick={() => this.getHistoryList()}>历史记录</div>} key="1">
              <Table
                className="history_table"
                columns={this.getColumn()}
                dataSource={tableList}
                loading={historyLoading}
                expandedRowRender={record => <p style={{ margin: 0, paddingRight: '250px' }}>{record.result ? '运行结果：' + record.result : '暂无结果'}</p>}
                rowClassName={record => !record.result && 'noExpand'}
                rowKey="id"
                scroll={{ x: true }}
                footer={(currentData) => (
                  <div style={{ display: total === 0 ? 'none' : 'block', }}>
                    <span style={{ lineHeight: '32px', marginRight: 10 }}>共计：{total} 条数据</span>
                    <span>第{pageIndex} / {Math.ceil(total / pageSize)}页</span>
                    {
                      <div style={{ float: 'right', margin: '0px 0' }}>
                        <Pagination
                          onChange={this.onChangePagination}
                          current={pageIndex}
                          total={total}
                          pageSize={pageSize}
                          pageSizeOptions={['5', '10', '20']}
                          showSizeChanger
                          showQuickJumper
                          onShowSizeChange={this.onShowSizeChange}
                        />
                      </div>
                    }
                  </div>
                )}
                pagination={false}
              />
            </TabPane>
            <TabPane tab="运行输出" key="2">
              <div style={{ textAlign: 'right', marginBottom: 20, }}>
                <Radio.Group
                  value={outputType}
                  onChange={this.outputType}
                >
                  <Radio value="text">Text</Radio>
                  <Radio value="json">JSON</Radio>
                  <Radio value="二维码">二维码</Radio>
                  {/* <Radio value="table">表格</Radio> */}
                </Radio.Group>
              </div>
              {outputType === 'text' &&
                <div className="textarea">{outputData}</div>
              }
              {outputType === 'json' &&
                <pre className="textarea">
                  {outputData && this.isJson(outputData) ? JSON.stringify(JSON.parse(outputData), null, 2) : outputData}
                </pre>
              }
              {outputType === '二维码' &&
                (outputData ? <div style={{ textAlign:'center' }}>
                  <QRCode value={outputData || '暂无数据'} size={200} style={{ textAlign:'center' }}/>
                </div> :
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)
              }
              {/*outputType === 'table' &&
              <Table
                columns={this.getColumn()}
                dataSource={tableList}
                expandedRowRender={record => <div>{record.description ? record.description : '暂无结果'}</div>}
                rowClassName={record => !record.description && 'noExpand'}
                rowKey="id"
                footer={(currentData) => (
                  <div>
                    <span style={{ lineHeight: '32px', marginRight: 10 }}>共计：{total} 条数据</span>
                    <span>第{pageIndex} / {Math.ceil(total / pageSize)}页</span>
                    {
                      <div style={{ display: total === 0 ? 'none' : 'block', float: 'right', margin: '0px 0' }}>
                        <Pagination
                          onChange={this.onChangePagination}
                          current={pageIndex}
                          total={total}
                          pageSize={pageSize}
                          pageSizeOptions={['5', '10', '20']}
                          showSizeChanger
                          showQuickJumper
                          onShowSizeChange={this.onShowSizeChange}
                        />
                      </div>
                    }
                  </div>
                )}
                pagination={false}
              />*/}
            </TabPane>
            <TabPane tab="运行日志" key="3">
              <Spin spinning={logloading}>
                {
                  logList.length === 0 ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} /> : <div className="journal">
                    <Row>
                      <Col span={4} className="leftTitle spaceBetween">
                        <span>步骤</span>
                        <Switch checkedChildren="筛选" unCheckedChildren="全部" checked={switchChecked} onChange={this.switchChange}/>
                      </Col>
                      <Col span={12} className="centerTitle">日志</Col>
                      <Col span={8} className="rightTitle">结果</Col>
                    </Row>
                    <Row type="flex">
                      <Col span={4} className="leftContent">
                        <Tree
                        // showLine
                          switcherIcon={<Icon type="down" />}
                          // expandedKeys={expandedKeys}
                          onSelect={this.onSelect}
                          className="tree"
                          // onExpand={this.onExpand}
                          // autoExpandParent={autoExpandParent}
                          selectedKeys={selectedKeys}
                        >
                          {this.tree(!switchChecked ? logList : logList.filter(item => item.stepStatus === 'RUN_SUCCESS' || item.stepStatus === 'RUN_FAILED'))}
                        </Tree>
                      </Col>
                      <Col span={12} className="centerContent">
                        {
                          selectedKeys.length > 0 && itemLog.length > 0 && itemLog.map(item => (
                            <div style={{ overflowY: 'auto' }} key={item}>{item}</div>
                          ))
                        }
                      </Col>
                      <Col span={8} className="rightContent">
                        <div style={{ overflowY: 'auto' }}>{selectedKeys.length > 0 && logResult}</div>
                      </Col>
                    </Row>
                  </div>
                }
              </Spin>
            </TabPane>
          </Tabs>
          }
          {modalVisible && <SeeInfoModal modalData={lineDetail} handleCancel={this.handleCancel}/>}
        </ConfigProvider>
      </div>
    );
  }
}

export default connect((state) => ({
  bizLine: state.initialValueObj.bizLine,
}), intialValueAction)(
  Form.create()(withRouter(Function))
);