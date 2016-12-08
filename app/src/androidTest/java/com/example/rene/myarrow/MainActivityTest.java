package com.example.rene.myarrow;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.rene.myarrow.GUI.Ergebnis.ErgebnisAmZiel;
import com.example.rene.myarrow.GUI.Ziel.BearbeiteZiel;

import com.robotium.solo.Solo;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    // solo.setActivityOrientation(Solo.LANDSCAPE);

    private Solo mSimulator;
    private String[] mCheck;
    private boolean mRotate=false;
    private int sleepTime=2000;
    private int mRunden=1;
    private final String mRunde = "/16 ";
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

        mSimulator.clickOnMenuItem("Tools");
        mSimulator.sleep(sleepTime);
        assertTrue("Tools werden nicht angezeigt", mSimulator.searchText("Dateinamen überprüfen"));
        mSimulator.clickOnText("Import Database");
        Log.d(TAG, "testMainActivity(): Datenbak wurde geladen.....");
        mSimulator.sleep(sleepTime/10);
        mSimulator.clickOnText("Dateinamen überprüfen");
        Log.d(TAG, "testMainActivity(): Dateinamen wurden überprüft.....");
        mSimulator.goBack();

        /**
         * 10 neue Runden eingeben
         */
        for (int n=0; n<mRunden; n++ ){
            Log.d(TAG, "testMainActivity(): Runde = " + n);
            testRunde();
        }

        //
        // Parcour Sub-Menu testen (lange drücken)
        //
        testParcourSubMenu();

        //
        // Einstellungen überprüfen
        //
        testEinstellungen();

        //
        // wenn man bis hierher gekommen ist, war es erfolgreich
        //
        Log.d(TAG, "!!!!!!!!!!!! Prüfung erfolgreich abgeschlossen !!!!!!!!!!!!");
    }

    public void testRunde() {
        Log.i(TAG, "Testen einer Runde");
        //
        // Los geht's und Default-Parcour aussuchen
        //
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();

        // Default-Parcour auswählen
        mCheck = new String[]{"Default-Parcour"};
        mSimulator.clickOnText(mCheck[0]);
        mSimulator.sleep(sleepTime);
        assertTrue(mCheck[0] + " wird nicht angezeigt", mSimulator.searchText("max. erreichte Punkte"));
        doRotate();

        // Schützen auswählen
        mCheck = new String[]{"Default-Schuetze 1"};
        testSchuetzenAuswählen();

        // und jetzt geht es los.....
        Log.i(TAG, "Click on Jetzt geht es los...");
        mSimulator.clickOnText("Jetzt geht es los...");
        assertTrue("Erstes Ziel wird nicht angezeigt", mSimulator.searchText("1/2"));
        assertTrue("Erstes Ziel => aktuelle Punkte / max.Punkte wird nicht angezeigt", mSimulator.searchText("0/40"));
        doRotate();

        // Erster Schuss
        mCheck = new String[]{"Default-Schuetze 1", "im 2ten Schuss", "+10 Punkte"};
        testErgebnisEintragen();

        // und jetzt zum nächsten Ziel
        mCheck = new String[]{"10 - Zweites Ziel", "2/2", "10/30"};
        testClickOnWeiter();

        // Zweiter Schuss
        assertTrue("Zweiter Ziel wird nicht angezeigt", mSimulator.searchText("2/2"));
        mCheck = new String[]{"Default-Schuetze 1", "im 2ten Schuss", "+10 Punkte"};
        testErgebnisEintragen();

        // Zum vorherigen Ziel zurueck
        // und schon wieder bin ich beim ersten Ziel
        mCheck = new String[]{"1/2", "0/40"};
        testClickOnZurueck();

        // und jetzt zum nächsten Ziel
        mCheck = new String[]{"20 - Zweites Ziel", "2/2", "10/30"};
        testClickOnWeiter();

        // Mit weiter jetzt zur Gesamtuebersicht
        Log.d(TAG, "Weiter geht es zur Gesamtuebersicht");
        mSimulator.clickOnText("weiter");
        assertTrue("Zusammenfassung der Runde wird nicht angezeigt", mSimulator.searchText("Parcourname"));

        // Ergebnis anzeigen
        Log.d(TAG, "Ergebnis anzeigen");
        mSimulator.clickOnButton("20");
        assertTrue("Ergebnis wird nicht angezeigt", mSimulator.searchText("Default-Schuetze 1"));
        mSimulator.clickOnText("OK");
        assertTrue("Zusammenfassung der Runde wird nicht angezeigt", mSimulator.searchText("Parcourname"));

        // und das war der Parcour
        Log.d(TAG, "Gesamtuebersicht wird angezeigt und jetzt geht es wieder zur Startseite");
        mSimulator.clickOnText("Ciao......");
        mSimulator.sleep(sleepTime);
        assertTrue("Startbildschirm wird nicht angezeigt", mSimulator.searchText("Los geht's"));

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

    public void testSchuetzenAuswählen (){
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

    public void testErgebnisEintragen() {
        assertEquals("Toggle Button " + mCheck[1] + " not found", true, mSimulator.searchToggleButton(mCheck[1]));
        mSimulator.clickOnToggleButton(mCheck[1]);
        assertEquals("Toggle Button is NOT " + mCheck[2], true, mSimulator.isToggleButtonChecked(mCheck[2]));
    }

    public void testClickOnWeiter() {
        Log.i(TAG, "Weiter geht es => Click on WEITER");
        mSimulator.clickOnText("weiter");
        mSimulator.assertCurrentActivity("Wartet auf die Class ErgebnisAmZiel", ErgebnisAmZiel.class);
        assertTrue(mCheck[0] + " " + mCheck[1] + " wird nicht angezeigt", mSimulator.searchText(mCheck[1]));
        assertTrue(mCheck[0] + " => aktuelle Punkte / max.Punkte (" + mCheck[2] +
                ") wird nicht angezeigt", mSimulator.searchText(mCheck[2]));
        Log.d(TAG, "getLocalClassName:   " + mSimulator.getCurrentActivity().getLocalClassName());
    }

    public void testClickOnZurueck() {
        Log.d(TAG, "Zurück zum vorherigen Ziel");
        mSimulator.clickOnText("zurück");
        mSimulator.clickOnText("OK");
        assertTrue("Erstes Ziel " + mCheck[0] + " wird nicht angezeigt", mSimulator.searchText(mCheck[0]));
        assertTrue("Erstes Ziel => aktuelle Punkte / max.Punkte wird nicht angezeigt", mSimulator.searchText(mCheck[1]));
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

    public void testEinstellungen(){
        Log.d(TAG, "testEinstellung(): started");
        //
        // Review Parcour testen
        //
        testGoToEinstellungen();
        testReviewParcour();

        //
        // Bögen testen
        //
        testGoToEinstellungen();
        testBoegen();

        //
        // Pfeile testen
        //
        testGoToEinstellungen();
        testPfeile();

        // Ziele testen
        testGoToEinstellungen();
        testZiele();

        // Berechnungen testen
        testGoToEinstellungen();
        testBerechnungen();

        // Schützen testen
        testGoToEinstellungen();
        testSchuetzen();

        Log.d(TAG, "testEinstellung(): ended");
    }

    public void testGoToEinstellungen() {
        //
        // Zu den Einstellungen gehen
        //
        assertEquals("Button Einstellungen nicht gefunden", true, mSimulator.searchButton("Einstellungen"));
        mSimulator.clickOnText("Einstellungen");
        mSimulator.sleep(sleepTime);
        assertTrue("Einstellungen werden nicht angezeigt", mSimulator.searchText("Bögen"));
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

    public void testParcourSubMenu() {

        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();

        mSimulator.clickLongOnText("Default-Parcour");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        doRotate();
        mSimulator.clickOnText("Parcour bearbeiten");
        assertTrue("Parcour wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        doRotate();
        mSimulator.clickOnText("Update Parcour");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();

        mSimulator.clickLongOnText("Default-Parcour");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        doRotate();
        mSimulator.clickOnText("Statistik");
        assertTrue("Statistik wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();
        mSimulator.goBack();
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();

        mSimulator.clickLongOnText("Default-Parcour");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        doRotate();
        mSimulator.clickOnText("Auf der Karte anzeigen");
        mSimulator.sleep(sleepTime);
        doRotate();
        mSimulator.goBack();
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();
        mSimulator.clickOnText("Neuer Parcour");
        assertTrue("Neuer Parcour wird nicht angezeigt", mSimulator.searchText("Neuen Parcour anlegen"));
        doRotate();
        mSimulator.enterText(0, "Please Delete");
        doRotate();
        mSimulator.enterText(1, "3");
        doRotate();
        mSimulator.clickOnText("Erstelle Parcour");
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        doRotate();
        mSimulator.clickOnText("Los geht's");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Please Delete"));
        doRotate();
        mSimulator.clickLongOnText("Please Delete");
        assertTrue("SubMenu wird nicht angezeigt", mSimulator.searchText("Parcour bearbeiten"));
        doRotate();
        mSimulator.clickOnText("Parcour löschen");
        assertTrue("SelectParcour wird nicht angezeigt", mSimulator.searchText("Default-Parcour"));
        doRotate();
        mSimulator.goBack();
        assertTrue("Los geht's => nicht gefunden !!", mSimulator.searchText("Los geht's"));
        doRotate();

    }

    private void doRotate(){
        if (mRotate) {
            mSimulator.setActivityOrientation(Solo.LANDSCAPE);
            mSimulator.setActivityOrientation(Solo.PORTRAIT);
        }
    }
}