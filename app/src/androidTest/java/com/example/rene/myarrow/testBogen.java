package com.example.rene.myarrow;

/**
 * Created by rene on 4/3/17.
 */

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

public class testBogen {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testBogen(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testBogen(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testBoegen() {
        // Update
        mSimulator.clickOnText("Bögen");
        assertTrue("Bogenliste wird nicht angezeigt", mSimulator.searchText("Default-Bogen"));
        mSimulator.clickOnText("Default-Bogen");
        mSimulator.sleep(sleepTime);
        assertTrue("Bogen Default-Bogen wird nicht angezeigt", mSimulator.searchText("Speichere Bogen..."));
        mSimulator.clickOnText("Speichere Bogen...");
        assertTrue("Bogenliste wird nicht angezeigt", mSimulator.searchText("Default-Bogen"));
        // Insert
        mSimulator.clickOnText("Neuer Bogen");
        assertTrue("Neuer Boegen wird nicht angezeigt", mSimulator.searchText("Speichere Bogen..."));
        mSimulator.clearEditText(0);
        mSimulator.enterText(0, "Default-Bogen 3");
        mSimulator.clickOnText("Speichere Bogen...");
        assertTrue("Bögen werden nicht angezeigt", mSimulator.searchText("Default-Bogen 3"));
        // Delete
        mSimulator.clickLongOnText("Default-Bogen 3");
        assertTrue("Löschen wird nicht angezeigt", mSimulator.searchText("Löschen"));
        mSimulator.clickOnText("Löschen");
        assertTrue("Bögen werden nicht angezeigt", mSimulator.searchText("Default-Bogen"));
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Pfeile"));
        mSimulator.goBack();
        assertTrue("Startseite wird nicht angezeigt", mSimulator.searchText("Einstellungen"));
    }

}
