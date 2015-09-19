package com.sveder.cardboardpassthrough;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Translate {
    public static final String DOWNLOAD_BASE = "http://tesseract-ocr.googlecode.com/files/";
    private static final String TAG = "TRANSLATE";
    public static String translatedText;

    public static void translateImage(byte[] data, final Activity activity){
        translatedText = "";
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        storeImage(bitmap, activity);


        File dir = getStorageDirectory(activity);
        OcrAsyncTask ocrAsyncTask = new OcrAsyncTask(activity, bitmap, dir.toString(), new OcrAsyncTask.Callback() {
            @Override
            public void onComplete(Object o, Error error) {
                if (error != null) {
                    Toast.makeText(activity, "OCR Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("OcrAsyncTask", error.getMessage());
                    return;
                }
                String excerpt = (String) o;
                TranslateAsyncTask translateAsyncTask = new TranslateAsyncTask(excerpt, "FR", "EN", new TranslateAsyncTask.Callback() {
                    @Override
                    public void onComplete(Object o, Error error) {
                        if (error != null) {
                            Toast.makeText(activity, "Translate Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("TranslateAsyncTask", error.getMessage());
                            return;
                        }
                        DataWrapper translation = (DataWrapper) o;
                        translatedText = translation.data.translations.get(0).translatedText;
//                        Toast.makeText(activity, translatedText, Toast.LENGTH_SHORT).show();
                        MainActivity main = (MainActivity) activity;
                        main.showText(translatedText);
                        Log.e("TranslateAsyncTask", translatedText);
                    }
                });
                translateAsyncTask.execute();
            }
        });
        ocrAsyncTask.execute();
    }

    private static void storeImage(Bitmap image, Activity activity) {
        File pictureFile = getOutputMediaFile(activity);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(Activity activity){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static File getStorageDirectory(Activity activity) {
        //Log.d(TAG, "getStorageDirectory(): API level is " + Integer.valueOf(android.os.Build.VERSION.SDK_INT));

        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (RuntimeException e) {
            Log.e(TAG, "Is the SD card visible?", e);
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            // We can read and write the media
            //    	if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) > 7) {
            // For Android 2.2 and above

            try {
                return activity.getExternalFilesDir(Environment.MEDIA_MOUNTED);
            } catch (NullPointerException e) {
                // We get an error here if the SD card is visible, but full
                Log.e(TAG, "External storage is unavailable");
            }


        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            Log.e(TAG, "External storage is read-only");
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // to know is we can neither read nor write
            Log.e(TAG, "External storage is unavailable");
        }
        return null;
    }

    public static void initOcrIfNecessary(Activity activity, String langCode){

        boolean doNewInit = false;
        File storageDirectory = getStorageDirectory(activity);
        if(storageDirectory != null){
            File data = new File(storageDirectory.toString()
                    + File.separator + "tessdata"
                    + File.separator + langCode + ".traineddata");
            doNewInit = !data.exists() || data.isDirectory();
        }
        if (doNewInit) {
            new OcrInitAsyncTask(activity, langCode).execute(storageDirectory.toString());
        }
    }
}
