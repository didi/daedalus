/* eslint-disable */
/** 环境管理 */
import React, { Component, Fragment  } from 'react';
import { 
  Card, 
  Table, 
  Pagination, 
  ConfigProvider,
  Divider,
  Button, 
  Row, 
  Col, 
  Form, 
  Input, 
  Modal, 
  Popconfirm, 
  Icon, 
  Tooltip, 
  message, 
  Select
} from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import request from '@/util/request';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import Mapping from '../public/mapping';
import './index.scss';
const { envOrExampleLayout } = Mapping;
const { Option } = Select;

class EnvManage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [], // 环境组table数据
      total: 0,
      pageSize: 10,
      pageIndex: 1,
      showModalVisible: false,
      title: '',  // 弹框title
      record: {}, // 当前操作行
      searchValue: '',  // 查询input数据
      modalTableData: [], // 弹框table数据
      editingKey: '', // 当前编辑的key
      tablegroupList: [], // 弹框环境列表（动态）
      addEnv: false,  //  //添加环境
      addEnvValue: '',  //  添加环境名称
      addVisible: false, // 是否是新增变量
      cluster: 'OFFLINE', // 环境组新增编辑环境的环境类型（线上、线下）
      editTableHeader: false, // 是否编辑环境的相信信息
      editTableHeaderItem: {}, // 当前要编辑的环境的相信信息
    };
  }
  componentDidMount() {
    this.getMyTable();
  }
  // 获取table数据
  getMyTable = value => {
    request(`/env/list`, {
      method: 'GET',
      params: {
        name: this.state.searchValue,
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize,
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({
          list: res.data,
          total: res.total,
          loading: false
        });
      } else {
        this.setState({ loading: false });
        message.error(res.msg);
      }
    });
  }
  // table 显示
  getColumn = () => {
    let column = [
      {
        dataIndex: 'name',
        title: '环境组名称',
      },
      {
        dataIndex: 'bizLine',
        title: '业务线',
        render: t => {
          const bizLines = this.props.bizLine || [];
          return <div>{(t || t === 0) && bizLines.length > 0 && bizLines.find(x => x.code === t).name}</div>;
        }
      },
      {
        dataIndex: 'creatorCN',
        title: '创建人'
      },
      {
        dataIndex: 'createTime',
        title: '添加时间',
      },
      {
        dataIndex: 'id',
        title: '操作',
        render: (t, record) => {
          return (
            <div>
              <Popconfirm
                title="确认删除?"
                onConfirm={() => this.delete(record)}
                okText="确认"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm>
              <Divider type="vertical" />
              <a onClick={() => { this.showModal('编辑环境组', record) }}>修改</a>
            </div>
          );
        }
      }
    ];
    return column;
  }
  // 编辑table
  getEditTable = record => {
    const modalTableData = [];
    record.data && record.data.forEach((item, i) => {
      modalTableData.push({
        key: i.toString(),
        ...item
      });
    });
    const tablegroupList = [];
    if (record.data && record.data.length > 0) {
      let a = { ...record.data[0] };
      delete a.envVarName;
      delete a.envVarDesc;
      delete a.key;
      delete a.bizLine;
      Object.keys(a).forEach((it, i) => {
        tablegroupList.push({
          key: i.toString(),
          title: it,
          dataIndex: it,
          cluster: record.clusterInfo[it]
        });
      });
    }
    this.setState({ modalTableData, tablegroupList });
  }
  // 搜索框发生变化
  searchInput = e => {
    this.setState({ searchValue: e.target.value });
  }
  // 查询
  search = () => {
    this.getMyTable();
  }
  // 重置
  reset = () => {
    this.setState({ searchValue: '' });
  }
  // 分页器发生变化
  onChangePagination = (current, pageSize) => {
    this.setState({ pageIndex: current, pageSize }, () => {
      this.getMyTable();
    });
  }
  // pageSize 变化的回调
  onShowSizeChange = (current, pageSize) => {
    this.setState({ pageIndex: current, pageSize }, () => {
      this.getMyTable();
    });
  }

  // 新建编辑实例
  showModal = (title, record) => {
    this.setState({
      title,
      showModalVisible: true,
      record: record ? record : {},
    });
    if (title === '编辑环境组') this.getEditTable(record);
  }
  // 弹框确认
  handleModalOk = () => {
    this.props.form.validateFields((error, value) => {
      if (!error) {
        this.setState({ btnLoading: true });
        let clusterInfo = {};
        this.state.tablegroupList.length > 0 && this.state.tablegroupList.map(item => {
          clusterInfo[item.dataIndex] = item.cluster;
          return item;
        });
        request(`/env/save`, {
          method: 'POST',
          body: {
            name: value.name,
            bizLine: value.bizLine,
            data: this.state.modalTableData,
            id: this.state.title === '编辑环境组' ? this.state.record.id : null,
            clusterInfo,
          }
        }).then(res => {
          if (res.success === true) {
            if (this.state.title === '编辑环境组') {
              message.success('修改成功');
            } else {
              message.success('添加成功');
            }
            this.setState({ btnLoading: false });
            this.getMyTable();
            this.handleModalCancel();
          } else {
            this.setState({ btnLoading: false });
            message.error(res.msg);
          }
        });
      }
    });
  }
  // 弹框取消
  handleModalCancel = () => {
    this.setState({
      showModalVisible: false,
      modalTableData: [],
      editingKey: '',
      tablegroupList: [],
      addEnv: false,
      addEnvValue: '',
      addVisible: false,
      loading: false,
      btnLoading: false,
      cluster: 'OFFLINE',
      editTableHeader: false,
      editTableHeaderItem: {},
    });
    this.props.form.resetFields();
  }
  // 删除环境组
  delete = record => {
    request(`/env/deleteEnv?envGroupId=${record.id}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        message.success('删除成功');
        this.getMyTable();
      } else {
        message.error(res.msg);
      }
    });
  }
  //  ----------- 环境组弹框
  // 判断要修改的是否为当前点击
  isEditing = record => record.key === this.state.editingKey;
  // 取消修改
  cancel = (key) => {
    this.setState({ editingKey: '' });
    if (this.state.addVisible) {
      const { modalTableData } = this.state;
      modalTableData.forEach((item, i) => {
        if (item.key === key) {
          modalTableData.splice(i, 1);
        }
      });
      this.setState({ modalTableData, addVisible: false });
    }
  };
  // 确认修改
  save(form, key) {
    form.validateFields((error, row) => {
      if (error) {
        return;
      }
      delete row.name;
      const newData = [...this.state.modalTableData];
      const index = newData.findIndex(item => key === item.key);
      if (index > -1) {
        const item = newData[index];
        newData.splice(index, 1, {
          key: item.key,
          ...row,
        });
        this.setState({ modalTableData: newData, editingKey: '', addVisible: false });
      } else {
        newData.push(row);
        this.setState({ modalTableData: newData, editingKey: '' });
      }
    });
  }
  // 添加环境
  addEvent = () => {
    this.setState({ addEnv: true });
  }
  // 环境input
  addChange = e => {
    this.setState({ addEnvValue: e.target.value });
  }
  // 环境input中环境类型选择
  inputSelect = e => {
    this.setState({ cluster: e });
  }
  // 新增编辑环境名
  onAddEnvName = () => {
    const { tablegroupList, modalTableData, cluster, addEnvValue, editTableHeader, editTableHeaderItem } = this.state;
    if (addEnvValue) {
      if (!isNaN(addEnvValue)) {
        message.error('错误输入类型');
        return;
      }
      if (!(editTableHeaderItem.title === addEnvValue) && tablegroupList.some(item => addEnvValue === item.title)) {
        message.error('已存在相同名称的环境');
      } else {
      // 编辑环境
        if (editTableHeader) {
          let index = tablegroupList.findIndex(it => it.dataIndex === editTableHeaderItem.dataIndex);
          tablegroupList.splice(index, 1, {
            key: tablegroupList.length.toString(),
            title: addEnvValue,
            dataIndex: addEnvValue,
            cluster,
          });
          const arr = modalTableData.map(item => {
            let tempItem = {};
            for (let key in item) {
              if (key === editTableHeaderItem.title) {
                tempItem[tablegroupList.find(x => x.title === addEnvValue).dataIndex] = item[Object.keys(item).find(it => it === editTableHeaderItem.title)];
              } else {
                tempItem[key] = item[key];
              }
            }
            return tempItem;
          });
          this.setState({
            tablegroupList,
            modalTableData: arr,
          });
        } else {
        // 新增环境
          tablegroupList.push({
            key: tablegroupList.length.toString(),
            title: addEnvValue,
            dataIndex: addEnvValue,
            cluster,
          });
          modalTableData.forEach(item => {
            item[tablegroupList.find(x => x.title === addEnvValue).dataIndex] = '';
          });
          this.setState({
            tablegroupList,
            modalTableData,
          });
        }
        this.addClose();
      }
    } else {
      this.addClose();
    }
  }
  // 更改table中环境信息
  editTableEnvInfo = item => {
    this.setState({
      editTableHeader: true,
      editTableHeaderItem: item,
      addEnvValue: item.title
    });
  }
  // 关闭添加环境
  addClose = () => {
    this.setState({
      addEnv: false,
      addEnvValue: '',
      cluster: 'OFFLINE',
      editTableHeader: false,
      editTableHeaderItem: {},
    });
  }
  // table 显示
  getModalColumn = () => {
    let column = [
      {
        title: '变量名',
        dataIndex: 'envVarName',
        render: (t, record) => (this.editInput(t, record, '变量名', 'envVarName'))
      },
      {
        title: '变量解释',
        dataIndex: 'envVarDesc',
        render: (t, record) => (this.editInput(t, record, '变量解释', 'envVarDesc'))
      },
    ];

    this.state.tablegroupList.forEach(item => {
      const dataIndex = item.dataIndex;
      column.push({
        title: () => {
          return this.state.editTableHeader && dataIndex === this.state.editTableHeaderItem.dataIndex ? <Input
            defaultValue={item.title}
            onChange={this.addChange}
            placeholder="环境名"
            value={this.state.addEnvValue}
            addonBefore={
              <Select
                defaultValue={item.cluster}
                style={{ width: 70 }}
                onChange={this.inputSelect}
                className="inputSelectClass"
              >
                <Option value="ONLINE">线上</Option>
                <Option value="OFFLINE">线下</Option>
              </Select>
            }
            addonAfter={
              <React.Fragment>
                <a onClick={this.onAddEnvName}>保存</a>
                <Divider type="vertical" />
                <a onClick={this.addClose}>关闭</a>
                <Divider type="vertical" />
                <Popconfirm
                  title="确认删除该环境么?"
                  onConfirm={() => this.deleteTableColumn(dataIndex)}
                >
                  <a>删除</a>
                </Popconfirm>
              </React.Fragment>
            }
          /> : <Fragment>
            <span>{item.title}</span>
            <span>({item.cluster === 'ONLINE' ? '线上' : '线下'})</span>
            <a
              disabled={this.state.addEnv || this.state.editingKey !== '' || (this.state.editTableHeader && dataIndex !== this.state.editTableHeaderItem.dataIndex)}
              style={{ marginLeft: 8, cursor: 'pointer' }}
              onClick={() => this.editTableEnvInfo(item)}
            >
              <Icon type="form" />
            </a>
          </Fragment>;
        },
        dataIndex: dataIndex,
        render: (t, record) => (this.editInput(t, record, item.title, dataIndex))
      });
    });

    let column2 = [{
      title: () => {
        return this.state.addEnv ? <Input
          onChange={this.addChange}
          placeholder="环境名"
          value={this.state.addEnvValue}
          addonBefore={
            <Select
              defaultValue="OFFLINE"
              style={{ width: 70 }}
              onChange={this.inputSelect}
              className="inputSelectClass"
            >
              <Option value="ONLINE">线上</Option>
              <Option value="OFFLINE">线下</Option>
            </Select>
          }
          addonAfter={
            <React.Fragment>
              <a onClick={this.onAddEnvName}>保存</a>
              <Divider type="vertical" />
              <a onClick={this.addClose}>关闭</a>
            </React.Fragment>
          }
        /> : <a
          disabled={this.state.editTableHeader || this.state.editingKey !== ''}
          style={{ marginLeft: 8, cursor: 'pointer' }}
          onClick={() => this.addEvent()}
        >
          <Tooltip placement="top" title="添加环境">
            <Icon type="plus-circle" theme="filled" />
          </Tooltip>
        </a>;
      },
      dataIndex: 'key',
      width: this.state.addEnv ? 350 : 10,
      fixed: 'right',
      render: t => (
        <div></div>
      )
    },
    {
      title: '操作',
      dataIndex: 'operation',
      width: 120,
      fixed: 'right',
      render: (text, record) => {
        const { editingKey, addEnv, editTableHeader } = this.state;
        const editable = this.isEditing(record);
        return editable ? (
          <span>
            <a
              onClick={() => this.save(this.props.form, record.key)}
              style={{ marginRight: 8 }}
            >
              保存
            </a>
            <Popconfirm title="是否取消?" onConfirm={() => this.cancel(record.key)}>
              <a>取消</a>
            </Popconfirm>
          </span>
        ) : (
          <React.Fragment>
            <a disabled={editingKey !== '' || editTableHeader || addEnv} onClick={() => this.edit(record.key)}>修改</a>
            <Divider type="vertical" />
            <Popconfirm
              title="确认删除?"
              onConfirm={() => this.deleteTableRow(record.key)}
              okText="确认"
              cancelText="取消"
            >
              <a disabled={editingKey !== '' || editTableHeader || addEnv}>删除</a>
            </Popconfirm>

          </React.Fragment>
        );
      }
    }];
    return [...column, ...column2];
  }
  // 当前要编辑项
  edit(key) {
    this.setState({ editingKey: key });
  }
  // 编辑的输入框
  editInput = (t, record, title, dataIndex) => {
    const { getFieldDecorator } = this.props.form;
    return this.isEditing(record) ? <Form.Item style={{ margin: 0 }}>
      {getFieldDecorator(dataIndex, {
        rules: title === '变量名' ? [{
          required: true,
          validator(rule, value, callback) {
            let regexp = new RegExp(/^[a-zA-Z\\_][0-9a-zA-Z\\_]*$/);
            if (value) {
              if (regexp.test(value)) {
                callback();// 必须写
              } else {
                callback('请输入正确格式！');
              }
            } else {
              callback('请输入...');
            }
          }
        }] : [{
          required: true,
          message: `请填写${title}!`,
        }],
        initialValue: t,
      })(
        <Input />
      )}
    </Form.Item> : <div>{t}</div>;
  }
  // 删除表格列
  deleteTableColumn = dataIndex => {
    const { tablegroupList, modalTableData } = this.state;
    tablegroupList.forEach((item, i) => {
      if (item.dataIndex === dataIndex) {
        tablegroupList.splice(i, 1);
      }
    });
    modalTableData.forEach(item => {
      delete item[dataIndex];
    });
    this.setState({ tablegroupList, modalTableData });
    this.addClose();
  }
  // 删除弹框行
  deleteTableRow = key => {
    const { modalTableData } = this.state;
    modalTableData.forEach((item, i) => {
      if (item.key === key) {
        modalTableData.splice(i, 1);
      }
    });
    this.setState({ modalTableData });
  }
  // 新增变量
  addVariable = () => {
    const { modalTableData, tablegroupList } = this.state;
    const key = new Date().getTime().toString();
    let record = {
      key: key,
      envVarName: '',
      envVarDesc: '',
      cluster: 'OFFLINE'
    };
    tablegroupList.forEach(item => {
      record[item.dataIndex] = '';
    });

    modalTableData.unshift({ ...record });
    this.setState({ addVisible: true, modalTableData, editingKey: key }, () => {
      this.isEditing(record);
    });
  }

  render() {
    const {
      list, pageIndex, total, pageSize, showModalVisible, title, record, searchValue, loading, btnLoading, modalTableData,
      editingKey, addEnv, editTableHeader,
    } = this.state;
    const { getFieldDecorator } = this.props.form;
    const bizLines = this.props.bizLine || [];

    return (
      <ConfigProvider locale={zhCN}>
        <div className="moretable envManage">
          <Card style={{ marginBottom: 20 }}>
            <h2>环境管理 </h2>
            <Row>
              <Col span={4}>

              </Col>
              <Col span={10}>
                <Col span={6} style={{ textAlign: 'right', lineHeight: '32px', marginRight: 5 }}>名称:</Col>
                <Col span={16}>
                  <Input
                    placeholder="请输入..."
                    value={searchValue}
                    onChange={this.searchInput}
                  />
                </Col>
              </Col>
              <Col span={4}>
                <Button type="primary" style={{ marginRight: 20 }} onClick={this.search}>查询</Button>
                <Button onClick={this.reset}>重置</Button>
              </Col>
            </Row>
          </Card>
          <Card>
            <div style={{ textAlign: 'right' }}>
              <Button type="primary" icon="plus" style={{ marginBottom: 10 }} onClick={() => this.showModal('添加环境组')}>新建</Button>
            </div>
            <Table
              rowKey="id"
              loading={loading}
              dataSource={list}
              columns={this.getColumn()}
              pagination={false}
              footer={(currentData) => (
                <div style={{ display: total === 0 ? 'none' : 'block', }}>
                  <span style={{ lineHeight: '32px', marginRight: 10 }}>共计：{total} 条数据</span>
                  <span>第{pageIndex} / {Math.ceil(total / pageSize)}页</span>
                  <div style={{ float: 'right', margin: '0px 0' }}>
                    <Pagination
                      onChange={this.onChangePagination}
                      current={pageIndex}
                      total={total}
                      pageSize={pageSize}
                      pageSizeOptions={['10', '20', '50']}
                      showSizeChanger
                      showQuickJumper
                      onShowSizeChange={this.onShowSizeChange}
                    />
                  </div>
                </div>
              )}
            />
          </Card>
        </div>
        <Modal
          title={title}
          visible={showModalVisible}
          onCancel={this.handleModalCancel}
          width={1200}
          footer={
            <div>
              <Button onClick={() => this.handleModalCancel()}>取消</Button>
              <Button
                onClick={() => this.handleModalOk()}
                type="primary"
                disabled={editingKey !== '' || addEnv || editTableHeader}
                loading={btnLoading}
              >
                确认
              </Button>
            </div>
          }
        >
          <Row>
            <Col span={20}>
              <Form.Item label="环境组名" {...envOrExampleLayout} >
                {getFieldDecorator('name', {
                  rules: [{ required: true, message: `请填写环境组名!` }],
                  initialValue: title === '添加环境组' ? '' : record.name
                })(
                  <Input placeholder="请输入环境组名称" />
                )}
              </Form.Item>
            </Col>
            <Col span={4} style={{ marginTop: 4 }}>
              <Button type="primary" onClick={this.addVariable} disabled={editingKey !== '' || addEnv || editTableHeader}>新增变量</Button>
            </Col>
          </Row>
          <Row>
            <Col span={20}>
              <Form.Item label="业务线" {...envOrExampleLayout} >
                {getFieldDecorator('bizLine', {
                  rules: [{ required: true, message: `请选择业务线!` }],
                  initialValue: title === '添加环境组' ? undefined : record.bizLine
                })(
                  <Select
                    style={{ width: '100%' }}
                    placeholder="请选择业务线"
                  >
                    {
                      bizLines && bizLines.length > 0 && bizLines.map(item => (
                        <Option key={item.code} value={item.code}>{item.name}</Option>
                      ))
                    }
                  </Select>
                )}
              </Form.Item>
            </Col>
          </Row>
          <Table
            dataSource={modalTableData}
            columns={this.getModalColumn()}
            rowClassName="editable-row"
            rowKey="key"
            pagination={{
              onChange: this.cancel,
            }}
            scroll={{ x: true }}
          />
        </Modal>
      </ConfigProvider>
    );
  }
}
export default connect((state) => ({
  bizLine: state.initialValueObj.bizLine,
}), intialValueAction)(
  Form.create()(EnvManage)
);