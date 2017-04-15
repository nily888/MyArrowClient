package com.example.rene.myarrow.GUI.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.Database.MyArrowDB;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.GUI.Schuetzen.UpdateRundenErgebnis;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.Synchronisation.EmpfangeDatenService;
import com.example.rene.myarrow.Synchronisation.SendeDatenService;
import com.example.rene.myarrow.misc.CheckDateinamen;

/**
 * 
 * @author René Düber
 *
 */
public class Tools extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = Tools.class.getSimpleName();
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools);
        context = this.getApplicationContext();
    }

    public void onClickBackupDB(View v) {
        /*
          Export der Datenbank ins MyArrow Standardverzeichnis.
         */
        MyArrowDB.getInstance(this).exportDB();
    }

    public void onClickRestoreDB(View v) {
        /*
          Import der Datenbank aus dem MyArrow Standardverzeichnis.
         */
        MyArrowDB.getInstance(this).restoreDB();
    }

    public void onClickSyncServer(View v) {
        Log.d(TAG, "onClickSyncServer(): Startet");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    Log.d(TAG, "onClickSyncServer(): Starte Thread...");
                    new SendeDatenService().selektiereDaten(context);
                    new EmpfangeDatenService(context).empfangeDaten(context);
                    Log.d(TAG, "onClickSyncServer(): ... Ende Thread");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Toast.makeText(this, "Sync im Hintergrund gestartet...", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onClickSyncServer(): Ende");
    }

    public void onClickSyncReset(View v) {
        Log.d(TAG, "onClickSyncReset(): Startet");
        new BogenSpeicher(this).transferReset();
        new PfeilSpeicher(this).transferReset();
        new ParcourSpeicher(this).transferReset();
        new RundenSpeicher(this).transferReset();
        new RundenSchuetzenSpeicher(this).transferReset();
        new RundenZielSpeicher(this).transferReset();
        new SchuetzenSpeicher(this).transferReset();
        new ZielSpeicher(this).transferReset();
        Toast.makeText(this, "Alle Tabellen resetet...", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onClickSyncReset(): Endet");
    }

    public void onClickCheckFiles(View v) {
        new CheckDateinamen(this).checkDatei();
    }

    public void onClickCheckGallery(View v) {
        new CheckDateinamen(this).checklistDir();
    }

    public void onClickConvertStartzeit(View v) {
        // Startzeit in anzeigbaren Text convertieren
        MyArrowDB.getInstance(this).convertStartzeitToText();
    }

    public void onClickUpdateGesamtPunkte(View v) {
        // Aktualisieren der Ergebnisse pro Runde und Schütze
        new UpdateRundenErgebnis(this).runUpdate();
    }
}