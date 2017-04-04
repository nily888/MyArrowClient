package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testBerechnung {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testBerechnung(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testBerechnung(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testBerechnungen() {
        Log.d(TAG, "Berechnung testen");
        mSimulator.clickOnText("Berechnung");
        assertTrue("Bechnungsparameter werden nicht angezeigt", mSimulator.searchText("Parameter für die Berechnung der Punkte"));
        mSimulator.clickOnText("Startwert");
        assertTrue("16 wird nicht angezeigt", mSimulator.searchText("16"));
        mSimulator.clickOnText("OK");
        assertTrue("Bechnungsparameter werden nicht angezeigt", mSimulator.searchText("Parameter für die Berechnung der Punkte"));
        mSimulator.clickOnText("Abzug");
        assertTrue("6 wird nicht angezeigt", mSimulator.searchText("6"));
        mSimulator.clickOnText("OK");
        assertTrue("Bechnungsparameter werden nicht angezeigt", mSimulator.searchText("Parameter für die Berechnung der Punkte"));
        mSimulator.clickOnText("Bonus");
        assertTrue("4 wird nicht angezeigt", mSimulator.searchText("4"));
        mSimulator.clickOnText("OK");
        assertTrue("Bechnungsparameter werden nicht angezeigt", mSimulator.searchText("Parameter für die Berechnung der Punkte"));
        mSimulator.clickOnText("Bonus Plus");
        assertTrue("4 wird nicht angezeigt", mSimulator.searchText("4"));
        mSimulator.clickOnText("OK");
        assertTrue("Bechnungsparameter werden nicht angezeigt", mSimulator.searchText("Parameter für die Berechnung der Punkte"));
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Berechnung"));
        mSimulator.goBack();
        assertTrue("Startbildschirm wird nicht angezeigt", mSimulator.searchText("Los geht's"));
        Log.d(TAG, "Berechnung Prüfung abgeschlossen");
    }
}
