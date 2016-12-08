package com.example.rene.myarrow.GUI.Einstellungen;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import com.example.rene.myarrow.R;

/**
* Zeigt die Liste der verfügbaren Programmeinstellungen und
* ihre Werte an. Es besteht die Möglichkeit, die
* Einstellungen zu ändern und zu speichern.
*
* @author Rene Dueber
*/
public class EinstellungenBearbeiten extends PreferenceActivity {

    /** Kuerzel fuers Logging. */
    public static final String EINSTELLUNGEN_NAME =
            EinstellungenBearbeiten.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            onCreatePreferenceActivity();
        } else {
            onCreatePreferenceFragment();
        }
    }

    /**
     * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
     * < 11).
     */
    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
        addPreferencesFromResource(R.xml.myarrow_einstellungen);
    }

    /**
     * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
     * 11).
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onCreatePreferenceFragment() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment ())
                .commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.myarrow_einstellungen); //outer class
            // private members seem to be visible for inner class, and
            // making it static made things so much easier
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Log.w(EINSTELLUNGEN_NAME,
                        "unbekannte Option gewaehlt: " + item);
                return super.onOptionsItemSelected(item);
        }
    }
}