/*
 * OzuRadyoApp.java
 */

package edu.ozyegin.ozuradyo;

import edu.ozyegin.ozuradyo.manage.ConsolePlayer;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class OzuRadyoApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new OzuRadyoView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of OzuRadyoApp
     */
    public static OzuRadyoApp getApplication() {
        return Application.getInstance(OzuRadyoApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("console"))
            (new ConsolePlayer()).play();
        else
            launch(OzuRadyoApp.class, args);
    }
}
