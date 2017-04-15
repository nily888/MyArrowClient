package com.example.rene.myarrow.GUI.Bogen;

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

import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.setPic;

/**
 * Created by nily on 15.12.15.
 */
public class NeuerBogen extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = NeuerBogen.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private BogenSpeicher mBogenSpeicher;
    private String mDateiname="";

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);
        // View aufrufen
        setContentView(R.layout.bogen_bearbeite);
        /* *
         * Store Button belegen
         */
        Button fldStoreButton = (Button) findViewById(R.id.store_button);
        fldStoreButton.setText("Speichere Bogen...");
        // ParcourSpeicher initialisieren
        mBogenSpeicher = new BogenSpeicher(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDateiname==null || mDateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mDateiname = prefs.getString("MeinBogenBild", null);
        }
    }

    @Override
    protected void onStop(){
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinBogenBild", mDateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
    }

    public void onClickStoreBogen(View v) {
        /* *
         * Bogen Name auslesen
         */
        EditText fldBogenName = (EditText) findViewById(R.id.edt_bogenname);
	    /*
 	      Standard auslesen
	     */
        CheckBox mStandard = (CheckBox) findViewById(R.id.txt_standard);
        //
        mBogenSpeicher.insertBogen(
                fldBogenName.getText().toString(),
                mStandard.isChecked(),
                mDateiname,
                new Date().getTime());
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    public void onClickimageButton(View v){
        EditText fldPfeilName = (EditText) findViewById(R.id.edt_bogenname);
        String mName = fldPfeilName.getText().toString();
        if (mName.equals("Name") || mName.equals("") || mName==null) {
            Toast.makeText(this, "Erst einen Namen für den Bogen eingeben", Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent i = new Intent(this, GetPicture.class);
        i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Bogen_" + mName);
        startActivityForResult(i, 1011);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        if (requestCode == 1011) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    /* Bild, falls vorhanden, anzeigen */
                    if (mDateiname!=null && !mDateiname.equals("")) {
                        Button imageButton = (Button)findViewById(R.id.imageButton);
                        new setPic(this, imageButton, mDateiname, Konstante.MY_TRANSPARENT50);
                    }
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
    }
}
