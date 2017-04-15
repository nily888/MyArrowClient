package com.example.rene.myarrow.GUI.Bogen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Bogen.Bogen;
import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.setPic;


/**
 * Created by nily on 15.12.15.
 */
public class BearbeiteBogen extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BearbeiteBogen.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private BogenSpeicher mBogenSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mBogenGID;
    private Bogen mBogen;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);
        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.bogen_bearbeite);
        //
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_BOGEN_GID)) {
            mBogenGID = extras.getString(Konstante.IN_PARAM_BOGEN_GID);
        } else {
            Log.w(TAG, "Keine Bogen-ID übergeben");
        }
        // BogenSpeicher initialisieren
        mBogenSpeicher = new BogenSpeicher(this);
        // Bogendetails laden
        mBogen = mBogenSpeicher.loadBogenDetails(mBogenGID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBogen.dateiname==null || mBogen.dateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mBogen.dateiname = prefs.getString("MeinBogenBild", null);
        }
        zeigeDetails();
    }

    @Override
    protected void onStop(){
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinBogenBild", mBogen.dateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bearbeite_bogen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //
        if (id == R.id.action_delete_picture) {
            mBogenSpeicher.deleteDateiname(mBogenGID);
            mBogen.dateiname = "";
            Button zielBildButton = (Button)findViewById(R.id.imageButton);
            zielBildButton.setBackgroundResource(android.R.drawable.btn_default);
            zielBildButton.setTextColor(Color.BLACK);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void zeigeDetails() {
        /* *
         * Store Button belegen
         */
        Button fldStoreButton = (Button) findViewById(R.id.store_button);
        fldStoreButton.setText("Speichere Bogen...");
        /* *
         * Bogen Name auslesen
         */
        EditText fldBogenName = (EditText) findViewById(R.id.edt_bogenname);
        fldBogenName.setText(mBogen.name);
         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);
        fldStandard.setEnabled(mBogen.standard);
        /*
           Wenn ein Bild existiert anzeigen
         */
        if (mBogen.dateiname != null && !mBogen.dateiname.equals("")) {
            //
            // Image auslesen
            //
            Button fldImage = (Button) findViewById(R.id.imageButton);
            new setPic(this, fldImage, mBogen.dateiname, Konstante.MY_TRANSPARENT50);
        }
    }

    public void onClickStoreBogen(View v) {
        /* *
         * Bogen Name auslesen
         */
        EditText fldBogenName = (EditText) findViewById(R.id.edt_bogenname);
         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);
        /*
          Update Bogen in der Datenbank speichern
         */
	    mBogenSpeicher.updateBogen(
                mBogenGID,
                fldBogenName.getText().toString(),
                fldStandard.isChecked(),
                mBogen.dateiname);
        finish();
    }

    public void onClickimageButton(View v){
        if (mBogen.dateiname==null || mBogen.dateiname.equals("")) {
            // es gibt noch kein Bild
            EditText fldPfeilName = (EditText) findViewById(R.id.edt_bogenname);
            String mName = fldPfeilName.getText().toString();
            if (mName.equals("Name") || mName.equals("") || mName == null) {
                Toast.makeText(this, "Erst einen Namen für den Bogen eingeben", Toast.LENGTH_SHORT).show();
                return;
            }
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Bogen_" + mName);
            startActivityForResult(i, 1010);
        } else {
            // Bild gibt es schon, jetzt nur noch anzeigen
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mBogen.dateiname);
            bildAnzeigen.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        if (requestCode == 1010) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    /* Bild, falls vorhanden, anzeigen */
                    if (mDateiname != null && !mDateiname.equals("")) {
                        Button imageButton = (Button) findViewById(R.id.imageButton);
                        new setPic(this, imageButton, mDateiname, Konstante.MY_TRANSPARENT50);
                        mBogen.dateiname = mDateiname;
                    }
                } else {
                    Log.w(TAG, "onActivityResult(): Kein Dateiname übergeben");
                }
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
