/* eslint-disable */
import React, { Component, Fragment } from 'react';
import G6 from '@antv/g6';
import { Button } from 'antd';
import request from '@/util/request';
import AddEditNodeDrawer from './addEditNodeDrawer';
import prune from 'json-prune';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import nodeDataAction from '../../store/action/nodeDataAction';
import _ from 'lodash'

class XMind extends Component {
  constructor(props) {
    super(props) 
    this.state = {
      addVisible: false,  // 添加节点弹框
      childVisible: false,  // 添加节点耳机弹框
      data: {
        nodes: [],
        edges: [],
      },  // 节点数据
      node: {},   // 当前点击要编辑的数据
      event: {}, // 当前点击的节点
      graph: {},  // G6实例
      useAddItemName: '',  // 当前使用的添加步骤
      isDelete: false,  // 是否删除了, 做G6渲染判断用的
      isFirestAdd: false, // 是不是第一次添加
      insTypeList: [], // 实例数据
      allList: []
    }
    this._graph = null
  }
  
  componentDidMount() {
    if (this.props.type !== 'add') this.getNodeData();
  }
  componentWillReceiveProps(nextProps) {
    const allList = [
      ...nextProps.sideAllData.extractVars,
      ...nextProps.sideAllData.globalVars,
      ...nextProps.sideAllData.inputVars
    ];
    let oldSelType = this.state.selType
    let newSelType = nextProps.selType
    this.setState({ allList, selType: newSelType }, () => {
      if(oldSelType !== newSelType) {
        this._graph && this._graph.render()
      }
    });
   
  }
  // 获取节点数据
  getNodeData = () => {
    request(`/pipeline/detail`, {
      method: 'GET',
      params: {
        pipelineId: this.props.type.split('-')[1]
      }
    }).then(res => {
      if (res.success === true) {
        const data = {};
        data.nodes = res.data.flow ? res.data.flow.steps : [];
        data.edges = res.data.flow ? res.data.flow.edges : [];
        window.sessionStorage.setItem('nodeData', JSON.stringify(data));
        this.setState({ data }, () => { if (this.state.data.nodes.length > 0) this.drawNode(this.state.data); });
      }
    });
  }
  // 获取实例数据
  getInsType = type => {
    if (type !== '连接步骤' || type !== '删除线' || type !== 'GROOVY' || type !== 'NOTICE') {
      if (type === 'DUBBO') type = 'REGISTRY';
      request(`/instance/list`, {
        method: 'GET',
        params: {
          insType: type,
          page: 0,
          pageSize: 9999
        }
      }).then(res => {
        if (res.success === true) {
          this.setState({ insTypeList: res.data });
        }
      });
    }
  }
  // G6绘图
  drawNode = dataList => {
    // 绘图
    G6.registerNode('dom-node', {
      draw: (cfg, group) => {
        // 判断当前的节点是否有两个或超过两个(两条线)指向它
        const targetArray = [];
        let data = _.cloneDeep(this.state.data);
        data.edges.length > 0 && data.edges.forEach(item => {
          if (item.target === cfg.id) targetArray.push(item);
        });
        let color = 'green'
        let {selType} = this.state
        if(selType) {
          if(JSON.stringify(cfg).indexOf(`#{${this.state.selType}}`) !== -1) {
            color = 'red';  //#ff4f3e
          }
        }
        const shape = group.addShape('dom', {
          attrs: {
            width: cfg.size[0],
            height: cfg.size[1],
            html: `
            <div id='label-shape' style="background-color: #f2f2f2; border: 1px solid #e6e7eb; border-radius: 5px; border-top: 4px solid ${color}; padding: 0 12px; cursor: pointer; position: relative;">
              <div class="nodeTitle" style="margin:auto; padding:auto; color: #595959; padding: 6px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                <span class="iconfont icon-dingwei" style="display: ${color === 'red' ? 'inline-block' : 'none'}; color: red; line-height: 20px"></span>
                <span>${cfg.name}</spam>
              </div>
              <div style="margin:auto; padding:auto; color: #ff4f3e; padding: 6px 0; border-top: 1px solid #e6e7eb; height:35px; text-align: right">${cfg.stepType || ''}</div>
            </div>
              `
          },
          name: 'label-shape',
          draggable: true
        });
        // 等同于节点的矩形
        group.addShape('rect', {
          attrs: {
            x: 0,
            y: 0,
            width: cfg.size[0],
            height: cfg.size[1],
            // stroke: 'black',
            fill: 'rgba(255, 255, 255, 0)',
            radius: [2, 4],
            cursor: 'pointer',
          },
          name: 'rect-shape',
        });
        if (cfg.id !== data.nodes[0].id && targetArray.length < 2) {
          // 绘制上面的小圆与对应的十字线
          group.addShape('circle', {
            attrs: {
              x: 75,
              y: 2,
              r: 9,
              fill: 'rgba(0, 0, 0, 0.6)',
              opacity: 0,
              cursor: 'pointer',
            },
            name: 'circle-top-shape',
          });
          group.addShape('rect', {
            attrs: {
              x: 74,
              y: -3,
              width: 1,
              height: 10,
              stroke: 'rgba(255, 255, 255, 0.8)',
              cursor: 'pointer',
              opacity: 0,
            },
            name: 'circle-top-shape',
          });
          group.addShape('rect', {
            attrs: {
              x: 70,
              y: 1.5,
              width: 10,
              height: 1,
              stroke: 'rgba(255, 255, 255, 0.8)',
              cursor: 'pointer',
              opacity: 0,
            },
            name: 'circle-top-shape',
          });
          // 绘制下面的小圆与十字线
          group.addShape('circle', {
            attrs: {
              x: 75,
              y: 73,
              r: 9,
              fill: 'rgba(0, 0, 0, 0.6)',
              cursor: 'pointer',
              opacity: 0,
            },
            name: 'circle-bottom-shape',
          });
          group.addShape('rect', {
            attrs: {
              x: 74,
              y: 68.5,
              width: 1,
              height: 10,
              stroke: 'rgba(255, 255, 255, 0.8)',
              cursor: 'pointer',
              opacity: 0,
            },
            name: 'circle-bottom-shape',
          });
          group.addShape('rect', {
            attrs: {
              x: 70,
              y: 72.5,
              width: 10,
              height: 1,
              stroke: 'rgba(255, 255, 255, 0.8)',
              cursor: 'pointer',
              opacity: 0,
            },
            name: 'circle-bottom-shape',
          });
        }
        // 绘制右面的小圆与十字线
        group.addShape('circle', {
          attrs: {
            x: 160,
            y: 38,
            r: 9,
            fill: 'rgba(0, 0, 0, 0.6)',
            cursor: 'pointer',
            opacity: 0,
          },
          name: 'circle-right-shape',
        });
        group.addShape('rect', {
          attrs: {
            x: 159,
            y: 33,
            width: 1,
            height: 10,
            stroke: 'rgba(255, 255, 255, 0.8)',
            cursor: 'pointer',
            opacity: 0,
          },
          name: 'circle-right-shape',
        });
        group.addShape('rect', {
          attrs: {
            x: 155,
            y: 37,
            width: 10,
            height: 1,
            stroke: 'rgba(255, 255, 255, 0.8)',
            cursor: 'pointer',
            opacity: 0,
            pointerEvents: 'none'
          },
          name: 'circle-right-shape',
        });
        // if(cfg.id === data.nodes[0].id){
        // 绘制左面的小圆与十字线
        group.addShape('circle', {
          attrs: {
            x: 0,
            y: 38,
            r: 9,
            fill: 'rgba(0, 0, 0, 0.6)',
            cursor: 'pointer',
            opacity: 0,
          },
          name: 'circle-left-shape',
        });
        group.addShape('rect', {
          attrs: {
            x: -1,
            y: 33,
            width: 1,
            height: 10,
            stroke: 'rgba(255, 255, 255, 0.8)',
            cursor: 'pointer',
            opacity: 0,
          },
          name: 'circle-left-shape',
        });
        group.addShape('rect', {
          attrs: {
            x: -5,
            y: 37,
            width: 10,
            height: 1,
            stroke: 'rgba(255, 255, 255, 0.8)',
            cursor: 'pointer',
            opacity: 0,
            pointerEvents: 'none'
          },
          name: 'circle-left-shape',
        });
        // }
        return shape;
      },
      afterDraw: (cfg, group) => {
        const BigBox = group.find(element => element.get('name') === 'rect-shape');
        const bot_circle = group.findAll(element => element.get('name') === 'circle-bottom-shape');
        const top_circle = group.findAll(element => element.get('name') === 'circle-top-shape');
        const right_circle = group.findAll(element => element.get('name') === 'circle-right-shape');
        const left_circle = group.findAll(element => element.get('name') === 'circle-left-shape');
        // 鼠标移入事件
        const onMouseOver = () => {
          bot_circle.forEach(item => {
            item.attr('opacity', 1);
          });
          top_circle.forEach(item => {
            item.attr('opacity', 1);
          });
          right_circle.forEach(item => {
            item.attr('opacity', 1);
          });
          left_circle.forEach(item => {
            item.attr('opacity', 1);
          });
        };
        // 鼠标移除时间
        const onMouseOut = () => {
          bot_circle.forEach(item => {
            item.attr('opacity', 0);
          });
          top_circle.forEach(item => {
            item.attr('opacity', 0);
          });
          right_circle.forEach(item => {
            item.attr('opacity', 0);
          });
          left_circle.forEach(item => {
            item.attr('opacity', 0);
          });
        };

        if (BigBox) {
          // 大矩形鼠标移入移除
          BigBox.on('mouseover', () => {
            onMouseOver();
          });
          BigBox.on('mouseout', () => {
            onMouseOut();
          });
          // 下面添加鼠标移入移除
          bot_circle.forEach(item => {
            item.on('mouseover', () => {
              onMouseOver();
            });
            item.on('mouseout', () => {
              onMouseOut();
            });
          });
          // 上面添加鼠标移入移除
          top_circle.forEach(item => {
            item.on('mouseover', () => {
              onMouseOver();
            });
            item.on('mouseout', () => {
              onMouseOut();
            });
          });
          // 右面添加鼠标移入移除
          right_circle.forEach(item => {
            item.on('mouseover', () => {
              onMouseOver();
            });
            item.on('mouseout', () => {
              onMouseOut();
            });
          });
           // 左面添加鼠标移入移除
           left_circle.forEach(item => {
            item.on('mouseover', () => {
              onMouseOver();
            });
            item.on('mouseout', () => {
              onMouseOut();
            });
          });
        }
      }
    }, 'single-node');
    // 两手指滑动效果
    G6.registerBehavior('double-finger-drag-canvas', {
      getEvents: function getEvents() {
        return {
          wheel: 'onWheel',
        };
      },
    
      onWheel: function onWheel(ev) {
        if (ev.ctrlKey) {
          const canvas = graph.get('canvas');
          const point = canvas.getPointByClient(ev.clientX, ev.clientY);
          let ratio = graph.getZoom();
          if (ev.wheelDelta > 0) {
            ratio = ratio + ratio * 0.05;
          } else {
            ratio = ratio - ratio * 0.05;
          }
          graph.zoomTo(ratio, {
            x: point.x,
            y: point.y,
          });
        } else {
          const x = ev.deltaX || ev.movementX;
          const y = ev.deltaY || ev.movementY;
          graph.translate(-x, -y);
        }
        ev.preventDefault();
      },
    });
    // 下方Minimap
    const minimap = new G6.Minimap({
      size: [120, 90],
      className: 'minimap',
      // type: 'default',
    });
    // 配置
    const graph = new G6.Graph({
      container: 'mountNode', // String | HTMLElement，必须，在 Step 1 中创建的容器 id 或容器本身
      plugins: [minimap],
      renderer: 'svg',
      linkCenter: true,
      width: document.body.clientWidth > 1900 ? 1625 : 1160, // Number，必须，图的宽度
      height: document.body.clientHeight > 800 ? 800 : 630, // Number，必须，图的高度
      fitViewPadding: [20, 40, 50, 20],
      modes: {
        default: ['drag-canvas', 'double-finger-drag-canvas'], // drag-canvas 允许拖拽画布、zoom-canvas 放缩画布、drag-node 拖拽节点
      },
      // 边默认的属性
      defaultEdge: {
        type: 'cubic-horizontal',
        style: {    // 链接线的样式
          stroke: '#A3B1BF',
          lineAppendWidth: 3
        },
        labelCfg: {
          // autoRotate: true, // 文本根据边的方向旋转
          style: {
            stroke: 'white',
            lineWidth: 5,
          },
        },
      },
      // 节点默认的属性
      defaultNode: {
        type: 'dom-node',
        size: [160, 74],
        labelCfg: {
          autoRotate: true, // 边上的标签文本根据边的方向旋转
          style: {
            fill: 'black',
            fontSize: 14,
          }
        },
        style: {
          fill: 'white',
          stroke: 'black',
          radius: 5,
        }
      },
      layout: {
        type: 'dagre', // 布局类型
        rankdir: 'LR',    // 自左至右布局
        nodeSep: 0,      // 节点之间间距
        rankSep: 20,      // 每个层级之间的间距
        controlPoints: true, // 可选
        preventOverlap: true, // 设置防止重叠
      }
    });

    graph.data(dataList); // 读取 Step 2 中的数据源到图上
    graph.render(); // 渲染图
    // 点击节点
    graph.on('node:click', event => {
      const { allList } = this.state;
      const { item } = event;
      const shape = event.target;
      // 判断当前要做的需求是否是修改
      if (shape.get('name') === 'rect-shape') {
        let node = item._cfg.model;
        if(node.headers) node.headers = this.addId(node.headers);
        if(node.urlParams) node.urlParams = this.addId(node.urlParams);
        if(node.formData) node.formData = this.addId(node.formData);
        if(node.cookies) node.cookies = this.addId(node.cookies);
        if(node.params) node.params = this.addId(node.params);
        if(node.attachments) node.attachments = this.addId(node.attachments);
        if(node.extractVars) node.extractVars = this.addId(node.extractVars);
        node.extractVars.map(it => {
          allList.forEach((ele, i) => {
            if (ele.value === it.name) {
              allList.splice(i, 1);
            }
          });
        });
        this.setState({
          childVisible: true,
          useAddItemName: node.stepType,
          node: node,
          graph,
          title: node ? node.name + ' —— ' + node.stepType + ' 编辑' : '',
          allList,
        }, () => {
          this.getInsType(node.stepType);
        });
      } else {
        this.setState({ addVisible: true, event, graph, title: '', node: item._cfg });
      }
    });
    this._graph = graph
  }
  // addId
  addId = list => {
    if(list.length > 0){
      list.forEach((it, i) => {
        if (!it.hasOwnProperty('id')) {
          it.id = i;
        }
      });
    } else {
      list = [{ id: new Date().getTime() }]
    }
    return list;
  }
  // 关闭抽屉
  onClose = form => {
    this.setState({
      addVisible: false,
      childVisible: false,
      node: {},
      useAddItemName: '',
      title: '',
      isFirestAdd: false,
      allList: [...this.props.sideAllData.extractVars, ...this.props.sideAllData.globalVars, ...this.props.sideAllData.inputVars]
    });
    form.resetFields();
  };
  // 展示二级弹框
  showChildDrawer = (item, i) => {
    this.setState({
      childVisible: true,
      useAddItemName: item.list[i].name,
      title: '详细信息——' + item.list[i].name,
    }, () => {
      this.getInsType(item.list[i].name);
    });
  }
  // 关闭二级弹框
  onChildrenDrawerClose = form => {
    this.setState({
      childVisible: false,
      title: '',
      allList: [
        ...this.props.sideAllData.extractVars,
        ...this.props.sideAllData.globalVars,
        ...this.props.sideAllData.inputVars
      ]
    });
    form.resetFields();
  }
  // 第一次添加节点
  addFirstNode = () => {
    this.setState({ addVisible: true, isFirestAdd: true });
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
  // 键组队去除不符合条件的
  filterKeyValue = (list, type) => {
    let defaultList = [];
    let filterList = _.cloneDeep(list);
    if( filterList && filterList.length > 0 ) {
      list.map((item, i) => {
        if((type === 'params' ? !item.type : !item.name) || !item.value){
          filterList.splice(i, 1)
        }
        return item;
      })
      return filterList;
    }
    return defaultList;
  }
  extractVarsFilter = list => {
    let defaultList = [];
    let filterList = _.cloneDeep(list);
    if( filterList && filterList.length > 0 ) {
      list.map((item, i) => {
        if(!item.location || !item.name || (item.location !== 'RESULT' && !item.path)){
          filterList.splice(i, 1)
        }
        return item;
      })
      return filterList;
    }
    return defaultList;
  }
  // 添加节点
  addNode = form => {
    form.validateFields((error, value) => {
      let extractIsError = value.extractVars && value.extractVars.some(it => it.hasOwnProperty('showError'));
      if (!error && !extractIsError) {
        const { event, data, graph, useAddItemName, isDelete, isFirestAdd } = this.state;
        const operatorList = this.props.operatorList;
        const { item } = event;
        const shape = event.target;
        let details = {};
        for(let k in value){
          details[k] = value[k]
        }
        if (useAddItemName === 'HTTP') {
          details.url = this.convert(value.url);
          if (value.cookieText) {
            details.cookieText = value.cookieText || '';
            delete details.cookies;
          } else {
            details.cookies = value.cookies;
            delete details.cookieText;
          }
          if (value.bodyType === 'JSON' || value.bodyType === 'TEXT') {
            details.body = value.body;
            details.formData = [];
          } else {
            details.formData = value.formData;
          }
        } else if (useAddItemName === 'DUBBO') {
          details.ip = this.convert(value.ip);
          details.className = this.convert(value.dubboInterface);
          details.method = this.convert(value.method);
          details.group = this.convert(value.group);
        }
        delete details.label;
        let currentMS = JSON.stringify(new Date().getTime());  // 当前毫秒值
        let desc = value.operator ? operatorList.find(x => x.name === value.operator).desc : '';  // 判断条件
        // 节点数据
        let nodeParams = {
          id: currentMS,
          name: value.label,
          stepType: useAddItemName,
          condition: {},
          extractVars: value.extractVars,
          ...details
        };
        if (value.variable) {
          nodeParams.condition = {
            rules: [
              {
                variable: this.convert(value.variable),
                operator: value.operator,
                value: this.convert(value.value),
                regex: value.regex,
              }
            ]
          };
        }
        // 判断是否是第一次添加节点
        if (!isFirestAdd) {
          // 判断是否选择连接到已有节点
          if (useAddItemName === '连接步骤') {
            let targetIndex = data.edges.findIndex((x) => x.target === value.haveNode)
            data.edges.push({
              source: item._cfg.id, // 起始点 id
              target: value.haveNode, // 目标点 id
              label: targetIndex > -1 ? data.edges[targetIndex].label : '',
            });
          } else if (useAddItemName === '删除线') {
            data.edges.splice(data.edges.findIndex((x) => x.target === value.deleEdge && x.source === item._cfg.id), 1);
          } else {
            // 点击左侧添加
            if (shape.get('name') === 'circle-left-shape') {
              // 当前连接该节点的label
              let targetLabel = data.nodes.find(it => it.id === item._cfg.id).condition.rules;
              // 判断条件
              let labelDesc = ''
              if(targetLabel && targetLabel.length > 0) labelDesc = operatorList.find(x => x.name === targetLabel[0].operator).desc;
              if(item._cfg.id === data.nodes[0].id){
                data.nodes.unshift({ ...nodeParams });
              } else {
                data.nodes.push({ ...nodeParams });
              }
              data.edges = data.edges.map(edg => {
                if(edg.target === item._cfg.id){
                  edg.target = currentMS;
                  edg.label = desc ? (value.variable + desc + (value.value || value.regex)).slice(0, 15) + '...' : '';
                }
                return edg;
              })
              data.edges.push({
                source: currentMS,
                target: item._cfg.id,
                label: labelDesc ? (targetLabel[0].variable + labelDesc + (targetLabel[0].value || targetLabel[0].regex)).slice(0, 15) : '',
              });
            } else {
              // 线数据
              let index = data.nodes.findIndex((x) => x.id === item._cfg.id);
              let i = data.edges.findIndex((x) => x.target === item._cfg.id);
              // 当前要添加兄弟级的父id
              let sourceList = item._cfg.edges.find(edge => edge._cfg.model.target === item._cfg.id)
              let source = item._cfg.edges.length > 0 && sourceList ? sourceList._cfg.model.source : '';
              const edgeParams = {
                source: shape.get('name') === 'circle-right-shape' ? item._cfg.id : source,
                target: currentMS,
                label: desc ? (value.variable + desc + (value.value || value.regex)).slice(0, 15) + '...' : '',
              };
              // 点击右侧添加
              if (shape.get('name') === 'circle-right-shape') {
                data.nodes.push({ ...nodeParams });
                data.edges = data.edges.map(edg => {
                  if(edg.source === item._cfg.id){
                    edg.source = currentMS;
                  }
                  return edg;
                })

                data.edges.push({ ...edgeParams });
              }
              // 点击上面添加
              if (shape.get('name') === 'circle-top-shape') {
                data.nodes.splice(index, 0, { ...nodeParams });
                if (item._cfg.edges.length > 0 && source !== item._cfg.id) {
                  if (i === -1) {
                    data.edges.push({ ...edgeParams });
                  } else {
                    data.edges.splice(i, 0, { ...edgeParams });
                  }
                }
              }
              // 点击下面添加
              if (shape.get('name') === 'circle-bottom-shape') {
                data.nodes.splice(index + 1, 0, { ...nodeParams });
                if (item._cfg.edges.length > 0 && source !== item._cfg.id) {
                  if (i === -1) {
                    data.edges.push({ ...edgeParams });
                  } else {
                    data.edges.splice(i + 1, 0, { ...edgeParams });
                  }
                }
              }
            }
          }
          if (shape.get('name') === 'circle-left-shape'){
            graph.data(data);
            graph.render();
          } else {
            graph.changeData(data);
          }
          this.onClose(form);
        } else {
          data.nodes.push({ ...nodeParams });
          if (isDelete) {
            graph.data(data);
            graph.render();
          } else {
            this.drawNode(data);
          }
          this.onClose(form);
        }
        const a = data;
        a.nodes.forEach(item => {
          // 数据判判断校验
          let fields = ['headers', 'urlParams', 'cookies', 'formData', 'params', 'attachments']
          fields.map(_key => {
            if(item.hasOwnProperty(_key)) {
              item[_key] = this.filterKeyValue(item[_key], _key)
            }
          })
          if(item.hasOwnProperty('extractVars')){
            item.extractVars = this.extractVarsFilter(item.extractVars);
          }
        });
        window.sessionStorage.setItem('nodeData', prune(a));
        this.props.onCallback && this.props.onCallback();
      }
    });
  }
  // 节点数据确认修改
  editOk = (form, id) => {
    const { graph, node } = this.state;
    let data = _.cloneDeep(this.state.data);
    const operatorList = this.props.operatorList;
    let extractIsError = node.extractVars.some(it => it.hasOwnProperty('showError'));
    form.validateFields((error, value) => {
      if (!error && !extractIsError) {
        let details = {};
        let stepType = data.nodes[data.nodes.findIndex(x => x.id === id)].stepType;
        for(let k in value){
          details[k] = value[k]
        }
        if (stepType === 'HTTP') {
          details.url = this.convert(value.url);
          if (value.cookieText) {
            details.cookieText = value.cookieText || '';
            delete details.cookies;
          } else {
            details.cookies = value.cookies;
            delete details.cookieText;
          }
          if (value.bodyType === 'JSON' || value.bodyType === 'TEXT') {
            details.body = value.body;
            details.formData = [];
          } else {
            details.formData = value.formData;
          }
        } else if (stepType === 'DUBBO') {
          details.ip = this.convert(value.ip);
          details.className = this.convert(value.dubboInterface);
          details.method = this.convert(value.method);
          details.group = this.convert(value.group);
        }
        delete details.label;
        data.nodes.forEach((item,index) => {
          if (item.id === id) {
            for (let k in details) {
              item[k] = details[k];
            }
            item.name = value.label;
            if (value.variable) {
              item.condition = {
                rules: [
                  {
                    variable: this.convert(value.variable),
                    operator: value.operator,
                    value: this.convert(value.value),
                    regex: value.regex,
                  }
                ]
              };
            } else {
              item.condition = { rules: [] };
            }
            item.extractVars = value.extractVars;
            item.preStepScript = value.preStepScript;
            item.postStepScript = value.postStepScript;
            item.delay = value.delay;
            item.alias = value.alias;
            if(!details.hasOwnProperty('cookieText')) delete item.cookieText;
            if(!details.hasOwnProperty('cookies')) delete item.cookies;
          }
          // 数据判判断校验
          let fields = ['headers', 'urlParams', 'cookies', 'formData', 'params', 'attachments']
          fields.map(_key => {
            if(item.hasOwnProperty(_key)) {
              item[_key] = this.filterKeyValue(item[_key], _key)
            }
          })
          if(item.hasOwnProperty('extractVars')){
            item.extractVars = this.extractVarsFilter(item.extractVars);
          }
          data.nodes[index] = item
        });
        data.edges.forEach(item => {
          if (item.target === id) {
            let desc = value.operator ? operatorList.find(x => x.name === value.operator).desc : ''; // 判断条件
            if (desc) {
              item.label = (value.variable + desc + (value.value || value.regex)).slice(0, 15) + '...';
            } else {
              delete item.label;
            }
          }
        });
        this.setState({ data }, () => {
          graph.data(data);
          graph.render();
          this.onClose(form);
        });
        window.sessionStorage.setItem('nodeData', prune(data));
        this.props.onCallback && this.props.onCallback();
      }
    });
  }
  // 删除当前点击节点
  deleteNode = (item, form) => {
    const { data, graph } = this.state;
    // 当前id指向的
    const itemSourceList = data.edges.filter(ele => ele.source === item.id);
    // 当前指向id的
    const itemTargetList = data.edges.filter(ele => ele.target === item.id);
    // 判断当前删除节点是否有子级，把子级链接到父级
    if(itemSourceList.length > 0 && itemTargetList.length > 0){
      itemSourceList.map(item => {
        itemTargetList.map(target => {
          if(data.edges.findIndex(edg => edg.source === target.source && edg.target === item.target) < 0){
            data.edges.push({
              source: target.source,
              target: item.target,
              label: item.label
            })
          }
        })
      })
    }
    if(item.id === data.nodes[0].id){
      let targetId = data.edges.find(ele => ele.source === item.id).target;
      let index = data.nodes.findIndex(ele => ele.id === targetId);
      data.nodes.unshift(data.nodes.splice(index , 1)[0]);
    }
    data.nodes = data.nodes.filter(ele => ele.id !== item.id);
    data.edges = data.edges.filter(ele => ele.source !== item.id && ele.source !== undefined);
    data.edges = data.edges.filter(ele => ele.target !== item.id);
    this.setState({ isDelete: true });
    graph.data(data);
     // graph.changeData();
    graph.render();
    let _data = _.cloneDeep(data);
    window.sessionStorage.setItem('nodeData', prune(_data));
    this.props.onCallback && this.props.onCallback();
    this.onClose(form);
  }

  render() {
    const { node, addVisible, childVisible, data, useAddItemName, event, title, insTypeList, allList } = this.state;
    let tempNode = _.cloneDeep(node);
    return (
      <Fragment>
        <div style={{ float: 'right', width: 'calc(100% - 240px)' }} id="mountNode" className="nodeCanvas"></div>
        {data.nodes.length === 0 && <Button style={{ position: 'absolute', top: 250, left: 400 }} icon="plus" onClick={this.addFirstNode}>添加步骤</Button>}
        <AddEditNodeDrawer
          addVisible={addVisible}
          childVisible={childVisible}
          allList={allList}
          data={data}
          node={tempNode}
          event={event}
          useAddItemName={useAddItemName}
          title={title}
          insTypeList={insTypeList}
          sideAllData={this.props.sideAllData}
          onClose={this.onClose}
          editOk={this.editOk}
          showChildDrawer={this.showChildDrawer}
          onChildrenDrawerClose={this.onChildrenDrawerClose}
          addNode={this.addNode}
          deleteNode={this.deleteNode}
          type={this.props.type}
        />
      </Fragment>
    );
  }
}
export default connect((state) => ({
  dubboParamType: state.initialValueObj.dubboParamType,
  operatorList: state.initialValueObj.operatorList,
}), {
  ...intialValueAction,
  ...nodeDataAction
})(XMind);