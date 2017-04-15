package com.example.rene.myarrow.GUI.Pfeil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.setPic;

/**
 * Created by nily on 15.12.15.
 */
public class NeuerPfeil extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = NeuerPfeil.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private PfeilSpeicher mPfeilSpeicher;
    private String mDateiname="";

    @Override
    protected void onCreate(Bundle icicle) {

        Log.d(TAG, "oncreate(): Start");

        // Super Methode aufrufen
        super.onCreate(icicle);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.pfeil_bearbeite);

        /* *
         * Store Button belegen
         */
        Log.d(TAG, "onCreate():  - findViewById");
        Button fldStoreButton = (Button) findViewById(R.id.store_button);
        fldStoreButton.setText("Speichere Pfeil...");

        // ParcourSpeicher initialisieren
        mPfeilSpeicher = new PfeilSpeicher(this);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart(): Start");
        super.onStart();
        Log.d(TAG, "onStart(): Dateiname - " + mDateiname);
        if (mDateiname==null || mDateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mDateiname = prefs.getString("MeinBogenBild", null);
            Log.d(TAG, "onStart(): Dateiname - " + mDateiname);
        }
        Log.d(TAG, "onStart(): Dateiname - " + mDateiname);
        Log.d(TAG, "onStart(): End");
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop(): Start");
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinBogenBild", mDateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
        Log.d(TAG, "onStop(): Dateiname - " + mDateiname);
        Log.d(TAG, "onStop(): End");
    }

    public void onClickStorePfeil(View v) {
        /* *
         * Pfeil Name auslesen
         */
        Log.d(TAG, "onClickStorePfeil(): Pfeil Name - findViewById");
        EditText fldPfeilName = (EditText) findViewById(R.id.edt_pfeilname);
        /*
 	      Standard auslesen
	     */
        Log.d(TAG, "onClickStorePfeil(): Standard - findViewById");
        CheckBox mStandard = (CheckBox) findViewById(R.id.txt_standard);
        /*
          Neuer Pfeil in der Datenbank abspeichern
          */
        Log.d(TAG, "onClickStorePfeil(): insertPfeil");
        mPfeilSpeicher.insertPfeil(
                fldPfeilName.getText().toString(),
                mStandard.isChecked(),
                mDateiname,
                new Date().getTime());
        //
        setResult(Activity.RESULT_OK, null);
        finish();
        Log.d(TAG, "onClickStorePfeil(): End");
    }

    public void onClickimageButton(View v) {
        Log.d(TAG,"onClickimageButton(): Start");
        EditText fldPfeilName = (EditText) findViewById(R.id.edt_pfeilname);
        String mName = fldPfeilName.getText().toString();
        if (mName.equals("Name") || mName.equals("") || mName==null) {
            Log.d(TAG, "onClickimageButton(): End");
            Toast.makeText(this, "Erst einen Namen für das Ziel eingeben", Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent i = new Intent(this, GetPicture.class);
        Log.d(TAG, "onClickimageButton(): Pfeil_" + mName);
        i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Pfeil_" + mName);
        startActivityForResult(i, 1008);
        Log.d(TAG, "onClickimageButton(): End");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): Start");
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        if (requestCode == 1008) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mDateiname);
                    /* Bild, falls vorhanden, anzeigen */
                    if (mDateiname!=null && !mDateiname.equals("")) {
                        Button imageButton = (Button)findViewById(R.id.imageButton);
                        new setPic(this, imageButton, mDateiname);
                    }
                    // Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mPfeil.dateiname);
                } else {
                    Log.w(TAG, "onActivityResult(): Kein Dateiname übergeben");
                }
                // Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mPfeil.dateiname);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        Log.d(TAG, "onActivityResult(): End");
    }
}
