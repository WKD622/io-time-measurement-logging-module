package agh.controllers;

import agh.agents.TimeAgent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ResourceBundle;

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
        SimpleStringProperty stopers;
        while (true) {
            stopers = timeAgent.getLog();
            TimeAgent.MeasuredTimesParser measuredTimesParser = new TimeAgent.MeasuredTimesParser(stopers);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Set<TimeAgent.StoperType> stopersSet = measuredTimesParser.allContainedStopers();
            for (TimeAgent.StoperType stoperType : new ArrayDeque<>(stopersSet)) {
                Double time = measuredTimesParser.getTimeForStoper(stoperType);
            }

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
