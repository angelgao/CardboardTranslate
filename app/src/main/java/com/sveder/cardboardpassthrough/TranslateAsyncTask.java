package com.sveder.cardboardpassthrough;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class TranslateAsyncTask extends AsyncTask<Void, Void, Void> {


    private String key = "AIzaSyA6BnpEl5MmjsB1dJ_6AkVS0Six8g9jdiQ";
    private String text;
    private String fromLang;
    private String toLang;
    private Callback callback;
    private DataWrapper translation;
    private Error error;

    public TranslateAsyncTask(String text, String from, String to, Callback callback) {
        this.text = text;
        this.callback = callback;
        fromLang = from;
        toLang = to;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            translation = translate(text, fromLang, toLang);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("translateerror", e.getMessage());
            error = new Error(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Log.e("callback", "post execute");
        if (callback != null) {
            Log.e("callback", "not null");
            callback.onComplete(translation, error);
        }

    }

    public interface Callback {
        void onComplete(Object o, Error error);
    }

    private DataWrapper translate(String text, String from, String to) {
        Log.e("translaterun", "running");
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://www.googleapis.com/language/translate/v2?key=" + key + "&q=" + encodedText + "&target=" + to + "&source=" + from;
            URL url = new URL(urlStr);

            URLConnection urlConnection = url.openConnection();
            // urlConnection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            InputStream response = urlConnection.getInputStream();
            String res = readStream(response);

            translation = DataWrapper.fromJson(res);
            Log.e("translationWrap", translation.data.translations.get(0).translatedText);
            return translation;

        } catch (IOException | JsonSyntaxException ex) {
            error = new Error(ex.getMessage());
            Log.e("translateError", ex.getMessage());
        }

        return null;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();


    }
}