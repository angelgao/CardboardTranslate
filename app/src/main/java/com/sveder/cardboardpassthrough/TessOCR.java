package com.sveder.cardboardpassthrough;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.tesseract.android.TessBaseAPI;
public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(String datapath) {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        String language = "fra";
        mTess.init(datapath, language);
    }

    public String getOCRResult(Pix bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();

        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

}