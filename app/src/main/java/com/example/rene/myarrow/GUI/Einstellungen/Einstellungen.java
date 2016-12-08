package com.example.rene.myarrow.GUI.Einstellungen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rene.myarrow.GUI.Bogen.SelektiereBogen;
import com.example.rene.myarrow.GUI.Parcour.ReviewParcour;
import com.example.rene.myarrow.GUI.Schuetzen.SelektiereSchuetze;
import com.example.rene.myarrow.GUI.Ziel.SelektiereParcour;
import com.example.rene.myarrow.GUI.Pfeil.SelektierePfeil;
import com.example.rene.myarrow.R;

/**
 * 
 * @author René Düber
 *
 */
public class Einstellungen extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = Einstellungen.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
      Log.d(TAG, "oncreate(): Start");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.einstellungen);
      Log.d(TAG, "oncreate(): End");
    }

    public void onClickSchuetzen(View v) {
        Log.d(TAG, "onClickSchuetzen(): Start");
        final Intent i = new Intent(this, SelektiereSchuetze.class);
        startActivity(i);
        Log.d(TAG, "onClickSchuetzen(): End");
    }

    public void onClickBogen(View v) {
        Log.d(TAG, "onClickBogen(): Start");
        final Intent i = new Intent(this, SelektiereBogen.class);
        startActivity(i);
        Log.d(TAG, "onClickBogen(): End");
    }

    public void onClickPfeil(View v) {
        Log.d(TAG, "onClickPfeil(): Start");
        final Intent i = new Intent(this, SelektierePfeil.class);
        startActivity(i);
        Log.d(TAG, "onClickPfeil(): End");
    }

    public void onClickZiel(View v) {
        Log.d(TAG, "onClickZiel(): Start");
        final Intent i = new Intent(this, SelektiereParcour.class);
        startActivity(i);
        Log.d(TAG, "onClickZiel(): End");
    }

    public void onClickBerechungen(View v) {
        Log.d(TAG, "onClickBerechungen(): Start");
        final Intent i = new Intent(this, EinstellungenBearbeiten.class);
        startActivity(i);
        Log.d(TAG, "onClickBerechungen(): End");
    }

    public void onClickReviewParcour(View v) {
        final Intent i = new Intent(this, ReviewParcour.class);
        startActivity(i);
    }
}