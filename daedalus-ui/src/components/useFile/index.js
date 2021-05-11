/* eslint-disable */
/** 使用文档 */
import React, { Component } from 'react';

export default class Iframe extends Component {

  render() {

    return (
      <div style={{ height: '100vh' }}>
        <iframe src={'http://10.96.85.213:4000/'} width="100%" height="100%"></iframe>
      </div>
    );
  }
}