/** 自定义form时间控件 */
import React from 'react';
import { DatePicker, TimePicker, } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import moment from 'moment';
import 'moment/locale/zh-cn';

export default class FormatPicker extends React.Component {

  handleCurrencyChange = (dataValue, dataString) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(dataString);
    }
  };

  render() {
    const { value, type, disabled } = this.props;
    let values = isNaN(value) ? value : Number(value);
    return (
      <React.Fragment>
        {
          type === 'DATE_PICKER' &&
          <DatePicker
            style={{ width: '60%' }}
            value={values ? moment(values, 'YYYY-MM-DD') : null}
            onChange={this.handleCurrencyChange}
            placeholder={disabled ? '' : '请输入...'}
            disabled={disabled ? true : false}
          />
        }
        {
          type === 'TIME_PICKER' &&
          <TimePicker
            style={{ width: '60%' }}
            value={values ? moment(values, 'HH:mm:ss') : null}
            onChange={this.handleCurrencyChange}
            placeholder={disabled ? '' : '请输入...'}
            disabled={disabled ? true : false}
          />
        }
        {
          type === 'DATE_TIME_PICKER' &&
          <DatePicker
            showTime
            locale={zhCN}
            style={{ width: '60%' }}
            value={values ? moment(moment(values).format('YYYY-MM-DD HH:mm:ss'), 'YYYY-MM-DD HH:mm:ss') : null}
            onChange={this.handleCurrencyChange}
            placeholder={disabled ? '' : '请输入...'}
            disabled={disabled ? true : false}
          />
        }
      </React.Fragment>
    );
  }
}