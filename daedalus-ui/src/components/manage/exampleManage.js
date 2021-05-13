/* eslint-disable */
/** 实例管理（非环境管理） */
import React, { Component, Fragment } from 'react';
import { 
  Card, 
  Table, 
  Pagination, 
  ConfigProvider, 
  Divider, 
  Button, 
  Form, 
  Input, 
  Modal, 
  Popconfirm, 
  Select, 
  InputNumber, 
  message
} from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import _ from 'lodash';
import request from '@/util/request';
import Mapping from '../public/mapping';
import Http from '../assemblyLine/useType/http';
import ExampleFilter from './exampleFilter';
const { envOrExampleLayout } = Mapping;
const { Option } = Select;

class ExampleManage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [],
      total: 0,
      pageSize: 10,
      pageIndex: 1,
      showModalVisible: false,
      title: '',
      record: {},
      loading: false,
      btnLoading: false,
    };
  }
  componentWillReceiveProps(nextProps) {
    if (this.props.match.path !== nextProps.match.path) {
      this.setState({
        pageIndex: 1
      }, () => {
        this.getMyTable();
      });
    }
  }
  componentDidMount() {
    this.getMyTable();
  }
  // 转换 数组转为字符串
  convert = params => {
    if (params !== undefined) {
      let array = typeof params === 'string' ? [params] : params;
      let str = '';
      array.forEach(item => {
        str += item;
      });
      return str;
    }
    return undefined;
  }
  // 去除不符合条件的
  filterKeyValue = list => {
    let defaultList = [];
    let filterList = _.cloneDeep(list);
    if (filterList && filterList.length > 0) {
      list.map((item, i) => {
        if (!item.name || !item.value) {
          filterList.splice(i, 1);
        }
        return item;
      });
      return filterList;
    }
    return defaultList;
  }
  // 键值对默认值
  addId = list => {
    if (list.length <= 0) {
      list = [{ id: new Date().getTime() }];
    }
    return list;
  }
  // 获取table数据
  getMyTable = value => {
    const path = this.props.match.path.split('/');
    let type = path[path.length - 1];
    this.setState({ loading: true });
    request(`/instance/list`, {
      method: 'GET',
      params: {
        insType: type.toUpperCase(),
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize,
        ...value
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ list: res.data, total: res.total, loading: false });
      } else {
        this.setState({ loading: false });
        message.error(res.msg);
      }
    });
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
    if (title === '编辑实例') {
      if (record.headers) record.headers = this.addId(record.headers);
      if (record.urlParams) record.urlParams = this.addId(record.urlParams);
      if (record.formData) record.formData = this.addId(record.formData);
      if (record.cookies) record.cookies = this.addId(record.cookies);
    }
    this.setState({
      title,
      showModalVisible: true,
      record: record ? record : {},
    });
  }
  // 弹框确认
  handleModalOk = () => {
    this.props.form.validateFields((error, value) => {
      if (!error) {
        const path = this.props.match.path.split('/');
        let type = path[path.length - 1];
        if (this.state.title === '编辑实例') {
          value.id = this.state.record.id;
        }
        if (type === 'http') {
          value.headers = this.filterKeyValue(value.headers);
          value.urlParams = this.filterKeyValue(value.urlParams);
          value.url = this.convert(value.url);
          if (value.cookieText) {
            delete value.cookies;
          } else {
            value.cookies = this.filterKeyValue(value.cookies);
          }
          if (value.bodyType === 'JSON' || value.bodyType === 'TEXT') {
            value.formData = [];
          } else {
            value.formData = this.filterKeyValue(value.formData);
          }
        }
        this.setState({ btnLoading: true });
        request(`/instance/save`, {
          method: 'POST',
          body: {
            ...value,
            instanceType: type.toUpperCase()
          }
        }).then(res => {
          if (res.success === true) {
            if (this.state.title === '编辑实例') {
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
    this.setState({ showModalVisible: false });
    this.props.form.resetFields();
  }
  // 删除实例
  delete = record => {
    request(`/instance/delete?instanceId=${record.id}`, {
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
  // table 显示
  getColumn = (type) => {
    let column = [
      {
        dataIndex: 'name',
        title: '名称',
      }
    ];
    if (type !== 'http') {
      column.push({
        dataIndex: 'ip',
        title: 'IP',
      });
      column.push({
        dataIndex: 'port',
        title: '端口号',
      });
    }
    if (type === 'http') {
      column.push({
        dataIndex: 'url',
        title: 'URL',
      }, {
        dataIndex: 'method',
        title: 'Method',
      }, {
        dataIndex: 'creator',
        title: '创建人',
      });
    } else if (type === 'registry') {
      column.push({
        dataIndex: 'protocol',
        title: '类型',
      });
    } else if (type === 'es') {
      column.push({
        dataIndex: 'database',
        title: 'Index',
      });
    } else if (type === 'mysql') {
      column.push({
        dataIndex: 'database',
        title: '数据库',
      });
      column.push({
        dataIndex: 'username',
        title: '用户名',
      });
      column.push({
        dataIndex: 'password',
        title: '密码',
      });
    }

    let column2 = [{
      dataIndex: 'createTime',
      title: '添加时间',
    },
    {
      dataIndex: 'id',
      title: '操作',
      width: 120,
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
            <a onClick={() => { this.showModal('编辑实例', record) }}>修改</a>
          </div>
        );
      }
    }
    ];
    return [...column, ...column2];
  }

  render() {
    const { list, pageIndex, total, pageSize, showModalVisible, title, record, loading, btnLoading } = this.state;
    const path = this.props.match.path.split('/');
    let type = path[path.length - 1];
    const { getFieldDecorator, setFieldsValue, getFieldValue } = this.props.form;
    let useTypeParams = {
      setFieldsValue,
      getFieldDecorator,
      getFieldValue,
      isEdit: title === '添加实例' ? false : true,
      node: record,
      // sideAllSelectData,
      disabled: false,
    };
    return (
      <ConfigProvider locale={zhCN}>
        <div className="moretable">
          <ExampleFilter
            getMyTable={this.getMyTable}
            type={type}
          />
          <Card>
            <div style={{ textAlign: 'right' }}>
              <Button type="primary" icon="plus" style={{ marginBottom: 10 }} onClick={() => this.showModal('添加实例')}>新建</Button>
            </div>
            <Table
              rowKey="id"
              loading={loading}
              dataSource={list}
              columns={this.getColumn(type)}
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
          width={800}
          visible={showModalVisible}
          onCancel={this.handleModalCancel}
          footer={
            <div>
              <Button onClick={() => this.handleModalCancel()}>取消</Button>
              <Button
                onClick={() => this.handleModalOk()}
                type="primary"
                loading={btnLoading}
              >
                确认
              </Button>
            </div>
          }
        >
          <Form.Item label="名称" {...envOrExampleLayout} >
            {getFieldDecorator('name', {
              rules: [{ required: true, message: '请输入名称！' }],
              initialValue: title === '添加实例' ? '' : record.name
            })(
              <Input placeholder="请输入名称" />
            )}
          </Form.Item>
          {
            type === 'http' &&
            <Http {...useTypeParams} />
          }
          {
            type !== 'http' &&
            <Fragment>
              <Form.Item label="IP" {...envOrExampleLayout} >
                {getFieldDecorator('ip', {
                  rules: [{ required: true, message: '请输入！' }],
                  initialValue: title === '添加实例' ? '' : record.ip
                })(
                  <Input placeholder="请输入..." />
                )}
              </Form.Item>
              <Form.Item label="端口号" {...envOrExampleLayout} >
                {getFieldDecorator('port', {
                  rules: [{ required: true, message: '请输入端口号！' }],
                  initialValue: title === '添加实例' ? '' : record.port
                })(
                  <InputNumber placeholder="请输入端口号" style={{ width: '100%' }} />
                )}
              </Form.Item>
            </Fragment>
          }
          {type === 'registry' &&
            <Form.Item label="类型" {...envOrExampleLayout} >
              {getFieldDecorator('protocol', {
                rules: [{ required: true, message: '请选择类型！' }],
                initialValue: title === '添加实例' ? 'zookeeper' : record.protocol
              })(
                <Select
                  style={{ width: '100%' }}
                  placeholder="请选择类型"
                >
                  <Option key="zookeeper" value="zookeeper">zookeeper</Option>
                </Select>
              )}
            </Form.Item>
          }
          {
            type === 'es' &&
            <Form.Item label="index" {...envOrExampleLayout} >
              {getFieldDecorator('database', {
                rules: [{ required: true, message: '请输入index！' }],
                initialValue: title === '添加实例' ? '' : record.database
              })(
                <Input placeholder="请输入index" style={{ width: '100%' }} />
              )}
            </Form.Item>
          }
          {type === 'mysql' &&
            <Fragment>
              <Form.Item label="数据库" {...envOrExampleLayout} >
                {getFieldDecorator('database', {
                  rules: [{ required: true, message: '请输入数据库！' }],
                  initialValue: title === '添加实例' ? '' : record.database
                })(
                  <Input placeholder="请输入数据库" />
                )}
              </Form.Item>
              <Form.Item label="用户名" {...envOrExampleLayout} >
                {getFieldDecorator('username', {
                  rules: [{ required: true, message: '请输入用户名！' }],
                  initialValue: title === '添加实例' ? '' : record.username
                })(
                  <Input placeholder="请输入用户名" />
                )}
              </Form.Item>
              <Form.Item label="密码" {...envOrExampleLayout} >
                {getFieldDecorator('password', {
                  rules: [{ required: true, message: '请输入密码！' }],
                  initialValue: title === '添加实例' ? '' : record.password
                })(
                  <Input placeholder="请输入密码" type="password" />
                )}
              </Form.Item>
            </Fragment>
          }
        </Modal>
      </ConfigProvider>
    );
  }
}
export default Form.create()(ExampleManage);