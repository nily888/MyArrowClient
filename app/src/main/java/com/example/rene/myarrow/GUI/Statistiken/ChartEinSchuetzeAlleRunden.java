package com.example.rene.myarrow.GUI.Statistiken;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rened on 24.01.2016.
 */
public class ChartEinSchuetzeAlleRunden  extends AppCompatActivity implements OnChartValueSelectedListener {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ChartEinSchuetzeAlleRunden.class.getSimpleName();

    private Cursor rs;
    private String mParcourGId;
    private String mSchuetzenGId;
    ArrayList<String> xAxis = new ArrayList<>();
    ArrayList<String> xAxisLong = new ArrayList<>();
    BarChart chart;
    String mFileChart;
    Boolean bFileChart = false;
    String mSchuetzenName;
    String mParcourName;
    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_anzeigen);

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGId = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Parcour-ID " + mParcourGId);
        } else {
            Log.w(TAG, "Keine Parcour-ID übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
            mSchuetzenGId = extras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Schuetzen-ID " + mSchuetzenGId);
        } else {
            Log.w(TAG, "Keine Schuetzen-ID übergeben");
        }

        // Schuetzenname oben als App-Label anzeigen
        ParcourSpeicher trs = new ParcourSpeicher(this);
        mParcourName= trs.getName(mParcourGId);
        SchuetzenSpeicher tss = new SchuetzenSpeicher(this);
        mSchuetzenName = tss.getSchuetzenNamen(mSchuetzenGId);
        mTitle = mSchuetzenName + " - " + mParcourName;
        setTitle(mSchuetzenName);


        /* Daten aus der Datenbank holen */
        /* */
        RundenSchuetzenSpeicher rSpeicher = new RundenSchuetzenSpeicher(this);
        rs = rSpeicher.getRundenPunkte(mParcourGId, mSchuetzenGId);
        if (rs.getCount() < 1){
            rs.close();
            finish();
            return;
        }

        /*
          pro Parcour den Durchschnittlichen Punktestand anzeigen
          BarChart initialisieren
         */
        chart = (BarChart) findViewById(R.id.chart);
        getXAxisValuesChart();
        BarData data = new BarData(xAxis, getDataSetChart());
        chart.setData(data);
        chart.setDescription("Alle Runden");
        chart.animateXY(2000, 2000);
        chart.setClickable(true);
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();

        Log.d(TAG, "onCreate(): End");

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h){
        Toast.makeText(this, xAxis.get(e.getXIndex()), Toast.LENGTH_SHORT).show();
        final Intent i = new Intent(this, ChartEineRundeAlleZiele.class);
        RundenSpeicher rrs = new RundenSpeicher(this);
        i.putExtra(Konstante.OUT_PARAM_RUNDEN_GID, rrs.getGIDMitStartzeit(xAxisLong.get(e.getXIndex())));
        i.putExtra(Konstante.OUT_PARAM_SCHUETZEN_GID, mSchuetzenGId);
        startActivity(i);
    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<IBarDataSet> getDataSetChart() {
        ArrayList<IBarDataSet> dataSets;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        if (!rs.moveToFirst()){
            return null;
        }

        int n=0;
        do {
            BarEntry v1e1 = new BarEntry(rs.getFloat(1), n);
            valueSet1.add(v1e1);
            n++;
        } while (rs.moveToNext());

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, mParcourName);
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        Log.d(TAG, "getDataSet(): End - " + n);
        return dataSets;
    }

    private void getXAxisValuesChart() {
        Log.d(TAG, "getXAxisValues(): Start");

        if (!rs.moveToFirst()){
            return;
        }

        int n=0;
        do {
            if (rs.getLong(0) > 0) {
                xAxis.add(DateFormat.getDateTimeInstance(
                        DateFormat.SHORT, DateFormat.SHORT).
                        format(new Date(rs.getLong(0))));
            } else {
                xAxis.add("unbekannt");
            }
            xAxisLong.add(rs.getString(0));
            n++;
        } while (rs.moveToNext());

        Log.d(TAG, "getXAxisValues(): End - " + n);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saveToPath) {
            saveChart();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shareToWhatsapp) {
            shareToWhatsapp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChart(){
        if (!bFileChart) {
            mTitle = "MYA_" + mTitle;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).
                    format(new Date());
            mFileChart = "MYA_" + mTitle + "_" + timeStamp + ".jpg";
            chart.saveToGallery(mFileChart, "MyArrow", "MyArrow", Bitmap.CompressFormat.JPEG, 100);
            bFileChart = true;
        }
    }

    private void shareToWhatsapp(){
        //
        //
        //
        saveChart();
        /*
          Show share dialog BOTH image and text
         */
        File imageFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        imageFile = new File(imageFile, mFileChart);
        Uri imageUri = Uri.parse(imageFile.getAbsolutePath());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_TEXT, mFileChart);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp ist nicht installiert.", Toast.LENGTH_SHORT).show();
        }
    }
}
