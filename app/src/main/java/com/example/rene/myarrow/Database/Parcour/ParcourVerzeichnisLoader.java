package com.example.rene.myarrow.Database.Parcour;

import android.content.Context;

import android.database.Cursor;

import android.util.Log;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class ParcourVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ParcourVerzeichnisLoader.class.getSimpleName();

    /** */
    private ParcourSpeicher mDao;

    /** */
    private CharSequence mNamensFilter;

    public ParcourVerzeichnisLoader(Context context) {
        super(context);
        mDao = new ParcourSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public ParcourVerzeichnisLoader(Context context, ParcourSpeicher dao, CharSequence namensFilter) {
        super(context);
        Log.d(TAG, "ParcourVerzeichnisLoader(): Start");
        mDao = dao;
        mNamensFilter = namensFilter;
        Log.d(TAG, "ParcourVerzeichnisLoader(): End");
    }

    public ParcourVerzeichnisLoader(Context context, ParcourSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        Log.d(TAG, "loadCursorData(): Start");
        Cursor cursor = mDao.loadParcourListe();
        Log.d(TAG, "loadCursorData(): End");
        return cursor;
    }
}
