import React, { Component } from 'react';
import { Form, Input, Radio, Modal } from 'antd';
import Mapping from '../../public/mapping';
const { oneLineLayout } = Mapping;
const { TextArea } = Input;

class ExportHttpModal extends Component {

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        title={'导入HTTP接口'}
        visible={true}
        onOk={() => this.props.exportHttpOk(this.props.form)}
        onCancel={this.props.exportHttpCancel}
        okText="确认"
        cancelText="取消"
      >
        <Form.Item label="导入类型" {...oneLineLayout} >
          {getFieldDecorator('exportType', {
            rules: [{ required: true, message: '请选择！' }],
            initialValue: 'CURL',
          })(
            <Radio.Group>
              <Radio value={'CURL'}>CURL</Radio>
              {/* <Radio value={'Swagger'}>Swagger</Radio> */}
            </Radio.Group>
          )}
        </Form.Item>
        <Form.Item label="导入内容" {...oneLineLayout} >
          {getFieldDecorator('exportContent', {
            rules: [{ required: true, message: '请填写！' }],
            initialValue: '',
          })(
            <TextArea
              autoSize={{ minRows: 5, maxRows: 8 }}
              placeholder="请输入CURL"
            />
          )}
        </Form.Item>
      </Modal>
    );
  }
}

export default Form.create()(ExportHttpModal);