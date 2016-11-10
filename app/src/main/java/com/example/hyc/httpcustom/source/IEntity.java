package com.example.hyc.httpcustom.source;

import com.google.gson.stream.JsonReader;

import java.io.IOException;

/**
 * Created by hyc on 2016/11/10.
 */

public interface IEntity {
    void readObj(JsonReader reader) throws IOException;
}
