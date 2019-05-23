package agh.controllers;

import agh.agents.*;
import agh.utils.Agents;
import agh.utils.LogLevel;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.collections.MapChangeListener;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StoperController implements Initializable {

    public Button startTimer;
    public Button stopTimer;
    public Button nextStage;
    public Button logButton;
    public Button showLogs;
    public Button quit;
    public Text timeRemaining;
    public Text timeElapsed;
    public AnchorPane stoperPane;
    public TextArea parametersTextArea;
    public TextArea infoTextArea;
    public TextField logField;

    private Controller controller = Controller.getInstance();

    private Map<TimeAgent.StoperType, Long> stages;
    private Map.Entry<TimeAgent.StoperType, Long> currentStage;
    private Iterator<Map.Entry<TimeAgent.StoperType, Long>> stagesIterator;

    private TimeAgent timeAgent;
    private ITime time;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            timeAgent = new TimeAgent();
            timeAgent.setup();

            AgentController ac = MainContainer.cc.getAgent("time");
            time = ac.getO2AInterface(ITime.class);

            stages = new LinkedHashMap<>();
//            completedStages = new LinkedHashMap<>();
            currentStage = null;

            timeAgent.getObservableTimes().addListener((MapChangeListener<TimeAgent.StoperType, Long>) change -> {
//            TimeAgent.StoperType key = change.getKey();
                if (change.getKey() == currentStage.getKey()) {
                    setTimes(change.getValueAdded());
                }
                System.out.println("changed");
                System.out.println(change);
            });
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private String prepareTimeText(long time) {
        String format;
        Date date = new Date(time);
        Date ONE_HOUR = new Date(60 * 60 * 1000);

        if (date.before(ONE_HOUR)) {
            format = "mm min ss s";
        } else {
            format = "HH h mm min ss s";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    private void setTimes(long timeElapsed) {
        this.timeElapsed.setText(prepareTimeText(timeElapsed));
        Long time = currentStage.getValue();
        if (time != null) {
            long timeRemaining = time - timeElapsed;
            this.timeRemaining.setText(prepareTimeText(timeRemaining));
        }
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

    private String prepareText(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    public void addParameters(Map<String, String> parameters) {
        String text = prepareText(parameters);
        parametersTextArea.setText(text);
    }

    public void setStages(Map<TimeAgent.StoperType, Long> stages) {
        timeAgent.getProductionStages().forEach(s -> this.stages.put(s, null));
        this.stages.putAll(stages);
        stagesIterator = this.stages.entrySet().iterator();
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("Stoper");
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    public void start() {
        nextStage();
    }

    private void nextStage() {
        currentStage = stagesIterator.next();

        Map<String, String> info = new LinkedHashMap<>();
        info.put("Aktualny etap", currentStage.getKey().toString());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        Date startDate = new Date();
        info.put("Czas rozpoczęcia etapu", formatter.format(startDate));

        Long duration = currentStage.getValue();
        if (duration != null) {
            Date endDate = new Date(startDate.getTime() + duration);
            info.put("Przewidywany czas zakończenia etapu", formatter.format(endDate));
        }

        String text = prepareText(info);
        infoTextArea.setText(text);

        if (!stagesIterator.hasNext()) {
            nextStage.setDisable(true);
        }
    }

    @FXML
    public void handleBack() {
        controller.handleBack(stoperPane);
    }

    public void handleStartTimer(ActionEvent actionEvent) {
        time.startTimer(currentStage.getKey());
        startTimer.setDisable(true);
        stopTimer.setDisable(false);
    }

    public void handleStopTimer(ActionEvent actionEvent) {
        time.stopTimer(currentStage.getKey());
        startTimer.setDisable(false);
        stopTimer.setDisable(true);
    }

    public void handleNextStage(ActionEvent actionEvent) {
        time.stopTimer(currentStage.getKey());
        nextStage();
        startTimer.setDisable(false);
        stopTimer.setDisable(true);
    }

    public void handleQuit(ActionEvent actionEvent) {
        time.stopTimer(currentStage.getKey());
        startTimer.setDisable(true);
        stopTimer.setDisable(true);
        handleBack();
    }

    public void handleLog(ActionEvent actionEvent) {
        try {
            ILogging logging = MainContainer.cc.getAgent("Logging-agent").getO2AInterface(ILogging.class);
            logging.sendMessage(LogLevel.INFO, Agents.TIME_AGENT, logField.getText());
            logField.clear();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
