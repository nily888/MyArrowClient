package com.example.rene.myarrow.GUI.Parcour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rene.myarrow.GUI.RundenZiel.RundenZielBearbeiten;
import com.example.rene.myarrow.GUI.RundenZiel.SelectRundenZiele;
import com.example.rene.myarrow.misc.Konstante;

/**
 * Created by rened on 20.03.2016.
 */
public class ReviewParcour extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ReviewParcour.class.getSimpleName();
    private String mParcourGID;
    private String mRundenGID;
    private String mSchuetzenGID;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //
        // Den Parcour auswählen
        //
        final Intent i = new Intent(this, SelectParcour.class);
        startActivityForResult(i, 3001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        final Bundle mExtras;
        if ( data != null) {
            mExtras = data.getExtras();
        } else {
            mExtras = null;
        }
        //
        // Parcour wurde ausgewählt, jetzt muss die Runde ausgewählt werden
        //
        if (requestCode == 3001) {
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
                    mParcourGID = mExtras.getString(Konstante.IN_PARAM_PARCOUR_GID);
                    final Intent i = new Intent(this, SelectRunde.class);
                    i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
                    startActivityForResult(i, 3002);
                } else {
                    Log.w(TAG, "onActivityResult(3001): Es wurde nichts übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                finish();
            } else {
                // Image capture failed, advise user
            }
        }
        //
        // Runde wurde ausgewählt, jetzt muss der Schuetze ausgewählt werden
        //
        else if (requestCode == 3002){
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_RUNDEN_GID)) {
                    final Intent i = new Intent(this, SelectRundenSchuetzen.class);
                    mRundenGID = mExtras.getString(Konstante.IN_PARAM_RUNDEN_GID);
                    i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
                    startActivityForResult(i, 3003);
                } else {
                    Log.w(TAG, "onActivityResult(3002): Es wurde nichts übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                final Intent i = new Intent(this, SelectParcour.class);
                startActivityForResult(i, 3001);
            } else {
                // Image capture failed, advise user
            }
        }
        //
        // Runde und Schuetze wurde ausgewählt, jetzt aus der Liste der Ziele das Ziel auswählen,
        // welche bearbeitet werden soll
        //
        else if (requestCode == 3003){
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
                    final Intent i = new Intent(this, SelectRundenZiele.class);
                    mSchuetzenGID = mExtras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
                    i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
                    i.putExtra(Konstante.OUT_PARAM_SCHUETZEN_GID, mSchuetzenGID);
                    startActivityForResult(i, 3004);
                } else {
                    Log.w(TAG, "onActivityResult(3003): Es wurde nichts übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                final Intent i = new Intent(this, SelectRunde.class);
                i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourGID);
                startActivityForResult(i, 3002);
            } else {
                // Image capture failed, advise user
                Log.e(TAG, "onActivityResult(3003): resultCode = " + resultCode);
            }
        }
        //
        // Rundenziel kann jetzt bearbeitet werden
        //
        else if (requestCode == 3004){
            if (resultCode == Activity.RESULT_OK) {
                if (mExtras != null && mExtras.containsKey(Konstante.IN_PARAM_RUNDENZIEL_GID)) {
                    final Intent i = new Intent(this, RundenZielBearbeiten.class);
                    i.putExtra(Konstante.OUT_PARAM_RUNDENZIEL_GID,
                            mExtras.getString(Konstante.IN_PARAM_RUNDENZIEL_GID));
                    startActivityForResult(i, 3005);
                } else {
                    Log.w(TAG, "onActivityResult(3004): Es wurde nichts übergeben");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                final Intent i = new Intent(this, SelectRundenSchuetzen.class);
                i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
                startActivityForResult(i, 3003);
            } else {
                // Image capture failed, advise user
                Log.e(TAG, "onActivityResult(3004): resultCode = " + resultCode);
            }
        }
        //
        // Rundenziel kann jetzt bearbeitet werden
        //
        else if (requestCode == 3005){
            final Intent i = new Intent(this, SelectRundenZiele.class);
            i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, mRundenGID);
            i.putExtra(Konstante.OUT_PARAM_SCHUETZEN_GID, mSchuetzenGID);
            startActivityForResult(i, 3004);
        }
    }
}
