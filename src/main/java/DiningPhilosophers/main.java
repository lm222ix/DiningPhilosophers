package DiningPhilosophers;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ludde on 2015-11-27.
 */
public class main {

    public static void main(String[] args) throws InterruptedException {
        SimulationStarter s = new SimulationStarter();
        new Thread(s).start();
    }
}
