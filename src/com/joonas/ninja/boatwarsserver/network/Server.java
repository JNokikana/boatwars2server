package com.joonas.ninja.boatwarsserver.network;

import com.google.gson.Gson;
import com.joonas.ninja.boatwarsserver.gui.Gui;
import com.joonas.ninja.boatwarsserver.util.MessageObject;
import com.joonas.ninja.boatwarsserver.util.Sender;
import com.joonas.ninja.boatwarsserver.util.ServerConstants;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ServerSocket server;
    private static ConnectionListener connectionListener;
    private static ExecutorService connectionPool;
    private static List<ConnectionHandler> connections;
    private static boolean listening;
    public static Gson gson;
    private static Gui gui;
    private static boolean loggingEnabled;
    private static boolean inProgress;
    /**
     * Whose turn is it currently.
     */
    private static int turnPlayerId = 0;

    public static void init(Gui chosen, boolean logging){
        try{
            gson = new Gson();
            gui = chosen;
            loggingEnabled = logging;
            connections = new ArrayList<ConnectionHandler>();
            connectionListener = new ConnectionListener();
            connectionPool = Executors.newFixedThreadPool(ServerConstants.MAX_PLAYERS);
            connectionListener.start();
            gui.printInfo(ServerConstants.SERVER_MESSAGE_START);
        }catch(Exception e){
            serverStop();
            gui.printError("", e);
        }
    }

    public static int getTurnPlayerId() {
        return turnPlayerId;
    }

    public static void setTurnPlayerId(int turnPlayerId) {
        Server.turnPlayerId = turnPlayerId;
    }


    public synchronized static List<ConnectionHandler> getConnections(){
        return connections;
    }

    public static boolean isRunning(){
        return listening;
    }

    public synchronized static Gui getGui(){
        return gui;
    }

    public synchronized static ServerSocket getServer(){
        return server;
    }

    public static boolean isLoggingEnabled(){
        return loggingEnabled;
    }

    public static boolean isInProgress() {
        return inProgress;
    }

    public static void setInProgress(boolean inProgress) {
        Server.inProgress = inProgress;
    }

    public static void serverStop(){
        try{
            if(connectionListener != null){
                connectionListener.stopListening();
            }
            if(server != null){
                server.close();
            }
            if(connectionPool != null){
                connectionPool.shutdown();
            }
        }catch(Exception e){
            gui.printError("", e);
        }
    }

    /**
     * Checks whether both players are ready ands starts the game if this is the case.
     */
    public synchronized static void startGameIfReady(){
        for(int i = 0; i < connections.size(); i ++){
            if(!connections.get(i).getPlayer().isReady()){
                return;
            }
        }

        Sender.broadcastBeginGame();
    }

    /**
     * Check whether or not rematch conditions have been met.
     */
    public synchronized static void startRematchIfOkay(){
        for(int i = 0; i < connections.size(); i ++){
            if(!connections.get(i).getPlayer().isWantsRematch()){
                return;
            }
        }
        Sender.broadcastBoatPlacement();
    }

    /**
     * Resets player variables to their pre-game state.
     */
    public synchronized static void resetVariables(){
        for(int i = 0; i < connections.size(); i ++){
            connections.get(i).getPlayer().setReady(false);
            connections.get(i).getPlayer().setWantsRematch(false);
        }
    }

    public synchronized static void resetGame(){
        Server.setInProgress(false);
        MessageObject message = new MessageObject(ServerConstants.REQUEST_RESET_GAME, "The other player disconnected. Game has been reset.", null);
        resetVariables();
        Sender.broadcastToAll(message);
        Sender.broadcastToAll(new MessageObject(ServerConstants.REQUEST_INFO, "Number of connected players is now " +
                connections.size() + ". Game will begin once two players have connected.", null));
    }

    /**
     * The listener thread for the server that waits for incoming client connections.
     */
    private static class ConnectionListener extends Thread{

        private void startListening() throws Exception{
            server = new ServerSocket(ServerConstants.PORT);
            listening = true;
        }

        private void stopListening() throws Exception{
            listening = false;
        }

        /**
         * Checks whether enough players have connected to begin the game.
         * @return
         */
        private void beginGame(){
            Sender.broadcastBoatPlacement();
            inProgress = true;
        }

        /**
         * Checks if the server is full.
         * @return
         */
        private boolean isServerFull(){
            if(connections.size() >= ServerConstants.MAX_PLAYERS){
                return true;
            }
            return false;
        }

        @Override
        public void run(){
            try{
                startListening();

                while(listening){
                    ConnectionHandler client = new ConnectionHandler(server.accept());
                    if(isServerFull()){
                        Sender.closeConnection(client, "Refused connection to " + client.getClient().getInetAddress().getHostAddress() + " - Server is full.");
                    }
                    else{
                        connections.add(client);
                        connectionPool.execute(client);
                        Sender.broadcastToAll(new MessageObject(ServerConstants.REQUEST_INFO, client.getClient().getInetAddress().getHostAddress() +
                                " connected.", null));
                        if(connections.size() == ServerConstants.MAX_PLAYERS){
                            beginGame();
                        }
                        else{
                            Sender.broadcastToAll(new MessageObject(ServerConstants.REQUEST_INFO, "Number of connected players is now " +
                                    connections.size() + ". Game will begin once two players have connected.", null));
                        }

                    }
                }
            }catch(Exception  e){
                serverStop();
                gui.printError("", e);
            }
        }
    }
}
