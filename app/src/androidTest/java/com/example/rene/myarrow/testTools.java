package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testTools {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testTools(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testTools(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }

    public void testToolMenu() {
        mSimulator.clickOnMenuItem("Tools");
        mSimulator.sleep(sleepTime);
        assertTrue("Tools werden nicht angezeigt", mSimulator.searchText("Dateinamen überprüfen"));
        mSimulator.clickOnText("Import Database");
        Log.d(TAG, "testMainActivity(): Datenbak wurde geladen.....");
        mSimulator.sleep(sleepTime/10);
        mSimulator.clickOnText("Dateinamen überprüfen");
        Log.d(TAG, "testMainActivity(): Dateinamen wurden überprüft.....");
        mSimulator.goBack();
    }
}
