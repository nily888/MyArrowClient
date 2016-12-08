package com.example.rene.myarrow.Database.Runden;

import android.content.Context;
import android.database.Cursor;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class RundenVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = RundenVerzeichnisLoader.class.getSimpleName();

    /** */
    private RundenSpeicher mDao;

    /** */
    private String mNamensFilter;

    public RundenVerzeichnisLoader(Context context) {
        super(context);
        mDao = new RundenSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public RundenVerzeichnisLoader(Context context, RundenSpeicher dao, String namensFilter) {
        super(context);
        mDao = dao;
        mNamensFilter = namensFilter;
    }

    public RundenVerzeichnisLoader(Context context, RundenSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        return mDao.loadRundenListe(mNamensFilter);
    }
}
