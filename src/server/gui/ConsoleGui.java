package server.gui;

/**
 * Created by joonas on 24.7.2015.
 */
public class ConsoleGui extends Gui{
    @Override
    public void printInfo(String info) {
        System.out.println(info);
    }

    @Override
    public void printError(String error) {
        System.err.println(error);
    }
}
