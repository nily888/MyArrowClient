package com.example.rene.myarrow.GUI.Statistiken;

import android.content.Intent;
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
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChartEinParcourAlleSchuetzen  extends AppCompatActivity implements OnChartValueSelectedListener {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ChartEinParcourAlleSchuetzen.class.getSimpleName();

    private String[][] rz;
    private String mParcourGId;
    ArrayList<String> xAxis = new ArrayList<>();
    BarChart chart;
    String mFileChart;
    Boolean bFileChart = false;
    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_anzeigen);

        Log.d(TAG, "onCreate(): Übergabeparameter abholen");
        final Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGId = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Parcour-ID " + mParcourGId);
        } else {
            Log.w(TAG, "Keine Parcour-ID übergeben");
        }

        // Schuetzenname oben als App-Label anzeigen
        ParcourSpeicher tps = new ParcourSpeicher(this);
        mTitle = tps.getName(mParcourGId);
        setTitle(mTitle);

        //
        // Daten aus der Datenbank holen
        //
        Log.d(TAG, "onCreate(): Daten aus der Datenbak holen");
        /* */
        RundenSchuetzenSpeicher rzSpeicher = new RundenSchuetzenSpeicher(this);
        rz = rzSpeicher.getParcourSchuetzenMax(mParcourGId);
        if (rz.length < 1){
            finish();
            return;
        }

        /*
          pro Parcour den Durchschnittlichen Punktestand anzeigen
          BarChart initialisieren
         */
        Log.d(TAG, "onCreate(): BarChart aufbauen");
        chart = (BarChart) findViewById(R.id.chart);
        getXAxisValuesChart();
        BarData data = new BarData( xAxis, getDataSetChart());
        chart.setData(data);
        chart.setDescription("Alle Ziele");
        chart.animateXY(2000, 2000);
        chart.setClickable(true);
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();

        //
        // Ende
        //
        Log.d(TAG, "onCreate(): End");
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h){
        Log.d(TAG, "onValueSelected(): Start");
        Log.d(TAG, "onValueSelected(): Schuetze - " + xAxis.get(e.getXIndex()));
        Toast.makeText(this, xAxis.get(e.getXIndex()), Toast.LENGTH_SHORT).show();
        final Intent i = new Intent(this, ChartEinSchuetzeAlleRunden.class);
        i.putExtra(Konstante.IN_PARAM_PARCOUR_GID, mParcourGId);
        i.putExtra(Konstante.IN_PARAM_SCHUETZEN_GID, rz[e.getXIndex()][0]);
        startActivity(i);
        Log.d(TAG, "onValueSelected(): End");
    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<IBarDataSet> getDataSetChart() {
            Log.d(TAG, "getDataSet(): Start");

            ArrayList<IBarDataSet> dataSets;
            ArrayList<BarEntry> valueSet1 = new ArrayList<>();

            if (rz.length<1){
                return null;
            }

            for (int n=0; n<(rz.length); n++) {
                BarEntry v1e1 = new BarEntry(Integer.valueOf(rz[n][2]), n);
                valueSet1.add(v1e1);
            }

            BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Eine Runde");
            barDataSet1.setColor(Color.rgb(0, 155, 0));
            dataSets = new ArrayList<>();
            dataSets.add(barDataSet1);

            Log.d(TAG, "getDataSet(): End" + rz.length);
            return dataSets;
        }

    private void getXAxisValuesChart() {
        Log.d(TAG, "getXAxisValues(): Start");
        if (rz.length<1){
            return;
        }
        for (int n=0; n<(rz.length); n++) {
            xAxis.add(rz[n][1]);
        }
        Log.d(TAG, "getXAxisValues(): End = " + rz.length);
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
            ParcourSpeicher tss = new ParcourSpeicher(this);
            String mTitle = "MYA_" + tss.getName(mParcourGId);
            mTitle = mTitle.replace("/", "_");
            mTitle = mTitle.replace("-", "_");
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
