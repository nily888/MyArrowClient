package com.example.rene.myarrow.Database.Schuetzen;

import com.example.rene.myarrow.Database.Schuetzen.SchuetzenColumns;

/**
 * Schnittstelle zur Tabelle PfeilTbl
 * Die Klasse liefert
 *
 * SQL-Code zur Erzeugung der Tabelle
 * SQL-Code für alle für MyArrow erforderlichen
 * Statements
 *
 * @author Rene Dueber
 */
public final class SchuetzenTbl implements SchuetzenColumns {

    /**
     * Name der Datenbanktabelle.
     */
    public static final String TABLE_NAME = "schuetzen";

    /**
     * SQL Anweisung zur Schemadefinition.
     */
    public static final String SQL_CREATE =
        "CREATE TABLE " + TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "gid TEXT UNIQUE, " +
            "name TEXT NOT NULL," +
            "dateiname TEXT," +
            "zeitstempel LONG, " +
            "transfered INTEGER " +
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
            " (name, dateiname, zeitstempel, transfered) " + "VALUES (?,?,?,0)";

    /**
     * SQL-Anweisung zur Loeschung aller Pfeile
     */
    public static final String STMT_Pfeil_DELETE = "DELETE " + TABLE_NAME;

    /** Liste aller bekannten Attribute. */
    public static final String[] ALL_COLUMNS = new String[] {
                ID,
                GID,
                NAME,
                DATEINAME,
                ZEITSTEMPEL,
                TRANSFERED
    };

    /**
     * WHERE-Bedingung fuer GID-Anfrage.
     */
    public static final String WHERE_GID_EQUALS = GID + "=?";

    /**
     * WHERE-Bedingung fuer ID-Anfrage.
     */
    public static final String WHERE_ID_EQUALS = ID + "=?";

    /**
     * WHERE-Bedingung fuer Schuetze-Anfrage.
     */
    public static final String WHERE_SCHUETZE_EQUALS = NAME + "=?";

    /**
     * Klasse enthaelt nur Konstanten.
     * Daher keine Objekterzeugung vorgesehen.
     */
    private SchuetzenTbl() {
    }
}
