package com.example.rene.myarrow.Database.Pfeil;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.telephony.TelephonyManager;

import android.util.Log;

import android.widget.Toast;

import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;

import java.util.Date;

/**
 * Created by nily on 15.12.15.
 */
public class PfeilSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "PfeilSpeicher";

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
    public PfeilSpeicher(Context context) {
        Log.d(TAG, "PfeilSpeicher(): Start");
        mDb = MyArrowDB.getInstance(context);
        mContext = context;
        oeffnen();
        Log.d(TAG, "PfeilSpeicher(): End - PfeilSpeicher angelegt.");
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private PfeilSpeicher() {
        Log.d(TAG, "PfeilSpeicher unused.");
    }

    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param name
     *          Rufnummer des Kontakts.
     * @param zeitstempel
     *      Zeitpunkt des Kontakts.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public long insertPfeil(
            String name,
            boolean standard,
            String dateiname,
            long zeitstempel) {

        Log.d(TAG, "insertPfeil-1(): Start");

        if (standard) {
            Log.d(TAG, "insertPfeil-1(): Standard auf FALSE setzen");
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(PfeilTbl.STANDARD, false);
            dataToInsert.put(PfeilTbl.TRANSFERED, 0);
            dataToInsert.put(PfeilTbl.ZEITSTEMPEL, new Date().getTime());
            final SQLiteDatabase dbCon = mDb.getWritableDatabase();
            dbCon.update(PfeilTbl.TABLE_NAME, dataToInsert, null, null);
            Log.i(TAG, "insertPfeil-1(): Standard überall auf FALSE gesetzt.");
            dbCon.close();
        }

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /**
             * Daten einfügen
             */
            daten.put(PfeilTbl.NAME, name);
            daten.put(PfeilTbl.STANDARD, standard);
            daten.put(PfeilTbl.DATEINAME, dateiname);
            daten.put(PfeilTbl.TRANSFERED, 0);
            daten.put(PfeilTbl.ZEITSTEMPEL, zeitstempel);
            final long id = dbCon.insertOrThrow(PfeilTbl.TABLE_NAME, null,
                    daten);
            Log.i(TAG, "insertPfeil-1(): Pfeil mit id=" + id + " erzeugt.");

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
            daten.put(PfeilTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(PfeilTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    PfeilTbl.TABLE_NAME,                  // Tabelle
                    daten,                                // Daten
                    PfeilTbl.WHERE_ID_EQUALS,             // Where
                    new String[]{String.valueOf(id)});    // Parameter für WHERE

            return id;
        } finally {
            Log.d(TAG, "insertPfeil-1(): End and close DB");
            dbCon.close();
        }
    }

    /**
     * Speichert eine Mobilfunknummer. Ist dieser bereits in der
     * Datenbank bekannt, wird der vorhandene Datensatz
     * geändert.<br>
     * Ansonsten wird ein neuer Datensatz erzeugt.
     *
     * @param pfeil
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertPfeil(Pfeil pfeil) {
        Log.d(TAG, "insertPfeil()-2:");
        return insertPfeil(
                pfeil.name,
                pfeil.standard,
                pfeil.dateiname,
                pfeil.zeitstempel);
    }

    /**
     *
     */
    public boolean storeForgeinPfeil(Pfeil pfeil) {
        Log.i(TAG, "storeForgeinPfeil(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(PfeilTbl.GID, pfeil.gid);
        daten.put(PfeilTbl.NAME, pfeil.name);
        daten.put(PfeilTbl.STANDARD, pfeil.standard);
        daten.put(PfeilTbl.DATEINAME, pfeil.dateiname);
        daten.put(PfeilTbl.TRANSFERED, 1);
        daten.put(PfeilTbl.ZEITSTEMPEL, pfeil.zeitstempel);
        Log.i(TAG, "storeForgeinPfeil(): Bogen mit id=" + pfeil.toString() + " erzeugt.");
        Log.i(TAG, "storeForgeinPfeil(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, PfeilTbl.TABLE_NAME);
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @param gid
     *          Schluessel der gesuchten Mobilfunknummer
     * @return true, wenn Datensatz geloescht wurde.
     */
    public boolean deletePfeil(String gid) {

        Log.d(TAG, "deletePfeil(): Begin");
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;

        RundenSpeicher mRunden = new RundenSpeicher(mContext);

        if (mRunden.getNochPfeile(gid)) {
            try {
                anzahlLoeschungen = dbCon.delete(
                        PfeilTbl.TABLE_NAME,
                        PfeilTbl.WHERE_GID_EQUALS,
                        new String[]{ gid });
                Log.i(TAG, "deletePfeil(): Pfeil id=" + gid + " gelöscht.");
            } finally {
                dbCon.close();
            }
        } else {
            Toast.makeText(mContext, "Pfeil noch zugeordnet", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "deletePfeil(): Anzahl - " + anzahlLoeschungen);
        Log.d(TAG, "deletePfeil(): End");
        return anzahlLoeschungen == 1;
    }

    public boolean deleteDateiname(String gid) {
        Log.d(TAG, "deleteDateiname(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(PfeilTbl.DATEINAME, "");
        daten.put(PfeilTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            final long anzahl = dbCon.update(PfeilTbl.TABLE_NAME, daten, PfeilTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
            Log.i(TAG, "deleteDateiname(): Pfeil mit gid=" + gid + " (Anzahl) aktualisiert.");
            return (anzahl==1);
        } finally {
            Log.d(TAG, "deleteDateinameZiel(): End and close DB");
            dbCon.close();
        }
    }

    /**
     * Aktualisieren eines Parcours mit der ID id
     * @param gid
     * @param name
     * @param standard
     * @return
     */
    public int updatePfeil(String gid,
                           String name,
                           boolean standard,
                           String mFile) {
        Log.d(TAG, "updatePfeil(): Start");
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(PfeilTbl.NAME, name);
        dataToInsert.put(PfeilTbl.STANDARD, standard);
        dataToInsert.put(PfeilTbl.DATEINAME, mFile);
        dataToInsert.put(PfeilTbl.TRANSFERED, 0);
        dataToInsert.put(PfeilTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final int mid = dbCon.update(PfeilTbl.TABLE_NAME, dataToInsert, PfeilTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        Log.i(TAG, "updatePfeil(): Pfeil mit gid=" + gid + " = " + mid + " aktualisiert.");
        dbCon.close();
        return mid;
    }

    /**
     * Gibt die ID basierend auf einen Namen zurück
     * @param name
     * @return
     */
    public int getIDfromName(String name){
        Log.d(TAG, "getIDfromName(): Start");
        final SQLiteQueryBuilder pfeilSuche = new SQLiteQueryBuilder();
        pfeilSuche.setTables(PfeilTbl.TABLE_NAME);
        Log.d(TAG, "getIDfromName(): Starte Query");
        Cursor cPfeil = pfeilSuche.query(mDb.getReadableDatabase(),
                new String[] { PfeilTbl.ID },
                PfeilTbl.WHERE_NAME_EQUALS,
                new String[] { name },
                null,
                null,
                null);
        if (cPfeil.getCount() != 1) {
            Log.e(TAG, "getIDfromName(): Hier ist ein Fehler aufgetreten - " + cPfeil.getCount());
        }

        return cPfeil.getInt(0);

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
    public Pfeil loadPfeilDetails(Cursor c) {
        Log.d(TAG, "loadPfeilDetails-1(): Start");

        if (!c.moveToFirst()) {
            Log.e(TAG, "loadPfeilDetails-1(): Fehler im Cursor!! " + c.getCount());
            return null;
        }

        final Pfeil pfeil = new Pfeil();
        pfeil.id = c.getLong(c.getColumnIndex(PfeilTbl.ID));
        pfeil.gid = c.getString(c.getColumnIndex(PfeilTbl.GID));
        pfeil.name = c.getString(c.getColumnIndex(PfeilTbl.NAME));
        pfeil.standard = (c.getInt(c.getColumnIndex(PfeilTbl.STANDARD)) == 1);
        pfeil.dateiname = c.getString(c.getColumnIndex(PfeilTbl.DATEINAME));
        pfeil.zeitstempel = c.getLong(c.getColumnIndex(PfeilTbl.ZEITSTEMPEL));

        Log.d(TAG, "loadPfeilDetails-1(): End");
        return pfeil;
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
    public Pfeil loadPfeilDetails(String gid) {
        Log.d(TAG, "loadPfeilDetails-2(): gestartet.....");
        Log.d(TAG, "loadPfeilDetails-2(): Pfeil-Id - " + gid);
        return loadPfeilDetails(mDb.getReadableDatabase().query(
                PfeilTbl.TABLE_NAME, PfeilTbl.ALL_COLUMNS,
                PfeilTbl.WHERE_GID_EQUALS, new String[]{ gid }, null, null, null));
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
    public Cursor loadPfeilListe() {
        Log.d(TAG, "loadPfeilListe-2(): Start");
        final SQLiteQueryBuilder pfeilSuche =
                new SQLiteQueryBuilder();
        pfeilSuche.setTables(PfeilTbl.TABLE_NAME);
        Log.d(TAG, "loadPfeilListe-2(): Starte Query");
        return pfeilSuche.query(mDb.getReadableDatabase(),
                PfeilTbl.ALL_COLUMNS,
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
        parcourSuche.setTables(PfeilTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {PfeilTbl.GID},                // tableColumns
                PfeilTbl.TRANSFERED + "=0",                // whereClause
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
        dataToInsert.put(PfeilTbl.TRANSFERED, 1);
        dataToInsert.put(PfeilTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    PfeilTbl.TABLE_NAME,
                    dataToInsert,
                    PfeilTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(PfeilTbl.TRANSFERED, 0);
        dataToInsert.put(PfeilTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                PfeilTbl.TABLE_NAME,
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
        Log.d(TAG, "schliessen(): Start");
        mDb.close();
        Log.d(TAG, "schliessen(): End - Datenbank MyArrow geschlossen.");
    }

    /**
     * Oeffnet die Datenbank, falls sie vorher mit
     * schliessen() geschlossen wurde.
     * <br>
     * Bei Bedarf wird das Schema angelegt bzw. aktualisiert.
     */
    public void oeffnen() {
        Log.d(TAG, "oeffnen(): Start");
        mDb.getReadableDatabase();
        Log.d(TAG, "oeffnen(): End - Datenbank MyArrow geoeffnet.");
    }

    /**
     * Gibt die Anzahl der SMSs in der Datenbank
     * zurueck.
     * <br>Performanter als Cursor::getCount.
     *
     * @return Anzahl der Kontakte.
     */
    public int anzahlPfeil() {
        Log.d(TAG, "anzahlPfeil(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + PfeilTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlPfeil(): Kein Pfeil gespeichert");
            return 0;
        }
        Log.d(TAG, "anzahlPfeil(): Anzahl gespeicherten Pfeile - " + c.getInt(0));
        return c.getInt(0);
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
                "select " + PfeilTbl.GID +
                        " from "  + PfeilTbl.TABLE_NAME +
                        " where " + PfeilTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Kein Pfeil gespeichert");
            return null;
        }
        Log.d(TAG, "getGID(): Anzahl gespeicherten Pfeile - " + c.getCount());
        String getGID = c.getString(0);
        c.close();
        return getGID;
    }

}
