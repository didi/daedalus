/** 固钉 */
import React, { Component } from 'react';
import { Popover, Icon } from 'antd';
import './layout.scss';

export default class Affixss extends Component {

  componentDidMount() {
    window.addEventListener('scroll', this.handleScroll);
  }

  componentWillUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  }
  back2top = () => {
    const duration = 500;
    const target = document.documentElement.scrollTop || document.body.scrollTop;
    const step = (target / duration) * (1000 / 60);
    const timer = setInterval(() => {
      let curTop = document.documentElement.scrollTop || document.body.scrollTop;
      if (curTop <= 0) {
        clearInterval(timer);
        return;
      }
      curTop -= step;
      document.documentElement.scrollTop = document.body.scrollTop = curTop;
    }, 1000 / 60);
  }

  render() {
    return (
      <div className="allAffix">
        <Popover placement="leftBottom" content={
          <div style={{ overflow: 'hidden' }}>
            <div style={{ float: 'right', width: '100px', textAlign: 'center', fontSize: '12px' }}>
              <a
                href={'https://im.xiaojukeji.com/channel?uid=90840&token=c551cb139f889af1b90d06c10b7c59a8&id=77279076342276659'}
                target="_blank"
                rel="noreferrer"
              >
                <img
                  src="https://view.didistatic.com/static/dcms/4l8rsgpmkbisnom1_500x502.png"
                  style={{ width: '100px', height: '100px', margin: '0 0 10px 0' }}
                  alt={''}
                />
              </a>
              <span>DChat扫码进入用户群</span>
            </div>
          </div>
        } trigger="hover">
          <div className="affix">
            <Icon type="qrcode" />
          </div>
        </Popover>
        <div className="affix" onClick={this.back2top}>
          <Icon type="arrow-up" />
        </div>
      </div>
    );
  }
}