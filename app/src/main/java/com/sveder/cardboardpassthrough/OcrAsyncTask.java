package com.sveder.cardboardpassthrough;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;

public class OcrAsyncTask extends AsyncTask<Void, Void, Void> {

    private Pix mBitmap;
    private Callback mCallback;
    private ProgressDialog dialog;
    private String mDatapath;

    private String excerpt;

    public OcrAsyncTask(Activity activity, Bitmap bitmap, String datapath, Callback callback) {
        mDatapath = datapath;
        mBitmap = ReadFile.readBitmap(bitmap);
        mCallback = callback;
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
//        dialog.setMessage("Processing image...");
//        dialog.setCancelable(false);
//        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        TessOCR tesseract = new TessOCR(mDatapath);

        excerpt = tesseract.getOCRResult(mBitmap);
        excerpt = excerpt.replaceAll("\n", " ");
        excerpt = excerpt.replaceAll("  ", "\n\n");
        excerpt = excerpt.replaceAll("\\p{Pd}", "-");

        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        if (mCallback != null) {
            mCallback.onComplete(excerpt, null);
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface Callback {
        void onComplete(Object o, Error error);
    }
}