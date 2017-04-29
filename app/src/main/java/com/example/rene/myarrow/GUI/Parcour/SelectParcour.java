package com.example.rene.myarrow.GUI.Parcour;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Parcour.ParcourTbl;
import com.example.rene.myarrow.Database.Parcour.ParcourVerzeichnisLoader;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;
import com.example.rene.myarrow.GUI.Statistiken.ChartEinParcourAlleSchuetzen;
import com.example.rene.myarrow.GUI.Ziel.AddZiel;
import com.example.rene.myarrow.misc.Konstante;
import com.example.rene.myarrow.misc.ShowMap;
import com.example.rene.myarrow.R;


/**
 * Created by nily on 14.12.15.
 */
public class SelectParcour extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Kuerzel fuers Logging. */
    private static final String TAG = SelectParcour.class.getSimpleName();

    /**
     * Spalten des Cursors der Parcoure, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_PARCOUR =
            new String[] {
                    ParcourTbl.NAME,
                    ParcourTbl.ANZAHL_ZIELE};

    /** IDs im SimpleListView Layout. */
    private static final int[] SIMPLE_LIST_VIEW_IDS =
            new int[] { R.id.txt_parcourname,
                        R.id.txt_anzahl_ziele};

    /** Loader IDs. */
    private static final int LOADER_ID_PARCOUR = 1;

    /** */
    private SimpleCursorAdapter mParcourAdapter;

    /** Schnittstelle zum persistenten Speicher. */
    private ParcourSpeicher mParcourSpeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SelectParcour", "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcour_select);

        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        mParcourSpeicher = new ParcourSpeicher(this);
        mParcourAdapter = new SimpleCursorAdapter(this, R.layout.parcour_liste,
                null, ANZEIGE_PARCOUR, SIMPLE_LIST_VIEW_IDS, 0);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mParcourAdapter);

        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        /* */
        LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = this;

        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_PARCOUR, null, mLoaderCallbacks);

        Log.d(TAG, "oncreate(): End");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Beginn of onDestroy");
        getLoaderManager().destroyLoader(LOADER_ID_PARCOUR);
        mParcourSpeicher.schliessen();
        super.onDestroy();
        Log.d(TAG, "End of onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"Begin of onCreateLoader "+id);
        switch( id ) {
            case LOADER_ID_PARCOUR:
                return new ParcourVerzeichnisLoader(this, mParcourSpeicher, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Beginn of onLoadFinished " + loader.getId());
        switch( loader.getId() ) {
            case LOADER_ID_PARCOUR:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mParcourAdapter.swapCursor(data);
                break;
        }
        Log.d(TAG,"End of onLoadFinished "+loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Beginn of onLoaderReset " + loader.getId());
        switch( loader.getId() ) {
            case LOADER_ID_PARCOUR:
                mParcourAdapter.swapCursor(null);
                break;
        }
        Log.d(TAG,"End of onLoaderReset "+loader.getId());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent intent = new Intent();
        String mGID = new ParcourSpeicher(this).getGID(id);
        intent.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mGID);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            getMenuInflater().inflate(R.menu.select_parcour_kontext, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected(): Start");
        final Intent i;

        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.
                        getMenuInfo();
        if (info.id == AdapterView.INVALID_ROW_ID) {
            Log.d(TAG, "Kein Listeneintrag ausgewählt!");
            return false;
        }
        Log.d(TAG, "Listeneintragngewählt: " + item + " " + item.getItemId());
        switch (item.getItemId()) {

            case R.id.opt_parcour_add_ziel:
                Log.d(TAG, "onContextItemSelected(): Parcour Add Ziel Start");
                i = new Intent(this, AddZiel.class);
                Log.d(TAG, "onContextItemSelected(): Parcour Add Ziel - Parcour-Id - " + info.id);
                i.putExtra(Konstante.OUT_PARAM_PARCOUR_GID, mParcourSpeicher.getGID(info.id));
                i.putExtra(Konstante.OUT_PARAM_AKTUELLES_ZIEL_ID, -1 );
                startActivity(i);
                getLoaderManager().getLoader(LOADER_ID_PARCOUR).onContentChanged();
                Log.d(TAG, "onContextItemSelected: Parcour Add Ziel End");
                return true;

            case R.id.opt_parcour_bearbeiten:
                Log.d(TAG, "onContextItemSelected(): Parcour bearbeiten Start");
                i = new Intent(this, BearbeiteParcour.class);
                Log.d(TAG, "onContextItemSelected(): Parcour bearbeiten - Parcour-Id - " + info.id);
                i.putExtra(Konstante.IN_PARAM_PARCOUR_GID, mParcourSpeicher.getGID(info.id));
                startActivity(i);
                Log.d(TAG, "onContextItemSelected: Parcour bearbeiten End");
                return true;

            case R.id.opt_parcour_loeschen:
                Log.d(TAG, "onContextItemSelected(): Parcour löschen Start");
                mParcourSpeicher.deleteParcour(mParcourSpeicher.getGID(info.id));
                getLoaderManager().getLoader(LOADER_ID_PARCOUR).onContentChanged();
                Log.d(TAG, "onContextItemSelected(): Parcour löschen End");
                return true;

            case R.id.opt_statistik:
                Log.d(TAG, "onContextItemSelected(): Statistik anzeigen Start");
                i = new Intent(this, ChartEinParcourAlleSchuetzen.class);
                Log.d(TAG, "onContextItemSelected(): Statistik anzeigen - Parcour-Id - " + info.id);
                i.putExtra(Konstante.IN_PARAM_PARCOUR_GID, mParcourSpeicher.getGID(info.id));
                startActivity(i);
                Log.d(TAG, "onContextItemSelected: Statistik anzeigen End");
                return true;

            case R.id.opt_show_on_map:
                // TODO: nach ShowMap schieben?
                Bundle mBundle = new Bundle();
                ZielSpeicher zs = new ZielSpeicher(this);
                Cursor c = zs.loadZielListe(mParcourSpeicher.getGID(info.id));
                if (c != null || c.getCount()<1) {
                    String[][] zielListe = new String[c.getCount()][3];
                    int n = 0;
                    c.moveToFirst();
                    do {
                        zielListe[n][0] = c.getString(c.getColumnIndex(ZielTbl.NAME));
                        zielListe[n][1] = c.getString(c.getColumnIndex(ZielTbl.GPS_LAT_KOORDINATEN));
                        zielListe[n][2] = c.getString(c.getColumnIndex(ZielTbl.GPS_LON_KOORDINATEN));
                        n++;
                    } while (c.moveToNext());
                    i = new Intent(this, ShowMap.class);
                    mBundle.putSerializable(Konstante.OUT_PARAM_GPS_ZIELE, zielListe);
                    i.putExtras(mBundle);
                    startActivity(i);
                }
                return true;
            default:
                Log.w(TAG, "unbekannte Option gewaehlt: " + item);
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Es soll ein neuer Parcour angelegt werden.
     *
     */
    public void onClickNeuerParcour(View v) {
        final Intent i = new Intent(this, NeuerParcour.class);
        finish();
        startActivity(i);
	}

}