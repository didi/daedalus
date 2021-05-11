import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { hot } from 'react-hot-loader';
import { Layout } from 'antd';
import { HashRouter as Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import Header from './layout/header';
import Footer from './layout/footer';
import Affix from './layout/affix';
import HeaderInput from './layout/headerInput';
import RouterConFig from './router';
import store from './store';
import './index.scss';
import 'antd/dist/antd.css';
import './static/icon_font/iconfont.css';

class APP extends Component  {

  render(){
    return (
      <Router>
        <Layout id="App">
          <Header />
          <HeaderInput />
          <RouterConFig />
          <Footer />
          <Affix />
        </Layout>
      </Router>
    );
  }
}

let Entry = hot(module)(APP);
ReactDOM.render(
  <Provider store={store}>
    <Entry />
  </Provider>,
  document.getElementById('root')
);