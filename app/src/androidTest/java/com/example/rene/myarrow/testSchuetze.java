package com.example.rene.myarrow;

import android.util.Log;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by rene on 4/4/17.
 */

public class testSchuetze {
    private Solo mSimulator;
    private int sleepTime;
    private static final String TAG = MainActivityTest.class.getSimpleName();

    public testSchuetze(Solo msimulator) {
        mSimulator = msimulator;
        sleepTime =0;
    }

    public testSchuetze(Solo msimulator, int sleeptime) {
        mSimulator = msimulator;
        sleepTime = sleeptime;
    }
    public void testSchuetzen() {
        Log.d(TAG, "Schützen überprüfen");
        // Update
        Log.d(TAG, "Schützen ändern");
        mSimulator.clickOnText("Schützen");
        // mSimulator.assertCurrentActivity("Wartet auf die Class SelektiereSchuetzen", SelektiereSchuetzen.class);
        assertTrue("Schützen werden nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        Log.d(TAG, "Schütze ändern => Default-Schuetze 1 auswählen");
        mSimulator.clickOnText("Default-Schuetze 1");
        // mSimulator.assertCurrentActivity("Wartet auf die Class BearbeiteBogen", BearbeiteBogen.class);
        assertTrue("Schützen werden nicht angezeigt", mSimulator.searchText("Speichere Schütze..."));
        Log.d(TAG, "Schützen ändern => keine Aenderungen vornehmen und direkt auf speichern druecken");
        mSimulator.clickOnText("Speichere Schütze...");
        // mSimulator.assertCurrentActivity("Wartet auf die Class SelektiereBogen", SelektiereBogen.class);
        assertTrue("Schützen werden nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        // Insert
        Log.d(TAG, "Schuetzen einfügen => auf Neuer Schütze drücken");
        mSimulator.clickOnText("Neuer Schütze");
        assertTrue("Neuer Schütze wird nicht angezeigt", mSimulator.searchText("Speichere Schuetze..."));
        Log.d(TAG, "Schütze einfügen => Text loeschen");
        mSimulator.clearEditText(0);
        Log.d(TAG, "Schütze einfügen => Default-Schuetze 4 einfuegen");
        mSimulator.enterText(0, "Default-Schuetze 4");
        Log.d(TAG, "Schütze einfügen => alles abspeichern");
        mSimulator.clickOnText("Speichere Schuetze...");
        // mSimulator.assertCurrentActivity("Wartet auf die Class SelektiereBogen", SelektiereBogen.class);
        assertTrue("Schützen werden nicht angezeigt", mSimulator.searchText("Default-Schuetze 4"));
        // Delete
        Log.d(TAG, "Schütze löschen");
        mSimulator.clickLongOnText("Default-Schuetze 4");
        assertTrue("Löschen wird nicht angezeigt", mSimulator.searchText("Löschen"));
        mSimulator.clickOnText("Löschen");
        // mSimulator.assertCurrentActivity("Wartet auf die Class SelektiereBogen", SelektiereBogen.class);
        assertTrue("Schützen werden nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        mSimulator.goBack();
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Pfeile"));
        mSimulator.goBack();
        assertTrue("Startseite wird nicht angezeigt", mSimulator.searchText("Einstellungen"));
        Log.d(TAG, "Schützen Prüfung abgeschlossen");
    }

}
