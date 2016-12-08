package com.example.rene.myarrow.Synchronisation;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.rene.myarrow.Database.Bogen.Bogen;
import com.example.rene.myarrow.Database.Bogen.BogenSpeicher;
import com.example.rene.myarrow.Database.Bogen.BogenTbl;
import com.example.rene.myarrow.Database.Parcour.Parcour;
import com.example.rene.myarrow.Database.Parcour.ParcourSpeicher;
import com.example.rene.myarrow.Database.Parcour.ParcourTbl;
import com.example.rene.myarrow.Database.Pfeil.Pfeil;
import com.example.rene.myarrow.Database.Pfeil.PfeilSpeicher;
import com.example.rene.myarrow.Database.Pfeil.PfeilTbl;
import com.example.rene.myarrow.Database.Runden.Runden;
import com.example.rene.myarrow.Database.Runden.RundenSpeicher;
import com.example.rene.myarrow.Database.Runden.RundenTbl;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzen;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenTbl;
import com.example.rene.myarrow.Database.RundenZiel.RundenZiel;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielTbl;
import com.example.rene.myarrow.Database.Schuetzen.Schuetzen;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenSpeicher;
import com.example.rene.myarrow.Database.Schuetzen.SchuetzenTbl;
import com.example.rene.myarrow.Database.Ziel.Ziel;
import com.example.rene.myarrow.Database.Ziel.ZielSpeicher;
import com.example.rene.myarrow.Database.Ziel.ZielTbl;

/**
 * Created by rened on 27.06.2016.
 */
public class EmpfangeDatenService {

    /**
     * Tag für die Logging.
     */
    private static final String TAG = EmpfangeDatenService.class.getSimpleName();

    /**
     * URL für die HTTP-Verbindung zum Server.
     */
    private String mUrlString = "http://"
            + NetzwerkKonfigurator.SERVER_IP + ":"
            + NetzwerkKonfigurator.HTTP_PORTNUM
            + "/MyArrowServer/MyArrow";

    private final ToolsDatenService httpRequest;
    private final Context context;
    private String deviceid;

    public EmpfangeDatenService(Context context) {
        this.context = context;
        httpRequest = new ToolsDatenService();
        /**
         * zunächst Device-Id (z.B. IMEI) auslesen
         */
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceid = tm.getDeviceId();
        if (deviceid==null) {
            Log.e(TAG, "EmpfangeDatenService(): Keine Device-ID vorhanden => auf 000000000000000 gesetzt!!");
            deviceid = "000000000000000";
        }
    }

    public void empfangeDaten(Context context) {
        Log.d(TAG, "empfangeDaten(): Start");
        String response = null;

        /**
         *
         */
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "getdata")
                .appendQueryParameter("deviceid", deviceid);

        if (httpRequest.sendeHttpRequest_temp(builder.build().getEncodedQuery())) {
            response = httpRequest.empfangeHttpRequest();
            Log.d(TAG, "empfangeDaten(): Response = " + response);
            if (response.equals("")) return;

            String tag = response.split("=")[0];
            int data = Integer.valueOf(response.split("=")[1]);
            int got = 0;
            if (tag.equals("anzahl") && data > 0) {
                got = erhalteDaten();
            }

            /**
             * man hat leider nicht erhalten, was man erwartet hat
             */
            if (data != got) {
                Log.e(TAG, "empfangeDaten(): Fehler - Anzahl erwartet=" + data + " und Anzahl erhalten=" + got);
            } else {
                Log.d(TAG, "empfangeDaten(): Es sollte alles synchronisiert sein ;-) => " + got);
            }

            httpRequest.schliesseHttpRequest();

        } else {
            Log.e(TAG, "empfangeDaten(): Verbindung konnte nicht hergestellt worden!");
        }

        Log.d(TAG, "empfangeDaten(): End");
    }

    private int erhalteDaten() {
        String response;
        String record;
        String tag;
        String data;
        boolean weiter = true;
        int got=0;

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("action", "o.k.")
                .appendQueryParameter("deviceid", deviceid);
        while (weiter) {
            if (httpRequest.sendeHttpRequest_temp(builder.build().getEncodedQuery())) {
                response = httpRequest.empfangeHttpRequest();
                Log.d(TAG, "erhalteDaten(): Response = " + response);
                if (response.equals("")) return -1;

                record = response.split("&")[0];
                tag = record.split("=")[0];
                data = record.split("=")[1];
                Log.d(TAG, "erhalteDaten(): TAG  = " + tag);
                Log.d(TAG, "erhalteDaten(): DATA = " + data);
                if (tag.equals("table")) {
                    got++;
                    switch (data) {
                        case "ziel":
                            speichereZiel(response);
                            break;

                        case "parcour":
                            speichereParcour(response);
                            break;

                        case "bogen":
                            speichereBogen(response);
                            break;

                        case "pfeil":
                            speicherePfeil(response);
                            break;

                        case "runden":
                            speichereRunden(response);
                            break;

                        case "rundenschuetzen":
                            speichereRundenSchuetzen(response);
                            break;

                        case "rundenziel":
                            speichereRundenZiel(response);
                            break;

                        case "schuetzen":
                            speichereSchuetzen(response);
                            break;

                        default:
                            Log.e(TAG, "erhalteDaten(): Fehler - Tabelle (" + data + ") unbekannt !!");
                            weiter=false;
                            break;

                    }

                } else if (record.equals("done=done")) {
                    builder = new Uri.Builder().appendQueryParameter("action", "done");
                    httpRequest.sendeHttpRequest_temp(builder.build().getEncodedQuery());
                    weiter = false;

                } else {
                    Log.e(TAG, "erhalteDaten(): unbekannter Fehler !!");
                    got = -1;
                    weiter = false;

                }
            } else {
                Log.e(TAG, "erhalteDaten(): Fehler - Verbindung konnte nicht hergestellt werden !!");
                weiter=false;
            }
        }
        return got;

    }

    private void speichereBogen(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        Bogen bogen = new Bogen();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            switch (tag) {
                case "table":
                    break;

                case BogenTbl.ID:
                    // bogen.id = Long.valueOf(data);
                    break;

                case BogenTbl.GID:
                    bogen.gid = data;
                    break;

                case BogenTbl.NAME:
                    bogen.name = data;
                    break;

                case BogenTbl.DATEINAME:
                    bogen.dateiname = data;
                    break;

                case BogenTbl.STANDARD:
                    bogen.standard = (data == "1" ? true : false);
                    break;

                case BogenTbl.ZEITSTEMPEL:
                    bogen.zeitstempel = Long.valueOf(data);
                    break;

                case BogenTbl.TRANSFERED:
                    // bogen.transfered = Integer.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereBogen(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        BogenSpeicher bs = new BogenSpeicher(context);
        bs.storeForgeinBogen(bogen);
        bs.schliessen();

    }

    private void speichereParcour(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        Parcour parcour = new Parcour();

        for (int n=0; n<daten.length; n++) {
            Log.d(TAG, "speichereParcour(): " + daten[n]);
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            parcour.transfered = 1;
            switch (tag) {
                case "table":
                    break;

                case ParcourTbl.ID:
                    // parcour.id = Long.valueOf(data);
                    break;

                case ParcourTbl.GID:
                    parcour.gid = data;
                    break;

                case ParcourTbl.NAME:
                    parcour.name = data;
                    break;

                case ParcourTbl.ANZAHL_ZIELE:
                    parcour.anzahl_ziele = Integer.valueOf(data);
                    break;

                case ParcourTbl.STRASSE:
                    parcour.strasse = data;
                    break;

                case ParcourTbl.PLZ:
                    parcour.plz = data;
                    break;

                case ParcourTbl.ORT:
                    parcour.ort = data;
                    break;

                case ParcourTbl.GPS_LAT_KOORDINATEN:
                    parcour.gps_lat_koordinaten = data;
                    break;

                case ParcourTbl.GPS_LON_KOORDINATEN:
                    parcour.gps_lon_koordinaten = data;
                    break;

                case ParcourTbl.ANMERKUNG:
                    parcour.anmerkung = data;
                    break;

                case ParcourTbl.STANDARD:
                    parcour.standard = (data.equals("1")?true:false);
                    break;

                case ParcourTbl.TRANSFERED:
                    // parcour.transfered = Integer.valueOf(data);
                    break;

                case ParcourTbl.ZEITSTEMPEL:
                    parcour.zeitstempel = Long.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereParcour(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        ParcourSpeicher ps = new ParcourSpeicher(context);
        ps.storeForgeinParcour(parcour);
        ps.schliessen();

    }

    private void speicherePfeil(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        Pfeil pfeil = new Pfeil();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            switch (tag) {
                case "table":
                    break;

                case PfeilTbl.ID:
                    // pfeil.id = Long.valueOf(data);
                    break;

                case PfeilTbl.GID:
                    pfeil.gid = data;
                    break;

                case PfeilTbl.NAME:
                    pfeil.name = data;
                    break;

                case PfeilTbl.STANDARD:
                    pfeil.standard = (data.equals("1")?true:false);
                    break;

                case PfeilTbl.DATEINAME:
                    pfeil.dateiname = data;
                    break;

                case PfeilTbl.TRANSFERED:
                    // pfeil.transfered = Integer.valueOf(data);
                    break;

                case PfeilTbl.ZEITSTEMPEL:
                    pfeil.zeitstempel = Long.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereBogen(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        PfeilSpeicher ps = new PfeilSpeicher(context);
        ps.storeForgeinPfeil(pfeil);
        ps.schliessen();

    }

    private void speichereRunden(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        Runden runden = new Runden();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            switch (tag) {
                case "table":
                    break;

                case RundenTbl.ID:
                    // runden.id = Integer.valueOf(data);
                    break;

                case RundenTbl.GID:
                    runden.gid = data;
                    break;

                case RundenTbl.PARCOURGID:
                    runden.parcourgid = data;
                    break;

                case RundenTbl.BOGENGID:
                    runden.bogengid = data;
                    break;

                case RundenTbl.PFEILGID:
                    runden.pfeilgid = data;
                    break;

                case RundenTbl.STARTZEIT:
                    runden.startzeit = Long.valueOf(data);
                    break;

                case RundenTbl.S_STARTZEIT:
                    runden.s_startzeit = data;
                    break;

                case RundenTbl.ENDZEIT:
                    runden.endzeit = Long.valueOf(data);
                    break;

                case RundenTbl.WETTER:
                    runden.wetter = data;
                    break;

                case RundenTbl.PUNKTESTAND:
                    runden.punktestand = Integer.valueOf(data);
                    break;

                case RundenTbl.TRANSFERED:
                    // runden.transfered = Integer.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereRunden(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        RundenSpeicher ps = new RundenSpeicher(context);
        ps.storeForgeinRunden(runden);
        ps.schliessen();

    }

    private void speichereRundenSchuetzen(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        RundenSchuetzen rundenschuetzen = new RundenSchuetzen();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            switch (tag) {
                case "table":
                    break;

                case RundenSchuetzenTbl.ID:
                    // rundenschuetzen.id = Long.valueOf(data);
                    break;

                case RundenSchuetzenTbl.GID:
                    rundenschuetzen.gid = data;
                    break;

                case RundenSchuetzenTbl.SCHUETZENGID:
                    rundenschuetzen.schuetzengid = data;
                    break;

                case RundenSchuetzenTbl.RUNDENGID:
                    rundenschuetzen.rundengid = data;
                    break;

                case RundenSchuetzenTbl.GESAMTERGEBNIS:
                    rundenschuetzen.gesamtergebnis = Integer.valueOf(data);
                    break;

                case RundenSchuetzenTbl.TRANSFERED:
                    // rundenschuetzen.transfered= Integer.valueOf(data);
                    break;

                case RundenSchuetzenTbl.ZEITSTEMPEL:
                    rundenschuetzen.zeitstempel = Long.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereBogen(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        RundenSchuetzenSpeicher rss = new RundenSchuetzenSpeicher(context);
        rss.storeForgeinRundenSchuetzen(rundenschuetzen);
        rss.schliessen();

    }

    private void speichereRundenZiel(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        RundenZiel rundenziel = new RundenZiel();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            Log.d(TAG, "speichereRundenZiel(): TAG =" + tag);
            data = daten[n].split("=")[1];
            Log.d(TAG, "speichereRundenZiel(): DATA=" + data);
            switch (tag) {
                case "table":
                    break;

                case RundenZielTbl.ID:
                    // rundenziel.id = Long.valueOf(data);
                    break;

                case RundenZielTbl.GID:
                    rundenziel.gid = data;
                    break;

                case RundenZielTbl.RUNDENGID:
                    rundenziel.rundengid = data;
                    break;

                case RundenZielTbl.ZIELGID:
                    rundenziel.zielgid = data;
                    break;

                case RundenZielTbl.RUNDENSCHUETZENGID:
                    rundenziel.rundenschuetzengid = data;
                    break;

                case RundenZielTbl.NUMMER:
                    rundenziel.nummer = Integer.valueOf(data);
                    break;

                case RundenZielTbl.EINS:
                    rundenziel.eins = (data.equals("1")?true:false);
                    break;

                case RundenZielTbl.ZWEI:
                    rundenziel.zwei = (data.equals("1")?true:false);
                    break;

                case RundenZielTbl.DREI:
                    rundenziel.drei = (data.equals("1")?true:false);
                    break;

                case "kills":
                    rundenziel.kill = (data.equals("1")?true:false);
                    break;

                case RundenZielTbl.KILLKILL:
                    rundenziel.killkill = (data.equals("1")?true:false);
                    break;

                case RundenZielTbl.PUNKTE:
                    rundenziel.punkte = Integer.valueOf(data);
                    break;

                case RundenZielTbl.ANMERKUNG:
                    rundenziel.anmerkung = data;
                    break;

                case RundenZielTbl.DATEINAME:
                    rundenziel.dateiname = data;
                    break;

                case RundenZielTbl.TRANSFERED:
                    // rundenziel.transfered = Integer.valueOf(data);
                    break;

                case RundenZielTbl.ZEITSTEMPEL:
                    rundenziel.zeitstempel = Long.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereRundenZiel(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        RundenZielSpeicher rzs = new RundenZielSpeicher(context);
        rzs.storeForgeinRundenZiel(rundenziel);
        rzs.schliessen();

    }

    private void speichereSchuetzen(String response) {
        String tag;
        String data;
        String[] daten = response.split("&");
        Schuetzen schuetzen = new Schuetzen();

        for (int n=0; n<daten.length; n++) {
            tag = daten[n].split("=")[0];
            data = daten[n].split("=")[1];
            switch (tag) {
                case "table":
                    break;

                case SchuetzenTbl.ID:
                    schuetzen.id = Long.valueOf(data);
                    break;

                case SchuetzenTbl.GID:
                    schuetzen.gid = data;
                    break;

                case SchuetzenTbl.NAME:
                    schuetzen.name = data;
                    break;

                case SchuetzenTbl.DATEINAME:
                    schuetzen.dateiname = data;
                    break;

                case SchuetzenTbl.ZEITSTEMPEL:
                    schuetzen.zeitstempel = Long.valueOf(data);
                    break;

                case SchuetzenTbl.TRANSFERED:
                    // schuetzen.transfered = Integer.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereSchuetzen(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        SchuetzenSpeicher ss = new SchuetzenSpeicher(context);
        ss.storeForgeinSchuetzen(schuetzen);
        ss.schliessen();

    }

    private void speichereZiel(String response) {
        String tag=null;
        String data=null;
        String[] daten = response.split("&");
        Ziel ziel = new Ziel();

        for (int n=0; n<daten.length; n++) {
            Log.d(TAG, "speichereZiel(): Problem? " + n + " / " + daten[n]);
            tag = daten[n].split("=")[0];
            if (daten[n].split("=")[1] == null) {
                data=null;
            } else{
                data = daten[n].split("=")[1];
            }
            switch (tag) {
                case "table":
                    break;

                case ZielTbl.ID:
                    // ziel.id = Integer.valueOf(data);
                    break;

                case ZielTbl.GID:
                    ziel.gid = data;
                    break;

                case ZielTbl.PARCOURGID:
                    ziel.parcourgid = data;
                    break;

                case ZielTbl.NUMMER:
                    ziel.nummer = Integer.valueOf(data);
                    break;

                case ZielTbl.NAME:
                    ziel.name = data;
                    break;

                case ZielTbl.GPS_LAT_KOORDINATEN:
                    ziel.gps_lat_koordinaten = data;
                    break;

                case ZielTbl.GPS_LON_KOORDINATEN:
                    ziel.gps_lon_koordinaten = data;
                    break;

                case ZielTbl.DATEINAME:
                    ziel.dateiname = data;
                    break;

                case ZielTbl.TRANSFERED:
                    // ziel.transfered = Integer.valueOf(data);
                    break;

                case ZielTbl.ZEITSTEMPEL:
                    ziel.zeitstempel = Long.valueOf(data);
                    break;

                default:
                    Log.e(TAG, "speichereZiel(): Fehler - Feld (" + tag + "/" + data + ") unbekannt !!");
                    break;

            }
        }

        ZielSpeicher zs = new ZielSpeicher(context);
        zs.storeForgeinZiel(ziel);
        zs.schliessen();

    }

}
