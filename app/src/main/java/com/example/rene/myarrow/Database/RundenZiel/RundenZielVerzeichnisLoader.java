package com.example.rene.myarrow.Database.RundenZiel;

import android.content.Context;
import android.database.Cursor;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class RundenZielVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = RundenZielVerzeichnisLoader.class.getSimpleName();

    /** */
    private RundenZielSpeicher mDao;

    /** */
    private String[] mIdFilter;

    public RundenZielVerzeichnisLoader(Context context) {
        super(context);
        mDao = new RundenZielSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param idFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public RundenZielVerzeichnisLoader(Context context, RundenZielSpeicher dao, String[] idFilter) {
        super(context);
        mDao = dao;
        mIdFilter = idFilter;
    }

    public RundenZielVerzeichnisLoader(Context context, RundenZielSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        return mDao.loadRundenZielListe(mIdFilter);
    }
}
