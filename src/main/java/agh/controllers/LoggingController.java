package agh.controllers;

import agh.agents.ILogging;
import agh.agents.InterfaceUI;
import agh.agents.LoggingAgent;
import agh.agents.MainContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoggingController implements Initializable {

    private Controller controller = Controller.getInstance();

    //public ObservableList<String> logs = FXCollections.observableArrayList();
    public SimpleStringProperty log = new SimpleStringProperty("");
    @FXML
    private Pane LoggingPane;
    @FXML
    TextArea textArea;
    @FXML
    protected void handleBack(ActionEvent event) {
        controller.handleBack(LoggingPane);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.textProperty().bind(log);
        try {
            AgentController ac = MainContainer.cc.getAgent("Logging-agent");
            ILogging logInterface = ac.getO2AInterface(ILogging.class);
            textArea.textProperty().bind(logInterface.getLog());
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }

    public void setScene(Stage stage, Parent root) {
        controller.setScene(stage, root, "Logi");
    }
}
