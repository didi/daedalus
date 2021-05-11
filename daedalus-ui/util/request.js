import 'whatwg-fetch';
import { notification } from 'antd';
import React from 'react';
import Util from './commonUtil';

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

let misBaseUrl = '';




function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  const errortext = codeMessage[response.status] || response.statusText;
  notification.error({
    message: `请求错误 ${response.status}: ${response.url}`,
    description: errortext,
  });
  const error = new Error(errortext);
  error.name = response.status;
  error.response = response;
  throw error;
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(url, options) {
  const defaultOptions = {
    credentials: 'include',
    headers: {
      'r-url': window.location.href
    }
  };
  const newOptions = { ...defaultOptions, ...options };
  if (
    newOptions.method === 'POST' ||
    newOptions.method === 'PUT' ||
    newOptions.method === 'DELETE'
  ) {
    if (!(newOptions.body instanceof FormData)) {
      newOptions.headers = {
        Accept: 'application/json',
        'Content-Type': 'application/json; charset=utf-8',
        ...newOptions.headers,
      };
      newOptions.body = JSON.stringify(newOptions.body);
    } else {
      newOptions.headers = {
        Accept: 'application/json',
        ...newOptions.headers,
      };
    }
  } else {
    if (newOptions.params !== undefined) {
      url += '?';
      for (let key in newOptions.params) {
        if (newOptions.params[key] !== undefined && newOptions.params[key] !== '') {
          url = url + key + '=' + newOptions.params[key] + '&';
        }
      }

      url = url.substring(0, url.length - 1);
    }
  }

  return new Promise((res, rej) => {
    fetch(misBaseUrl + url, newOptions)
      .then(checkStatus)
      .then(response => {
        if (newOptions.method === 'DELETE' || response.status === 204) {
          return response.text();
        }
        return response.json();
      })
      .then(response => {
        if (response.code === '300') {
          window.location.replace(response.data.redirectUrl);

          return;
        } else if (response.code === '302') {
          const { requestPath, redirectUrl, extra = [] } = response.data;
          notification.error({
            message: '权限认证',
            duration: 0,
            description: (
              <div style={{ wordBreak: 'break-all' }}>
                <code>{requestPath}</code>
                <p style={{ margin: 0 }}>权限不足，点击下方角色申请：</p>
                {extra.map((it) => (
                  <a
                    key={it.id}
                    href={redirectUrl + it.id}
                    style={{ display: 'block' }}
                    target="_blank"
                    rel="noreferrer"
                  >
                    {it.nameZh}
                  </a>
                ))}
              </div>
            )
          });

          return;
        }

        res(response);
      })
      .catch(e => {
        const status = e.name;
        if (status === 401) {
          return;
        }
        if (status === 403) {
          // handle the 403 status
          return;
        }
        if (status <= 504 && status >= 500) {
          // handle the 500 status
          return;
        }
        if (status >= 404 && status < 422) {
          // handle the 404 status
        }

        rej(e);
      });
  });
}
