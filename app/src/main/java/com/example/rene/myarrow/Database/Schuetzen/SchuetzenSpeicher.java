package com.example.rene.myarrow.Database.Schuetzen;

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
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;

import java.util.Date;

/**
 * Created by nily on 15.12.15.
 */
public class SchuetzenSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "SchuetzenSpeicher";

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
    public SchuetzenSpeicher(Context context) {
        mDb = MyArrowDB.getInstance(context);
        mContext = context;
        oeffnen();
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private SchuetzenSpeicher() {
        Log.d(TAG, "SchuetzenSpeicher unused.");
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
    public long insertSchuetzen( String name, String dateiname, long zeitstempel) {
        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /**
             * Daten einfügen
             */
            daten.put(SchuetzenTbl.NAME, name);
            daten.put(SchuetzenTbl.DATEINAME, dateiname);
            daten.put(SchuetzenTbl.ZEITSTEMPEL, zeitstempel);
            daten.put(SchuetzenTbl.TRANSFERED, 0);
            final long id = dbCon.insertOrThrow(SchuetzenTbl.TABLE_NAME, null,
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
            daten.put(SchuetzenTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(SchuetzenTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    SchuetzenTbl.TABLE_NAME,                // Tabelle
                    daten,                                  // Daten
                    SchuetzenTbl.WHERE_ID_EQUALS,           // Where
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
     * @param schuetzen
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertSchuetzen(Schuetzen schuetzen) {
        return insertSchuetzen(
                schuetzen.name,
                schuetzen.dateiname,
                schuetzen.zeitstempel);
    }

    /**
     *
     */
    public boolean storeForgeinSchuetzen(Schuetzen schuetzen) {
        Log.d(TAG, "storeForgeinSchuetzen(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(SchuetzenTbl.GID, schuetzen.gid);
        daten.put(SchuetzenTbl.NAME, schuetzen.name);
        daten.put(SchuetzenTbl.DATEINAME, schuetzen.dateiname);
        daten.put(SchuetzenTbl.ZEITSTEMPEL, schuetzen.zeitstempel);
        daten.put(SchuetzenTbl.TRANSFERED, 1);
        Log.d(TAG, "storeForgeinSchuetzen(): Schuetzen " + schuetzen.toString() + " erzeugt.");
        Log.d(TAG, "storeForgeinSchuetzen(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, SchuetzenTbl.TABLE_NAME);
    }

    public boolean deleteSchuetzen(long id) {
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;

        RundenSchuetzenSpeicher mRunden = new RundenSchuetzenSpeicher(mContext);

        if (mRunden.getNochSchuetze(id)) {
            try {
                anzahlLoeschungen = dbCon.delete(
                        SchuetzenTbl.TABLE_NAME,
                        SchuetzenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(id)});
            } finally {
                dbCon.close();
            }
        } else {
            Toast.makeText(mContext, "Schuetzen noch zugeordnet", Toast.LENGTH_SHORT).show();
        }
        return anzahlLoeschungen == 1;
    }

    public boolean deleteDateiname(String gid) {
        final ContentValues daten = new ContentValues();
        daten.put(SchuetzenTbl.DATEINAME, "");
        daten.put(SchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            final long anzahl = dbCon.update(SchuetzenTbl.TABLE_NAME, daten, SchuetzenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
            return (anzahl==1);
        } finally {
            dbCon.close();
        }
    }

    /**
     * Aktualisieren eines Parcours mit der ID id
     * @param gid
     * @param name
     * @return
     */
    public long updateSchuetzen(String gid, String name, String mFile) {
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(SchuetzenTbl.NAME, name);
        dataToInsert.put(SchuetzenTbl.DATEINAME, mFile);
        dataToInsert.put(SchuetzenTbl.TRANSFERED, 0);
        dataToInsert.put(SchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(SchuetzenTbl.TABLE_NAME, dataToInsert, SchuetzenTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    /**
     * Gibt die ID basierend auf einen Namen zurück
     * @param name
     * @return
     */
    public int getIDfromName(String name){
        final SQLiteQueryBuilder schuetzenSuche = new SQLiteQueryBuilder();
        schuetzenSuche.setTables(SchuetzenTbl.TABLE_NAME);
        Cursor cSchuetzen = schuetzenSuche.query(mDb.getReadableDatabase(),
                new String[]{SchuetzenTbl.ID},
                SchuetzenTbl.WHERE_SCHUETZE_EQUALS,
                new String[]{name},
                null,
                null,
                null);
        if (cSchuetzen.getCount() != 1 || !cSchuetzen.moveToFirst()) {
            Log.e(TAG, "getIDfromName(): Hier ist ein Fehler aufgetreten - " + cSchuetzen.getCount());
        }

        return cSchuetzen.getInt(0);

    }

    public String getSchuetzenNamen(String gid){
        final SQLiteQueryBuilder schuetzenSuche = new SQLiteQueryBuilder();
        schuetzenSuche.setTables(SchuetzenTbl.TABLE_NAME);
        Cursor cSchuetzen = schuetzenSuche.query(mDb.getReadableDatabase(),
                new String[] { SchuetzenTbl.NAME },
                SchuetzenTbl.WHERE_GID_EQUALS,
                new String[] { gid },
                null,
                null,
                null);
        if (cSchuetzen.getCount()!=1 || !cSchuetzen.moveToFirst()) {
            Log.e(TAG, "getIDfromName(): Hier ist ein Fehler aufgetreten - " + cSchuetzen.getCount());
        }
        String getSchuetzenNamen = cSchuetzen.getString(0);
        cSchuetzen.close();
        return getSchuetzenNamen;
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
    public Schuetzen loadSchuetzenDetails(Cursor c) {
        if (!c.moveToFirst()) {
            Log.e(TAG, "loadSchuetzenDetails-1(): Fehler im Cursor!! " + c.getCount());
            return null;
        }

        final Schuetzen schuetzen = new Schuetzen();
        schuetzen.id = c.getLong(c.getColumnIndex(SchuetzenTbl.ID));
        schuetzen.gid = c.getString(c.getColumnIndex(SchuetzenTbl.GID));
        schuetzen.name = c.getString(c.getColumnIndex(SchuetzenTbl.NAME));
        schuetzen.dateiname = c.getString(c.getColumnIndex(SchuetzenTbl.DATEINAME));
        schuetzen.zeitstempel = c.getLong(c.getColumnIndex(SchuetzenTbl.ZEITSTEMPEL));
        schuetzen.transfered = c.getInt(c.getColumnIndex(SchuetzenTbl.TRANSFERED));
        return schuetzen;
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
    public Schuetzen loadSchuetzenDetails(String gid) {
        return loadSchuetzenDetails(mDb.getReadableDatabase().query(
                SchuetzenTbl.TABLE_NAME, SchuetzenTbl.ALL_COLUMNS,
                SchuetzenTbl.WHERE_GID_EQUALS, new String[]{ gid }, null, null, null));
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
    public Cursor loadSchuetzenListe() {
        final SQLiteQueryBuilder schuetzenSuche =
                new SQLiteQueryBuilder();
        schuetzenSuche.setTables(SchuetzenTbl.TABLE_NAME);
        return schuetzenSuche.query(mDb.getReadableDatabase(),
                SchuetzenTbl.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * Selektion aller Einträge, die noch transferiert werden müssen.
     *
     * @return Cursor der zu transferierenden Schuetzen
     */
    public Cursor transferListe() {
        final SQLiteQueryBuilder schuetzenSuche = new SQLiteQueryBuilder();
        schuetzenSuche.setTables(SchuetzenTbl.TABLE_NAME);
        return schuetzenSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {SchuetzenTbl.GID},               // tableColumns
                SchuetzenTbl.TRANSFERED + "=0",                // whereClause
                null,                                          // whereArgs
                null,                                          // groupBy
                null,                                          // having
                null);                                         // orderBy
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
        dataToInsert.put(SchuetzenTbl.TRANSFERED, 1);
        dataToInsert.put(SchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    SchuetzenTbl.TABLE_NAME,
                    dataToInsert,
                    SchuetzenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(SchuetzenTbl.TRANSFERED, 0);
        dataToInsert.put(SchuetzenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                SchuetzenTbl.TABLE_NAME,
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
    public int anzahlSchuetzen() {
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + SchuetzenTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlSchuetzen(): Kein Schuetzen gespeichert");
            return 0;
        }
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
                "select " + SchuetzenTbl.GID +
                        " from "  + SchuetzenTbl.TABLE_NAME +
                        " where " + SchuetzenTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Kein Schuetze gespeichert");
            return null;
        }
        Log.d(TAG, "getGID(): Anzahl gespeicherten Schuetzen - " + c.getCount());
        String getGID = c.getString(0);
        c.close();
        return getGID;
    }

}
