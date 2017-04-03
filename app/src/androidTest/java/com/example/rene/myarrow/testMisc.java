package com.example.rene.myarrow;

import android.util.Log;

import com.example.rene.myarrow.GUI.Ergebnis.ErgebnisAmZiel;
import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/3/17.
 */

public class testMisc {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testMisc(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testMisc(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testClickOnWeiter(String[] mCheck) {
        Log.i(TAG, "Weiter geht es => Click on WEITER");
        mSimulator.clickOnText("weiter");
        mSimulator.assertCurrentActivity("Wartet auf die Class ErgebnisAmZiel", ErgebnisAmZiel.class);
        assertTrue(mCheck[0] + " " + mCheck[1] + " wird nicht angezeigt", mSimulator.searchText(mCheck[1]));
        assertTrue(mCheck[0] + " => aktuelle Punkte / max.Punkte (" + mCheck[2] +
                ") wird nicht angezeigt", mSimulator.searchText(mCheck[2]));
        Log.d(TAG, "getLocalClassName:   " + mSimulator.getCurrentActivity().getLocalClassName());
    }

    public void testClickOnZurueck(String[] mCheck) {
        Log.d(TAG, "Zurück zum vorherigen Ziel");
        mSimulator.clickOnText("zurück");
        mSimulator.clickOnText("OK");
        assertTrue("Erstes Ziel " + mCheck[0] + " wird nicht angezeigt", mSimulator.searchText(mCheck[0]));
        assertTrue("Erstes Ziel => aktuelle Punkte / max.Punkte wird nicht angezeigt", mSimulator.searchText(mCheck[1]));
    }

    public void SlideToRight(){
        Log.d(TAG, "nach rechts");
        mSimulator.scrollViewToSide(mSimulator.getView(R.id.pager), Solo.RIGHT);
        Log.d(TAG, "nach links");
        mSimulator.scrollViewToSide(mSimulator.getView(R.id.pager), Solo.LEFT);
        Log.d(TAG, "nach rechts");
        mSimulator.scrollViewToSide(mSimulator.getView(R.id.pager), Solo.RIGHT);
        // Log.d(TAG, "sleep()");
        // mSimulator.sleep(sleepTime);
    }

    public void doRotate(Boolean mRotate){
        if (mRotate) {
            mSimulator.setActivityOrientation(Solo.LANDSCAPE);
            mSimulator.setActivityOrientation(Solo.PORTRAIT);
        }
    }

}
