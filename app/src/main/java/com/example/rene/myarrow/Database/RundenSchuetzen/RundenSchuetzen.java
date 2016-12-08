package com.example.rene.myarrow.Database.RundenSchuetzen;

import android.net.Uri;

/**
 * Created by nily on 15.12.15.
 */
public class RundenSchuetzen implements RundenSchuetzenColumns{

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
     * Pflichtfeld: Schuetzen-GID
     *
     * TEXT
     */
    public String schuetzengid;

    /**
     * Pflichtfeld: Runden-GID
     *
     * TEXT
     */
    public String rundengid;

    /**
     * Pflichtfeld: Gesamtergebnis
     *
     * INTEGER
     */
    public int gesamtergebnis;

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
        return "table=rundenschuetzen" +
                "&" + ID + "=" + String.valueOf(id) +
                "&" + GID + "=" + gid +
                "&" + SCHUETZENGID + "=" + schuetzengid +
                "&" + RUNDENGID + "=" + rundengid +
                "&" + GESAMTERGEBNIS + "=" + String.valueOf(gesamtergebnis) +
                "&" + ZEITSTEMPEL + "=" + String.valueOf(zeitstempel);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "rundenschuetzen")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(SCHUETZENGID, schuetzengid)
                .appendQueryParameter(RUNDENGID, rundengid)
                .appendQueryParameter(GESAMTERGEBNIS, String.valueOf(gesamtergebnis))
                .appendQueryParameter(ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
