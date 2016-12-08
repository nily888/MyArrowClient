package com.example.rene.myarrow.Database.Parcour;

/**
 * Created by nily on 14.12.15.
 */
public interface ParcourColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * Globaler Primaerschluessel
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    String NAME = "name";

    /**
     * Pflichtfeld: Anzahl der Ziele
     *
     * INTEGER
     */
    String ANZAHL_ZIELE = "anzahl_ziele";

    /**
     * Pflichtfeld: Strasse
     *
     * TEXT
     */
    String STRASSE = "strasse";

    /**
     * Pflichtfeld: PLZ
     *
     * TEXT
     */
    String PLZ = "plz";

    /**
     * Pflichtfeld: Ort
     *
     * TEXT
     */
    String ORT = "ort";

    /**
     * Pflichtfeld: Ort
     *
     * Text
     */
    String GPS_LAT_KOORDINATEN = "gps_lat_koordinaten";

    /**
     * Pflichtfeld: Ort
     *
     * Text
     */
    String GPS_LON_KOORDINATEN = "gps_lon_koordinaten";

    /**
     * Pflichtfeld: Anmerkung
     *
     * TEXT
     */
    String ANMERKUNG = "anmerkung";

    /**
     * Pflichtfeld: Standard
     *
     * BOOLEAN
     */
    String STANDARD = "standard";

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
