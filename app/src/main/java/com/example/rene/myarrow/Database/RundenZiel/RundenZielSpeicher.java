package com.example.rene.myarrow.Database.RundenZiel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;

import java.util.Date;

/**
 * Created by nily on 15.12.15.
 */
public class RundenZielSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "RundenZielSpeicher";

    /** Verweis auf die Mobilfunknummern-Datenbank. */
    private MyArrowDB mDb;
    private Context mContext;
    /**
     *
     *
     * Erzeugt einen neuen SMSSpeicher. <br>
     * Dabei wird sichergestellt, dass die zugrundeliegende
     * Datenbank unmittelbar nutzbar ist.
     *
     * @param context
     *          Kontext der Anwendung, für die der Speicher
     *          gültig sein soll.
     */
    public RundenZielSpeicher(Context context) {
        mDb = MyArrowDB.getInstance(context);
        oeffnen();
        mContext = context;
    }
    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private RundenZielSpeicher() {
        Log.d(TAG, "RundenzielSpeicher unused.");
    }

    public long insertRundenziel(
            String rundengid,
            String zielgid,
            String rundenschuetzengid,
            int nummer,
            boolean eins,
            boolean zwei,
            boolean drei,
            boolean kill,
            boolean killkill,
            int punkte,
            String anmerkung,
            String dateiname,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /**
             * Daten einfügen
             */
            daten.put(RundenZielTbl.RUNDENGID, rundengid);
            daten.put(RundenZielTbl.ZIELGID, zielgid);
            daten.put(RundenZielTbl.RUNDENSCHUETZENGID, rundenschuetzengid);
            daten.put(RundenZielTbl.NUMMER, nummer);
            daten.put(RundenZielTbl.EINS,eins);
            daten.put(RundenZielTbl.ZWEI, zwei);
            daten.put(RundenZielTbl.DREI, drei);
            daten.put(RundenZielTbl.KILL, kill);
            daten.put(RundenZielTbl.KILLKILL, killkill);
            daten.put(RundenZielTbl.PUNKTE, punkte);
            daten.put(RundenZielTbl.ANMERKUNG, anmerkung);
            daten.put(RundenZielTbl.DATEINAME, dateiname);
            daten.put(RundenZielTbl.TRANSFERED, 0);
            daten.put(RundenZielTbl.ZEITSTEMPEL, zeitstempel);
            final long id = dbCon.insertOrThrow(RundenZielTbl.TABLE_NAME, null,
                    daten);

            /**
             * zunächst Device-Id (z.B. IMEI) auslesen
             */
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceid = tm.getDeviceId();
            if(deviceid==null){
                Log.d(TAG, "insertRundenziel(): Keine DeviceID gefunden, auf 000000000000000 gesetzt!!");
                deviceid="000000000000000";
            }

            /**
             * Globale ID aktualisieren
             */
            daten.clear();
            daten.put(RundenZielTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(RundenZielTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    RundenZielTbl.TABLE_NAME,               // Tabelle
                    daten,                                  // Daten
                    RundenZielTbl.WHERE_ID_EQUALS,          // Where
                    new String[]{String.valueOf(id)});      // Parameter für WHERE

            return id;
        } finally {
            dbCon.close();
        }
    }

    public long insertRundenziel(RundenZiel rundenziel) {
        return insertRundenziel(
                rundenziel.rundengid,
                rundenziel.zielgid,
                rundenziel.rundenschuetzengid,
                rundenziel.nummer,
                rundenziel.eins,
                rundenziel.zwei,
                rundenziel.drei,
                rundenziel.kill,
                rundenziel.killkill,
                rundenziel.punkte,
                rundenziel.anmerkung,
                rundenziel.dateiname,
                rundenziel.zeitstempel);
    }

    public long updateRundenziel(
            String rundengid,
            String rundenschuetzengid,
            int nummer,
            boolean eins,
            boolean zwei,
            boolean drei,
            boolean kill,
            boolean killkill,
            int punkte,
            String anmerkung,
            String dateiname,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        daten.put(RundenZielTbl.EINS,eins);
        daten.put(RundenZielTbl.ZWEI, zwei);
        daten.put(RundenZielTbl.DREI, drei);
        daten.put(RundenZielTbl.KILL, kill);
        daten.put(RundenZielTbl.KILLKILL, killkill);
        daten.put(RundenZielTbl.PUNKTE, punkte);
        daten.put(RundenZielTbl.ANMERKUNG, anmerkung);
        daten.put(RundenZielTbl.DATEINAME, dateiname);
        daten.put(RundenZielTbl.TRANSFERED, 0);
        daten.put(RundenZielTbl.ZEITSTEMPEL, zeitstempel);

        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenZielTbl.TABLE_NAME, daten,
                    RundenZielTbl.WHERE_RUNDEN_NUMMER_SCHUETZEN_EQUALS,
                    new String[]{ rundengid, String.valueOf(nummer), rundenschuetzengid});
        } finally {
            dbCon.close();
        }
    }

    public long updateRundenziel(RundenZiel rundenziel) {
        return updateRundenziel(
                rundenziel.rundengid,
                rundenziel.rundenschuetzengid,
                rundenziel.nummer,
                rundenziel.eins,
                rundenziel.zwei,
                rundenziel.drei,
                rundenziel.kill,
                rundenziel.killkill,
                rundenziel.punkte,
                rundenziel.anmerkung,
                rundenziel.dateiname,
                rundenziel.zeitstempel);
    }

    /**
     * Punkte am Ziel auf Null setzen
     *
     * @param gid RundenZielID
     * @return
     */
    public long updatePunkteToZero(String gid){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenZielTbl.PUNKTE, 0);
        dataToInsert.put(RundenZielTbl.TRANSFERED, 0);
        dataToInsert.put(RundenZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final long nRows = dbCon.update(RundenZielTbl.TABLE_NAME, dataToInsert, RundenZielTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        dbCon.close();
        return nRows;
    }

    /**
     *
     */
    public boolean storeForgeinRundenZiel(RundenZiel rundenziel) {
        Log.d(TAG, "storeForgeinRundenZiel(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(RundenZielTbl.GID, rundenziel.gid);
        daten.put(RundenZielTbl.RUNDENGID, rundenziel.rundengid);
        daten.put(RundenZielTbl.ZIELGID, rundenziel.zielgid);
        daten.put(RundenZielTbl.RUNDENSCHUETZENGID, rundenziel.rundenschuetzengid);
        daten.put(RundenZielTbl.NUMMER, rundenziel.nummer);
        daten.put(RundenZielTbl.EINS,rundenziel.eins);
        daten.put(RundenZielTbl.ZWEI, rundenziel.zwei);
        daten.put(RundenZielTbl.DREI, rundenziel.drei);
        daten.put(RundenZielTbl.KILL, rundenziel.kill);
        daten.put(RundenZielTbl.KILLKILL, rundenziel.killkill);
        daten.put(RundenZielTbl.PUNKTE, rundenziel.punkte);
        daten.put(RundenZielTbl.ANMERKUNG, rundenziel.anmerkung);
        daten.put(RundenZielTbl.DATEINAME, rundenziel.dateiname);
        daten.put(RundenZielTbl.TRANSFERED, 1);
        daten.put(RundenZielTbl.ZEITSTEMPEL, rundenziel.zeitstempel);
        Log.d(TAG, "storeForgeinRundenZiel(): RundenZiel " + rundenziel.toString() + " erzeugt.");
        Log.d(TAG, "storeForgeinRundenZiel(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, RundenZielTbl.TABLE_NAME);
    }

    public boolean deleteDateiname(String gid){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenZielTbl.DATEINAME, "");
        dataToInsert.put(RundenZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final long nRows = dbCon.update(RundenZielTbl.TABLE_NAME, dataToInsert, RundenZielTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        dbCon.close();
        return (nRows==1);
    }

    public boolean deleteRundenziel(String gid) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        try {
            anzahlLoeschungen = dbCon.delete(
                    RundenZielTbl.TABLE_NAME,
                    RundenZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
        return anzahlLoeschungen == 1;
    }

    public int deleteRundenzielWithRundenID(String rundenGID) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        try {
            anzahlLoeschungen = dbCon.delete(
                    RundenZielTbl.TABLE_NAME,
                    RundenZielTbl.WHERE_RUNDENGID_EQUALS,
                    new String[]{ rundenGID });
        } finally {
            dbCon.close();
        }
        return anzahlLoeschungen;
    }

    // TODO: Alle loadRundenZiel zusammenführen
    public RundenZiel loadRundenZiel(Cursor c) {
        if (!c.moveToFirst()) {
            Log.e(TAG, "loadRundenziel-2(): Fehler im Cursor!! " + c.getCount());
            return null;
        }
        final RundenZiel rundenziel = new RundenZiel();
        rundenziel.id                 = c.getLong(c.getColumnIndex(RundenZielTbl.ID));
        rundenziel.gid                = c.getString(c.getColumnIndex(RundenZielTbl.GID));
        rundenziel.rundengid          = c.getString(c.getColumnIndex(RundenZielTbl.RUNDENGID));
        rundenziel.zielgid            = c.getString(c.getColumnIndex(RundenZielTbl.ZIELGID));
        rundenziel.rundenschuetzengid = c.getString(c.getColumnIndex(RundenZielTbl.RUNDENSCHUETZENGID));
        rundenziel.nummer             = c.getInt(c.getColumnIndex(RundenZielTbl.NUMMER));
        rundenziel.eins               = (c.getInt(c.getColumnIndex(RundenZielTbl.EINS)) == 1 );
        rundenziel.zwei               = (c.getInt(c.getColumnIndex(RundenZielTbl.ZWEI)) == 1 );
        rundenziel.drei               = (c.getInt(c.getColumnIndex(RundenZielTbl.DREI)) == 1 );
        rundenziel.kill               = (c.getInt(c.getColumnIndex(RundenZielTbl.KILL)) == 1 );
        rundenziel.killkill           = (c.getInt(c.getColumnIndex(RundenZielTbl.KILLKILL)) == 1 );
        rundenziel.punkte             = c.getInt(c.getColumnIndex(RundenZielTbl.PUNKTE));
        rundenziel.anmerkung          = c.getString(c.getColumnIndex(RundenZielTbl.ANMERKUNG));
        rundenziel.dateiname          = c.getString(c.getColumnIndex(RundenZielTbl.DATEINAME));
        rundenziel.zeitstempel        = c.getLong(c.getColumnIndex(RundenZielTbl.ZEITSTEMPEL));
        return rundenziel;
    }

    public RundenZiel loadRundenZiel(String rundenGID, String rundenschuetzenGID, int nummer) {
        Log.d(TAG, "loadRundenZiel-0(): Start");
        Log.d(TAG, "loadRundenZiel-0(): rundenGID          = " + rundenGID);
        Log.d(TAG, "loadRundenZiel-0(): rundenschuetzenGID = " + rundenschuetzenGID);
        Log.d(TAG, "loadRundenZiel-0(): nummer             = " + nummer);
        final SQLiteQueryBuilder rzSuche = new SQLiteQueryBuilder();
        rzSuche.setTables(RundenZielTbl.TABLE_NAME);
        final Cursor c = rzSuche.query(mDb.getReadableDatabase(),
                RundenZielTbl.ALL_COLUMNS,
                RundenZielTbl.WHERE_RUNDEN_NUMMER_SCHUETZEN_EQUALS,
                new String[] { rundenGID, String.valueOf(nummer), rundenschuetzenGID },
                null,
                null,
                null);
        if (c.getCount()!=1) {
            Log.e(TAG, "loadRundenziel-0(): Fehler im Cursor!! " + c.getCount());
            return null;
        }
        return loadRundenZiel(c);
    }

    public RundenZiel loadRundenZiel(String rundenZielGID) {
        Log.d(TAG, "loadRundenZiel-1(): Start ");
        Log.d(TAG, "loadRundenZiel-1(): rundenZielGID = " + rundenZielGID);
        final SQLiteQueryBuilder rzSuche = new SQLiteQueryBuilder();
        rzSuche.setTables(RundenZielTbl.TABLE_NAME);
        final Cursor c = rzSuche.query(mDb.getReadableDatabase(),
                RundenZielTbl.ALL_COLUMNS,
                RundenZielTbl.WHERE_GID_EQUALS,
                new String[] { rundenZielGID },
                null,
                null,
                null);
        if (c.getCount()!=1) {
            Log.e(TAG, "loadRundenziel-1(): Fehler im Cursor!! " + c.getCount());
            return null;
        }
        return loadRundenZiel(c);
    }

    public Cursor loadRundenZielListe(String[] idFilter) {
        String rundenGID = idFilter[0];
        String schuetzenGID = idFilter[1];
        String sqlStatement =
                "select " +
                        RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.ID + ", " +
                        RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.NUMMER + ", " +
                        ZielTbl.TABLE_NAME + "." + ZielTbl.NAME + ", " +
                        RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.PUNKTE +
                        " from "  + RundenZielTbl.TABLE_NAME  +
                        " join "  + ZielTbl.TABLE_NAME +
                        " on "    + RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.ZIELGID +
                        " = "     + ZielTbl.TABLE_NAME + "." + ZielTbl.GID +
                        " where " + RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.RUNDENGID +
                        "='"      + rundenGID +"'" +
                        " and "   + RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.RUNDENSCHUETZENGID +
                        "='"      + schuetzenGID + "'" +
                        ";";
        Log.d(TAG, "loadRundenZielListe(): " + sqlStatement);
        return mDb.getReadableDatabase().rawQuery(sqlStatement, null);
    }

    public Cursor loadRundenZielListe() {
        final SQLiteQueryBuilder rundenzielSuche =
                new SQLiteQueryBuilder();
        rundenzielSuche.setTables(RundenZielTbl.TABLE_NAME);
        return rundenzielSuche.query(mDb.getReadableDatabase(),
                RundenZielTbl.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * Selektion aller Einträge, die noch transferiert werden müssen.
     *
     * @return Cursor der zu transferierenden Parcoure
     */
    public Cursor transferListe() {
        final SQLiteQueryBuilder parcourSuche = new SQLiteQueryBuilder();
        parcourSuche.setTables(RundenZielTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {RundenZielTbl.GID},                // tableColumns
                RundenZielTbl.TRANSFERED + "=0",                // whereClause
                null,                                        // whereArgs
                null,                                        // groupBy
                null,                                        // having
                null);                                       // orderBy
    }

    /**
     * Nach erfolgreichem übertragen der Daten, Datensatz als "übertragen (transfered=1)" markieren
     *
     * @param gid
     *      Datensatz ID, welche aktualisiert werden soll.
     * @return
     *      Anzahl der Datensätze, welche aktualisiert wurden. Sollte nur ein Datensatz sein.
     */
    public long transferUpdate(String gid){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenZielTbl.TRANSFERED, 1);
        dataToInsert.put(RundenZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenZielTbl.TABLE_NAME,
                    dataToInsert,
                    RundenZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenZielTbl.TRANSFERED, 0);
        dataToInsert.put(RundenZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                RundenZielTbl.TABLE_NAME,
                dataToInsert,
                null,
                null);
        } catch ( SQLException sqlEx) {
            Log.e(TAG, "transferReset(): Error-Message = " + sqlEx.getMessage());
            Log.e(TAG, "transferReset(): Error-Message = " + sqlEx.toString());
        } finally {
            dbCon.close();
        }
    }

    /**
     * Schliesst die zugrundeliegende Datenbank.
     * Vor dem naechsten Zugriff muss oeffnen() aufgerufen
     * werden.
     */
    public void schliessen() {
        mDb.close();
    }

    /**
     * Oeffnet die Datenbank, falls sie vorher mit
     * schliessen() geschlossen wurde.
     * <br>
     * Bei Bedarf wird das Schema angelegt bzw. aktualisiert.
     */
    public void oeffnen() {
        mDb.getReadableDatabase();
    }

    /**
     * Gibt die Anzahl der SMSs in der Datenbank
     * zurueck.
     * <br>Performanter als Cursor::getCount.
     *
     * @return Anzahl der Kontakte.
     */
    public int anzahlRundenziel() {
        // TODO: eine Seite aufsetzen mit Informationen zur Datenbank
        // TODO: Anzahl der Einträge pro Tabelle
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + RundenZielTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlRundenziel(): Kein Rundenziel gespeichert");
            return 0;
        }
        return c.getInt(0);
    }

    public int summeRundenPunkte(String mRundenGID, String rundenschuetzenGID) {
        final SQLiteQueryBuilder rzSuche = new SQLiteQueryBuilder();
        rzSuche.setTables(RundenZielTbl.TABLE_NAME);
        final Cursor c = rzSuche.query(
                mDb.getReadableDatabase(),
                new String[]{("SUM(" + RundenZielTbl.PUNKTE + ")")},
                RundenZielTbl.WHERE_SUMME_EINE_RUNDE_EIN_SCHUETZE,
                new String[]{ mRundenGID, rundenschuetzenGID },
                null,
                null,
                null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "summeRundenPunkte(): Fehler im Cursor!! " + c.getCount());
        }
        return c.getInt(0);
    }

    /**
     * Gibt die Punkte pro Ziel in einer Runde für einen Schützen in der Datenbank
     * zurueck.
     * @param rundenGID GID der Runde
     * @param schuetzenGID GID des Schuetzen aus der Tabelle Schuetze
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public Cursor getZielPunkte(String rundenGID, String schuetzenGID) {
        String sqlStatement =
                "select " +
                        RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.NUMMER + ", " +
                        RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.PUNKTE +
                " from " + RundenZielTbl.TABLE_NAME  +
                " where " + RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.RUNDENGID +
                        "='" + rundenGID +"'" +
                " and "   + RundenZielTbl.TABLE_NAME + "." + RundenZielTbl.RUNDENSCHUETZENGID +
                        "='" + schuetzenGID + "'" +
                ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getZielPunkte(): Keine Ziele gespeichert");
            c.close();
            return null;
        }
        return c;
    }

    public int getRundenZielPunkte(String rundenGID, String schuetzenGID, int aktuellesZiel){
        String sqlStatement =
                "select " +
                        RundenZielTbl.PUNKTE +
                        " from " +
                        RundenZielTbl.TABLE_NAME  +
                        " where " + RundenZielTbl.RUNDENGID +          "='" + rundenGID + "'" +
                        " and "   + RundenZielTbl.RUNDENSCHUETZENGID + "='" + schuetzenGID + "'" +
                        " and "   + RundenZielTbl.NUMMER +             "="  + String.valueOf(aktuellesZiel) +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst() && c.getCount()!=1) {
            Log.d(TAG, "getRundenZielPunkte(): Keine Ziele gespeichert");
            c.close();
            return -1;
        }
        int getRundenZielPunkte = c.getInt(0);
        c.close();
        return getRundenZielPunkte;
    }

    /**
     *
     * @param rundenGID
     * @param rundenSchuetzenGID
     * @return
     */
    public int getRundenZielPunkte(String rundenGID, String rundenSchuetzenGID){
        String sqlStatement =
                "select sum(" +
                        RundenZielTbl.PUNKTE + "), count(" +
                        ") from " +
                        RundenZielTbl.TABLE_NAME  +
                        " where " + RundenZielTbl.RUNDENGID +          "='" + rundenGID + "'" +
                        " and "   + RundenZielTbl.RUNDENSCHUETZENGID + "='" + rundenSchuetzenGID + "'" +
                        ";";
         final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst() && c.getCount()!=1) {
            Log.d(TAG, "getRundenZielPunkte(): Keine Ziele gespeichert");
            c.close();
            return -1;
        }
        // Log.d(TAG,"Summe: " + rundenId + " - " + rundenSchuetzenId + " - " +  c.getInt(0) + " - " + c.getInt(1));
        int getRundenZielPunkte = c.getInt(0);
        c.close();
        return getRundenZielPunkte;
    }

    public String getDateiname(String rundenGID, String rundenschuetzenGID, int nummer) {
        // TODO: WHERE Klausel später anpassen, keine rawQuery
        String sqlStatement =
                "select " +
                        RundenZielTbl.DATEINAME + ", " +
                        RundenZielTbl.NUMMER +
                        " from " +
                        RundenZielTbl.TABLE_NAME  +
                        " where " + RundenZielTbl.NUMMER +             "=" + String.valueOf(nummer) +
                        " and   " + RundenZielTbl.RUNDENGID +          "='" + rundenGID + "'" +
                        " and   " + RundenZielTbl.RUNDENSCHUETZENGID + "='" + rundenschuetzenGID + "'" +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getDateiname(): Keine Ziele gespeichert");
            return null;
        }
        return c.getString(0);
    }

    public String getDateiname(String rundenGID, int nummer) {
        String sqlStatement =
                "select " +
                        RundenZielTbl.DATEINAME + ", " +
                        RundenZielTbl.NUMMER +
                        " from " +
                        RundenZielTbl.TABLE_NAME  +
                        " where " + RundenZielTbl.NUMMER +            "=" + String.valueOf(nummer) +
                        " and   " + RundenZielTbl.RUNDENGID +         "='" + rundenGID + "'" +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getDateiname(): Keine Ziele gespeichert");
            return null;
        }
        String getDateiname = c.getString(0);
        c.close();
        return getDateiname;
    }

    /**
     * Gibt die Anzahl der SMSs in der Datenbank
     * zurueck.
     * <br>Performanter als Cursor::getCount.
     *
     * @return Anzahl der Kontakte.
     */
    public String getGID(long id) {
        Log.d(TAG, "getGID(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenZielTbl.GID +
                        " from "  + RundenZielTbl.TABLE_NAME +
                        " where " + RundenZielTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Kein Parcour gespeichert");
            return null;
        }
        Log.d(TAG, "getGID(): Anzahl gespeicherten Parcours - " + c.getCount());
        String getGID = c.getString(0);
        c.close();
        return getGID;
    }

    public long updatePunkteRundenziel(
            String rundengid,
            String rundenschuetzengid,
            int nummer,
            boolean eins,
            boolean zwei,
            boolean drei,
            boolean kill,
            boolean killkill,
            int punkte,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        daten.put(RundenZielTbl.EINS, eins ? 1 : 0);
        daten.put(RundenZielTbl.ZWEI, zwei ? 1 : 0);
        daten.put(RundenZielTbl.DREI, drei ? 1 : 0);
        daten.put(RundenZielTbl.KILL, kill ? 1 : 0);
        daten.put(RundenZielTbl.KILLKILL, killkill ? 1 : 0);
        daten.put(RundenZielTbl.PUNKTE, punkte);
        daten.put(RundenZielTbl.TRANSFERED, 0);
        daten.put(RundenZielTbl.ZEITSTEMPEL, zeitstempel);

        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenZielTbl.TABLE_NAME, daten,
                    RundenZielTbl.WHERE_RUNDEN_NUMMER_SCHUETZEN_EQUALS,
                    new String[]{
                            rundengid,
                            String.valueOf(nummer),
                            rundenschuetzengid });
        } finally {
            dbCon.close();
        }
    }

    public long updatePunkteRundenziel(RundenZiel rundenziel) {
        return updatePunkteRundenziel(
                rundenziel.rundengid,
                rundenziel.rundenschuetzengid,
                rundenziel.nummer,
                rundenziel.eins,
                rundenziel.zwei,
                rundenziel.drei,
                rundenziel.kill,
                rundenziel.killkill,
                rundenziel.punkte,
                rundenziel.zeitstempel);
    }

    public long updateDateiname(
            String rundenGID,
            int nummer,
            String dateiname,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        daten.put(RundenZielTbl.DATEINAME, dateiname);
        daten.put(RundenZielTbl.TRANSFERED, 0);
        daten.put(RundenZielTbl.ZEITSTEMPEL, zeitstempel);

        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenZielTbl.TABLE_NAME, daten,
                    RundenZielTbl.WHERE_RUNDENGID_NUMMER_EQUALS,
                    new String[]{
                            rundenGID,
                            String.valueOf(nummer)});
        } finally {
            dbCon.close();
        }
    }
}
