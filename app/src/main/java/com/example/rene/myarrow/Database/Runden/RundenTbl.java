package com.example.rene.myarrow.Database.Runden;

/**
 * Created by nily on 15.12.15.
 */
public class RundenTbl implements RundenColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "runden";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gid TEXT, " +
                    "parcourgid TEXT NOT NULL," +
                    "bogengid TEXT NOT NULL," +
                    "pfeilgid TEXT NOT NULL," +
                    "startzeit LONG," +
                    "s_startzeit TEXT," +
                    "endzeit LONG," +
                    "wetter TEXT, " +
                    "transfered INTEGER " +
                    ");";

    /**
     * Standard-Sortierreihenfolge fuer die Tabelle.
     *
     * Sortiert wird nach Zeitstempel absteigend.
     */
    public static final String DEFAULT_SORT_ORDER = STARTZEIT + " DESC";

    /**
     * SQL Anweisung zur Loeschung der Tabelle.
     */
    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * SQL Anweisung fuer Erzeugung eines
     * neuen Eintrages.
     */
    public static final String STMT_INSERT = "INSERT INTO "+ TABLE_NAME +
            " (parcourid, parcourgid, bogenid, bogengid, pfeilid, pfeilgid, startzeit, endzeit, wetter)" +
            " VALUES (?,?,?,?,?,?,?,?,?)";

    /**
     * SQL-Anweisung zur Loeschung aller Runden
     */
    public static final String STMT_Pfeil_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
            ID,
            GID,
            PARCOURGID,
            BOGENGID,
            PFEILGID,
            STARTZEIT,
            S_STARTZEIT,
            ENDZEIT,
            WETTER,
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
     * WHERE-Bedingung fuer ParcourGID-Anfrage.
     */
    public static final String WHERE_PARCOURGID_EQUALS = PARCOURGID + "=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private RundenTbl() {
    }

}
