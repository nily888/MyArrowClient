package com.example.rene.myarrow.Database.Bogen;

/**
 * Created by nily on 14.12.15.
 */
public interface BogenColumns {

    /**
     * Primaerschluessel.
     * */
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
     * Pflichtfeld: Dateiname
     *
     * TEXT
     */
    String DATEINAME = "dateiname";

    /**
     * Pflichtfeld: Dateiname
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
