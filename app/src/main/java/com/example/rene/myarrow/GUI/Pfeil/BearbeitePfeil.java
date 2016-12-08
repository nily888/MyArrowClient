package com.example.rene.myarrow.GUI.Pfeil;

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

import com.example.rene.myarrow.Database.Pfeil.Pfeil;
import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.setPic;

/**
 * Created by nily on 15.12.15.
 */
public class BearbeitePfeil extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BearbeitePfeil.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private PfeilSpeicher mPfeilSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mPfeilGID;
    private Pfeil mPfeil;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);
        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.pfeil_bearbeite);
        //Übergabeparameter PfeilId abholen
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PFEIL_GID)) {
            mPfeilGID = extras.getString(Konstante.IN_PARAM_PFEIL_GID);
        } else {
            Log.e(TAG, "Keine Pfeil-ID übergeben");
        }
        // PfeilSpeicher initialisieren
        mPfeilSpeicher = new PfeilSpeicher(this);
        // Pfeildetails laden
        mPfeil = mPfeilSpeicher.loadPfeilDetails(mPfeilGID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPfeil.dateiname==null || mPfeil.dateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mPfeil.dateiname = prefs.getString("MeinPfeilBild", null);
        }
        zeigeDetails();
    }

    @Override
    protected void onStop(){
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinPfeilBild", mPfeil.dateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bearbeite_pfeil, menu);
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
            mPfeilSpeicher.deleteDateiname(mPfeilGID);
            mPfeil.dateiname = "";
            Button zielBildButton = (Button)findViewById(R.id.imageButton);
            zielBildButton.setBackgroundResource(android.R.drawable.btn_default);
            zielBildButton.setTextColor(Color.BLACK);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void zeigeDetails() {
        /* *
         * Speicher Button = Update Peil...
         */
        Button fldStoreButton = (Button) findViewById(R.id.store_button);
        fldStoreButton.setText("Speichere Pfeil...");
        /* *
         * Pfeil Name auslesen
         */
        EditText fldPfeilName = (EditText) findViewById(R.id.edt_pfeilname);
        fldPfeilName.setText(mPfeil.name);
         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);
        fldStandard.setEnabled(mPfeil.standard);
        /**
         *  Wenn ein Bild existiert anzeigen
         */
        if (mPfeil.dateiname!=null && !mPfeil.dateiname.equals("")) {
            /* *
             * Image auslesen
             */
            Button fldImage = (Button) findViewById(R.id.imageButton);
            new setPic(this, fldImage, mPfeil.dateiname, Konstante.MY_TRANSPARENT50);
        }
    }

    public void onClickStorePfeil(View v) {
        /* *
         * Pfeil Name auslesen
         */
        EditText fldPfeilName = (EditText) findViewById(R.id.edt_pfeilname);
         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);
        /**
         * Update Pfeil in der Datenbank speichern
         */
	    mPfeilSpeicher.updatePfeil(
                mPfeilGID,
                fldPfeilName.getText().toString(),
                fldStandard.isChecked(),
                mPfeil.dateiname);
        finish();
    }

    public void onClickimageButton(View v) {
        if (mPfeil.dateiname==null || mPfeil.dateiname.equals("")) {
            // es gibt noch kein Bild
            EditText fldPfeilName = (EditText) findViewById(R.id.edt_pfeilname);
            String mName = fldPfeilName.getText().toString();
            if (mName.equals("Name") || mName.equals("") || mName == null) {
                Toast.makeText(this, "Erst einen Namen für das Ziel eingeben", Toast.LENGTH_SHORT).show();
                return;
            }
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Pfeil_" + mName);
            startActivityForResult(i, 1009);
        } else {
            // Bild gibt es schon, jetzt nur noch anzeigen
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mPfeil.dateiname);
            bildAnzeigen.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        if (requestCode == 1009) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    /** Bild, falls vorhanden, anzeigen */
                    if (mDateiname!=null && !mDateiname.equals("")) {
                        Button imageButton = (Button)findViewById(R.id.imageButton);
                        new setPic(this, imageButton, mDateiname, Konstante.MY_TRANSPARENT50);
                        mPfeil.dateiname = mDateiname;
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
