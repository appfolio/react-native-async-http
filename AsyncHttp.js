'use strict';

var AsyncHttpClient = require('react-native').NativeModules.AsyncHttp;

var AsyncHttp = {
  get(url, headers, callback) {
    var getHeaders = {};
    for (var key in headers) {
      var attrName = key;
      var attrValue = String(headers[key]);
      getHeaders[attrName] = attrValue;
    }
    AsyncHttpClient.get(url, getHeaders, callback);
  },

  post(url, params, headers, callback) {
    var postParams = {};
    for (var key in params) {
      var attrName = key;
      var attrValue = String(params[key]);
      postParams[attrName] = attrValue;
    }
    var postHeaders = {};
    for (var key in headers) {
      var attrName = key;
      var attrValue = String(headers[key]);
      postHeaders[attrName] = attrValue;
    }
    AsyncHttpClient.post(url, postParams, postHeaders, callback);
  },

  clearCookies() {
    AsyncHttpClient.clearCookies();
  }
};

module.exports = AsyncHttp;
