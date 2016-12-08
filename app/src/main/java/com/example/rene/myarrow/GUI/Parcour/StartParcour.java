package com.example.rene.myarrow.GUI.Parcour;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenTbl;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.GUI.Ergebnis.ErgebnisAmZiel;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BerechneErgebnis;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.MultiSpinner;
import com.example.rene.myarrow.misc.MultiSpinner.MultiSpinnerListener;
import com.example.rene.myarrow.misc.ShowMap;

/**
 * Created by nily on 15.12.15.
 */
public class StartParcour extends Activity implements MultiSpinnerListener{

    /** Kuerzel fuers Logging. */
    private static final String TAG = StartParcour.class.getSimpleName();

    /** Schnittstelle zur persistenten Speicher. */
    private ParcourSpeicher mParcourSpeicher;

    /** Die DB Id des ausgewählten Kontaktes. */
    private String    mParcourGId;
    private Parcour   parcourCursor;
    private String[] schuetzenArray;
    private String[] ausgewaehlteSchuetzen;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.parcour_start);
        final Bundle extras = getIntent().getExtras();
        if (extras != null &&
                extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGId = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
        } else {
            Log.w(TAG, "Keine Parcour Id übergeben");
        }

        // Spinner befüllen
        // fillBogenSpinner();
        // fillPfeilSpinner();
        fillSchuetzenSpinner();

        // ParcourSpeicher initialisieren
        mParcourSpeicher = new ParcourSpeicher(this);
        parcourCursor = mParcourSpeicher.loadParcourDetails(mParcourGId);

    }

    @Override
    protected void onStart() {
        zeigeDetails();
        super.onStart();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onDestroy() {
        mParcourSpeicher.schliessen();
        super.onDestroy();
    }

    /**
     * Befüllt die Views der Activity mit den Daten des
     * GeoKontakts aus der Datenbank.
     */
    private void zeigeDetails() {
        if (mParcourGId.equals("")) {
            Log.e(TAG, "zeigeDetails(): Fehler mParcour = " + mParcourGId);
            return;
        }

        /** Parcour Name und auf transparent setzen */
        final Button fldName = (Button) findViewById(R.id.txt_name);
        fldName.setText(parcourCursor.name);
        fldName.setBackgroundColor(Color.TRANSPARENT);

        /** Anzahl der Ziele */
        final TextView fldAnzahl = (TextView) findViewById(R.id.txt_anzahl_ziele);
        fldAnzahl.setText(String.valueOf(parcourCursor.anzahl_ziele));

        /** Maximal erreichbare Punktezahl */
        BerechneErgebnis berechnePunkte = new BerechneErgebnis(this);
        final TextView fldPunkte = (TextView) findViewById(R.id.txt_max_punkte);
        fldPunkte.setText(String.valueOf(parcourCursor.anzahl_ziele*berechnePunkte.getErgebnis(1, 2)));

        /** Maximal erreichte Punktezahl */
	    RundenSchuetzenSpeicher rundenSchuetzenSpeicher = new RundenSchuetzenSpeicher(this);
        final TextView fldmaxPunkte = (TextView) findViewById(R.id.txt_maximal_erreichte_punkte);
        fldmaxPunkte.setText(String.valueOf(rundenSchuetzenSpeicher.getParcourMax(mParcourGId)));

        /** Button Hintergrundbild setzen */
        Button startButton = (Button)findViewById(R.id.los_button);
        Resources res = getResources();
        Drawable d = res.getDrawable(R.mipmap.start_button);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        startButton.setBackground(d);
        startButton.setTypeface(null, Typeface.BOLD);
        startButton.setTextColor(Color.BLACK);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.mobilfunknummer_anzeigen, menu);
        return true;
    }

    public void onClickShowMap(View v){
        // TODO: nach ShowMap schieben?
        Bundle mBundle = new Bundle();
        ZielSpeicher zs = new ZielSpeicher(this);
        Cursor c = zs.loadZielListe(parcourCursor.gid);
        if (c != null || c.getCount()<1) {
            String[][] zielListe = new String[c.getCount()][3];
            int n = 0;
            c.moveToFirst();
            do {
                // TODO WICHTIG UMSTELLEN
                zielListe[n][0] = c.getString(3); // c.getColumnIndex(ZielTbl.NAME)
                zielListe[n][1] = c.getString(4); // c.getColumnIndex(ZielTbl.GPS_LAT_KOORDINATEN)
                zielListe[n][2] = c.getString(5); // c.getColumnIndex(ZielTbl.GPS_LON_KOORDINATEN)
                n++;
            } while (c.moveToNext());
            c.close();
            final Intent i = new Intent(this, ShowMap.class);
            mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
            i.putExtras(mBundle);
            startActivity(i);
        }
    }

    public void onClickRundeStarten(View v) {
        //
        // Wenn kein Schuetze, dann sofort wieder zurueck....
        //
        if (ausgewaehlteSchuetzen == null){
            Toast.makeText(this, "Es wurde kein Schütze ausgewählt", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * Neue Runde anlegen und Daten entsprechend abspeichern
         */
        RundenSpeicher rundenSpeicher = new RundenSpeicher(this);

        /* *
         * Bogen Spinner auslesen
         */
        // Spinner BogenSpinner = (Spinner) findViewById(R.id.bogen_spinner);
        // Cursor cBogenItemText = (Cursor) BogenSpinner.getSelectedItem();
        // BogenSpeicher bogenSpeicher = new BogenSpeicher(this);
        //
        // Pfeil Spinner auslesen
        //
        // Spinner PfeilSpinner = (Spinner) findViewById(R.id.pfeil_spinner);
        // Cursor cPfeilItemText = (Cursor) PfeilSpinner.getSelectedItem();
        // PfeilSpeicher pfeilSpeicher = new PfeilSpeicher(this);
        /* *
         * Wetter Spinner auslesen
         */
        Spinner WetterSpinner = (Spinner) findViewById(R.id.wetter_spinner);
        String wetterItemText = WetterSpinner.getSelectedItem().toString();
        /**
         * Startzeit notieren
         */
        long tempTimeStamp = new Date().getTime();

        /**
         * Jetzt die Runde anlegen
         */
        long rundenID = rundenSpeicher.insertRunden(
                mParcourGId,        // parcourGID
                "123456789012345_1",     // bogenGID = DEFAULT
                "123456789012345_1",     // pfeilGID = DEFAULT
                tempTimeStamp,
                0,
                wetterItemText);

        /**
         * und jetzt noch die Schuetzen und RundenZiele anlegen
         */
        // Speicher initialisieren
        ZielSpeicher mZiel = new ZielSpeicher(this);
        RundenZielSpeicher mRundenZielSpeicher = new RundenZielSpeicher(this);
        RundenSchuetzenSpeicher rsTemp = new RundenSchuetzenSpeicher(this);
        // Schleife über alle Schützen
        for (int m=0; m<(ausgewaehlteSchuetzen.length); m++) {
            // Zunächst den Schuetzen für diese Runde anlegen
            rsTemp.insertRundenSchuetzen(ausgewaehlteSchuetzen[m], rundenSpeicher.getGID(rundenID), 0, tempTimeStamp);
            // Schleife über alle Ziele
            for (int n = 1; n <= parcourCursor.anzahl_ziele; n++) {
                mRundenZielSpeicher.insertRundenziel(
                        rundenSpeicher.getGID(rundenID),          // rundenGID
                        mZiel.getZielGID(parcourCursor.gid, n),   // zielGID
                        ausgewaehlteSchuetzen[m],                 // rundenschuetzenGID
                        n,
                        false, false, false, false, false,
                        0,
                        "",
                        "",
                        tempTimeStamp);
            }
        }

        /**
         * und jetzt zur nächsten Seite und dem ersten Ziel
         */
        final Intent i = new Intent(this, ErgebnisAmZiel.class);
        i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGId);
        i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, rundenSpeicher.getGID(rundenID));
        i.putExtra(Konstante.OUT_PARAM_ANZAHL_ZIELE_ID, parcourCursor.anzahl_ziele);
        i.putExtra(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, 1);
        i.putExtra(Konstante.OUT_PARAM_NEUES_ZIEL, 1);
        finish();
        startActivity(i);
    }

    /**
    private void fillBogenSpinner() {
        Log.d(TAG, "fillBogenSpinner(): Start");
        BogenSpeicher bogenSpeicher = new BogenSpeicher(this);
        Cursor bogenCursor = bogenSpeicher.loadBogenListe();
        Spinner BogenSpinner = (Spinner) findViewById(R.id.bogen_spinner);

        // Now create an array adapter and set it to display using our row
        Log.d(TAG, "fillBogenSpinner(): SimpleCursorAdapter");
        SimpleCursorAdapter spinnerBogenadapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                bogenCursor,
                new String[] { BogenTbl.NAME },
                new int[] {android.R.id.text1},
                0);

        spinnerBogenadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // BogenSpinner.setPromptId(0);
        BogenSpinner.setAdapter(spinnerBogenadapter);
        Log.d(TAG, "fillBogenSpinner(): End");
    }

    private void fillPfeilSpinner() {

        Log.d(TAG, "fillPfeilSpinner(): Start");

        PfeilSpeicher pfeilSpeicher = new PfeilSpeicher(this);
        Cursor pfeilCursor = pfeilSpeicher.loadPfeilListe();
        Spinner PfeilSpinner = (Spinner) findViewById(R.id.pfeil_spinner);

        // Now create an array adapter and set it to display using our row
        Log.d(TAG, "fillPfeilSpinner(): SimpleCursorAdapter");
        SimpleCursorAdapter spinnerPfeiladapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                pfeilCursor,
                new String[] { PfeilTbl.NAME },
                new int[] {android.R.id.text1},
                0);

        spinnerPfeiladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // PfeilSpinner.setPromptId(0);
        PfeilSpinner.setAdapter(spinnerPfeiladapter);

        Log.d(TAG, "fillPfeilSpinner(): End");

    }
    */

    private void fillSchuetzenSpinner() {
        // Declaring List Variable`
        MultiSpinner SchuetzenSpinner;
        SchuetzenSpeicher schuetzenSpeicher = new SchuetzenSpeicher(this);
        List<String> items = new ArrayList<String>();
        // Schuetzenliste aufbauen
        Cursor schuetzenListe = schuetzenSpeicher.loadSchuetzenListe();
        schuetzenArray = new String[schuetzenListe.getCount()];
        int n = 0;
        while (schuetzenListe.moveToNext()) {
            //Adding item to List
            schuetzenArray[n] = schuetzenListe.getString(schuetzenListe.getColumnIndex(SchuetzenTbl.GID));
            items.add(schuetzenListe.getString(schuetzenListe.getColumnIndex(SchuetzenTbl.NAME)));
            n++;
        }
        // Schützenauswahl anzeigen
        SchuetzenSpinner = (MultiSpinner) findViewById(R.id.schuetzen_spinner);
        SchuetzenSpinner.setItems(items, "Schützen", StartParcour.this);
    }

    @Override
    public void onItemsSelected(boolean[] selected){
        int m=0;
        for (int n=0; n<(selected.length); n++){
            if (selected[n]) {
                m++;
            }
        }
        ausgewaehlteSchuetzen = new String[m];
        m=0;
        for (int n=0; n<(selected.length); n++){
            if (selected[n]) {
                ausgewaehlteSchuetzen[m]= schuetzenArray[n];
                m++;
            }
        }
    }
}
