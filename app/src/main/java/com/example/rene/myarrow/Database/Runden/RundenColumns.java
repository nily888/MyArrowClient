package com.example.rene.myarrow.Database.Runden;

/**
 * Created by nily on 14.12.15.
 */
public interface RundenColumns {

    /**
     * Primaerschluessel.
     */
    String ID = "_id";

    /**
     * globaler Primaerschluessel.
     */
    String GID = "gid";

    /**
     * Pflichtfeld: Parcour-ID
     *
     * LONG
     */
    String PARCOURID = "parcourid";

    /**
     * Pflichtfeld: Parcour-GID
     *
     * TEXT
     */
    String PARCOURGID = "parcourgid";

    /**
     * Pflichtfeld: Bogen-ID
     *
     * LONG
     */
    String BOGENID = "bogenid";

    /**
     * Pflichtfeld: Bogen-GID
     *
     * TEXT
     */
    String BOGENGID = "bogengid";

    /**
     * Pflichtfeld: Pfeil-ID
     *
     * LONG
     */
    String PFEILID = "pfeilid";

    /**
     * Pflichtfeld: Pfeil-ID
     *
     * TEXT
     */
    String PFEILGID = "pfeilgid";

    /**
     * Zeitpunkt zum Start der Runde.
     *
     * LONG
     */
    String STARTZEIT = "startzeit";

    /**
     * Zeitpunkt zum Start der Runde.
     *
     * STRING
     */
    String S_STARTZEIT = "s_startzeit";

    /**
     * Zeitpunkt zum Ende der Runde.
     *
     * LONG
     */
    String ENDZEIT = "endzeit";

    /**
     * Wetter zum Start der Runde.
     *
     * TEXT
     */
    String WETTER = "wetter";

    /**
     * aktueller Punktestand.
     *
     * INTEGER
     */
    String PUNKTESTAND = "punktestand";

    /**
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja).
     *
     * INT
     */
    String TRANSFERED = "transfered";

}
