package server;

import server.gui.ConsoleGui;
import server.network.Server;

/**
 * Created by Joonas on 23.7.2015.
 * This is the server for the boatwars game.
 */
public class Main {
    public static void main(String[]args){
        Server.init(new ConsoleGui(), false);
    }
}
