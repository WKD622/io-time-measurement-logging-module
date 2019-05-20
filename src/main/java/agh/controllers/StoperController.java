package agh.controllers;

import agh.agents.TimeAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

public class StoperController implements Initializable {
    private Controller controller = Controller.getInstance();

    @FXML
    public AnchorPane stoperPane;
    @FXML
    private TextArea parametersTextArea;
    @FXML
    private TextArea infoTextArea;
    @FXML
    private TextField logField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TimeAgent timeAgent = new TimeAgent();
        timeAgent.setup();

        Thread T = new Thread(() -> {
            HashMap<TimeAgent.StoperType, Double> stopers = new HashMap<>();

            while (true) {
                try {
                    while (true) {
//                        stopers = timeAgent.getLog();
                        System.out.println(timeAgent.getLog());
//                        System.out.println(stopers.toString());
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        T.start();
//        addBehaviour(new CyclicBehaviour(this) {
//            public void action() {
//                ACLMessage msg = myAgent.receive();
//                if (msg != null) {
//                    parseMessage(msg.getContent());
//                } else {
//                    block();
//                }
//            }
//        });

//        while (true) {
//            for
//            TimeAgent.MeasuredTimesParser measuredTimesParser = new TimeAgent.MeasuredTimesParser(stopers);

//            Set<TimeAgent.StoperType> stopersSet = measuredTimesParser.allContainedStopers();
//            for (TimeAgent.StoperType stoperType : new ArrayDeque<>(stopersSet)) {
//                Double time = measuredTimesParser.getTimeForStoper(stoperType);
//            }

//        }
    }

    @FXML
    public void handleShowLogs() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logger.fxml"));
        Parent root;
        try {
            root = loader.load();
            LoggerController controller = loader.getController();
            controller.setScene((Stage) stoperPane.getScene().getWindow(), root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("Stoper");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void handleBack() {
        controller.handleBack(stoperPane);
    }
}
