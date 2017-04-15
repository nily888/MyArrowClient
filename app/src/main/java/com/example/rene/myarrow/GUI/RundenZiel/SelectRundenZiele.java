package com.example.rene.myarrow.GUI.RundenZiel;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.rene.myarrow.Database.RundenZiel.RundenZielVerzeichnisLoader;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielTbl;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;


/**
 * Created by nily on 14.12.15.
 */
public class SelectRundenZiele extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Kuerzel fuers Logging. */
    private static final String TAG = SelectRundenZiele.class.getSimpleName();

    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_RUNDENZIEL =
            new String[] {RundenZielTbl.NUMMER, ZielTbl.NAME, RundenZielTbl.PUNKTE};

    /** IDs im SimpleListView Layout. */
    private static final int[] SIMPLE_LIST_VIEW_IDS = new int[] { R.id.txt_nummer, R.id.txt_name, R.id.txt_punkte};

    /** Loader IDs. */
    private static final int LOADER_ID_RUNDENZIEL = 4;

    /** */
    private SimpleCursorAdapter mRundenZielAdapter;

    /** Schnittstelle zum persistenten Speicher. */
    private RundenZielSpeicher mRundenZielSpeicher;
    private String mRundenGID;
    private String mSchuetzenGID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rundenziel_select);

        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        //
        //
        //
        getParameter();

        //
        //
        //
        mRundenZielSpeicher = new RundenZielSpeicher(this);
        mRundenZielAdapter = new SimpleCursorAdapter(this, R.layout.rundenziel_liste,
                null, ANZEIGE_RUNDENZIEL, SIMPLE_LIST_VIEW_IDS, 0);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mRundenZielAdapter);

        //
        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        /* */
        LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = this;

        //
        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_RUNDENZIEL, null, mLoaderCallbacks);
    }

    @Override
    protected void onDestroy() {
        getLoaderManager().destroyLoader(LOADER_ID_RUNDENZIEL);
        mRundenZielSpeicher.schliessen();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch( id ) {
            case LOADER_ID_RUNDENZIEL:
                String[] idFilter = new String[]{mRundenGID, mSchuetzenGID};
                return new RundenZielVerzeichnisLoader(this, mRundenZielSpeicher, idFilter);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch( loader.getId() ) {
            case LOADER_ID_RUNDENZIEL:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mRundenZielAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch( loader.getId() ) {
            case LOADER_ID_RUNDENZIEL:
                mRundenZielAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent intent = new Intent();
        intent.putExtra(Konstante.OUT_PARAM_RUNDENZIEL_GID, mRundenZielSpeicher.getGID(id));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    protected void getParameter() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_RUNDEN_GID)) {
            mRundenGID = extras.getString(Konstante.IN_PARAM_RUNDEN_GID);
        } else {
            mRundenGID = null;
            Log.w(TAG, "Keine Runden-GId übergeben");
        }
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_SCHUETZEN_GID)) {
            mSchuetzenGID = extras.getString(Konstante.IN_PARAM_SCHUETZEN_GID);
        } else {
            mSchuetzenGID = null;
            Log.w(TAG, "Keine RundenScheutzen-GId übergeben");
        }
    }
}