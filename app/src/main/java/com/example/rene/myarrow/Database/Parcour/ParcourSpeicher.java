package com.example.rene.myarrow.Database.Parcour;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.telephony.TelephonyManager;

import android.util.Log;
import java.util.Date;

import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;

/**
 * Created by nily on 15.12.15.
 */
public class ParcourSpeicher {

    /** Markierung für Logging. */
    private static final String TAG = "ParcourSpeicher";

    /** Verweis auf die Mobilfunknummern-Datenbank. */
    private MyArrowDB mDb;

    /** Context für alle bereitstellen */
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
    public ParcourSpeicher(Context context) {
        Log.d(TAG, "ParcourSpeicher(): Start");
        mDb = MyArrowDB.getInstance(context);
        mContext = context;
        oeffnen();
        Log.d(TAG, "ParcourSpeicher(): End - ParcourSpeicher angelegt.");
    }

    /**
     * Erzeugung ohne Context nicht moeglich.
     */
    @SuppressWarnings("unused")
    private ParcourSpeicher() {
        Log.d(TAG, "ParcourSpeicher unused.");
    }


    /**
     * Legt eine neue Mobilfunknummer in der Datenbank an.
     *
     * @param anzahl_ziele
     *          Rufnummer des Kontakts.
     * @param zeitstempel
     *      Zeitpunkt des Kontakts.
     * @param name
     *      Text der SMS.
     * @return Datenbank-Id des neuen Kontakts
     * @throws SQLException
     *           falls Speichern nicht möglich.
     */
    public String insertParcour(
            String name,
            int anzahl_ziele,
            String strasse,
            String plz,
            String ort,
            String gps_lat_koordination,
            String gps_lon_koordination,
            String anmerkung,
            boolean standard,
            long zeitstempel) {

        Log.d(TAG, "insertSMS()-1: Start");

        final ContentValues daten = new ContentValues();
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            /*
              Daten einfügen
             */
            daten.put(ParcourTbl.NAME, name);
            daten.put(ParcourTbl.ANZAHL_ZIELE, anzahl_ziele);
            daten.put(ParcourTbl.STRASSE, strasse);
            daten.put(ParcourTbl.PLZ, plz);
            daten.put(ParcourTbl.ORT, ort);
            daten.put(ParcourTbl.GPS_LAT_KOORDINATEN, gps_lat_koordination);
            daten.put(ParcourTbl.GPS_LON_KOORDINATEN, gps_lon_koordination);
            daten.put(ParcourTbl.ANMERKUNG, anmerkung);
            daten.put(ParcourTbl.STANDARD, standard);
            daten.put(ParcourTbl.TRANSFERED, 0);
            daten.put(ParcourTbl.ZEITSTEMPEL, zeitstempel);
            final long id = dbCon.insertOrThrow(ParcourTbl.TABLE_NAME, null,
                    daten);
            Log.i(TAG, "insertSMS()-1: SMS mit id=" + id + " erzeugt.");

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
            daten.put(ParcourTbl.GID, deviceid + "_" + String.valueOf(id));
            daten.put(ParcourTbl.ZEITSTEMPEL, zeitstempel);
            dbCon.update(
                    ParcourTbl.TABLE_NAME,                  // Tabelle
                    daten,                                  // Daten
                    ParcourTbl.WHERE_ID_EQUALS,             // Where
                    new String[]{String.valueOf(id)});      // Parameter für WHERE

            return deviceid + "_" + String.valueOf(id);
        } finally {
            Log.d(TAG, "insertSMS()-1: End and close DB");
            dbCon.close();
        }
    }


    /**
     * Speichert eine Mobilfunknummer. Ist dieser bereits in der
     * Datenbank bekannt, wird der vorhandene Datensatz
     * geändert.<br>
     * Ansonsten wird ein neuer Datensatz erzeugt.
     *
     * @param parcour
     *          Zu speichernde Mobilfunknummer.
     * @return id der persistenten Mobilfunknummer.
     * @throws SQLException
     *           falls Neuanlegen gefordert aber nicht
     *           möglich.
     */
    public String insertParcour(Parcour parcour) {
        Log.d(TAG, "insertSMS()-2:");
        return insertParcour(
                parcour.name,
                parcour.anzahl_ziele,
                parcour.strasse,
                parcour.plz,
                parcour.ort,
                parcour.gps_lat_koordinaten,
                parcour.gps_lon_koordinaten,
                parcour.anmerkung,
                parcour.standard,
                parcour.zeitstempel);
    }


    /**
     * Entfernt einPacour und die entsprechenden Runden aus der Datenbank.
     *
     * @param gid
     * *          Globaler Schluessel den gesuchten Parcour
     * @return
     *            true, wenn Datensatz geloescht wurde.
     */
    public void deleteParcour(String gid) {
        Log.d(TAG, "deleteParcour(): Begin");
        RundenSpeicher rundenSpeicher = new RundenSpeicher(mContext);
        RundenSchuetzenSpeicher rundenSchuetzenSpeicher = new RundenSchuetzenSpeicher(mContext);
        RundenZielSpeicher rundenZielSpeicher = new RundenZielSpeicher(mContext);
        /* Runden-Id auslösen */
        String mrundenGID = rundenSpeicher.getRundenIdWithParcourId(gid);
        /* RundenSchuetzen löschen */
        Log.i(TAG, "deleteParcour(): RundenSchuetzen löschen");
        rundenSchuetzenSpeicher.deleteRundenSchuetzenWithRundenGID(mrundenGID);
        rundenSchuetzenSpeicher.schliessen();
        /* RundenZiel löschen */
        Log.i(TAG, "deleteParcour(): RundenZiel löschen");
        rundenZielSpeicher.deleteRundenzielWithRundenID(mrundenGID);
        rundenZielSpeicher.schliessen();
        /* Runden löschen */
        Log.i(TAG, "deleteParcour(): Runden löschen");
        rundenSpeicher.deleteRundenWithParcourID(gid);
        rundenSpeicher.schliessen();
        Log.i(TAG, "deleteParcour(): Parcour löschen");
        int anzahlLoeschungen = 0;
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            anzahlLoeschungen = dbCon.delete(
                    ParcourTbl.TABLE_NAME,
                    ParcourTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
            Log.i(TAG, "deleteParcour(): Parcour id=" + gid + " gelöscht.");
        } finally {
            dbCon.close();
        }
        Log.d(TAG, "deleteParcour(): Anzahl - " + anzahlLoeschungen);
        Log.d(TAG, "deleteParcour(): End");
    }

    /**
     * Aktualisieren eines Parcours mit der ID id
     * @param gid
     * @param name
     * @param strasse
     * @param plz
     * @param ort
     * @param gps_lat_koordination
     * @param gps_lat_koordination
     * @param anmerkung
     * @param standard
     * @return
     */
    public int updateParcour(String gid,
                             String name,
                             String strasse,
                             String plz,
                             String ort,
                             String gps_lat_koordination,
                             String gps_lon_koordination,
                             String anmerkung,
                             boolean standard) {
        Log.d(TAG, "updateParcour(): Start");
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(ParcourTbl.NAME, name);
        dataToInsert.put(ParcourTbl.STRASSE, strasse);
        dataToInsert.put(ParcourTbl.PLZ, plz);
        dataToInsert.put(ParcourTbl.ORT, ort);
        dataToInsert.put(ParcourTbl.GPS_LAT_KOORDINATEN, gps_lat_koordination);
        dataToInsert.put(ParcourTbl.GPS_LON_KOORDINATEN, gps_lon_koordination);
        dataToInsert.put(ParcourTbl.ANMERKUNG, anmerkung);
        dataToInsert.put(ParcourTbl.STANDARD, standard);
        dataToInsert.put(ParcourTbl.TRANSFERED, 0);
        dataToInsert.put(ParcourTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final int mid = dbCon.update(ParcourTbl.TABLE_NAME, dataToInsert, ParcourTbl.WHERE_GID_EQUALS,
                new String[]{ gid });
        Log.i(TAG, "updateParcour(): Parcour mit id=" + gid + "=" + mid + " aktualisiert.");
        dbCon.close();
        return mid;
    }

    /**
     * Aktualisieren der Anzahl der Ziele für einen Parcours mit der ID gid
     * @param gid
     * @param mAnzahlZieleNew
     * @return
     */
    public int updateAnzahlZiele(String gid, int mAnzahlZieleNew) {
        Log.d(TAG, "updateAnzahlZiele(): Start");
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(ParcourTbl.ANZAHL_ZIELE, mAnzahlZieleNew);
        dataToInsert.put(ParcourTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        final int mid = dbCon.update(ParcourTbl.TABLE_NAME, dataToInsert, ParcourTbl.WHERE_GID_EQUALS,
                new String[]{gid});
        Log.i(TAG, "updateAnzahlZiele(): Parcour mit id=" + gid + "=" + mid + " aktualisiert.");
        dbCon.close();
        return mid;
    }

    /**
     *
     */
    public boolean storeForgeinParcour(Parcour parcour) {
        Log.i(TAG, "storeForgeinParcour(): Start");
        final ContentValues daten = new ContentValues();
        daten.put(ParcourTbl.GID, parcour.gid);
        daten.put(ParcourTbl.NAME, parcour.name);
        daten.put(ParcourTbl.STRASSE, parcour.strasse);
        daten.put(ParcourTbl.PLZ, parcour.plz);
        daten.put(ParcourTbl.ORT, parcour.ort);
        daten.put(ParcourTbl.GPS_LAT_KOORDINATEN, parcour.gps_lat_koordinaten);
        daten.put(ParcourTbl.GPS_LON_KOORDINATEN, parcour.gps_lon_koordinaten);
        daten.put(ParcourTbl.ANMERKUNG, parcour.anmerkung);
        daten.put(ParcourTbl.STANDARD, parcour.standard);
        daten.put(ParcourTbl.TRANSFERED, 1);
        daten.put(ParcourTbl.ZEITSTEMPEL, new Date().getTime());
        Log.i(TAG, "storeForgeinParcour(): Parcour " + parcour.toString() + " übergeben.");
        Log.i(TAG, "storeForgeinParcour(): End");
        return MyArrowDB.getInstance(mContext).storeForgeinDataset(daten, ParcourTbl.TABLE_NAME);

    }

    /**
     * Liefert einen Cursor auf alle Felder der Mobilfunknummern-
     * Tabelle zurück. <br>
     * Wenn eine komplette <code>Mobilfunknummer</code> genutzt
     * werden soll, ist die <code>loadMobilfunknummer</code>
     * -Methode vorzuziehen.
     *
     * @param gid Globaler Schluessel des gesuchten Kontakts
     * @return Cursor, oder null
     */
    public Parcour loadParcourDetails(String gid) {
        Log.d(TAG, "loadParcourDetails-2(): gestartet.....");
        Log.d(TAG, "loadParcourDetails-2(): Parcour-Id - " + gid);
        return loadParcourDetails(mDb.getReadableDatabase().query(
                ParcourTbl.TABLE_NAME, ParcourTbl.ALL_COLUMNS,
                ParcourTbl.WHERE_GID_EQUALS, new String[] { gid }, null, null, null));
    }

    /**
     * Liefert einen Cursor auf alle Felder der Mobilfunknummern-
     * Tabelle zurück. <br>
     * Wenn eine komplette <code>Mobilfunknummer</code> genutzt
     * werden soll, ist die <code>loadMobilfunknummer</code>
     * -Methode vorzuziehen.
     *
     * @param c Schluessel des gesuchten Kontakts
     * @return Cursor, oder null
     */
    public Parcour loadParcourDetails(Cursor c) {

        Log.d(TAG, "loadParcourDetails-1(): Start");

        Parcour mparcour = new Parcour();
        if (!c.moveToFirst()) {
            Log.e(TAG, "loadParcourDetails-1(): Fehler im Cursor!! " + c.getCount());
            return null;
        }

        mparcour.id                  = c.getInt(c.getColumnIndex(ParcourTbl.ID));
        mparcour.gid                  = c.getString(c.getColumnIndex(ParcourTbl.GID));
        mparcour.name                = c.getString(c.getColumnIndex(ParcourTbl.NAME));
        mparcour.anzahl_ziele        = c.getInt(c.getColumnIndex(ParcourTbl.ANZAHL_ZIELE));
        mparcour.strasse             = c.getString(c.getColumnIndex(ParcourTbl.STRASSE));
        mparcour.plz                 = c.getString(c.getColumnIndex(ParcourTbl.PLZ));
        mparcour.ort                 = c.getString(c.getColumnIndex(ParcourTbl.ORT));
        mparcour.gps_lat_koordinaten = c.getString(c.getColumnIndex(ParcourTbl.GPS_LAT_KOORDINATEN));
        mparcour.gps_lon_koordinaten = c.getString(c.getColumnIndex(ParcourTbl.GPS_LON_KOORDINATEN));
        mparcour.anmerkung           = c.getString(c.getColumnIndex(ParcourTbl.ANMERKUNG));
        mparcour.standard            = (c.getInt(c.getColumnIndex(ParcourTbl.STANDARD))==1);
        mparcour.transfered          = c.getInt(c.getColumnIndex(ParcourTbl.TRANSFERED));
        mparcour.zeitstempel         = c.getLong(c.getColumnIndex(ParcourTbl.ZEITSTEMPEL));

        Log.d(TAG, "loadParcourDetails-1(): End");

        return mparcour;
    }

    public Cursor loadParcourListe() {
        Log.d(TAG, "loadParcourListe-2(): Start");
        final SQLiteQueryBuilder parcourSuche =
                new SQLiteQueryBuilder();
        parcourSuche.setTables(ParcourTbl.TABLE_NAME);
        Log.d(TAG, "loadParcourListe-2(): Starte Query");
        return parcourSuche.query(mDb.getReadableDatabase(),
                ParcourTbl.ALL_COLUMNS,
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
        parcourSuche.setTables(ParcourTbl.TABLE_NAME);
        return parcourSuche.query(mDb.getReadableDatabase(), // tableName
                new String[] {ParcourTbl.GID},               // tableColumns
                ParcourTbl.TRANSFERED + "=0",                // whereClause
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
        dataToInsert.put(ParcourTbl.TRANSFERED, 1);
        dataToInsert.put(ParcourTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            return dbCon.update(
                    ParcourTbl.TABLE_NAME,
                    dataToInsert,
                    ParcourTbl.WHERE_GID_EQUALS,
                    new String[]{ gid });
        } finally {
            dbCon.close();
        }
    }

    public void transferReset(){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(ParcourTbl.TRANSFERED, 0);
        dataToInsert.put(ParcourTbl.ZEITSTEMPEL, new Date().getTime());
        final SQLiteDatabase dbCon = mDb.getWritableDatabase();
        try {
            dbCon.update(
                ParcourTbl.TABLE_NAME,
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
    public int anzahlParcours() {
        Log.d(TAG, "anzahlParcours(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select count(*) from " + ParcourTbl.TABLE_NAME,
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "anzahlParcours(): Kein Parcour gespeichert");
            return 0;
        }
        int anzahlParcours =c.getInt(0);
        c.close();
        Log.d(TAG, "anzahlParcours(): Anzahl gespeicherten Parcours - " + anzahlParcours);
        return anzahlParcours;
    }

    /**
     * Gibt die Anzahl der SMSs in der Datenbank
     * zurueck.
     * <br>Performanter als Cursor::getCount.
     *
     * @return Anzahl der Kontakte.
     */
    public String getName(long id) {
        Log.d(TAG, "getName(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + ParcourTbl.NAME +
                " from "  + ParcourTbl.TABLE_NAME +
                " where " + ParcourTbl.ID + "=" + String.valueOf(id) +
                ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getName(): Kein Parcour gespeichert");
            return null;
        }
        Log.d(TAG, "getName(): Anzahl gespeicherten Parcours - " + c.getCount());
        String getName = c.getString(0);
        c.close();
        return getName;
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
                "select " + ParcourTbl.GID +
                        " from "  + ParcourTbl.TABLE_NAME +
                        " where " + ParcourTbl.ID + "=" + String.valueOf(id) +
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

    /**
     * Gibt die Anzahl der SMSs in der Datenbank
     * zurueck.
     * <br>Performanter als Cursor::getCount.
     *
     * @return Anzahl der Kontakte.
     */
    public String getName(String gid) {
        Log.d(TAG, "getName(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + ParcourTbl.NAME +
                " from "  + ParcourTbl.TABLE_NAME +
                " where " + ParcourTbl.GID + "='" + gid + "'" +
                ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getName(): Kein Parcour gespeichert");
            return null;
        }
        Log.d(TAG, "getName(): Anzahl gespeicherten Parcours - " + c.getCount());
        String getName = c.getString(0);
        c.close();
        return getName;
    }

    public int getIDMitNamen(String name) {
        Log.d(TAG, "getIDMitNamen(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + ParcourTbl.ID +
                        " from "  + ParcourTbl.TABLE_NAME +
                        " where " + ParcourTbl.NAME + "='" + name +
                        "';",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getIDMitNamen(): Kein Parcour mit dem Name " + name + " gespeichert");
            return -1;
        }
        Log.d(TAG, "getID()MitNamen: Anzahl gespeicherten Parcours - " + c.getCount());
        int getIDMitNamen = c.getInt(0);
        c.close();
        return getIDMitNamen;
    }

    public int getAnzahlZiele(Long mParcourID) {
        Log.d(TAG, "getIDMitNamen(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + ParcourTbl.ANZAHL_ZIELE +
                        " from "  + ParcourTbl.TABLE_NAME +
                        " where " + ParcourTbl.ID + "=" + String.valueOf(mParcourID) +
                        ";",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getAnzahlZiele(): Kein Parcour mit der ID " + mParcourID + " gespeichert");
            return -1;
        }
        Log.d(TAG, "getID()MitNamen: Anzahl gespeicherten Parcours - " + c.getCount());
        int getAnzahlZiele = c.getInt(0);
        c.close();
        return getAnzahlZiele;
    }

    public int getAnzahlZiele(String mParcourGID) {
        Log.d(TAG, "getIDMitNamen(): Start");
        final Cursor c = mDb.getReadableDatabase().rawQuery(
                "select " + ParcourTbl.ANZAHL_ZIELE +
                        " from "  + ParcourTbl.TABLE_NAME +
                        " where " + ParcourTbl.GID + "='" + String.valueOf(mParcourGID) +
                        "';",
                null);
        if (!c.moveToFirst()) {
            Log.d(TAG, "getAnzahlZiele(): Kein Parcour mit der ID " + mParcourGID + " gespeichert");
            return -1;
        }
        Log.d(TAG, "getID()MitNamen: Anzahl gespeicherten Parcours - " + c.getCount());
        int getAnzahlZiele = c.getInt(0);
        c.close();
        return getAnzahlZiele;
    }
}
