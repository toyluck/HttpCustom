package com.example.hyc.httpcustom.source;

import android.text.TextUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by hyc on 2016/11/9.
 */

public abstract class AbstractCallback<T> implements ICallback<T> {

    private String _path;

    public void setPath(String path) {
        _path = path;
    }


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

    private T listRequest(HttpURLConnection connection) throws IOException, JSONException {

        //在这个位置进行判断
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        OutputStream        os          = null;
        if (TextUtils.isEmpty(_path)) {
            os = new ByteArrayOutputStream();

        } else {
            os = new FileOutputStream(_path);
        }
        byte[] buf = new byte[1024];
        int    len;
        while ((len = inputStream.read(buf)) != -1) {
            os.write(buf, 0, len);
            os.flush();
        }
        inputStream.close();
        os.close();
        String rawResponse = os.toString();

        return bindData(rawResponse);

    }

    protected abstract T bindData(String rawResponse) throws JSONException;

}
