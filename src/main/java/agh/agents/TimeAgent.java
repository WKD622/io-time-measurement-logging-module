package agh.agents;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private CpuTimeStopwatch stoperForest = new CpuTimeStopwatch(StopwatchType.LEARNING_FOREST);
    private CpuTimeStopwatch stoperM5p = new CpuTimeStopwatch(StopwatchType.LEARNING_M5P);
    private CpuTimeStopwatch stoperMlp = new CpuTimeStopwatch(StopwatchType.LEARNING_MLP);
    private CpuTimeStopwatch stoperVote = new CpuTimeStopwatch(StopwatchType.LEARNING_VOTE);
    private static HashMap<StopwatchType, Stopwatch> stages = new HashMap<>();
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
                stoperForest,
                stoperM5p,
                stoperMlp,
                stoperVote
        ).forEach(s -> stages.put(s.getType(), s));
    }

    public ObservableMap<StopwatchType, Long> getObservableTimes() {
        return observableTimes;
    }

    public void setup() {
        registerO2AInterface(ITime.class, this);

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    for (Map.Entry<StopwatchType, Stopwatch> e : stages.entrySet()) {
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
    }

    @Override
    public void start(StopwatchType stage) {
        stages.get(stage).start();
//        System.out.println("started: " + stage.toString());
    }

    @Override
    public void stop(StopwatchType stage) {
        stages.get(stage).stop();
//        System.out.println("stopped: " + stage.toString());
    }

    @Override
    public void reset(StopwatchType stage) {
        stages.get(stage).reset();
    }

    @Override
    public boolean isMeasuring(StopwatchType stage) {
        Boolean result = null;
        try {
            Callable<Boolean> callable = () -> stages.get(stage).isMeasuring();
            FutureTask<Boolean> futureTask = new FutureTask<>(callable);
            Platform.runLater(futureTask);
            result = futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public long time(StopwatchType stage) {
        return stages.get(stage).time();
    }

    abstract class Stopwatch {

        StopwatchType type;
        boolean measuring;
        boolean ended;
        long start;
        long stop;

        final Supplier<Long> measurement;

        Stopwatch(StopwatchType type, Supplier<Long> measurement) {
            this.type = type;
            this.measurement = measurement;

            this.measuring = false;
            reset();
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

    class CpuTimeStopwatch extends Stopwatch {

        CpuTimeStopwatch(StopwatchType type) {
            super(type, System::nanoTime);
        }
    }

    class UserTimeStopwatch extends Stopwatch {

        UserTimeStopwatch(StopwatchType type) {
            super(type, System::nanoTime);
        }
    }

//    public enum ProcessState {
//        START_LEARNING,
//        END_LEARNING,
//        START_WYTAPIANIE,
//        END_WYTAPIANIE,
//        START_KRZEPNIECIE,
//        END_KRZEPNIECIE,
//        START_STUDZENIE_1,
//        END_STUDZENIE_1,
//        START_PODGRZANIE_1,
//        END_PODGRZANIE_1,
//        START_STUDZENIE_2,
//        END_STUDZENIE_2,
//        START_PODGRZANIE_2,
//        END_PODGRZANIE_2,
//        START_USZLACHETNIANIE,
//        END_USZLACHETNIANIE,
//    }

    public enum StopwatchType {
        LEARNING_MLP,
        LEARNING_M5P,
        LEARNING_FOREST,
        LEARNING_VOTE,
        WYTAPIANIE,
        KRZEPNIECIE,
        STUDZENIE1,
        PODGRZANIE1,
        STUDZENIE2,
        PODGRZANIE2,
        USZLACHETNIANIE
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
