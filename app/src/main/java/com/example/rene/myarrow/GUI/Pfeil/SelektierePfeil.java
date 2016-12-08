package com.example.rene.myarrow.GUI.Pfeil;

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

import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.Database.Pfeil.PfeilTbl;
import com.example.rene.myarrow.Database.Pfeil.PfeilVerzeichnisLoader;
import com.example.rene.myarrow.R;
import com.example.rene.myarrow.misc.Konstante;


/**
 * Created by nily on 19.12.15.
 */
public class SelektierePfeil extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Kuerzel fuers Logging.
     */
    private static final String TAG = SelektierePfeil.class.getSimpleName();

    /**
     * Parametername fuer Intent Bundle.
     */
    static final String IN_PARAM_PFEIL_ID = "PFEIL_ID";

    /**
     * Spalten des Cursors der Mobilfunknummern, die
     * in der Liste angezeigt werden.
     */
    private static final String[] ANZEIGE_PFEIL =
            new String[]{PfeilTbl.NAME};

    /**
     * IDs im SimpleListView Layout.
     */
    private static final int[] SIMPLE_LIST_VIEW_IDS =
            new int[]{R.id.txt_pfeilname};

    /**
     * Loader IDs.
     */
    private static final int LOADER_ID_PFEIL = 1;

    /** */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;

    /** */
    private SimpleCursorAdapter mPfeilAdapter;

    /**
     * Schnittstelle zum persistenten Speicher.
     */
    private PfeilSpeicher mPfeilSpeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate(): Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pfeil_selektiere);

        // Allgemeine Form:
        registerForContextMenu(findViewById(android.R.id.list));

        mPfeilSpeicher = new PfeilSpeicher(this);

        mPfeilAdapter = new SimpleCursorAdapter(this, R.layout.pfeil_liste,
                null, ANZEIGE_PFEIL, SIMPLE_LIST_VIEW_IDS, 0);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mPfeilAdapter);

        // Eine Überschrift hinzufügen
        /**

         LayoutInflater lf;
         View headerView;
         lf = this.getLayoutInflater();
         headerView = (View)lf.inflate(R.layout.header, null, false);

         listView.addHeaderView(headerView, null, false);

         */

        // Referenz auf das Objekt, das sich um die Callbacks nach einer
        // Datenanfrage kuemmert. Ist i.A. die Activity oder das aufrufende
        // Fragment.
        mLoaderCallbacks = this;

        // Registriert einen Loader mit ID LOADER_ID_MOBILLISTE beim LoaderManager.
        // Ab hier uebernimmt der Manager die Kontrolle ueber den Lebenszyklus des Loaders.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_PFEIL, null, mLoaderCallbacks);

        Log.d(TAG, "oncreate(): End");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Beginn of onDestroy");
        getLoaderManager().destroyLoader(LOADER_ID_PFEIL);
        mPfeilSpeicher.schliessen();
        super.onDestroy();
        Log.d(TAG, "End of onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         Bundle args) {
        Log.d(TAG, "Begin of onCreateLoader " + id);
        switch (id) {
            case LOADER_ID_PFEIL:
                return new PfeilVerzeichnisLoader(this, mPfeilSpeicher, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor data) {
        Log.d(TAG, "onLoadFinished(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_PFEIL:
                // Daten sind geladen. Der Cursor wird an den Adapter gebunden.
                mPfeilAdapter.swapCursor(data);
                break;
        }
        Log.d(TAG, "onLoadFinished(): End - " + loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset(): Start - " + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_PFEIL:
                mPfeilAdapter.swapCursor(null);
                break;
        }
        Log.d(TAG, "onLoaderReset(): End - " + loader.getId());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent i = new Intent(this, BearbeitePfeil.class);
        i.putExtra(Konstante.IN_PARAM_PFEIL_GID, mPfeilSpeicher.getGID(id));
        Log.d(TAG, "Bearbeitung für den Pfeil mit der id=" + id + " anzeigen");
        startActivityForResult(i, 1007);
    }

    /**
     * Es soll ein neuer Parcour angelegt werden.
     */
    public void onClickNeuerPfeil(View v) {
        final Intent i = new Intent(this, NeuerPfeil.class);
        startActivityForResult(i, 1007);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            getMenuInflater().inflate(R.menu.selektiere_pfeil_kontext, menu);
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
            case R.id.opt_pfeil_loeschen:
                Log.d(TAG, "onContextItemSelected(): Start-Löschen");
                mPfeilSpeicher.deletePfeil(mPfeilSpeicher.getGID(info.id));
                getLoaderManager().getLoader(LOADER_ID_PFEIL).onContentChanged();
                Log.d(TAG, "onContextItemSelected(): End-Löschen");
                return true;
            default:
                Log.w(TAG, "onContextItemSelected(): unbekannte Option gewaehlt: " + item);
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): Start");
        if (requestCode == 1007) {
            getLoaderManager().getLoader(LOADER_ID_PFEIL).onContentChanged();
        }
        Log.d(TAG, "onActivityResult(): End");
    }
}
