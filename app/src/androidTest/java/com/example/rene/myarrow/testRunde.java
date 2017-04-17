package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testRunde {
    private Solo mSimulator;
    private int sleepTime;
    private Boolean mRotate;
    private testMisc tm;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testRunde(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
        tm = new testMisc(mSimulator, sleepTime);
        mRotate = false;
    }

    public testRunde(Solo msimulator, int sleeptime, Boolean mrotate) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
        tm = new testMisc(mSimulator, sleepTime);
        mRotate = mrotate;
    }

    public void testRunde(int mTargets) {
        String[] mCheck;

        /*
          Den Parcour starten
         */
        new testParcour(mSimulator, sleepTime, mRotate).testStartParcour();

        /*
          Anzahl der Ziele ausgeben
         */
        Log.i(TAG, "testRunde(): Anzahl der Runden " + String.valueOf(mTargets));

        /*
         und jetzt geht es los.....
        */
        Log.i(TAG, "testRunde(): Click on Jetzt geht es los...");

        if (!mSimulator.searchText("Jetzt geht es los...")) {
            Log.e(TAG, "testRunde()Search: Text not found= Jetzt geht es los...");
            if (mSimulator.searchText("max. erreichte Punkte")) {
                mSimulator.goBack();
            }
        }

        if (!mSimulator.searchButton("Jetzt geht es los...")) {
            Log.e(TAG, "testRunde()Button: Text not found= Jetzt geht es los...");
            if (mSimulator.searchText("max. erreichte Punkte")) {
                mSimulator.goBack();
            }
        }

        assertTrue("Jetzt geht es los... wird nicht angezeigt", mSimulator.searchText("Jetzt geht es los..."));

        mSimulator.clickOnButton("Jetzt geht es los...");
        assertTrue("Erstes Ziel wird nicht angezeigt", mSimulator.searchText("1/" + String.valueOf(mTargets).trim()));
        assertTrue("Erstes Ziel => aktuelle Punkte / max.Punkte wird nicht angezeigt",
                mSimulator.searchText("0/" + String.valueOf(mTargets*20).trim()));
        tm.doRotate(mRotate);

        /*
          Jetzt die einzelnen Runden eintragen
         */
        for (int n=1; n<=mTargets;n++){
            /*
              Ergebnis eintargen
             */
            Log.d(TAG, n + "tes Ergebnis eintragen");
            // TODO n ist nicht gleich n
            mCheck = new String[]{"Default-Schuetze " + String.valueOf(n).trim(), "im 2ten Schuss", "+10 Punkte"};
            testErgebnisEintragen(mCheck);

            if (n == mTargets) {
                /*
                  Mit weiter jetzt zur Gesamtuebersicht
                 */
                Log.d(TAG, "Weiter geht es zur Gesamtuebersicht");
                mSimulator.clickOnText("weiter");
                assertTrue("Zusammenfassung der Runde wird nicht angezeigt", mSimulator.searchText("Parcourname"));
            } else {
                /*
                  und jetzt zum nächsten Ziel
                 */
                mCheck = new String[]{"10 - nächstes Ziel (1)",
                        String.valueOf(n+1).trim() + "/" + String.valueOf(mTargets).trim(),
                        String.valueOf(n*10).trim() + "/" + String.valueOf((mTargets*20)-(n*10)).trim()};
                tm.testClickOnWeiter(mCheck);

                // Zum vorherigen Ziel zurueck
                // und schon wieder bin ich beim ersten Ziel
                mCheck = new String[]{String.valueOf(n).trim() + "/" + String.valueOf(mTargets).trim(),
                        String.valueOf((n-1)*10).trim() + "/" + String.valueOf((mTargets*20)-((n-1)*10)).trim()};
                tm.testClickOnZurueck(mCheck);

                /*
                  und jetzt zum nächsten Ziel
                 */
                mCheck = new String[]{"10 - nächstes Ziel (2)",
                        String.valueOf(n+1).trim() + "/" + String.valueOf(mTargets).trim(),
                        String.valueOf(n * 10).trim() + "/" + String.valueOf((mTargets*20)-(n*10)).trim()};
                tm.testClickOnWeiter(mCheck);
            }
        }

        // Ergebnis anzeigen
        Log.d(TAG, "Ergebnis anzeigen");
        mSimulator.clickOnButton(String.valueOf(mTargets).trim());
        assertTrue("Ergebnis wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        mSimulator.clickOnText("OK");
        assertTrue("Zusammenfassung der Runde wird nicht angezeigt", mSimulator.searchText("Parcourname"));

        // und das war der Parcour
        Log.d(TAG, "Gesamtuebersicht wird angezeigt und jetzt geht es wieder zur Startseite");
        mSimulator.clickOnText("Ciao......");
        mSimulator.sleep(sleepTime);
        assertTrue("Startbildschirm wird nicht angezeigt", mSimulator.searchText("Los geht's"));

    }

    /**
     * Ziel ausgewaehlt und Ergebnis soll eingetragen werden
     * @param mCheck
     */
    private void testErgebnisEintragen(String[] mCheck) {
        assertEquals("Toggle Button " + mCheck[1] + " not found", true, mSimulator.searchToggleButton(mCheck[1]));
        mSimulator.clickOnToggleButton(mCheck[1]);
        assertEquals("Toggle Button is NOT " + mCheck[2], true, mSimulator.isToggleButtonChecked(mCheck[2]));
    }

}
