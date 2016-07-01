'use strict';

var AsyncHttpClient = require('react-native').NativeModules.AsyncHttp;

var AsyncHttp = {
  get(url, headers, callback) {
    var postHeaders = {};
    for (var key in headers) {
      var attrName = key;
      var attrValue = String(headers[key]);
      postHeaders[attrName] = attrValue;
    }
    AsyncHttpClient.get(url, postHeaders, callback);
  },

  post(url, params, headers, callback) {
    var postparams = {};
    for (var key in params) {
      var attrName = key;
      var attrValue = String(params[key]);
      postparams[attrName] = attrValue;
    }
    var postHeaders = {};
    for (var key in headers) {
      var attrName = key;
      var attrValue = String(headers[key]);
      postHeaders[attrName] = attrValue;
    }
    AsyncHttpClient.post(url, postparams, postHeaders, callback);
  },

  clearCookies() {
    AsyncHttpClient.clearCookies();
  }
};

module.exports = AsyncHttp;
