package com.example.rene.myarrow.Database.RundenZiel;

/**
 * Created by nily on 15.12.15.
 */
public class RundenZielTbl implements RundenZielColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "rundenziel";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "_id                INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gid                TEXT, " +
                    "rundengid          TEXT NOT NULL," +
                    "zielgid            TEXT NOT NULL," +
                    "rundenschuetzengid TEXT NOT NULL," +
                    "nummer             INTEGER NOT NULL," +
                    "eins               BOOLEAN," +
                    "zwei               BOOLEAN," +
                    "drei               BOOLEAN," +
                    "kill               BOOLEAN," +
                    "killkill           BOOLEAN," +
                    "punkte             INTEGER," +
                    "anmerkung          TEXT," +
                    "gpskoordinaten     DOUBLE," +
                    "dateiname          TEXT," +
                    "zeitstempel        LONG, " +
                    "transfered         INTEGER " +
                    ");";

    /**
     * Standard-Sortierreihenfolge fuer die Tabelle.
     *
     * Sortiert wird nach Zeitstempel absteigend.
     */
    public static final String DEFAULT_SORT_ORDER = ZEITSTEMPEL + " DESC";

    /**
     * SQL Anweisung zur Loeschung der Tabelle.
     */
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * SQL Anweisung fuer Erzeugung eines
     * neuen Eintrages.
     */
    public static final String STMT_INSERT = "INSERT INTO "+ TABLE_NAME +
            " (rundenid, rundengid, zielid, zielgid, rundenschuetzenid, rundenschuetzengid," +
            " nummer, eins, zwei, drei, kill, killkill," +
            " punkte, anmerkung, gpskoordinaten, dateiname, zeitstempel) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * SQL-Anweisung zur Loeschung aller Pfeile
     */
    public static final String STMT_RUNDENZIEL_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
            ID,
            GID,
            RUNDENGID,
            ZIELGID,
            RUNDENSCHUETZENGID,
            NUMMER,
            EINS,
            ZWEI,
            DREI,
            KILL,
            KILLKILL,
            PUNKTE,
            ANMERKUNG,
            DATEINAME,
            ZEITSTEMPEL,
            TRANSFERED
    };

    /**
     * WHERE-Bedingung fuer ID-Anfrage.
     */
    public static final String WHERE_ID_EQUALS = ID + "=?";

    /**
     * WHERE-Bedingung fuer GID-Anfrage.
     */
    public static final String WHERE_GID_EQUALS = GID + "=?";

    /**
     * WHERE-Bedingung fuer alle Ziele einer Runde.
     */
    public static final String WHERE_RUNDENGID_EQUALS = RUNDENGID + "=?";

    /**
     * WHERE-Bedingung fuer aller Ziele einer Runde eines Schuetzen
     */
    public static final String WHERE_RUNDEN_NUMMER_SCHUETZEN_EQUALS =
            RUNDENGID + "=? AND " + NUMMER +"=? AND " + RUNDENSCHUETZENGID +"=?";

    /**
     * WHERE-Bedingung fuer aller Ziele einer Runde.
     */
    public static final String WHERE_RUNDENGID_NUMMER_EQUALS =
            RUNDENGID + "=? AND " + NUMMER +"=?";

    /**
     * Bildet die Summe über eine Runde
     */
    public static final String WHERE_SUMME_EINE_RUNDE_EIN_SCHUETZE =
            RUNDENGID + "=? AND " + RUNDENSCHUETZENGID + "=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private RundenZielTbl() {
    }
}
