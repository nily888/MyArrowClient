package com.example.rene.myarrow.misc;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by nily on 26.12.2015.
 */
public class WoBinIch implements LocationListener {

    LocationManager locationManager;
    LocationListener locationListener;
    Criteria cri;
    String provider;
    Context mContext;
    // locationManager.removeUpdates(this);

    public WoBinIch(Context c) {
        mContext = c;
        locationManager=(LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        cri=new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_FINE);
        cri.setPowerRequirement(Criteria.POWER_LOW);
        provider=locationManager.getBestProvider(cri,false);
    }

    public String[] getLocation() {

        String[] mAntwort;
        if(provider!=null & !provider.equals("")) {

            Location location=locationManager.getLastKnownLocation(provider);

            if(location!=null) {
                mAntwort = new String[] {String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())};
            }
            else{
                mAntwort = null;
                Toast.makeText(mContext, "aktuellen Ort nicht gefunden", Toast.LENGTH_LONG).show();
            }
        }
        else {
            mAntwort = null;
            Toast.makeText(mContext,"Provider ist NULL",Toast.LENGTH_LONG).show();
        }
        return mAntwort;
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

}

