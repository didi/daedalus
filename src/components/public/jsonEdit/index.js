/* eslint-disable */
/** json编辑器 */
import React, { Component } from 'react';
import { JsonEditor as Editor } from 'jsoneditor-react';
import { message } from 'antd';
import 'jsoneditor-react/es/editor.min.css';
import './json-edit.scss';

export default class JsonEditor extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: this.props.value
    };
  }
  handleChange(e) {
    const { onChange } = this.props;
    if (onChange) {
      onChange(JSON.stringify(e));
    }
  }
  componentDidMount() {
    this.props.onBind && this.props.onBind(this)
  }
  checkValue = value => {
    try {
      JSON.parse(value);
    } catch (e) {
      return value;
    }
    return JSON.parse(value);
  }
  updateValue = (value) => {
    if (value) {
      value = this.checkValue(value);
      this.editor.jsonEditor.set(value);
    }
  }
  render() {
    let { value } = this.props;
    if (value) {
      value = this.checkValue(value);
    }
    return (
      <Editor
        ref={ref => this.editor = ref}
        value={value}
        mode={'code'}
        allowedModes={["code", "form", "text", "tree", "view", "preview"]}
        history={true} // 撤回
        search={true} // code类型搜索
        onChange={e => this.handleChange(e)}
        onError={err => {
          if (err) {
            message.error('输入错误');
          }
        }}
      />
    );
  }
}