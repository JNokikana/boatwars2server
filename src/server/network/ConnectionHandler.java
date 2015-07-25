package server.network;

import server.util.MessageObject;
import server.util.Player;
import server.util.ServerConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Predicate;

public class ConnectionHandler extends Thread{
    private Socket client;
    private boolean running;
    private BufferedReader input;
    private PrintWriter output;
    private String readData;
    private Player player;
    
    public ConnectionHandler(Socket client){
        try{
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            running = true;
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
    }

    public boolean isRunning(){
        return running;
    }

    public Socket getClient(){
        return this.client;
    }

    public synchronized PrintWriter getOutput(){
        return output;
    }

    public void disconnectFromClient(){
        try{
            if(input != null){
                input.close();
            }
            if(client != null){
                client.close();
            }
            running = false;
            /* We remove this connection from the list of active connections. */
            Server.getConnections().remove(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleRequest(MessageObject data){
        switch(data.getType()){
            case ServerConstants.REQUEST_JOIN:

        }
    }
    
    @Override
    public void run(){
        try{
            while(running){
                while((readData = input.readLine()) != null){
                    Server.getGui().printInfo(readData);
                    handleRequest(Server.gson.fromJson(readData, MessageObject.class));
                }

                if(input.read() == -1){
                    Server.getGui().printInfo(client.getInetAddress().getHostAddress() + " disconnected from server.");
                    disconnectFromClient();
                }
            }
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
    }
}
