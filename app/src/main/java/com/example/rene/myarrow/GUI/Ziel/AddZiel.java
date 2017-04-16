package com.example.rene.myarrow.GUI.Ziel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.ShowMap;
import com.example.rene.myarrow.misc.WoBinIch;
import com.example.rene.myarrow.misc.setPic;

/**
 * Created by nily on 15.12.15.
 */
public class AddZiel extends AppCompatActivity{

    /** Kuerzel fuers Logging. */
    private static final String TAG = AddZiel.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private ZielSpeicher mZielSpeicher;
    private ParcourSpeicher mParcourSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mParcourGID;
    private Ziel mZiel;
    private Parcour mParcour;

    @Override
    protected void onCreate(Bundle icicle) {

        // Super Methode aufrufen
        super.onCreate(icicle);

        //
        setContentView(R.layout.ziel_add);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGID = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Parcour-ID " + mParcourGID);
        } else {
            Log.w(TAG, "Keine Parcour-ID übergeben");
        }

        // ParcourSpeicher und Parcour initialisieren
        mParcourSpeicher = new ParcourSpeicher(this);
        mParcour = mParcourSpeicher.loadParcourDetails(mParcourGID);

        // ZielSpeicher initialisieren
        mZielSpeicher = new ZielSpeicher(this);
        mZiel = new Ziel();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mZiel!= null || mZiel.dateiname==null || mZiel.dateiname.equals("")) {
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
                // wird hier nicht benutzt
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
        EditText fldZielNummer = (EditText) findViewById(R.id.edt_zielnummer);
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
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.mipmap.start_button, null);
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

    public void onClickAddZiel(View v) {
        Log.d(TAG, "onClickAddZiel(): Start");
        /* *
         * Ziel Nummer auslesen
         */
        Log.d(TAG, "onClickAddZiel(): Ziel Nummer - findViewById");
        EditText fldZielNummer = (EditText) findViewById(R.id.edt_zielnummer);
        int nZielNummer = Integer.valueOf(fldZielNummer.getText().toString());

        /*
          Passt die Zielnummer? Darf nur um eins höher sein als die aktuelle Anzahl von Zielen
         */
        Log.d(TAG, "onClickAddZiel(): Anzahl Ziele / neueZielnummer: "
                + String.valueOf(mParcour.anzahl_ziele) + " / " + String.valueOf(nZielNummer));
        if ((mParcour.anzahl_ziele+1) < nZielNummer) {
            Toast.makeText(this, "Die Zielnummer darf nicht höher als " + String.valueOf(mParcour.anzahl_ziele+1) + " sein!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //
        // wollen Sie wirklich ein Ziel hinzufügen?
        //
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Weiter");
        builder2.setMessage("Sind Sie sicher ? Empfehlung wäre neuen Parcour anzulegen!");
        // O.K., verstanden ein Ziel hinzufügen
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* *
                 * Ziel Nummer auslesen
                 */
                Log.d(TAG, "onClickAddZiel(): Ziel Nummer - findViewById");
                EditText fldZielNummer = (EditText) findViewById(R.id.edt_zielnummer);
                int nZielNummer = Integer.valueOf(fldZielNummer.getText().toString());                /* *
                 * Ziel Name auslesen
                 */
                Log.d(TAG, "onClickAddZiel(): Ziel Name - findViewById");
                EditText fldZielName = (EditText) findViewById(R.id.edt_zielname);
                String strZielName = fldZielName.getText().toString();

                /* *
                 * GPS-Lat Koordinate auslesen
                 */
                Log.d(TAG, "onClickAddZiel(): GPS-Lat Koordinate - findViewById");
                Button fldgps_lat_koordinate = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
                String strLat = fldgps_lat_koordinate.getText().toString();

                /* *
                 * GPS-Lon Koordinate auslesen
                 */
                Log.d(TAG, "onClickAddZiel(): GPS-Lat Koordinate - findViewById");
                Button fldgps_lon_koordinate = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
                String strLon = fldgps_lon_koordinate.getText().toString();

                /* *
                 * Ziel Dateinamen auslesen
                 */
                Log.d(TAG, "onClickAddZiel(): Dateiname - findViewById");
                TextView fldZielDateiname = (TextView) findViewById(R.id.txt_zieldateiname);
                String strZielDateiname = fldZielDateiname.getText().toString();

                /*
                  Add Ziel zur Datenbank hinzu
                  zunächst alle Zielnummern aber neuem Ziel um einen erhöhen
                 */
                long mid;
                for (int n=mParcour.anzahl_ziele; (n>(nZielNummer-1)); n--){
                    Log.d(TAG, "onClickAddZiel(): updateZiel - " + mParcourGID + " " + n);
                    mid = mZielSpeicher.updateZiel(mParcourGID, n, n+1);
                    // TODO Null Updates it hier ein Problem
                    Log.d(TAG, "onClickAddZiel(): updateZiel - Anzahl Updates: " + mid);
                }
                /*
                  jetzt neues Ziel hinzufügen
                 */
                Log.d(TAG, "onClickAddZiel(): insertZiel");
                mid = mZielSpeicher.insertZiel(
                        mParcourGID,         // Global Parcour ID
                        nZielNummer,         // Nummer
                        strZielName,         // Name
                        strLat,              // Lat Koordinate
                        strLon,              // Lon Koordinate
                        strZielDateiname);   // Dateiname
                Log.d(TAG, "onClickAddZiel(): insertZiel - ID: " + mid);
                /*
                  Anzahl der Ziele wird um Eins erhöht
                 */
                Log.d(TAG, "onClickAddZiel(): updateAnzahlZieleParcour");
                mid = mParcourSpeicher.updateAnzahlZiele(mParcourGID, mParcour.anzahl_ziele+1);
                Log.d(TAG, "onClickAddZiel(): End - " + mid);
                dialog.dismiss();
                finish();
            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert2 = builder2.create();
        alert2.show();
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
