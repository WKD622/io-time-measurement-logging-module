package agh.controllers;

import agh.agents.ILogging;
import agh.agents.MainContainer;
import agh.utils.AgentsFilters;
import agh.utils.FilterItem;
import agh.utils.LogLevelFilters;
import agh.utils.LogMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
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
    @FXML
    private ChoiceBox<FilterItem> logLevelFilterBox;
    @FXML
    private ChoiceBox<FilterItem> agentFilterBox;
    @FXML
    private ChoiceBox<FilterItem> timeFilterBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init!");

        try {
            AgentController ac = MainContainer.cc.getAgent("Logging-agent");
            ILogging logInterface = ac.getO2AInterface(ILogging.class);
            logWindow.textProperty().bind(Bindings.createStringBinding(
                    () -> logInterface.getLog().stream().map(LogMessage::getLog).collect(Collectors.joining("\n")),
                    logInterface.getLog()));

            loadLogLevelFilter();
            loadAgentFilter();
            loadTimeFilter();
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

    @FXML
    void levelFilterAction(ActionEvent event) {
        System.out.println(logLevelFilterBox.getValue());
    }

    @FXML
    void agentFilterAction(ActionEvent event) {
        System.out.println(agentFilterBox.getValue());
    }

    @FXML
    void timeFilterAction(ActionEvent event) {
        System.out.println(timeFilterBox.getValue());
    }

    private void loadLogLevelFilter() {
        for (LogLevelFilters filter : LogLevelFilters.values()) {
            logLevelFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }
        logLevelFilterBox.setValue( logLevelFilterBox.getItems().get(0) );
    }

    private void loadAgentFilter() {
        for (AgentsFilters filter : AgentsFilters.values()) {
            agentFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }
        agentFilterBox.setValue( agentFilterBox.getItems().get(0) );
    }

    private void loadTimeFilter() {
        for (AgentsFilters filter : AgentsFilters.values()) {
            timeFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }
        timeFilterBox.setValue( timeFilterBox.getItems().get(0) );
    }
}
