package com.example.hyc.httpcustom;

import android.support.v7.app.AppCompatActivity;

import com.example.hyc.httpcustom.source.AppException;
import com.example.hyc.httpcustom.source.GlobalRequestErrListerner;

import java.net.HttpURLConnection;

/**
 * Created by hyc on 2016/11/10.
 */

public class BaseActivity extends AppCompatActivity implements GlobalRequestErrListerner {

    @Override
    public boolean globalCatchException(AppException e) {
        if (e.getStatuCode() == HttpURLConnection.HTTP_NOT_AUTHORITATIVE) {
            // TODO: 2016/11/10 进行操作
            return true;
        }

        return false;
    }
}
