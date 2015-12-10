package DiningPhilosophers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Ludde on 2015-11-27.
 */
public class Philosopher extends Circle implements Runnable {

    public enum STATE {
        Hungry,
        Eating,
        Thinking,
        Initial,
        ShuttingDown
    }

    private STATE currentState = STATE.Initial;

    private final int id;
    public ChopStick left;
    public ChopStick right;
    private final int sleepTimeBound = 300;
    private Random random = new Random();

    private boolean stopPhilosopher = false;
    public void setStopPhilosopher(boolean t) {this.stopPhilosopher = t;}
    private Logger logger;
    private SimulationStarter.philosopherContainer pc;

    //For measure avg time spent doing
    public ArrayList<Integer> eatRecords;
    public ArrayList<Integer> thinkRecords;
    public ArrayList<Integer> hungryRecords;

    public int getID() {
        return this.id;
    }

    public STATE getState() {return this.currentState;}
    public synchronized void setState(STATE state) {
        STATE previousState = this.currentState;
        if(previousState.equals(STATE.Eating)) {    //If previous state was eating the forks should now be black or "free"
            left.setFill(Paint.valueOf("BLACK"));
            right.setFill(Paint.valueOf("BLACK"));
        }
        if(state.equals(STATE.Eating)) {
            setFill(Paint.valueOf("RED"));
            moveChopsticks();
        } else if(state.equals(STATE.Hungry)) {
            setFill(Paint.valueOf("BLUE"));
        }else if(state.equals(STATE.Thinking)) {
            setFill(Paint.valueOf("GREEN"));
        } else if(state.equals(STATE.ShuttingDown)) {
            setFill(Paint.valueOf("BLACK"));
        } else if(state.equals(STATE.Initial)) {
            setFill(Paint.valueOf("BLACK"));
        } else{
            setFill(Paint.valueOf("ORANGE"));   //Orange is unknown, should never happen.
        }
        this.currentState = state;}

    public void moveChopsticks() {      //Red chopsticks are "Taken"
       left.setFill(Paint.valueOf("RED"));
       right.setFill(Paint.valueOf("RED"));
    }

    public Philosopher(int id, ChopStick left, ChopStick right, Logger log) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.logger = log;

        eatRecords = new ArrayList<Integer>();
        thinkRecords = new ArrayList<Integer>();
        hungryRecords = new ArrayList<Integer>();
    }

    public void setPc(SimulationStarter.philosopherContainer pc) {
        this.pc = pc;
    }

    public void run() {
        try {
            while(!stopPhilosopher) {
                think();        //Think for a random amount of time, when the thinking is done i assume the philisopher gets hungry and tries to pick up chopsticks.
                long startMeasureTime = System.currentTimeMillis();
                while(currentState.equals(STATE.Hungry)) {
                    if(left.pickUpChopStick(this, "left")) {        //pick up left first
                        if(right.pickUpChopStick(this, "right")) {       //Then tries to pick up right.
                            long endMeasureTime = System.currentTimeMillis();
                            int res = ((int) (endMeasureTime - startMeasureTime));
                            hungryRecords.add(res);
                            eat();
                            right.putDownChopStick(this, "right");
                        }
                        left.putDownChopStick(this, "left");        //Puts down left chopstick either after eating or if right was not avalible.
                    }
                    //Wait and try again soon
                    //Thread.sleep(2000);
                }
            }
        } catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private void think() throws InterruptedException {
        logger.log(this + " is thinking");
        setState(STATE.Thinking);
        //Sleeptime is calculated like: Value between 1 and 30 times sleepTimeBound. For example with sleepTimeBound=100, Time wating will be between 0.1 seconds and 3 seconds.
        int sleeptime = (1+random.nextInt(30))*sleepTimeBound;
        thinkRecords.add(sleeptime);
        Thread.sleep(sleeptime);

        if(!currentState.equals(STATE.ShuttingDown))
        setState(STATE.Hungry);
        logger.log(this + " is hungry.");
    }

    private void eat() throws InterruptedException {
        logger.log(this + " is eating");
        setState(STATE.Eating);
        int sleeptime = (1+random.nextInt(30))*sleepTimeBound;
        eatRecords.add(sleeptime);
        Thread.sleep(sleeptime);
        setState(STATE.Thinking);
    }

    public void stop() {
        stopPhilosopher = true;
    }

    @Override
    public String toString() {
        return "Philosopher #" + this.id;
    }

    public void printStatistics() {
        int avgtime = 0;
        for(Integer i : thinkRecords) {
            avgtime += + i;
        }
        if(thinkRecords.isEmpty()) {
            logger.log(this + " tought" + " 0 times.");
        } else {
            String thinkMsg = this + " thought " + thinkRecords.size() + " times, on average tought for " + avgtime / thinkRecords.size() + " ms";
            logger.log(thinkMsg);
        }

        avgtime = 0;
        for(Integer i : eatRecords) {
            avgtime += + i;
        }
        if(eatRecords.isEmpty()) {
            logger.log(this + " ate" + " 0 times.");
        } else {
            String eatMsg = this + " ate " + eatRecords.size() + " times, on average ate for " + avgtime / eatRecords.size() + " ms";
            logger.log(eatMsg);
        }

        avgtime = 0;
        for(Integer i : hungryRecords) {
            avgtime += i;
        }
        if(hungryRecords.isEmpty()) {
            logger.log(this + " was hungry " + " 0 times.");
        } else {
            String hungryMsg = this + " was hungry " + eatRecords.size() + " times, on average hungry for " + avgtime / hungryRecords.size() + " ms";
            logger.log(hungryMsg);
        }
    }
}
