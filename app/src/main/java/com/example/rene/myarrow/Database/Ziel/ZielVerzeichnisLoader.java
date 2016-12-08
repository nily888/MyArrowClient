package com.example.rene.myarrow.Database.Ziel;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class ZielVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = ZielVerzeichnisLoader.class.getSimpleName();

    /** */
    private ZielSpeicher mDao;
    private String mParcourGId;

    public ZielVerzeichnisLoader(Context context) {
        super(context);
        mDao = new ZielSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     */
    public ZielVerzeichnisLoader(Context context, ZielSpeicher dao, String parcourGId) {
        super(context);
        Log.d(TAG, "ZielVerzeichnisLoader(): Start");
        mDao = dao;
        mParcourGId = parcourGId;
        Log.d(TAG, "ZielVerzeichnisLoader(): End");
    }

    public ZielVerzeichnisLoader(Context context, ZielSpeicher dao) {
        this(context, dao, null);
        Log.d(TAG, "ZielVerzeichnisLoader(): Start/End");
    }

    @Override
    protected Cursor loadCursorData() {
        Log.d(TAG, "loadCursorData(): Start");
        Cursor cursor = mDao.loadZielListe(mParcourGId);
        Log.d(TAG, "loadCursorData(): End");
        return cursor;
    }
}
