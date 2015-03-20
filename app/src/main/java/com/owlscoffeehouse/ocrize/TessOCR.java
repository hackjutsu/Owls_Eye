package com.owlscoffeehouse.ocrize;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(Context context) {
        mTess = new TessBaseAPI();
        String DATA_PATH = Environment.getExternalStorageDirectory() + "/tesseract/";
        String lang = "eng";

        String TessDataDirPath = DATA_PATH + "tessdata/";
        File tessDataDir = new File(TessDataDirPath);
        if (!(tessDataDir.exists())) {
            tessDataDir.mkdir();
        }

        if (!(new File(tessDataDir + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = context.getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(TessDataDirPath + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v("TESS-DEMO", "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e("TESS-DEMO", "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        if ((tessDataDir.exists())) {
            mTess.init(DATA_PATH, lang);
        }
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();

        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
