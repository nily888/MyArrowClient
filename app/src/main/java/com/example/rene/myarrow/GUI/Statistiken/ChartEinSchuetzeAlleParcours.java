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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rened on 25.01.2016.
 */
public class ChartEinSchuetzeAlleParcours  extends AppCompatActivity implements OnChartValueSelectedListener {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ChartEinSchuetzeAlleParcours.class.getSimpleName();

    /** */
    private ParcourSpeicher pSpeicher;
    private String mSchuetzenGId;
    private RundenSchuetzenSpeicher rsSpeicher;
    private String[][] rs;
    ArrayList<String> xAxis = new ArrayList<>();
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
        if (extras != null &&
                extras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
            mSchuetzenGId = extras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
        } else {
            Log.w(TAG, "Keine Schuetzen-ID übergeben");
        }

        // Schuetzenname oben als App-Label anzeigen
        SchuetzenSpeicher tss = new SchuetzenSpeicher(this);
        mTitle = tss.getSchuetzenNamen(mSchuetzenGId);
        setTitle(mTitle);

        /** Daten aus der Datenbank holen */
        rsSpeicher = new RundenSchuetzenSpeicher(this);
        rs = rsSpeicher.getParcourAvg(mSchuetzenGId);
        if (rs == null || rs.length < 1){
            finish();
            return;
        }

        /**
         * pro Parcour den Durchschnittlichen Punktestand anzeigen
         * BarChart initialisieren
         */
        chart = (BarChart) findViewById(R.id.chart);
        getXAxisValuesChart();
        BarData data = new BarData(xAxis, getDataSetChart());
        chart.setData(data);
        chart.setDescription("Alle Parcours");
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
        Toast.makeText(this, xAxis.get(e.getXIndex()), Toast.LENGTH_SHORT).show();
        final Intent i = new Intent(this, ChartEinSchuetzeAlleRunden.class);
        i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, rs[e.getXIndex()][0]);
        i.putExtra(Konstante.OUT_PARAM_SCHUETZEN_GID, mSchuetzenGId);
        startActivity(i);
    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<IBarDataSet> getDataSetChart() {
        ArrayList<IBarDataSet> dataSets;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        if (rs.length<1){
            return null;
        }
        for (int n=0; n<(rs.length); n++){
            BarEntry v1e1 = new BarEntry(Float.valueOf(rs[n][2]), n);
            valueSet1.add(v1e1);
        }
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Alle Parcoure");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private void getXAxisValuesChart() {
        if (rs.length<1){
            return;
        }
        for (int n=0; n<(rs.length); n++) {
            xAxis.add(rs[n][1]);
        }
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
            SchuetzenSpeicher tss = new SchuetzenSpeicher(this);
            String mTitle = tss.getSchuetzenNamen(mSchuetzenGId);
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
        // Bilddatei erstellen und abspeichern
        //
        saveChart();
        //
        //Show share dialog BOTH image and text
        //
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
