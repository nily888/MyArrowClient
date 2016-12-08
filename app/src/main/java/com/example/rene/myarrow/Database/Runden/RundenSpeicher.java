package com.example.rene.myarrow.Database.Runden;

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
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.misc.BerechneErgebnis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nily on 15.12.15.
 */
public class RundenSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "RundenSpeicher";

    /** Verweis auf die MyArrow-Datenbank. */
    private MyArrowDB mDb;

    /** Context weitergeben */
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
    public RundenSpeicher(Context context) {
        mDb = MyArrowDB.getInstance(context);
        mContext = context;
        oeffnen();
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private RundenSpeicher() {
        Log.d(TAG, "RundenSpeicher unused.");
    }

    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param parcourgid
     *          Rufnummer des Kontakts.
     * @param bogengid
     *      Zeitpunkt des Kontakts.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public long insertRunden(
            String parcourgid,
            String bogengid,
            String pfeilgid,
            long startzeit,
            long endzeit,
            String wetter) {

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /**
             * Daten einfügen
             */
            daten.put(RundenTbl.PARCOURGID, parcourgid);
            daten.put(RundenTbl.BOGENGID, bogengid);
            daten.put(RundenTbl.PFEILGID, pfeilgid);
            daten.put(RundenTbl.STARTZEIT, startzeit);
            daten.put(RundenTbl.S_STARTZEIT, new SimpleDateFormat("dd/MM/yy HH:mm", Locale.GERMAN).format(new Date(startzeit)));
            daten.put(RundenTbl.ENDZEIT, endzeit);
            daten.put(RundenTbl.WETTER, wetter);
            daten.put(RundenTbl.TRANSFERED, 0);
            final long id = dbCon.insertOrThrow(RundenTbl.TABLE_NAME, null, daten);

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
            daten.put(RundenTbl.GID, deviceid + "_" + String.valueOf(id));
            dbCon.update(
                    RundenTbl.TABLE_NAME,                   // Tabelle
                    daten,                                  // Daten
                    RundenTbl.WHERE_ID_EQUALS,              // Where
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
     * @param runden
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertRunden(Runden runden) {
        return insertRunden(
                runden.parcourgid,
                runden.bogengid,
                runden.pfeilgid,
                runden.startzeit,
                runden.endzeit,
                runden.wetter);
    }

    /**
     *
     */
    public boolean storeForgeinRunden(Runden runden) {
        Log.i(TAG, "storeForgeinRunden(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(RundenTbl.GID, runden.gid);
        daten.put(RundenTbl.PARCOURGID, runden.parcourgid);
        daten.put(RundenTbl.BOGENGID, runden.bogengid);
        daten.put(RundenTbl.PFEILGID, runden.pfeilgid);
        daten.put(RundenTbl.STARTZEIT, runden.startzeit);
        daten.put(RundenTbl.S_STARTZEIT, new SimpleDateFormat("dd/MM/yy HH:mm", Locale.GERMAN).format(new Date(runden.startzeit)));
        daten.put(RundenTbl.ENDZEIT, runden.endzeit);
        daten.put(RundenTbl.WETTER, runden.wetter);
        daten.put(RundenTbl.TRANSFERED, 1);
        Log.i(TAG, "storeForgeinRunden(): Runden mit id=" + runden.toString() + " erzeugt.");
        Log.i(TAG, "storeForgeinRunden(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, RundenTbl.TABLE_NAME);
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @param gid
     *          Schluessel der gesuchten Mobilfunknummer
     * @return true, wenn Datensatz geloescht wurde.
     */
    public boolean deleteRunden(String gid) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        try {
            anzahlLoeschungen = dbCon.delete(
                    RundenTbl.TABLE_NAME,
                    RundenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
        return anzahlLoeschungen == 1;
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @param parcourGID
     *          Schluessel der gesuchten Mobilfunknummer
     * @return true, wenn Datensatz geloescht wurde.
     */
    public int deleteRundenWithParcourID(String parcourGID) {
        RundenZielSpeicher rundenzielSpeicher = new RundenZielSpeicher(mContext);

        /** alle Runden zur PacourID selektieren */
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.GID +
                        " from "  + RundenTbl.TABLE_NAME +
                        " where " + ParcourTbl.GID + "='" + parcourGID + "'" +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "deleteRundenWithParcourID(): Keine Runde gespeichert");
            return 0;
        }
        do {
            // alle Rundenziele löschen
            rundenzielSpeicher.deleteRundenzielWithRundenID(c.getString(0));
            // Runde löschen
            deleteRunden(c.getString(0));
        } while (c.moveToNext());
        int deleteRundenWithParcourID = c.getCount();
        c.close();
        return deleteRundenWithParcourID;
    }

    /**
     * Liefert einen Cursor auf alle Felder der Mobilfunknummern-
     * Tabelle zurück. <br>
     * Wenn eine komplette <code>Mobilfunknummer</code> genutzt
     * werden soll, ist die <code>loadMobilfunknummer</code>
     * -Methode vorzuziehen.
     *
     * @param gid Schluessel des gesuchten Kontakts
     * @return Cursor, oder null
     */
    public Runden loadRunden(String gid) {
        return loadRunden(mDb.getReadableDatabase().query(
                RundenTbl.TABLE_NAME, RundenTbl.ALL_COLUMNS,
                RundenTbl.WHERE_GID_EQUALS, new String[]{ gid }, null, null, null));
    }

    /**
     * Lädt die SMSs aus dem SMSTbl-Datensatz,
     * auf dem der Cursor gerade steht.
     * <p>
     * Der Cursor wird anschließend deaktiviert, da er im
     * SMSSpeicher nur intern als "letzter Aufruf"
     * aufgerufen wird.
     *
     * @param c aktuelle Cursorposition != null
     * @return Exemplar von Mobilfunknummer.
     */
    public Runden loadRunden(Cursor c) {
        if (!c.moveToFirst()) {
            Log.e(TAG, "loadRunden-1(): Fehler im Cursor!! " + c.getCount());
        }

        final Runden runden = new Runden();
        runden.id          = c.getInt(c.getColumnIndex(RundenTbl.ID));
        runden.gid         = c.getString(c.getColumnIndex(RundenTbl.GID));
        runden.parcourgid  = c.getString(c.getColumnIndex(RundenTbl.PARCOURGID));
        runden.bogengid    = c.getString(c.getColumnIndex(RundenTbl.BOGENGID));
        runden.pfeilgid    = c.getString(c.getColumnIndex(RundenTbl.PFEILGID));
        runden.startzeit   = c.getLong(c.getColumnIndex(RundenTbl.STARTZEIT));
        runden.s_startzeit = c.getString(c.getColumnIndex(RundenTbl.S_STARTZEIT));
        runden.endzeit     = c.getLong(c.getColumnIndex(RundenTbl.ENDZEIT));
        runden.wetter      = c.getString(c.getColumnIndex(RundenTbl.WETTER));
        return runden;
    }

    /**
     * Liefert alle Mobilfunknummern mit einstellbarer Sortierung
     * zurück. <br>
     * Es kann (optional) ein Filterkriterium angegeben
     * werden. Wenn der <code>namensFilter</code> definiert
     * ist, werden nur Kontakte geliefert, deren NAME mit
     * diesem Buchstaben beginnt.
     *
     * @return Cursor auf die Ergebnisliste.
     */
    public Cursor loadRundenListe() {
        final SQLiteQueryBuilder rundenSuche =
                new SQLiteQueryBuilder();
        rundenSuche.setTables(RundenTbl.TABLE_NAME);
        return rundenSuche.query(mDb.getReadableDatabase(),
                RundenTbl.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor loadRundenListe(String mParcourGID) {
        if (mParcourGID != null) {
            final SQLiteQueryBuilder rundenSuche = new SQLiteQueryBuilder();
            rundenSuche.setTables(RundenTbl.TABLE_NAME);
            Log.d(TAG, "loadRundenListe(): rundenSuche = " + rundenSuche.toString());
            return rundenSuche.query(
                    mDb.getReadableDatabase(),
                    new String[]{RundenTbl.ID, RundenTbl.S_STARTZEIT},
                    RundenTbl.WHERE_PARCOURGID_EQUALS,
                    new String[]{mParcourGID},
                    null,
                    null,
                    RundenTbl.STARTZEIT + " DESC");
        } else {
            return null;
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
    public int anzahlRunden() {
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + RundenTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "anzahlRunden(): Kein Runden gespeichert");
            return 0;
        }
        return c.getInt(0);
    }


    public int getPunktestand(long rundenID){
       final Cursor c = mDb.getReadableDatabase().rawQuery(
         "select "+ RundenTbl.PUNKTESTAND + " from " + RundenTbl.TABLE_NAME + 
        " WHERE " + RundenTbl.ID +"="+String.valueOf(rundenID)+";",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getPunktestand(): Kein Eintrag mit der RUNDENID " + rundenID + " gefunden");
            return 0;
        }
       return c.getInt(0);
    }

    public long updatePunkteStand(long mRundenID, int mGesamt){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenTbl.PUNKTESTAND, getPunktestand(mRundenID) + mGesamt);
        dataToInsert.put(RundenTbl.ENDZEIT, new Date().getTime());
        dataToInsert.put(RundenTbl.TRANSFERED, 0);
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final long id = dbCon.update(RundenTbl.TABLE_NAME, dataToInsert, RundenTbl.WHERE_ID_EQUALS,
                    new String[]{String.valueOf(mRundenID)});
        dbCon.close();
        return id;
    }

    /**
     * Selektion aller Einträge, die noch transferiert werden müssen.
     *
     * @return Cursor der zu transferierenden Parcoure
     */
    public Cursor transferListe() {
        final SQLiteQueryBuilder parcourSuche = new SQLiteQueryBuilder();
        parcourSuche.setTables(RundenTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {RundenTbl.GID},                // tableColumns
                RundenTbl.TRANSFERED + "=0",                 // whereClause
                null,                                        // whereArgs
                null,                                        // groupBy
                null,                                        // having
                null);                                       // orderBy
    }

    /**
     * Nach erfolgreichem übertragen der Daten, Datensatz als "übertragen (transfered=1)" markieren
     *
     * @param id
     *      Datensatz ID, welche aktualisiert werden soll.
     * @return
     *      Anzahl der Datensätze, welche aktualisiert wurden. Sollte nur ein Datensatz sein.
     */
    public long transferUpdate(String gid){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenTbl.TRANSFERED, 1);
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenTbl.TABLE_NAME,
                    dataToInsert,
                    RundenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(RundenTbl.TRANSFERED, 0);
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                RundenTbl.TABLE_NAME,
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

    public int getMaxPunktestand(long parcourID) {
        final Cursor c = mDb.getReadableDatabase().rawQuery(
            "select max("+ RundenTbl.PUNKTESTAND + ") from " + RundenTbl.TABLE_NAME +
            " WHERE " + RundenTbl.PARCOURID + "=" + String.valueOf(parcourID) + ";",
            null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getMaxPunktestand(): Kein Eintrag mit der PARCOURID " + parcourID + " gefunden");
            return 0;
        }
        return c.getInt(0);
    }

    /**
     * Gibt die durchschnittlichen Punkte pro Parcour in der Datenbank
     * zurueck.
     *
     * @return Parcourname, Durchschnittlichen Punkte.
     */
    public Cursor getParcoursAvg() {
        BerechneErgebnis be =new BerechneErgebnis(mContext);
        String sqlStatement =
                "select " +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.NAME + ", sum(" +
                        RundenTbl.TABLE_NAME + "." + RundenTbl.PUNKTESTAND +
                        ")*100/" + be.getErgebnis(1,2) + "/sum(" +
                        ParcourTbl.TABLE_NAME + "." + ParcourTbl.ANZAHL_ZIELE +
                        ") " +
                    "from " +
                    ParcourTbl.TABLE_NAME  +
                " join " +
                        RundenTbl.TABLE_NAME +
                " on " +
                    ParcourTbl.TABLE_NAME + "." + ParcourTbl.ID + "=" +
                    RundenTbl.TABLE_NAME + "." + RundenTbl.PARCOURID +
                " group by " + ParcourTbl.NAME +
                ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getParcoursAvg(): Keine Runde gespeichert");
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
    public Cursor getRundenPunkte(long parcourID) {
        String sqlStatement =
                "select " +
                    RundenTbl.STARTZEIT + ", " + RundenTbl.PUNKTESTAND +
                " from " +
                    RundenTbl.TABLE_NAME  +
                " where " + RundenTbl.PARCOURID + "=" + String.valueOf(parcourID) +
                ";";
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                sqlStatement,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getRundenPunkte(): Keine Runde gespeichert");
            return null;
        }
        return c;
    }

    /**
     *
     * @param startzeit
     * @return
     */
    public String getGIDMitStartzeit(String startzeit){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.GID + " from " + RundenTbl.TABLE_NAME +
                " WHERE " + RundenTbl.STARTZEIT + "=" + startzeit + ";",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getIDMitStartzeit(): Kein Eintrag mit der Startzeit " + startzeit + " gefunden");
            return null;
        }
        String getGIDMitStartzeit = c.getString(0);
        c.close();
        return getGIDMitStartzeit;
    }

    /**
     *
     * @param mRundenGID
     *          RundenGID der zu findenen Runde
     * @return
     *          ParcourID
     */
    public String getParcourGID(String mRundenGID){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.PARCOURGID + " from " + RundenTbl.TABLE_NAME +
                        " WHERE " + RundenTbl.GID + "='" + mRundenGID + "';",
                null);
        if (!c.moveToFirst()) {
            Log.e(TAG, "getParcourGID(): Keinen Eintrag mit der GID " + mRundenGID + " gefunden");
            return null;
        }
        String getParcourGID = c.getString(0);
        c.close();
        return getParcourGID;
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean getNochPfeile(String gid){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.ID + " from " + RundenTbl.TABLE_NAME +
                        " WHERE " + RundenTbl.PFEILGID + "='" + gid + "';",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getNochPfeile(): Kein Eintrag mit der Pfeil-GId " + gid + " gefunden");
            return true;
        }
        return (c.getInt(0)==0);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean getNochBogen(String gid){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.ID + " from " + RundenTbl.TABLE_NAME +
                        " WHERE " + RundenTbl.BOGENGID + "='" + gid + "';",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "getNochBogen(): Kein Eintrag mit der Bogen-GId " + gid + " gefunden");
            return true;
        }
        return (c.getInt(0)==0);
    }

    public long updateEndzeit(String rundenGID) {
        final ContentValues daten = new ContentValues();
        daten.put(RundenTbl.ENDZEIT, new Date().getTime());
        daten.put(RundenTbl.TRANSFERED, 0);
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    RundenTbl.TABLE_NAME, daten,
                    RundenTbl.WHERE_GID_EQUALS,
                    new String[]{ rundenGID });
        } finally {
            dbCon.close();
        }
    }

    public String getGID(long id) {
        Log.d(TAG, "getGID(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.GID +
                        " from "  + RundenTbl.TABLE_NAME +
                        " where " + RundenTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Keine Runde gespeichert");
            c.close();
            return null;
        }
        Log.d(TAG, "getGID(): Anzahl gespeicherten Parcours - " + c.getCount());
        String getGID = c.getString(0);
        c.close();
        return getGID;
    }

    public long getEndzeit(String rundenGID) {
        final SQLiteDatabase dbCon = mDb.getReadableDatabase();
        try {
            final Cursor c = mDb.getReadableDatabase().rawQuery(
                    "select " + RundenTbl.ENDZEIT + " from " + RundenTbl.TABLE_NAME +
                            " WHERE " + RundenTbl.GID + "='" + rundenGID + "'" + ";",
                    null);
            if (!c.moveToFirst()) {
                Log.d(TAG, "getEndzeit(): Kein Eintrag mit der Runden-Id " + rundenGID + " gefunden");
                c.close();
                return 0;
            }
            long getEndzeit = c.getLong(0);
            c.close();
            return getEndzeit;
        } finally {
            dbCon.close();
        }
    }

    public long getRundenIdWithParcourId(long id){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.ID + " from " + RundenTbl.TABLE_NAME +
                        " WHERE " + RundenTbl.PARCOURID + "=" + String.valueOf(id) + ";",
                null);

        if (!c.moveToFirst()) {
            Log.d(TAG, "RundenIdWithParcourId(): Kein Eintrag mit Parcour-Id " + id + " gefunden");
            return 0;
        }

        return Long.valueOf(c.getInt(0));
    }

    public String getRundenIdWithParcourId(String parcourGID){
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + RundenTbl.GID + " from " + RundenTbl.TABLE_NAME +
                        " WHERE " + RundenTbl.PARCOURGID + "='" + parcourGID + "';",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "RundenIdWithParcourId(): Kein Eintrag mit Parcour-Id " + parcourGID + " gefunden");
            c.close();
            return null;
        }
        String getRundenIdWithParcourId = c.getString(0);
        c.close();
        return getRundenIdWithParcourId;
    }

    public void deleteKompletteRunden(String mRundenGID){
        new RundenSpeicher(mContext).deleteRunden(mRundenGID);
        new RundenZielSpeicher(mContext).deleteRundenzielWithRundenID(mRundenGID);
        new RundenSchuetzenSpeicher(mContext).deleteRundenSchuetzenWithRundenGID(mRundenGID);
    }
}