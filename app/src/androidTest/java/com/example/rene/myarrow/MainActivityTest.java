package com.example.rene.myarrow;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    // solo.setActivityOrientation(Solo.LANDSCAPE);

    private Solo mSimulator;
    private String[] mCheck;
    private static final boolean mRotate=false;
    private static final int sleepTime=2000;
    private static final int mRunden=1;
    private static final String mJahr = "/17 ";
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public MainActivityTest(){
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        mSimulator = new Solo(getInstrumentation(), getActivity());
    }

    public void testMainActivity() throws Exception {

        /**
         * Default Orientation PORTRAIT
         */
        mSimulator.setActivityOrientation(Solo.PORTRAIT);

        /**
         * Aus dem Tools Menu etwas testen / benutzen
         */
        new testTools(mSimulator, sleepTime).testToolMenu();

        /**
         * Parcour auswählen, kein Ziel hinzufügen
         */
        new testParcour(mSimulator, sleepTime, mRotate).testParcour(0);

        /**
         * n neue Runden eingeben, 2 Ziele
         */
        for (int n=0; n<mRunden; n++ ){
            Log.d(TAG, "testMainActivity(): Runde = " + n);
            new testRunde(mSimulator, sleepTime, mRotate).testRunde(2);
        }

        /**
         * Parcour auswählen, zwei Ziele hinzufügen
         */
        new testParcour(mSimulator, sleepTime, mRotate).testParcour(2);

        /**
         * n neue Runden eingeben, yy Ziele
         */
        for (int n=0; n<mRunden; n++ ){
            Log.d(TAG, "testMainActivity(): Runde = " + n);
            new testRunde(mSimulator, sleepTime, mRotate).testRunde(4);
        }

        /**
         * Parcour Sub-Menu testen (lange drücken)
         */
        new testParcour(mSimulator, sleepTime, mRotate).testParcourSubMenu();

        /**
         * Einstellungen überprüfen
         */
        new testEinstellungen(mSimulator, sleepTime).testEinstellungen(mJahr);

        /**
         * wenn man bis hierher gekommen ist, war es erfolgreich
         */
        Log.d(TAG, "!!!!!!!!!!!! Prüfung erfolgreich abgeschlossen !!!!!!!!!!!!");

    }

    public void testEreignisMenu(){

        mSimulator.clickOnText(mCheck[0]);
        assertTrue("Ereignis-Menu wird nicht angezeigt", mSimulator.searchText("Bild"));
        mSimulator.clickOnText("Bild");
        // assertTrue("Foto-Auswahlmenu wird nicht angezeigt", mSimulator.searchText("Gallery"));
        // mSimulator.clickOnImage(0);
        // assertTrue("Bildauswahl wird nicht angezeigt", mSimulator.searchText("Choose picture"));
        // mSimulator.clickOnImage(0);
        mSimulator.goBack();
        mSimulator.goBack();
        assertTrue("Ergebnis wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));

        mSimulator.clickOnText(mCheck[0]);
        assertTrue("Ereignis-Menu wird nicht angezeigt", mSimulator.searchText("Map"));
        mSimulator.clickOnText("Map");
        mSimulator.goBack();
        assertTrue("Ergebnis wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
    }
}