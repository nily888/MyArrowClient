package com.example.rene.myarrow.misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by nily on 27.12.2015.
 */
public class BildAnzeigen {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BildAnzeigen.class.getSimpleName();

    private Context mContext;
    private String mFile;

    public BildAnzeigen( Context c, String f) {
        mContext = c;
        mFile = f;
    }

    @SuppressWarnings("unused")
    private BildAnzeigen() {
        Log.d(TAG, "BildAnzeigen unused.");
    }

    public void show() {
        //
        // wenn die Datei existiert, dann bitte anzeigen
        //
        Uri photoUri = getUri();
        if (photoUri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(photoUri, "image/*");
            mContext.startActivity(intent);
        }
    }

    private Uri getUri() {
        //
        // Dateiname ist leer
        //
        if (mFile==null || mFile.equals("")) {
            Toast.makeText(mContext, "Kein Dateiname übergeben!!", Toast.LENGTH_LONG).show();
            return null;
        }
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        //
        // jenachdem ob der Path mit dabei ist oder nicht
        //
        File file;
        if (mFile.startsWith("MYA_")) {
            file = new File(imagePath, mFile);
        } else {
            file = new File(mFile);
        }
        //
        // Existiert die Datei?
        //
        if (!file.exists()) {
            Toast.makeText(mContext, "Datei nicht gefunden!!", Toast.LENGTH_LONG).show();
            return null;
        }
        return Uri.fromFile(file);
    }
}
