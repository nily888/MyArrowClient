package com.example.rene.myarrow.Database.Schuetzen;

import android.net.Uri;


/**
 * Created by nily on 14.12.15.
 */
public class Schuetzen implements SchuetzenColumns {

    /**
     * id der DB Tabelle in der MyArrow Datenbank.
     * */
    public long id;

    /**
     * gid der DB Tabelle in der MyArrow Datenbank.
     * */
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
        return "table=schuetzen"+
               "&" + Schuetzen.ID + "=" + String.valueOf(id) +
               "&" + Schuetzen.GID + "=" + String.valueOf(gid) +
               "&" + Schuetzen.NAME + "=" + name +
               "&" + Schuetzen.DATEINAME + "=" + dateiname +
               "&" + Schuetzen.ZEITSTEMPEL + "=" + zeitstempel;
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "schuetzen")
                .appendQueryParameter(Schuetzen.ID, String.valueOf(id))
                .appendQueryParameter(Schuetzen.GID, gid)
                .appendQueryParameter(Schuetzen.NAME, name)
                .appendQueryParameter(Schuetzen.DATEINAME, dateiname)
                .appendQueryParameter(Schuetzen.ZEITSTEMPEL, String.valueOf(zeitstempel));
        return builder.build().getEncodedQuery();
    }
}
