package agh.controllers;

import agh.agents.*;
import agh.utils.Agents;
import agh.utils.LogLevel;
import agh.utils.View;
import jade.wrapper.ControllerException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class StopwatchController implements Initializable {

    public Button start;
    public Button stop;
    public Button next;
    public Button log;
    public Button logs;
    public Button quit;
    public Text timeRemaining;
    public Label timeRemainingLabel;
    public Text timeElapsed;
    public AnchorPane pane;
    public TextArea parameters;
    public TextArea info;
    public TextField logMessage;

    private Stage previousStage;

    private final TimeAgent timeAgent = new TimeAgent();
    private ITime time;

    private final Map<TimeAgent.ProductionStage, Long> stages = new LinkedHashMap<>();
    private SimpleObjectProperty<Map.Entry<TimeAgent.ProductionStage, Long>> currentStage =
            new SimpleObjectProperty<>(null);
    private Iterator<Map.Entry<TimeAgent.ProductionStage, Long>> stagesIterator;
    private SimpleBooleanProperty measuring = new SimpleBooleanProperty(false);

    private final MapChangeListener<? super TimeAgent.ProductionStage, ? super Long> timesListener = change -> {
        if (change.getKey() == currentStage.get().getKey()) {
            setTimes(change.getValueAdded());
        }
    };

    private boolean started = false;

    private void setTimes(long timeElapsed) {
        this.timeElapsed.setText(prepareTime(timeElapsed));
        Long time = currentStage.get().getValue();
        if (time != null) {
            long timeRemaining = time - timeElapsed;
            this.timeRemainingLabel.setVisible(true);
            this.timeRemaining.setText(prepareTime(timeRemaining));
        } else {
            this.timeRemainingLabel.setVisible(false);
        }
    }

    private String prepareTime(long time) {
        String format;
        Duration duration = Duration.ofMillis(time);
        Duration oneHour = Duration.ofHours(1);

        if (duration.abs().compareTo(oneHour) < 0) {
            format = "mm 'min' ss 's'";
        } else {
            format = "HH 'h' mm 'min' ss 's'";
        }

        if (duration.isNegative()) {
            format = "–" + format;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(LocalTime.ofSecondOfDay(duration.abs().get(ChronoUnit.SECONDS)));
    }

    private String prepareMap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    private void clearStage() {
        info.clear();
        timeRemaining.setText("");
        timeElapsed.setText("");
    }

    private void nextStage() {
        if (stagesIterator.hasNext()) {
            currentStage.set(stagesIterator.next());
        } else {
            currentStage.set(null);
        }
    }

    private void showStage() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Aktualny etap", currentStage.get().getKey().toString());
        String text = prepareMap(info);
        this.info.setText(text);
    }

    private void appendStageDetails(String previousText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Map<String, String> info = new LinkedHashMap<>();
        LocalDateTime start = LocalDateTime.now();

        info.put("Czas rozpoczęcia etapu", formatter.format(start));

        Long duration = currentStage.get().getValue();
        if (duration != null) {
            LocalDateTime end = LocalDateTime.from(start).plus(duration, ChronoUnit.MILLIS);
            info.put("Przewidywany czas zakończenia etapu", formatter.format(end));
        }

        this.info.setText(previousText + "\n" + prepareMap(info));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (!started) {
                timeAgent.setup();
                timeAgent.getObservableTimes().addListener(timesListener);
                time = MainContainer.cc.getAgent("time").getO2AInterface(ITime.class);
            } else {
                showStage();
            }
//            System.out.println("started=" + started);
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    void setScene(Stage stage, Parent root) {
        previousStage = stage;
        stage.setScene(new Scene(root));
        stage.setTitle("Stoper");
        stage.show();
    }

    void setStages(Map<TimeAgent.ProductionStage, Long> stages) {
        timeAgent.productionStages().forEach(s -> this.stages.put(s, null));
        this.stages.putAll(stages);
    }

    void showParameters(Map<String, String> parameters) {
        this.parameters.setText(prepareMap(parameters));
    }

    void initializeProperties() {
        if (started) {
            return;
        }

        start.disableProperty().bind(Bindings.createBooleanBinding(() ->
                measuring.get(),
                measuring));
        stop.disableProperty().bind(Bindings.createBooleanBinding(() ->
                !measuring.get(),
                measuring));
        next.disableProperty().bind(Bindings.createBooleanBinding(() ->
                stagesIterator == null || !stagesIterator.hasNext(),
                currentStage));
    }

    void initializeIterator() {
        if (!started) {

            stagesIterator = stages.entrySet().iterator();
            currentStage.set(stagesIterator.next());
            showStage();
        }
    }

    @FXML
    public void handleShowLogs() {
        try {
            time.stop(currentStage.get().getKey());
            measuring.set(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logger.fxml"));
            Parent root = loader.load();
            LoggerController controller = loader.getController();
            controller.setPreviousView(View.STOPWATCH);
            controller.setScene((Stage) pane.getScene().getWindow(), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack() {
//        controller.handleBack(pane);
        try {
            time.stop(currentStage.get().getKey());
            measuring.set(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/predict.fxml"));
            Parent root = loader.load();
            PredictController controller = loader.getController();
            controller.setScene(previousStage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleStart(ActionEvent actionEvent) {
        if (!started) {
            started = true;
        }
        appendStageDetails(info.getText());

        measuring.set(true);
        time.start(currentStage.get().getKey());
    }

    public void handleStop(ActionEvent actionEvent) {
        measuring.set(false);
        time.stop(currentStage.get().getKey());
    }

    public void handleNext(ActionEvent actionEvent) {
        measuring.set(false);
        time.stop(currentStage.get().getKey());

        clearStage();
        nextStage();
        showStage();
    }

    public void handleQuit(ActionEvent actionEvent) {
        measuring.set(false);
        time.stop(currentStage.get().getKey());
        stages.forEach((k, v) -> time.reset(k));

        started = false;
        clearStage();
        handleBack();
    }

    public void handleLog(ActionEvent actionEvent) {
        try {
            ILogging logging = MainContainer.cc.getAgent("Logging-agent").getO2AInterface(ILogging.class);
            logging.sendMessage(LogLevel.INFO, Agents.USER, logMessage.getText());
            logMessage.clear();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
