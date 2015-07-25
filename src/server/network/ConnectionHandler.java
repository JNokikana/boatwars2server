package server.network;

import server.util.MessageObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Predicate;

public class ConnectionHandler extends Thread implements Predicate {
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
            System.out.println("Suljettu");
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
                    Server.getGui().printInfo(client.getInetAddress().getHostAddress() + " disconnected from server.");
                    disconnectFromClient();
                }
            }
        }catch(Exception e){
            disconnectFromClient();
            e.printStackTrace();
        }
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) {
        return false;
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     * <p>
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    @Override
    public Predicate and(Predicate other) {
        return null;
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    @Override
    public Predicate negate() {
        return null;
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     * <p>
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    @Override
    public Predicate or(Predicate other) {
        return null;
    }
}
