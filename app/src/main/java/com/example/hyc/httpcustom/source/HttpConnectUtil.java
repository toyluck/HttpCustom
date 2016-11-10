package com.example.hyc.httpcustom.source;


import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;

/**
 * Created by hyc on 16-11-9.
 */
public class HttpConnectUtil {

    public static HttpURLConnection execute(Request request) throws AppException {
        //todo 进行一些debug的操作:
        if (!URLUtil.isNetworkUrl(request.getUrl())) {
            throw new AppException(AppException.ExceptionType.MANUL, "url does't fit!!!");
        }
        switch (request.getMethod()) {
            case GET:
                return get(request);
            case POST:
                return post(request);
            case DELETE:
                break;
            case PUT:
                break;
        }
        return null;
    }


    private static HttpURLConnection get(Request request) throws AppException {
        URL               url;
        HttpURLConnection conn;
        try {
            url = new URL(request.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(request.getReadTimeout());
            conn.setConnectTimeout(request.getTimeout());
            conn.setRequestMethod(request.getMethodName());
            conn.setRequestProperty("Content-Type", "application/json");
            request.checkCanceld();
            return conn;
        } catch (InterruptedIOException err) {
            throw new AppException(AppException.ExceptionType.REQ_TIMEOUT, err);
        } catch (IOException err) {
            throw new AppException(AppException.ExceptionType.SERVECE, err.getMessage());
        }

    }

    /**
     * 读取数据流
     *
     * @return
     * @throws IOException
     */
    private static String getResponse(URLConnection connection) throws IOException {
        InputStream  in  = new BufferedInputStream(connection.getInputStream());
        OutputStream os  = new ByteArrayOutputStream();
        byte[]       buf = new byte[1024];
        int          len;
        while ((len = in.read(buf)) != -1) {
            os.write(buf, 0, len);
            os.flush();
        }
        in.close();
        os.close();
        return os.toString();
    }

    private static HttpURLConnection post(Request request) throws AppException {
        URL               url;
        HttpURLConnection conn;
        try {
            url = new URL(request.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(request.getReadTimeout());
            conn.setConnectTimeout(request.getTimeout());
            conn.setRequestMethod(request.getMethodName());
            //这里请求头要修改  不能为json
            // conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
// TODO: 2016/11/10 添加多文件上传
            
            OutputStream os = conn.getOutputStream();
            os.write(request.getContent().getBytes());
            os.flush();
            request.checkCanceld();
            return conn;
        } catch (InterruptedIOException err) {
            throw new AppException(AppException.ExceptionType.REQ_TIMEOUT, err);
        } catch (IOException err) {
            throw new AppException(AppException.ExceptionType.SERVECE, err);
        }
    }
}
