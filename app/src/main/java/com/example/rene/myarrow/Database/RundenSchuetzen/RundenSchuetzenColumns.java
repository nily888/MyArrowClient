package com.example.rene.myarrow.Database.RundenSchuetzen;

/**
 * Created by nily on 15.12.15.
 */
public interface RundenSchuetzenColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * globaler Primaerschluessel.
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Schuetzen-ID
     *
     * TEXT
     */
    String SCHUETZENID = "schuetzenid";

    /**
     * Pflichtfeld: Schuetzen-GID
     *
     * TEXT
     */
    String SCHUETZENGID = "schuetzengid";

    /**
     * Pflichtfeld: Runden-ID
     *
     * TEXT
     */
    String RUNDENID = "rundenid";

    /**
     * Pflichtfeld: Runden-GID
     *
     * TEXT
     */
    String RUNDENGID = "rundengid";

    /**
     * Pflichtfeld: Gesamtergebnis
     *
     * INTEGER
     */
    String GESAMTERGEBNIS = "gesamtergebnis";

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
