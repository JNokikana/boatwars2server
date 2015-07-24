package server.network;

import com.google.gson.Gson;
import server.gui.Gui;
import server.util.Sender;
import server.util.ServerConstants;
import server.util.ServerLogger;

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

    public static boolean isLoggingEnabled(){
        return loggingEnabled;
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

        @Override
        public void run(){
            try{
                startListening();

                while(listening){
                    ConnectionHandler client = new ConnectionHandler(server.accept());
                    if(connections.size() >= ServerConstants.MAX_PLAYERS){
                        Sender.closeConnection(client, "Paskaa perseeseen");
                    }
                    else{
                        connections.add(client);
                        Sender.broadcastToAll(connections, ServerConstants.REQUEST_INFO, client.getClient().getInetAddress().getHostAddress() + " connected.");
                        connectionPool.execute(client);
                    }
                }
            }catch(Exception  e){
                serverStop();
                gui.printError("", e);
            }
        }
    }
}
