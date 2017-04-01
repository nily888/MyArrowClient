package com.example.rene.myarrow.Database.Parcour;

/**
 * Created by nily on 15.12.15.
 */
public class ParcourTbl implements ParcourColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "parcour";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "_id                 INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gid                 TEXT UNIQUE, " +
                    "name                TEXT NOT NULL, " +
                    "anzahl_ziele        INTEGER NOT NULL, " +
                    "strasse             TEXT, " +
                    "plz                 TEXT, " +
                    "ort                 TEXT, " +
                    "gps_lat_koordinaten TEXT, " +
                    "gps_lon_koordinaten TEXT, " +
                    "anmerkung           TEXT, " +
                    "standard            BOOLEAN, " +
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
            " (name, dateiname, standard, zeitstempel, transfered)" +
            " VALUES (?,?,?,?,?)";

    /**
     * SQL-Anweisung zur Loeschung aller Pfeile
     */
    public static final String STMT_Pfeil_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
            ID,
            GID,
            NAME,
            ANZAHL_ZIELE,
            STRASSE,
            PLZ,
            ORT,
            GPS_LAT_KOORDINATEN,
            GPS_LON_KOORDINATEN,
            ANMERKUNG,
            STANDARD,
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
     * WHERE-Bedingung fuer Parcour-Namen-Anfrage.
     */
    public static final String WHERE_NAME_EQUALS = NAME + "=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private ParcourTbl() {}

}
