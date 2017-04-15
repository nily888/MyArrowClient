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

import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;
import com.example.rene.myarrow.Database.Ziel.ZielVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;

/**
 * Created by nily on 28.12.2015.
 */
public class SelektiereZiel  extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Kuerzel fuers Logging.
     */
    private static final String TAG = SelektiereZiel.class.getSimpleName();

    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_ZIELE = new String[]{ ZielTbl.NUMMER, ZielTbl.NAME };

    /**
     * IDs im SimpleListView Layout.
     */
    private static final int[] SIMPLE_LIST_VIEW_IDS = new int[]{R.id.txt_zielnummer, R.id.txt_zielname};

    /**
     * Loader IDs.
     */
    private static final int LOADER_ID_ZIELE = 1;

    /** */
    private SimpleCursorAdapter mZielAdapter;

    /**
     * Schnittstelle zum persistenten Speicher.
     */
    private ZielSpeicher mZielSpeicher;

    private String mParcourGId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SelectParcour", "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcour_selektiere);

        // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Konstante.IN_PARAM_PARCOUR_GID)) {
            mParcourGId = extras.getString(Konstante.IN_PARAM_PARCOUR_GID);
            Log.d(TAG, "oncreate(): Aufruf mit Parcour-id " + mParcourGId);
        } else {
            Log.w(TAG, "onCreate(): Keine Parcour Id übergeben");
        }

        // Allgemeine Form:
        // registerForContextMenu(findViewById(android.R.id.list));

        mZielSpeicher = new ZielSpeicher(this);

        mZielAdapter = new SimpleCursorAdapter(this, R.layout.ziel_liste,
                null, ANZEIGE_ZIELE, SIMPLE_LIST_VIEW_IDS, 0);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mZielAdapter);

        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        /* */
        LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = this;

        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_ZIELE, null, mLoaderCallbacks);

        Log.d(TAG, "oncreate(): End");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy(): Start");
        getLoaderManager().destroyLoader(LOADER_ID_ZIELE);
        mZielSpeicher.schliessen();
        super.onDestroy();
        Log.d(TAG, "onDestroy(): End");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader(): Start - " + id);
        switch (id) {
            case LOADER_ID_ZIELE:
                return new ZielVerzeichnisLoader(this, mZielSpeicher, mParcourGId);
        }
        Log.d(TAG, "onCreateLoader(): End - " + id);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_ZIELE:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mZielAdapter.swapCursor(data);
                break;
        }
        Log.d(TAG, "onLoadFinished(): End - " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_ZIELE:
                mZielAdapter.swapCursor(null);
                break;
        }
        Log.d(TAG, "onLoaderReset(): End - " + loader.getId());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick(): Start");
        super.onListItemClick(l, v, position, id);
        final Intent i = new Intent(this, BearbeiteZiel.class);
        i.putExtra(Konstante.OUT_PARAM_ZIEL_GID, mZielSpeicher.getGID(id));
        Log.d(TAG, "onListItemClick(): Bearbeitung für das Ziel mit der id=" + id + " anzeigen");
        startActivityForResult(i, 1012);
        Log.d(TAG, "onListItemClick(): End");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): Start");
        if (requestCode == 1012) {
            getLoaderManager().getLoader(LOADER_ID_ZIELE).onContentChanged();
        }
        Log.d(TAG, "onActivityResult(): End");
    }
}
