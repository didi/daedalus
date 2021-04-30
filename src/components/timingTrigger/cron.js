/** 定时cron */
import React, { Component, Fragment } from 'react';
import { Input, Button, Modal } from 'antd';
import Cron from 'antd-cron';

export default class CronTime extends Component {

  state = {
    cronModel: false,
  }
  // cron组件change
  onCronChange = value => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(value);
    }
  }
  // 生成cron的model取消
  generateCronCancel = () => {
    this.setState({ cronModel: false });
  }
   // 生成cron的model确认
   generateCronOk = () => {
     this.generateCronCancel();
   }

   render() {
     const { value } = this.props;
     const { cronModel } = this.state;
     return (
       <Fragment>
         <Input
           style={{ width: '200px', marginRight: 20 }}
           placeholder="请输入定时时间"
           value={value}
         />
         <Button
           type="primary"
           onClick={() => this.setState({ cronModel: true })}
         >
          点击生成
         </Button>
         {cronModel &&
         <Modal
           title="定时时间"
           visible={true}
           onOk={this.generateCronOk}
           onCancel={this.generateCronCancel}
           okText="确认"
           cancelText="取消"
           width="700px"
         >
           <Cron onChange={this.onCronChange} value={value} />
         </Modal>
         }
       </Fragment>
     );
   }
}