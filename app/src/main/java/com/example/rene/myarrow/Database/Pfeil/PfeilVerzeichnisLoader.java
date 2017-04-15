package com.example.rene.myarrow.Database.Pfeil;

import android.content.Context;

import android.database.Cursor;

import android.util.Log;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class PfeilVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = PfeilVerzeichnisLoader.class.getSimpleName();

    /** */
    private PfeilSpeicher mDao;

    public PfeilVerzeichnisLoader(Context context) {
        super(context);
        mDao = new PfeilSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public PfeilVerzeichnisLoader(Context context, PfeilSpeicher dao, CharSequence namensFilter) {
        super(context);
        Log.d(TAG, "PfeilVerzeichnisLoader(): Start");
        mDao = dao;
        /* */
        CharSequence mNamensFilter = namensFilter;
        Log.d(TAG, "PfeilVerzeichnisLoader(): End");
    }

    public PfeilVerzeichnisLoader(Context context, PfeilSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        Log.d(TAG, "loadCursorData(): Start");
        Cursor cursor = mDao.loadPfeilListe();
        Log.d(TAG, "loadCursorData(): End");
        return cursor;
    }
}
