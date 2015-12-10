package DiningPhilosophers;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.Random;

public class Philosopher extends Circle implements Runnable {


    //Possible states of a philosopher
    public enum STATE {
        Hungry,
        Eating,
        Thinking,
        Initial,
        ShuttingDown
    }

    //State starts of as initial.
    private STATE currentState = STATE.Initial;

    private final int id;
    public ChopStick left;
    public ChopStick right;
    private final int sleepTimeBound = 300;     //This sets the length of wich the Philosopher will think/eat. 1-30 will be randomed and multiplied with this number. Example set to 300 for 300ms - 9000ms range.
    private Random random = new Random();

    private boolean stopPhilosopher = false;
    private Logger logger;

    //For measure avg time spent doing stuff
    public ArrayList<Integer> eatRecords;
    public ArrayList<Integer> thinkRecords;
    public ArrayList<Integer> hungryRecords;


    //set & gets
    public int getID() {
        return this.id;
    }


    public STATE getState() {return this.currentState;}
    //using setState for some functionality. Colors chopsticks/philosophers on state change.
    public void setState(STATE state) {
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

    public void setStopPhilosopher(boolean stopPhilosopher) {
        this.stopPhilosopher = stopPhilosopher;
    }

    //Doesnt move anything yet but colors Taken/Locked chopsticks red.
    public void moveChopsticks() {      //Red chopsticks are "Taken"
       left.setFill(Paint.valueOf("RED"));
       right.setFill(Paint.valueOf("RED"));
    }

    public Philosopher(int id, ChopStick left, ChopStick right, Logger log) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.logger = log;

        eatRecords = new ArrayList<>();
        thinkRecords = new ArrayList<>();
        hungryRecords = new ArrayList<>();
    }


    public void run() {
        try {
            while(!stopPhilosopher) {
                think();        //Think for a random amount of time, when the thinking is done i assume the philisopher gets hungry and tries to pick up chopsticks.
                long startMeasureTime = System.currentTimeMillis();     //Start measure time for hungry timing.
                while(currentState.equals(STATE.Hungry)) {
                    if(left.pickUpChopStick(this, "left")) {        //pick up left first
                        if(right.pickUpChopStick(this, "right")) {       //Then tries to pick up right.
                            long endMeasureTime = System.currentTimeMillis();   //Stop measure time for hungry timing.
                            int res = ((int) (endMeasureTime - startMeasureTime)); //Calc diffrence
                            hungryRecords.add(res);     //add time hungry to list.
                            eat();      //Eat when chopsticks aquired.
                            right.putDownChopStick(this, "right");
                        }
                        left.putDownChopStick(this, "left");        //Puts down left chopstick either after eating or if right was not avalible.
                    }
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

        if(!currentState.equals(STATE.ShuttingDown)) {
            setState(STATE.Hungry);
            logger.log(this + " is hungry.");
        }
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


    //Prints out a summary of how many times and for how long on average a philosopher tought/was hungry/ate
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
