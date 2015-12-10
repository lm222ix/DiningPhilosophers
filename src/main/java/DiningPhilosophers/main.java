package DiningPhilosophers;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main {

    public static void main(String[] args) throws InterruptedException {

        //Starts up gui
        SimulationStarter s = new SimulationStarter();
        new Thread(s).start();
    }
}
