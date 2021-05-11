/* eslint-disable */
/** 提及输入框 */
import React from 'react';
import { MentionsInput, Mention } from 'react-mentions';
import { message } from 'antd';
import './index.scss';

export default class InputMentions extends React.Component {

  handleChange = e => {
    const { onChange } = this.props;
    if (onChange) {
      if (/#\{\s*\}/g.test(e.target.value)) {
        message.info('请输入有效变量名，如：#{demo}');
      } else {
        onChange(e.target.value.replace(/ #/g, '#'));
      }
    }
  };

  render() {
    let { optionList, value, disabled, placeholder } = this.props;
    let mentionValue = value ? value.replace(/\S#/g, word => word.replace('#', ' #')) : '';
    const list = [];
    if (optionList) {
      optionList.forEach(item => {
        list.push({ id: item.key, display: item.value });
      });
    }
    // 输入框整体的样式
    let defaultStyle = {
      control: {
        backgroundColor: disabled ? '#f5f5f5' : '#fff',
        fontSize: 14,
        fontWeight: 'normal',
        color: disabled ? 'rgba(0, 0, 0, 0.25)' : 'rgba(0, 0, 0, 0.65)',
        lineHeight: '32px',
      },
      // 多行样式
      '&multiLine': {
        control: {
          // fontFamily: 'inherit',
          minHeight: 32,
        },
        highlighter: {
          padding: '0 11px',
          border: '1px solid transparent',
        },
        input: {
          padding: '0 11px',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
        },
      },
      // 单行样式
      '&singleLine': {
        display: 'inline-block',
        width: 180,
        highlighter: {
          padding: 1,
          border: '2px inset transparent',
        },
        input: {
          padding: 1,
          border: '2px inset',
        },
      },
      // 联想数据
      suggestions: {
        zIndex: 999,
        list: {
          backgroundColor: 'white',
          border: '1px solid rgba(0,0,0,0.15)',
          fontSize: 14,
          maxHeight: '200px',
          overflowY: 'auto',
        },
        item: {
          padding: '5px 15px',
          borderBottom: '1px solid rgba(0,0,0,0.15)',
          '&focused': {
            backgroundColor: '#cee4e5',
          },
        },
      },
    };
    // 联想区域样式
    let mentionStyle = {
      backgroundColor: '#88c6ff',
      // border: '1px solid #1890ff',
      borderRadius: '4px',
      boxShadow: '0 2px 0 rgba(0, 0, 0, 0.045)',
      textShadow: '0 -1px 0 rgba(0, 0, 0, 0.12)',
      // color: 'rgba(0,0,0,0)',
      padding: '3px 0px',
      // marginLeft: '5px',
      // box-sizing: border-box;
    };
    return (
      <MentionsInput
        value={mentionValue}
        onChange={this.handleChange}
        allowSpaceInQuery
        disabled={disabled}
        placeholder={placeholder}
        style={defaultStyle}
        className="mentionInput"
      >
        <Mention
          trigger={/(\#([A-Za-z0-9_.]*))$/}
          markup="#{__display__}"
          singleLine={true}
          data={list}
          style={mentionStyle}
        />
      </MentionsInput>
    );
  }
}