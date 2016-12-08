package com.example.rene.myarrow.Database.RundenZiel;

import android.net.Uri;

/**
 * Created by nily on 15.12.15.
 */
public class RundenZiel implements RundenZielColumns{

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
     * Pflichtfeld: Runden-ID
     *
     * LONG
     */
    public long rundenid;

    /**
     * Pflichtfeld: Runden-GID
     *
     * TEXT
     */
    public String rundengid;

    /**
     * Pflichtfeld: Ziel-ID
     *
     * LONG
     */
    public long zielid;

    /**
     * Pflichtfeld: Ziel-GID
     *
     * TEXT
     */
    public String zielgid;

    /**
     * Pflichtfeld: RundenSchuetzen-ID
     *
     * LONG
     */
    public long rundenschuetzenid;

    /**
     * Pflichtfeld: RundenSchuetzen-GID
     *
     * TEXT
     */
    public String rundenschuetzengid;

    /**
     * Pflichtfeld: Nummer
     *
     * INTEGER
     */
    public int nummer;

    /**
     * Pflichtfeld: im ersten Schuss
     *
     * BOOLEAN
     */
    public boolean eins;

    /**
     * Pflichtfeld: im zweiten Schuss
     *
     * BOOLEAN
     */
    public boolean zwei;

    /**
     * Pflichtfeld: im dritten Schuss
     *
     * BOOLEAN
     */
    public boolean drei;

    /**
     * Pflichtfeld: im Kill
     *
     * BOOLEAN
     */
    public boolean kill;

    /**
     * Pflichtfeld: im Kill-Kill
     *
     * BOOLEAN
     */
    public boolean killkill;

    /**
     * Pflichtfeld: Punkte
     *
     * INTEGER
     */
    public int punkte;

    /**
     * Pflichtfeld: Anmerkung
     *
     * TEXT
     */
    public String anmerkung;

    /**
     * Pflichtfeld: GPS Koordinaten
     *
     * Double
     */
    public double gpskoordinaten;

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
        return "table=rundenziel" +
                "&" + ID + "=" + String.valueOf(id) +
                "&" + GID + "=" + gid +
                "&" + RUNDENID + "=" + String.valueOf(rundenid) +
                "&" + RUNDENGID + "=" + rundengid +
                "&" + ZIELID + "=" + String.valueOf(zielid) +
                "&" + ZIELGID + "=" + zielgid +
                "&" + RUNDENSCHUETZENID + "=" + String.valueOf(rundenschuetzenid) +
                "&" + RUNDENSCHUETZENGID + "=" + rundenschuetzengid +
                "&" + NUMMER + "=" + String.valueOf(nummer) +
                "&" + EINS + "=" + String.valueOf(eins) +
                "&" + ZWEI + "=" + String.valueOf(zwei) +
                "&" + DREI + "=" + String.valueOf(drei) +
                "&" + KILL + "=" + String.valueOf(kill) +
                "&" + KILLKILL + "=" + String.valueOf(killkill) +
                "&" + PUNKTE + "=" + String.valueOf(punkte) +
                "&" + ANMERKUNG + "=" + anmerkung +
                "&" + GPSKOORDINATEN + "=" + String.valueOf(gpskoordinaten) +
                "&" + DATEINAME + "=" + dateiname +
                "&" + ZEITSTEMPEL + "=" + String.valueOf(zeitstempel);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "rundenziel")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(RUNDENID, String.valueOf(rundenid))
                .appendQueryParameter(RUNDENGID, rundengid)
                .appendQueryParameter(ZIELID, String.valueOf(zielid))
                .appendQueryParameter(ZIELGID, zielgid)
                .appendQueryParameter(RUNDENSCHUETZENID, String.valueOf(rundenschuetzenid))
                .appendQueryParameter(RUNDENSCHUETZENGID, rundenschuetzengid)
                .appendQueryParameter(NUMMER, String.valueOf(nummer))
                .appendQueryParameter(EINS, String.valueOf(eins))
                .appendQueryParameter(ZWEI, String.valueOf(zwei))
                .appendQueryParameter(DREI, String.valueOf(drei))
                .appendQueryParameter(KILL, String.valueOf(kill))
                .appendQueryParameter(KILLKILL, String.valueOf(killkill))
                .appendQueryParameter(PUNKTE, String.valueOf(punkte))
                .appendQueryParameter(ANMERKUNG, anmerkung)
                .appendQueryParameter(GPSKOORDINATEN, String.valueOf(gpskoordinaten))
                .appendQueryParameter(DATEINAME, dateiname)
                .appendQueryParameter(ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
