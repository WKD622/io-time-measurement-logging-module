package agh.agents;

import jade.core.Agent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class TimeAgent extends Agent implements ITime {

    private RealTimeStopwatch stoperWytapianie = new RealTimeStopwatch(StopwatchType.WYTAPIANIE);
    private RealTimeStopwatch stoperKrzepiniecie = new RealTimeStopwatch(StopwatchType.KRZEPNIECIE);
    private RealTimeStopwatch stoperStudzenie1 = new RealTimeStopwatch(StopwatchType.STUDZENIE1);
    private RealTimeStopwatch stoperPodgrzanie1 = new RealTimeStopwatch(StopwatchType.PODGRZANIE1);
    private RealTimeStopwatch stoperStudzenie2 = new RealTimeStopwatch(StopwatchType.STUDZENIE2);
    private RealTimeStopwatch stoperPodgrzanie2 = new RealTimeStopwatch(StopwatchType.PODGRZANIE2);
    private RealTimeStopwatch stoperUszlachetnianie = new RealTimeStopwatch(StopwatchType.USZLACHETNIANIE);
    private CpuTimeStopwatch stoperForestCpu = new CpuTimeStopwatch(StopwatchType.LEARNING_FOREST_CPU);
    private UserTimeStopwatch stoperForestUser = new UserTimeStopwatch(StopwatchType.LEARNING_FOREST_USER);
    private RealTimeStopwatch stoperForestReal = new RealTimeStopwatch(StopwatchType.LEARNING_FOREST_REAL);
    private CpuTimeStopwatch stoperM5pCpu = new CpuTimeStopwatch(StopwatchType.LEARNING_M5P_CPU);
    private UserTimeStopwatch stoperM5pUser = new UserTimeStopwatch(StopwatchType.LEARNING_M5P_USER);
    private RealTimeStopwatch stoperM5pReal = new RealTimeStopwatch(StopwatchType.LEARNING_M5P_REAL);
    private CpuTimeStopwatch stoperMlpCpu = new CpuTimeStopwatch(StopwatchType.LEARNING_MLP_CPU);
    private UserTimeStopwatch stoperMlpUser = new UserTimeStopwatch(StopwatchType.LEARNING_MLP_USER);
    private RealTimeStopwatch stoperMlpReal = new RealTimeStopwatch(StopwatchType.LEARNING_MLP_REAL);
    private CpuTimeStopwatch stoperVoteCpu = new CpuTimeStopwatch(StopwatchType.LEARNING_VOTE_CPU);
    private UserTimeStopwatch stoperVoteUser = new UserTimeStopwatch(StopwatchType.LEARNING_VOTE_USER);
    private RealTimeStopwatch stoperVoteReal = new RealTimeStopwatch(StopwatchType.LEARNING_VOTE_REAL);
    private static HashMap<StopwatchType, Stopwatch> stopwatches = new HashMap<>();
    private static ObservableMap<StopwatchType, Long> observableTimes = FXCollections.observableHashMap();

    public TimeAgent() {
        Arrays.asList(
                stoperWytapianie,
                stoperKrzepiniecie,
                stoperStudzenie1,
                stoperPodgrzanie1,
                stoperStudzenie2,
                stoperPodgrzanie2,
                stoperUszlachetnianie,
                stoperForestCpu,
                stoperForestUser,
                stoperForestReal,
                stoperM5pCpu,
                stoperM5pUser,
                stoperM5pReal,
                stoperMlpCpu,
                stoperMlpUser,
                stoperMlpReal,
                stoperVoteCpu,
                stoperVoteUser,
                stoperVoteReal
        ).forEach(s -> stopwatches.put(s.getType(), s));
    }

    public ObservableMap<StopwatchType, Long> getObservableTimes() {
        return observableTimes;
    }

    public void setup() {
        registerO2AInterface(ITime.class, this);

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    for (Map.Entry<StopwatchType, Stopwatch> e : stopwatches.entrySet()) {
                        Stopwatch stopwatch = e.getValue();
                        if (stopwatch.isMeasuring()) {
                            Platform.runLater(() -> observableTimes.put(e.getKey(), stopwatch.time()));
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        T.start();
    }

    @Override
    public void start(StopwatchType type) {
        stopwatches.get(type).start();
    }

    @Override
    public void stop(StopwatchType type) {
        stopwatches.get(type).stop();
    }

    @Override
    public void reset(StopwatchType type) {
        stopwatches.get(type).reset();
    }

    @Override
    public boolean isMeasuring(StopwatchType type) {
        Boolean result = null;
        try {
            Callable<Boolean> callable = () -> stopwatches.get(type).isMeasuring();
            FutureTask<Boolean> futureTask = new FutureTask<>(callable);
            Platform.runLater(futureTask);
            result = futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public long time(StopwatchType type) {
        return stopwatches.get(type).time();
    }

    @Override
    public void initializeMeasurement(StopwatchType type, Supplier<Long> measurement) {
        ((LearningTimeStopwatch) stopwatches.get(type)).initializeMeasurement(measurement);
    }

    abstract class Stopwatch {

        StopwatchType type;
        boolean measuring;
        boolean ended;
        long start;
        long stop;

        Supplier<Long> measurement;

        Stopwatch(StopwatchType type, Supplier<Long> measurement) {
            this.type = type;
            this.measurement = measurement;

            this.measuring = false;
            reset();
        }

        Stopwatch(StopwatchType type) {
            this.type = type;
            this.measuring = false;
        }

        StopwatchType getType() {
            return type;
        }

        boolean isMeasuring() {
            return measuring;
        }

        boolean isEnded() {
            return ended;
        }

        @Override
        public String toString() {
            return time() / 1000.0 + "s";
        }

        void reset() {
            start = measurement.get();
            ended = true;
        }

        void start() {
            if (ended) {
                start = measurement.get();
                ended = false;
            } else {
                start = measurement.get() - (stop - start);
            }
            measuring = true;
        }

        void stop() {
            stop = measurement.get();
            measuring = false;
        }

        long time() {
            return measurement.get() - start;
        }
    }

    class RealTimeStopwatch extends Stopwatch {

        RealTimeStopwatch(StopwatchType type) {
            super(type, System::currentTimeMillis);
        }
    }

    abstract class LearningTimeStopwatch extends Stopwatch {

        LearningTimeStopwatch(StopwatchType type) {
            super(type);
        }

        void initializeMeasurement(Supplier<Long> measurement) {
            this.measurement = measurement;
            reset();
        }
    }

    class CpuTimeStopwatch extends LearningTimeStopwatch {

        CpuTimeStopwatch(StopwatchType type) {
            super(type);
        }
    }

    class UserTimeStopwatch extends LearningTimeStopwatch {

        UserTimeStopwatch(StopwatchType type) {
            super(type);
        }
    }

    public enum StopwatchType {
        WYTAPIANIE,
        KRZEPNIECIE,
        STUDZENIE1,
        PODGRZANIE1,
        STUDZENIE2,
        PODGRZANIE2,
        USZLACHETNIANIE,
        LEARNING_VOTE_USER,
        LEARNING_VOTE_CPU,
        LEARNING_VOTE_REAL,
        LEARNING_MLP_USER,
        LEARNING_MLP_CPU,
        LEARNING_MLP_REAL,
        LEARNING_M5P_USER,
        LEARNING_M5P_CPU,
        LEARNING_M5P_REAL,
        LEARNING_FOREST_USER,
        LEARNING_FOREST_CPU,
        LEARNING_FOREST_REAL
    }

    public List<StopwatchType> productionStages() {
        final LinkedList<StopwatchType> result = new LinkedList<>();
        result.add(StopwatchType.WYTAPIANIE);
        result.add(StopwatchType.KRZEPNIECIE);
        result.add(StopwatchType.STUDZENIE1);
        result.add(StopwatchType.PODGRZANIE1);
        result.add(StopwatchType.STUDZENIE2);
        result.add(StopwatchType.PODGRZANIE2);
        result.add(StopwatchType.USZLACHETNIANIE);
        return result;
    }
}
