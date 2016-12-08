package com.example.rene.myarrow.Synchronisation;

/**
 * Konfigurationsklasse für die Netzwerkeinstellungen.
 *
 * Wird vom NetzwerkService für die Kommunikation mit
 * dem MyArrow-Server gebraucht.
 *
 * @author René Düber, 2016
 *
 */
public final class NetzwerkKonfigurator {

    /**
     * IP-Adresse des Amando Servers, wenn die visionera-
     * Serverinstallation verwendet wird.
     */
    // public static final String SERVER_IP = "192.168.178.47";
    public static final String SERVER_IP = "192.168.178.43";
    // public static final String SERVER_IP = "192.168.43.142";

    /**
     * HTTP-Portnummer des MyArrow Servers.
     */
    public static final int HTTP_PORTNUM = 37570;

    /**
     * App Name des MyArrow Servers.
     */
    public static final String APP_NAME = "/MyArrowServer/MyArrow";

}
