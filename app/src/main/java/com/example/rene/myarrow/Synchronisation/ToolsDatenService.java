package com.example.rene.myarrow.Synchronisation;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rened on 27.06.2016.
 */
public class ToolsDatenService {

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

    /**
     * Verbindung zum Server
     */
    private HttpURLConnection conn = null;

    public ToolsDatenService() {

    }

    /**
     * Versendet den Datensatz vom Schuetzen via HTTP. Die
     * Implementierung nutzt HttpsURLConnection.
     */
    public boolean sendeHttpRequest(String data){

        Log.d(TAG, "sendeHttpRequest(): Start");

        String response="";

        HttpURLConnection conn = null;
        try {

            Log.d(TAG, "sendeHttpRequest(): Erzeuge URL " + mUrlString);
            URL url = new URL(mUrlString);

            Log.d(TAG, "sendeHttpRequest(): Verbindung aufbauen " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));

            Log.d(TAG, "sendeHttpRequest(): Outputstreamerzeugen");
            OutputStream os = conn.getOutputStream();
            DataOutputStream writer = new DataOutputStream(os);

            Log.d(TAG, "sendeHttpRequest(): Daten übertragen - String    - " + data);
            writer.writeBytes(data);
            Log.d(TAG, "sendeHttpRequest(): Daten übertragen? - " + conn.getResponseCode() +": " + conn.getResponseMessage());

            Log.d(TAG, "sendeHttpRequest(): Writer -> Flush()");
            writer.flush();

            Log.d(TAG, "sendeHttpRequest(): Writer -> Close()");
            writer.close();

            Log.d(TAG, "sendeHttpRequest(): os -> Close()");
            os.close();

            Log.d(TAG, "sendeHttpRequest(): Connect()");
            conn.connect();

            Log.d(TAG, "sendeHttpRequest(): Antwort abholen");
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
            return response.equals("Data saved");
        } catch (ClientProtocolException e1) {
            Log.e(TAG, "sendeHttpRequest(): ClientProtocolException: " + e1.toString());
            return false;
        } catch (IOException e2) {
            Log.e(TAG, "sendeHttpRequest(): IOException - " + e2.toString());
            return false;
        } catch (IllegalStateException e3) {
            Log.e(TAG, "sendeHttpRequest(): IllegalStateException - " + e3.toString());
            return false;
        } catch (NetworkOnMainThreadException e4) {
            Log.e(TAG, "sendeHttpRequest(): NetworkOnMainThreadException - " + e4.toString());
            return false;
        } finally {
            if (conn == null) { conn.disconnect(); }
            Log.d(TAG, "sendeHttpRequest(): End = " + response);
        }
    }

    /**
     * Stellt die Verbindung zum Server per Http-POST Request her
     *
     * @return TRUE, falls die Verbindung hergestellt werden konnte, sonst FALSE
     *
     */
    public  boolean verbindeHttpRequest(String bytes) {

        try {
            Log.d(TAG, "sendeHttpRequest(): Erzeuge URL " + mUrlString);
            URL url = new URL(mUrlString);

            Log.d(TAG, "sendeHttpRequest(): Verbindung aufbauen " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            if (Integer.valueOf(bytes)>0) {
                conn.setRequestProperty("Content-Length", bytes);
            }

            return true;

        } catch (ClientProtocolException e1) {
            Log.e(TAG, "sendeHttpRequest(): ClientProtocolException: " + e1.toString());
            return false;

        } catch (IOException e2) {
            Log.e(TAG, "sendeHttpRequest(): IOException - " + e2.toString());
            return false;

        } catch (IllegalStateException e3) {
            Log.e(TAG, "sendeHttpRequest(): IllegalStateException - " + e3.toString());
            return false;

        } catch (NetworkOnMainThreadException e4) {
            Log.e(TAG, "sendeHttpRequest(): NetworkOnMainThreadException - " + e4.toString());
            return false;

        }
    }

    /**
     * Versendet den Datensatz vom Schuetzen via HTTP. Die
     * Implementierung nutzt HttpsURLConnection.
     */
    public boolean sendeHttpRequest_temp(String data){
        Log.d(TAG, "sendeHttpRequest(): Start");

        verbindeHttpRequest(Integer.toString(data.getBytes().length));

        try {
            Log.d(TAG, "sendeHttpRequest(): Outputstreamerzeugen");
            OutputStream os = conn.getOutputStream();
            DataOutputStream writer =new DataOutputStream(os);

            Log.d(TAG, "sendeHttpRequest(): Daten übertragen - String - " + data);
            writer.writeBytes(data);
            Log.d(TAG, "sendeHttpRequest(): Daten übertragen? - " + conn.getResponseCode() +": " + conn.getResponseMessage());

            Log.d(TAG, "sendeHttpRequest(): Writer -> Flush()");
            writer.flush();
            Log.d(TAG, "sendeHttpRequest(): Writer -> Close()");
            writer.close();
            Log.d(TAG, "sendeHttpRequest(): os -> Close()");
            os.close();
            Log.d(TAG, "sendeHttpRequest(): Connect()");
            conn.connect();

            Log.d(TAG, "sendeHttpRequest(): End");
            return (conn.getResponseCode() == HttpURLConnection.HTTP_OK)?true:false;

        } catch (ClientProtocolException e1) {
            Log.e(TAG, "sendeHttpRequest(): ClientProtocolException: " + e1.toString());
            return false;

        } catch (IOException e2) {
            Log.e(TAG, "sendeHttpRequest(): IOException - " + e2.toString());
            return false;

        } catch (IllegalStateException e3) {
            Log.e(TAG, "sendeHttpRequest(): IllegalStateException - " + e3.toString());
            return false;

        } catch (NetworkOnMainThreadException e4) {
            Log.e(TAG, "sendeHttpRequest(): NetworkOnMainThreadException - " + e4.toString());
            return false;

        }

    }

    /**
     * Hole die Antwort / Informationen vom Server ab
     *
     * @return String, was vom Server zurück kam
     *
     */
    public String empfangeHttpRequest() {
        Log.d(TAG, "empfangeHttpRequest(): Start");
        String response = "";

        try {
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            } else {
                response = null;
            }
            Log.d(TAG, "empfangeHttpRequest(): End");
            return response;

        } catch (ClientProtocolException e1) {
            Log.e(TAG, "sendeHttpRequest(): ClientProtocolException: " + e1.toString());
            return null;

        } catch (IOException e2) {
            Log.e(TAG, "sendeHttpRequest(): IOException - " + e2.toString());
            return null;

        } catch (IllegalStateException e3) {
            Log.e(TAG, "sendeHttpRequest(): IllegalStateException - " + e3.toString());
            return null;

        } catch (NetworkOnMainThreadException e4) {
            Log.e(TAG, "sendeHttpRequest(): NetworkOnMainThreadException - " + e4.toString());
            return null;

        }
    }

    /**
     * Schliessen der Verbindung zum Server
     */
    public void schliesseHttpRequest() {
        if (conn != null) {
            conn.disconnect();
        }
    }

}
