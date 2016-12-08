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

import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenTbl;
import com.example.rene.myarrow.Database.Runden.RundenVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;


/**
 * Created by nily on 14.12.15.
 */
public class SelectRunde extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Kuerzel fuers Logging.
    private static final String TAG = SelectRunde.class.getSimpleName();

    //
    //Spalten des Cursors der Mobilfunknummern, die
    //in der Liste angezeigt werden.
    //
    private static final String[] ANZEIGE_RUNDEN = new String[] {RundenTbl.S_STARTZEIT};

    // IDs im SimpleListView Layout.
    private static final int[] SIMPLE_LIST_VIEW_IDS = new int[] {R.id.txt_startzeit};

    // Loader IDs.
    private static final int LOADER_ID_RUNDEN = 3;

    //
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;

    //
    private SimpleCursorAdapter mRundenAdapter;

    // Schnittstelle zum persistenten Speicher.
    private RundenSpeicher mRundenSpeicher;
    private String mParcourGID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runden_select);

        //
        //
        //
        getParameter();

        //
        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        mRundenSpeicher = new RundenSpeicher(this);
        mRundenAdapter = new SimpleCursorAdapter(this, R.layout.runden_liste,
                null, ANZEIGE_RUNDEN, SIMPLE_LIST_VIEW_IDS, 0);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mRundenAdapter);
        //
        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        mLoaderCallbacks = this;
        //
        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_RUNDEN, null, mLoaderCallbacks);
    }

    @Override
    protected void onDestroy() {
        getLoaderManager().destroyLoader(LOADER_ID_RUNDEN);
        mRundenSpeicher.schliessen();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch( id ) {
            case LOADER_ID_RUNDEN:
                return new RundenVerzeichnisLoader(this, mRundenSpeicher, mParcourGID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch( loader.getId() ) {
            case LOADER_ID_RUNDEN:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mRundenAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch( loader.getId() ) {
            case LOADER_ID_RUNDEN:
                mRundenAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent intent = new Intent();
        intent.putExtra(Konstante.IN_PARAM_RUNDEN_GID, mRundenSpeicher.getGID(id));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            getMenuInflater().inflate(R.menu.select_runden_kontext, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.
                        getMenuInfo();
        if (info.id == AdapterView.INVALID_ROW_ID) {
            Log.d(TAG, "Kein Listeneintrag ausgewählt!");
            return false;
        }
        //
        //
        //
        switch (item.getItemId()) {
            case R.id.opt_kompletteunde_runden_löschen:
                Log.w(TAG, "onContextItemSelected(): Alles zu dieser Runde löschen = " + info.id);
                mRundenSpeicher.deleteKompletteRunden(mRundenSpeicher.getGID(info.id));
                getLoaderManager().getLoader(LOADER_ID_RUNDEN).onContentChanged();
            default:
                Log.w(TAG, "unbekannte Option gewaehlt: " + item);
                return super.onContextItemSelected(item);
        }
    }

    protected void getParameter() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGID = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
        } else {
            mParcourGID = "";
            Log.e(TAG, "Keine Parcour-Id übergeben");
        }
    }
}