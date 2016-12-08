package com.example.rene.myarrow.Database.RundenZiel;

/**
 * Created by nily on 15.12.15.
 */
public interface RundenZielColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * globaler Primaerschluessel.
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Runden-ID
     *
     * LONG
     */
    String RUNDENID = "rundenid";

    /**
     * Pflichtfeld: Runden-GID
     *
     * TEXT
     */
    String RUNDENGID = "rundengid";

    /**
     * Pflichtfeld: Ziel-ID
     *
     * LONG
     */
    String ZIELID = "zielid";

    /**
     * Pflichtfeld: Ziel-GID
     *
     * TEXT
     */
    String ZIELGID = "zielgid";

    /**
     * Pflichtfeld: RundenSchuetzen-ID
     *
     * LONG
     */
    String RUNDENSCHUETZENID = "rundenschuetzenid";

    /**
     * Pflichtfeld: RundenSchuetzen-GID
     *
     * TEXT
     */
    String RUNDENSCHUETZENGID = "rundenschuetzengid";

    /**
     * Pflichtfeld: Nummer
     *
     * INTEGER
     */
    String NUMMER = "nummer";

    /**
     * Pflichtfeld: im ersten Schuss
     *
     * BOOLEAN
     */
    String EINS = "eins";

    /**
     * Pflichtfeld: im zweiten Schuss
     *
     * BOOLEAN
     */
    String ZWEI = "zwei";

    /**
     * Pflichtfeld: im dritten Schuss
     *
     * BOOLEAN
     */
    String DREI = "drei";

    /**
     * Pflichtfeld: im Kill
     *
     * BOOLEAN
     */
    String KILL = "kill";

    /**
     * Pflichtfeld: im Kill-Kill
     *
     * BOOLEAN
     */
    String KILLKILL = "killkill";

    /**
     * Pflichtfeld: Punkte
     *
     * INTEGER
     */
    String PUNKTE = "punkte";

    /**
     * Pflichtfeld: Anmerkung
     *
     * TEXT
     */
    String ANMERKUNG = "anmerkung";

    /**
     * Pflichtfeld: GPS Koordinaten
     *
     * DEZIMAL
     */
    String GPSKOORDINATEN = "gpskoordinaten";

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
