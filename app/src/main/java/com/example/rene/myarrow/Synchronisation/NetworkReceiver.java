package com.example.rene.myarrow.Synchronisation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;



/**
 * Created by René Düber on 13.05.2016.
 */
public class NetworkReceiver extends BroadcastReceiver {

    /** Tag zum Loggen in der LogCat. */
    private static final String TAG = NetworkReceiver.class.getSimpleName();

    /**
     * Methode wird einer Netzwerk-Änderung aufgerufen.
     *
     * @param context
     *          Kontext der Anwendung
     * @param intent
     *          Intent, der die Session enthält
     */
    @Override
    public final void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "onReceive(): Begin");
        Log.d(TAG, "onReceive(): Intent = " + intent.getAction());

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        /**
         * O.K., gibt es eine WiFi Verbindung?
         */
        if (wifi.isAvailable()) {
            Log.d(TAG, "onReceive(): Network Available, start Thread");

            /**
             * Bin ich im richtgen Netzwerk => 192.168.178......
             */
            WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
            int ip = myWifiInfo.getIpAddress();
            String ipAddress = Formatter.formatIpAddress(ip);
            Log.d(TAG, "onReceive(): WiFi address is "
                    + android.text.format.Formatter.formatIpAddress(ip));

            if (ipAddress.startsWith(NetzwerkKonfigurator.SERVER_IP.substring(0,10))) {
                Log.d(TAG, "onReceive(): Richtiger Nummernbereich......");
                /**
                 * Ja ich bin im richtigen Netz, starte jetzt den Upload....
                 */
                new Thread() {
                    public void run() {
                        Log.d(TAG, "onReceive(): im Thread sendqueuedSMS() - started");
                        new SendeDatenService().selektiereDaten(context);
                        new EmpfangeDatenService(context).empfangeDaten(context);
                        Log.d(TAG, "onReceive(): im Thread sendqueuedSMS() - ended");
                    }
                }.start();
            } else {
                Log.d(TAG, "onReceive(): Falscher Nummernbereich");
            }
        }
        Log.d(TAG, "onReceive(): End");
    }
}
