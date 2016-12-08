package com.example.rene.myarrow.Database.Runden;

import android.net.Uri;

/**
 * Created by nily on 14.12.15.
 */
public class Runden implements RundenColumns{

    /**
     * id der DB Tabelle in der MyArrow Datenbank.
     */
    public long id;

    /**
     * Pflichtfeld: Globaler Primaerschuessel
     *
     * TEXT
     */
    public String gid;

    /**
     * Pflichtfeld: Parcour-ID
     *
     * LONG
     */
    public long parcourid;

    /**
     * Pflichtfeld: Parcour-GID
     *
     * TEXT
     */
    public String parcourgid;

    /**
     * Pflichtfeld: Bogen-ID
     *
     * LONG
     */
    public long bogenid;

    /**
     * Pflichtfeld: Bogen-GID
     *
     * TEXT
     */
    public String bogengid;

    /**
     * Pflichtfeld: Pfeil-ID
     *
     * LONG
     */
    public long pfeilid;

    /**
     * Pflichtfeld: Pfeil-GID
     *
     * TEXT
     */
    public String pfeilgid;

    /**
     * Zeitpunkt zum Start der Runde.
     *
     * LONG
     */
    public long startzeit;

    /**
     * Zeitpunkt zum Start der Runde.
     *
     * STRING
     */
    public String s_startzeit;

    /**
     * Zeitpunkt zum Ende der Runde.
     *
     * LONG
     */
    public long endzeit;

    /**
     * Wetter zum Start der Runde.
     *
     * TEXT
     */
    public String wetter;

    /**
     * aktueller Punktestand.
     *
     * INTEGER
     */
    public int punktestand;

    /**
     * Wurde der Datensatz schon zum Server raufgeladen (0=nein; 1=ja)
     *
     * INT
     */
    public int transfered;

    /**
     * Datensatz als String konvertieren
     */
    public String toString() {
        return "table=runden" +
                "&" + ID + "=" + String.valueOf(id) +
                "&" + GID + "=" + gid +
                "&" + PARCOURID + "=" + String.valueOf(parcourid) +
                "&" + PARCOURGID + "=" + parcourgid +
                "&" + BOGENID + "=" + String.valueOf(bogenid) +
                "&" + BOGENGID + "=" + bogengid +
                "&" + PFEILID + "=" + String.valueOf(pfeilid) +
                "&" + PFEILGID + "=" + pfeilgid +
                "&" + STARTZEIT + "=" + String.valueOf(startzeit) +
                "&" + S_STARTZEIT + "=" + s_startzeit +
                "&" + ENDZEIT + "=" + String.valueOf(endzeit) +
                "&" + WETTER + "=" + wetter +
                "&" + PUNKTESTAND + "=" + String.valueOf(punktestand);
    }

    /**
     * Datensatz als String konvertieren
     */
    public String toBuilder() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("table", "runden")
                .appendQueryParameter(ID, String.valueOf(id))
                .appendQueryParameter(GID, gid)
                .appendQueryParameter(PARCOURID, String.valueOf(parcourid))
                .appendQueryParameter(PARCOURGID, parcourgid)
                .appendQueryParameter(BOGENID, String.valueOf(bogenid))
                .appendQueryParameter(BOGENGID, bogengid)
                .appendQueryParameter(PFEILID, String.valueOf(pfeilid))
                .appendQueryParameter(PFEILGID, pfeilgid)
                .appendQueryParameter(STARTZEIT, String.valueOf(startzeit))
                .appendQueryParameter(S_STARTZEIT, s_startzeit)
                .appendQueryParameter(ENDZEIT, String.valueOf(endzeit))
                .appendQueryParameter(WETTER, wetter)
                .appendQueryParameter(PUNKTESTAND, String.valueOf(punktestand));
        return builder.build().getEncodedQuery();
    }
}
