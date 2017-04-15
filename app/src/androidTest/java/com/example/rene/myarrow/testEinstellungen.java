package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testEinstellungen {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testEinstellungen(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testEinstellungen(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testEinstellungen(String mJahr){

        Log.d(TAG, "testEinstellung(): started");

        /*
          Review Parcour testen
         */
        testGoToEinstellungen();
        new testReviewParcour(mSimulator, sleepTime, mJahr).testReviewParcour();

        /*
          Bögen testen
         */
        testGoToEinstellungen();
        new testBogen(mSimulator, sleepTime).testBoegen();

        /*
          Pfeile testen
         */
        testGoToEinstellungen();
        new testPfeil(mSimulator, sleepTime).testPfeile();

        /*
          Ziele testen
         */
        testGoToEinstellungen();
        new testZiel(mSimulator, sleepTime).testZiele();

        /*
          Berechnungen testen
         */
        testGoToEinstellungen();
        new testBerechnung(mSimulator, sleepTime).testBerechnungen();

        /*
          Schützen testen
         */
        testGoToEinstellungen();
        new testSchuetze(mSimulator, sleepTime).testSchuetzen();

        Log.d(TAG, "testEinstellung(): ended");

    }

    private void testGoToEinstellungen() {
        //
        // Zu den Einstellungen gehen
        //
        assertEquals("Button Einstellungen nicht gefunden", true, mSimulator.searchButton("Einstellungen"));
        mSimulator.clickOnText("Einstellungen");
        mSimulator.sleep(sleepTime);
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Bögen"));
    }

}
