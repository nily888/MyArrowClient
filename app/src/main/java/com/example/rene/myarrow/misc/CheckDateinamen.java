package com.example.rene.myarrow.misc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielTbl;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;

import java.io.File;

public class CheckDateinamen {

    /** Kuerzel fuers Logging. */
    private static final String TAG = CheckDateinamen.class.getSimpleName();
    private Context mContext;

    public CheckDateinamen(Context context){
        mContext = context;
    }

    public void checkDatei(){
        Log.d(TAG, "CheckDateinamen(): Start");
        int mZahl=0;
        //
        RundenZielSpeicher rzSpeicher = new RundenZielSpeicher(mContext);
        Cursor rz = rzSpeicher.loadRundenZielListe();
        while (rz.moveToNext()){
            String mDatei = rz.getString(rz.getColumnIndex(RundenZielTbl.DATEINAME));
            Log.d(TAG, "CheckDateinamen(): Datei - " + mDatei);
            if (mDatei!=null && !mDatei.equals("")){
                if(!getCheckFile(mDatei)){
                    Log.d(TAG, "CheckDateinamen(): wird gelöscht");
                    rzSpeicher.deleteDateiname(rz.getString(rz.getColumnIndex(RundenZielTbl.GID)));
                    mZahl++;
                }
            }
        }
        //
        ZielSpeicher zSpeicher = new ZielSpeicher(mContext);
        Cursor z = zSpeicher.loadZielListe();
        while (z.moveToNext()){
            String mDatei = z.getString(z.getColumnIndex(ZielTbl.DATEINAME));
            Log.d(TAG, "CheckDateinamen(): Datei - " + mDatei);
            if (mDatei!=null && !mDatei.equals("")){
                if(!getCheckFile(mDatei)){
                    Log.d(TAG, "CheckDateinamen(): wird gelöscht");
                    zSpeicher.deleteDateiname(z.getString(z.getColumnIndex(ZielTbl.GID)));
                    mZahl++;
                }
            }
        }
        Toast.makeText(mContext, String.valueOf(mZahl) + " Dateinamen gelöscht", Toast.LENGTH_SHORT).show();
    }

    private boolean getCheckFile(String mFile) {
        Log.d(TAG, "getCheckFile(): Start");
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        Log.d(TAG, "getCheckFile(): Find " + imagePath.getAbsolutePath());
        // Create an image file name
        Log.d(TAG, "getCheckFile(): Dateinamen definieren mit " + mFile);
        File image = new File(mFile);
        Log.d(TAG, "getCheckFile(): End       - " + image.exists());
        return image.exists();
    }

    public void checklistDir() {
        Log.d(TAG, "checklistDir(): Start");
        int mZahl = 0;
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        File[] files = imagePath.listFiles();
        if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    Log.d(TAG, "checklistDir(): Ordner" + files[i].getAbsolutePath());
                }
                else {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(files[i].getAbsoluteFile()));
                    mContext.sendBroadcast(mediaScanIntent);
                    Log.d(TAG, "checklistDir(): Datei" + files[i].getAbsolutePath());
                    mZahl++;
                }
            }
        }
        Toast.makeText(mContext, String.valueOf(mZahl) + " Datein gefunden", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "checklistDir(): End");
    }
}
