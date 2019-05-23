package agh.agents;

import jade.core.Agent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TimeAgent extends Agent implements ITime {

    private RealTimeStopwatch stoperWytapianie = new RealTimeStopwatch(ProductionStage.WYTAPIANIE);
    private RealTimeStopwatch stoperKrzepiniecie = new RealTimeStopwatch(ProductionStage.KRZEPNIECIE);
    private RealTimeStopwatch stoperStudzenie1 = new RealTimeStopwatch(ProductionStage.STUDZENIE1);
    private RealTimeStopwatch stoperPodgrzanie1 = new RealTimeStopwatch(ProductionStage.PODGRZANIE1);
    private RealTimeStopwatch stoperStudzenie2 = new RealTimeStopwatch(ProductionStage.STUDZENIE2);
    private RealTimeStopwatch stoperPodgrzanie2 = new RealTimeStopwatch(ProductionStage.PODGRZANIE2);
    private RealTimeStopwatch stoperUszlachetnianie = new RealTimeStopwatch(ProductionStage.USZLACHETNIANIE);
    private CpuTimeStopwatch stoperLearning = new CpuTimeStopwatch(ProductionStage.LEARNING);
    private static HashMap<ProductionStage, Stopwatch> stages = new HashMap<>();
    private static ObservableMap<ProductionStage, Long> observableTimes = FXCollections.observableHashMap();

    public TimeAgent() {
        Arrays.asList(
                stoperWytapianie,
                stoperKrzepiniecie,
                stoperStudzenie1,
                stoperPodgrzanie1,
                stoperStudzenie2,
                stoperPodgrzanie2,
                stoperUszlachetnianie,
                stoperLearning
        ).forEach(s -> stages.put(s.getType(), s));
    }

    public ObservableMap<ProductionStage, Long> getObservableTimes() {
        return observableTimes;
    }

    public void setup() {
        registerO2AInterface(ITime.class, this);

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    for (Map.Entry<ProductionStage, Stopwatch> e : stages.entrySet()) {
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
    public void start(ProductionStage stage) {
        stages.get(stage).start();
//        System.out.println("started: " + stage.toString());
    }

    @Override
    public void stop(ProductionStage stage) {
        stages.get(stage).stop();
//        System.out.println("stopped: " + stage.toString());
    }

    @Override
    public void reset(ProductionStage stage) {
        stages.get(stage).reset();
    }

    @Override
    public boolean isMeasuring(ProductionStage stage) {
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

    abstract class Stopwatch {

        ProductionStage type;
        boolean measuring;
        boolean ended;
        long start;
        long stop;

        final Supplier<Long> measurement;

        Stopwatch(ProductionStage type, Supplier<Long> measurement) {
            this.type = type;
            this.measurement = measurement;

            this.measuring = false;
            reset();
        }

        ProductionStage getType() {
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

        RealTimeStopwatch(ProductionStage type) {
            super(type, System::currentTimeMillis);
        }
    }

    class CpuTimeStopwatch extends Stopwatch {

        CpuTimeStopwatch(ProductionStage type) {
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

    public enum ProductionStage {
        LEARNING,
        WYTAPIANIE,
        KRZEPNIECIE,
        STUDZENIE1,
        PODGRZANIE1,
        STUDZENIE2,
        PODGRZANIE2,
        USZLACHETNIANIE
    }

    public List<ProductionStage> productionStages() {
        final LinkedList<ProductionStage> result = new LinkedList<>();
        result.add(ProductionStage.WYTAPIANIE);
        result.add(ProductionStage.KRZEPNIECIE);
        result.add(ProductionStage.STUDZENIE1);
        result.add(ProductionStage.PODGRZANIE1);
        result.add(ProductionStage.STUDZENIE2);
        result.add(ProductionStage.PODGRZANIE2);
        result.add(ProductionStage.USZLACHETNIANIE);
        return result;
    }
}
