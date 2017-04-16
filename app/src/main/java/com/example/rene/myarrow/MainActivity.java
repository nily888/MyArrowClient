package com.example.rene.myarrow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.rene.myarrow.GUI.Einstellungen.Einstellungen;
import com.example.rene.myarrow.GUI.Parcour.SelectParcour;
import com.example.rene.myarrow.GUI.Parcour.StartParcour;
import com.example.rene.myarrow.GUI.Statistiken.ChartSelectGroup;
import com.example.rene.myarrow.GUI.Tools.Tools;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.MyDialog;

public class MainActivity extends AppCompatActivity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        //
        //
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        //
        //
        MyDialog dialog = ((MyDialog) this.getFragmentManager().findFragmentByTag("MyDialogTag"));
        if (dialog != null) {
            Dialog df = dialog.getDialog();
            if (df != null && df.isShowing()) {
                df.dismiss();
            }
        }
    }

    @Override
    public void onStart() {
        zeigeDetails();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tools) {
            Intent i = new Intent(this, Tools.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //
        // Rückgabewerte von Activities
        //
        switch (requestCode) {
            case 1712:
                //
                // Parcour wurde ausgewählt und jetzt wird der Parcour gestartet
                //
                if (resultCode == Activity.RESULT_OK) {
                    final Bundle mExtras = data.getExtras();
                    if (mExtras!=null && mExtras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
                        final Intent i = new Intent(this, StartParcour.class);
                        i.putExtra(Konstante.IN_PARAM_PARCOUR_GID, mExtras.getString(Konstante.IN_PARAM_PARCOUR_GID));
                        startActivity(i);
                    } else {
                        Log.e(TAG, "onActivityResult(): Es wurde kein Parcour übergeben");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }
                break;
        }
    }

    public void zeigeDetails() {
        Button startButton = (Button)findViewById(R.id.start_button);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.mipmap.start_button, null);
        d.setAlpha(Konstante.MY_TRANSPARENT30);
        startButton.setBackground(d);
        startButton.setTypeface(null, Typeface.BOLD);
        startButton.setTextColor(Color.BLACK);
    }

    public void onClickStartButton(View v){
        Intent myIntent = new Intent(this, SelectParcour.class);
        // myIntent.putExtra("key", value); //Optional parameters
        this.startActivityForResult(myIntent, 1712);
    }

    public void onClickStatistikenButton(View v){
        Intent myIntent = new Intent(this, ChartSelectGroup.class);
        // myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
    }

    public void onClickEinstellungenButton(View v){
        Intent myIntent = new Intent(this, Einstellungen.class);
        // myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
    }

}
