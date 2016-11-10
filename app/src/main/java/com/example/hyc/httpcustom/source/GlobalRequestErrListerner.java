package com.example.hyc.httpcustom.source;

/**
 * Created by hyc on 2016/11/10.
 */

public interface GlobalRequestErrListerner {
    boolean globalCatchException(AppException e);
}
