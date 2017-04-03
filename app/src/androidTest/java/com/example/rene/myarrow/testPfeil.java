package com.example.rene.myarrow;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/3/17.
 */

public class testPfeil {
    private Solo mSimulator;
    private int sleepTime;

    public testPfeil(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testPfeil(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testPfeile(){
        // Update
        String mPfeil = "Default-Pfeil";
        String mNeuerPfeil = "Default-Pfeil 3";
        mSimulator.clickOnText("Pfeile");
        assertTrue("Pfeile werden nicht angezeigt", mSimulator.searchText(mPfeil));
        mSimulator.clickOnText(mPfeil);
        mSimulator.sleep(sleepTime);
        assertTrue("Pfeile werden nicht angezeigt", mSimulator.searchText("Speichere Pfeil..."));
        mSimulator.clickOnText("Speichere Pfeil...");
        assertTrue("Pfeile werden nicht angezeigt", mSimulator.searchText(mPfeil));
        // Insert
        mSimulator.clickOnText("Neuer Pfeil");
        assertTrue("Neuer Pfeil werden nicht angezeigt", mSimulator.searchText("Speichere Pfeil..."));
        mSimulator.clearEditText(0);
        mSimulator.enterText(0, mNeuerPfeil);
        mSimulator.clickOnText("Speichere Pfeil...");
        assertTrue("Pfeile werden nicht angezeigt", mSimulator.searchText(mNeuerPfeil));
        // Delete
        mSimulator.clickLongOnText(mNeuerPfeil);
        assertTrue("Löschen wird nicht angezeigt", mSimulator.searchText("Löschen"));
        mSimulator.clickOnText("Löschen");
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Bögen"));
        mSimulator.goBack();
        assertTrue("Startseite wird nicht angezeigt", mSimulator.searchText("Einstellungen"));
    }

}
