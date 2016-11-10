package com.example.hyc.httpcustom.source;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.logging.XMLFormatter;

/**
 * Created by hyc on 2016/11/9.
 */

public abstract class Callback<T> implements ICallback<T> {


    @Override
    public T parse(HttpURLConnection connection) {

        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return listRequest(connection);
            } else {
                return null;
            }
        } catch (Exception e) {
            onFailure(e);
            return null;
        }
    }

    public T listRequest(HttpURLConnection connection) throws IOException, JSONException {
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        OutputStream        os          = new ByteArrayOutputStream();
        byte[]              buf         = new byte[1024];
        int                 len;
        while ((len = inputStream.read(buf)) != -1) {
            os.write(buf, 0, len);
            os.flush();
        }
        inputStream.close();
        os.close();

        String rawResponse = os.toString();
        // 这里的泛型最终影响到的还是父类的
        Type   superType  = this.getClass().getGenericSuperclass();
        Type[] interfaces = superType.getClass().getGenericInterfaces();
        if (null == interfaces || interfaces.length == 0) {
            return (T) rawResponse;

        }
        // 拿到泛型
        Type       type       = ((ParameterizedType) superType).getActualTypeArguments()[0];
        Gson       gson       = new Gson();
        JSONObject jsonObject = new JSONObject(rawResponse);
        Object     obj        = gson.fromJson(rawResponse, type);
        return (T) obj;

    }


}
