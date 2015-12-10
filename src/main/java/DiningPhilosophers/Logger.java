package DiningPhilosophers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * Created by Ludde on 2015-11-30.
 */
public class Logger {

    private boolean console;
    private boolean log;
    PrintWriter out;
    Timestamp time;

    public Logger(boolean console, boolean log) {
        this.console = console;
        this.log = log;
        time = new Timestamp(System.currentTimeMillis());
        File file = new File("log.txt");
        try {
            out = new PrintWriter(new FileWriter(file.getAbsoluteFile(), false), true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        out.println();
        out.write("- - - - Simulation started on " + time.toString() + " - - - -");
        out.println();

    }

    public synchronized void log(String message) {
        time.setTime(System.currentTimeMillis());
        if (log) {
            out.write(time.getMinutes() + ":" + time.getSeconds() + " - " + message);
            out.println();
        }
        if (console) {
            System.out.println(message);
        }
    }
}
