package DiningPhilosophers;/**
 * Created by Ludde on 2015-11-30.
 */

import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DiningGUI extends Application implements Runnable{

    public void run() {
        launch();
    }

    private SimulationStarter s;
    private Logger logger;
    @Override
    public void start(Stage primaryStage) {
        logger = new Logger(true,true);
        //s = new SimulationStarter(logger);



        Label tf = new Label("Input time in seconds");
        final NumberField simulationLengthInput = new NumberField();
        simulationLengthInput.setText("9999");

        simulationLengthInput.setPrefSize(40, 20);

        Button startButton = new Button("Start Simulation");
       /* startButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    //s.runSimulation(Integer.valueOf(simulationLengthInput.getText()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/

        Button endButton = new Button("End Simulation");
        endButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    s.shutdownSimulation();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        VBox input = new VBox();
        input.getChildren().addAll(tf, simulationLengthInput);

        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(10, 10, 10, 10));
        bottomBox.setStyle("-fx-background-color: #cdcabe; -fx-text-fill: white;");
        bottomBox.getChildren().addAll(startButton, input, endButton);
        bottomBox.setAlignment(Pos.CENTER);



        BorderPane root = new BorderPane();
        root.setBottom(bottomBox);
        primaryStage.setTitle("Dining Philosophers Simulation GUI");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.show();
    }

    private class NumberField extends TextField
    {

        @Override
        public void replaceText(int start, int end, String text)
        {
            if (validate(text))
            {
                super.replaceText(start, end, text);
            }
        }

        @Override
        public void replaceSelection(String text)
        {
            if (validate(text))
            {
                super.replaceSelection(text);
            }
        }

        private boolean validate(String text)
        {
            return ("".equals(text) || text.matches("[0-9]") && getText().length()<4);
        }
    }
}
