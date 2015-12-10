package DiningPhilosophers;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ludde on 2015-11-27.
 */
public class ChopStick extends Rectangle {

    public Lock lock;
    private final int id;
    private Logger logger;

    public ChopStick(int id, Logger logger) {
        this.id = id;
        lock = new ReentrantLock();
        this.logger = logger;
        this.setWidth(8);
        this.setHeight(60);
        this.setFill(Paint.valueOf("BLACK"));
    }

    public boolean pickUpChopStick(Philosopher whoPickedUp, String leftOrRight) {
        if(!whoPickedUp.getState().equals(Philosopher.STATE.ShuttingDown)) {
            try {
                if (lock.tryLock(2000, TimeUnit.MILLISECONDS)) {    //Tries to aquire the lock, if not avalible it will try for 2 seconds time.
                    logger.log(whoPickedUp + " picked up " + leftOrRight + " " + this);
                    return true;
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;

    }

    public void putDownChopStick(Philosopher whoPutDown, String leftOrRight) {
        lock.unlock();
        logger.log(whoPutDown + " put down " + leftOrRight + this);
    }

    @Override
    public String toString() {
        return "ChopStick #" + id;
    }
}
