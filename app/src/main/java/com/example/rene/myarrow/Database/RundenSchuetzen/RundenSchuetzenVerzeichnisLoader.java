package com.example.rene.myarrow.Database.RundenSchuetzen;

import android.content.Context;
import android.database.Cursor;

import com.example.rene.myarrow.misc.AbstractCursorLoader;

/**
 * Loader speziell für die Liste von Geokontakten.
 */
public class RundenSchuetzenVerzeichnisLoader extends AbstractCursorLoader {

    /** Kuerzel fuers Logging. */
    private static final String TAG = RundenSchuetzenVerzeichnisLoader.class.getSimpleName();

    /** */
    private RundenSchuetzenSpeicher mDao;

    /** */
    private String mNamensFilter;

    public RundenSchuetzenVerzeichnisLoader(Context context) {
        super(context);
        mDao = new RundenSchuetzenSpeicher(context);
    }

    /**
     *
     * @param context Context der aufrufenden Android-Komponente (z.B. Activity).
     * @param dao
     * @param namensFilter optionaler Filter zur Beschraenkung der Ergebnismenge.
     */
    public RundenSchuetzenVerzeichnisLoader(Context context, RundenSchuetzenSpeicher dao, String namensFilter) {
        super(context);
        mDao = dao;
        mNamensFilter = namensFilter;
    }

    public RundenSchuetzenVerzeichnisLoader(Context context, RundenSchuetzenSpeicher dao) {
        this(context,dao,null);
    }

    @Override
    protected Cursor loadCursorData() {
        return mDao.loadRundenSchuetzenNamen(mNamensFilter);
    }
}
