import React, { Component } from 'react';
import { Card, Button, Row, Col, Form, Input } from 'antd';
import Mapping from '../public/mapping';
const { envOrExampleLayout } = Mapping;

class ExampleFilter extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  componentWillReceiveProps(nextProps) {
    if (this.props.type !== nextProps.type) {
      this.props.form.resetFields();
    }
  }
  // 查询
  search = () => {
    this.props.form.validateFields((error, value) => {
      if (!error) {
        this.props.getMyTable(value);
      }
    });
  }
  // 重置
  reset = () => {
    this.props.form.resetFields();
    this.props.getMyTable();
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { type } = this.props;
    return (
      <Card style={{ marginBottom: 20 }}>
        <h2>
          {type === 'http' && 'HTTP模板'}
          {type === 'mysql' && 'MYSQL实例'}
          {type === 'redis' && 'Redis/Fusion实例'}
          {type === 'es' && 'ES实例'}
          {type === 'registry' && '注册中心'}
        </h2>
        <Row>
          <Col span={10}>
            <Form.Item label="名称" {...envOrExampleLayout} >
              {getFieldDecorator('name', {
                initialValue: ''
              })(
                <Input placeholder="请输入..." />
              )}
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item label={type === 'http' ? 'URL' : 'IP'} {...envOrExampleLayout} >
              {getFieldDecorator('ip', {
                initialValue: ''
              })(
                <Input placeholder="请输入..." />
              )}
            </Form.Item>
          </Col>
          <Col span={4}>
            <Button type="primary" style={{ marginRight: 20, marginLeft: 20 }} onClick={this.search}>查询</Button>
            <Button onClick={this.reset}>重置</Button>
          </Col>
        </Row>
      </Card>
    );
  }
}
export default Form.create()(ExampleFilter);