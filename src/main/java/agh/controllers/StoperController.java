package agh.controllers;

import agh.agents.TimeAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StoperController implements Initializable {

    public Button startTimer;
    public Button stopTimer;
    public Button nextStage;
    public Button logButton;
    public Button showLogs;
    public Text timeRemaining;
    public Text timeElapsed;
    public Button quit;
    private Controller controller = Controller.getInstance();

    private Map<String, String> parameters;

    //
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

//        Thread T = new Thread(() -> {
//            HashMap<TimeAgent.StoperType, Double> stopers = new HashMap<>();
//
//            while (true) {
//                try {
//                    while (true) {
////                        stopers = timeAgent.getLog();
//                        System.out.println(timeAgent.getLog());
////                        System.out.println(stopers.toString());
//                        Thread.sleep(5000);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        T.start();
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

    public void setProductionParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        final String text = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
        parametersTextArea.setText(text);
//        System.out.println(this.parameters);
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

    public void handleStartTimer(ActionEvent actionEvent) {
    }

    public void handleStopTimer(ActionEvent actionEvent) {
    }

    public void handleNextStage(ActionEvent actionEvent) {
    }

    public void handleQuit(ActionEvent actionEvent) {
    }

    public void handleLog(ActionEvent actionEvent) {
    }
}
