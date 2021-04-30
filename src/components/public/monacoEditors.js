import React, { Component } from 'react';
import MonacoEditor from 'react-monaco-editor';

export default class MonacoEditors extends Component {
  constructor(props) {
    super(props);
    this.state = {

    };
  }
  onChangeHandle(value, e) {
    const { onChange } = this.props;
    if (onChange) {
      // console.log(value);
      onChange(value);
    }
  }
  editorDidMountHandle(editor, monaco) {
    // console.log('editorDidMount', editor);
    editor.focus();
  }
  render() {
    const { value, language, readOnly } = this.props;
    const options = {
      selectOnLineNumbers: true,
      // showFoldingControls: 'always', // 是否展示折叠
      // renderSideBySide: false,
      cursorStyle: 'underline',
      // cursorSmoothCaretAnimation: 'line',
      readOnly: readOnly ? true : false
    };
    return (
      <div>
        <MonacoEditor
          // width="400"
          height="300"
          theme="vs-dark"
          language={language}
          value={value}
          options={options}
          onChange={(value, e) => this.onChangeHandle(value, e)}
          editorDidMount={this.editorDidMountHandle}
        />
      </div>
    );
  }
}