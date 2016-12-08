package com.example.rene.myarrow.Database.Schuetzen;

/**
 * Created by nily on 14.12.15.
 */
public interface SchuetzenColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * globaler Primaerschluessel.
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Name
     *
     * TEXT
     */
    String NAME = "name";

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
