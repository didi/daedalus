/* eslint-disable */
import React, { Component } from 'react';
import { Button, Input, Select, Form, Tooltip, Modal, Radio, Switch, Spin, Icon } from 'antd';
import { connect, } from 'react-redux';
import intialValueAction from '../../store/action/intialValueAction';
import Mapping from '../public/mapping';
import request from '@/util/request';
import debounce from 'lodash/debounce';
const { fiveLineLayout } = Mapping;
const { TextArea } = Input;
const { Option } = Select;

class AssemblyLineModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      allPerson: [],
      fetching: false,
    };
    this.lastFetchId = 0;
    this.getStaff = debounce(this.getStaff, 800);
  }

  getStaff = str => {
    this.setState({ allPerson: [], fetching: true });
    request(`/api/thirdservice/user/fuzzy?searchStr=${str}`, {
      method: 'GET',
    }).then(res => {
      if (res.code === 200) {
        this.setState({ allPerson: res.data, fetching: false });
      } else {
        this.setState({ allPerson: [], fetching: false });
      }
    });
  }

  render() {
    const { modalTitle, switchValue, modalVisible, type, modalData } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { allPerson, fetching } = this.state;
    const bizLines = this.props.bizLine || [];
    const tagList = this.props.tag || [];
    const envList = this.props.envList || [];
    return (
      <Modal
        title={modalTitle}
        visible={modalVisible}
        closable={false}
        width="700px"
        footer={
          <div>
            {
              type === 'add' && modalTitle === '新建流水线' ? <Tooltip placement="top" title="取消以后将会返回工作台">
                <Button onClick={() => this.props.handleCancel(this.props.form)}>取消</Button>
              </Tooltip> :
              <Button onClick={() => this.props.handleCancel(this.props.form)}>取消</Button>
            }
            <Button
              onClick={() => this.props.handleOk(this.props.form)}
              type="primary"
            >
              确定
            </Button>
          </div>
        }
      >
        <Form.Item label="流水线名称" {...fiveLineLayout} >
          {getFieldDecorator('name', {
            rules: [{ required: true, message: '请输入流水线名称' }],
            initialValue: modalTitle !== '新建流水线' ? modalData.name : ''
          })(
            <Input placeholder="请输入流水线名称" />
          )}
        </Form.Item>
        <Form.Item label="业务线" {...fiveLineLayout} >
          {getFieldDecorator('bizLine', {
            rules: [{ required: true, message: '请选择业务线' }],
            initialValue: modalTitle !== '新建流水线' ? modalData.bizLine : undefined
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
        <Form.Item
          label={
            <span>
              <span>多环境支持</span>
              <Tooltip title="开启后，可以使用环境组中的变量，运行时可以选择不同环境（预发、测试等）">
                <Icon type="question-circle" style={{ fontSize: '15px', marginLeft: 4, cursor: 'pointer' }} />
              </Tooltip>
            </span>
          }
          {...fiveLineLayout}
        >
          {getFieldDecorator('envSupport', {
            initialValue: modalTitle !== '新建流水线' ? modalData.envSupport : false,
            valuePropName: 'checked'
          })(
            <Switch
              checkedChildren="开"
              unCheckedChildren="关"
              onChange={checked => this.props.switchChange(checked)}
            />
          )}
        </Form.Item>
        {
          switchValue && <Form.Item label="环境组选择" {...fiveLineLayout} >
            {getFieldDecorator('envGroupId', {
              rules: [{ required: true, message: '请选择环境组' }],
              initialValue: modalTitle !== '新建流水线' ? modalData.envGroupId : undefined
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择环境组"
                allowClear
                showSearch
                optionFilterProp="children"
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }
              >
                {
                  envList && envList.map(item => (
                    <Option key={item.id} value={item.id}>{item.name}</Option>
                  ))
                }
              </Select>
            )}
          </Form.Item>
        }
        {
          !switchValue && <Form.Item label="运行环境" {...fiveLineLayout} >
            {getFieldDecorator('online', {
              rules: [{ required: true, message: '请选择运行环境' }],
              initialValue: modalTitle !== '新建流水线' ? modalData.online : undefined
            })(
              <Select
                style={{ width: '100%' }}
                placeholder="请选择运行环境"
              >
                <Option key="true" value={true}>线上</Option>
                <Option key="false" value={false}>线下</Option>
              </Select>
            )}
          </Form.Item>
        }
        <Form.Item label="运行权限" {...fiveLineLayout} >
          {getFieldDecorator('permission.visible', {
            initialValue: (modalTitle !== '新建流水线' && modalData.permission) ? String(modalData.permission.visible) : '0'
          })(
            <Radio.Group>
              <Radio value="0">所有人</Radio>
              <Radio value="1">创建人</Radio>
              {/*<Radio value="2">选择人</Radio>*/}
            </Radio.Group>
          )}
        </Form.Item>
        {/*
          getFieldValue('permission.visible') === '2' && <Form.Item label="运行权限人" {...fiveLineLayout} >
            {getFieldDecorator('permission.runners', {
              rules: [{ required: true, message: '请选择运行权限人！' }],
              initialValue: (modalTitle !== '新建流水线' && modalData.permission) ? modalData.permission.runners : undefined,
            })(
              <Select
                showSearch
                allowClear
                mode="multiple"
                style={{ width: '100%' }}
                placeholder={'请选择运行权限人！'}
                notFoundContent={fetching ? <Spin size="small" /> : null}
                filterOption={false}
                onSearch={str => this.getStaff(str)}
              >
                {allPerson && allPerson.map(item => (
                  <Option key={item.userName} value={item.eMail.split('@')[0]}>
                    {item.userName + '(' + item.eMail.split('@')[0] + ')'}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
        */}
        <Form.Item label="编辑权限" {...fiveLineLayout} >
          {getFieldDecorator('permission.editable', {
            initialValue: (modalTitle !== '新建流水线' && modalData.permission) ? String(modalData.permission.editable) : '0'
          })(
            <Radio.Group>
              <Radio value="0">所有人</Radio>
              <Radio value="1">创建人</Radio>
              {/*<Radio value="2">选择人</Radio>*/}
            </Radio.Group>
          )}
        </Form.Item>
        {/*
          getFieldValue('permission.editable') === '2' && <Form.Item label="编辑权限人" {...fiveLineLayout} >
            {getFieldDecorator('permission.editors', {
              rules: [{ required: true, message: '请选择编辑权限人！' }],
              initialValue: (modalTitle !== '新建流水线' && modalData.permission) ? modalData.permission.editors : undefined,
            })(
              <Select
                showSearch
                allowClear
                mode="multiple"
                style={{ width: '100%' }}
                placeholder={'请选择编辑权限人！'}
                notFoundContent={fetching ? <Spin size="small" /> : null}
                filterOption={false}
                onSearch={str => this.getStaff(str)}
              >
                {allPerson && allPerson.map(item => (
                  <Option key={item.userName} value={item.eMail.split('@')[0]}>
                    {item.userName + '(' + item.eMail.split('@')[0] + ')'}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
         */}
        <Form.Item label="标签" {...fiveLineLayout} >
          {getFieldDecorator('tags', {
            initialValue: modalTitle !== '新建流水线' ? modalData.tags : undefined
          })(
            <Select
              mode="tags"
              style={{ width: '100%' }}
              placeholder="请选择标签"
            >
              {
                tagList && tagList.length > 0 && tagList.map(item => (
                  <Option key={item} value={item}>{item}</Option>
                ))
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="备注" {...fiveLineLayout} >
          {getFieldDecorator('remark', {
            initialValue: modalTitle !== '新建流水线' ? modalData.remark : ''
          })(
            <TextArea
              autoSize={{ minRows: 3 }}
              placeholder="请填写本流水线用户、用法、解释等"
            />
          )}
        </Form.Item>
      </Modal>
    );
  }
}

export default connect((state) => ({
  bizLine: state.initialValueObj.bizLine,
  tag: state.initialValueObj.tag,
  envList: state.initialValueObj.envList,
}), intialValueAction)(
  Form.create()(AssemblyLineModal)
);