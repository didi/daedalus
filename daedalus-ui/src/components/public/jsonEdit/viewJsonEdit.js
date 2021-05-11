/* eslint-disable */
/** json编辑器 */
import React, { Component } from 'react';
import { JsonEditor as Editor } from 'jsoneditor-react';
import 'jsoneditor-react/es/editor.min.css';
import './json-edit.scss';

export default class ViewJsonEditor extends Component {
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
        mode={'preview'}
        allowedModes={['code', 'form', 'text', 'tree', 'view', 'preview']}
      />
    );
  }
}