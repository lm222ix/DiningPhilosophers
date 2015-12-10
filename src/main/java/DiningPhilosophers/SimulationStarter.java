package DiningPhilosophers;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationStarter extends Application implements Runnable {


    private final int numberOfPhilosophers = 5;     //Set number of philosophers. The GUI is not generic and will not work with more or less than 5.
    private ArrayList<ChopStick> chopSticks;
    private ArrayList<Philosopher> philosophers;
    private ExecutorService executer;           //ExecutorService to run philosopher threads.
    private Logger logger;
    ArrayList<philosopherContainer> pcs;

    public SimulationStarter() {
        this.logger = new Logger(true, true);
        philosophers = new ArrayList<>();
        chopSticks = new ArrayList<>();
        executer = Executors.newFixedThreadPool(numberOfPhilosophers);          //One thread per philosopher

    }
    public void run() {     //Implementing runnable, running class this just launches the GUI.
            launch();
    }

    //This method starts the simulation.
    private void runPhilosopherThreads() {
        logger.out.println();
        logger.out.write("- - - - Simulation started - - - -");
        logger.out.println();

        for(Philosopher p : philosophers) {
            //Clear statistics if not empty, this is cleared on each simulation started.
            if(!(p.eatRecords.isEmpty() || p.hungryRecords.isEmpty() || p.thinkRecords.isEmpty())) {
                p.eatRecords.clear();
                p.hungryRecords.clear();
                p.thinkRecords.clear();
            }
                p.setStopPhilosopher(false);
            p.setState(Philosopher.STATE.Initial);
            executer.execute(p);
        }
        //Enter animationTimer wich calls updateLabels each frame.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateLabels();
            }
        }.start();
    }
    private void updateLabels() {
        for(philosopherContainer pc : pcs) {
            pc.stateLabel.setText(pc.owner.getState().toString());
           // updateFills(pc);
        }
    }

    //This method is equivalent to setState in philosopher. Sticking with using that one.
    /*
    public synchronized void updateFills(philosopherContainer pc) {
        STATE previousState = pc.owner.getState();
        if(previousState.equals(STATE.Eating)) {    //If previous state was eating the forks should now be black or "free"
            pc.owner.left.setFill(Paint.valueOf("BLACK"));
            pc.owner.right.setFill(Paint.valueOf("BLACK"));
        }
        if(pc.owner.getState().equals(STATE.Eating)) {
            pc.owner.setFill(Paint.valueOf("RED"));
            pc.owner.moveChopsticks();
        } else if(pc.owner.getState().equals(STATE.Hungry)) {
            pc.owner.setFill(Paint.valueOf("BLUE"));
        }else if(pc.owner.getState().equals(STATE.Thinking)) {
            pc.owner.setFill(Paint.valueOf("GREEN"));
        } else if(pc.owner.getState().equals(STATE.ShuttingDown)) {
            pc.owner.setFill(Paint.valueOf("BLACK"));
        } else if(pc.owner.getState().equals(STATE.Initial)) {
            pc.owner.setFill(Paint.valueOf("BLACK"));
        } else{
            pc.owner.setFill(Paint.valueOf("ORANGE"));   //Orange is unknown, should never happen.
        }
}
*/


    //Method for filling arraylist with chopsticks
    private void fillChops() {
        for(int i = 0; i< numberOfPhilosophers; i++) {
            ChopStick temp = new ChopStick(i,logger);
            chopSticks.add(temp);
        }
    }
    //And philosophers
    private void fillPhilosophers() {
        for(int i = 0; i<numberOfPhilosophers; i++) {
            Philosopher temp = new Philosopher(i, chopSticks.get(i), chopSticks.get((i+1) % numberOfPhilosophers), logger);
            temp.setFill(Paint.valueOf("BLACK"));       //Give them color and size.
            temp.setRadius(50);
            philosophers.add(temp);

        }
    }

    //To shut off the simulation.
    public void shutdownSimulation() throws InterruptedException {
        logger.log("Shutting down..");
        for(philosopherContainer pc : pcs) {
            pc.stateLabel.setText(Philosopher.STATE.ShuttingDown.toString());
        }
        for(Philosopher p : philosophers) {
            p.setState(Philosopher.STATE.ShuttingDown);
            p.stop();
        }

        executer.shutdown();

        //wait for all to shutdown
      while(!executer.isTerminated()) {
            Thread.sleep(500);
       }

        for(Philosopher p : philosophers) {
            p.setState(Philosopher.STATE.Initial);
            p.printStatistics();
        }
    }

    /*Used this for much debugging, should never be more than two eating simoultaneously. Not using it at the moment.
    private void printHowManyEatingRightNow() {
        int count = 0;
        String who = "";
        for(Philosopher p : philosophers) {
            if(p.getState().equals(Philosopher.STATE.Eating)) {
                count++;
                who = who +" "+ p;
            }
        }
        logger.log("Eating currently: " + count + " (" + who + ")");
    }*/


    //GUI below.
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Clear stuff if not empty. this is cleared on each time program is run.(Not each simulation started with button).
        if(!philosophers.isEmpty() && !chopSticks.isEmpty()) {
            philosophers.clear();
            chopSticks.clear();
        }

        //Create chops and philos.
        fillChops();
        fillPhilosophers();

        //Start button.
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //Make new threadpool if the last one is terminated. ("Restart" threads.)
                if (executer.isTerminated()) {
                    executer = Executors.newFixedThreadPool(numberOfPhilosophers);
                }
                runPhilosopherThreads();
            }
        });

        //End button, shuts down sim.
        Button endButton = new Button("End Simulation");
        endButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    shutdownSimulation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        //Box for buttons on bottom of screen.
        HBox buttons = new HBox(10);
        buttons.setPadding(new Insets(10, 10, 10, 10));
        buttons.setStyle("-fx-background-color: #cdcabe;");
        buttons.getChildren().addAll(startButton, endButton);
        buttons.setAlignment(Pos.CENTER);

        //Box for colorGuide also on bottom of screen.
        HBox colorGuide = new HBox(10);
       // colorGuide.setStyle("-fx-background-color: #9790cd;");
        Rectangle red = new Rectangle();
        red.setFill(Paint.valueOf("RED"));
        red.setHeight(25);
        red.setWidth(25);
        Label eatingLabel = new Label("Eating");
        Rectangle green = new Rectangle();
        green.setFill(Paint.valueOf("GREEN"));
        green.setHeight(25);
        green.setWidth(25);
        Label thinkingLabel = new Label("Thinking");
        Rectangle blue = new Rectangle();
        blue.setFill(Paint.valueOf("BLUE"));
        blue.setHeight(25);
        blue.setWidth(25);
        Label hungryLabel = new Label("Hungry");
        Rectangle black = new Rectangle();
        black.setFill(Paint.valueOf("BLACK"));
        black.setHeight(25);
        black.setWidth(25);
        Label initialLabel = new Label("Initial/Shutdown");

        colorGuide.setAlignment(Pos.CENTER_LEFT);
        colorGuide.setPadding(new Insets(10, 10, 10, 10));
        colorGuide.getChildren().addAll(green, thinkingLabel,blue,hungryLabel,red,eatingLabel,black, initialLabel);


        //Box to contain colorguide and buttons
        VBox bottomBox = new VBox();
        bottomBox.getChildren().addAll(colorGuide,buttons);

        //Make a brown circle to represent a table.
        Circle table = new Circle();
        table.setFill(Paint.valueOf("CD4F00"));
        table.setRadius(200);
        table.setCenterX(302.5);
        table.setCenterY(290);

        //Pane that contains the graphical simulation(table,philos and chops).
        Pane pane = new Pane();
        pane.getChildren().add(table);
        placePhilosophers(pane);
        placeChopsticks(pane);

        //Borderpane with our bottomBox and pane placed.
        BorderPane root = new BorderPane();
        root.setCenter(pane);
        root.setBottom(bottomBox);
        primaryStage.setTitle("Dining Philosophers Simulation GUI");
        primaryStage.setScene(new Scene(root, 625, 650));
        primaryStage.show();
    }


    /*
    Using a class philosopherContainer here, contains a ID label, a state label,
    the philosopher(circle) and a box to keep it all in. This makes it easier to update the labels in the GUI
    and its easier to reach a specific philosophers GUI layout and change it.
     */
    public class philosopherContainer {

        public Label IDLabel;
        public Label stateLabel;
        public HBox box;
        public Philosopher owner;

        public  philosopherContainer(Philosopher p) {
            owner = p;
            box = new HBox();
            IDLabel = new Label(p.toString());
            stateLabel = new Label(p.getState().toString());

            VBox textBox = new VBox();
            textBox.setAlignment(Pos.CENTER);
            textBox.getChildren().addAll(IDLabel, stateLabel);
            //Placing the textbox so it is not in the way. This solution is for 5 philosophers only.
            if(p.getID() == 1) {
                textBox.setTranslateX(-100);
                textBox.setTranslateY(-75);
            }else if(p.getID() == 2) {
                textBox.setTranslateX(-100);
                textBox.setTranslateY(75);
            } else if(p.getID() == 4) {
                textBox.setTranslateX(-50);
                textBox.setTranslateY(75);
            }

            box.setPrefSize(200, 100);
            box.getChildren().addAll(p,textBox);
        }

    }

    // Places philosopher circles on the pane.
    private void placePhilosophers(Pane pane) {
        pcs  = new ArrayList<>();
        for(int i = 0; i<numberOfPhilosophers; i++) {
            philosopherContainer pc = new philosopherContainer(philosophers.get(i));
            pcs.add(pc);
            pane.getChildren().add(pc.box);
        }


        //positioning of boxes(containing philosopher circles, among other things). (note to self: use scenebuilder next time).
        pcs.get(0).box.setLayoutX(250);
        pcs.get(0).box.setLayoutY(15);
        pcs.get(4).box.setLayoutX(455);
        pcs.get(4).box.setLayoutY(180);
        pcs.get(3).box.setLayoutX(395);
        pcs.get(3).box.setLayoutY(390);
        pcs.get(2).box.setLayoutX(100);
        pcs.get(2).box.setLayoutY(380);
        pcs.get(1).box.setLayoutX(45);
        pcs.get(1).box.setLayoutY(180);
    }

    //Positioning the chopsticks
    private void placeChopsticks(Pane pane) {
        chopSticks.get(0).setLayoutX(400);
        chopSticks.get(0).setLayoutY(130);
        chopSticks.get(0).setRotate(40);
        chopSticks.get(1).setLayoutX(195);
        chopSticks.get(1).setLayoutY(130);
        chopSticks.get(1).setRotate(-40);
        chopSticks.get(2).setLayoutX(140);
        chopSticks.get(2).setLayoutY(290);
        chopSticks.get(2).setRotate(75);
        chopSticks.get(3).setLayoutX(300);
        chopSticks.get(3).setLayoutY(410);
        chopSticks.get(4).setLayoutX(450);
        chopSticks.get(4).setLayoutY(300);
        chopSticks.get(4).setRotate(100);

        pane.getChildren().addAll(chopSticks);
    }


}
