package com.example.rene.myarrow.GUI.Bogen;

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

import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.Database.Bogen.BogenTbl;
import com.example.rene.myarrow.Database.Bogen.BogenVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;

/**
 * Created by nily on 19.12.15.
 */
public class SelektiereBogen extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Kuerzel fuers Logging.
     */
    private static final String TAG = SelektiereBogen.class.getSimpleName();
    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_BOGEN = new String[]{BogenTbl.NAME};
    /**
     * IDs im SimpleListView Layout.
     */
    private static final int[] SIMPLE_LIST_VIEW_IDS = new int[]{R.id.txt_bogenname};
    /**
     * Loader IDs.
     */
    private static final int LOADER_ID_BOGEN = 1;
    /**
     *
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;
    /**
     *
     */
    private SimpleCursorAdapter mBogenAdapter;
    /**
     * Schnittstelle zum persistenten Speicher.
     */
    private BogenSpeicher mBogenSpeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bogen_selektiere);

        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        mBogenSpeicher = new BogenSpeicher(this);

        mBogenAdapter = new SimpleCursorAdapter(this, R.layout.bogen_liste,
                null, ANZEIGE_BOGEN, SIMPLE_LIST_VIEW_IDS, 0);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mBogenAdapter);

        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        mLoaderCallbacks = this;

        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_BOGEN, null, mLoaderCallbacks);
    }

    @Override
    protected void onDestroy() {
        getLoaderManager().destroyLoader(LOADER_ID_BOGEN);
        mBogenSpeicher.schliessen();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         Bundle args) {
        switch (id) {
            case LOADER_ID_BOGEN:
                return new BogenVerzeichnisLoader(this, mBogenSpeicher, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID_BOGEN:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mBogenAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID_BOGEN:
                mBogenAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent i = new Intent(this, BearbeiteBogen.class);
        i.putExtra(Konstante.IN_PARAM_BOGEN_GID, mBogenSpeicher.getGID(id));
        startActivityForResult(i, 1006);
    }

    /**
     * Es soll ein neuer Parcour angelegt werden.
     */
    public void onClickNeuerBogen(View v) {
        final Intent i = new Intent(this, NeuerBogen.class);
        startActivityForResult(i, 1006);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            getMenuInflater().inflate(R.menu.selektiere_bogen_kontext, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected(): Start");
        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info.id == AdapterView.INVALID_ROW_ID) {
            Log.d(TAG, "Kein Listeneintrag ausgewählt!");
            return false;
        }
        switch (item.getItemId()) {
            case R.id.opt_bogen_loeschen:
                Log.d(TAG, "onContextItemSelected(): Löschen");
                mBogenSpeicher.deleteBogen(mBogenSpeicher.getGID(info.id));
                getLoaderManager().getLoader(LOADER_ID_BOGEN).onContentChanged();
                Log.d(TAG, "End of onContextItemSelected: Löschen");
                return true;
            default:
                Log.w(TAG, "unbekannte Option gewaehlt: " + item);
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1006) {
            getLoaderManager().getLoader(LOADER_ID_BOGEN).onContentChanged();
        }
    }
}