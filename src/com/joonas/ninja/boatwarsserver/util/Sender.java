package com.joonas.ninja.boatwarsserver.util;

import com.joonas.ninja.boatwarsserver.network.ConnectionHandler;
import com.joonas.ninja.boatwarsserver.network.Server;

/**
 * Created by Joonas on 23.7.2015.
 */

/**
 * A utility class that contains the available replies that the server gives.
 */
public class Sender {

    public static final String SENDER_NAME = "SERVER";

    public synchronized static void broadcastToAll(MessageObject data) {
        MessageObject message = data;

        if(message.getSender() == null){
            message.setSender(SENDER_NAME);
        }
        Server.getGui().printInfo(message.getMessage());

        for (int i = 0; i < Server.getConnections().size(); i++) {
            Server.getConnections().get(i).getOutput().println(Server.gson.toJson(message));
        }
    }

    public static void broadcastBeginGame(){
        MessageObject message = new MessageObject(ServerConstants.REQUEST_GAMEPLAY_START, "", null);
        broadcastToAll(message);
    }

    public static void broadcastBoatPlacement(){
        MessageObject message = new MessageObject(ServerConstants.REQUEST_BEGIN, "", SENDER_NAME);
        String gameId;

        for (int i = 0; i < Server.getConnections().size(); i++) {
            gameId = String.valueOf(i);
            message.setMessage(gameId);
            Server.getGui().printInfo(gameId);
            Server.getConnections().get(i).getOutput().println(Server.gson.toJson(message));
        }
    }

    public synchronized static void closeConnection(ConnectionHandler client, String message) {
        try {
            Server.getGui().printInfo(message);
            MessageObject object = new MessageObject(ServerConstants.REQUEST_INFO, message, SENDER_NAME);
            client.getOutput().println(Server.gson.toJson(object));
            client.disconnectFromClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
