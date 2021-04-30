/** 超级输入框 */
import React from 'react';
import { Select, Popover } from 'antd';
const { Option } = Select;

export default class Selects extends React.Component {

  handleCurrencyChange = currency => {
    this.triggerChange(currency);
  };

  triggerChange = changedValue => {
    const { onChange } = this.props;
    if (onChange) {
      onChange([
        ...changedValue,
      ]);
    }
  };

  render() {
    const { optionList, value, placeholder, disabled } = this.props;
    return (
      <Select
        mode="tags"
        placeholder={placeholder ? placeholder : '请填选...'}
        style={{ width: '100%' }}
        optionLabelProp="label"
        className="changePopover"
        value={value || undefined}
        onChange={this.handleCurrencyChange}
        disabled={disabled ? true : false}
      >
        {
          optionList && optionList.length > 0 && optionList.map(item => (
            <Option key={item.key} value={`#{` + item.value + '}'} label={
              <Popover
                placement="bottom"
                content={item.name}
                trigger="click"
              >
                <div
                  style={{ color: 'white', background: '#1890FF', borderRadius: ' 2px', padding: '0 5px', cursor: 'pointer' }}
                >
                  {item.value}
                </div>
              </Popover>
            }>
              {item.value} <span style={{ float: 'right' }}>({item.name})</span>
            </Option>
          ))
        }
      </Select>
    );
  }
}