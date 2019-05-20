package agh.controllers;

import agh.agents.ILogging;
import agh.agents.MainContainer;
import agh.utils.*;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoggerController implements Initializable {
    private Controller controller = Controller.getInstance();
    private boolean loading = false;

    @FXML
    private Pane LoggerPane;
    @FXML
    private TableView<LogMessage> tableView;
    @FXML
    private ChoiceBox<FilterItem> logLevelFilterBox;
    @FXML
    private ChoiceBox<FilterItem> agentFilterBox;

    private FilteredList<LogMessage> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AgentController ac = MainContainer.cc.getAgent("Logging-agent");
            ILogging logInterface = ac.getO2AInterface(ILogging.class);
            list = new FilteredList<>(logInterface.getLog());

            tableView.setItems(list);

            loadTable();
            loadFilters();
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
        controller.handleBack(LoggerPane);
    }

    @FXML
    void levelFilterAction(ActionEvent event) { updateFiltering(); }

    @FXML
    void agentFilterAction(ActionEvent event) { updateFiltering(); }

    @FXML
    void handleSave(ActionEvent event) {
        try{
            FileWriter fw = new FileWriter("logs.txt");
            list.forEach(e -> saveLine(fw, e.getMessage()));
            fw.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    private void saveLine(FileWriter fw, String text){
        try {
            fw.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTable(){
        TableColumn<LogMessage,String> timeCol = new TableColumn<>("Czas");
        timeCol.setCellValueFactory(new PropertyValueFactory("time"));
        TableColumn<LogMessage,String> agentCol = new TableColumn<>("Agent");
        agentCol.setCellValueFactory(new PropertyValueFactory("agent"));
        TableColumn<LogMessage,String> levelCol = new TableColumn<>("Poziom");
        levelCol.setCellValueFactory(new PropertyValueFactory("level"));
        TableColumn<LogMessage,String> messageCol = new TableColumn<>("Wiadomość");
        messageCol.setCellValueFactory(new PropertyValueFactory("message"));

        tableView.getColumns().setAll(timeCol, agentCol, levelCol, messageCol);
    }

    private void loadFilters(){
        loading = true;
        for (LogLevel filter : LogLevel.values()) {
            logLevelFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        for (Agents filter : Agents.values()) {
            agentFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        agentFilterBox.getSelectionModel().selectFirst();
        logLevelFilterBox.getSelectionModel().selectFirst();
        loading = false;
    }

    private boolean levelPredicate(LogMessage m) {
        if(logLevelFilterBox.getValue().key == LogLevel.EMPTY)
            return true;
        else
            return m.getLevel().equals(logLevelFilterBox.getValue().key);
    }
    private boolean agentPredicate(LogMessage m) {
        if(agentFilterBox.getValue().key == Agents.EMPTY)
            return true;
        else
            return m.getAgent().equals(agentFilterBox.getValue().key);
    }


    private void updateFiltering(){
        if(loading)
            return;
        list.setPredicate(s-> levelPredicate(s) && agentPredicate(s) );
    }
}
