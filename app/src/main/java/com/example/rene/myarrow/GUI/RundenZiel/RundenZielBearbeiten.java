package com.example.rene.myarrow.GUI.RundenZiel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzen;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZiel;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.Schuetzen;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.GUI.Ergebnis.TabsPagerAdapter;
import com.example.rene.myarrow.GUI.Ergebnis.TrefferAmZiel;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BerechneErgebnis;
import com.example.rene.myarrow.misc.BildAnzeigen;
import com.example.rene.myarrow.misc.GetPicture;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.setPic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class RundenZielBearbeiten extends FragmentActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = RundenZielBearbeiten.class.getSimpleName();

    /** lokale Variablen initialisieren */
    private String              mRundenZielGID;
    private Ziel                mZiel;
    private RundenZiel          mRundenZiel;
    private Schuetzen           mSchuetzen;
    private ViewPager           viewPager;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle icicle) {
        //
        // Super Methode aufrufen
        //
        super.onCreate(icicle);

        //
        // Hole Übergabeparameter ab
        //
        getÜbergabeparameter();
        Context mContext = this;

        //
        // View laden
        //
        setContentView(R.layout.ergebnis_am_ziel);

        //
        // WeiterButton Bild laden und Text setzen
        //
        Button weiterButton = (Button) findViewById(R.id.weiter_button);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.mipmap.start_button, null);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        weiterButton.setBackground(d);
        weiterButton.setTypeface(null, Typeface.BOLD);
        weiterButton.setTextColor(Color.BLACK);
        weiterButton.setText("Speichern");

        //
        // ZurückButton Text setzen
        //
        Button zurueckButton = (Button) findViewById(R.id.zurueck_button);
        zurueckButton.setText("Abbrechen");

        //
        // RundenZiel laden
        //
        mRundenZiel = new RundenZielSpeicher(this).loadRundenZiel(mRundenZielGID);

        //
        // Ziel laden
        //
        mZiel = new ZielSpeicher(this).loadZiel(String.valueOf(mRundenZiel.zielgid));

        //
        // Runden-Schuetzen laden
        //
        RundenSchuetzen mRundenSchuetzen = new RundenSchuetzenSpeicher(this).loadRundenSchuetzenSchuetzenGID(mRundenZiel.rundenschuetzengid);

        //
        // Schuetzen laden
        //
        mSchuetzen = new SchuetzenSpeicher(this).loadSchuetzenDetails(mRundenSchuetzen.schuetzengid);

        //
        // Fragment TrefferAmZiel aufbauen
        //
        Fragment newFragment = new TrefferAmZiel();
        Bundle bundle = new Bundle();
        Log.d(TAG,"oncreate(): RUNDENZIEL_GID      - " + mRundenZiel.rundengid + " / " + mRundenZielGID);
        Log.d(TAG,"oncreate(): RUNDENSCHUETZEN_GID - " + mRundenSchuetzen.schuetzengid);
        Log.d(TAG,"oncreate(): ZIEL_GID            - " + mRundenZiel.zielgid);
        bundle.putString(Konstante.OUT_PARAM_RUNDEN_GID, mRundenZiel.rundengid);
        bundle.putString(Konstante.OUT_PARAM_RUNDENSCHUETZEN_GID, mRundenSchuetzen.schuetzengid);
        bundle.putInt(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, -1); // mZiel.nummer
        bundle.putString(Konstante.OUT_PARAM_ZIEL_GID, mRundenZiel.gid);
        //        zielgid);
        bundle.putString("schuetzenname", mSchuetzen.name);
        newFragment.setArguments(bundle);
        newFragment.setRetainInstance(true);
        mFragments = new ArrayList<Fragment>();
        mFragments.add(newFragment);
        viewPager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);

        //
        //
        //
        String[] sMyOptions = getResources().getStringArray(R.array.my_options);
        final Spinner spinnerMyOptions = (Spinner) findViewById(R.id.but_ziel_button);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_options_liste,
                R.id.txt_options, sMyOptions);
        spinnerMyOptions.setAdapter(adapter);
        sMyOptions[0] = String.valueOf(mZiel.nummer) + " - " + String.valueOf(mZiel.name);
    }

    @Override
    protected void onStart() {
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
    protected void onPause() {
        //
        // Wenn auf Weiter geklickt wurde, wurde dies eh schon aufgerufen, sonst Context zwischenspeichern
        //
        if (!mFragments.isEmpty()) {
            View ThisView = mFragments.get(viewPager.getCurrentItem()).getView();
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

    private void zeigeDetails() {
        //
        // Wenn vorhanden, Ergebnisbild anzeigen
        //
        if (mRundenZiel.dateiname!=null && !mRundenZiel.dateiname.equals("")) {
            File mFile = new File(mRundenZiel.dateiname);
            if (mFile.exists()) {
                Button imageButton = (Button) findViewById(R.id.foto_button);
                new setPic(this, imageButton, mRundenZiel.dateiname);
            }
        }
    }

    public void onClickZurueck(View v) {
        mFragments.clear();
        final Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void onClickFoto(View v) {
        String mDateiname = mRundenZiel.dateiname;
        if (mDateiname!=null && !mDateiname.equals("")) {
            // Bild gibt es schon, jetzt nur noch anzeigen
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mDateiname);
            bildAnzeigen.show();
        }
        else{
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "Ergebnis_Ziel_" + String.valueOf(mZiel.nummer));
            startActivityForResult(i, 1002);
        }
    }

    public void onClickWeiter(View v) {
        //
        // Daten sichern
        //
        datenSpeichern();
        //
        // nur OK zurückgeben
        //
        final Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        //
        // aktuelles Activity zerstören
        //
        finish();
    }

    public void onClickZielBild(View v) {
        if (mZiel.dateiname != null && !mZiel.dateiname.equals("")) {
            BildAnzeigen bildAnzeigen = new BildAnzeigen(this, mZiel.dateiname);
            bildAnzeigen.show();
        } else {
            final Intent i = new Intent(this, GetPicture.class);
            i.putExtra(Konstante.OUT_PARAM_DATEINAME_ID, "ZielBild_" + String.valueOf(mZiel.nummer));
            startActivityForResult(i, 1003);
        }
    }

    private void datenSpeichern() {

        //
        // Daten im RundenZiel abspeichern
        //
        punkteSpeichern(mFragments.get(viewPager.getCurrentItem()).getView());

        //
        // Variable initialisieren
        //
        int mSchuss;
        int mKill;
        BerechneErgebnis berechnePunkte = new BerechneErgebnis(this);

        //
        // wie gut wurde getroffen
        //
        mSchuss=0;
        mKill=0;
        if (mRundenZiel.eins) mSchuss = 1;
        if (mRundenZiel.zwei) mSchuss = 2;
        if (mRundenZiel.drei) mSchuss = 3;
        if (mRundenZiel.kill) mKill = 1;
        if (mRundenZiel.killkill) mKill = 2;
        int mPunkteAlt = mRundenZiel.punkte;
        mRundenZiel.punkte = berechnePunkte.getErgebnis(mSchuss, mKill);

        //
        // Update der Daten im Runden Ziel => =0
        //
        new RundenZielSpeicher(this).updatePunkteRundenziel(mRundenZiel);

        //
        // Gesamtpunkte aktualisieren
        //
        // alten Wert abziehen
        new RundenSchuetzenSpeicher(this)
                .updateGesamtergebnis(mRundenZiel.rundengid, mSchuetzen.gid, -mPunkteAlt);
        // neuen Wert draufaddieren
        new RundenSchuetzenSpeicher(this)
                .updateGesamtergebnis(mRundenZiel.rundengid, mSchuetzen.gid, mRundenZiel.punkte);
    }

    private void punkteSpeichern(View myView) {
        //
        // Ist die View ggf leer?
        //
        if (myView == null) {
            Log.e(TAG, "punkteSpeichern(): Fehler bei der Übergabe der View (leer) !!");
            return;
        }

        //
        // Variable initialisieren
        //
        ToggleButton toggleDisplay;

        //
        // Daten aus der View auslesen
        //
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.ein_schuss);
        mRundenZiel.eins = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.zwei_schuss);
        mRundenZiel.zwei = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.drei_schuss);
        mRundenZiel.drei = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.kill_schuss);
        mRundenZiel.kill = toggleDisplay.isChecked();
        toggleDisplay = (ToggleButton) myView.findViewById(R.id.killkill_schuss);
        mRundenZiel.killkill = toggleDisplay.isChecked();

        /*
          Zeitstempel abspeichern
         */
        mRundenZiel.zeitstempel = new Date().getTime();

        /*
          Update der Daten im Runden Ziel
         */
        new RundenZielSpeicher(this).updatePunkteRundenziel(mRundenZiel);

    }

    protected void getÜbergabeparameter() {
        /* Hole Übergabeparameter ab */
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_RUNDENZIEL_GID)) {
            mRundenZielGID = extras.getString(Konstante.IN_PARAM_RUNDENZIEL_GID);
            Log.d(TAG, "oncreate(): RUNDENZIEL_GID - " + mRundenZielGID);
        } else {
            Log.w(TAG, "oncreate(): Keine RundenZiel-Id übergeben");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras = data.getExtras();
        //
        // Ergebnisbild
        //
        if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
                    String mDateiname = mExtras.getString(Konstante.IN_PARAM_DATEINAME_ID);
                    new RundenZielSpeicher(this).updateDateiname(mRundenZiel.rundengid, mZiel.nummer, mDateiname, new Date().getTime());
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
                        new ZielSpeicher(this).updateDateiname(mZiel.gid, mZielBildDateiname);
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
    }
}

