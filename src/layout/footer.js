/** 底部 */
import React, { Component } from 'react';
import { Layout } from 'antd';
const { Footer } = Layout;

export default class Footers extends Component {

  render() {
    return (
      <Footer style={{ textAlign: 'center' }}>- Daedalus -</Footer>
    );
  }
}