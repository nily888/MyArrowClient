package com.example.rene.myarrow.Database.RundenSchuetzen;

public class RundenSchuetzenTbl implements RundenSchuetzenColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "rundenschuetzen";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gid            TEXT UNIQUE, " +
                    "schuetzengid   TEXT NOT NULL," +
                    "rundengid      TEXT NOT NULL," +
                    "gesamtergebnis INTEGER," +
                    "zeitstempel    LONG, " +
                    "transfered INTEGER " +
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
            " (schuetzenid, schuetzengid, rundenid, rundengid, gesamtergebnis, zeitstempel) " +
            " VALUES (?,?,?,?,?,?)";

    /**
     * SQL-Anweisung zur Loeschung aller Pfeile
     */
    public static final String STMT_RUNDENSCHUETZEN_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
            ID,
            GID,
            SCHUETZENGID,
            RUNDENGID,
            GESAMTERGEBNIS,
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
     * WHERE-Bedingung fuer SCHUETZENGID-Anfrage.
     */
    public static final String WHERE_SCHUETZENGID_EQUALS = SCHUETZENGID + "=?";

    /**
     * WHERE-Bedingung fuer aller Ziele einer Runde.
     */
    public static final String WHERE_RUNDENGID_EQUALS = RUNDENGID + "=?";

    /**
     * WHERE-Bedingung fuer aller Ziele einer Runde.
     */
    public static final String WHERE_RUNDEN_SCHUETZEN_EQUALS =
            RUNDENGID + "=? AND " + SCHUETZENGID +"=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private RundenSchuetzenTbl() {
    }
}
