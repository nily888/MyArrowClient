package com.example.rene.myarrow.GUI.Parcour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.WoBinIch;

/**
 * Created by nily on 15.12.15.
 */
public class NeuerParcour extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = NeuerParcour.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private ParcourSpeicher mParcourSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mParcourGId;
    private Parcour parcourCursor;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.parcour_neuer);

        // ParcourSpeicher initialisieren
        mParcourSpeicher = new ParcourSpeicher(this);

    }

    public void onClickCreateParcour(View v) {


        /**
         * Neue Runde anlegen und Daten entsprechend abspeichern
         */
        // RundenSpeicher rundenSpeicher = new RundenSpeicher(this);

        /* *
         * Parcour Name auslesen
         */
        EditText fldParcourName = (EditText) findViewById(R.id.txt_parcourname);

         /* *
         * Anzahl der Ziele auf dem Parcour auslesen
         */
        EditText fldAnzahlZiele = (EditText) findViewById(R.id.txt_anzahl_ziele);

         /* *
         * Strasse auslesen
         */
        EditText fldStrasse = (EditText) findViewById(R.id.txt_strasse);

         /* *
         * PLZ auslesen
         */
        EditText fldPLZ = (EditText) findViewById(R.id.txt_plz);

         /* *
          * Ort auslesen
          */
        EditText fldOrt = (EditText) findViewById(R.id.txt_ort);

        /* *
         * GPS Koordinaten auslesen
         */
        TextView fldGPSLatKoordinaten = (TextView) findViewById(R.id.txt_gps_lat_koordinaten);

        /* *
         * GPS Koordinaten auslesen
         */
        TextView fldGPSLonKoordinaten = (TextView) findViewById(R.id.txt_gps_lon_koordinaten);

         /* *
         * Anmerkung auslesen
         */
        EditText fldAnmerkung = (EditText) findViewById(R.id.txt_anmerkungen);

        /**
         * Startzeit notieren
         */
        long tempTimeStamp = new Date().getTime();

        //
        //
        //
        if (fldAnzahlZiele.getText().toString().isEmpty() || Integer.valueOf(fldAnzahlZiele.getText().toString()) == 0) {
            Toast.makeText(this, "Der Parcour muss mindestens ein Ziel enthalten!!", Toast.LENGTH_LONG).show();
            return;
        }

        /**
         * Parcour in der Datenbank speichern
         */
	    String mParcourGID = mParcourSpeicher.insertParcour(
			fldParcourName.getText().toString(),
			Integer.valueOf(fldAnzahlZiele.getText().toString()),
			fldStrasse.getText().toString(),
			fldPLZ.getText().toString(),
			fldOrt.getText().toString(),
            fldGPSLatKoordinaten.getText().toString(),
            fldGPSLonKoordinaten.getText().toString(),
			fldAnmerkung.getText().toString(),
			false,
			tempTimeStamp);

	    /**
 	     * Entsprechende Ziele in einer Schleife anlegen
	     */
        // ZielSpeicher initialisieren
        ZielSpeicher mZielSpeicher = new ZielSpeicher(this);
	    for ( int n= 1; n <= Integer.valueOf(fldAnzahlZiele.getText().toString()); n++) {
            Log.d(TAG, "onClickCreateParcour(): insertZiel - " + n);
            mZielSpeicher.insertZiel(
                mParcourGID,
                n,
                (fldParcourName.getText().toString() + "-" + String.valueOf(n)),
                "",
                "",
                "");
	    }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parcour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_getlocation) {
            String[] tempWo = new WoBinIch(this).getLocation();
            if (tempWo!= null) {
                TextView fldGPSLat = (TextView) findViewById(R.id.txt_gps_lat_koordinaten);
                fldGPSLat.setText(tempWo[0]);
                TextView fldGPSLon = (TextView) findViewById(R.id.txt_gps_lon_koordinaten);
                fldGPSLon.setText(tempWo[1]);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
