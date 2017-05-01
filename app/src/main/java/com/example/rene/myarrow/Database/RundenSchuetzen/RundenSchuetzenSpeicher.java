package com.example.rene.myarrow.Database.RundenSchuetzen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Parcour.ParcourTbl;
import com.example.rene.myarrow.Database.Runden.RundenTbl;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenTbl;
import com.example.rene.myarrow.misc.BerechneErgebnis;

import java.util.Date;

/**
 * Created by nily on 15.12.15.
 */
public class RundenSchuetzenSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "RundenSchuetzenSpeicher";

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
    public RundenSchuetzenSpeicher(Context context) {
        mDb = MyArrowDB.getInstance(context);
        mContext = context;
        oeffnen();
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private RundenSchuetzenSpeicher() {
        Log.d(TAG, "RundenSchuetzenSpeicher unused.");
    }

    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param zeitstempel
     *      Zeitpunkt des Kontakts.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public long insertRundenSchuetzen(
            String schuetzengid,
            String rundengid,
            int gesamtergebnis,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /*
              Daten einfügen
             */
            daten.put(RundenSchuetzenTbl.SCHUETZENGID, schuetzengid);
            daten.put(RundenSchuetzenTbl.RUNDENGID, rundengid);
            daten.put(RundenSchuetzenTbl.GESAMTERGEBNIS, gesamtergebnis);
            daten.put(RundenSchuetzenTbl.TRANSFERED, 0);
            daten.put(RundenSchuetzenTbl.ZEITSTEMPEL, zeitstempel);
            final long id = dbCon.insertOrThrow(RundenSchuetzenTbl.TABLE_NAME, null, daten);

            /*
              zunächst Device-Id (z.B. IMEI) auslesen
             */
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceid = tm.getDeviceId();
            if(deviceid==null){
                Log.d(TAG, "insertRundenziel(): Keine DeviceID gefunden, auf 000000000000000 gesetzt!!");
                deviceid="000000000000000";
            }

            /*
              Globale ID aktualisieren
             */
            daten.clear();
            daten.put(RundenSchuetzenTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(RundenSchuetzenTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    RundenSchuetzenTbl.TABLE_NAME,          // Tabelle
                    daten,                                  // Daten
                    RundenSchuetzenTbl.WHERE_ID_EQUALS,     // Where
                    new String[]{String.valueOf(id)});      // Parameter für WHERE

            return id;
        } finally {
            dbCon.close();
        }
    }

    /**
     * Speichert eine Mobilfunknummer. Ist dieser bereits in der
     * Datenbank bekannt, wird der vorhandene Datensatz
     * geändert.<br>
     * Ansonsten wird ein neuer Datensatz erzeugt.
     *
     * @param rundenSchuetzen
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertRundenSchuetzen(RundenSchuetzen rundenSchuetzen) {
        return insertRundenSchuetzen(
                rundenSchuetzen.schuetzengid,
                rundenSchuetzen.rundengid,
                rundenSchuetzen.gesamtergebnis,
                rundenSchuetzen.zeitstempel);
    }

    /**
     *
     */
    public boolean storeForgeinRundenSchuetzen(RundenSchuetzen rundenschuetzen) {
        Log.i(TAG, "storeForgeinRundenSchuetzen(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(RundenSchuetzenTbl.GID, rundenschuetzen.gid);
        daten.put(RundenSchuetzenTbl.SCHUETZENGID, rundenschuetzen.schuetzengid);
        daten.put(RundenSchuetzenTbl.RUNDENGID, rundenschuetzen.rundengid);
        daten.put(RundenSchuetzenTbl.GESAMTERGEBNIS, rundenschuetzen.gesamtergebnis);
        daten.put(RundenSchuetzenTbl.TRANSFERED, 1);
        daten.put(RundenSchuetzenTbl.ZEITSTEMPEL, rundenschuetzen.zeitstempel);
        Log.i(TAG, "storeForgeinRundenSchuetzen(): RundenSchuetzen " + rundenschuetzen.toString() + " erzeugt.");
        Log.i(TAG, "storeForgeinRundenSchuetzen(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, RundenSchuetzenTbl.TABLE_NAME);
    }

    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param zeitstempel
     *      Zeitpunkt des Kontakts.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public long updateRundenSchuetzen(
            String schuetzenGID,
            String rundenGID,
            int gesamtergebnis,
            long zeitstempel) {

        final ContentValues daten = new ContentValues();
        daten.put(RundenSchuetzenTbl.GESAMTERGEBNIS, gesamtergebnis);
        daten.put(RundenSchuetzenTbl.TRANSFERED, 0);
        daten.put(RundenSchuetzenTbl.ZEITSTEMPEL, zeitstempel);

        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(RundenSchuetzenTbl.TABLE_NAME, daten, RundenSchuetzenTbl.WHERE_RUNDEN_SCHUETZEN_EQUALS,
                    new String[]{ rundenGID, schuetzenGID });
        } finally {
            dbCon.close();
        }
    }

    public long updateRundenSchuetzen(RundenSchuetzen rundenSchuetzen) {
        return updateRundenSchuetzen(
                rundenSchuetzen.schuetzengid,
                rundenSchuetzen.rundengid,
                rundenSchuetzen.gesamtergebnis,
                rundenSchuetzen.zeitstempel);
    }

    public long updateGesamtergebnis(String gid, String schuetzenGID, int punkte){
        int mPunktestand = new RundenSchuetzenSpeicher(mContext).getPunktestand(gid, schuetzenGID);
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenSchuetzenTbl.GESAMTERGEBNIS, mPunktestand + punkte);
        dataToInsert.put(RundenSchuetzenTbl.TRANSFERED, 0);
        dataToInsert.put(RundenSchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final long nRows = dbCon.update(
                RundenSchuetzenTbl.TABLE_NAME, dataToInsert,
                RundenSchuetzenTbl.WHERE_RUNDEN_SCHUETZEN_EQUALS,
                new String[]{ gid, schuetzenGID });
        dbCon.close();
        return nRows;
    }

    public long updateGesamtergebnis(String gid, int punkte){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenSchuetzenTbl.GESAMTERGEBNIS, punkte);
        dataToInsert.put(RundenSchuetzenTbl.TRANSFERED, 0);
        dataToInsert.put(RundenSchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final long nRows = dbCon.update(
                RundenSchuetzenTbl.TABLE_NAME,
                dataToInsert,
                RundenSchuetzenTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        dbCon.close();
        return nRows;
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @param id
     *          Schluessel der gesuchten Mobilfunknummer
     * @return true, wenn Datensatz geloescht wurde.
     */
    public boolean deleteRundenSchuetzen(long id) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        try {
            anzahlLoeschungen = dbCon.delete(
                    RundenSchuetzenTbl.TABLE_NAME,
                    RundenSchuetzenTbl.WHERE_ID_EQUALS,
                    new String[]{String.valueOf(id)});
        } finally {
            dbCon.close();
        }
        return anzahlLoeschungen == 1;
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @return true, wenn Datensatz geloescht wurde.
     */
    public int deleteRundenSchuetzenWithRundenGID(String rundenGID) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        try {
            anzahlLoeschungen = dbCon.delete(
                    RundenSchuetzenTbl.TABLE_NAME,
                    RundenSchuetzenTbl.WHERE_RUNDENGID_EQUALS,
                    new String[]{ rundenGID });
        } finally {
            dbCon.close();
        }
        return anzahlLoeschungen;
    }

    public RundenSchuetzen loadRundenSchuetzen(Cursor c) {
        if (!c.moveToFirst()) {
            Log.e(TAG, "loadRundenSchuetzen-1(): Fehler im Cursor!! " + c.getCount());
        }

        final RundenSchuetzen rundenSchuetzen = new RundenSchuetzen();
        rundenSchuetzen.id             = c.getLong(c.getColumnIndex(RundenSchuetzenTbl.ID));
        rundenSchuetzen.gid            = c.getString(c.getColumnIndex(RundenSchuetzenTbl.GID));
        rundenSchuetzen.schuetzengid   = c.getString(c.getColumnIndex(RundenSchuetzenTbl.SCHUETZENGID));
        rundenSchuetzen.rundengid      = c.getString(c.getColumnIndex(RundenSchuetzenTbl.RUNDENGID));
        rundenSchuetzen.gesamtergebnis = c.getInt(c.getColumnIndex(RundenSchuetzenTbl.GESAMTERGEBNIS));
        rundenSchuetzen.zeitstempel    = c.getLong(c.getColumnIndex(RundenSchuetzenTbl.ZEITSTEMPEL));
        return rundenSchuetzen;
    }

    public RundenSchuetzen loadRundenSchuetzenSchuetzenGID(String rundenSchuetzenGID) {
        final SQLiteQueryBuilder rzSuche = new SQLiteQueryBuilder();
        rzSuche.setTables(RundenSchuetzenTbl.TABLE_NAME);
        final Cursor c = rzSuche.query(mDb.getReadableDatabase(),
                RundenSchuetzenTbl.ALL_COLUMNS,
                RundenSchuetzenTbl.WHERE_SCHUETZENGID_EQUALS,
                new String[] { rundenSchuetzenGID },
                null,
                null,
                null);
        return loadRundenSchuetzen(c);
    }

    public RundenSchuetzen loadRundenSchuetzenGID(String gid) {
        final SQLiteQueryBuilder rzSuche = new SQLiteQueryBuilder();
        rzSuche.setTables(RundenSchuetzenTbl.TABLE_NAME);
        final Cursor c = rzSuche.query(mDb.getReadableDatabase(),
                RundenSchuetzenTbl.ALL_COLUMNS,
                RundenSchuetzenTbl.WHERE_GID_EQUALS,
                new String[] { gid },
                null,
                null,
                null);
        return loadRundenSchuetzen(c);
    }

    /**
     *
     * @param rundenGID
     * @return
     */
    public String[][] loadRundenSchuetzenListe(String rundenGID) {
        final SQLiteQueryBuilder rundenSchuetzenSuche =
                new SQLiteQueryBuilder();
        rundenSchuetzenSuche.setTables(RundenSchuetzenTbl.TABLE_NAME);
        Cursor rsResult = rundenSchuetzenSuche.query(mDb.getReadableDatabase(),
                RundenSchuetzenTbl.ALL_COLUMNS,
                RundenSchuetzenTbl.WHERE_RUNDENGID_EQUALS,
                new String[] { rundenGID },
                null,
                null,
                null);
        String[][] resultArray = new String[rsResult.getCount()][4];
        SchuetzenSpeicher mSchuetze = new SchuetzenSpeicher(mContext);
        int n=0;
        while (rsResult.moveToNext()){
            resultArray[n][0] = rsResult.getString(rsResult.
                    getColumnIndex(RundenSchuetzenTbl.GID));                                         // ID
            resultArray[n][1] = rsResult.getString(rsResult.
                    getColumnIndex(RundenSchuetzenTbl.SCHUETZENGID));                                // SCHUETZENGID
            resultArray[n][2] = mSchuetze.getSchuetzenNamen(rsResult.getString(rsResult.
                    getColumnIndex(RundenSchuetzenTbl.SCHUETZENGID)));                               // Schuetzennamen
            resultArray[n][3] = rsResult.getString(rsResult.
                    getColumnIndex(RundenSchuetzenTbl.GESAMTERGEBNIS));                             // Gesamtergebnis
            n++;
        }
        rsResult.close();
        return resultArray;
    }

    public Cursor loadRundenSchuetzenNamen(String rundenGID) {
        //
        // Namensliste der Runden-Schützen
        //
        String sqlStatement =
                "select " +
                        SchuetzenTbl.TABLE_NAME + "." + SchuetzenTbl.ID + ", " +   // hier wird wirklich die ID benötigt
                        SchuetzenTbl.TABLE_NAME + "." + SchuetzenTbl.NAME +
                        " from " +
                        SchuetzenTbl.TABLE_NAME +
                        " join " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID + "=" +
                        SchuetzenTbl.TABLE_NAME + "." + SchuetzenTbl.GID +
                        " where " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID +"='" + rundenGID +
                        "';";
        return mDb.getReadableDatabase().rawQuery(sqlStatement, null);
    }

    public Cursor loadRundenSchuetzenListe(){
        String sqlStatement =
                "select " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GID + ", " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + ", " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID +

                        " from " + RundenSchuetzenTbl.TABLE_NAME +
                        ";";
        return mDb.getReadableDatabase().rawQuery(sqlStatement, null);
    }

    /**
     * Selektion aller Einträge, die noch transferiert werden müssen.
     *
     * @return Cursor der zu transferierenden Parcoure
     */
    public Cursor transferListe() {
        final SQLiteQueryBuilder parcourSuche = new SQLiteQueryBuilder();
        parcourSuche.setTables(RundenSchuetzenTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {RundenSchuetzenTbl.GID},       // tableColumns
                RundenSchuetzenTbl.TRANSFERED + "=0",        // whereClause
                null,                                        // whereArgs
                null,                                        // groupBy
                null,                                        // having
                null);                                       // orderBy
    }

    /**
     * Nach erfolgreichem übertragen der Daten, Datensatz als "übertragen (transfered=1)" markieren
     *
     * @return
     *      Anzahl der Datensätze, welche aktualisiert wurden. Sollte nur ein Datensatz sein.
     */
    public long transferUpdate(String gid){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenSchuetzenTbl.TRANSFERED, 1);
        dataToInsert.put(RundenSchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenSchuetzenTbl.TABLE_NAME,
                    dataToInsert,
                    RundenSchuetzenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        final ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenSchuetzenTbl.TRANSFERED, 0);
        dataToInsert.put(RundenSchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                RundenSchuetzenTbl.TABLE_NAME,
                dataToInsert,
                null,
                null);
        }  catch ( SQLException sqlEx) {
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

    public int anzahlRundenSchuetzen() {
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + RundenSchuetzenTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.i(TAG, "anzahlRundenSchuetzen(): Kein RundenSchuetzen gespeichert");
            return 0;
        }
        int nReturn = c.getInt(0);
        c.close();
        return nReturn;
    }

    public boolean getNochSchuetze(long id){
        boolean bReturn = false;

        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenSchuetzenTbl.ID + " from " + RundenSchuetzenTbl.TABLE_NAME +
                        " WHERE " + RundenSchuetzenTbl.SCHUETZENGID + "=" + String.valueOf(id) + ";",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getNochSchuetze(): Kein Eintrag mit der Schuetze-Id " + id + " gefunden");
            bReturn = true;
        }

        c.close();

        return bReturn;
    }

    public int getPunktestand(String rundenGID, String schuetzenGID){
        String sqlStatement =
                "select "+ RundenSchuetzenTbl.GESAMTERGEBNIS + " from " + RundenSchuetzenTbl.TABLE_NAME +
                " WHERE " + RundenSchuetzenTbl.RUNDENGID + "='" + rundenGID +    "'" +
                " AND "+ RundenSchuetzenTbl.SCHUETZENGID + "='" + schuetzenGID + "'" +
                " ;";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getPunktestand(): Kein Eintrag mit der RUNDENGID " + rundenGID + " gefunden");
            return 0;
        }
        int getPunktestand = c.getInt(0);
        c.close();
        return getPunktestand;
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public String[][] getSchuetzenAvg() {
        BerechneErgebnis be =new BerechneErgebnis(mContext);
        String sqlStatement =
                "select " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID +
                        ", sum(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        "), sum(" +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.ANZAHL_ZIELE +
                        ") from " +
                        RundenTbl.TABLE_NAME +
                        " join " +
                        ParcourTbl.TABLE_NAME  +
                            " on " +
                            ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID + "=" +
                            RundenTbl.TABLE_NAME  + "." + RundenTbl.PARCOURGID +
                        " join " +
                        RundenSchuetzenTbl.TABLE_NAME +
                            " on " +
                            RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                            RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " group by " + RundenSchuetzenTbl.SCHUETZENGID +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "getSchuetzenAvg(): Keine Runde gespeichert");
            return null;
        }
        String[][] returnCursor = new String[c.getCount()][3];
        SchuetzenSpeicher schuetzenName = new SchuetzenSpeicher(mContext);
        int n=0;
        do {
            returnCursor[n][0] = c.getString(0);
            returnCursor[n][1] = schuetzenName.getSchuetzenNamen(c.getString(0));
            returnCursor[n][2] = String.valueOf((float)Math.round(c.getInt(1)*100/c.getInt(2)/be.getErgebnis(1,2)));
            n++;
        } while (c.moveToNext());
        c.close();
        return returnCursor;
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public String[][] getRundenSchuetzenMax(String rundenGID) {
        // SchuetzenID
        // max. Summe der erreichten Punkte
        String sqlStatement =
                "select " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID +
                        ", max(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        ") from " +
                        RundenTbl.TABLE_NAME +
                        " join " +
                        RundenSchuetzenTbl.TABLE_NAME +
                            " on " +
                            RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                            RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " where " +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +"='" + rundenGID + "'" +
                        " group by " + RundenSchuetzenTbl.SCHUETZENGID +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "getRundenSchuetzenMax(): Nichts gefunden");
            c.close();
            return null;
        }
        String[][] returnCursor = new String[c.getCount()][3];
        SchuetzenSpeicher schuetzenName = new SchuetzenSpeicher(mContext);
        int n=0;
        do {
            returnCursor[n][0] = c.getString(0);
            returnCursor[n][1] = schuetzenName.getSchuetzenNamen(c.getString(0));
            returnCursor[n][2] = String.valueOf(c.getInt(1));
            n++;
        } while (c.moveToNext());
        c.close();
        return returnCursor;
    }

    public int getParcourMax(String parcourGId){
        int nReturn =0;

        // max. Summe der erreichten Punkte
        String sqlStatement =
                "select max(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        ") from " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " join " +
                        RundenTbl.TABLE_NAME +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " where " +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURGID +"='" + parcourGId + "'" +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst() && c.getCount()!=1) {
            Log.d(TAG, "getRundenSchuetzenMax(): Nichts gefunden");
            nReturn = 0;
        } else {
            nReturn = 1;
        }
        c.close();
        return nReturn;
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public String[][] getParcourAvg() {
        BerechneErgebnis be =new BerechneErgebnis(mContext);
        String sqlStatement =
                "select " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID +
                        ", " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.NAME +
                        ", sum(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        "), sum(" +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.ANZAHL_ZIELE +
                        ") from " +
                        RundenTbl.TABLE_NAME +
                        " join " +
                        ParcourTbl.TABLE_NAME  +
                        " on " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURGID +
                        " join " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " group by " + ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getParcourAvg(): Keine Runde gespeichert");
            return null;
        }
        String[][] returnCursor = new String[c.getCount()][3];
        // SchuetzenSpeicher schuetzenName = new SchuetzenSpeicher(mContext);
        int n=0;
        do {
            returnCursor[n][0] = c.getString(0);
            returnCursor[n][1] = c.getString(1);
            returnCursor[n][2] = String.valueOf((float)Math.round(c.getInt(2)*100/c.getInt(3)/be.getErgebnis(1,2)));
            n++;
        } while (c.moveToNext());
        c.close();
        return returnCursor;
    }

    public String[][] getParcourSchuetzenMax(String parcourGId){
        // max. Summe der erreichten Punkte
        String sqlStatement =
                "select " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID +
                        ", max(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        ") from " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " join " +
                        RundenTbl.TABLE_NAME +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " where " +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURGID +"='" + parcourGId + "'" +
                        " group by " + RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "getParcourSchuetzenMax(): Nichts gefunden");
            return null;
        }
        String[][] returnCursor = new String[c.getCount()][3];
        SchuetzenSpeicher schuetzenName = new SchuetzenSpeicher(mContext);
        int n=0;
        do {
            returnCursor[n][0] = c.getString(0);
            returnCursor[n][1] = schuetzenName.getSchuetzenNamen(c.getString(0));
            returnCursor[n][2] = c.getString(1);
            n++;
        } while (c.moveToNext());
        c.close();
        return returnCursor;
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public Cursor getRundenPunkte(String parcourGID, String schuetzenGID) {
        String sqlStatement =
                "select " +
                        RundenTbl.STARTZEIT + ", " + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        " from " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " join " +
                        RundenTbl.TABLE_NAME  +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " where " + RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURGID
                        + "='" + parcourGID + "'" +
                        " and " + RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID
                        + "='" + schuetzenGID + "'" +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getRundenPunkte(): Keine Parcour/Schuetze gespeichert");
            return null;
        }
        return c;
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public String[][] getParcourAvg(String schuetzenGID) {
        BerechneErgebnis be =new BerechneErgebnis(mContext);
        // SchuetzenID
        // Summe der erreichten Punkte
        // Summe der Ziele
        String sqlStatement =
                "select " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID +
                        ", " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.NAME +
                        ", sum(" +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.GESAMTERGEBNIS +
                        "), sum(" +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.ANZAHL_ZIELE +
                        ") from " +
                        RundenTbl.TABLE_NAME +
                        " join " +
                        ParcourTbl.TABLE_NAME  +
                        " on " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURGID +
                        " join " +
                        RundenSchuetzenTbl.TABLE_NAME +
                        " on " +
                        RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.RUNDENGID + "=" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.GID +
                        " where " + RundenSchuetzenTbl.TABLE_NAME + "." + RundenSchuetzenTbl.SCHUETZENGID + "=" +
                        "'" + schuetzenGID +"'" +
                        " group by " + ParcourTbl.TABLE_NAME + "." + ParcourTbl.GID +
                        ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(sqlStatement, null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getParcourAvg(): Keine Runde gespeichert");
            return null;
        }
        String[][] returnCursor = new String[c.getCount()][3];
        // SchuetzenSpeicher schuetzenName = new SchuetzenSpeicher(mContext);
        int n=0;
        do {
            returnCursor[n][0] = c.getString(0);                            // ParcourGID
            returnCursor[n][1] = c.getString(1);                            // Parcour Name
            returnCursor[n][2] = String.valueOf((float)Math                 // Durchschnittliche Punkte
                    .round(c.getInt(2)*100/c.getInt(3)/be.getErgebnis(1,2)));
            n++;
        } while (c.moveToNext());
        c.close();
        return returnCursor;
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
                "select " + RundenSchuetzenTbl.GID +
                        " from "  + RundenSchuetzenTbl.TABLE_NAME +
                        " where " + RundenSchuetzenTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Kein RundenSchuetzen gespeichert");
            return null;
        }
        String getGID = c.getString(0);
        Log.d(TAG, "getGID(): Anzahl gespeicherten RundenSchuetzen = " + id + "/" + getGID + " = " + c.getCount());
        c.close();
        return getGID;
    }

}
