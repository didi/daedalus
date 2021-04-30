/* eslint-disable */
/** 工作台首页 */
import React, { Component, Fragment } from 'react';
import {
  Card,
  Col,
  Row,
  Icon,
  Table,
  Pagination,
  ConfigProvider,
  Divider,
  Tag,
  Menu,
  Dropdown,
  Tooltip,
  message
} from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import request from '@/util/request';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import './index.scss';

class WorkBench extends Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [], // 最新
      listLoading: false,
      myList: [], // 我的
      myLoading: false,
      recentlyList: [],  // 最近
      recentlytLoading: false,
      popularList: [],  // 最多
      popularLoading: false,
      favoritesList: [],  // 收藏
      favoritestLoading: false,
      total: 0,
      pageSize: 5,
      pageIndex: 1,
    };
  }

  componentDidMount() {
    this.getMyTable();
    this.getQueryRecently();
    this.getQueryPopular();
    this.getQueryFavorites();
    this.getQueryList();
  }
  // 获取我的流水线
  getMyTable = filters => {
    this.setState({ myLoading: true });
    request(`/pipeline/queryOwn`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 5,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ myList: res.data, myLoading: false });
      } else {
        this.setState({ myLoading: false });
      }
    });
  }
  // // 最近使用列表
  getQueryRecently = filters => {
    this.setState({ recentlytLoading: true });
    request(`/pipeline/queryRecently`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 5,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ recentlyList: res.data, recentlytLoading: false });
      } else {
        this.setState({ recentlytLoading: false });
      }
    });
  }
  // // 获取最多流水线
  getQueryPopular = filters => {
    this.setState({ popularLoading: true });
    request(`/pipeline/queryPopular`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 5,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ popularList: res.data, popularLoading: false });
      } else {
        this.setState({ popularLoading: false });
      }
    });
  }
  // // 获取收藏流水线
  getQueryFavorites = filters => {
  //   this.setState({ favoritestLoading: true });
    request(`/pipeline/queryFavorites`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 5,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ favoritesList: res.data, favoritestLoading: false });
      } else {
        this.setState({ favoritestLoading: false });
      }
    });
  }
  // // 获取最新创建
  getQueryList = filters => {
    this.setState({ listLoading: true });
    request(`/pipeline/list`, {
      method: 'GET',
      params: {
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ list: res.data, total: res.total, listLoading: false });
      } else {
        this.setState({ listLoading: false });
      }
    });
  }
  // 分页器发生变化
  onChangePagination = page => {
    this.setState({ pageIndex: page }, () => {
      this.getQueryList();
    });
  }
  // 历史分页器 pageSize 变化的回调
  onShowSizeChange = (current, pageSize) => {
    this.setState({ pageIndex: 1, pageSize }, () => {
      this.getQueryList();
    });
  }
  // table筛选功能
  handleTableChange = (pagination, filters, title) => {
    switch (title) {
      case '我的流水线':
        return this.getMyTable(filters);
      case '最近使用':
        return this.getQueryRecently(filters);
      case '收藏夹':
        return this.getQueryFavorites(filters);
      case '平台使用排行':
        return this.getQueryPopular(filters);
      default:
        return this.getQueryList(filters);
    }
  }
  // 创建流水线
  addAssemblyLine = () => {
    this.props.history.push('/assemblyLine/add');
  }
  // 编辑流水线
  editAssemblyLine = record => {
    this.props.history.push(`/assemblyLine/edit-${record.id}`);
  }
  // 收藏
  favorites = record => {
    request(`/user/collect?isCollect=${!record.collect}&pipelineId=${record.id}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        const { list, myList, recentlyList, popularList, favoritesList } = this.state;
        list.forEach(item => {
          if (item.id === record.id) item.collect = !item.collect;
        });
        myList.forEach(item => {
          if (item.id === record.id) item.collect = !item.collect;
        });
        recentlyList.forEach(item => {
          if (item.id === record.id) item.collect = !item.collect;
        });
        popularList.forEach(item => {
          if (item.id === record.id) item.collect = !item.collect;
        });

        let recordList = record;
        delete recordList.edit;
        if (favoritesList.some(ele => { return ele.id === record.id })) {
          recordList.collect = false;
          favoritesList.splice(favoritesList.findIndex(x => x.id === record.id), 1);
        } else {
          recordList.collect = true;
          favoritesList.unshift({ ...recordList });
        }
        this.setState({ list, myList, recentlyList, popularList, favoritesList });
        if (recordList.collect || record.collect) {
          message.success('收藏成功');
        } else {
          message.success('取消收藏成功');
        }
      } else {
        message.error(res.msg);
      }
    });
  }
  // 查看更多
  handleMenuClick = type => {
    this.props.history.push(`/moretable/${type}`);
  }

  render() {
    const {
      pageIndex, total, myList, favoritesList, popularList, recentlyList, pageSize, list, listLoading,
      myLoading, recentlytLoading, popularLoading, favoritestLoading
    } = this.state;
    const bizLines = [];
    const tagsList = [];
    this.props.bizLine.length > 0 && this.props.bizLine.forEach(item => {
      bizLines.push({
        text: item.name,
        value: item.code,
      });
    });
    this.props.tag.forEach(item => {
      tagsList.push({
        text: item,
        value: item,
      });
    });
    const columns = [
      {
        dataIndex: 'name',
        title: '名称',
        fixed: 'left',
      },
      {
        dataIndex: 'bizLine',
        title: '业务线',
        filters: bizLines,
        render: t => {
          const aa = this.props.bizLine.find(x => x.code === t);
          return <div>{aa ? aa.name : ''}</div>;
        }
      },
      {
        dataIndex: 'tag',
        title: '标签',
        filters: tagsList,
        render: (t, record) => {
          return <div>
            {
              record.tags && record.tags.map(item => {
                return <Tag
                  key={item}
                  onClick={() => this.props.history.push(`/filterTable/tag/${item}`)}
                  style={{ cursor: 'pointer' }}
                >
                  {item}
                </Tag>;
              })
            }
          </div>;
        }
      },
      {
        dataIndex: 'creatorCN',
        title: '创建人',
        render: (t, record) => {
          return <span
            onClick={() => this.props.history.push(`/filterTable/people/${record.creator}`)}
            style={{ cursor: 'pointer' }}
          >
            {t}
          </span>;
        }
      },
      {
        dataIndex: 'id',
        title: '操作',
        width: 120,
        fixed: 'right',
        render: (t, record) => {
          return (
            <div>
              <a onClick={() => this.favorites(record)}>
                <Tooltip placement="bottom" title={'收藏'}>
                  {record.collect ? <Icon type="heart" theme="filled" style={{ color: 'red' }} /> : <Icon type="heart" />}
                </Tooltip>
              </a>
              <Divider type="vertical" />
              {
                record.editable === true && <React.Fragment>
                  <a onClick={() => this.editAssemblyLine(record)}>
                    <Tooltip placement="bottom" title={'编辑'}>
                      <Icon type="edit" />
                    </Tooltip>
                  </a>
                  <Divider type="vertical" />
                </React.Fragment>
              }
              <a onClick={() => this.props.history.push(`/function/${record.id}`)}>
                <Tooltip placement="bottom" title={'运行'}>
                  <Icon type="play-square" />
                </Tooltip>
              </a>
            </div>
          );
        }
      }
    ];
    return (
      <ConfigProvider locale={zhCN}>
        <div className="work">
          <Card style={{ marginTop: 20 }}>
            <div className="operation">
              <div className="actionItem">
                <Icon type="plus-square" onClick={this.addAssemblyLine} />
                <span>创建流水线</span>
              </div>
              <div className="actionItem">
                <Icon type="menu" onClick={() => this.props.history.push('/moretable/latestCreate')} />
                <span>浏览流水线</span>
              </div>
              <div className="actionItem">
                <Icon type="profile" onClick={() => this.props.history.push('/moretable/myCreate')} />
                <span>管理流水线</span>
              </div>
            </div>
          </Card>
          <Row className="bisectionRow">
            <Col span={12} className="pr_10 col">
              <Card
                title="我的流水线"
                extra={
                  <Fragment>
                    <Icon type="reload" className="cursor" title="刷新" onClick={() => this.getMyTable()} />
                    <Dropdown
                      overlay={
                        <Menu onClick={() => this.handleMenuClick('myCreate')}>
                          <Menu.Item>查看更多</Menu.Item>
                        </Menu>
                      }
                      placement="bottomLeft"
                    >
                      <Icon type="more" className="cursor" style={{ marginLeft: 10 }} />
                    </Dropdown>
                  </Fragment>
                }
              >
                <Table
                  rowKey="id"
                  loading={myLoading}
                  dataSource={myList}
                  columns={columns}
                  pagination={false}
                  onChange={(pagination, filters) => this.handleTableChange(pagination, filters, '我的流水线')}
                  scroll={{ x: true }}
                />
              </Card>
            </Col>
            <Col span={12} className="pl_10 col">
              <Card
                title="收藏夹"
                extra={
                  <Fragment>
                    <Icon type="reload" className="cursor" title="刷新" onClick={() => this.getQueryFavorites()} />
                    <Dropdown
                      overlay={
                        <Menu onClick={() => this.handleMenuClick('collection')}>
                          <Menu.Item>查看更多</Menu.Item>
                        </Menu>
                      }
                      placement="bottomLeft"
                    >
                      <Icon type="more" className="cursor" style={{ marginLeft: 10 }} />
                    </Dropdown>
                  </Fragment>
                }
              >
                <Table
                  rowKey="id"
                  loading={favoritestLoading}
                  dataSource={favoritesList}
                  columns={columns}
                  pagination={false}
                  onChange={(pagination, filters) => this.handleTableChange(pagination, filters, '收藏夹')}
                  scroll={{ x: true }}
                />
              </Card>
            </Col>
          </Row>
          <Row className="bisectionRow">
            <Col span={12} className="pr_10 col">
              <Card
                title="最近使用"
                extra={
                  <Fragment>
                    <Icon type="reload" className="cursor" title="刷新" onClick={() => this.getQueryRecently()} />
                    <Dropdown
                      overlay={
                        <Menu onClick={() => this.handleMenuClick('recentlyUse')}>
                          <Menu.Item>查看更多</Menu.Item>
                        </Menu>
                      }
                      placement="bottomLeft"
                    >
                      <Icon type="more" className="cursor" style={{ marginLeft: 10 }} />
                    </Dropdown>
                  </Fragment>
                }
              >
                <Table
                  rowKey="id"
                  loading={recentlytLoading}
                  dataSource={recentlyList}
                  columns={columns}
                  pagination={false}
                  onChange={(pagination, filters) => this.handleTableChange(pagination, filters, '最近使用')}
                  scroll={{ x: true }}
                />
              </Card>
            </Col>
            <Col span={12} className="pl_10 col">
              <Card
                title="平台使用排行"
                extra={
                  <Fragment>
                    <Icon type="reload" className="cursor" title="刷新" onClick={() => this.getQueryPopular()} />
                    <Dropdown
                      overlay={
                        <Menu onClick={() => this.handleMenuClick('mostUse')}>
                          <Menu.Item>查看更多</Menu.Item>
                        </Menu>
                      }
                      placement="bottomLeft"
                    >
                      <Icon type="more" className="cursor" style={{ marginLeft: 10 }} />
                    </Dropdown>
                  </Fragment>
                }
              >
                <Table
                  rowKey="id"
                  loading={popularLoading}
                  dataSource={popularList}
                  columns={columns}
                  pagination={false}
                  onChange={(pagination, filters) => this.handleTableChange(pagination, filters, '平台使用排行')}
                  scroll={{ x: true }}
                />
              </Card>
            </Col>
          </Row>
          <Card
            title="平台最新流水线"
            extra={
              <Fragment>
                <Icon type="reload" className="cursor" title="刷新" onClick={() => this.getQueryList()} />
                <Dropdown
                  overlay={
                    <Menu onClick={() => this.handleMenuClick('latestCreate')}>
                      <Menu.Item>查看更多</Menu.Item>
                    </Menu>
                  }
                  placement="bottomLeft"
                >
                  <Icon type="more" className="cursor" style={{ marginLeft: 10 }} />
                </Dropdown>
              </Fragment>
            }
          >
            <Table
              rowKey="id"
              loading={listLoading}
              dataSource={list}
              columns={columns}
              pagination={false}
              onChange={(pagination, filters) => this.handleTableChange(pagination, filters, '平台最新流水线')}
              footer={(currentData) => (
                <div style={{ display: total === 0 ? 'none' : 'block', textAlign: 'right' }}>
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
              )}
            />
          </Card>
        </div>
      </ConfigProvider>
    );
  }
}
export default connect((state) => ({
  bizLine: state.initialValueObj.bizLine,
  tag: state.initialValueObj.tag,
}), intialValueAction)(WorkBench);