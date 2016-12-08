package com.example.rene.myarrow.GUI.Schuetzen;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Schuetzen.Schuetzen;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.setPic;

/**
 * Created by nily on 15.12.15.
 */
public class BearbeiteSchuetze extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BearbeiteSchuetze.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private SchuetzenSpeicher mSchuetzenSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mSchuetzeGID;
    private Schuetzen mSchuetze;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);
        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.schuetzen_bearbeite);
        //Übergabeparameter SchuetzeId abholen
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
            mSchuetzeGID = extras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
        } else {
            Log.w(TAG, "Keine Schuetze-ID übergeben");
        }
        // SchuetzeSpeicher initialisieren
        mSchuetzenSpeicher = new SchuetzenSpeicher(this);
        // Schuetzedetails laden
        mSchuetze = mSchuetzenSpeicher.loadSchuetzenDetails(mSchuetzeGID);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart(): Start");
        super.onStart();
        Log.d(TAG, "onStart(): Dateiname - " + mSchuetze.dateiname);
        if (mSchuetze.dateiname==null || mSchuetze.dateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mSchuetze.dateiname = prefs.getString("MeinSchuetzenBild", null);
            Log.d(TAG, "onStart(): Dateiname - " + mSchuetze.dateiname);
        }
        Log.d(TAG, "onStart(): Dateiname - " + mSchuetze.dateiname);
        zeigeDetails();
        Log.d(TAG, "onStart(): End");
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop(): Start");
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinSchuetzenBild", mSchuetze.dateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
        Log.d(TAG, "onStop(): Dateiname - " + mSchuetze.dateiname);
        Log.d(TAG, "onStop(): End");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.remove("MeinSchuetzenBild");
        // editor.commit();
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bearbeite_schuetzen, menu);
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
            mSchuetzenSpeicher.deleteDateiname(mSchuetzeGID);
            mSchuetze.dateiname = "";
            Button zielBildButton = (Button)findViewById(R.id.imageButton);
            zielBildButton.setBackgroundResource(android.R.drawable.btn_default);
            zielBildButton.setTextColor(Color.BLACK);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void zeigeDetails() {
        Log.d(TAG, "zeigeDetails(): Start");
        Log.d(TAG, "zeigeDetails(): Name      - " + mSchuetze.name);
        Log.d(TAG, "zeigeDetails(): Dateiname - " + mSchuetze.dateiname);
        /* *
         * Speicher Button = Update Peil...
         */
        Log.d(TAG, "zeigeDetails(): Button Name - findViewById");
        Button fldStoreButton = (Button) findViewById(R.id.store_button);
        fldStoreButton.setText("Speichere Schütze...");
        /* *
         * Schuetze Name auslesen
         */
        Log.d(TAG, "zeigeDetails(): Schuetze Name - findViewById");
        EditText fldSchuetzeName = (EditText) findViewById(R.id.edt_schuetzenname);
        fldSchuetzeName.setText(mSchuetze.name);
        /**
         *  Wenn ein Bild existiert anzeigen
         */
        if (mSchuetze.dateiname!=null && !mSchuetze.dateiname.equals("")) {
            /* *
             * Image auslesen
             */
            Log.d(TAG, "zeigeDetails(): Image - findViewById");
            Button fldImage = (Button) findViewById(R.id.imageButton);
            new setPic(this, fldImage, mSchuetze.dateiname);
        }
        Log.d(TAG, "zeigeDetails(): End");
    }

    public void onClickStoreSchuetzen(View v) {
        Log.d(TAG, "onClickStoreSchuetzen(): Start");
        /* *
         * Schuetze Name auslesen
         */
        Log.d(TAG, "onClickStoreSchuetzen(): Schuetze Name - findViewById");
        EditText fldSchuetzeName = (EditText) findViewById(R.id.edt_schuetzenname);
        /**
         * Update Schuetze in der Datenbank speichern
         */
        Log.d(TAG, "onClickStoreSchuetzen(): updateSchuetzen");
	    long mid = mSchuetzenSpeicher.updateSchuetzen(
                mSchuetzeGID,
                fldSchuetzeName.getText().toString(),
                mSchuetze.dateiname);
        Log.d(TAG, "onClickUpdateSchuetzen(): End - " + mid);
        finish();
    }

    public void onClickimageButton(View v) {
        Log.d(TAG,"onClickimageButton(): Start");
        if (mSchuetze.dateiname==null || mSchuetze.dateiname.equals("")) {
            // es gibt noch kein Bild
            EditText fldSchuetzeName = (EditText) findViewById(R.id.edt_schuetzenname);
            String mName = fldSchuetzeName.getText().toString();
            if (mName.equals("Name") || mName.equals("") || mName == null) {
                Log.d(TAG, "onClickimageButton(): End");
                Toast.makeText(this, "Erst einen Namen für den Schützen eingeben", Toast.LENGTH_SHORT).show();
                return;
            }
            final Intent i = new Intent(this, GetPicture.class);
            Log.d(TAG, "onClickimageButton(): Schuetze_" + mName);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Schuetze_" + mName);
            startActivityForResult(i, 1109);
        } else {
            // Bild gibt es schon, jetzt nur noch anzeigen
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mSchuetze.dateiname);
            bildAnzeigen.show();
        }
        Log.d(TAG, "onClickimageButton(): End");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): Start");
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        if (requestCode == 1109) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mDateiname);
                    /** Bild, falls vorhanden, anzeigen */
                    if (mDateiname!=null && !mDateiname.equals("")) {
                        Button imageButton = (Button)findViewById(R.id.imageButton);
                        new setPic(this, imageButton, mDateiname);
                        mSchuetze.dateiname = mDateiname;
                    }
                    Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mSchuetze.dateiname);
                } else {
                    Log.w(TAG, "onActivityResult(): Kein Dateiname übergeben");
                }
                Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mSchuetze.dateiname);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        Log.d(TAG, "onActivityResult(): End");
    }
}
