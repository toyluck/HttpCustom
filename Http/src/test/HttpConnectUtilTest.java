package test;

import org.junit.Test;
import source.HttpConnectUtil;
import source.Request;

import java.io.IOException;

/**
 * Created by hyc on 16-11-9.
 */
public class HttpConnectUtilTest {
    @Test
    public void get() throws IOException {
        String path = "http://www.baidu.com";

        String content = HttpConnectUtil.execute(new Request.Build().baseUrl(path).build());
        System.out.println(content);

    }

    @Test
    public void post() throws IOException {
        String path = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&&password=123456";
        String result = HttpConnectUtil.execute(new Request.Build().baseUrl(path).setContent(content).build());
        System.out.println(result);

    }
}
