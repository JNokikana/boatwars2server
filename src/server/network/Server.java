package server.network;

import com.google.gson.Gson;
import server.gui.Gui;
import server.util.Sender;
import server.util.ServerConstants;

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

    public static List<ConnectionHandler> getConnections(){
        return connections;
    }

    public static boolean isRunning(){
        return listening;
    }

    public static Gui getGui(){
        return gui;
    }

    public static ServerSocket getServer(){
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
                        Sender.broadcastToAll(connections, ServerConstants.REQUEST_INFO, client.getClient().getInetAddress().getHostAddress() + " connected.");
                    }
                }
            }catch(Exception  e){
                serverStop();
                gui.printError("", e);
            }
        }
    }
}
