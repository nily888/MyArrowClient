package com.example.rene.myarrow.Synchronisation;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.rene.myarrow.Database.Bogen.Bogen;
import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Pfeil.Pfeil;
import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.Database.Runden.Runden;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzen;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenTbl;
import com.example.rene.myarrow.Database.RundenZiel.RundenZiel;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.Schuetzen;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.Synchronisation.ToolsDatenService;

public class SendeDatenService {

    /**
     * Tag für die LogCat.
     */
    private static final String TAG = SendeDatenService.class.getSimpleName();

    /**
     * URL für die HTTP-Verbindung zum Server.
     */
    private String mUrlString = "http://"
            + NetzwerkKonfigurator.SERVER_IP + ":"
            + NetzwerkKonfigurator.HTTP_PORTNUM
            + NetzwerkKonfigurator.APP_NAME;

    private Context context;
    private ToolsDatenService httpRequest;

    public SendeDatenService() {
        super();
        httpRequest = new ToolsDatenService();
    }

    /**
     * Setzen des Sessionkontext, um auf die Datenbank zugreifen zu können. <br>
     *
     * @param context
     *
     */
    public void setContext(Context context){
        this.context = context;
    }

    public void selektiereDaten(Context context) {
        /*
          setze den Sessionkontext
         */
        this.context = context;

        /*
          Synchronisiere die Tabelle "Schuetzen"
         */
        synchronisiereSchuetzen();

        /*
          Synchronisiere die Tabelle "Parcour"
         */
        synchronisiereParcour();

        /*
          Synchronisiere die Tabelle "Ziel"
         */
        synchronisiereZiel();

        /*
          Synchronisiere die Tabelle "Bogen"
         */
        synchronisiereBogen();

        /*
          Synchronisiere die Tabelle "Pfeil"
         */
        synchronisierePfeil();

        /*
          Synchronisiere die Tabelle "Runden"
         */
        synchronisiereRunden();

        /*
          Synchronisiere die Tabelle "RundenSchuetzen"
         */
        synchronisiereRundenSchuetzen();

        /*
          Synchronisiere die Tabelle "RundenZiel"
         */
        synchronisiereRundenZiel();

    }

    /**
     * Update der Tabelle "Schuetzen" an den Server schicken
     */
    private void synchronisiereSchuetzen(){
        /*
          Liste der zu transferierenden Schützen erstellen
         */
        SchuetzenSpeicher schuetzenSpeicher = new SchuetzenSpeicher(context);
        Cursor transferListe = schuetzenSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereSchuetzen(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Schuetzen-Datensatz mit der ID laden
                 */
                Schuetzen mSchuetzen = schuetzenSpeicher.loadSchuetzenDetails(transferListe.getString(0));
                Log.d(TAG, "synchronisiereSchuetzen(): " + mSchuetzen.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mSchuetzen.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    schuetzenSpeicher.transferUpdate(mSchuetzen.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "Parcour"
     */
    private void synchronisiereParcour(){
        /*
          Liste der zu transferierenden Parcoure erstellen
         */
        ParcourSpeicher parcourSpeicher = new ParcourSpeicher(context);
        Cursor transferListe = parcourSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereParcour(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Schuetzen-Datensatz mit der ID laden
                 */
                Parcour mParcour = parcourSpeicher.loadParcourDetails(transferListe.getString(0));
                Log.d(TAG, "synchronisiereParcour(): " + mParcour.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mParcour.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    parcourSpeicher.transferUpdate(mParcour.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "Bogen"
     */
    private void synchronisiereBogen(){
        /*
          Liste der zu transferierenden Bogen erstellen
         */
        BogenSpeicher bogenSpeicher = new BogenSpeicher(context);
        Cursor transferListe = bogenSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereParcour(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Schuetzen-Datensatz mit der ID laden
                 */
                Bogen mBogen = bogenSpeicher.loadBogenDetails(transferListe.getString(0));
                Log.d(TAG, "synchronisiereParcour(): " + mBogen.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mBogen.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    bogenSpeicher.transferUpdate(mBogen.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "Pfeil"
     */
    private void synchronisierePfeil(){
        /*
          Liste der zu transferierenden Pfeile erstellen
         */
        PfeilSpeicher pfeilSpeicher = new PfeilSpeicher(context);
        Cursor transferListe = pfeilSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisierePfeil(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Pfeil-Datensatz mit der ID laden
                 */
                Pfeil mPfeil = pfeilSpeicher.loadPfeilDetails(transferListe.getString(0));
                Log.d(TAG, "synchronisierePfeil(): " + mPfeil.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mPfeil.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    pfeilSpeicher.transferUpdate(mPfeil.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "Runden"
     */
    private void synchronisiereRunden(){
        /*
          Liste der zu transferierenden Runden erstellen
         */
        RundenSpeicher rundenSpeicher = new RundenSpeicher(context);
        Cursor transferListe = rundenSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereRunden(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Runden-Datensatz mit der ID laden
                 */
                Runden mRunden = rundenSpeicher.loadRunden(transferListe.getString(0));
                Log.d(TAG, "synchronisiereRunden(): " + mRunden.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mRunden.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    rundenSpeicher.transferUpdate(mRunden.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "RundenSchuetzen"
     */
    private void synchronisiereRundenSchuetzen(){
        /*
          Liste der zu transferierenden Parcoure erstellen
         */
        final RundenSchuetzenSpeicher rundenSchuetzenSpeicher = new RundenSchuetzenSpeicher(context);
        final Cursor transferListe = rundenSchuetzenSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereRundenSchuetzen(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  RundenSchuetzen-Datensatz mit der ID laden
                 */
                RundenSchuetzen mRundenSchuetzen = rundenSchuetzenSpeicher.loadRundenSchuetzenGID(
                                transferListe.getString(transferListe.getColumnIndex(RundenSchuetzenTbl.GID)));
                Log.d(TAG, "synchronisiereRundenSchuetzen(): " + mRundenSchuetzen.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mRundenSchuetzen.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    rundenSchuetzenSpeicher.transferUpdate(mRundenSchuetzen.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
        rundenSchuetzenSpeicher.schliessen();
    }

    /**
     * Synchronisation der Tabelle "RundenZiel"
     */
    private void synchronisiereRundenZiel(){
        /*
          Liste der zu transferierenden RundenZiele erstellen
         */
        RundenZielSpeicher rundenZielSpeicher = new RundenZielSpeicher(context);
        Cursor transferListe = rundenZielSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereRundenZiel(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  RundenZiel-Datensatz mit der ID laden
                 */
                RundenZiel mRundenZiel = rundenZielSpeicher.loadRundenZiel(transferListe.getString(0));
                Log.d(TAG, "synchronisiereRundenZiel(): " + mRundenZiel.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mRundenZiel.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    rundenZielSpeicher.transferUpdate(mRundenZiel.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }

    /**
     * Synchronisation der Tabelle "Ziel"
     */
    private void synchronisiereZiel(){
        /*
          Liste der zu transferierenden Parcoure erstellen
         */
        ZielSpeicher zielSpeicher = new ZielSpeicher(context);
        Cursor transferListe = zielSpeicher.transferListe();

        /*
          Wieviele Records wurden selektiert
         */
        Log.d(TAG, "synchronisiereZiel(): " + transferListe.getCount());

        /*
         * Abarbeitung der Liste
         */
        if (transferListe.moveToFirst()) {
            do {
                /*
                  Ziel-Datensatz mit der ID laden
                 */
                Ziel mZiel = zielSpeicher.loadZiel(transferListe.getString(0));
                Log.d(TAG, "synchronisiereZiel(): " + mZiel.toString());
                /*
                  Datensatz zum Server schicken
                 */
                if (httpRequest.sendeHttpRequest(mZiel.toBuilder())){
                    /*
                      Wenn die richtige Antwort zurückgekommen ist, Datensatz auf "transfered=1" setzen
                     */
                    zielSpeicher.transferUpdate(mZiel.gid);
                }
                /*
                  und zum nächsten Datensatz
                 */
            } while (transferListe.moveToNext());
        }
        transferListe.close();
    }
}

