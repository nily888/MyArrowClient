package com.example.rene.myarrow.GUI.Ergebnis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZiel;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.GUI.Ziel.AddZiel;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BerechneErgebnis;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.ShowMap;
import com.example.rene.myarrow.misc.WoBinIch;
import com.example.rene.myarrow.misc.setPic;

import static android.support.design.R.id.info;

public class ErgebnisAmZiel extends FragmentActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ErgebnisAmZiel.class.getSimpleName();

    /** lokale Variablen initialisieren */
    private String              mParcourGID;
    private String              mRundenGID;
    private String              mRundenSchuetzenGID;
    private int                 mAnzahlZiele;
    private int                 mAktuellesZiel;
    private int                 mNeuesZiel;
    private ZielSpeicher        zielSpeicher;
    private Ziel                mZiel;
    private RundenZiel          rz;
    private String[][]          rundenschuetzenArray;
    private ViewPager           viewPager;
    private TabsPagerAdapter    mAdapter;
    private ArrayList<Fragment> mFragments;
    private boolean             bClickWeiter = false;
    private Context             mContext;
    private String[]            sMyOptions;

    @Override
    public void onCreate(Bundle icicle) {
        //
        // Super Methode aufrufen
        //
        super.onCreate(icicle);

        //
        // Hole Übergabeparameter ab
        //
        getÜbergabeparameter();

        //
        // View laden
        //
        setContentView(R.layout.ergebnis_am_ziel);

        //
        mContext = this;

        //
        // WeiterButton Bild laden
        //
        Button weiterButton = (Button) findViewById(R.id.weiter_button);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.mipmap.start_button, null);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        weiterButton.setBackground(d);
        weiterButton.setTypeface(null, Typeface.BOLD);
        weiterButton.setTextColor(Color.BLACK);

        //
        // Ziel Informationen holen
        //
        zielSpeicher = new ZielSpeicher(this);
        mZiel = zielSpeicher.loadZiel(mParcourGID, mAktuellesZiel);

        //
        // Runden-Schuetzen Namen holen
        // mRundenSchuetzenId muss belegt werden, wenn es nur ein Schuetze ist (WICHTIG)
        //
        rundenschuetzenArray = new RundenSchuetzenSpeicher(this).loadRundenSchuetzenListe(mRundenGID);
        mRundenSchuetzenGID = rundenschuetzenArray[0][1];

        //
        // Runden Ziel Informatrionen holen
        //
        rz = new RundenZiel();

        //
        // Wenn zuvor der Zurueck-Button gedrückt wurde, Punkte wieder abziehen
        //
        if (mNeuesZiel==0) {
            updatePunktekonto();
        }

        //
        //
        //
        loadTrefferAmZiel();

        //
        sMyOptions = getResources().getStringArray(R.array.my_options);
        final Spinner spinnerMyOptions = (Spinner) findViewById(R.id.but_ziel_button);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_options_liste,
                R.id.txt_options, sMyOptions);
        spinnerMyOptions.setAdapter(adapter);
        spinnerMyOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                switch (arg2) {

                    case 1:
                        // Bild soll angezeigt werden
                        if (mZiel.dateiname != null && !mZiel.dateiname.equals("")) {
                            // Bild gibt es schon, jetzt nur noch anzeigen
                            BildAnzeigen bildAnzeigen = new BildAnzeigen(mContext, mZiel.dateiname);
                            bildAnzeigen.show();
                        } else {
                            final Intent i = new Intent(mContext, GetPicture.class);
                            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Ergebnis_Ziel_" + String.valueOf(mAktuellesZiel));
                            startActivityForResult(i, 1003);
                        }
                        break;

                    case 2:
                        // Map mit dem Zielort soll angezeigt werden
                        if (mZiel.gps_lat_koordinaten == null || mZiel.gps_lon_koordinaten == null ||
                            mZiel.gps_lat_koordinaten.isEmpty() || mZiel.gps_lon_koordinaten.isEmpty()) {
                            String[] tempWo = new WoBinIch(mContext).getLocation();
                                if (tempWo != null) {
                                    mZiel.gps_lat_koordinaten = tempWo[0];
                                    mZiel.gps_lon_koordinaten = tempWo[1];
                                    zielSpeicher.updateZiel(mZiel);
                                }
                        }
                        if ( mZiel.gps_lat_koordinaten != null &&
                             mZiel.gps_lon_koordinaten != null &&
                            !mZiel.gps_lat_koordinaten.equals("") &&
                            !mZiel.gps_lon_koordinaten.equals("") &&
                            !mZiel.gps_lat_koordinaten.equals("NULL") &&
                            !mZiel.gps_lon_koordinaten.equals("NULL")) {
                                String[][] zielListe = new String[1][3];
                                final Intent intent = new Intent(mContext, ShowMap.class);
                                zielListe[0][0] = mZiel.name;
                                zielListe[0][1] = mZiel.gps_lat_koordinaten;
                                zielListe[0][2] = mZiel.gps_lon_koordinaten;
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
                                intent.putExtras(mBundle);
                                startActivity(intent);
                        }
                        break;

                    case 3:
                        Log.d(TAG, "onCreate(): Parcour Add Ziel Start");
                        Intent i = new Intent(mContext, AddZiel.class);
                        Log.d(TAG, "onCreate(): Parcour Add Ziel - ParcourGID     - " + mParcourGID);
                        Log.d(TAG, "onCreate(): Parcour Add Ziel - RundenGID      - " + mRundenGID);
                        Log.d(TAG, "onCreate(): Parcour Add Ziel - mAktuellesZiel - " + mAktuellesZiel);
                        i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
                        i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
                        i.putExtra(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, mAktuellesZiel );
                        startActivityForResult(i, 1004);
                        Log.d(TAG, "onCreate(): Parcour Add Ziel End");
                        break;
                }
                // wieder auch den ersten Wert setzen
                spinnerMyOptions.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
           }

        });
    }

    @Override
    public void onStart() {
        /*
          Super Methode aufrufen
         */
        super.onStart();
        /*
          Jetzt alle Felder / Details anzeigen => leere View wird ausgefüllt
         */
        zeigeDetails();
    }

    @Override
    public void onPause() {
        //
        // Wenn auf Weiter geclickt wurde, wurde dies eh schon aufgerufen, sonst Context zwischenspeichern
        //
        View ThisView = mFragments.get(viewPager.getCurrentItem()).getView();
        if (!bClickWeiter && ThisView != null) {
            punkteSpeichern(ThisView);
        }
        //
        // Super Methode aufrufen
        //
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        onClickZurueck(this.getCurrentFocus());
    }

    private ArrayList<Fragment> loadMyFragments(){
        for (int index=0;(index<rundenschuetzenArray.length); index++) {
            Fragment newFragment = new TrefferAmZiel();
            Bundle bundle = new Bundle();
            /*
              Submit rundenGID
             */
            bundle.putString(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
            /*
              Submit all rundenschuetzen GIDs (Array)
             */
            bundle.putString(Konstante.OUT_PARAM_RUNDENSCHUETZEN_GID, rundenschuetzenArray[index][1]);
            /*
              Submit the current Target
             */
            bundle.putInt(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, mAktuellesZiel);
            /*
              OUT_PARAM_ZIEL_GID = -1 means
             */
            bundle.putString(Konstante.OUT_PARAM_ZIEL_GID, "-1");
            bundle.putString("schuetzenname", rundenschuetzenArray[index][2]);
            newFragment.setArguments(bundle);
            newFragment.setRetainInstance(true);
            mFragments.add(newFragment);
        }
        return mFragments;
    }

    private void zeigeDetails() {
        /*
          Prüfen, ob die Parameter passen
         */
        if (mAnzahlZiele < 1) {
            Log.d(TAG, "zeigeDetails(): Hier ist wohl etwas schief gegangen!!");
            return;
        }
        /*
          aktuelle Zielnummer und Anzahl der Ziele in dieser Runden anzeigen
         */
        sMyOptions[0] = String.valueOf(mAktuellesZiel) + "/" + String.valueOf(mAnzahlZiele);
        /*
          Wenn vorhanden, Ergebnisbild anzeigen
         */
        RundenZielSpeicher rzSpeicher = new RundenZielSpeicher(this);
        String mDateiname = rzSpeicher.getDateiname(mRundenGID, mAktuellesZiel);
        if (mDateiname!=null && !mDateiname.equals("")) {
            File mFile = new File(mDateiname);
            if (mFile.exists()) {
                Button imageButton = (Button) findViewById(R.id.foto_button);
                new setPic(this, imageButton, mDateiname);
            }
        }
    }

    private void loadTrefferAmZiel(){
        /*
          Swipe vorbereiten
         */
        mFragments = new ArrayList<>();
        mFragments = loadMyFragments();
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);
        //
        // on swiping the viewpager make respective tab selected
        //
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int oldPosition = 0;

            @Override
            public void onPageSelected(int position) {
                View ThisFragment = mFragments.get(oldPosition).getView();
                if (ThisFragment != null && oldPosition != position) {
                    mRundenSchuetzenGID = rundenschuetzenArray[oldPosition][1];
                    punkteSpeichern(ThisFragment);
                    mRundenSchuetzenGID = rundenschuetzenArray[position][1];
                    mAdapter.notifyDataSetChanged();
                    oldPosition = position;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void onClickZurueck(View v) {
        /*
          wollen Sie wirklich zurück?
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zurück");
        builder.setMessage("Sind Sie sicher?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mAktuellesZiel > 1) {
                    /*
                      und jetzt zur vorherigen Seite / Ziel
                     */
                    final Intent i = new Intent(mContext, ErgebnisAmZiel.class);
                    i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
                    i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
                    i.putExtra(Konstante.OUT_PARAM_ANZAHL_ZIELE_ID, mAnzahlZiele);
                    i.putExtra(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, mAktuellesZiel - 1);
                    i.putExtra(Konstante.OUT_PARAM_NEUES_ZIEL, 0);
                    startActivity(i);
                    finish();
                } else {
                    /*
                      es soll der Parcour abgebrochen werden
                     */
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Abbrechen");
                    builder.setMessage("Sind Sie sicher?");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Context ac = getApplicationContext();
                            new RundenSpeicher(ac).deleteKompletteRunden(mRundenGID);
                            finish();
                            dialog.dismiss();
                        }

                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void onClickFoto(View v) {
        punkteSpeichern(viewPager.getChildAt(viewPager.getCurrentItem()));
        RundenZielSpeicher rzSpeicher = new RundenZielSpeicher(this);
        String mDateiname = rzSpeicher.getDateiname(mRundenGID, mAktuellesZiel);
        if (mDateiname!=null && !mDateiname.equals("")) {
            // Bild gibt es schon, jetzt nur noch anzeigen
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mDateiname);
            bildAnzeigen.show();
        }
        else{
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Ergebnis_Ziel_" + String.valueOf(mAktuellesZiel));
            startActivityForResult(i, 1002);
        }
    }

    public void onClickWeiter(View v) {
        //
        //
        //
        bClickWeiter = true;
        //
        // Daten sichern
        //
        datenSpeichern();
        /*
          und jetzt zur nächsten Seite / Ziel oder es ist zu Ende
         */
        if (mAktuellesZiel == mAnzahlZiele) {
            // Ende...
            final Intent i = new Intent(this, GesamtErgebnisAnzeigen.class);
            i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
            i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
            Log.d(TAG, "Am Ende.... " + mAktuellesZiel);
            startActivity(i);
            finish();
        } else {
            // Zur nächsten Seite....
            final Intent i = new Intent(this, ErgebnisAmZiel.class);
            i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
            i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
            i.putExtra(Konstante.OUT_PARAM_ANZAHL_ZIELE_ID, mAnzahlZiele);
            i.putExtra(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, (mAktuellesZiel + 1));
            i.putExtra(Konstante.OUT_PARAM_NEUES_ZIEL, 1);
            Log.d(TAG, "Zum nächsten Ziel: " + (mAktuellesZiel + 1));
            startActivity(i);
            finish();
        }
    }

    private void datenSpeichern() {
        /*
          letzte Daten abspeichern
         */
        punkteSpeichern(mFragments.get(viewPager.getCurrentItem()).getView());
        /*
          Variable initialisieren
         */
        RundenZiel rz;
        int mSchuss;
        int mKill;
        BerechneErgebnis berechnePunkte = new BerechneErgebnis(this);
        /*
          Daten auslesen
          */
        for (int n=0; n<(rundenschuetzenArray.length); n++) {
            rz = new RundenZielSpeicher(this).loadRundenZiel(mRundenGID,
                    rundenschuetzenArray[n][1], mAktuellesZiel);
            /*
              wie gut wurde getroffen
             */
            mSchuss=0;
            mKill=0;
            if (rz.eins) mSchuss = 1;
            if (rz.zwei) mSchuss = 2;
            if (rz.drei) mSchuss = 3;
            if (rz.kill) mKill = 1;
            if (rz.killkill) mKill = 2;
            rz.punkte = berechnePunkte.getErgebnis(mSchuss, mKill);
            /*
              Update der Daten im Runden Ziel
             */
            RundenZielSpeicher rzSpeicher = new RundenZielSpeicher(this);
            rzSpeicher.updateRundenziel(rz);
            /*
               Gesamtpunkte aktualisieren
             */
            RundenSchuetzenSpeicher mRundenSchuetzenSpeicher = new RundenSchuetzenSpeicher(this);
            mRundenSchuetzenSpeicher.updateGesamtergebnis(mRundenGID,
                    rundenschuetzenArray[n][1], rz.punkte);
        }
        //
        // Endezeit noch abspeichern
        //
        RundenSpeicher rundenSpeicher = new RundenSpeicher(this);
        rundenSpeicher.updateEndzeit(mRundenGID);
    }

    private void punkteSpeichern(View myView) {
        //
        // Ist die View ggf leer?
        //
        if (myView == null) {
            Log.e(TAG, "punkteSpeichern(): Fehler bei der Übergabe der View (leer) !!");
            Log.d(TAG, "punkteSpeichern(): ParcourGID           = " + mParcourGID);
            Log.d(TAG, "punkteSpeichern(): RundenGID            = " + mRundenGID);
            Log.d(TAG, "punkteSpeichern(): RundenSchuetzenGID   = " + mRundenSchuetzenGID);
            Log.d(TAG, "punkteSpeichern(): Anzahl Ziele         = " + mAnzahlZiele);
            Log.d(TAG, "punkteSpeichern(): Aktuelles Ziel       = " + mAktuellesZiel);
            Log.d(TAG, "punkteSpeichern(): Neues Ziel           = " + mNeuesZiel);
            Log.d(TAG, "punkteSpeichern(): zielSpeicher         = " + zielSpeicher.toString());
            Log.d(TAG, "punkteSpeichern(): Ziel                 = " + mZiel.toString());
            Log.d(TAG, "punkteSpeichern(): rundenZiel           = " + rz.toString());
            Log.d(TAG, "punkteSpeichern(): rundenschuetzenArray = " + rundenschuetzenArray.toString());
            Log.d(TAG, "punkteSpeichern(): sMyOptions           = " + sMyOptions.toString());
            return;
        }
        //
        // Variable initialisieren
        //
        ToggleButton toggleDisplay;
        RundenZiel rz = new RundenZiel();
        rz.rundengid = mRundenGID;
        rz.zielid = mZiel.id;
        rz.nummer= mAktuellesZiel;
        rz.rundenschuetzengid = mRundenSchuetzenGID;

        //
        // Daten aus der View auslesen
        //
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.ein_schuss);
        rz.eins = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.zwei_schuss);
        rz.zwei = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.drei_schuss);
        rz.drei = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.kill_schuss);
        rz.kill = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.killkill_schuss);
        rz.killkill = toggleDisplay.isChecked();

        /*
          Zeitstempel abspeichern
         */
        rz.zeitstempel = new Date().getTime();

        /*
          Update der Daten im Runden Ziel
         */
        new RundenZielSpeicher(this).updatePunkteRundenziel(rz);

    }

    protected void getÜbergabeparameter() {
        /* Hole Übergabeparameter ab */
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGID = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
        } else {
            Log.e(TAG, "oncreate(): Keine Parcour-Id übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_RUNDEN_GID)) {
            mRundenGID = extras.getString(Konstante.IN_PARAM_RUNDEN_GID);
        } else {
            Log.e(TAG, "oncreate(): Keine Runden-Id übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_ANZAHL_ZIELE_ID)) {
            mAnzahlZiele = extras.getInt(Konstante.IN_PARAM_ANZAHL_ZIELE_ID);
        } else {
            Log.e(TAG, "oncreate(): Keine Anzahl Ziele übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_AKTUELLES_ZIEL_ID)) {
            mAktuellesZiel = extras.getInt(Konstante.IN_PARAM_AKTUELLES_ZIEL_ID);
        } else {
            Log.e(TAG, "oncreate(): Kein aktuelles Ziel übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_NEUES_ZIEL)) {
            mNeuesZiel = extras.getInt(Konstante.IN_PARAM_NEUES_ZIEL);
        } else {
            Log.e(TAG, "oncreate(): Kein neues Ziel übergeben");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        if (data == null) return;
        final Bundle mExtras = data.getExtras();
        //
        // Ergebnisbild
        //
        if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    new RundenZielSpeicher(this).updateDateiname(mRundenGID, mAktuellesZiel, mDateiname, new Date().getTime());
                    /* Bild, falls vorhanden, anzeigen */
                    if (mDateiname != null && !mDateiname.equals("")) {
                        Button fotoButton2 = (Button)findViewById(R.id.foto_button);
                        new setPic(this, fotoButton2, mDateiname);
                    }
                } else {
                    Log.w(TAG, "onActivityResult(): Nichts übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        //
        // Zielbild
        //
        else if (requestCode == 1003){
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mZielBildDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    // Bild, falls vorhanden, anzeigen
                    if (mZielBildDateiname != null && !mZielBildDateiname.equals("")) {
                        // Bild Dateiname abspeichern
                        zielSpeicher.updateDateiname(mZiel.gid, mZielBildDateiname);
                    }
                } else {
                    Log.w(TAG, "onActivityResult(): Kein Dateiname übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        else if (requestCode == 1004){
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_ANZAHL_ZIELE_ID)) {
                    int tempAnzahlZiele = mExtras.getInt(Konstante.IN_PARAM_ANZAHL_ZIELE_ID);
                    if (tempAnzahlZiele != mAnzahlZiele) mAnzahlZiele = tempAnzahlZiele;
                } else {
                    Log.w(TAG, "onActivityResult(): Kein Anzahle der Ziele erhalten, Fehler??");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private void updatePunktekonto() {
        int tempPunkte;
        RundenZielSpeicher rzSpeicher = new RundenZielSpeicher(this);
        RundenSchuetzenSpeicher rsSpeicher = new RundenSchuetzenSpeicher(this);
        for (int n=0; n<(rundenschuetzenArray.length); n++) {
            tempPunkte = rzSpeicher.getRundenZielPunkte(mRundenGID, rundenschuetzenArray[n][1], mAktuellesZiel);
            rsSpeicher.updateGesamtergebnis(mRundenGID, rundenschuetzenArray[n][1], -tempPunkte);
        }
    }

}