package server.network;

import server.util.MessageObject;

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
    
    public ConnectionHandler(Socket client){
        try{
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            running = true;
            System.out.println(input);
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleRequest(MessageObject data){
        System.out.println(data.getMessage());
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
                    disconnectFromClient();
                }
            }
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
    }
}
