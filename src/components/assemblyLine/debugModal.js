import React, { Component, Fragment } from 'react';
import { 
  Form,
  Modal,
  Select,
  Input,
  InputNumber,
  Radio,
  Checkbox,
  Empty,
  Spin,
  Row,
  Col,
  Tree,
  Tabs,
  Button,
  Switch,
  Icon,
  message
} from 'antd';
import QRCode from 'qrcode.react';
import FormatPicker from '../public/formatPicker';
import request from '@/util/request';
import './index.scss';
const { TextArea } = Input;
const { Option } = Select;
const { TabPane } = Tabs;
const { TreeNode } = Tree;
const oneItemLayout = {
  labelCol: { xs: { span: 5 }},
  wrapperCol: { xs: { span: 18 }}
};

class debugMoadl extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showTabActiveKey: '1',
      outputType: 'text',
      selectedKeys: [], // 日志tree列表当前所点击
      outputData: '',
      logloading: false,
      switchChecked: true, // 步骤 是:筛选 否:全部
      logList: [], // 提交以后获取的日志数据
      itemLog: [],  // 运行日志当前点击步骤日志
      logResult: '',  // 运行日志当前点击步骤结果
      submitLoading: false, // 提交loading
    };
  }
  componentWillReceiveProps(nextProps) {
    if (this.props.visible !== nextProps.visible) {
      this.setState({
        showTabActiveKey: '1',
        outputType: 'text',
        selectedKeys: [], // 日志tree列表当前所点击
        outputData: '',
        logloading: false,
        switchChecked: true, // 步骤 是:筛选 否:全部
        logList: [], // 提交以后获取的日志数据
        itemLog: [],  // 运行日志当前点击步骤日志
        logResult: '',  // 运行日志当前点击步骤结果
        submitLoading: false, // 提交loading
      });
      this.props.form.resetFields();
    }
  }
  // tab发生变化（输出、日志）
  showTabOnChange = key => {
    this.setState({ showTabActiveKey: key, switchChecked: true });
    if (key !== '1') this.setState({ outputType: 'text' });
    if (key === '2') this.getJournalData();
    if (key !== '2') this.setState({ selectedKeys: [] });
  }
  // 输出类型
  outputTypeChange = e => {
    this.setState({ outputType: e.target.value });
  }
  // 树形图
  tree = data => {
    return data.map(item => {
      let color = '';
      if (item.stepStatus === 'RUN_SUCCESS') color = '#08d608';
      if (item.stepStatus === 'RUN_FAILED') color = '#fb5d5d';
      // const index = item.title.indexOf(searchValue);
      // const beforeStr = item.title.substr(0, index);
      // const afterStr = item.title.substr(index + searchValue.length);
      // const title =
      //     index > -1 ? (
      //       <span>
      //         {beforeStr}
      //         <span style={{ color: '#f50' }}>{searchValue}</span>
      //         {afterStr}
      //       </span>
      //     ) : (
      //       <span>{item.title}</span>
      //     );
      // if (item.children.length > 0) {
      //   return (
      //     <TreeNode key={item.id} title={title}>
      //       {this.tree(item.children)}
      //     </TreeNode>
      //   );
      // }
      return (
        <TreeNode
          key={item.stepId}
          title={
            <span style={{ color: color }}>{item.stepName}</span>
          }
          switcherIcon={this.icon(item.stepStatus, color)}
        />
      );
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
        return item;
      });
    }
  }
  // 判断可不可以转换成json
  isJson = text => {
    try {
      JSON.parse(text);
    } catch (e) {
      return false;
    }
    return true;
  }
  // 步骤筛选
  switchChange = checked => {
    this.setState({ switchChecked: checked });
  }
  // 重置
  reset = () => {
    this.setState({
      showTabActiveKey: '1',
      logId: '',
    });
    this.props.form.resetFields();
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
  // 提交
  submit = () => {
    this.props.form.validateFields((error, value) => {
      if (error) return;
      const allData = { ...this.props.info };
      allData.variable = {
        inputVars: this.props.sideData[0].groupList,
        globalVars: this.props.sideData[1].groupList
      };
      allData.flow = JSON.parse(sessionStorage.getItem('nodeData'));
      if (allData.flow) {
        allData.flow.steps = allData.flow.nodes;
        delete allData.flow.nodes;
        allData.flow.steps.forEach(item => this.deleteAttribute(item));
        allData.flow.edges.forEach(item => this.deleteAttribute(item));
      }
      this.setState({ submitLoading: true });
      request(`/dispatch/debug`, {
        method: 'POST',
        body: {
          ...value,
          pipeline: { ...allData }
        }
      }).then(res => {
        this.setState({
          submitLoading: false,
          logId: res.data ? res.data.logId : '',
        });
        if (res.success === true) {
          this.setState({ outputData: res.data.result });
        } else {
          message.error(res.msg);
          if (res.data && res.data.logId) {
            this.setState({ showTabActiveKey: '2' });
          }
        }
      });
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

  render() {
    const { inputVars, envData, info, visible } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { showTabActiveKey, outputType, selectedKeys, outputData, logloading, switchChecked, logList, itemLog, logResult, submitLoading } = this.state;
    let envList = envData.clusterInfo ? Object.keys(envData.clusterInfo) : {};
    return (
      <Modal
        title="调试"
        visible={visible}
        width="1000px"
        footer={null}
        onCancel={this.props.closeDebugModal}
        className="debug"
      >
        {
          info.envSupport && <Form.Item label="环境" {...oneItemLayout}>
            {getFieldDecorator('env', {
              rules: [{ required: true, message: '请选择！' }],
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="选择环境"
              >
                {
                  envList.length > 0 && envList.map(item => (
                    <Option key={item} value={item}>{item}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        }
        {
          inputVars && inputVars.length > 0 && inputVars.map((it, i) => {
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
              <Form.Item label={it.label} {...oneItemLayout}>
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: it.defaultValue
                })(
                  <Input placeholder="请输入..." />
                )}
              </Form.Item>
              }
              {it.inputType === 'NUMBER_INPUT' && show &&
              <Form.Item label={it.label} {...oneItemLayout}>
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: it.defaultValue
                })(
                  <InputNumber placeholder="请输入..." style={{ width: '100%' }} />
                )}
              </Form.Item>
              }
              {it.inputType === 'TEXTAREA' && show &&
              <Form.Item label={it.label} {...oneItemLayout}>
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: it.defaultValue
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
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请选择！' }],
                  initialValue: it.defaultValue
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
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请选择！' }],
                  initialValue: (it.options.length > 0 && it.options[0].value) ? it.options[0].value : undefined
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
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请选择！' }],
                  initialValue: undefined
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
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: undefined
                })(
                  <FormatPicker type="DATE_PICKER" />
                )}
              </Form.Item>
              }
              {it.inputType === 'TIME_PICKER' && show &&
              <Form.Item label={it.label} {...oneItemLayout}>
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: undefined
                })(
                  <FormatPicker type="TIME_PICKER" />
                )}
              </Form.Item>
              }
              {it.inputType === 'DATE_TIME_PICKER' && show &&
              <Form.Item label={it.label} {...oneItemLayout}>
                {getFieldDecorator(`inputs.${it.name}`, {
                  rules: [{ required: it.required, message: '请输入！' }],
                  initialValue: undefined
                })(
                  <FormatPicker type="DATE_TIME_PICKER" />
                )}
              </Form.Item>
              }
            </Fragment>;
          })
        }
        <div style={{ textAlign: 'center' }}>
          {
            inputVars.length > 0 ? <Fragment>
              <Button type="primary" style={{ marginRight: '30px' }} onClick={this.submit} loading={submitLoading}>提交</Button>
              <Button onClick={this.reset}>重置</Button>
            </Fragment> : <Button type="primary" onClick={this.submit} loading={submitLoading}>运行</Button>
          }
        </div>
        <Tabs
          onChange={this.showTabOnChange}
          activeKey={showTabActiveKey}
          type="card"
          style={{ marginTop: 20 }}
        >
          <TabPane tab="输出" key="1">
            <div style={{ textAlign: 'right', marginBottom: 20, }}>
              <Radio.Group
                value={outputType}
                onChange={this.outputTypeChange}
              >
                <Radio value="text">Text</Radio>
                <Radio value="json">JSON</Radio>
                <Radio value="二维码">二维码</Radio>
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
            (outputData ? <div style={{ textAlign: 'center' }}>
              <QRCode value={outputData || '暂无数据'} size={200} style={{ textAlign: 'center' }} />
            </div> :
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)
            }
          </TabPane>
          <TabPane tab="日志" key="2">
            <Spin spinning={logloading}>
              {
                logList.length === 0 ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} /> : <div className="journal">
                  <Row>
                    <Col span={4} className="leftTitle spaceBetween">
                      <span>步骤</span>
                      <Switch checkedChildren="筛选" unCheckedChildren="全部" checked={switchChecked} onChange={this.switchChange} />
                    </Col>
                    <Col span={12} className="centerTitle">日志</Col>
                    <Col span={8} className="rightTitle">结果</Col>
                  </Row>
                  <Row type="flex">
                    <Col span={4} className="leftContent">
                      <Tree
                        switcherIcon={<Icon type="down" />}
                        onSelect={this.onSelect}
                        className="tree"
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
      </Modal>
    );
  }
}

export default Form.create()(debugMoadl);