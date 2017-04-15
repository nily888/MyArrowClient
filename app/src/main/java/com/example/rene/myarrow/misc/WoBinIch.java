package com.example.rene.myarrow.misc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_FINE);
        cri.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(cri, false);
    }

    public String[] getLocation() {

        String[] mAntwort;
        if (provider != null & !provider.equals("")) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);

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

