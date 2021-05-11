/** 代码编辑器 */
import React from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/theme/monokai.css';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import 'codemirror/addon/edit/closebrackets.js';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/sql-hint.js';
import 'codemirror/addon/hint/javascript-hint.js';
import 'codemirror/addon/hint/anyword-hint.js';
import 'codemirror/addon/edit/matchbrackets.js';
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/mode/sql/sql.js';
import 'codemirror/mode/groovy/groovy.js';
import 'codemirror/keymap/sublime';
import './index.scss';

export default class CodeMirrors extends React.Component {

  handleChange = value => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(value);
    }
  };

  render() {
    const { value, options, data } = this.props;
    return (
      <CodeMirror
        value={value}
        options={options}
        onBeforeChange={(editor, data, value) => {
          this.handleChange(value);
        }}
        // onInputRead={editor => {
        //   editor.showHint();
        // }}
        onInputRead={(cm, obj) => {
          if (obj.text[0] !== ' ') {
            cm.setOption('hintOptions', {
              tables: data,
              completeSingle: false
            });
            cm.execCommand('autocomplete');
          }
        }}
      />
    );
  }
}