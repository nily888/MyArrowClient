package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/3/17.
 */

public class testReviewParcour {
    private Solo mSimulator;
    private int sleepTime;
    private String mRunde;

    public testReviewParcour(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
        mRunde = "/17";
    }

    public testReviewParcour(Solo msimulator, int sleeptime, String mrunde) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
        mRunde = mrunde;
    }

    public void testReviewParcour() {
        Log.d(TAG, "Rieview Parcour testen");
        mSimulator.clickOnText("Review Parcour");
        assertTrue("Default-Parcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        mSimulator.clickOnText("Default-Parcour");
        assertTrue("Runde wird nicht angezeigt", mSimulator.searchText(mRunde));
        mSimulator.clickInList(0);
        // mSimulator.clickOnText(mRunde);
        assertTrue("Default-Schuetze 1 wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        mSimulator.clickOnText("Default-Schuetze 1");
        assertTrue("Ziel 1 wird nicht angezeigt", mSimulator.searchText("Default Ziel 1"));
        mSimulator.clickOnText("Default Ziel 1");
        assertTrue("Ziel zu Bearbeiten wird nicht angezeigt", mSimulator.searchText("+10 Punkte"));
        mSimulator.clickOnText("+10 Punkte");
        assertTrue("Ziel Ergebnis kann nicht geändert werden", mSimulator.searchText("im 2ten Schuss"));
        mSimulator.clickOnText("im 1ten Schuss");
        assertTrue("Mit einem Schuss wird nicht angezeigt", mSimulator.searchText("+16 Punkte"));
        mSimulator.clickOnText("Speichern");
        assertTrue("Ziel 1 mit 16 Punkte wird nicht angezeigt", mSimulator.searchText("16"));
        mSimulator.clickOnText("Default Ziel 1");
        assertTrue("Mit einem Schuss wird nicht angezeigt", mSimulator.searchText("+16 Punkte"));
        mSimulator.clickOnText("+16 Punkte");
        assertTrue("im 1ten Schuss wird nicht angezeigt", mSimulator.searchText("im 1ten Schuss"));
        mSimulator.clickOnText("im 2ten Schuss");
        assertTrue("+10 Punkte wird nicht angezeigt", mSimulator.searchText("+10 Punkte"));
        mSimulator.clickOnText("Speichern");
        assertTrue("Ziel 1 mit 10 Punkte wird nicht angezeigt", mSimulator.searchText("10"));
        mSimulator.goBack();
        assertTrue("Default-Schuetze 1 wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        mSimulator.goBack();
        assertTrue("Runde wird nicht angezeigt", mSimulator.searchText(mRunde));
        mSimulator.goBack();
        assertTrue("Default-Parcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Review Parcour"));
        mSimulator.goBack();
        assertTrue("Startbildschirm wird nicht angezeigt", mSimulator.searchText("Los geht's"));
        Log.d(TAG, "Review Parcour Prüfung abgeschlossen");
    }

}
