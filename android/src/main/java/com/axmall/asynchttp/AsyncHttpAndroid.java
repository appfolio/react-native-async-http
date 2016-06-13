package com.smccz.asynchttp;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import cz.msebera.android.httpclient.Header;

import com.facebook.react.bridge.WritableMap;
import com.loopj.android.http.*;
import java.nio.charset.StandardCharsets;

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
    public void get(String url, final Callback cb) {
        try {
            httpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(null, responseMap);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable error) {
                    WritableMap responseMap = populateResponseData(statusCode, headers, response);
                    cb.invoke(error.getMessage(), responseMap);
                }
            });
        } catch(Exception e) {
            cb.invoke("pro error" + e.getMessage());
        }
    }

    @ReactMethod
    public void post(String url, ReadableMap data, final Callback cb) {
        try {
            RequestParams params = new RequestParams();
            ReadableMapKeySetIterator iterator = data.keySetIterator();

            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                params.put(key, data.getString(key));
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
                    cb.invoke(error.getMessage(), responseMap);
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

    private WritableMap populateResponseData(int statusCode, Header[] headers, byte[] response) {
        WritableMap responseMap = Arguments.createMap();
        WritableMap headersMap = Arguments.createMap();
        for (Header header: headers) {
            headersMap.putString(header.getName().toLowerCase(), header.getValue());
        }
        responseMap.putInt("status", statusCode);
        responseMap.putMap("headers", headersMap);
        responseMap.putString("body", new String(response));
        return responseMap;
    }
}
