package com.example.rene.myarrow.Database.Parcour;

import android.net.Uri;

/**
 * Created by nily on 14.12.15.
 */
public class Parcour implements ParcourColumns{

    /**
     * Allgemeiner Schluessel zum Speichern von SMSs
     * in Schluessel-Wert-Paaren.
     */
    public static final String KEY_PARCOUR = "parcour";

    /**
     * id der DB Tabelle in der MyArrow Datenbank.
     */
    public long id;

    /**
     * Pflichtfeld: Globaler Primaerschuessel
     *
     * TEXT
     */
    public String gid;

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    public String name;

    /**
     * Pflichtfeld: Anzahl der Ziele
     *
     * INTEGER
     */
    public int anzahl_ziele;

    /**
     * Pflichtfeld: Strasse
     *
     * TEXT
     */
    public String strasse;

    /**
     * Pflichtfeld: PLZ
     *
     * TEXT
     */
    public String plz;

    /**
     * Pflichtfeld: Ort
     *
     * TEXT
     */
    public String ort;

    /**
     * Pflichtfeld: Ort
     *
     * Text
     */
    public String gps_lat_koordinaten;

    /**
     * Pflichtfeld: Ort
     *
     * Text
     */
    public String gps_lon_koordinaten;

    /**
     * Pflichtfeld: Anmerkung
     *
     * TEXT
     */
    public String anmerkung;

    /**
     * Pflichtfeld: Standard
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
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja)
     *
     * INT
     */
    public int transfered;

    /**
     * Datensatz als String konvertieren
     */
    public String toString() {
        return "table=parcour" +
               "&" + ID + "=" + String.valueOf(id) +
               "&" + GID + "=" + gid +
               "&" + NAME + "=" + name +
               "&" + ANZAHL_ZIELE + "=" + String.valueOf(anzahl_ziele) +
               "&" + STRASSE + "=" + strasse +
               "&" + PLZ + "=" + plz +
               "&" + ORT + "=" + ort +
               "&" + GPS_LAT_KOORDINATEN + "=" + gps_lat_koordinaten +
               "&" + GPS_LON_KOORDINATEN + "=" + gps_lon_koordinaten +
               "&" + ANMERKUNG + "=" + anmerkung +
               "&" + STANDARD + "=" + String.valueOf(standard) +
               "&" + ZEITSTEMPEL + "=" + String.valueOf(zeitstempel);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "parcour")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(NAME, name)
                .appendQueryParameter(ANZAHL_ZIELE, String.valueOf(anzahl_ziele))
                .appendQueryParameter(STRASSE, strasse)
                .appendQueryParameter(PLZ, plz)
                .appendQueryParameter(ORT, ort)
                .appendQueryParameter(GPS_LAT_KOORDINATEN, gps_lat_koordinaten)
                .appendQueryParameter(GPS_LON_KOORDINATEN, gps_lon_koordinaten)
                .appendQueryParameter(ANMERKUNG, anmerkung)
                .appendQueryParameter(STANDARD, String.valueOf(standard))
                .appendQueryParameter(ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
