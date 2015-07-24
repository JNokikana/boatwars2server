package server.util;

import server.network.ConnectionHandler;
import server.network.Server;

import java.util.List;

/**
 * Created by Joonas on 23.7.2015.
 */

/**
 * A utility class that contains the available replies that the server gives.
 */
public class Sender {

    public static final String SENDER_NAME = "SERVER";

    public static void broadcastToAll(List<ConnectionHandler> clients, String type, String text) {
        MessageObject message = new MessageObject(type, text, SENDER_NAME);
        for (int i = 0; i < clients.size(); i++) {
            Server.getGui().printInfo("Sending: " + text);
            clients.get(i).getOutput().printf(Server.gson.toJson(message));
        }
    }

    public static void closeConnection(ConnectionHandler client, String message) {
        try {
            Server.getGui().printInfo("Sending: " + message);
            MessageObject object = new MessageObject(ServerConstants.REQUEST_DISCONNECT, message, SENDER_NAME);
            client.getOutput().printf(Server.gson.toJson(object));
            client.disconnectFromClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
