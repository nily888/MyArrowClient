package com.example.rene.myarrow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.rene.myarrow.Database.Bogen.BogenTbl;
import com.example.rene.myarrow.Database.Parcour.ParcourTbl;
import com.example.rene.myarrow.Database.Pfeil.PfeilTbl;
import com.example.rene.myarrow.Database.Runden.RundenTbl;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenTbl;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielTbl;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenTbl;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;
import com.example.rene.myarrow.SendMail.GmailSender;

public class MyArrowDB extends SQLiteOpenHelper {
    // TODO: Indicies erstellen

    /** Markierung für Logging. */
    private static final String TAG = "MyArrowDB";

    /** Name der Datenbankdatei. */
    private static final String DATENBANK_NAME = "myarrow.db";

    private static final int DATENBANK_VERSION = 12;

    private static MyArrowDB sINSTANCE;

    private static final Object sLOCK = "";

    private static Context mContext;


    /**
     * Die Datenbank kann nur nach Kenntnis "ihrer" Anwendung
     * verwaltet werden. Daher muss der Context der Anwendung
     * uebergeben werden.
     *
     * @param context
     *          Context der aufrufenden Anwendung.
     * @return Das <i>eine</i> Exemplar der Amando-Datenbank,
     *    das in der Anwendung verwendet werden darf.
     */
    public static MyArrowDB getInstance(Context context) {
        Log.d(TAG, "getInstance(): entered");
        mContext = context;
        if( sINSTANCE == null ) {
            synchronized(sLOCK) {
                if( sINSTANCE == null ) {
                    sINSTANCE = new MyArrowDB(context.getApplicationContext());
                }
            }
        }
        return sINSTANCE;
    }

    /**
     * Die Datenbank kann nur nach Kenntnis "ihrer" Anwendung
     * verwaltet werden. Daher muss der Context der Anwendung
     * uebergeben werden.<br>
     * Der Constructor darf nur von getInstance aufgerufen werden,
     * um eine Mehrfach-Instanziierung zu verhindern.
     *
     * @param context
     *          Context der aufrufenden Anwendung.
     */
    private MyArrowDB(Context context) {
        super(context, DATENBANK_NAME, null,
                DATENBANK_VERSION);
    }

    /**
     * Wird aufgerufen, wenn das Datenbankschema neu
     * angelegt werden soll.
     * <br>
     * Es wird die Tabelle <code>MobilfunknummernTbl</code>
     * angelegt.
     * <br>
     * Anschliessend wird die Initialbefuellung der Datenbank
     * durchgefuehrt.
     *
     * @param db Aktuelle Datenbank-Verbindung
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        /** Tabellen erstellen */
        db.execSQL(BogenTbl.SQL_CREATE);
        db.execSQL(PfeilTbl.SQL_CREATE);
        db.execSQL(ZielTbl.SQL_CREATE);
        db.execSQL(ParcourTbl.SQL_CREATE);
        db.execSQL(RundenTbl.SQL_CREATE);
        db.execSQL(RundenZielTbl.SQL_CREATE);
        db.execSQL(SchuetzenTbl.SQL_CREATE);
        db.execSQL(RundenSchuetzenTbl.SQL_CREATE);

        /** Testdaten einspielen */
        defaultBefüllung(db);

    }

    /**
     * Wird aufgerufen, wenn sich die Version des Schemas
     * geaendert hat.
     * <br>
     * In diesem Fall wird die Datenbank geloescht und mit
     * neuem Schema wieder aufgebaut.
     *
     *
     * @param db Aktuelle Datenbank-Verbindung
     * @param oldVersion bisherige Schemaversion
     * @param newVersion neue Schemaversion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");

        /**
         * Vorsichtshalber machen wir mal ein Backup....
         */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        exportDB(timeStamp + "_myarrow_backup.db");
        Log.i(TAG, "onUpgrade(): Backup erstellt = " + timeStamp + "_myarrow_backup.db");

        /**
         * jetzt geht's los mt den Upgrades....
         */
        if (newVersion==1) {
            /** Alle tabellen löschen */
            db.execSQL(BogenTbl.SQL_DROP);
            db.execSQL(PfeilTbl.SQL_DROP);
            db.execSQL(ZielTbl.SQL_DROP);
            db.execSQL(ParcourTbl.SQL_DROP);
            db.execSQL(RundenTbl.SQL_DROP);
            db.execSQL(RundenZielTbl.SQL_DROP);
            db.execSQL(SchuetzenTbl.SQL_DROP);
            db.execSQL(RundenSchuetzenTbl.SQL_DROP);
            /** Tabellen wieder anlegen und Defaultbefüllung ausführen */
            onCreate(db);
        }
        if (oldVersion<2 ) { Upgrade1to2(db);  }
        if (oldVersion<3 ) { Upgrade2to3(db);  }
        if (oldVersion<4 ) { Upgrade3to4(db);  }
        if (oldVersion<5 ) { Upgrade4to5(db);  }
        if (oldVersion<6 ) { Upgrade5to6(db);  }
        if (oldVersion<7 ) { Upgrade6to7(db);  }
        if (oldVersion<8 ) { Upgrade7to8(db);  }
        if (oldVersion<9 ) { Upgrade8to9(db);  }
        if (oldVersion<10) { Upgrade9to10(db); }
        if (oldVersion<11) { Upgrade10to11(db); }
        if (oldVersion<12) { Upgrade11to12(db); }
    }

    private void Upgrade1to2(SQLiteDatabase db) {
        /**
         * Hinzufügen der beiden Felder für die Location im Parcour
         */
        db.execSQL("ALTER TABLE " + ParcourTbl.TABLE_NAME + " ADD COLUMN "
                + ParcourTbl.GPS_LAT_KOORDINATEN + " TEXT;");
        db.execSQL("ALTER TABLE " + ParcourTbl.TABLE_NAME + " ADD COLUMN "
                + ParcourTbl.GPS_LON_KOORDINATEN + " TEXT;");
        /**
         * Löschen des alten Feldes in der Tabelle Parcour
         */
        try {
            dropColumn(db, ParcourTbl.SQL_CREATE, ParcourTbl.TABLE_NAME, new String[]{"gps_koordinaten"});
        } catch (java.sql.SQLException e) {
            Log.d(TAG, "Upgrade1to2(): SQL Fehlermeldung - " + e.getSQLState());
            Log.d(TAG, "Upgrade1to2(): SQL Fehlermeldung - " + e.getMessage());
        }
    }

    private void Upgrade2to3(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + ZielTbl.TABLE_NAME + " ADD COLUMN "
                + ZielTbl.GPS_LAT_KOORDINATEN + " TEXT;");
        db.execSQL("ALTER TABLE " + ZielTbl.TABLE_NAME + " ADD COLUMN "
                + ZielTbl.GPS_LON_KOORDINATEN + " TEXT;");
    }

    private void Upgrade3to4(SQLiteDatabase db) {
        Log.d(TAG, "upgrade3to4(): started");
        // die Startzeit in anzeigbaren Text umformatieren
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME + " ADD COLUMN "
                + RundenTbl.S_STARTZEIT + " TEXT;");
        convertStartzeitToText(db);
        Log.d(TAG, "upgrade3to4(): ended");
    }

    private void Upgrade4to5(SQLiteDatabase db) {
        Log.d(TAG, "upgrade4to5(): started");
        /**
         * Schuetzen Tabelle für das Synchronisieren erweitern,
         * Default Wert 0 = noch nicht transferiert
         */
        db.execSQL("ALTER TABLE " + SchuetzenTbl.TABLE_NAME + " ADD COLUMN "
                + SchuetzenTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        Log.d(TAG, "upgrade4to5(): ended");
    }

    private void Upgrade5to6(SQLiteDatabase db) {
        Log.d(TAG, "upgrade5to6(): started");
        /**
         * Schuetzen Tabelle um die globale ID erweitern,
         */
        db.execSQL("ALTER TABLE " + SchuetzenTbl.TABLE_NAME + " ADD COLUMN "
                + SchuetzenTbl.GID + " TEXT;");

        /**
         * Initialbefüllung für die GID
         *
         * zunächst Device-Id auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();

        /**
         * Schuetzen-IDs auslesen
         */
        final Cursor c = db.rawQuery(
                "select " + SchuetzenTbl.ID +
                        " from "  + SchuetzenTbl.TABLE_NAME +
                        " where " + SchuetzenTbl.GID + " IS NULL ;",
                null);

        /**
         *
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                dataToInsert.put(SchuetzenTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(SchuetzenTbl.ID))));
                db.update(SchuetzenTbl.TABLE_NAME, dataToInsert, SchuetzenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(SchuetzenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();
        Log.d(TAG, "upgrade5to6(): ended");
    }

    private void Upgrade6to7(SQLiteDatabase db) {
        Log.d(TAG, "upgrade6to7(): started");
        /**
         * Parcour Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + ParcourTbl.TABLE_NAME + " ADD COLUMN "
                + ParcourTbl.GID + " TEXT;");

        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();

        /**
         * Parcour-IDs auslesen
         */
        final Cursor c = db.rawQuery(
                "select " + ParcourTbl.ID +
                " from "  + ParcourTbl.TABLE_NAME +
                " where " + ParcourTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                dataToInsert.put(ParcourTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(ParcourTbl.ID))));
                db.update(ParcourTbl.TABLE_NAME, dataToInsert, ParcourTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(ParcourTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();
        Log.d(TAG, "upgrade6to7(): ended");
    }

    private void Upgrade7to8(SQLiteDatabase db) {
        Log.d(TAG, "upgrade7to8(): started");
        /**
         * Parcour Tabelle für das Synchronisieren erweitern,
         * Default Wert 0 = noch nicht transferiert
         */
        db.execSQL("ALTER TABLE " + ParcourTbl.TABLE_NAME + " ADD COLUMN "
                + ParcourTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        Log.d(TAG, "upgrade7to8(): ended");
    }

    private void Upgrade8to9(SQLiteDatabase db) {
        Log.d(TAG, "upgrade8to9(): started");
        /**
         * Ziel, Bogen, Pfeil, Runden, RundenSchuetzen, RundenZiel Tabellen für das Synchronisieren erweitern,
         * Default Wert 0 = noch nicht transferiert
         */
        db.execSQL("ALTER TABLE " + ZielTbl.TABLE_NAME + " ADD COLUMN "
                + ZielTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        db.execSQL("ALTER TABLE " + BogenTbl.TABLE_NAME + " ADD COLUMN "
                + BogenTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        db.execSQL("ALTER TABLE " + PfeilTbl.TABLE_NAME + " ADD COLUMN "
                + PfeilTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME + " ADD COLUMN "
                + RundenTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        db.execSQL("ALTER TABLE " + RundenSchuetzenTbl.TABLE_NAME + " ADD COLUMN "
                + RundenSchuetzenTbl.TRANSFERED + " INTEGER DEFAULT 0;");
        db.execSQL("ALTER TABLE " + RundenZielTbl.TABLE_NAME + " ADD COLUMN "
                + RundenZielTbl.TRANSFERED + " INTEGER DEFAULT 0;");

        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        Cursor c;
        /**
         * Bogen Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + BogenTbl.TABLE_NAME + " ADD COLUMN "
                + BogenTbl.GID + " TEXT;");

        /**
         * Bogen-IDs auslesen
         */
        c = db.rawQuery(
                "select " + BogenTbl.ID +
                " from "  + BogenTbl.TABLE_NAME +
                " where " + BogenTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Bogen: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(BogenTbl.ID))));
                dataToInsert.put(BogenTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(BogenTbl.ID))));
                db.update(BogenTbl.TABLE_NAME, dataToInsert, BogenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(BogenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * Ziel Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + ZielTbl.TABLE_NAME + " ADD COLUMN "
                + ZielTbl.GID + " TEXT;");

        /**
         * Ziel-IDs auslesen
         */
        c = db.rawQuery(
                "select " + ZielTbl.ID +
                " from "  + ZielTbl.TABLE_NAME +
                " where " + ZielTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Ziel: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID))));
                dataToInsert.put(ZielTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID))));
                db.update(ZielTbl.TABLE_NAME, dataToInsert, ZielTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * Pfeil Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + PfeilTbl.TABLE_NAME + " ADD COLUMN "
                + PfeilTbl.GID + " TEXT;");

        /**
         * Pfeil-IDs auslesen
         */
        c = db.rawQuery(
                "select " + PfeilTbl.ID +
                        " from "  + PfeilTbl.TABLE_NAME +
                        " where " + PfeilTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Pfeil: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(PfeilTbl.ID))));
                dataToInsert.put(PfeilTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(PfeilTbl.ID))));
                db.update(PfeilTbl.TABLE_NAME, dataToInsert, PfeilTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(PfeilTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * Runden Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME + " ADD COLUMN "
                + RundenTbl.GID + " TEXT;");

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenTbl.ID +
                        " from "  + RundenTbl.TABLE_NAME +
                        " where " + RundenTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Runden: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID))));
                dataToInsert.put(RundenTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID))));
                db.update(RundenTbl.TABLE_NAME, dataToInsert, RundenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * RundenSchuetzen Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + RundenSchuetzenTbl.TABLE_NAME + " ADD COLUMN "
                + RundenSchuetzenTbl.GID + " TEXT;");

        /**
         * RundenSchuetzen-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenSchuetzenTbl.ID +
                        " from "  + RundenSchuetzenTbl.TABLE_NAME +
                        " where " + RundenSchuetzenTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "RundenSchuetzen: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID))));
                dataToInsert.put(RundenSchuetzenTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID))));
                db.update(RundenSchuetzenTbl.TABLE_NAME, dataToInsert, RundenSchuetzenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * RundenZiel Tabelle um die globale ID erweitern
         */
        db.execSQL("ALTER TABLE " + RundenZielTbl.TABLE_NAME + " ADD COLUMN "
                + RundenZielTbl.GID + " TEXT;");

        /**
         * RundenZiel-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenZielTbl.ID +
                " from "  + RundenZielTbl.TABLE_NAME +
                " where " + RundenZielTbl.GID + " IS NULL ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "RundenZiel: gid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID))));
                dataToInsert.put(RundenZielTbl.GID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID))));
                db.update(RundenZielTbl.TABLE_NAME, dataToInsert, RundenZielTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();
        Log.d(TAG, "upgrade8to9(): ended");
    }

    private void Upgrade9to10(SQLiteDatabase db) {
        Log.d(TAG, "upgrade9to10(): started");
        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        Cursor c;

        /**
         * Globale Forgein Key bei RUNDEN hinzufügen
         */
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME +
                " ADD COLUMN " + RundenTbl.PARCOURGID + " TEXT NOT NULL DEFAULT \"\";");
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME +
                " ADD COLUMN " + RundenTbl.BOGENGID   + " TEXT NOT NULL DEFAULT \"\";");
        db.execSQL("ALTER TABLE " + RundenTbl.TABLE_NAME +
                " ADD COLUMN " + RundenTbl.PFEILGID   + " TEXT NOT NULL DEFAULT \"\";");

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenTbl.ID        + ", "
                          + RundenTbl.PARCOURID + ", "
                          + RundenTbl.BOGENID   + ", "
                          + RundenTbl.PFEILID   +
                        " from "  + RundenTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Runden: parcourgid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PARCOURID))));
                Log.i(TAG, "Runden: bogengid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.BOGENID))));
                Log.i(TAG, "Runden: pfeilgid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PFEILID))));
                dataToInsert.put(RundenTbl.PARCOURGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PARCOURID))));
                dataToInsert.put(RundenTbl.BOGENGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.BOGENID))));
                dataToInsert.put(RundenTbl.PFEILGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PFEILID))));
                db.update(RundenTbl.TABLE_NAME, dataToInsert, RundenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();
        /**
         * Globale Forgein Key bei RUNDENSCHUETZEN hinzufügen
         */
        db.execSQL("ALTER TABLE " + RundenSchuetzenTbl.TABLE_NAME +
                " ADD COLUMN " + RundenSchuetzenTbl.SCHUETZENGID + " TEXT NOT NULL DEFAULT \"\";");
        db.execSQL("ALTER TABLE " + RundenSchuetzenTbl.TABLE_NAME +
                " ADD COLUMN " + RundenSchuetzenTbl.RUNDENGID    + " TEXT NOT NULL DEFAULT \"\";");

        /**
         * RundenSchuetzen-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenSchuetzenTbl.ID          + ", "
                          + RundenSchuetzenTbl.SCHUETZENID + ", "
                          + RundenSchuetzenTbl.RUNDENID    +
                        " from "  + RundenSchuetzenTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "RundenSchuetzen: schuetzengid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.SCHUETZENID))));
                Log.i(TAG, "RundenSchuetzen: rundengid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.RUNDENID))));
                dataToInsert.put(RundenSchuetzenTbl.SCHUETZENGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.SCHUETZENID))));
                dataToInsert.put(RundenSchuetzenTbl.RUNDENGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.RUNDENID))));
                db.update(RundenSchuetzenTbl.TABLE_NAME, dataToInsert, RundenSchuetzenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * Globale Forgein Key bei RUNDENZIEL hinzufügen
         */
        db.execSQL("ALTER TABLE " + RundenZielTbl.TABLE_NAME +
                " ADD COLUMN " + RundenZielTbl.RUNDENSCHUETZENGID + " TEXT NOT NULL DEFAULT \"\";");
        db.execSQL("ALTER TABLE " + RundenZielTbl.TABLE_NAME +
                " ADD COLUMN " + RundenZielTbl.RUNDENGID          + " TEXT NOT NULL DEFAULT \"\";");
        db.execSQL("ALTER TABLE " + RundenZielTbl.TABLE_NAME +
                " ADD COLUMN " + RundenZielTbl.ZIELGID            + " TEXT NOT NULL DEFAULT \"\";");

        /**
         * RundenZiel-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenZielTbl.ID                + ", "
                          + RundenZielTbl.RUNDENSCHUETZENID + ", "
                          + RundenZielTbl.RUNDENID          + ", "
                          + RundenZielTbl.ZIELID            +
                        " from "  + RundenZielTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "RundenZiel: rundenschuetzengid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENSCHUETZENID))));
                Log.i(TAG, "RundenZiel: rundengid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENID))));
                Log.i(TAG, "RundenZiel: zielgid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ZIELID))));
                dataToInsert.put(RundenZielTbl.RUNDENSCHUETZENGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENSCHUETZENID))));
                dataToInsert.put(RundenZielTbl.RUNDENGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENID))));
                dataToInsert.put(RundenZielTbl.ZIELGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ZIELID))));
                db.update(RundenZielTbl.TABLE_NAME, dataToInsert, RundenZielTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        /**
         * Globale Forgein Key bei ZIEL hinzufügen
         */
        db.execSQL("ALTER TABLE " + ZielTbl.TABLE_NAME +
                " ADD COLUMN " + ZielTbl.PARCOURGID + " TEXT NOT NULL DEFAULT \"\";");

        /**
         * Ziel-IDs auslesen
         */
        c = db.rawQuery(
                "select " + ZielTbl.ID        + ", "
                          + ZielTbl.PARCOURID +
                        " from "  + ZielTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * Initialbefüllung für die GID
         */
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                Log.i(TAG, "Ziel: parcourgid = " + deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.PARCOURID))));
                dataToInsert.put(ZielTbl.PARCOURGID, deviceid + "_" + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.PARCOURID))));
                db.update(ZielTbl.TABLE_NAME, dataToInsert, ZielTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID)))});
            } while (c.moveToNext());
        }
        c.close();

        Log.d(TAG, "upgrade9to10(): ended");
    }

    private void Upgrade10to11(SQLiteDatabase db) {
        Log.d(TAG, "upgrade10to11(): started");
        /**
         * Kontrollieren, ob die Felder korrekt migiert wurden
         */
        if (!checkGID(db))
        {
            Log.e(TAG, "Upgrade10to11(): Fehler, nichts geht mehr..... !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }

        /**
         * Löschen des alten Feldes in der Tabelle Parcour
         */
        try {
            /**
             * Tabelle "Runden" aufräumen
             */
            dropColumn(db, RundenTbl.SQL_CREATE, RundenTbl.TABLE_NAME, new String[]{
                    "parcourid",
                    "bogenid",
                    "pfeilid"
            });
            /**
             * Tabelle "RundenSchuetzen" aufräumen
             */
            dropColumn(db, RundenSchuetzenTbl.SQL_CREATE, RundenSchuetzenTbl.TABLE_NAME, new String[]{
                "schuetzenid",
                "rundenid"
            });
            /**
             * Tabelle "RundenZiel" aufräumen
             */
            dropColumn(db, RundenZielTbl.SQL_CREATE, RundenZielTbl.TABLE_NAME, new String[]{
                "rundenid",
                "zielid",
                "rundenschuetzenid",
                "gpskoordinaten"
            });
            /**
             * Tabelle "Ziel" aufräumen
             */
            dropColumn(db, ZielTbl.SQL_CREATE, ZielTbl.TABLE_NAME, new String[]{
                    "parcourid"
            });
        } catch (java.sql.SQLException e) {
            Log.e(TAG, "Upgrade10to11(): SQL Fehlermeldung - " + e.getSQLState());
            Log.e(TAG, "Upgrade10to11(): SQL Fehlermeldung - " + e.getMessage());
        }
        Log.d(TAG, "upgrade10to11(): ended");
    }

    private void Upgrade11to12(SQLiteDatabase db) {
        Log.d(TAG, "upgrade11to12(): started");

        /**
         * Kontrollieren, ob die in der Tabelle Parcour das Feld GID angelegt ist
         */
        Cursor c = db.rawQuery("pragma table_info(parcour);", null);
        Boolean found = false;
        while (c.moveToNext() && !found){
            if (c.getString(c.getColumnIndex("name")).equals("gid")) {
                found = true;
            }
        }
        c.close();
        /**
         * Feld "gid" nicht gefunden
         */
        if (!found)  Upgrade6to7(db);

        Log.d(TAG, "upgrade11to12(): ended");

    }

    public void convertStartzeitToText(SQLiteDatabase db) {
        final Cursor c = db.rawQuery(
                "select " + RundenTbl.ID + ", "
                          + RundenTbl.STARTZEIT +
                        " from "  + RundenTbl.TABLE_NAME +
                        ";",
                null);
        if (c.moveToFirst()) {
            ContentValues dataToInsert = new ContentValues();
            do {
                dataToInsert.clear();
                dataToInsert.put(RundenTbl.S_STARTZEIT, new SimpleDateFormat("dd/MM/yy HH:mm", Locale.GERMAN).format(new Date(c.getLong(1))));
                db.update(RundenTbl.TABLE_NAME, dataToInsert, RundenTbl.WHERE_ID_EQUALS,
                        new String[]{String.valueOf(c.getInt(0))});
            } while (c.moveToNext());
        }
        c.close();
    }

    public void convertStartzeitToText() {
        convertStartzeitToText(sINSTANCE.getWritableDatabase());
    }

    private void defaultBefüllung(SQLiteDatabase db) {

        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid;
        if (tm.getDeviceId() != null){
            deviceid = tm.getDeviceId();
        }else {
            deviceid = "000000000000000";
        }
        Date tempTimeStamp = new Date();

        // Default-Pfeil anlegen
        final ContentValues daten = new ContentValues();
        daten.put(PfeilTbl.GID, "123456789012345_1");
        daten.put(PfeilTbl.TRANSFERED, 0);
        daten.put(PfeilTbl.NAME, "Default-Pfeil 1");
        daten.put(PfeilTbl.STANDARD, true);
        daten.put(PfeilTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        db.insert(PfeilTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Bogen anlegen
        daten.put(BogenTbl.GID, "123456789012345_1");
        daten.put(BogenTbl.TRANSFERED, 0);
        daten.put(BogenTbl.NAME, "Default-Bogen 1");
        daten.put(BogenTbl.STANDARD, true);
        daten.put(BogenTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        db.insert(BogenTbl.TABLE_NAME, null, daten);
        daten.clear();

        /**
         * 01 Keiler
         * 02 Spinne
         * 03 Raten und Eichhörnchen
         * 04 Osterhase
         * 05 Luxs
         * 06 Dachs
         * 07 Geier
         * 08 Gemse
         * 09 Rehe
         * 10 Uhu
         * 11 Biene
         * 12 Hirschbulle
         * 13 Geier
         * 14 2 Bären
         * 15 stehender Steinbock
         * 16 liegender Steinbock
         * 17 Truthahn
         * 18 2 Pilse
         * 19 liegender Eber
         * 20 2 Füchse
         * 21 Biber, Hase und Pilz
         * 22 stehender Bär
         * 23 2 Rehe
         * 24 fliegender Eber
         * 25 Wolf
         * 26 Ente
         * 27 Frosch
         * 28 Luxs und Vogelfiecher
         */
        // Default-Ziel-1 anlegen

        daten.put(ZielTbl.GID, deviceid + "_1");
        daten.put(ZielTbl.TRANSFERED, 0);
        daten.put(ZielTbl.PARCOURGID, deviceid + "_1");
        daten.put(ZielTbl.NUMMER, 1);
        daten.put(ZielTbl.NAME, "Default Ziel 1");
        daten.put(ZielTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        db.insert(ZielTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Ziel-2 anlegen
        daten.put(ZielTbl.GID, deviceid + "_2");
        daten.put(ZielTbl.TRANSFERED, 0);
        daten.put(ZielTbl.PARCOURGID, deviceid + "_1");
        daten.put(ZielTbl.NUMMER, 2);
        daten.put(ZielTbl.NAME, "Default Ziel 2");
        daten.put(ZielTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        db.insert(ZielTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Parcour anlegen
        daten.put(ParcourTbl.GID, deviceid + "_1");
        daten.put(ParcourTbl.TRANSFERED, 0);
        daten.put(ParcourTbl.NAME, "Default-Parcour");
        daten.put(ParcourTbl.ANZAHL_ZIELE, 2);
        daten.put(ParcourTbl.STRASSE, "Strasse");
        daten.put(ParcourTbl.PLZ, "PLZ");
        daten.put(ParcourTbl.ORT, "Ort");
        daten.put(ParcourTbl.GPS_LAT_KOORDINATEN, "");
        daten.put(ParcourTbl.GPS_LON_KOORDINATEN, "");
        daten.put(ParcourTbl.ANMERKUNG, "Anmerkung");
        daten.put(ParcourTbl.STANDARD, true);
        daten.put(ParcourTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        db.insert(ParcourTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Schuetzen 1 anlegen
        daten.put(SchuetzenTbl.GID, deviceid + "_1");
        daten.put(SchuetzenTbl.NAME, "Default-Schuetze 1");
        daten.put(SchuetzenTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        daten.put(SchuetzenTbl.TRANSFERED, 0);
        db.insert(SchuetzenTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Schuetzen 2 anlegen
        daten.put(SchuetzenTbl.GID, deviceid + "_2");
        daten.put(SchuetzenTbl.NAME, "Default-Schuetze 2");
        daten.put(SchuetzenTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        daten.put(SchuetzenTbl.TRANSFERED, 0);
        db.insert(SchuetzenTbl.TABLE_NAME, null, daten);
        daten.clear();

        // Default-Schuetzen 3 anlegen
        daten.put(SchuetzenTbl.GID, deviceid + "_3");
        daten.put(SchuetzenTbl.NAME, "Default-Schuetze 3");
        daten.put(SchuetzenTbl.ZEITSTEMPEL, tempTimeStamp.getTime());
        daten.put(SchuetzenTbl.TRANSFERED, 0);
        db.insert(SchuetzenTbl.TABLE_NAME, null, daten);
        daten.clear();
    }

    private void dropColumn( SQLiteDatabase db,
                             String createTableCmd,
                             String tableName,
                             String[] colsToRemove) throws java.sql.SQLException {

        List<String> updatedTableColumns = getTableColumns(db ,tableName);
        // Remove the columns we don't want anymore from the table's list of columns
        updatedTableColumns.removeAll(Arrays.asList(colsToRemove));

        String columnsSeperated = TextUtils.join(",", updatedTableColumns);

        db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");

        // Creating the table on its new format (no redundant columns)
        db.execSQL(createTableCmd);

        // Populating the table with the data
        db.execSQL("INSERT INTO " + tableName + "(" + columnsSeperated + ") SELECT "
                + columnsSeperated + " FROM " + tableName + "_old;");
        db.execSQL("DROP TABLE " + tableName + "_old;");
    }

    private List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        ArrayList<String> columns = new ArrayList<String>();
        String cmd = "pragma table_info(" + tableName + ");";
        Cursor cur = db.rawQuery(cmd, null);

        while (cur.moveToNext()) {
            columns.add(cur.getString(cur.getColumnIndex("name")));
        }
        cur.close();

        return columns;
    }

    public void exportDB(){
        exportDB("myarrow_backup.db");
    }

    public void exportDB(String backupDBPath){
        File sd = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "com.example.rene.myarrow" + "/databases/" + DATENBANK_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            GmailSender gSender = new GmailSender(mContext);
            gSender.sendMail(
                    "My Arrow Database Backup",
                    "Bitte sicher abspeichern",
                    backupDB,
                    null, null);
            Toast.makeText(mContext, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreDB(){
        File sd = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "com.example.rene.myarrow" + "/databases/" + DATENBANK_NAME;
        String backupDBPath = "myarrow_backup.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        // zunächst aktuelle Datenbank löschen
        currentDB.deleteOnExit();
        // jetzt wird der Restore gestartet....
        try {
            source = new FileInputStream(backupDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(mContext, "DB Restored!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkGID(SQLiteDatabase db){

        Log.d(TAG, "checkGID(): started");
        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();

        /**
         * Tabellen nur mit Primary-Key
         */
        if (!checkBogenGID(db, deviceid)) return false;
        if (!checkPfeilGID(db, deviceid)) return false;
        if (!checkSchuetzenGID(db, deviceid)) return false;
        if (!checkParcourGID(db, deviceid)) return false;

        /**
         * Tabellen mit Primary-Key und Forgein-Keys
         */
        if (!checkRundenGID(db, deviceid)) return false;
        if (!checkRundenSchuetzenGID(db, deviceid)) return false;
        if (!checkRundenZielGID(db, deviceid)) return false;
        if (!checkZielGID(db, deviceid)) return false;

        Log.d(TAG, "checkGID(): ended");
        return true;

    }

    private boolean compareGID(String deviceid, String id, String gid) {
        if (!gid.equals(deviceid + "_" + id)) {
            Log.e(TAG, "compareGID(): FEHLER!!! "
                     + deviceid + "_" + id
                     + " => "
                     + gid);
            return false;
        }
        return true;
    }

    private boolean checkRundenGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenTbl.ID + ", "
                          + RundenTbl.PARCOURID + ", "
                          + RundenTbl.BOGENID + ", "
                          + RundenTbl.PFEILID + ", "
                          + RundenTbl.GID + ", "
                          + RundenTbl.PARCOURGID + ", "
                          + RundenTbl.BOGENGID + ", "
                          + RundenTbl.PFEILGID +
                " from "  + RundenTbl.TABLE_NAME +
                " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkRundenGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(RundenTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.ID))),
                        c.getString(c.getColumnIndex(RundenTbl.GID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PARCOURID))),
                        c.getString(c.getColumnIndex(RundenTbl.PARCOURGID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.BOGENID))),
                        c.getString(c.getColumnIndex(RundenTbl.BOGENGID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenTbl.PFEILID))),
                        c.getString(c.getColumnIndex(RundenTbl.PFEILGID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkRundenSchuetzenGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenSchuetzenTbl.ID + ", "
                          + RundenSchuetzenTbl.SCHUETZENID + ", "
                          + RundenSchuetzenTbl.RUNDENID + ", "
                          + RundenSchuetzenTbl.GID + ", "
                          + RundenSchuetzenTbl.SCHUETZENGID + ", "
                          + RundenSchuetzenTbl.RUNDENGID +
                        " from "  + RundenSchuetzenTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkRundenSchuetzenGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(RundenSchuetzenTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.ID))),
                        c.getString(c.getColumnIndex(RundenSchuetzenTbl.GID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.SCHUETZENID))),
                        c.getString(c.getColumnIndex(RundenSchuetzenTbl.SCHUETZENGID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenSchuetzenTbl.RUNDENID))),
                        c.getString(c.getColumnIndex(RundenSchuetzenTbl.RUNDENGID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkRundenZielGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + RundenZielTbl.ID + ", "
                          + RundenZielTbl.RUNDENSCHUETZENID + ", "
                          + RundenZielTbl.ZIELID + ", "
                          + RundenZielTbl.RUNDENID   + ", "
                          + RundenZielTbl.GID + ", "
                          + RundenZielTbl.RUNDENSCHUETZENGID + ", "
                          + RundenZielTbl.ZIELGID + ", "
                          + RundenZielTbl.RUNDENGID +
                " from "  + RundenZielTbl.TABLE_NAME +
                " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkRundenZielGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(RundenZielTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ID))),
                        c.getString(c.getColumnIndex(RundenZielTbl.GID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENSCHUETZENID))),
                        c.getString(c.getColumnIndex(RundenZielTbl.RUNDENSCHUETZENGID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.ZIELID))),
                        c.getString(c.getColumnIndex(RundenZielTbl.ZIELGID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(RundenZielTbl.RUNDENID))),
                        c.getString(c.getColumnIndex(RundenZielTbl.RUNDENGID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkZielGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Runden-IDs auslesen
         */
        c = db.rawQuery(
                "select " + ZielTbl.ID + ", "
                          + ZielTbl.PARCOURID + ", "
                          + ZielTbl.GID + ", "
                          + ZielTbl.PARCOURGID +
                " from "  + ZielTbl.TABLE_NAME +
                " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkZielGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(ZielTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID))),
                        c.getString(c.getColumnIndex(ZielTbl.GID)))) {
                    return false;
                }

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.PARCOURID))),
                        c.getString(c.getColumnIndex(ZielTbl.PARCOURGID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkBogenGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Bogen-IDs auslesen
         */
        c = db.rawQuery(
                "select " + BogenTbl.ID + ", "
                          + BogenTbl.GID +
                        " from "  + ZielTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkZielGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(ZielTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(ZielTbl.ID))),
                        c.getString(c.getColumnIndex(ZielTbl.GID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkPfeilGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Pfeil-IDs auslesen
         */
        c = db.rawQuery(
                "select " + PfeilTbl.ID + ", "
                          + PfeilTbl.GID +
                " from "  + PfeilTbl.TABLE_NAME +
                " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkPfeilGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(PfeilTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(PfeilTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(PfeilTbl.ID))),
                        c.getString(c.getColumnIndex(PfeilTbl.GID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkParcourGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Parcour-IDs auslesen
         */
        c = db.rawQuery(
                "select " + ParcourTbl.ID + ", "
                          + ParcourTbl.GID +
                " from "  + ParcourTbl.TABLE_NAME +
                " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkParcourGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(ParcourTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(ParcourTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(ParcourTbl.ID))),
                        c.getString(c.getColumnIndex(ParcourTbl.GID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    private boolean checkSchuetzenGID(SQLiteDatabase db, String deviceid) {
        Cursor c;

        /**
         * Schuetzen-IDs auslesen
         */
        c = db.rawQuery(
                "select " + SchuetzenTbl.ID + ", "
                          + SchuetzenTbl.GID +
                        " from "  + SchuetzenTbl.TABLE_NAME +
                        " ;",
                null);

        /**
         * GID kontrollieren
         */
        if (c.moveToFirst()) {
            do {

                Log.d(TAG, "checkSchuetzenGID(): ID ="
                        + String.valueOf(c.getInt(c.getColumnIndex(SchuetzenTbl.ID)))
                        + " / GID="
                        + c.getString(c.getColumnIndex(SchuetzenTbl.GID)));

                if (!compareGID(
                        deviceid,
                        String.valueOf(c.getInt(c.getColumnIndex(SchuetzenTbl.ID))),
                        c.getString(c.getColumnIndex(SchuetzenTbl.GID)))) {
                    return false;
                }

            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    /**
     *
     */
    public boolean storeForgeinDataset(ContentValues daten, String table) {
        Log.i(TAG, "storeForgeinDataset(): Start");
        final SQLiteDatabase dbCon = sINSTANCE.getWritableDatabase();
        try {
            //
            final long id = dbCon.insertOrThrow(table, null, daten);

        } catch(SQLiteConstraintException e) {
            // TODO Fehler 2067 noch abfangen
            if (e.equals(19)) {             // SQLITE_CONSTRAINT = Abort due to constraint violation
                String gid = daten.getAsString("gid");
                daten.remove("gid");
                final int mid = dbCon.update(table, daten, "gid=?", new String[]{ gid });
                if (mid != 1) {
                    Log.e(TAG, "storeForgeinDataset(): Ein nicht behebbarer Fehler (update) ist ausgetreten !! =>" + e.toString());
                    return false;

                }
            } else {
                Log.e(TAG, "storeForgeinDataset(): Ein nicht behebbarer Fehler (insert) ist ausgetreten !! =>" + e.toString());
                return false;

            }

        } finally {
            dbCon.close();

        }

        Log.i(TAG, "storeForgeinDataset(): End");
        return true;
    }
}
