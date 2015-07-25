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

    public static void broadcastToAll(String type, String text) {
        MessageObject message = new MessageObject(type, "'" + text + "'", SENDER_NAME);
        for (int i = 0; i < Server.getConnections().size(); i++) {
            Server.getGui().printInfo(text);
            Server.getConnections().get(i).getOutput().println(Server.gson.toJson(message));
        }
    }

    public static void broadcastBeginGame(){
        MessageObject message = new MessageObject(ServerConstants.REQUEST_BEGIN, "", SENDER_NAME);
        String gameId;

        for (int i = 0; i < Server.getConnections().size(); i++) {
            gameId = String.valueOf(i);
            message.setMessage(gameId);
            Server.getGui().printInfo(gameId);
            Server.getConnections().get(i).getOutput().println(Server.gson.toJson(message));
        }
    }

    public static void closeConnection(ConnectionHandler client, String message) {
        try {
            Server.getGui().printInfo(message);
            MessageObject object = new MessageObject(ServerConstants.REQUEST_INFO, "'" + message + "'", SENDER_NAME);
            client.getOutput().println(Server.gson.toJson(object));
            client.disconnectFromClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
