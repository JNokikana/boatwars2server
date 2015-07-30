package com.joonas.ninja.boatwarsserver.network;

import com.joonas.ninja.boatwarsserver.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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
            Server.resetGame();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createNewPlayer(String nickname){
        player = new Player();
        player.setName(nickname);
        Server.getGui().printInfo("Player created: " + nickname);
    }

    private void readyPlayer(){
        player.setReady(true);
        Sender.broadcastToAll(new MessageObject(ServerConstants.REQUEST_INFO, "Player " + player.getName() + " is ready.", null));
        Server.startGameIfReady();
    }

    private void markRematch(MessageObject data){
        MessageObject message;

        if(!player.isWantsRematch()){
            message = new MessageObject(ServerConstants.REQUEST_MESSAGE, data.getSender() + " desires a rematch!", Sender.SENDER_NAME);
        }
        else{
            message = new MessageObject(ServerConstants.REQUEST_MESSAGE, data.getSender() + " is desperately spamming for a rematch!", Sender.SENDER_NAME);
        }

        player.setWantsRematch(true);
        Sender.broadcastToAll(message);
        Server.startRematchIfOkay();
    }

    public void handleRequest(MessageObject data){
        switch(data.getType()){
            case ServerConstants.REQUEST_JOIN:
                createNewPlayer(data.getSender());
                break;
            case ServerConstants.REQUEST_ENDTURN:
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_READY:
                readyPlayer();
                break;
            case ServerConstants.REQUEST_MESSAGE:
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_HIT:
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_MISS:
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_SUNK:
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_ALL_DESTROYED:
                /* We reset the player variables to their pre-game state. */
                Server.resetVariables();
                data.setMessage(data.getSender() + ServerConstants.LOSER_MESSAGES[(int)(Math.random() * ServerConstants.LOSER_MESSAGES.length)] + ServerConstants.SERVER_MESSAGE_REMATCH);
                data.setSender(Sender.SENDER_NAME);
                Sender.broadcastToAll(data);
                break;
            case ServerConstants.REQUEST_REMATCH_YES:
                markRematch(data);
                break;
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
                    Sender.broadcastToAll(new MessageObject(ServerConstants.REQUEST_INFO, client.getInetAddress().getHostAddress() + " disconnected from server.", null));
                    disconnectFromClient();
                }
            }
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
    }
}
