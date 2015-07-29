package abalone.utilities;

import javax.swing.*;

/**
 * Handles exceptions that are caught in the game.
 */
public class ExceptionHandler {

    public static final String ERROR_LOG = "crash_log/error.log";

    /**
     * Handles the given exception, records its message and stack trace
     * in ERROR_LOG, shows the user a confirm dialog asking the user to
     * report the problem, then closes the system.
     * @param e
     */
    public static void handle(Exception e){
        String log = e.getClass().getName() + "\n" + e.getMessage() + "\n";
        for(StackTraceElement element : e.getStackTrace()){
            log += element.toString() + "\n";
        }
        FileHandler.write(ERROR_LOG, log);
        String message = "I'm terribly sorry but something has gone horribly wrong.\n" +
                         "You will find a file under resources/crash_log/error.log.\n" +
                         "I will be grateful if you could send this file to James at either \n" +
                         "https://www.facebook.com/AbaloneTesters or by email to: \n" +
                         "james.byrne.2013@uni.strath.ac.uk.\n" +
                         "Thank you for your help!";
        JOptionPane.showMessageDialog(null, message, "A crash has occurred.", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
}
