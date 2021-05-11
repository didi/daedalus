/* eslint-disable */
/** 查看更多 */
import React, { Component } from 'react';
import { Card, Table, Pagination, ConfigProvider, Divider, Tag, Tooltip, Icon, message } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import request from '@/util/request';

export default class MoreTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [], //
      bizLine: [],  // 业务线
      tags: [],  // 标签
      total: 0,
      pageSize: 10,
      pageIndex: 1,
      loading: false,
    };
  }

  componentDidMount() {
    this.getBizlineList();
    this.getTagsList();
    this.getTableList();
  }
  getUrl = () => {
    let url = '';
    switch (this.props.match.params.type) {
      case 'myCreate':
        url = `/pipeline/queryOwn`;
        break;
      case 'recentlyUse':
        url = `/pipeline/queryRecently`;
        break;
      case 'collection':
        url = `/pipeline/queryFavorites`;
        break;
      case 'mostUse':
        url = `/pipeline/queryPopular`;
        break;
      default:
        url = `/pipeline/list`;
        break;
    }
    return url;
  }
  getTableList = filters => {
    this.setState({ loading: true });
    request(this.getUrl(), {
      method: 'GET',
      params: {
        page: this.state.pageIndex - 1,
        pageSize: this.state.pageSize,
        bizLine: filters ? filters.bizLine : [],
        tags: filters ? filters.tag : [],
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ list: res.data, loading: false, total: res.total });
      } else {
        this.setState({ loading: false });
      }
    });
  }
  // 获取业务线
  getBizlineList = () => {
    request(`/config/bizLine`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        this.setState({ bizLine: res.data });
      }
    });
  }
  // 获取标签
  getTagsList = () => {
    request(`/config/tags`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        this.setState({ tags: res.data });
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
  // table筛选功能
  handleTableChange = (pagination, filters) => {
    this.getTableList(filters);
  }
  // 收藏
  favorites = record => {
    request(`/user/collect?isCollect=${!record.collect}&pipelineId=${record.id}`, {
      method: 'POST',
    }).then(res => {
      if (res.success === true) {
        const { list } = this.state;
        let recordList = record;
        if (this.props.match.params.type !== 'collection') {
          list.forEach(item => {
            if (item.id === record.id) item.collect = !item.collect;
          });
        } else {
          if (list.some(ele => { return ele.id === record.id })) {
            recordList.collect = false;
            list.splice(list.findIndex(x => x.id === record.id), 1);
          } else {
            recordList.collect = true;
            list.unshift({ ...recordList });
          }
        }
        this.setState({ list });
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

  render() {
    const { pageIndex, total, pageSize, list, bizLine, tags, loading } = this.state;
    let type = this.props.match.params.type;
    const bizLines = [];
    const tagsList = [];
    bizLine.forEach(item => {
      bizLines.push({
        text: item.name,
        value: item.code,
      });
    });
    tags.forEach(item => {
      tagsList.push({
        text: item,
        value: item,
      });
    });
    const columns = [
      {
        dataIndex: 'name',
        title: '名称',
      },
      {
        dataIndex: 'bizLine',
        title: '业务线',
        filters: bizLines,
        render: t => {
          return <div>{bizLine.length > 0 ? bizLine.find(x => x.code === t).name : ''}</div>;
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
                return <Tag key={item}>{item}</Tag>;
              })
            }
          </div>;
        }
      },
      {
        dataIndex: 'creatorCN',
        title: '创建人',
      },
      {
        dataIndex: 'id',
        title: '操作',
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
                  <a onClick={() => this.props.history.push(`/assemblyLine/edit-${record.id}`)}>
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
        <div className="moretable">
          <Card
            title={
              type === 'myCreate' ? '我的流水线' : type === 'recentlyUse' ? '最近使用' : type === 'collection' ? '收藏夹' : type === 'mostUse' ? '平台使用排行' : '平台最新流水线'
            }
          >
            <Table
              rowKey="id"
              loading={loading}
              dataSource={list}
              columns={columns}
              pagination={false}
              onChange={this.handleTableChange}
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
      </ConfigProvider>
    );
  }
}