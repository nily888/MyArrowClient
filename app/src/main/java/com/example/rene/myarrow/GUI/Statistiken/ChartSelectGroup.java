package com.example.rene.myarrow.GUI.Statistiken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rene.myarrow.R;


public class ChartSelectGroup extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ChartSelectGroup.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_select_group);
        Log.d(TAG, "oncreate(): End");
    }

    public void onClickAlleParcoure(View v) {
        Log.d(TAG, "onClickBogen(): Start");
        final Intent i = new Intent(this, ChartAlleParcours.class);
        startActivity(i);
        Log.d(TAG, "onClickBogen(): End");
    }

    public void onClickAlleSchuetzen(View v) {
        Log.d(TAG, "onClickPfeil(): Start");
        final Intent i = new Intent(this, ChartAlleSchuetzen.class);
        startActivity(i);
        Log.d(TAG, "onClickPfeil(): End");
    }
}
