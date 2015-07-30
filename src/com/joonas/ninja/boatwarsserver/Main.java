package com.joonas.ninja.boatwarsserver;

import com.joonas.ninja.boatwarsserver.gui.ConsoleGui;
import com.joonas.ninja.boatwarsserver.network.Server;

/**
 * Created by Joonas on 23.7.2015.
 * This is the server for the boatwars game.
 */
public class Main {
    public static void main(String[]args){
        Server.init(new ConsoleGui(), false);
    }
}
