package com.example.rene.myarrow.Database.Ziel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.rene.myarrow.Database.MyArrowDB;

import java.util.Date;

/**
 * Created by nily on 15.12.15.
 */
public class ZielSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "ZielSpeicher";

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
    public ZielSpeicher(Context context) {
        mDb = MyArrowDB.getInstance(context);
        oeffnen();
        mContext = context;
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private ZielSpeicher() {
        Log.d(TAG, "ZielSpeicher unused.");
    }

    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param nummer
     *          Rufnummer des Kontakts.
     * @param name
     *      Text der SMS.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public long insertZiel(
            String parcourgid,
            int nummer,
            String name,
            String gps_lat_koordinaten,
            String gps_lon_koordinaten,
            String dateiname) {
        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /*
              Daten einfügen
             */
            daten.put(ZielTbl.PARCOURGID, parcourgid);
            daten.put(ZielTbl.NUMMER, nummer);
            daten.put(ZielTbl.NAME, name);
            daten.put(ZielTbl.GPS_LAT_KOORDINATEN, gps_lat_koordinaten);
            daten.put(ZielTbl.GPS_LON_KOORDINATEN, gps_lon_koordinaten);
            daten.put(ZielTbl.DATEINAME, dateiname);
            daten.put(ZielTbl.TRANSFERED, 0);
            daten.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
            final long id = dbCon.insertOrThrow(ZielTbl.TABLE_NAME, null,
                    daten);

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
            daten.put(ZielTbl.GID, deviceid + "_" + String.valueOf(id));
            dbCon.update(
                    ZielTbl.TABLE_NAME,                   // Tabelle
                    daten,                                // Daten
                    ZielTbl.WHERE_ID_EQUALS,              // Where
                    new String[]{String.valueOf(id)});    // Parameter für WHERE

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
     * @param ziel
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public long insertZiel(Ziel ziel) {
        return insertZiel(
                ziel.parcourgid,
                ziel.nummer,
                ziel.name,
                ziel.gps_lat_koordinaten,
                ziel.gps_lon_koordinaten,
                ziel.dateiname);
    }

    /**
     *
     */
    public boolean storeForgeinZiel(Ziel ziel) {
        Log.d(TAG, "storeForgeinZiel(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(ZielTbl.GID, ziel.gid);
        daten.put(ZielTbl.PARCOURGID, ziel.parcourgid);
        daten.put(ZielTbl.NUMMER, ziel.nummer);
        daten.put(ZielTbl.NAME, ziel.name);
        daten.put(ZielTbl.GPS_LAT_KOORDINATEN, ziel.gps_lat_koordinaten);
        daten.put(ZielTbl.GPS_LON_KOORDINATEN, ziel.gps_lon_koordinaten);
        daten.put(ZielTbl.DATEINAME, ziel.dateiname);
        daten.put(ZielTbl.TRANSFERED, 1);
        daten.put(ZielTbl.ZEITSTEMPEL, ziel.zeitstempel);
        Log.d(TAG, "storeForgeinZiel(): Ziel " + ziel.toString() + " erzeugt.");
        Log.d(TAG, "storeForgeinZiel(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, ZielTbl.TABLE_NAME);
    }

    public long updateDateiname(String gid, String dateiname) {
        final ContentValues daten = new ContentValues();
        daten.put(ZielTbl.DATEINAME, dateiname);
        daten.put(ZielTbl.TRANSFERED, 0);
        daten.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());

        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(ZielTbl.TABLE_NAME, daten, ZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public long updateZiel(String gid, String name, String gps_lat, String gps_lon, String dateiname) {
        final ContentValues daten = new ContentValues();
        daten.put(ZielTbl.NAME, name);
        daten.put(ZielTbl.GPS_LAT_KOORDINATEN, gps_lat);
        daten.put(ZielTbl.GPS_LON_KOORDINATEN, gps_lon);
        daten.put(ZielTbl.DATEINAME, dateiname);
        daten.put(ZielTbl.TRANSFERED, 0);
        daten.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(ZielTbl.TABLE_NAME, daten, ZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public long updateZiel(String parcourgid, int nummer_old, int nummer_new) {
        Log.d(TAG, "updateZiel-Nummer(): Start - " + parcourgid + " " + nummer_old + " " + nummer_new);
        final ContentValues daten = new ContentValues();
        daten.put(ZielTbl.NUMMER, nummer_new);
        daten.put(ZielTbl.TRANSFERED, 0);
        daten.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(ZielTbl.TABLE_NAME, daten, ZielTbl.WHERE_PARCOURGID_NUMMER_EQUALS,
                    new String[]{ parcourgid, String.valueOf(nummer_old) });
        } finally {
            dbCon.close();
            Log.d(TAG, "updateZiel-Nummer(): End");
        }
    }

    public long updateZiel(Ziel mZiel) {
        return updateZiel(
                mZiel.gid,
                mZiel.name,
                mZiel.gps_lat_koordinaten,
                mZiel.gps_lon_koordinaten,
                mZiel.dateiname);
    }

    public boolean deleteDateiname(String gid) {
        final ContentValues daten = new ContentValues();
        daten.put(ZielTbl.DATEINAME, "");
        daten.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            final long anzahl = dbCon.update(ZielTbl.TABLE_NAME, daten, ZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
            return (anzahl==1);
        } finally {
            dbCon.close();
        }
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
    public Ziel loadZiel(Cursor c) {
        final Ziel ziel = new Ziel();
        ziel.id = c.getInt(c.getColumnIndex(ZielTbl.ID));
        ziel.gid = c.getString(c.getColumnIndex(ZielTbl.GID));
        ziel.parcourgid = c.getString(c.getColumnIndex(ZielTbl.PARCOURGID));
        ziel.nummer = c.getInt(c.getColumnIndex(ZielTbl.NUMMER));
        ziel.name = c.getString(c.getColumnIndex(ZielTbl.NAME));
        ziel.gps_lat_koordinaten = c.getString(c.getColumnIndex(ZielTbl.GPS_LAT_KOORDINATEN));
        ziel.gps_lon_koordinaten = c.getString(c.getColumnIndex(ZielTbl.GPS_LON_KOORDINATEN));
        ziel.dateiname = c.getString(c.getColumnIndex(ZielTbl.DATEINAME));
        ziel.zeitstempel = c.getLong(c.getColumnIndex(ZielTbl.ZEITSTEMPEL));
        return ziel;
    }

    /**
     * Lädt die SMSs aus dem SMSTbl-Datensatz,
     * auf dem der Cursor gerade steht.
     * <p>
     * Der Cursor wird anschließend deaktiviert, da er im
     * SMSSpeicher nur intern als "letzter Aufruf"
     * aufgerufen wird.
     *
     * @param parcourgid
     * @param nummer
     *
     * @return Exemplar von Ziel.
     */
    public Ziel loadZiel(String parcourgid, int nummer) {
        final Cursor clZ2 = mDb.getReadableDatabase().query(
                ZielTbl.TABLE_NAME,
                ZielTbl.ALL_COLUMNS,
                ZielTbl.WHERE_PARCOURGID_NUMMER_EQUALS,
                new String[] {parcourgid, String.valueOf(nummer)},
                null,
                null,
                null);

        if (clZ2.getCount() != 1) {
            Log.e(TAG, "loadZiel-2(): Anzahl gefundener Ziele" + clZ2.getCount());
        } else if (clZ2.getCount() == -1) {
            Log.e(TAG, "loadZiel-2(): Fehler bei der Query-Ausführung!");
            Log.e(TAG, "loadZiel-2(): Anzahl gefundener Ziele" + clZ2.getCount());
        }
        clZ2.moveToFirst();
        return loadZiel(clZ2);

    }

    /**
     * Lädt die SMSs aus dem SMSTbl-Datensatz,
     * auf dem der Cursor gerade steht.
     * <p>
     * Der Cursor wird anschließend deaktiviert, da er im
     * SMSSpeicher nur intern als "letzter Aufruf"
     * aufgerufen wird.
     *
     * @param zielgid
     *
     * @return Exemplar von Ziel.
     */
    public Ziel loadZiel(String zielgid) {
        final Cursor clZ2 = mDb.getReadableDatabase().query(
                ZielTbl.TABLE_NAME,
                ZielTbl.ALL_COLUMNS,
                ZielTbl.WHERE_GID_EQUALS,
                new String[] {zielgid},
                null,
                null,
                null);

        if (clZ2.getCount() != 1) {
            Log.e(TAG, "loadZiel-3(): Anzahl gefundener Ziele" + clZ2.getCount());
        } else if (clZ2.getCount() == -1) {
            Log.e(TAG, "loadZiel-3(): Fehler bei der Query-Ausführung!");
            Log.e(TAG, "loadZiel-3(): Anzahl gefundener Ziele" + clZ2.getCount());
        }
        clZ2.moveToFirst();
        return loadZiel(clZ2);
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
    public Cursor loadZielListe() {
        final SQLiteQueryBuilder zielSuche =
                new SQLiteQueryBuilder();
        zielSuche.setTables(ZielTbl.TABLE_NAME);
        return zielSuche.query(mDb.getReadableDatabase(),
                ZielTbl.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor loadZielListe(String parcourGID) {
        final SQLiteQueryBuilder zielSuche = new SQLiteQueryBuilder();
        zielSuche.setTables(ZielTbl.TABLE_NAME);
        String[] whereAttribs = null;
        if (parcourGID != null && parcourGID.length() > 0) {
            zielSuche.appendWhere(ZielTbl.WHERE_PARCOURGID_EQUALS);
            whereAttribs = new String[]{parcourGID};
        }
        return zielSuche.query(mDb.getReadableDatabase(),
                ZielTbl.ALL_COLUMNS,
                null,
                whereAttribs,
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
        parcourSuche.setTables(ZielTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {ZielTbl.GID},                // tableColumns
                ZielTbl.TRANSFERED + "=0",                // whereClause
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
        dataToInsert.put(ZielTbl.TRANSFERED, 1);
        dataToInsert.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    ZielTbl.TABLE_NAME,
                    dataToInsert,
                    ZielTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(ZielTbl.TRANSFERED, 0);
        dataToInsert.put(ZielTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                ZielTbl.TABLE_NAME,
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
    public int anzahlZiele() {
        int nReturn = 0;
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + ZielTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlZiele(): Kein Ziel gespeichert");
            nReturn = 0;
        } else {
            nReturn = c.getInt(0);
        }
        c.close();
        return nReturn;
    }

    public String getZielGID(String parcourGID, int nummer) {
        final SQLiteQueryBuilder zielSuche = new SQLiteQueryBuilder();
        zielSuche.setTables(ZielTbl.TABLE_NAME);
        Cursor cZiel = zielSuche.query(mDb.getReadableDatabase(),
                new String[] { ZielTbl.GID },
                ZielTbl.WHERE_PARCOURGID_NUMMER_EQUALS,
                new String[] { parcourGID, String.valueOf(nummer) },
                null,
                null,
                null);
        if (cZiel.getCount()!=1 || !cZiel.moveToFirst()) {
            Log.e(TAG, "getZielID(): Hier ist ein Fehler aufgetreten - " + cZiel.getCount());
        }
        String getZielGID = cZiel.getString(0);
        cZiel.close();
        return getZielGID;
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
                "select " + ZielTbl.GID +
                        " from "  + ZielTbl.TABLE_NAME +
                        " where " + ZielTbl.ID + "=" + String.valueOf(id) +
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

}
