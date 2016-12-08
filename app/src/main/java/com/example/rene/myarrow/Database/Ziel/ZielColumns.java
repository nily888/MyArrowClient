package com.example.rene.myarrow.Database.Ziel;

/**
 * Created by nily on 14.12.15.
 */
public interface ZielColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * globaler Primaerschluessel.
     *
     * TEXT
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Parcour-GID
     *
     * TEXT
     */
    String PARCOURID = "parcourid";

    /**
     * Pflichtfeld: ParcourGID
     *
     * TEXT
     */
    String PARCOURGID = "parcourgid";

    /**
     * Pflichtfeld: Nummer
     *
     * INTEGER
     */
    String NUMMER = "nummer";

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    String NAME = "name";

    /**
     * Pflichtfeld: GPS_LAT_KOORDINATEN
     *
     * TEXT
     */
    String GPS_LAT_KOORDINATEN = "gps_lat_koordinaten";

    /**
     * Pflichtfeld: GPS_LON_KOORDINATEN
     *
     * TEXT
     */
    String GPS_LON_KOORDINATEN = "gps_lon_koordinaten";

    /**
     * Pflichtfeld: Dateiname
     *
     * TEXT
     */
    String DATEINAME = "dateiname";

    /**
     * Zeitpunkt der letzten Änderung.
     *
     * LONG
     */
    String ZEITSTEMPEL = "zeitstempel";

    /**
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja).
     *
     * INT
     */
    String TRANSFERED = "transfered";

}
