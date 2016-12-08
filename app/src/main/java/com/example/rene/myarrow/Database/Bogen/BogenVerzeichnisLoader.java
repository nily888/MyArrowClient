package com.example.rene.myarrow.Database.Bogen;

import android.content.Context;

import android.database.Cursor;

import android.util.Log;

import com.example.rene.myarrow.misc.AbstractCursorLoader;


/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class BogenVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = BogenVerzeichnisLoader.class.getSimpleName();

    /** */
    private BogenSpeicher mDao;

    /** */
    private CharSequence mNamensFilter;

    public BogenVerzeichnisLoader(Context context) {
        super(context);
        mDao = new BogenSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public BogenVerzeichnisLoader(Context context, BogenSpeicher dao, CharSequence namensFilter) {
        super(context);
        Log.d(TAG, "BogenVerzeichnisLoader(): Start");
        mDao = dao;
        mNamensFilter = namensFilter;
        Log.d(TAG, "BogenVerzeichnisLoader(): End");
    }

    public BogenVerzeichnisLoader(Context context, BogenSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        Log.d(TAG, "loadCursorData(): Start");
        Cursor cursor = mDao.loadBogenListe();
        Log.d(TAG, "loadCursorData(): End");
        return cursor;
    }
}
