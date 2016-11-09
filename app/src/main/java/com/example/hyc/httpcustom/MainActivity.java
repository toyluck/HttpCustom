package com.example.hyc.httpcustom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hyc.httpcustom.models.User;
import com.example.hyc.httpcustom.source.Callback;
import com.example.hyc.httpcustom.source.ICallback;
import com.example.hyc.httpcustom.source.Request;
import com.example.hyc.httpcustom.source.RequestTask;
import com.example.hyc.httpcustom.utils.esprossoUtils.EsprossoIdelResource;

import static com.example.hyc.httpcustom.source.Request.RequestMethod.POST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        request((Button) v);
    }

    private void request(final Button v) {
        EsprossoIdelResource.increment();
        String path = "http://www.163.com/";
        path = "http://api.stay4it.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=123456";

        Request request = new Request.Build().baseUrl(path).method(POST).setContent(content).build();
//        String  content = HttpConnectUtil.execute(request);
        request.setiCallback(new Callback<User>() {


            @Override
            public void onSuccessed(User response) {
                System.out.println("response = " + response);
                Button tv = v;
                tv.setText("response");
            }

            @Override
            public void onFailure(Exception err) {
                System.out.println("err.getMessage() = " + err.getMessage());
            }
        });
        new RequestTask().addTask(request);
    }
}
