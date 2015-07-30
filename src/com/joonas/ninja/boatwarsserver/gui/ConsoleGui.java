package com.joonas.ninja.boatwarsserver.gui;

import java.util.Calendar;

/**
 * Created by joonas on 24.7.2015.
 */
public class ConsoleGui extends Gui{
    @Override
    public void printInfo(String info) {
        System.out.println("[" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "][INFO]: " + info);
    }

    @Override
    public void printError(String error, Exception e) {
        System.out.println("[" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "][ERROR]: " + error);
        e.printStackTrace();
    }
}
