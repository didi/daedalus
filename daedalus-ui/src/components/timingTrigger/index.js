/* eslint-disable */
/** 定时任务 */
import React, { Component, Fragment } from 'react';
import { Card, Table, Pagination, ConfigProvider, Divider, Button, Switch, message, Popconfirm } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import Modals from './modal';
import request from '@/util/request';

class TimingTrigger extends Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [],
      loading: false,
      total: 0,
      pageSize: 10,
      pageIndex: 1,
      visible: false,
      record: {},
      title: '',
      lineList: [],
    };
  }
  componentDidMount() {
    this.getTableList();
    this.getQueryList();
  }
  // 获取定时任务列表
  getTableList = () => {
    this.setState({ loading: true });
    request(`/schedule/list`, {
      method: 'GET',
      params: {
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize,
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ list: res.data, loading: false, total: res.total });
      } else {
        this.setState({ loading: false });
      }
    });
  }
  // 获取最新创建流水线列表
  getQueryList = () => {
    request(`/pipeline/list`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 9999,
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ lineList: res.data });
      }
    });
  }
  // 分页器发生变化
  onChangePagination = page => {
    this.setState({ pageIndex: page }, () => {
      this.getTableList();
    });
  }
  // 历史分页器 pageSize 变化的回调
  onShowSizeChange = (current, pageSize) => {
    this.setState({ pageIndex: 1, pageSize }, () => {
      this.getTableList();
    });
  }
  // 创建 or 编辑
  showModal = (title, record) => {
    this.setState({
      visible: true,
      title,
      record: record ? record : {},
    });
  }
  // 关闭弹框
  closeModal = () => {
    this.setState({
      visible: false,
      title: '',
      record: {}
    });
  }
  // 删除任务
  delete = record => {
    request(`/schedule/delete?scheduleId=${record.id}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        message.success('删除成功');
        this.getTableList();
      } else {
        message.success(res.msg);
      }
    });
  }
  // 更改状态
  switchChange = record => {
    const { list } = this.state;
    let status = record.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
    request(`/schedule/switch?scheduleId=${record.id}&status=${status}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        list.find(item => item.id === record.id).status = status;
        message.success('状态更新成功');
        this.setState({ list });
      } else {
        message.success(res.msg);
      }
    });
  }

  render() {
    const { pageIndex, total, pageSize, list, loading, visible, record, title, lineList } = this.state;
    const columns = [
      {
        dataIndex: 'pipelineId',
        title: '流水线名称',
        render: t => {
          let name = lineList.length > 0 && lineList.find(item => item.id === t).name;
          return (
            <span>{name || ''}</span>
          );
        }
      },
      {
        dataIndex: 'cronRule',
        title: '定时时间',
      },
      {
        dataIndex: 'env',
        title: '环境',
      },
      {
        dataIndex: 'status',
        title: '状态',
        render: (t, record) => {
          return (
            <Switch
              checked={t === 'ENABLED' ? true : false}
              checkedChildren="开"
              unCheckedChildren="关"
              onChange={() => this.switchChange(record)}
            />
          );
        }
      },
      {
        dataIndex: 'creatorCN',
        title: '创建人',
      },
      {
        dataIndex: 'createTime',
        title: '创建时间',
      },
      {
        dataIndex: 'id',
        title: '操作',
        fixed: 'right',
        render: (t, record) => {
          return (
            <Fragment>
              <a onClick={() => this.showModal('编辑定时任务', record)}>编辑</a>
              <Divider type="vertical" />
              <Popconfirm placement="top" title="确认删除？" onConfirm={() => this.delete(record)}>
                <a>删除</a>
              </Popconfirm>
              <Divider type="vertical" />
              <a onClick={() => this.props.history.push(`/function/${record.pipelineId}`)}>查看运行结果</a>
            </Fragment>
          );
        }
      }
    ];
    return (
      <ConfigProvider locale={zhCN}>
        <div className="moretable">
          <Card
            title={'定时触发流水线'}
            extra={<Button type="primary" onClick={() => this.showModal('创建定时任务')}>创建定时任务</Button>}
          >
            <Table
              rowKey="id"
              loading={loading}
              dataSource={list}
              columns={columns}
              pagination={false}
              onChange={this.handleTableChange}
              scroll={{ x: true }}
              footer={(currentData) => (
                <div style={{ display: total === 0 ? 'none' : 'block', textAlign: 'right' }}>
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
              )}
            />
          </Card>
        </div>
        <Modals
          modalTitle={title}
          visible={visible}
          record={record}
          lineList={lineList}
          closeModal={this.closeModal}
          getTableList={this.getTableList}
        />
      </ConfigProvider>
    );
  }
}
export default TimingTrigger;