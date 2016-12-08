package com.example.rene.myarrow.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import java.io.File;

/**
 * Created by nily on 28.12.2015.
 */
public class setPic {

    /** Kuerzel fuers Logging. */
    private static final String TAG = setPic.class.getSimpleName();

    private Button mb;
    private Context mContext;
    private String mPath;
    private int mScale;
    private int mTransparent=Konstante.MY_TRANSPARENT30;

    public setPic(Context c, final Button b, String mCurrentPhotoPath) {
        mContext = c;
        mb = b;
        mPath = mCurrentPhotoPath;
        mScale = 28;
        scaliereBild();
    }

    public setPic(Context c, final Button b, String mCurrentPhotoPath, int transparent) {
        Log.d(TAG, "setPic-2(): Start");
        mContext = c;
        mb = b;
        mPath = mCurrentPhotoPath;
        mTransparent = transparent;
        scaliereBild();
        Log.d(TAG, "setPic-2(): End");
    }

    public void setTransparent(int transparent){
        mTransparent = transparent;
    }

    private void scaliereBild(){
        File mFile;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = mScale;
        // bmOptions.inPurgeable = true;
        // Existiert die Datei?
        if (mPath==null) {
            // Dateiname ist leer
            return;
        } else {
            //
            // jenachdem ob der Path mit dabei ist oder nicht
            //
            if (mPath.startsWith("MYA_")) {
                File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "MyArrow");
                mFile = new File(imagePath, mPath);
            } else {
                mFile = new File(mPath);
            }
            if (!mFile.exists()) {
                // Datei nicht gefunden
                return;
            }
        }
        // Skaliere Bild und setze es als Buttonhintergrund
        Bitmap bitmap = BitmapFactory.decodeFile(mFile.toString(), bmOptions);
        // Resources res = mContext.getResources();
        Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
        d.setAlpha(mTransparent);
        mb.setBackground(d);
        mb.setTypeface(null, Typeface.BOLD);
        mb.setTextColor(Color.BLACK);
    }
}
