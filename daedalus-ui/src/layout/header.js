/** 顶部 */
import React, { Component } from 'react';
import { Menu, Layout } from 'antd';
import { Link } from 'react-router-dom';
import './layout.scss';
import request from '@/util/request';
import store from '../store/index';
import intialValueAction from '../store/action/intialValueAction';
const { Header } = Layout;
const { SubMenu } = Menu;

export default class Headers extends Component {
  componentDidMount() {
    this.getBizlineList();
    this.getTagsList();
    this.getOperatorList();
    this.getDubboParamType();
    this.getDateFormat();
    this.getEnvList();
  }
  // 获取业务线
  getBizlineList = () => {
    request(`/config/bizLine`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setBizLine({bizLine: res.data}));
      }
    });
  }
  // 获取标签
  getTagsList = () => {
    request(`/config/tags`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setTag({tag: res.data}));
      }
    });
  }
  // 获取执行条件
  getOperatorList = () => {
    request(`/config/operator`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setOperator({operatorList: res.data}));
      }
    });
  }
  // 获取dubboParamType
  getDubboParamType = () => {
    request(`/config/dubboParamType`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setDubboParamType({dubboParamType: res.data}));
      }
    });
  }
  // 获取格式
  getDateFormat = () => {
    request(`/config/dateFormat`, {
      method: 'GET',
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setDateFormat({dateFormat: res.data}));
      }
    });
  }
  // 获取环境列表
  getEnvList = () => {
    request(`/env/list`, {
      method: 'GET',
      params: {
        page: 0,
        pageSize: 9999,
      }
    }).then(res => {
      if (res.success === true) {
        store.dispatch(intialValueAction.setEnvGroup({envList: res.data}));
      }
    });
  }

  render() {
    return (
      <Header className='header'>
        <div className='header_title'>Daedalus</div>
        <Menu
          theme="dark"
          mode="horizontal"
          style={{ lineHeight: '64px' }}
          defaultSelectedKeys={window.location.hash.split('#/')[1] ? window.location.hash.split('#/')[1] : 'workbench'}
        >
          <Menu.Item key="workbench">
            <Link to="/workbench">工作台</Link>
          </Menu.Item>
          <SubMenu title={<span>实例管理</span>} key="manage">
            <Menu.Item key="envManage"><Link to="/envManage">环境管理</Link></Menu.Item>
            <Menu.Item key="mysql"><Link to="/exampleManage/mysql">MYSQL</Link></Menu.Item>
            <Menu.Item key="redis"><Link to="/exampleManage/redis">Redis</Link></Menu.Item>
            <Menu.Item key="http"><Link to="/exampleManage/http">HTTP模板</Link></Menu.Item>
//            <Menu.Item key="es"><Link to="/exampleManage/es">ES</Link></Menu.Item>
            <Menu.Item key="registry"><Link to="/exampleManage/registry">注册中心</Link></Menu.Item>
          </SubMenu>
          <Menu.Item key="useFile">
            <Link to="/useFile">使用文档</Link>
          </Menu.Item>
          <Menu.Item key="timingTrigger">
            <Link to="/timingTrigger">定时任务</Link>
          </Menu.Item>
          <Menu.Item key="catalog">
            <Link to="/catalog">我的目录</Link>
          </Menu.Item>
          {/*<Menu.Item key="statistics">
            <Link to="/statistics">统计大盘</Link>
          </Menu.Item>*/}
        </Menu>
      </Header>
    );
  }
}