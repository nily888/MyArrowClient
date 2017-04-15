package com.example.rene.myarrow.GUI.Ergebnis;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Runden.Runden;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.MainActivity;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.MyDialog;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by nily on 16.12.15.
 */
public class GesamtErgebnisAnzeigen extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ErgebnisAmZiel.class.getSimpleName();

    /** lokale Variablen initialisieren */
    private String mParcourGID;
    private String mRundenGID;
    private Parcour mParcour;
    private Runden mRunden;

    @Override
    protected void onCreate(Bundle icicle) {
        // Super Methode aufrufen
        super.onCreate(icicle);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        setContentView(R.layout.gesamt_ergebnis_anzeigen);

        Button ciaoButton = (Button)findViewById(R.id.ciao_button);
        Resources res = getResources();
        Drawable d = res.getDrawable(R.mipmap.start_button);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        ciaoButton.setBackground(d);
        ciaoButton.setTypeface(null, Typeface.BOLD);
        ciaoButton.setTextColor(Color.BLACK);

        /* */
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGID = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
        } else {
            Log.w(TAG, "oncreate(): Keine Parcour-Id übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_RUNDEN_GID)) {
            mRundenGID = extras.getString(Konstante.IN_PARAM_RUNDEN_GID);
        } else {
            Log.w(TAG, "oncreate(): Keine Runden-Id übergeben");
        }

        /* Parcour Details laden*/
        ParcourSpeicher pS= new ParcourSpeicher(this);
        mParcour = pS.loadParcourDetails(mParcourGID);

        /* Runden Details laden*/
        RundenSpeicher rS= new RundenSpeicher(this);
        mRunden = rS.loadRunden(mRundenGID);

    }

    @Override
    protected void onStart() {
        zeigeDetails();
        super.onStart();
    }

    /**
     * Befüllt die Views der Activity mit den Daten des
     * GeoKontakts aus der Datenbank.
     */
    private void zeigeDetails() {
        /*
          Parcour Name anzeigen

          */
        final TextView fldName = (TextView) findViewById(R.id.txt_name);
        fldName.setText(mParcour.name);

        /*
          Runden Startzeit anzeigen

         */
        final TextView fldStart = (TextView) findViewById(R.id.txt_startzeit);
        if (mRunden.startzeit > 0) {
            fldStart.setText(DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM, DateFormat.SHORT).
                    format(new Date(mRunden.startzeit)));
        } else {
            fldStart.setText("unbekannt");
        }

        /*
          Runden Endzeit anzeigen

         */
        final TextView fldEnd = (TextView) findViewById(R.id.txt_endzeit);
        if (mRunden.endzeit > 0) {
            fldEnd.setText(DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM, DateFormat.SHORT).
                    format(new Date(mRunden.endzeit)));
        } else {
            fldEnd.setText("unbekannt");
        }

        /*
          Runden Dauer anzeigen

         */
        final TextView fldDauer = (TextView) findViewById(R.id.txt_dauer);
        fldDauer.setText(String.valueOf(((mRunden.endzeit-mRunden.startzeit)/1000/60)));

        //
        // Gesamtergebnis anzeigen
        //
        RundenSchuetzenSpeicher rsSpeicher = new RundenSchuetzenSpeicher(this);
        String[][] rs = rsSpeicher.getRundenSchuetzenMax(mRundenGID);
        int max = Integer.valueOf(rs[0][2]);
        for (int n=1; n<(rs.length); n++){
            if (max < Integer.valueOf(rs[n][2])){
                max = Integer.valueOf(rs[n][2]);
            }
        }
        final Button fldGesamt = (Button) findViewById(R.id.txt_gesamtergebnis);
        fldGesamt.setText(String.valueOf(max));

        /*
          Parcour Name anzeigen

          */
        final TextView fldAnmerkung = (TextView) findViewById(R.id.txt_anmerkungen);
        fldAnmerkung.setText("noch nicht implementiert!");

        Log.d(TAG, "zeigeDetails(): End");

    }

    public void onClickSchuetzenliste(View v){
        MyDialog testIt = new MyDialog();
        testIt.setTitle("Ergebnis");
        RundenSchuetzenSpeicher rsSpeicher = new RundenSchuetzenSpeicher(this);
        String[][] rs = rsSpeicher.getRundenSchuetzenMax(mRundenGID);
        String mListe="";
        String mTemp;
        for (int n=0; n<(rs.length); n++){
            if (Integer.valueOf(rs[n][2])<10) {
                mTemp = "    " + rs[n][2].replace(" ", "");
            } else if (Integer.valueOf(rs[n][2])<100) {
                mTemp = "  " + rs[n][2].replace(" ", "");
            } else {
                mTemp = rs[n][2].replace(" ", "");
            }
            mListe = mListe
                    + mTemp + " "
                    + rs[n][1] + "\n\n";
        }
        testIt.setMessage(mListe);
        testIt.show(getFragmentManager(), "Test-It");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.mobilfunknummer_anzeigen, menu);
        return true;
    }

    public void onClickCiao(View v){
		    finish();
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
    }

}
