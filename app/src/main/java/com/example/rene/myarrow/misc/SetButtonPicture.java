package com.example.rene.myarrow.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;

import java.io.File;

/**
 * Created by nily on 28.12.2015.
 */
public class SetButtonPicture {

    /** Kuerzel fuers Logging. */
    private static final String TAG = SetButtonPicture.class.getSimpleName();

    private Button mb;
    private Context mContext;
    private String mPath;
    private int mScale;

    public SetButtonPicture(Context c, final Button b, String mCurrentPhotoPath) {
        Log.d(TAG, "SetButtonPicture-1(): Start");
        mContext = c;
        mb = b;
        mPath = mCurrentPhotoPath;
        mScale = 28;
        scaliereBild();
        Log.d(TAG, "SetButtonPicture-1(): End");
    }

    public SetButtonPicture(Context c, final Button b, String mCurrentPhotoPath, int scale) {
        Log.d(TAG, "SetButtonPicture-2(): Start");
        mContext = c;
        mb = b;
        mPath = mCurrentPhotoPath;
        mScale = scale;
        scaliereBild();
        Log.d(TAG, "SetButtonPicture-2(): End");
    }

    private void scaliereBild(){
        Log.d(TAG, "scaliereBild(): Start");
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
            File mFile = new File(mPath);
            if (!mFile.exists()) {
                // Datei nicht gefunden
                return;
            }
        }
        // Skaliere Bild und setze es als Buttonhintergrund
        Bitmap bitmap = BitmapFactory.decodeFile(mPath, bmOptions);
        Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
        mb.setBackground(d);
        mb.setTextColor(Color.WHITE);
        Log.d(TAG, "scaliereBild(): End");
    }
}
