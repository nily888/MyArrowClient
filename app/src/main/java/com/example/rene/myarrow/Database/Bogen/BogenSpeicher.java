package com.example.rene.myarrow.Database.Bogen;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.telephony.TelephonyManager;

import android.util.Log;
import java.util.Date;

import android.widget.Toast;

import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;

/**
 * Created by nily on 15.12.15.
 */
public class BogenSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "BogenSpeicher";

    /**
     * Verweis auf die MyArrow-Datenbank.
     */
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
    public BogenSpeicher(Context context) {
        Log.d(TAG, "BogenSpeicher(): Start");
        mContext = context;
        mDb = MyArrowDB.getInstance(context);
        /*
          Datenbank öffnen
         */
        oeffnen();
        Log.d(TAG, "BogenSpeicher(): End - BogenSpeicher angelegt.");
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private BogenSpeicher() {
        Log.d(TAG, "BogenSpeicher unused.");
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
    public long insertBogen(
            String name,
            boolean standard,
            String dateiname,
            long zeitstempel) {

        Log.d(TAG, "insertBogen-1(): Start");

        /*
          neuer Standard wir gesetzt, daher zunächst alle anderen auf FALSE setzen
         */
        if (standard) {
            Log.d(TAG, "insertBogen-1(): Standard auf FALSE setzen");
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(BogenTbl.STANDARD, false);
            dataToInsert.put(BogenTbl.TRANSFERED, 0);
            dataToInsert.put(BogenTbl.ZEITSTEMPEL, new Date().getTime());
            final SQLiteDatabase dbCon = mDb.getWritableDatabase();
            dbCon.update(BogenTbl.TABLE_NAME, dataToInsert, null, null);
            Log.i(TAG, "insertBogen-1(): Standard überall auf FALSE gesetzt.");
            dbCon.close();
        }

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /*
              Daten einfügen
             */
            daten.put(BogenTbl.NAME, name);
            daten.put(BogenTbl.STANDARD, standard);
            daten.put(BogenTbl.TRANSFERED, 0);
            daten.put(BogenTbl.DATEINAME, dateiname);
            daten.put(BogenTbl.ZEITSTEMPEL, zeitstempel);
            final long id = dbCon.insertOrThrow(BogenTbl.TABLE_NAME, null,
                    daten);
            Log.i(TAG, "insertBogen-1(): Bogen mit id=" + id + " erzeugt.");
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
            daten.put(BogenTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(BogenTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    BogenTbl.TABLE_NAME,                    // Tabelle
                    daten,                                  // Daten
                    BogenTbl.WHERE_ID_EQUALS,               // Where
                    new String[]{String.valueOf(id)});      // Parameter für WHERE

            return id;
        } finally {
            Log.d(TAG, "insertBogen-1(): End and close DB");
            dbCon.close();
        }
    }

    /**
     * Speichert eine Mobilfunknummer. Ist dieser bereits in der
     * Datenbank bekannt, wird der vorhandene Datensatz
     * geändert.<br>
     * Ansonsten wird ein neuer Datensatz erzeugt.
     *
     * @param bogen
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertBogen(Bogen bogen) {
        Log.d(TAG, "insertZiel()-2:");
        return insertBogen(
                bogen.name,
                bogen.standard,
                bogen.dateiname,
                bogen.zeitstempel);
    }

    /**
     *
     */
    public boolean storeForgeinBogen(Bogen bogen) {
        Log.i(TAG, "storeForgeinBogen(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(BogenTbl.GID, bogen.gid);
        daten.put(BogenTbl.NAME, bogen.name);
        daten.put(BogenTbl.STANDARD, bogen.standard);
        daten.put(BogenTbl.DATEINAME, bogen.dateiname);
        daten.put(BogenTbl.ZEITSTEMPEL, bogen.zeitstempel);
        Log.i(TAG, "storeForgeinBogen(): Bogen mit id=" + bogen.toString() + " erzeugt.");
        Log.i(TAG, "storeForgeinBogen(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, BogenTbl.TABLE_NAME);
    }

    /**
     * Entfernt eine Mobilfunknummer aus der Datenbank.
     *
     * @param gid
     *          Schluessel der gesuchten Mobilfunknummer
     * @return true, wenn Datensatz geloescht wurde.
     */
    public boolean deleteBogen(String gid) {

        Log.d(TAG, "deleteBogen(): Begin");
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        int anzahlLoeschungen = 0;
        RundenSpeicher mRunden = new RundenSpeicher(mContext);

        if (mRunden.getNochBogen(gid)) {

            try {
                anzahlLoeschungen = dbCon.delete(
                    BogenTbl.TABLE_NAME,
                    BogenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
                Log.i(TAG, "deleteBogen(): Ziel id=" + gid + " gelöscht.");
            } finally {
                dbCon.close();
            }
        } else {
            Toast.makeText(mContext, "Bogen noch zugeordnet", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "deleteBogen(): Anzahl - " + anzahlLoeschungen);
        Log.d(TAG, "deleteBogen(): End");
        return anzahlLoeschungen == 1;
    }

    public boolean deleteDateiname(String gid) {
        Log.d(TAG, "deleteDateiname(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(BogenTbl.DATEINAME, "");
        daten.put(BogenTbl.TRANSFERED, 0);
        daten.put(BogenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            final long anzahl = dbCon.update(BogenTbl.TABLE_NAME, daten, BogenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
            Log.i(TAG, "deleteDateiname(): Bogen mit id=" + gid + " (Anzahl) aktualisiert.");
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
    public int updateBogen(String gid,
                           String name,
                           boolean standard,
                           String mFile) {
        Log.d(TAG, "updateBogen(): Start");
        Log.d(TAG, "updateBogen(): Name     - " + name);
        Log.d(TAG, "updateBogen(): Standard - " + standard);
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(BogenTbl.NAME, name);
        dataToInsert.put(BogenTbl.STANDARD, standard);
        dataToInsert.put(BogenTbl.DATEINAME, mFile);
        dataToInsert.put(BogenTbl.TRANSFERED, 0);
        dataToInsert.put(BogenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final int mid = dbCon.update(BogenTbl.TABLE_NAME, dataToInsert, BogenTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        Log.i(TAG, "updateBogen(): Bogen mit gid=" + gid + " = " + mid + " aktualisiert.");
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
        final SQLiteQueryBuilder bogenSuche = new SQLiteQueryBuilder();
        bogenSuche.setTables(BogenTbl.TABLE_NAME);
        Log.d(TAG, "getIDfromName(): Starte Query - Name: " + name);
        Cursor cBogen = bogenSuche.query(mDb.getReadableDatabase(),
                            new String[] { BogenTbl.ID },
                            BogenTbl.WHERE_BOGEN_EQUALS,
                            new String[] { name },
                            null,
                            null,
                            null);
        if (cBogen.getCount() != 1) {
            Log.e(TAG, "getIDfromName(): Hier ist ein Fehler aufgetreten - " + cBogen.getCount());
        }

        return cBogen.getInt(0);

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
    public Bogen loadBogenDetails(Cursor c) {

        Log.d(TAG, "loadBogenDetails-1(): Start");

        if (!c.moveToFirst()) {
            Log.e(TAG, "loadBogenDetails-1(): Fehler im Cursor!! " + c.getCount());
            return null;
        }
        final Bogen bogen = new Bogen();
        bogen.id = c.getLong(c.getColumnIndex(BogenTbl.ID));
        bogen.gid = c.getString(c.getColumnIndex(BogenTbl.GID));
        bogen.name = c.getString(c.getColumnIndex(BogenTbl.NAME));
        bogen.standard = (c.getInt(c.getColumnIndex(BogenTbl.STANDARD)) == 1);
        bogen.dateiname = c.getString(c.getColumnIndex(BogenTbl.DATEINAME));
        bogen.transfered = c.getInt(c.getColumnIndex(BogenTbl.TRANSFERED));
        bogen.zeitstempel = c.getLong(c.getColumnIndex(BogenTbl.ZEITSTEMPEL));

        Log.d(TAG, "loadBogenDetails-1(): End");
        return bogen;
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
    public Bogen loadBogenDetails(String gid) {
        Log.d(TAG, "loadBogenDetails-2(): gestartet.....");
        Log.d(TAG, "loadBogenDetails-2(): Bogen-Id - " + gid);
        return loadBogenDetails(mDb.getReadableDatabase().query(
                BogenTbl.TABLE_NAME, BogenTbl.ALL_COLUMNS,
                BogenTbl.WHERE_GID_EQUALS, new String[]{ gid }, null, null, null));
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
    public Cursor loadBogenListe() {
        Log.d(TAG, "loadBogenListe-2(): Start");
        final SQLiteQueryBuilder bogenSuche =
                new SQLiteQueryBuilder();
        bogenSuche.setTables(BogenTbl.TABLE_NAME);
        Log.d(TAG, "loadBogenListe-2(): Starte Query");
        return bogenSuche.query(mDb.getReadableDatabase(),
                BogenTbl.ALL_COLUMNS,
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
        parcourSuche.setTables(BogenTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {BogenTbl.GID},                // tableColumns
                BogenTbl.TRANSFERED + "=0",                // whereClause
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
        dataToInsert.put(BogenTbl.TRANSFERED, 1);
        dataToInsert.put(BogenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    BogenTbl.TABLE_NAME,
                    dataToInsert,
                    BogenTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    /**
     * Alle Daten sollen noch einmal synchonisiert werden
     */
    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(BogenTbl.TRANSFERED, 0);
        dataToInsert.put(BogenTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                BogenTbl.TABLE_NAME,
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
    public int anzahlBogen() {
        Log.d(TAG, "anzahlBogen(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + BogenTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlBogen(): Kein Bogen gespeichert");
            return 0;
        }
        Log.d(TAG, "anzahlBogen(): Anzahl gespeicherten Bögen - " + c.getInt(0));
        c.close();
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
                "select " + BogenTbl.GID +
                        " from "  + BogenTbl.TABLE_NAME +
                        " where " + BogenTbl.ID + "=" + String.valueOf(id) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getGID(): Kein Bogen gespeichert");
            return null;
        }
        Log.d(TAG, "getGID(): Anzahl gespeicherten Bögen - " + c.getCount());
        String getGID = c.getString(0);
        c.close();
        return getGID;
    }

}
