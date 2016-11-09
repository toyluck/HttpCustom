package com.example.hyc.httpcustom;

import com.example.hyc.httpcustom.source.ICallback;
import com.example.hyc.httpcustom.source.Request;
import com.example.hyc.httpcustom.source.RequestTask;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by hyc on 16-11-9.
 *
 */
public class HttpConnectUtilTest {
    @Test
    public void get() throws IOException {
        String path = "http://www.baidu.com";
        Request request = new Request.Build().baseUrl(path).build();
//        String  content = HttpConnectUtil.execute(request);
        request.setiCallback(new ICallback() {
            @Override
            public void onSuccessed(String response) {
                System.out.println("response = " + response);
            }

            @Override
            public void onFailure(Exception err) {
                System.out.println("err.getMessage() = " + err.getMessage());
            }
        });
        new RequestTask().addTask(request);
//        System.out.println(content);

    }

    @Test
    public void getErr() throws IOException {
        String path = "http://www.baidu.com";

        Request request = new Request.Build().baseUrl(path+"----").build();
//        String  content = HttpConnectUtil.execute(request);
        request.setiCallback(new ICallback() {
            @Override
            public void onSuccessed(String response) {
                System.out.println("response = " + response);
            }

            @Override
            public void onFailure(Exception err) {
                System.out.println("err.getMessage() = " + err.getMessage());
            }
        });
        new RequestTask().addTask(request);
//        System.out.println(content);

    }

    @Test
    public void post() throws IOException {

        String  path    = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String  content = "account=stay4it&&password=123456";
        Request request = new Request.Build().baseUrl(path).setContent(content).build();
        request.setiCallback(new ICallback() {
            @Override
            public void onSuccessed(String response) {
                System.out.println("response = " + response);
            }

            @Override
            public void onFailure(Exception err) {
                System.out.println("err.getMessage() = " + err.getMessage());
            }
        });
        new RequestTask().addTask(request);
//        String  result = HttpConnectUtil.execute(request);
//        System.out.println(result);

    }
}
