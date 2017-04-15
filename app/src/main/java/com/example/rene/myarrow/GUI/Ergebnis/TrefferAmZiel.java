package com.example.rene.myarrow.GUI.Ergebnis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZiel;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BerechneErgebnis;
import com.example.rene.myarrow.misc.Konstante;

public class TrefferAmZiel extends Fragment implements View.OnClickListener{

    /** Kuerzel fuers Logging. */
    private static final String TAG = TrefferAmZiel.class.getSimpleName();

    /**
     * Variable initialisieren
     */
    View             rootView;
    String           mRundenGID;
    String           mRundenSchuetzenGID;
    int              mAktuellesZiel;
    String           mZielGID;
    String           mSchuetze;
    Context          mContext;
    BerechneErgebnis berechnePunkte;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        berechnePunkte = new BerechneErgebnis(mContext);
        getParameter(savedInstanceState);
        /*
          View anzeigen
         */
        rootView = inflater.inflate(R.layout.treffer_am_ziel, container, false);
        /*
          Buttons in der View anlegen
         */
        // im ersten Schuss
        ToggleButton einSchuss = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
        einSchuss.setTextOff("im 1ten Schuss");
        einSchuss.setTextOn("+" + String.valueOf(berechnePunkte.getErgebnis(1, 0)) + " Punkte");
        einSchuss.setOnClickListener(this);
        // im zweiten Schuss
        ToggleButton zweiSchuss = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
        zweiSchuss.setTextOff("im 2ten Schuss");
        zweiSchuss.setTextOn("+" + String.valueOf(berechnePunkte.getErgebnis(2, 0)) + " Punkte");
        zweiSchuss.setOnClickListener(this);
        // im dritten Schuss
        ToggleButton dreiSchuss = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
        dreiSchuss.setTextOff("im 3ten Schuss");
        dreiSchuss.setTextOn("+" + String.valueOf(berechnePunkte.getErgebnis(3, 0)) + " Punkte");
        dreiSchuss.setOnClickListener(this);
        //
        ToggleButton killSchuss = (ToggleButton) rootView.findViewById(R.id.kill_schuss);
        killSchuss.setTextOff("Kill?");
        killSchuss.setTextOn("+" + String.valueOf(berechnePunkte.getErgebnis(0, 1)) + " Punkte");
        killSchuss.setOnClickListener(this);
        //
        ToggleButton killkillSchuss = (ToggleButton) rootView.findViewById(R.id.killkill_schuss);
        killkillSchuss.setTextOff("Spot-Kill?");
        killkillSchuss.setTextOn("+" + String.valueOf(berechnePunkte.getErgebnis(0, 2)) + " Punkte");
        killkillSchuss.setOnClickListener(this);
        /*

         */
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        zeigeDetails();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("rundenGID", mRundenGID);
        savedInstanceState.putString("rundenschuetzenGID", mRundenSchuetzenGID);
        savedInstanceState.putInt("maktuellesziel", mAktuellesZiel);
        savedInstanceState.putString("schuetzenname", mSchuetze);
        savedInstanceState.putString(Konstante.IN_PARAM_ZIEL_GID, mZielGID);
    }

    private void zeigeDetails(){
        /*
          Variable initialisieren
         */
        RundenZiel rz = new RundenZiel();
        Log.d(TAG, "zeigeDetails(): mAktuellesZiel = " + mAktuellesZiel);
        Log.d(TAG, "zeigeDetails(): mZielGID       = " + mZielGID);
        /*

          mAktuallesZiel = -1 means
          mZielGID = -1 means

         */
        // TODO to be filled out
        if (mAktuellesZiel>-1 && mZielGID.equals("-1")) {
            rz = new RundenZielSpeicher(mContext)
                    .loadRundenZiel(mRundenGID, mRundenSchuetzenGID, mAktuellesZiel);
        } else {
            rz = new RundenZielSpeicher(mContext).loadRundenZiel(mZielGID);
        }
        if (rz==null) {
            Log.e(TAG, "zeigeDetails(): Ziel nicht gefunden!!!");
            return;
        }

        ToggleButton toggleDisplay;
        /*
          Ein Schuss
         */
        toggleDisplay = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
        toggleDisplay.setChecked(rz.eins);
        /*
          Zwei Schuss
         */
        toggleDisplay = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
        toggleDisplay.setChecked(rz.zwei);
        /*
          Drei Schuss
         */
        toggleDisplay = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
        toggleDisplay.setChecked(rz.drei);
        /*
          Kill
         */
        toggleDisplay = (ToggleButton) rootView.findViewById(R.id.kill_schuss);
        toggleDisplay.setChecked(rz.kill);
        /*
          Kill-Kill
         */
        toggleDisplay = (ToggleButton) rootView.findViewById(R.id.killkill_schuss);
        toggleDisplay.setChecked(rz.killkill);
        /*
          Schuetzennamen anzeigen
         */
        TextView fldSchuetze = (TextView) rootView.findViewById(R.id.txt_schuetze);
        fldSchuetze.setText(mSchuetze);
        /*
          aktuellen Punktestand anzeigen
         */
        int mAktuellePunkte = new RundenSchuetzenSpeicher(mContext)
                .getPunktestand(mRundenGID, mRundenSchuetzenGID);
        int mNochErreichbar = (
                new ParcourSpeicher(mContext).getAnzahlZiele(
                        new RundenSpeicher(mContext).getParcourGID(mRundenGID))
                - mAktuellesZiel + 1
                )*berechnePunkte.maxPunkte() +
                mAktuellePunkte;
        String mPunkteStand = String.valueOf(mAktuellePunkte) + "/" + String.valueOf(mNochErreichbar);
        TextView fldaktuellePunkte = (TextView) rootView.findViewById(R.id.txt_aktuellerpunktestand);
        fldaktuellePunkte.setText(mPunkteStand);
        //
        // Ende
        //
    }

    private void getParameter(Bundle savedInstanceState){
        Bundle bundle = this.getArguments();
        mRundenGID = bundle.getString(Konstante.IN_PARAM_RUNDEN_GID);
        Log.d(TAG, "getParameter(): mRundenGID - " + mRundenGID);
        if (mRundenGID.isEmpty() || mRundenGID.equals("")) {
            mRundenGID = savedInstanceState.getString(Konstante.IN_PARAM_RUNDEN_GID);
        }
        mRundenSchuetzenGID = bundle.getString(Konstante.IN_PARAM_RUNDENSCHUETZEN_GID);
        Log.d(TAG, "getParameter(): mRundenSchuetzenGID - " + mRundenSchuetzenGID);
        if (mRundenSchuetzenGID.isEmpty() || mRundenSchuetzenGID.equals("")) {
            mRundenSchuetzenGID = savedInstanceState.getString(Konstante.IN_PARAM_RUNDENSCHUETZEN_GID);
        }
        mAktuellesZiel = bundle.getInt(Konstante.IN_PARAM_AKTUELLES_ZIEL_ID);
        Log.d(TAG, "getParameter(): mAktuellesZiel - " + mAktuellesZiel);
        if (mAktuellesZiel == 0){
            mAktuellesZiel = savedInstanceState.getInt(Konstante.IN_PARAM_AKTUELLES_ZIEL_ID);
        }
        mSchuetze = bundle.getString("schuetzenname");
        Log.d(TAG, "getParameter(): mSchuetze - " + mSchuetze);
        if (mSchuetze.isEmpty() || mSchuetze.equals("")) {
            mSchuetze = savedInstanceState.getString("schuetzenname");
        }
        mZielGID = bundle.getString(Konstante.IN_PARAM_ZIEL_GID);
        Log.d(TAG, "getParameter(): mZielGID       - " + mZielGID);
        if (mZielGID.isEmpty() || mZielGID.equals("")) {
            mZielGID = savedInstanceState.getString(Konstante.IN_PARAM_ZIEL_GID);
        }
    }

    @Override
    public void onClick(View v) {
        /* Variable initialisieren */
        ToggleButton toggleDisplay1 = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
        ToggleButton toggleDisplay2 = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
        ToggleButton toggleDisplay3 = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
        ToggleButton toggleDisplayK = (ToggleButton) rootView.findViewById(R.id.kill_schuss);
        ToggleButton toggleDisplayS = (ToggleButton) rootView.findViewById(R.id.killkill_schuss);

        /* Soll die Änderung durchgeführt werden? */
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Ändern?");
        builder.setMessage("Sind Sie sicher?");

        //
        switch (v.getId()) {
            case R.id.ein_schuss:
                if (toggleDisplay2.isChecked() || toggleDisplay3.isChecked()){
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick(): setPositiveBottun() - Start");
                            ToggleButton toggleDisplay2 = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
                            toggleDisplay2.setChecked(false); // Decheck Zwei Schuss
                            ToggleButton toggleDisplay3 = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
                            toggleDisplay3.setChecked(false); // Decheck Drei Schuss
                            dialog.dismiss();
                            Log.d(TAG, "onClick(): setPositiveButton() - End");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToggleButton toggleDisplay1 = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
                            toggleDisplay1.toggle();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;

            case R.id.zwei_schuss:
                if (toggleDisplay1.isChecked() || toggleDisplay3.isChecked()){
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick(): setPositiveBottun() - Start");
                            ToggleButton toggleDisplay1 = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
                            toggleDisplay1.setChecked(false); // Decheck Ein Schuss
                            ToggleButton toggleDisplay3 = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
                            toggleDisplay3.setChecked(false); // Decheck Drei Schuss
                            dialog.dismiss();
                            Log.d(TAG, "onClick(): setPositiveButton() - End");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToggleButton toggleDisplay2 = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
                            toggleDisplay2.toggle();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;

            case R.id.drei_schuss:
                if (toggleDisplay2.isChecked() || toggleDisplay1.isChecked()){
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick(): setPositiveBottun() - Start");
                            ToggleButton toggleDisplay2 = (ToggleButton) rootView.findViewById(R.id.zwei_schuss);
                            toggleDisplay2.setChecked(false); // Decheck Zwei Schuss
                            ToggleButton toggleDisplay1 = (ToggleButton) rootView.findViewById(R.id.ein_schuss);
                            toggleDisplay1.setChecked(false); // Decheck Drei Schuss
                            dialog.dismiss();
                            Log.d(TAG, "onClick(): setPositiveButton() - End");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToggleButton toggleDisplay3 = (ToggleButton) rootView.findViewById(R.id.drei_schuss);
                            toggleDisplay3.toggle();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.kill_schuss:
                if (toggleDisplayS.isChecked()){
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick(): setPositiveBottun() - Start");
                            ToggleButton toggleDisplayS = (ToggleButton) rootView.findViewById(R.id.killkill_schuss);
                            toggleDisplayS.setChecked(false); // Decheck Zwei Schuss
                            dialog.dismiss();
                            Log.d(TAG, "onClick(): setPositiveButton() - End");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToggleButton toggleDisplayK = (ToggleButton) rootView.findViewById(R.id.kill_schuss);
                            toggleDisplayK.toggle();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;

            case R.id.killkill_schuss:
                if (toggleDisplayK.isChecked()){
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick(): setPositiveBottun() - Start");
                            ToggleButton toggleDisplayK = (ToggleButton) rootView.findViewById(R.id.kill_schuss);
                            toggleDisplayK.setChecked(false); // Decheck Zwei Schuss
                            dialog.dismiss();
                            Log.d(TAG, "onClick(): setPositiveButton() - End");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToggleButton toggleDisplayS = (ToggleButton) rootView.findViewById(R.id.killkill_schuss);
                            toggleDisplayS.toggle();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
    }

}
