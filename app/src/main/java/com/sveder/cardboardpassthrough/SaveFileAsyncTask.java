package com.sveder.cardboardpassthrough;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveFileAsyncTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private Bitmap mBitmap;
    private Callback mCallback;
    private static final String TAG = "SaveFileAsyncTask";

    public SaveFileAsyncTask(Activity activity, Bitmap bitmap,  Callback callback) {
        this.activity = activity;
        mBitmap = bitmap;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        storeImage(mBitmap, activity);

        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        if (mCallback != null) {
            mCallback.onComplete(null, null);
        }
    }

    public interface Callback {
        void onComplete(Object o, Error error);
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
}
