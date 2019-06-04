package agh.controllers;

import agh.agents.ILogging;
import agh.agents.MainContainer;
import agh.utils.*;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoggerController implements Initializable {
    private Controller controller = Controller.getInstance();
    private boolean loading = false;
    private String LOG_FILENAME = "logs.txt";
    private View previousView;

    @FXML
    private Pane LoggerPane;
    @FXML
    private TableView<LogMessage> tableView;
    @FXML
    private CheckComboBox<FilterItem> logLevelFilterBox;
    @FXML
    private CheckComboBox<FilterItem> agentFilterBox;
    @FXML
    private CheckComboBox<FilterItem> typeFilterBox;
    @FXML
    private CheckComboBox<FilterItem> logSeverityFilterBox;

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
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
//        controller.handleBack(LoggerPane);
        try {
            if (previousView == View.MAIN) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                Parent root = loader.load();
                MainController controller = loader.getController();
                controller.setScene((Stage) LoggerPane.getScene().getWindow(), root);
            } else if (previousView == View.STOPWATCH) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/stopwatch.fxml"));
                Parent root = loader.load();
                StopwatchController controller = loader.getController();
                controller.setScene((Stage) LoggerPane.getScene().getWindow(), root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent _event) {
        try{
            FileWriter fw = new FileWriter(LOG_FILENAME);
            list.forEach(e -> saveLine(fw, e.getLog()));
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
        TableColumn<LogMessage,String> levelCol = new TableColumn<>("Rodzaj");
        levelCol.setCellValueFactory(new PropertyValueFactory("level"));
        TableColumn<LogMessage,String> severityCol = new TableColumn<>("Poziom");
        severityCol.setCellValueFactory(new PropertyValueFactory("severity"));
        TableColumn<LogMessage,String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        TableColumn<LogMessage,String> messageCol = new TableColumn<>("Wiadomość");
        messageCol.setCellValueFactory(new PropertyValueFactory("message"));

        tableView.getColumns().setAll(timeCol, agentCol, levelCol, severityCol, typeCol, messageCol);
    }

    private void loadFilters(){
        loading = true;
        for (LogLevel filter : LogLevel.values()) {
            logLevelFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        for (Agents filter : Agents.values()) {
            agentFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        for ( LogMessageType filter : LogMessageType.values()) {
            typeFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        for ( LogSeverity filter : LogSeverity.values()) {
            logSeverityFilterBox.getItems().add(new FilterItem<>(filter, filter.toString()));
        }

        loading = false;

        initCheckComboBox(agentFilterBox);
        initCheckComboBox(logLevelFilterBox);
        initCheckComboBox(typeFilterBox);
        initCheckComboBox(logSeverityFilterBox);
    }

    private void initCheckComboBox(CheckComboBox<FilterItem> box){
        box.getCheckModel().getCheckedItems().addListener((ListChangeListener<FilterItem>) c -> updateFiltering());
        box.getCheckModel().checkAll();
    }

    private boolean levelPredicate(LogMessage m) {
        return logLevelFilterBox.getCheckModel().getCheckedItems().stream().map(f -> f.key).anyMatch(k -> k == m.getLevel());
    }

    private boolean agentPredicate(LogMessage m) {
        return agentFilterBox.getCheckModel().getCheckedItems().stream().map(f -> f.key).anyMatch(k -> k == m.getAgent());
    }

    private boolean typePredicate(LogMessage m) {
        return typeFilterBox.getCheckModel().getCheckedItems().stream().map(f -> f.key).anyMatch(k -> k == m.getType());
    }

    private boolean severityPredicate(LogMessage m) {
        return logSeverityFilterBox.getCheckModel().getCheckedItems().stream().map(f -> f.key).anyMatch(k -> k == m.getSeverity());
    }

    private void updateFiltering(){
        if(loading)
            return;
        list.setPredicate(s-> levelPredicate(s) && agentPredicate(s) && typePredicate(s) && severityPredicate(s));
    }

    public void setPreviousView(View previousView) {
        this.previousView = previousView;
    }
}
