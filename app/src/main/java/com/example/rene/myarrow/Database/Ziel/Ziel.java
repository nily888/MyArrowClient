package com.example.rene.myarrow.Database.Ziel;

import android.net.Uri;

/**
 * Created by nily on 14.12.15.
 */
public class Ziel implements ZielColumns{

    /**
     * id der DB Tabelle in der MyArrow Datenbank.
     */
    public int id;

    /**
     * Pflichtfeld: Globaler Primaerschuessel
     *
     * TEXT
     */
    public String gid;

    /**
     * Pflichtfeld: ParcourGID
     *
     * TEXT
     */
    public String parcourgid;

    /**
     * Pflichtfeld: Nummer
     *
     * INTEGER
     */
    public int nummer;

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    public String name;

    /**
     * Pflichtfeld: GPS_LAT_KOORDINATEN
     *
     * TEXT
     */
    public String gps_lat_koordinaten;

    /**
     * Pflichtfeld: GPS_LON_KOORDINATEN
     *
     * TEXT
     */
    public String gps_lon_koordinaten;

    /**
     * Pflichtfeld: Dateiname
     *
     * TEXT
     */
    public String dateiname;

    /**
     * Zeitpunkt der letzten Änderung.
     *
     * LONG
     */
    public long zeitstempel;

    /**
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja)
     *
     * INT
     */
    public int transfered;

    /**
     * Datensatz als String konvertieren
     */
    public String toString() {
        return "table=ziel" +
                "&" + ID + "=" + String.valueOf(id) +
                "&" + GID + "=" + gid +
                "&" + PARCOURGID  + "=" + parcourgid +
                "&" + NUMMER + "=" + String.valueOf(nummer) +
                "&" + NAME + "=" + name +
                "&" + GPS_LAT_KOORDINATEN + "=" + gps_lat_koordinaten +
                "&" + GPS_LON_KOORDINATEN + "=" + gps_lon_koordinaten +
                "&" + DATEINAME + "=" + dateiname +
                "&" + ZEITSTEMPEL + "=" + String.valueOf(zeitstempel);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "ziel")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(PARCOURGID, parcourgid)
                .appendQueryParameter(NUMMER, String.valueOf(nummer))
                .appendQueryParameter(NAME, name)
                .appendQueryParameter(GPS_LAT_KOORDINATEN, gps_lat_koordinaten)
                .appendQueryParameter(GPS_LON_KOORDINATEN, gps_lon_koordinaten)
                .appendQueryParameter(DATEINAME, dateiname)
                .appendQueryParameter(ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
