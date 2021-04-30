/* eslint-disable */
import React, { Component } from 'react';
import { Tree, Icon, Dropdown, Menu, Input, Empty, message, Modal, Form, Select, Spin } from 'antd';
import ResizePanel from 'react-resize-panel';
import copy from 'copy-to-clipboard';
import Function from './function';
import request from '@/util/request';
import './index.scss';
import Mapping from '../public/mapping';
const { TreeNode } = Tree;
const { Search } = Input;
const { confirm } = Modal;
const { Option } = Select;
const { oneLineLayout } = Mapping;

class Catalog extends Component {
  constructor(props) {
    super(props);
    this.state = {
      gData: [], // 目录数据
      searchValue: '', // 搜索数据
      autoExpandParent: true, // 是否自动展开父节点
      expandedKeys: [], // 当前展开的目录
      dataList: [], // 目录、子目录的所有数据
      id: '', // 当前tree查看流水线的id
      record: {}, // 当前点击或操作tree数据
      showModal: false, // 添加重名米弹框
      operation: '', // 目录操作
      bizLine: [],  // 流水线数据
      treekey: [], // 当前tree所选择的list
      changeCont: 0, // 触发父组件render，更新子组件
    };
    this.lineDetailDict = {}
  }
  componentDidMount() {
    this.getTreeList();
    if (this.props.match.params.id) this.showInfo();
  }
  // 分享导入
  showInfo = () => {
    const that = this;
    confirm({
      title: `确认导入?`,
      content: '将把分享过来的目录导入到我的目录列表内',
      okText: '确认',
      cancelText: '取消',
      icon: <Icon type="info-circle" style={{ color:'#1890ff' }}/>,
      onOk() {
        return new Promise((resolve, reject) => {
          request(`/directory/importShare`, {
            method: 'GET',
            params: {
              linkId: that.props.match.params.id
            }
          }).then(res => {
            if (res.success === true) {
              resolve();
              that.props.history.push(`/catalog`);
            } else {
              reject();
              message.error(res.msg);
            }
          });
        });
      },
      onCancel() { that.props.history.push(`/catalog`) },
    });
  }
  // 获取目录数据
  getTreeList = () => {
    request(`/directory/detail`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        this.setState({ gData: res.data.directories, expandedKeys: res.data.unfoldNodes });
        this.generateList(res.data.directories, '', []);
      }
    });
  }
  // 保存目录
  saveTreeList = data => {
    request(`/directory/save`, {
      method: 'POST',
      body: {
        directories: data,
        unfoldNodes: this.state.expandedKeys
      }
    }).then(res => {
      if (res.success === true) {
        // this.getTreeList();
      } else {
        message.error(res.msg);
      }
    });
  }
  // 获取所有目录、子目录的数据
  generateList = (data, parentId, dataList) => {
    for (let i = 0; i < data.length; i++) {
      const node = data[i];
      const { id } = node;
      dataList.push({ id, name: node.name, type: node.type, parentIds: parentId });
      if (node.children) {
        this.generateList(node.children, id, dataList);
      }
    }
    this.setState({ dataList });
  };
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
        this.setState({ bizLine: res.data });
      }
    });
  }
  // 目录操作
  handleMenuClick = (e, item) => {
    if (e.key !== '删除' && e.key !== '分享') {
      if (e.key.indexOf('流水线') > -1) {
        this.getQueryList();
      }
      this.setState({
        showModal: true,
        record: item,
        operation: e.key
      });
    } else {
      if(e.key === '删除'){
        this.showDeleteConfirm(item);
      } else {
        this.getLinkId(item.id);
      }
    }
  }
  // 获取分享链接
  getLinkId = id => {
    request(`/directory/getLinkId`, {
      method: 'GET',
      params: {
        nodeId: id
      }
    }).then(res => {
      if (res.success === true) {
        copy(`${window.location.href.split('#')[0]}#/catalogshare/${res.data}`);
        message.success('地址复制成功，快去分享吧');
      } else {
        message.error(res.msg);
      }
    });
  }
  // 目录下流水线点击
  treeClick = item => {
    this.setState({ record: item });
    if(item.pipelineId && this.lineDetailDict[item.pipelineId] === undefined) {
      this.getLineDetail(item.pipelineId);
    }
  }
  // 流水线详情
  getLineDetail = id => {
    request(`/pipeline/detail`, {
      method: 'GET',
      params: {
        pipelineId: id
      }
    }).then(res => {
      if (res.success === true) {
        let sideAllSelectData = [];
        res.data.variable.inputVars.map(item => {
          sideAllSelectData.push({
            key: item.name + item.inputType,
            value: item.name,
            name: '运行输入'
          });
        });
        res.data.variable.globalVars.map(item => {
          sideAllSelectData.push({
            key: item.name + item.valueType,
            value: item.name,
            name: '全局变量'
          });
        });
        res.data.flow && res.data.flow.steps.length > 0 && res.data.flow.steps.map(item => {
          item.extractVars.map(ele => {
            sideAllSelectData.push({
              key: item.id,
              value: ele.name,
              name: '提取变量'
            });
          });
        });
        this.lineDetailDict[id] = {
          lineDetail: res.data,
          formData: res.data.variable.inputVars ? res.data.variable.inputVars : [],
          sideAllSelectData
        }
        if (res.data.envSupport) {
          this.getEnvDetail(res.data.envGroupId, id);
        } else {
          this.setState({changeCont: this.state.changeCont + 1});
        }
      } else {
        this.setState({changeCont: this.state.changeCont + 1});
      }
    });
  }
  // 获取环境详情
  getEnvDetail = (id, lineId) => {
    request(`/env/detail`, {
      method: 'GET',
      params: {
        envGroupId: id
      }
    }).then(res => {
      if (res.success === true) {
        let envData = JSON.parse(JSON.stringify(res.data.data[0]));
        delete envData.envVarDesc;
        delete envData.envVarName;
        delete envData.key;
        delete envData.bizLine;
        envData = Object.keys(envData);
        let sideAllSelectData = []
        if(this.lineDetailDict.hasOwnProperty(lineId)) {
          sideAllSelectData = this.lineDetailDict[lineId].sideAllSelectData;
        }
        res.data.data.map(item => {
          sideAllSelectData.push({
            key: item.envVarName,
            value: item.envVarName,
            name: '环境变量'
          });
        });
        this.lineDetailDict[lineId] = {
          ...this.lineDetailDict[lineId],
          envData,
          choiceEnvData: res.data,
          envValue: envData[0],
          sideAllSelectData
        }
      }
      this.setState({changeCont: this.state.changeCont + 1});
    });
  }
  // 改变环境
  envChange = (e, id) => {
    this.lineDetailDict[id].envValue = e;
    this.setState({changeCont: this.state.changeCont + 1});
  }
  // 拖拽目录
  onDrop = info => {
    const { dataList } = this.state;
    const dropKey = info.node.props.eventKey;
    const dragKey = info.dragNode.props.eventKey;
    const dropPos = info.node.props.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
    let type = dataList.find(item => item.id === dropKey).type === 'directory';
    const loop = (data, id, callback) => {
      data.forEach((item, index, arr) => {
        if (item.id === id) {
          return callback(item, index, arr);
        }
        if (item.children) {
          return loop(item.children, id, callback);
        }
      });
    };
    const data = [...this.state.gData];
    if (type || dropPosition !== 0) {
      let dragObj;
      loop(data, dragKey, (item, index, arr) => {
        arr.splice(index, 1);
        dragObj = item;
      });
      if (!info.dropToGap) {
        loop(data, dropKey, item => {
          item.children = item.children || [];
          // where to insert 示例添加到尾部，可以是随意位置
          item.children.push(dragObj);
        });
      } else if (
        (info.node.props.children || []).length > 0 &&
        info.node.props.expanded &&
        dropPosition === 1
      ) {
        loop(data, dropKey, item => {
          item.children = item.children || [];
          // where to insert 示例添加到头部，可以是随意位置
          item.children.unshift(dragObj);
        });
      } else {
        let ar;
        let i;
        loop(data, dropKey, (item, index, arr) => {
          ar = arr;
          i = index;
        });
        if (dropPosition === -1) {
          ar.splice(i, 0, dragObj);
        } else {
          ar.splice(i + 1, 0, dragObj);
        }
      }
      this.setState({ gData: data });
      this.generateList(data, '', []);
      this.saveTreeList(data);
    } else {
      message.info('不允许拖拽到流水线中');
    }
  };
  // 符合当前搜索的目录key
  getParentKey = (id, tree) => {
    let parentKey;
    for (let i = 0; i < tree.length; i++) {
      const node = tree[i];
      if (node.children) {
        if (node.children.some(item => item.id === id)) {
          parentKey = node.id;
        } else if (this.getParentKey(id, node.children)) {
          parentKey = this.getParentKey(id, node.children);
        }
      }
    }
    return parentKey;
  };
  // 展开关闭目录
  onExpand = expandedKeys => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    }, () => this.saveTreeList(this.state.gData));
  };
  // 搜索发生更改
  onChange = e => {
    const { value } = e.target;
    const { dataList, gData } = this.state;
    const expandedKeys = dataList.map(item => {
      if (item.name.indexOf(value) > -1) {
        return this.getParentKey(item.id, gData);
      }
      return null;
    })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    this.setState({
      expandedKeys: value ? expandedKeys : [],
      searchValue: value,
      autoExpandParent: value ? true : false,
    });
  };
  // 删除
  showDeleteConfirm = item => {
    const { gData, dataList } = this.state;
    const that = this;
    confirm({
      title: `确认删除${item.name}?`,
      content: '如有子级存在，也会一同删除哦',
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        const deleteTree = (data, menu) => {
          if (menu.parentIds) {
            data.map(ele => {
              if (ele.id === menu.parentIds) {
                return ele.children.splice(ele.children.findIndex(tree => tree.id === item.id), 1);
              }
              if (ele.children) {
                return deleteTree(ele.children, menu);
              }
            });
          } else {
            data.splice(data.findIndex(ele => ele.id === item.id), 1);
          }
          return data;
        };
        return new Promise((resolve, reject) => {
          let menu = dataList.filter(ele => ele.id === item.id)[0];
          deleteTree(gData, menu);
          that.setState({ gData });
          that.generateList(gData, '', []);
          resolve();
          request(`/directory/save`, {
            method: 'POST',
            body: {
              directories: gData,
              unfoldNodes: that.state.expandedKeys
            }
          }).then(res => {
            if (res.success === true) {
              // that.getTreeList();
            } else {
              reject();
              message.error(res.msg);
            }
          });
        });
      },
      onCancel() { console.log('Cancel') },
    });
  }
  // 确认添加更改
  handleOk = () => {
    const { gData, operation, record, dataList, bizLine } = this.state;
    this.props.form.validateFields((error, value) => {
      if (error) return;
      // 循环查找并插入
      const insertTree = (id, data, params) => {
        if (id) {
          data.map((item) => {
            if (item.id === id) {
              return item.children.push({ ...params });
            }
            if (item.children) {
              return insertTree(id, item.children, params);
            }
          });
        } else {
          data.push({ ...params });
        }
        return data;
      };
      // 循环查找并更改
      const updataTree = (id, data) => {
        if (id) {
          data.forEach((item) => {
            if (item.id === id) {
              item.name = value.name;
            }
            if (item.children) {
              updataTree(id, item.children);
            }
          });
        }
        return data;
      };
      if (operation === '重命名') {
        updataTree(record.id, gData);
      } else {
        let menu = dataList.filter(ele => ele.id === record.id)[0];
        let params = {
          id: new Date().getTime().toString(),
          type: operation.indexOf('目录') > -1 ? 'directory' : 'pipeline',
          parentId: operation.indexOf('同级') > -1 ? menu.parentIds : menu.id,
        };
        if (operation.indexOf('目录') > -1) {
          params.name = value.name;
          params.children = [];
        } else {
          params.pipelineId = value.pipelineId;
          params.name = bizLine.find(line => line.id === value.pipelineId).name;
        }
        insertTree(operation.indexOf('同级') > -1 ? menu.parentIds : menu.id, gData, params);
      }
      this.setState({ gData });
      this.generateList(gData, '', []);
      this.handleCancel();
      this.saveTreeList(gData);
    });
  }
  // 取消
  handleCancel = () => {
    this.props.form.resetFields();
    this.setState({
      showModal: false,
      record: {},
      operation: ''
    });
  }
  // 收藏
  favoritesChange = data => {
    let lineDetail = this.lineDetailDict[this.state.record.pipelineId].lineDetail;
    lineDetail.collect = data.collect;
    this.lineDetailDict[this.state.record.pipelineId].lineDetail = lineDetail;
  }

  render() {
    const { searchValue, gData, expandedKeys, autoExpandParent, record, showModal, operation, bizLine, treekey } = this.state;
    const { getFieldDecorator } = this.props.form;
    const treeTitle = item => {
      const index = item.name.indexOf(searchValue);
      const beforeStr = item.name.substr(0, index);
      const afterStr = item.name.substr(index + searchValue.length);
      const title = index > -1 ? (
        <span>
          {item.children && <Icon type="folder" className="folder" />}
          {beforeStr}
          <span style={{ color: '#f50' }}>{searchValue}</span>
          {afterStr}
        </span>
      ) : (
        <span>{item.children && <Icon type="folder" className="folder" />}<span>{item.name}</span></span>
      );
      return <div className="treeTitle">
        <div style={{ width: '100%' }} onClick={() => this.treeClick(item)}>{title}</div>
        <Dropdown
          placement="bottomLeft"
          trigger={['click']}
          overlay={
            <Menu onClick={e => this.handleMenuClick(e, item)}>
              {item.children && <Menu.Item key="添加目录">添加目录</Menu.Item>}
              {item.children && <Menu.Item key="添加流水线">添加流水线</Menu.Item>}
              <Menu.Item key="添加同级目录">添加同级目录</Menu.Item>
              <Menu.Item key="添加同级流水线">添加同级流水线</Menu.Item>
              {item.children && <Menu.Item key="重命名">重命名</Menu.Item>}
              {!(gData.length <= 1 && item.id === gData[0].id) && <Menu.Item key="删除">删除</Menu.Item>}
              <Menu.Item key="分享">分享</Menu.Item>
            </Menu>
          }
        >
          <Icon type="unordered-list" className="more" />
        </Dropdown>
      </div>;
    };
    const tree = data => data.map(item => {
      if (item.children && item.children.length > 0) {
        return (
          <TreeNode key={item.id} title={treeTitle(item)}>
            {tree(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode key={item.id} title={treeTitle(item)} />;
    });
    let currLineDetail = this.lineDetailDict[record.pipelineId];
    return (
      <React.Fragment>
        <div className="body">
          <div className="content">
            <Search style={{ margin: '8px 0' }} placeholder="搜索" onChange={this.onChange} />
            <Tree
              draggable
              blockNode
              onDrop={this.onDrop}
              onExpand={this.onExpand}
              expandedKeys={expandedKeys}
              autoExpandParent={autoExpandParent}
              className="tree"
              onSelect={(selectedKeys) => this.setState({ treekey: selectedKeys })}
            >
              {tree(gData)}
            </Tree>
          </div>
          <ResizePanel direction="w" style={{ width: '400px' }} handleClass="customHandle" borderClass="customResizeBorder">
            <div className="sidebar">
              {
                record && record.pipelineId && treekey.length > 0 && this.lineDetailDict[record.pipelineId] ?
                  <Function
                    id={record.pipelineId}
                    lineDetail={currLineDetail}
                    currEnv={currLineDetail.envValue }
                    envChange={this.envChange}
                    favoritesChange={this.favoritesChange}
                  /> :
                  <div className="empty">
                    <Spin spinning={(record.pipelineId || this.lineDetailDict[record.pipelineId] !== undefined) && treekey.length > 0}>
                      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={'暂无数据'} />
                    </Spin>
                  </div>
              }
            </div>
          </ResizePanel>
        </div>
        {showModal &&
          <Modal
            title={operation}
            visible={showModal}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
            okText="确认"
            cancelText="取消"
            width={600}
          >
            {
              (operation.indexOf('目录') > -1 || operation === '重命名') ? <Form.Item label="目录名称" {...oneLineLayout}>
                {getFieldDecorator('name', {
                  rules: [{ required: true, message: `请输入更改名称!` }],
                  initialValue: operation === '重命名' ? record.name : ''
                })(
                  <Input placeholder="请输入目录名称" />
                )}
              </Form.Item> :
              <Form.Item label="流水线" {...oneLineLayout} >
                {getFieldDecorator('pipelineId', {
                  rules: [{ required: true, message: `请选择流水线!` }],
                  initialValue: undefined
                })(
                  <Select
                    style={{ width: '100%' }}
                    placeholder="请选择流水线"
                    showSearch
                    allowClear
                    optionFilterProp="children"
                    filterOption={(input, option) =>
                      option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                  >
                    {
                      bizLine.length > 0 && bizLine.map(item => (
                        <Option key={item.id} value={item.id}>{item.name}</Option>
                      ))
                    }
                  </Select>
                )}
              </Form.Item>
            }
          </Modal>
        }
      </React.Fragment>
    );
  }
}
export default Form.create()(Catalog);