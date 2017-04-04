package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testParcour {
    private Solo mSimulator;
    private int sleepTime;
    private testMisc tm;
    private Boolean mRotate;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testParcour(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testParcour(Solo msimulator, int sleeptime, Boolean mrotate) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
        tm = new testMisc(mSimulator, sleepTime);
        mRotate = mrotate;    }

    public void testParcour(int newTargets){
        String[] mCheck;

        /**
         * Los geht's und Default-Parcour aussuchen
         */
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);

        /**
         * Sollen neue Ziele hinzukommen?
         */
        if (newTargets > 0) {
            for (int n=1; n<=newTargets; n++) {
                mSimulator.clickLongOnText("Default-Parcour");
                assertTrue("ParcourSubMenu wird nicht angezeigt", mSimulator.searchText("Ziel hinzufügen"));
                tm.doRotate(mRotate);
                mSimulator.clickOnText("Ziel hinzufügen");
                assertTrue("Ziel hinzufügen wird nicht angezeigt", mSimulator.searchText("Zielnummer"));
                mSimulator.clearEditText(0);
                mSimulator.enterText(0, "1");
                mSimulator.enterText(1, "Added Ziel: " + String.valueOf(n).trim());
                mSimulator.clickOnText("Add Ziel...");
                assertTrue("Hinweis wird nicht angezeigt", mSimulator.searchText("Sind Sie sicher?"));
                mSimulator.clickOnText("OK");
                assertTrue("Default-Parcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
            }
        }

        /**
         * Default-Parcour auswählen
         */
        mCheck = new String[]{"Default-Parcour"};
        mSimulator.clickOnText(mCheck[0]);
        mSimulator.sleep(sleepTime);
        assertTrue(mCheck[0] + " wird nicht angezeigt", mSimulator.searchText("max. erreichte Punkte"));
        tm.doRotate(mRotate);

        // Schützen auswählen
        mCheck = new String[]{"Default-Schuetze 1"};
        testSchuetzenAuswählen(mCheck);

    }

    /**
     * Schuetzen aus der Combo Box auswaehlen
     * @param mCheck
     */
    public void testSchuetzenAuswählen (String[] mCheck){
        // Schützen auswählen
        Log.d(TAG, "Click on Schützen");
        mSimulator.clickOnText("Schützen");
        Log.d(TAG, "getLocalClassName:   " + mSimulator.getCurrentActivity().getLocalClassName());
        assertTrue("Default-Schuetze 1 wird in der Schützenliste nicht angezeigt!!", mSimulator.searchText("Default-Schuetze 1"));
        assertTrue("Default-Schuetze 2 wird in der Schützenliste nicht angezeigt!!", mSimulator.searchText("Default-Schuetze 2"));
        for (int n = 0; (n < mCheck.length); n++) {
            mSimulator.clickOnText(mCheck[n]);
        }
        mSimulator.clickOnText("OK");
        assertTrue("StartParcour wird nicht angezeigt", mSimulator.searchText("max. erreichte Punkte"));
    }

    /**
     * Parcour Sub-Menu testen
     */
    public void testParcourSubMenu() {

        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);

        mSimulator.clickLongOnText("Default-Parcour");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Parcour bearbeiten");
        assertTrue("Parcour wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Update Parcour");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);

        mSimulator.clickLongOnText("Default-Parcour");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Statistik");
        assertTrue("Statistik wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);
        mSimulator.goBack();
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);

        //mSimulator.clickLongOnText("Default-Parcour");
        //assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        //doRotate();
        //mSimulator.clickOnText("Auf der Karte anzeigen");
        //mSimulator.sleep(sleepTime);
        //doRotate();
        //mSimulator.goBack();
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Neuer Parcour");
        assertTrue("Neuer Parcour wird nicht angezeigt", mSimulator.searchText("Neuen Parcour anlegen"));
        tm.doRotate(mRotate);
        mSimulator.enterText(0, "Please Delete");
        tm.doRotate(mRotate);
        mSimulator.enterText(1, "3");
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Erstelle Parcour");
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Please Delete"));
        tm.doRotate(mRotate);
        mSimulator.clickLongOnText("Please Delete");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        tm.doRotate(mRotate);
        mSimulator.clickOnText("Parcour löschen");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        tm.doRotate(mRotate);
        mSimulator.goBack();
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        tm.doRotate(mRotate);
    }

}
