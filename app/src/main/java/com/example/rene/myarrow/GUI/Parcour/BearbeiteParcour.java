package com.example.rene.myarrow.GUI.Parcour;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.ShowMap;
import com.example.rene.myarrow.misc.WoBinIch;

/**
 * Created by nily on 15.12.15.
 */
public class BearbeiteParcour extends AppCompatActivity implements LocationListener {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BearbeiteParcour.class.getSimpleName();


    /** Schnittstelle zur persistenten Speicher. */
    private ParcourSpeicher mParcourSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String mParcourGId;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle icicle) {

        // Super Methode aufrufen
        super.onCreate(icicle);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.parcour_bearbeite);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGId = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
        } else {
            Log.w(TAG, "Keine Parcour-ID übergeben");
        }

        // ParcourSpeicher initialisieren
        mParcourSpeicher = new ParcourSpeicher(this);

        //
        // GPS Request absetzen
        //
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    protected void onStart() {
        zeigeDetails();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    private void zeigeDetails() {
        Parcour mParcour = mParcourSpeicher.loadParcourDetails(mParcourGId);

        /* *
         * Parcour Name auslesen
         */
        EditText fldParcourName = (EditText) findViewById(R.id.txt_parcourname);
        fldParcourName.setText(mParcour.name);

        /* *
         * Anzahl Ziele auslesen
         */
        TextView fldAnzahlZiele = (TextView) findViewById(R.id.txt_anzahl_ziele);
        fldAnzahlZiele.setText(String.valueOf(mParcour.anzahl_ziele));

        /* *
         * GPS Koordinaten auslesen
         */
        Button fldGPSLatKoordinaten = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
        fldGPSLatKoordinaten.setText(String.valueOf(mParcour.gps_lat_koordinaten));
        Button fldGPSLonKoordinaten = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
        fldGPSLonKoordinaten.setText(String.valueOf(mParcour.gps_lon_koordinaten));

         /* *
         * Strasse auslesen
         */
        EditText fldStrasse = (EditText) findViewById(R.id.txt_strasse);
        fldStrasse.setText(mParcour.strasse);

         /* *
         * PLZ auslesen
         */
        EditText fldPLZ = (EditText) findViewById(R.id.txt_plz);
        fldPLZ.setText(mParcour.plz);

         /* *
         * Ort auslesen
         */
        EditText fldOrt = (EditText) findViewById(R.id.txt_ort);
        fldOrt.setText(mParcour.ort);

         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);
        fldStandard.setEnabled(mParcour.standard);

        /* *
         * Anmerkung auslesen
         */
        EditText fldAnmerkung = (EditText) findViewById(R.id.txt_anmerkungen);
        fldAnmerkung.setText(mParcour.anmerkung);
    }

    public void onClickUpdateParcour(View v) {
        /* *
         * Parcour Name auslesen
         */
        EditText fldParcourName = (EditText) findViewById(R.id.txt_parcourname);

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
        Button fldGPSLatKoordinaten = (Button) findViewById(R.id.txt_gps_lat_koordinaten);

         /* *
         * GPS Koordinaten auslesen
         */
        Button fldGPSLonKoordinaten = (Button) findViewById(R.id.txt_gps_lon_koordinaten);

         /* *
         * Anmerkung auslesen
         */
        EditText fldAnmerkung = (EditText) findViewById(R.id.txt_anmerkungen);

         /* *
         * Standard auslesen
         */
        CheckBox fldStandard = (CheckBox) findViewById(R.id.txt_standard);

        /*
          Update Parcour in der Datenbank speichern
         */
	    mParcourSpeicher.updateParcour(
                mParcourGId,
                fldParcourName.getText().toString(),
                fldStrasse.getText().toString(),
                fldPLZ.getText().toString(),
                fldOrt.getText().toString(),
                fldGPSLatKoordinaten.getText().toString(),
                fldGPSLonKoordinaten.getText().toString(),
                fldAnmerkung.getText().toString(),
                fldStandard.isChecked());
        finish();
    }

    public void onClickWoBinIch(View v) {
        EditText fldParcourName = (EditText) findViewById(R.id.txt_parcourname);
        Button fldGPSLatKoordinaten = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
        Button fldGPSLonKoordinaten = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
        if (!fldGPSLatKoordinaten.getText().toString().equals("") &&
            !fldGPSLatKoordinaten.getText().toString().equals("NULL") &&
             fldGPSLatKoordinaten.getText().toString() != null &&
            !fldGPSLonKoordinaten.getText().toString().equals("") &&
            !fldGPSLonKoordinaten.getText().toString().equals("NULL") &&
             fldGPSLonKoordinaten.getText().toString() != null) {

            String[][] zielListe = new String[1][3];
            final Intent i = new Intent(this, ShowMap.class);
            zielListe[0][0] = fldParcourName.getText().toString();
            zielListe[0][1] = fldGPSLatKoordinaten.getText().toString();
            zielListe[0][2] = fldGPSLonKoordinaten.getText().toString();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
            i.putExtras(mBundle);
            startActivity(i);

        }
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
            if (tempWo!=null) {
                Button fldGPSLat = (Button) findViewById(R.id.txt_gps_lat_koordinaten);
                fldGPSLat.setText(tempWo[0]);
                Button fldGPSLon = (Button) findViewById(R.id.txt_gps_lon_koordinaten);
                fldGPSLon.setText(tempWo[1]);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
