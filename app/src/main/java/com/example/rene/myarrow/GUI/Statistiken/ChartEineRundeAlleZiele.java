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
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.BildAnzeigen;
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
import java.util.Locale;

public class ChartEineRundeAlleZiele extends AppCompatActivity implements OnChartValueSelectedListener {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ChartEineRundeAlleZiele.class.getSimpleName();

    /** */
    private RundenZielSpeicher rzSpeicher;
    private Cursor rz;
    private String mRundenGID;
    private String mSchuetzenGID;
    ArrayList<String> xAxis = new ArrayList<>();
    BarChart chart;
    private Boolean bFileChart = false;
    String mFileChart;
    String mSchuetzenName;
    String mParcourName;
    String mTitle;
    String mSubTile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_anzeigen);

        final Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey(Konstante.IN_PARAM_RUNDEN_GID)) {
            mRundenGID = extras.getString(Konstante.IN_PARAM_RUNDEN_GID);
        } else {
            Log.w(TAG, "Keine Runden-GID übergeben");
        }
        if (extras!=null && extras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
            mSchuetzenGID = extras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
        } else {
            Log.w(TAG, "Keine Schuetzen-GID übergeben");
        }

        // Schuetzenname oben als App-Label anzeigen
        ParcourSpeicher trs = new ParcourSpeicher(this);
        RundenSpeicher rs = new RundenSpeicher(this);
        mParcourName= trs.getName(rs.loadRunden(mRundenGID).parcourgid);
        SchuetzenSpeicher tss = new SchuetzenSpeicher(this);
        mSchuetzenName = tss.getSchuetzenNamen(mSchuetzenGID);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).
                format(rs.getEndzeit(mRundenGID));
        mSubTile = mParcourName + "-" + timeStamp;
        mTitle = mSchuetzenName + " - " + mSubTile;
        setTitle(mSchuetzenName);

        /* Daten aus der Datenbank holen */
        rzSpeicher = new RundenZielSpeicher(this);
        rz = rzSpeicher.getZielPunkte(mRundenGID, mSchuetzenGID);
        if ( rz == null || rz.getCount() < 1){
            finish();
            return;
        }

        /* pro Parcour den Durchschnittlichen Punktestand anzeigen
	        BarChart initialisieren
         */
        chart = (BarChart) findViewById(R.id.chart);
        getXAxisValuesChart();
        BarData data = new BarData(xAxis, getDataSetChart());
        chart.setData(data);
        chart.setDescription("Alle Ziele");
        chart.animateXY(2000, 2000);
        chart.setClickable(true);
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h){
        Toast.makeText(this, xAxis.get(e.getXIndex()), Toast.LENGTH_SHORT).show();
        BildAnzeigen bildAnzeigen = new BildAnzeigen(this, rzSpeicher.getDateiname(mRundenGID, Integer.valueOf(xAxis.get(e.getXIndex()))));
        bildAnzeigen.show();
    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<IBarDataSet> getDataSetChart() {
        ArrayList<IBarDataSet> dataSets;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        if (!rz.moveToFirst()){
            return null;
        }
        int n=0;
        do {
            BarEntry v1e1 = new BarEntry(rz.getInt(1), n);
            valueSet1.add(v1e1);
            n++;
        } while (rz.moveToNext());
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, mSubTile);
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private void getXAxisValuesChart() {
        if (!rz.moveToFirst()){
            return;
        }
        int n=0;
        do {
            xAxis.add(rz.getString(0));
            n++;
        } while (rz.moveToNext());
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
            mFileChart = "MYA_" + mTitle + ".jpg";
            chart.saveToGallery(mFileChart, "MyArrow", "MyArrow", Bitmap.CompressFormat.JPEG, 100);
            bFileChart = true;
            Toast.makeText(this, mFileChart + " gespeichert", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mFileChart + " wurde schon gespeichert", Toast.LENGTH_SHORT).show();
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
