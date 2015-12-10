package DiningPhilosophers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ludde on 2015-11-27.
 */
public class SimulationStarter extends Application implements Runnable {

    private final int numberOfPhilosophers = 5;
    private ArrayList<ChopStick> chopSticks;
    private ArrayList<Philosopher> philosophers;
    private ExecutorService executer;
    private Logger logger;
    ArrayList<philosopherContainer> pcs;

    public SimulationStarter() {
        this.logger = new Logger(true, true);
        philosophers = new ArrayList<Philosopher>();
        chopSticks = new ArrayList<ChopStick>();
        executer = Executors.newFixedThreadPool(numberOfPhilosophers);

    }
    public void run() {
        chopSticks.clear();
        philosophers.clear();
            launch();
    }

    //Helper methods
    private void runPhilosopherThreads() {

        for(Philosopher p : philosophers) {
            if(!(p.eatRecords.isEmpty() || p.hungryRecords.isEmpty() || p.thinkRecords.isEmpty())) {
                p.eatRecords.clear();
                p.hungryRecords.clear();
                p.thinkRecords.clear();
            }
                p.setStopPhilosopher(false);
            p.setState(Philosopher.STATE.Initial);
            executer.execute(p);
        }
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
        }
    }

    private void fillChops() {
        for(int i = 0; i< numberOfPhilosophers; i++) {
            ChopStick temp = new ChopStick(i,logger);
            chopSticks.add(temp);
        }
    }

    private void fillPhilosophers() {
        for(int i = 0; i<numberOfPhilosophers; i++) {
            Philosopher temp = new Philosopher(i, chopSticks.get(i), chopSticks.get((i+1) % numberOfPhilosophers), logger);
            philosophers.add(temp);
        }
    }

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
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        /*timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent actionEvent) {
                                System.out.println("smthhappend");
                                printHowManyEatingRightNow();
                                updateLabels();
                            }
                        }
                )
        );*/

        if(!philosophers.isEmpty() && !chopSticks.isEmpty()) {
            philosophers.clear();
            chopSticks.clear();
        }
        fillChops();
        fillPhilosophers();
       // timeline.play();

        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (executer.isTerminated()) {
                    executer = Executors.newFixedThreadPool(numberOfPhilosophers);
                }
                runPhilosopherThreads();
            }
        });

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



        HBox buttons = new HBox(10);
        buttons.setPadding(new Insets(10, 10, 10, 10));
        buttons.setStyle("-fx-background-color: #cdcabe;");
        buttons.getChildren().addAll(startButton, endButton);
        buttons.setAlignment(Pos.CENTER);

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
        blue.setFill(Paint.valueOf("BLACK"));
        blue.setHeight(25);
        blue.setWidth(25);
        Label initialLabel = new Label("Initial/Shutdown");

        colorGuide.setPadding(new Insets(10, 10, 10, 10));
        colorGuide.getChildren().addAll(green, thinkingLabel,blue,hungryLabel,red,eatingLabel,black,initialLabel);


        VBox bottomBox = new VBox();
        bottomBox.getChildren().addAll(colorGuide,buttons);

        Circle table = new Circle();
        table.setFill(Paint.valueOf("CD4F00"));
        table.setRadius(200);
        table.setCenterX(302.5);
        table.setCenterY(290);


        Pane pane = new Pane();
        for(Philosopher p : philosophers) {
            p.setFill(Paint.valueOf("BLACK"));
            p.setRadius(50);
        }

        pane.getChildren().add(table);
        placePhilosophers(pane);
        placeChopsticks(pane);


        BorderPane root = new BorderPane();
        root.setCenter(pane);
        root.setBottom(bottomBox);
        primaryStage.setTitle("Dining Philosophers Simulation GUI");
        primaryStage.setScene(new Scene(root, 625, 650));
        primaryStage.show();
    }

    private void placePhilosophers(Pane pane) {
        pcs  = new ArrayList<philosopherContainer>();

        for(int i = 0; i<numberOfPhilosophers; i++) {
            philosopherContainer pc = new philosopherContainer(philosophers.get(i));
            philosophers.get(i).setPc(pc);
            pcs.add(pc);
            pane.getChildren().add(pc.box);
        }

        //positioning
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
}
