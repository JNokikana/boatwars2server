package server.util;

/**
 * Created by Joonas on 23.7.2015.
 */
public abstract interface ServerConstants {

    public final static String SERVER_MESSAGE_START = "BoatWars server started!";
    public final static String SERVER_MESSAGE_BEGIN = "Both players have joined. Place your boats. When ready, press 'Ready'.";

    public final static int MAX_PLAYERS = 2;
    public final static int PORT = 17413;
    /*testi*/
    public final static String REQUEST_JOIN = "JOIN";
    public final static String REQUEST_MESSAGE = "MESSAGE";
    public final static String REQUEST_SERVER = "SERVER";
    public final static String REQUEST_PLAYER = "PLAYER";
    public final static String REQUEST_DISCONNECT = "DISCONNECT";
    public final static String REQUEST_CLIENT_BYE = "IT IS A GOOD DAY TO DIE!";
    public final static String REQUEST_READY = "READY";
    public final static String REQUEST_ENDTURN = "ENDTURN";
    public final static String REQUEST_BEGIN = "BEGIN";
    public final static String REQUEST_HIT = "HIT";
    public final static String REQUEST_MISS = "MISS";
    public final static String REQUEST_ALL_DESTROYED = "YOU SUNK MY BATTLESHIPS!";
    public final static String REQUEST_SUNK = "SUNK";
    public final static String REQUEST_INFO = "INFO";
    public final static String REQUEST_GAMEPLAY_START = "GAMEON";
}
