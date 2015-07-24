package server.gui;

import java.text.SimpleDateFormat;

/**
 * Created by joonas on 24.7.2015.
 */
public abstract class Gui {
    protected final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("DD/MM/YYYY|HH:mm:ss");

    public abstract void printInfo(String info);
    public abstract void printError(String error, Exception e);
}
