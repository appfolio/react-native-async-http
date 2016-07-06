package com.smccz.asynchttp;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import cz.msebera.android.httpclient.Header;

import com.facebook.react.bridge.WritableMap;
import com.loopj.android.http.*;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

public class AsyncHttpAndroid extends ReactContextBaseJavaModule {
    private static AsyncHttpClient httpClient = new AsyncHttpClient();
    private PersistentCookieStore cookieStore;
    private final ReactApplicationContext mReactContext;

    public AsyncHttpAndroid(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        this.cookieStore = new PersistentCookieStore(reactContext);
        httpClient.setCookieStore(this.cookieStore);
        httpClient.addHeader("X-Requested-With", "XMLHttpRequest");
    }

    @Override
    public String getName() {
        return "AsyncHttp";
    }

    @ReactMethod
    public void get(String url, ReadableMap headers, final Callback cb) {

        try {
            clearHeaders();
            ReadableMapKeySetIterator headerIterator = headers.keySetIterator();

            while (headerIterator.hasNextKey()) {
                String key = headerIterator.nextKey();
                httpClient.addHeader(key, headers.getString(key));
            }

            httpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(null, responseMap);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable error) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(error.toString(), responseMap);
                }
            });
        } catch(Exception e) {
            cb.invoke("pro error" + e.getMessage());
        }
    }

    @ReactMethod
    public void post(String url, ReadableMap data, ReadableMap headers, final Callback cb) {

        try {
            clearHeaders();
            RequestParams params = new RequestParams();
            ReadableMapKeySetIterator dataIterator = data.keySetIterator();

            while (dataIterator.hasNextKey()) {
                String key = dataIterator.nextKey();
                params.put(key, data.getString(key));
            }

            ReadableMapKeySetIterator headerIterator = headers.keySetIterator();

            while (headerIterator.hasNextKey()) {
                String key = headerIterator.nextKey();
                httpClient.addHeader(key, headers.getString(key));
            }

            httpClient.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(null, responseMap);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable error) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(error.toString(), responseMap);
                }
            });

        } catch(Exception e) {
            cb.invoke("pro error" + e.getMessage());
        }
    }

    @ReactMethod
    public void clearCookies() {
        this.cookieStore.clear();
    }

    private void clearHeaders() {
        httpClient.removeAllHeaders();
        httpClient.addHeader("X-Requested-With", "XMLHttpRequest");
    }

    private WritableMap populateResponseData(int statusCode, Header[] headers, byte[] response) {
        WritableMap headersMap = Arguments.createMap();
        if (headers == null) {
            headersMap = null;
        } else {
            for (Header header: headers) {
                headersMap.putString(header.getName().toLowerCase(), header.getValue());
            }
        }

        WritableMap responseMap = Arguments.createMap();

        responseMap.putInt("status", statusCode);
        responseMap.putMap("headers", headersMap);
        if (response == null) {
            responseMap.putString("body", null);
        } else {
            responseMap.putString("body", new String(response));
        }
        return responseMap;
    }
}
