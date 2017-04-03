package com.example.rene.myarrow;

import android.util.Log;

import com.example.rene.myarrow.GUI.Ziel.BearbeiteZiel;
import com.robotium.solo.Solo;

import static android.content.ContentValues.TAG;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/3/17.
 */

public class testZiel {
    private Solo mSimulator;
    private int sleepTime;

    public testZiel(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testZiel(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testZiele(){
        Log.d(TAG, "Ziele testen");
        mSimulator.clickOnText("Ziele");
        assertTrue("Parcoure werden nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        mSimulator.clickOnText("Default-Parcour");
        assertTrue("Ziele werden nicht angezeigt", mSimulator.searchText("Default Ziel 1"));
        mSimulator.clickOnText("Default Ziel 1");
        mSimulator.assertCurrentActivity("Wartet auf die Class BearbeiteZiel", BearbeiteZiel.class);
        // mSimulator.clickOnText("Zielbild");
        // mSimulator.assertCurrentActivity("Wartet auf die Class BildAnzeigen", BildAnzeigen.class);
        // mSimulator.goBack();
        // mSimulator.assertCurrentActivity("Wartet auf die Class BearbeiteZiel", BearbeiteZiel.class);
        assertTrue("Pfeil wird nicht angezeigt", mSimulator.searchText("Update Ziel..."));
        mSimulator.clickOnText("Update Ziel...");
        assertTrue("GoBack: Ziele werden nicht angezeigt", mSimulator.searchText("Default Ziel 1"));
        mSimulator.goBack();
        mSimulator.sleep(sleepTime);
        assertTrue("Parcoure werden nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Berechnung"));
        mSimulator.goBack();
        assertTrue("Startseite wird nicht angezeigt", mSimulator.searchText("Einstellungen"));
        Log.d(TAG, "Ziel Prüfung abgeschlossen");
    }

}
