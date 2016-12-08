package com.example.rene.myarrow.misc;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

public class ShowMap extends FragmentActivity implements OnMapReadyCallback {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ShowMap.class.getSimpleName();

    private GoogleMap mMap;
    private String[][] zielListe=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        /** Hole Übergabeparameter ab */
        Object[] objectArray = (Object[]) getIntent().getExtras().getSerializable(Konstante.IN_PARAM_GPS_ZIELE);
        if(objectArray!=null){
            zielListe = new String[objectArray.length][];
            for(int i=0;i<objectArray.length;i++){
                zielListe[i]=(String[]) objectArray[i];
            }
        }

        if (zielListe == null || zielListe.length<1) {
            finish();
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Variablen initiieren
        LatLng gpsParcour;
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
        Location lastLocation = new Location("von");
        Location currLocation = new Location("bis");
        float totalDistance = 0;
        // Marker fuer den Parcour setze und auf Strassenlevel zoomen
        mMap = googleMap;
        // Meinen Standpunkt anzeigen
        mMap.setMyLocationEnabled(true);
        // Ziele auf der Karte markieren
        for (int n = 0; (n < zielListe.length); n++) {

            if (!zielListe[n][1].equals(null)
                    && !zielListe[n][1].equals("null")
                    && !zielListe[n][1].equals("NULL")
                    && !zielListe[n][1].equals("")
                    && !zielListe[n][1].isEmpty()
                    &&  zielListe[n][1] != null &&
                !zielListe[n][2].equals(null)
                    && !zielListe[n][2].equals("null")
                    && !zielListe[n][2].equals("NULL")
                    && !zielListe[n][2].equals("")
                    && !zielListe[n][2].isEmpty()
                    &&  zielListe[n][2] != null ) {

                gpsParcour = new LatLng(Double.valueOf(zielListe[n][1]), Double.valueOf(zielListe[n][2]));

                // Marker auf der Karte setzen
                mMap.addMarker(new MarkerOptions().position(gpsParcour).title(zielListe[n][0]));

                // Punkte für den Weg / Linie setzen
                coordList.add(gpsParcour);

                // Berechnung der Entfernung zwischen den Zielen
                if (n == 0) {
                    lastLocation.setLatitude(gpsParcour.latitude);
                    lastLocation.setLongitude(gpsParcour.longitude);
                } else {

                    currLocation.setLatitude(gpsParcour.latitude);
                    currLocation.setLongitude(gpsParcour.longitude);
                    totalDistance += lastLocation.distanceTo(currLocation);
                    Log.d(TAG, "Distanz: " + lastLocation.distanceTo(currLocation));
                    lastLocation.setLatitude(currLocation.getLatitude());
                    lastLocation.setLongitude(currLocation.getLongitude());
                }
            }
        }

        if (!zielListe[0][1].equals(null)
                && !zielListe[0][1].equals("null")
                && !zielListe[0][1].equals("NULL")
                && !zielListe[0][1].equals("")
                && !zielListe[0][1].isEmpty()
                &&  zielListe[0][1]!= null &&
            !zielListe[0][2].equals(null)
                && !zielListe[0][2].equals("null")
                && !zielListe[0][2].equals("NULL")
                && !zielListe[0][2].equals("")
                && !zielListe[0][2].isEmpty()
                &&  zielListe[0][2] != null ) {

            // Für den Rückweg
            gpsParcour = new LatLng(Double.valueOf(zielListe[0][1]), Double.valueOf(zielListe[0][2]));
            coordList.add(gpsParcour);

            // Weg zwischen den Zielen anzeigen
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(coordList);
            polylineOptions.width(5).color(Color.BLUE).geodesic(true);
            mMap.addPolyline(polylineOptions);

            // Fokussieren und Zoom auf Strassenniveau
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gpsParcour, 15));

            // Und wieder zum Anfang zurück
            currLocation.setLatitude(Double.valueOf(zielListe[0][1]));
            currLocation.setLongitude(Double.valueOf(zielListe[0][2]));
            totalDistance += lastLocation.distanceTo(currLocation);
        }

        // Jetzt noch Entfernung anzeigen
        Toast.makeText(this, String.format(Locale.GERMAN, "Parcourlänge beträgt: %.2f km", (totalDistance /1000)), Toast.LENGTH_LONG).show();
    }

    public void showParcourOnMap(long parcourID){
        Bundle mBundle = new Bundle();
        ZielSpeicher zs = new ZielSpeicher(this);
        Cursor c = zs.loadZielListe(String.valueOf(parcourID));
        if (c != null || c.getCount()<1) {
            String[][] zielListe = new String[c.getCount()][3];
            int n = 0;
            c.moveToFirst();
            do {
                zielListe[n][0] = c.getString(3); // c.getColumnIndex(ZielTbl.NAME)
                zielListe[n][1] = c.getString(4); // c.getColumnIndex(ZielTbl.GPS_LAT_KOORDINATEN)
                zielListe[n][2] = c.getString(5); // c.getColumnIndex(ZielTbl.GPS_LON_KOORDINATEN)
                n++;
            } while (c.moveToNext());
            final Intent i = new Intent(this, ShowMap.class);
            mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
            i.putExtras(mBundle);
            startActivity(i);
        }
    }
}
