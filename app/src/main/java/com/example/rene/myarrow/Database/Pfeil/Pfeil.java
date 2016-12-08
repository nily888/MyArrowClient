package com.example.rene.myarrow.Database.Pfeil;

import android.net.Uri;

/**
 * Created by nily on 14.12.15.
 */
public class Pfeil implements PfeilColumns{

    /**
     * id der DB Tabelle in der MyArrow Datenbank.
     */
    public long id;

    /**
     * Globaler Primaerschluessel
     */
    public String gid;

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    public String name;

    /**
     * Pflichtfeld: Dateiname
     *
     * TEXT
     */
    public String dateiname;

    /**
     * Pflichtfeld: Dateiname
     *
     * BOOLEAN
     */
    public boolean standard;

    /**
     * Zeitpunkt der letzten Änderung.
     *
     * LONG
     */
    public long zeitstempel;

    /**
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja).
     *
     * INT
     */
    public int transfered;

    /**
     * Datensatz als String konvertieren
     */
    public String toString() {
        return "table=pfeil" +
                "&" + ID + "=" + String.valueOf(id) +
                "&" + GID + "=" + gid +
                "&" + NAME + "=" + name +
                "&" + DATEINAME + "=" + dateiname +
                "&" + STANDARD + "=" + String.valueOf(standard) +
                "&" + ZEITSTEMPEL + "=" + String.valueOf(zeitstempel);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "pfeil")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(NAME, name)
                .appendQueryParameter(DATEINAME, dateiname)
                .appendQueryParameter(STANDARD, String.valueOf(standard))
                .appendQueryParameter(ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
