package com.example.rene.myarrow.GUI.Ziel;

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

import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Parcour.ParcourTbl;
import com.example.rene.myarrow.Database.Parcour.ParcourVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;

/**
 * Created by nily on 19.12.15.
 */
public class SelektiereParcour extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Kuerzel fuers Logging.
     */
    private static final String TAG = SelektiereParcour.class.getSimpleName();

    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_PARCOUR =
            new String[]{
                    ParcourTbl.NAME,
                    ParcourTbl.ANZAHL_ZIELE};

    /**
     * IDs im SimpleListView Layout.
     */
    private static final int[] SIMPLE_LIST_VIEW_IDS =
            new int[]{R.id.txt_parcourname,
                    R.id.txt_anzahl_ziele};

    /**
     * Loader IDs.
     */
    private static final int LOADER_ID_PARCOUR = 1;

    /** */
    private SimpleCursorAdapter mParcourAdapter;

    /**
     * Schnittstelle zum persistenten Speicher.
     */
    private ParcourSpeicher mParcourSpeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SelectParcour", "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcour_selektiere);

        // Allgemeine Form:
        // registerForContextMenu(findViewById(android.R.id.list));

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
        Log.d(TAG, "Begin of onCreateLoader " + id);
        switch (id) {
            case LOADER_ID_PARCOUR:
                return new ParcourVerzeichnisLoader(this, mParcourSpeicher, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_PARCOUR:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mParcourAdapter.swapCursor(data);
                break;
        }
        Log.d(TAG, "onLoadFinished(): End - " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_PARCOUR:
                mParcourAdapter.swapCursor(null);
                break;
        }
        Log.d(TAG, "onLoaderReset(): End - " + loader.getId());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick(): Start");
        super.onListItemClick(l, v, position, id);
        final Intent i = new Intent(this, SelektiereZiel.class);
        i.putExtra(Konstante.IN_PARAM_PARCOUR_GID, mParcourSpeicher.getGID(id));
        Log.d(TAG, "onListItemClick(): Bearbeitung für den Parcour mit der id=" + id + " anzeigen");
        startActivity(i);
        Log.d(TAG, "onListItemClick(): End");
    }

}
