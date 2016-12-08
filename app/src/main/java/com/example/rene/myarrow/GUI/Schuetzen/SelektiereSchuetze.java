package com.example.rene.myarrow.GUI.Schuetzen;

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

import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenTbl;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;


/**
 * Created by nily on 19.12.15.
 */
public class SelektiereSchuetze extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Kuerzel fuers Logging.
     */
    private static final String TAG = SelektiereSchuetze.class.getSimpleName();

    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_SCHUETZEN =
            new String[]{SchuetzenTbl.NAME};

    /**
     * IDs im SimpleListView Layout.
     */
    private static final int[] SIMPLE_LIST_VIEW_IDS =
            new int[]{R.id.txt_schuetzenname};

    /**
     * Loader IDs.
     */
    private static final int LOADER_ID_SCHUETZEN = 10;

    /** */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;

    /** */
    private SimpleCursorAdapter mSchuetzenAdapter;

    /**
     * Schnittstelle zum persistenten Speicher.
     */
    private SchuetzenSpeicher mSchuetzenSpeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schuetzen_selektiere);

        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        mSchuetzenSpeicher = new SchuetzenSpeicher(this);

        mSchuetzenAdapter = new SimpleCursorAdapter(this, R.layout.schuetzen_liste,
                null, ANZEIGE_SCHUETZEN, SIMPLE_LIST_VIEW_IDS, 0);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mSchuetzenAdapter);

        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        mLoaderCallbacks = this;

        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_SCHUETZEN, null, mLoaderCallbacks);

        Log.d(TAG, "oncreate(): End");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Beginn of onDestroy");
        getLoaderManager().destroyLoader(LOADER_ID_SCHUETZEN);
        mSchuetzenSpeicher.schliessen();
        super.onDestroy();
        Log.d(TAG, "End of onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         Bundle args) {
        Log.d(TAG, "Begin of onCreateLoader " + id);
        switch (id) {
            case LOADER_ID_SCHUETZEN:
                return new SchuetzenVerzeichnisLoader(this, mSchuetzenSpeicher, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor data) {
        Log.d(TAG, "onLoadFinished(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_SCHUETZEN:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mSchuetzenAdapter.swapCursor(data);
                break;
        }
        Log.d(TAG, "onLoadFinished(): End - " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_SCHUETZEN:
                mSchuetzenAdapter.swapCursor(null);
                break;
        }
        Log.d(TAG, "onLoaderReset(): End - " + loader.getId());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent i = new Intent(this, BearbeiteSchuetze.class);
        i.putExtra(Konstante.IN_PARAM_SCHUETZEN_GID, mSchuetzenSpeicher.getGID(id));
        startActivityForResult(i, 1107);
    }

    /**
     * Es soll ein neuer Parcour angelegt werden.
     */
    public void onClickNeuerSchuetzen(View v) {
        final Intent i = new Intent(this, NeuerSchuetze.class);
        startActivityForResult(i, 1107);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            getMenuInflater().inflate(R.menu.selektiere_schuetzen_kontext, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected(): Start");
        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info.id == AdapterView.INVALID_ROW_ID) {
            Log.d(TAG, "onContextItemSelected(): Kein Listeneintrag ausgewählt!");
            return false;
        }
        Log.d(TAG, "onContextItemSelected(): Listeneintrag gewählt: " + item + " " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.opt_schuetzen_loeschen:
                Log.d(TAG, "onContextItemSelected(): Start-Löschen");
                mSchuetzenSpeicher.deleteSchuetzen(info.id);
                getLoaderManager().getLoader(LOADER_ID_SCHUETZEN).onContentChanged();
                Log.d(TAG, "onContextItemSelected(): End-Löschen");
                return true;
            default:
                Log.w(TAG, "onContextItemSelected(): unbekannte Option gewaehlt: " + item);
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1107) {
            getLoaderManager().getLoader(LOADER_ID_SCHUETZEN).onContentChanged();
        }
    }
}
