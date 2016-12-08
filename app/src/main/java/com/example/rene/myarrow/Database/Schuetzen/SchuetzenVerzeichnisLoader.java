package com.example.rene.myarrow.Database.Schuetzen;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.rene.myarrow.misc.AbstractCursorLoader;


/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class SchuetzenVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = SchuetzenVerzeichnisLoader.class.getSimpleName();

    /** */
    private SchuetzenSpeicher mDao;

    /** */
    private CharSequence mNamensFilter;

    public SchuetzenVerzeichnisLoader(Context context) {
        super(context);
        mDao = new SchuetzenSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public SchuetzenVerzeichnisLoader(Context context, SchuetzenSpeicher dao, CharSequence namensFilter) {
        super(context);
        Log.d(TAG, "SchuetzenVerzeichnisLoader(): Start");
        mDao = dao;
        mNamensFilter = namensFilter;
        Log.d(TAG, "SchuetzenVerzeichnisLoader(): End");
    }

    public SchuetzenVerzeichnisLoader(Context context, SchuetzenSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        Log.d(TAG, "loadCursorData(): Start");
        Cursor cursor = mDao.loadSchuetzenListe();
        Log.d(TAG, "loadCursorData(): End");
        return cursor;
    }
}
