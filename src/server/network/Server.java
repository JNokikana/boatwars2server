package server.network;

import com.google.gson.Gson;
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

    public static void init(){
        try{
            gson = new Gson();
            connections = new ArrayList<ConnectionHandler>();
            connectionListener = new ConnectionListener();
            connectionPool = Executors.newFixedThreadPool(ServerConstants.MAX_PLAYERS);
            connectionListener.start();
        }catch(Exception e){
            shutdown();
            e.printStackTrace();
        }
    }

    public static List<ConnectionHandler> getConnections(){
        return connections;
    }

    public static boolean isRunning(){
        return listening;
    }

    public static void shutdown(){
        try{
            connectionListener.stopListening();
            server.close();
            connectionPool.shutdown();
        }catch(Exception e){
            e.printStackTrace();
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
                shutdown();
                e.printStackTrace();
            }
        }
    }
}
