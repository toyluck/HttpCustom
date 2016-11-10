package com.example.hyc.httpcustom;

import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hyc.httpcustom.models.User;
import com.example.hyc.httpcustom.source.AppException;
import com.example.hyc.httpcustom.source.FileCallback;
import com.example.hyc.httpcustom.source.JsonCallback;
import com.example.hyc.httpcustom.source.Request;
import com.example.hyc.httpcustom.source.RequestTask;
import com.example.hyc.httpcustom.utils.esprossoUtils.EsprossoIdelResource;

import java.io.File;
import java.io.IOException;

import static com.example.hyc.httpcustom.source.Request.RequestMethod.POST;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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
        path = "http://api.stay4it2.com/v1/public/core/?service=user.login";
        String content = "account=stay4it&password=1234562";
        String parent  = getExternalCacheDir() + "/download";
        new File(parent).mkdirs();
        File file = new File(parent, "user.txt");
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String  downloadpath = file.getAbsolutePath();
        Request request      = new Request.Build().baseUrl(path).method(POST).setContent(content).build();
        request.setGlobalRequestListerner(this);
        request.setiCallback(new FileCallback(downloadpath) {
            @Override
            public void onSuccessed(String response) {
                System.out.println("response = " + response);
                Button tv = v;
                tv.setText("response");
            }

            @Override
            public void onFailure(AppException err) {
                System.out.println("err.getMessage() = " + err.toString());

            }

            @UiThread
            @Override
            public void onProgress(long currerntCount, long totalCount) {
                super.onProgress(currerntCount, totalCount);
                System.out.println("currerntCount = [" + currerntCount + "], totalCount = [" + totalCount + "]");
                System.out.println("Thread.currentThread() = " + Thread.currentThread());
            }
        });

        new RequestTask().addTask(request);
    }
}
