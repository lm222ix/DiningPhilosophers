package DiningPhilosophers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

public class Logger {

    private boolean console;
    private boolean log;
    PrintWriter out;
    Timestamp time;

    //Logger constructor takes 2 bools. Set first one to true to print to console, second one to print to logfile.
    //I use both in my example.

    public Logger(boolean console, boolean log) {
        this.console = console;
        this.log = log;
        time = new Timestamp(System.currentTimeMillis());
        File file = new File("log.txt");
        try {
            out = new PrintWriter(new FileWriter(file.getAbsoluteFile(), false), true); //Create new logfile each time program is run
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    //Synchronized keyword is important here, the log file looks really messy without it since threads will print on same line sometimes.
    public synchronized void log(String message) {
        time.setTime(System.currentTimeMillis());
        if (log) {
            out.write(time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds() + " - " + message);
            out.println();
        }
        if (console) {
            System.out.println(message);
        }
    }
}
