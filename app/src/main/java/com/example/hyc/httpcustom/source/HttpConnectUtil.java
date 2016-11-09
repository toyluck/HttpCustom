package com.example.hyc.httpcustom.source;



import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hyc on 16-11-9.
 */
public class HttpConnectUtil {

    public static String execute(Request request) throws IOException {

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


    private static String get(Request request) throws IOException {
        URL               url;
        HttpURLConnection conn;
        try {
            url = new URL(request.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(request.getReadTimeout());
            conn.setConnectTimeout(request.getTimeout());
            conn.setRequestMethod(request.getMethodName());
            conn.setRequestProperty("Content-Type", "application/json");


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            ICallback iCallback = request._iCallback;
            Type[]    interfaces = iCallback.getClass().getGenericInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                System.out.print(interfaces[i]+" ");
            }
            Type      genericSuperclass = iCallback.getClass().getGenericSuperclass();
            System.out.println("genericSuperclass = " + genericSuperclass);
            return getResponse(conn);
        } catch (IOException err) {

            throw new IOException(err);
        }

    }

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

    private static String post(Request request) throws IOException {
        URL               url ;
        HttpURLConnection conn;
        try {
            url = new URL(request.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(request.getReadTimeout());
            conn.setConnectTimeout(request.getTimeout());
            conn.setRequestMethod(request.getMethodName());
            //这里请求头要修改  不能为json
//            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(request.getContent().getBytes());
            os.flush();
            return getResponse(conn);
        } catch (IOException err) {
            throw new IOException(err);
        }
    }
}