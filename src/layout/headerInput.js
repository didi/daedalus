/** 头部搜索框 */
import React from 'react';
import { withRouter } from 'react-router-dom';
import { Select, Spin } from 'antd';
import debounce from 'lodash/debounce';
import request from '@/util/request';
import './layout.scss';
const { Option } = Select;

class HeaderInput extends React.Component {

  constructor(props) {
    super(props);
    this.lastFetchId = 0;
    this.fetchUser = debounce(this.fetchUser, 800);
  }

  state = {
    data: [],
    value: [],
    fetching: false,
  };

  fetchUser = value => {
    this.setState({ data: [], fetching: true });
    request(`/pipeline/search`, {
      method: 'GET',
      params: {
        key: value
      }
    }).then(res => {
      if (res.success === true) {
        this.setState({ data: res.data, fetching: false });
      }
    });
  };

  handleChange = value => {
    this.setState({
      value,
      data: [],
      fetching: false,
    });
  };

  render() {
    const { fetching, data, value } = this.state;
    return (
      <Select
        showSearch
        allowClear
        labelInValue
        value={value}
        placeholder="搜索"
        notFoundContent={fetching ? <Spin size="small" /> : null}
        filterOption={false}
        onSearch={this.fetchUser}
        onChange={this.handleChange}
        className="header_input"
        showArrow={false}
      >
        {data.map(d => (
          <Option
            key={d.id}
            value={d.id}
          >
            <a
              href={`#/function/${d.id}`}
              target="_blank"
              style={{ color: 'rgba(0, 0, 0, 0.65)', display: 'block' }}
              rel="noreferrer"
            >
              {d.name}</a>
          </Option>
        ))}
      </Select>
    );
  }
}

export default withRouter(HeaderInput);