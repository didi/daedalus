/* eslint-disable */
export default {
  getQueryString(name, search = window.location.search) {
    let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    let r = search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
  },
  getHashQuery(name) {
    let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    let hashSearch = window.location.hash.split('?')[1];
    if (hashSearch) {
      let r = hashSearch.match(reg);
      if (r != null) return unescape(r[2]);
      return null;
    }
    return null;
  }
};