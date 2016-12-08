package com.example.rene.myarrow.Database.Ziel;

/**
 * Created by nily on 15.12.15.
 */
public class ZielTbl implements ZielColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "ziel";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "_id                 INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gid                 TEXT, " +
                    "parcourgid          TEXT NOT NULL," +
                    "nummer              INTEGER NOT NULL," +
                    "name                TEXT NOT NULL," +
                    "gps_lat_koordinaten TEXT, " +
                    "gps_lon_koordinaten TEXT, " +
                    "dateiname           TEXT, " +
                    "zeitstempel         LONG, " +
                    "transfered          INTEGER " +
                    ");";

    /**
     * Standard-Sortierreihenfolge fuer die Tabelle.
     *
     * Sortiert wird nach Zeitstempel absteigend.
     */
    public static final String DEFAULT_SORT_ORDER = NAME + " DESC";

    /**
     * SQL Anweisung zur Loeschung der Tabelle.
     */
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * SQL Anweisung fuer Erzeugung eines
     * neuen Eintrages.
     */
    public static final String STMT_INSERT = "INSERT INTO "+ TABLE_NAME +
            " (parcourid, parcourgid, name, dateiname, standard, zeitstempel)" +
            " VALUES (?,?,?,?,?,?)";

    /**
     * SQL-Anweisung zur Loeschung aller Ziele
     */
    public static final String STMT_ZIELE_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
            ID,
            GID,
            PARCOURGID,
            NUMMER,
            NAME,
            GPS_LAT_KOORDINATEN,
            GPS_LON_KOORDINATEN,
            DATEINAME,
            ZEITSTEMPEL,
            TRANSFERED
    };

    /**
     * WHERE-Bedingung fuer ID-Anfrage.
     */
    public static final String WHERE_ID_EQUALS =
            ID + "=?";

    /**
     * WHERE-Bedingung fuer GID-Anfrage.
     */
    public static final String WHERE_GID_EQUALS =
            GID + "=?";

    /**
     * WHERE-Bedingung fuer Parcour-Nummer-Anfrage.
     */
    public static final String WHERE_PARCOURGID_NUMMER_EQUALS =
            PARCOURGID + "=? AND " + NUMMER + "=?";

    /**
     * WHERE-Bedingung fuer Parcour-Anfrage.
     */
    public static final String WHERE_PARCOURGID_EQUALS =
            PARCOURGID + "=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private ZielTbl() {
    }

}
