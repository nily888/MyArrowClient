package com.example.rene.myarrow.GUI.Ziel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.ShowMap;
import com.example.rene.myarrow.misc.WoBinIch;
import com.example.rene.myarrow.misc.setPic;
import com.example.rene.myarrow.R;

/**
 * Created by nily on 15.12.15.
 */
public class BearbeiteZiel extends AppCompatActivity{

    /** Kuerzel fuers Logging. */
    private static final String TAG = BearbeiteZiel.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private ZielSpeicher mZielSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mZielGID;
    private Ziel mZiel;

    @Override
    protected void onCreate(Bundle icicle) {

        // Super Methode aufrufen
        super.onCreate(icicle);

        //
        setContentView(R.layout.ziel_bearbeite);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_ZIEL_GID)) {
            mZielGID = extras.getString(Konstante.IN_PARAM_ZIEL_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Ziel-ID " + mZielGID);
        } else {
            Log.w(TAG, "Keine Ziel-ID übergeben");
        }

        // ZielSpeicher und Ziel initialisieren
        mZielSpeicher = new ZielSpeicher(this);
        mZiel = mZielSpeicher.loadZiel(mZielGID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mZiel.dateiname==null || mZiel.dateiname.equals("")) {
            final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            mZiel.dateiname = prefs.getString("MeinZielBild", null);
        }
        zeigeDetails();
    }

    @Override
    protected void onStop(){
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("MeinZielBild", mZiel.dateiname);
        // editor.commit();
        editor.apply();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bearbeite_ziel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Button zielBildButton;
        int id = item.getItemId();
        //
        switch (id) {
            case R.id.action_delete_picture:
                mZielSpeicher.deleteDateiname(mZielGID);
                mZiel.dateiname = "";
                zielBildButton = (Button) findViewById(R.id.zielbild_button);
                zielBildButton.setBackgroundResource(android.R.drawable.btn_default);
                zielBildButton.setTextColor(Color.BLACK);
                // Dateiname auch in der Anzeige löschen
                TextView fldZielDateiname = (TextView) findViewById(R.id.txt_zieldateiname);
                fldZielDateiname.setText("");
                break;

            case R.id.action_getlocation:
                String[] tempWo = new WoBinIch(this).getLocation();
                if (tempWo!=null) {
                    Button fldGPSLat = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
                    fldGPSLat.setText(tempWo[0]);
                    Button fldGPSLon = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
                    fldGPSLon.setText(tempWo[1]);
                }
                break;

            case R.id.action_delete_location:
                Button fldGPSLat = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
                fldGPSLat.setText("");
                Button fldGPSLon = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
                fldGPSLon.setText("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void zeigeDetails() {
        /* *
         * Ziel Nummer auslesen
         */
        TextView fldZielNummer = (TextView) findViewById(R.id.txt_zielnummer);
        fldZielNummer.setText(String.valueOf(mZiel.nummer));

        /* *
         * Ziel Name auslesen
         */
        EditText fldZielName = (EditText) findViewById(R.id.edt_zielname);
        fldZielName.setText(mZiel.name);

        /* *
         * GPS-Lat Koordinate auslesen
         */
        TextView fldgps_lat_koordinaten = (TextView) findViewById(R.id.txt_gps_lat_koordinaten);
        fldgps_lat_koordinaten.setText(String.valueOf(mZiel.gps_lat_koordinaten));

        /* *
         * GPS-Lon Koordinate auslesen
         */
        TextView fldgps_lon_koordinaten = (TextView) findViewById(R.id.txt_gps_lon_koordinaten);
        fldgps_lon_koordinaten.setText(String.valueOf(mZiel.gps_lon_koordinaten));

        /* *
         * Dateiname auslesen
         */
        TextView fldZielDateiname = (TextView) findViewById(R.id.txt_zieldateiname);
        fldZielDateiname.setText(mZiel.dateiname);

        /*
          Ziel Image auslesen
         */
        Button fotoButton = (Button) findViewById(R.id.zielbild_button);
        new setPic(this, fotoButton, mZiel.dateiname);

        //
        // UpdateButton
        //
        Button updateButton = (Button)findViewById(R.id.update_button);
        Resources res = getResources();
        Drawable d = res.getDrawable(R.mipmap.start_button);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        updateButton.setBackground(d);
        updateButton.setTypeface(null, Typeface.BOLD);
        updateButton.setTextColor(Color.BLACK);

    }

    public void onClickZielBild(View v) {
        /* Wenn schon ein Bild existiert anzeigen, sonst aufnehmen */
        if (mZiel.dateiname != null && !mZiel.dateiname.equals("")) {
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mZiel.dateiname);
            bildAnzeigen.show();
        } else {
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "ZielBild_" + String.valueOf(mZiel.nummer));
            startActivityForResult(i, 1004);
        }
    }

    public void onClickUpdateZiel(View v) {

        Log.d(TAG, "onClickUpdateZiel(): Start");

        /* *
         * Ziel Name auslesen
         */
        Log.d(TAG, "onClickUpdateZiel(): Ziel Name - findViewById");
        EditText fldZielName = (EditText) findViewById(R.id.edt_zielname);

        /* *
         * GPS-Lat Koordinate auslesen
         */
        Log.d(TAG, "onClickUpdateZiel(): GPS-Lat Koordinate - findViewById");
        Button fldgps_lat_koordinate = (Button) findViewById(R.id.txt_gps_lat_koordinaten);

        /* *
         * GPS-Lon Koordinate auslesen
         */
        Log.d(TAG, "onClickUpdateZiel(): GPS-Lat Koordinate - findViewById");
        Button fldgps_lon_koordinate = (Button) findViewById(R.id.txt_gps_lon_koordinaten);

        /* *
         * Ziel Name auslesen
         */
        Log.d(TAG, "onClickUpdateZiel(): Dateiname - findViewById");
        TextView fldZielDateiname = (TextView) findViewById(R.id.txt_zieldateiname);

        /*
          Update Ziel in der Datenbank speichern
         */
        Log.d(TAG, "onClickUpdateZiel(): updateZiel");
	    long mid = mZielSpeicher.updateZiel(
                mZielGID,                                    // Ziel-Id
                fldZielName.getText().toString(),           // Name
                fldgps_lat_koordinate.getText().toString(), // Lat Koordinate
                fldgps_lon_koordinate.getText().toString(), // Lon Koordinate
                fldZielDateiname.getText().toString());     // Dateiname

        Log.d(TAG, "onClickUpdateZiel(): End - " + mid);

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): Start");
        //data available only when default environment is setting. null for customize filename.
        Log.d(TAG, "onActivityResult(): requestCode==" + requestCode);
        Log.d(TAG, "onActivityResult(): resultCode==" + resultCode);
        switch (requestCode) {
            case 1004:
                if (resultCode == Activity.RESULT_OK) {
                    final Bundle mExtras = data.getExtras();
                    if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                        String mZielBildDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                        Log.d(TAG, "onActivityResult(): Aufruf mit Dateiname " + mZielBildDateiname);
                        /* Bild, falls vorhanden, anzeigen */
                        if (mZielBildDateiname != null && !mZielBildDateiname.equals("")) {
                            /* Bild als Button Hintergrund setzen */
                            Button zielBildButton = (Button) findViewById(R.id.zielbild_button);
                            new setPic(this, zielBildButton, mZielBildDateiname);
                            /* Bild Dateiname abspeichern */
                            mZiel.dateiname = mZielBildDateiname;
                        }
                    } else {
                        Log.w(TAG, "onActivityResult(): Kein Dateiname übergeben");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }
                break;
        }
        Log.d(TAG, "onActivityResult(): End");
    }

    public void onClickWoBinIch(View v) {
        Button fldGPSLatKoordinaten = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
        Button fldGPSLonKoordinaten = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
        EditText fldZielName = (EditText) findViewById(R.id.edt_zielname);
        if (!fldGPSLatKoordinaten.getText().toString().equals("") &&
            !fldGPSLatKoordinaten.getText().toString().equals("NULL") &&
             fldGPSLatKoordinaten.getText().toString() != null &&
            !fldGPSLonKoordinaten.getText().toString().equals("") &&
            !fldGPSLonKoordinaten.getText().toString().equals("NULL") &&
             fldGPSLonKoordinaten.getText().toString() != null) {
            String[][] zielListe = new String[1][3];
            final Intent i = new Intent(this, ShowMap.class);
            zielListe[0][0] = fldZielName.getText().toString();
            zielListe[0][1] = fldGPSLatKoordinaten.getText().toString();
            zielListe[0][2] = fldGPSLonKoordinaten.getText().toString();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
            i.putExtras(mBundle);
            startActivity(i);
        }
    }
}
