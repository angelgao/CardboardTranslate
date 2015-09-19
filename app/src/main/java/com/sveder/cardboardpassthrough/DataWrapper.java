package com.sveder.cardboardpassthrough;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by david on 15-09-19.
 */
public class DataWrapper {
    public Data data;

    public static DataWrapper fromJson(String s) {
        return new Gson().fromJson(s, DataWrapper.class);
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}
class Data {
    public List<Translation> translations;
}
class Translation {
    public String translatedText;
}