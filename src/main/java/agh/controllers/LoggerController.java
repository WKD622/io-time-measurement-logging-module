package agh.controllers;

import agh.agents.ILogging;
import agh.agents.MainContainer;
import agh.utils.LogMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LoggerController implements Initializable {
    private Controller controller = Controller.getInstance();

    @FXML
    private Pane LoggerPane;
    @FXML
    private TextArea logWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init!");

        try {
            AgentController ac = MainContainer.cc.getAgent("Logging-agent");
            ILogging logInterface = ac.getO2AInterface(ILogging.class);
            logWindow.textProperty().bind(Bindings.createStringBinding(
                    () -> logInterface.getLog().stream().map(LogMessage::getLog).collect(Collectors.joining("\n")),
                    logInterface.getLog()));
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("Logger");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void handleBack() {
        System.out.println("Back");
        controller.handleBack(LoggerPane);
    }
}
