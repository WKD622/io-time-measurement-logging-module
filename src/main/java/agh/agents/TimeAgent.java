package agh.agents;

import agh.utils.LogLevel;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;

public class TimeAgent extends Agent implements ITime {

    private RealTimeStoper stoperWytapianie = new RealTimeStoper(StoperType.WATAPIANIE);
    private RealTimeStoper stoperKrzepiniecie = new RealTimeStoper(StoperType.KRZEPNIECIE);
    private RealTimeStoper stoperStudzenie1 = new RealTimeStoper(StoperType.STUDZENIE1);
    private RealTimeStoper stoperPodgrzanie1 = new RealTimeStoper(StoperType.PODGRZANIE1);
    private RealTimeStoper stoperStudzenie2 = new RealTimeStoper(StoperType.STUDZENIE2);
    private RealTimeStoper stoperPodgrzanie2 = new RealTimeStoper(StoperType.PODGRZANIE2);
    private RealTimeStoper stoperUszlachetnianie = new RealTimeStoper(StoperType.USZLACHETNIANIE);
    private CpuTimeStoper stoperLearning = new CpuTimeStoper(StoperType.LEARNING);
//    private List<Stoper> stopers = new ArrayList<>();
    private HashMap<StoperType, Stoper> stopers = new HashMap<>();
    private HashMap<StoperType, Long> times = new HashMap<>();
    private ObservableMap<StoperType, Long> observableTimes;

    public TimeAgent() {
//        stopers.putAll(
//        this.stopers.addAll(Arrays.asList(
        Arrays.asList(
                stoperWytapianie,
                stoperKrzepiniecie,
                stoperStudzenie1,
                stoperPodgrzanie1,
                stoperStudzenie2,
                stoperPodgrzanie2,
                stoperUszlachetnianie,
                stoperLearning
        ).forEach(s -> stopers.put(s.getType(), s));
    }

    public HashMap<StoperType, Long> getLog() {
        return times;
    }

    public void setup() {
        observableTimes = FXCollections.observableMap(times);

        registerO2AInterface(ITime.class, this);

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                for (Stoper stoper : stopers) {
//                    if (stoper.isMeasuring() || stoper.isEnded()) {
//                    if (stoper.isMeasuring()) {
//                        times.put(stoper.getType(), stoper.getTimePassed());
//                    }
//                }
                for (Map.Entry<StoperType, Stoper> e : stopers.entrySet()) {
//                    if (stoper.isMeasuring() || stoper.isEnded()) {
                    Stoper stoper = e.getValue();
                    if (stoper.isMeasuring()) {
                        System.out.println(stoper.toString() + " is measuring");
                        observableTimes.put(e.getKey(), stoper.getTimePassed());
                    }
                }
//                System.out.println("1s ///");
            }
        });

//        logInterface = MainContainer.cc.getAgent("Logging-agent").getO2AInterface(ILogging.class);
//        logInterface.sendMessage(LogLevel.INFO, agent, "Start first step");


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

//    private void parseMessage(String message) {
//        if (message.contains("START")) {
//            if (message.contains(ProcessState.START_WYTAPIANIE.toString())) {
//                this.stoperWytapianie.start();
//            } else if (message.contains(ProcessState.START_KRZEPNIECIE.toString())) {
//                this.stoperKrzepiniecie.start();
//            } else if (message.contains(ProcessState.START_STUDZENIE_1.toString())) {
//                this.stoperStudzenie1.start();
//            } else if (message.contains(ProcessState.START_PODGRZANIE_1.toString())) {
//                this.stoperPodgrzanie1.start();
//            } else if (message.contains(ProcessState.START_STUDZENIE_2.toString())) {
//                this.stoperStudzenie2.start();
//            } else if (message.contains(ProcessState.START_PODGRZANIE_2.toString())) {
//                this.stoperPodgrzanie2.start();
//            } else if (message.contains(ProcessState.START_LEARNING.toString())) {
//                this.stoperLearning.start();
//            } else if (message.contains(ProcessState.START_USZLACHETNIANIE.toString())) {
//                this.stoperUszlachetnianie.start();
//            }
//        } else if (message.contains("END")) {
//            if (message.contains(ProcessState.END_WYTAPIANIE.toString())) {
//                this.stoperWytapianie.stop();
//                times.put(this.stoperWytapianie.getType(), this.stoperWytapianie.getMeasurement());
//            } else if (message.contains(ProcessState.END_KRZEPNIECIE.toString())) {
//                this.stoperKrzepiniecie.stop();
//                times.put(this.stoperKrzepiniecie.getType(), this.stoperKrzepiniecie.getMeasurement());
//            } else if (message.contains(ProcessState.END_STUDZENIE_1.toString())) {
//                this.stoperStudzenie1.stop();
//                times.put(this.stoperStudzenie1.getType(), this.stoperStudzenie1.getMeasurement());
//            } else if (message.contains(ProcessState.END_PODGRZANIE_1.toString())) {
//                this.stoperPodgrzanie1.stop();
//                times.put(this.stoperPodgrzanie1.getType(), this.stoperPodgrzanie1.getMeasurement());
//            } else if (message.contains(ProcessState.END_STUDZENIE_2.toString())) {
//                this.stoperStudzenie2.stop();
//                times.put(this.stoperStudzenie2.getType(), this.stoperStudzenie2.getMeasurement());
//            } else if (message.contains(ProcessState.END_PODGRZANIE_2.toString())) {
//                this.stoperPodgrzanie2.stop();
//                times.put(this.stoperPodgrzanie2.getType(), this.stoperPodgrzanie2.getMeasurement());
//            } else if (message.contains(ProcessState.END_USZLACHETNIANIE.toString())) {
//                this.stoperUszlachetnianie.stop();
//                times.put(this.stoperUszlachetnianie.getType(), this.stoperUszlachetnianie.getMeasurement());
//            } else if (message.contains(ProcessState.END_LEARNING.toString())) {
//                this.stoperLearning.stop();
//                times.put(this.stoperLearning.getType(), this.stoperLearning.getMeasurement());
//            }
//        }
//    }

    public ObservableMap<StoperType, Long> getObservableTimes() {
        return observableTimes;
    }

    @Override
    public void startTimer(StoperType timer) {
        stopers.get(timer).start();
        System.out.println("started: " + timer.toString());
        System.out.println(stopers);
        System.out.println(observableTimes);
    }

    @Override
    public void stopTimer(StoperType timer) {
        stopers.get(timer).stop();
        System.out.println("stopped: " + timer.toString());
    }

    interface Stoper {
        StoperType getType();

        void start();

        long getTimePassed();

        boolean isMeasuring();

//        boolean isEnded();

        void stop();

        long getMeasurement();

        void reset();
    }

    class RealTimeStoper implements Stoper {
        private boolean ended;
        private boolean isMeasuring;
        private long start;
        private long stop;
        private StoperType type;

        public RealTimeStoper(StoperType type) {
            this.isMeasuring = false;
            this.type = type;
//            this.ended = false;
            reset();
        }

        @Override
        public StoperType getType() {
            return this.type;
        }

        @Override
        public void start() {
            this.start = System.currentTimeMillis();
            this.isMeasuring = true;
        }

//        public long getTimePassed() {
//            return (System.currentTimeMillis() - this.start) / 1000.0;
//        }
        @Override
        public long getTimePassed() {
            return System.currentTimeMillis() - this.start;
        }

        @Override
        public boolean isMeasuring() {
            return this.isMeasuring;
        }

//        @Override
//        public boolean isEnded() {
//            return this.ended;
//        }

        @Override
        public void stop() {
            stop = System.currentTimeMillis();
            this.isMeasuring = false;
//            this.ended = true;
        }

//        public double getMeasurement() {
//            return (stop - start) / 1000.0;
//        }
        @Override
        public long getMeasurement() {
            return stop - start;
        }

//        public String toString() {
//            return this.getTimePassed() + " s.";
//        }
        public String toString() {
            return this.getTimePassed() + " s.";
        }

        @Override
        public void reset() {
            this.start = 0;
            this.stop = 0;
        }
    }

    class CpuTimeStoper implements Stoper {
        private boolean ended;
        private boolean isMeasuring;
        private long start;
        private long stop;
        private StoperType type;

        public CpuTimeStoper(StoperType type) {
            this.isMeasuring = false;
            this.type = type;
            this.ended = false;
        }

        @Override
        public StoperType getType() {
            return this.type;
        }

        @Override
        public void start() {
            this.start = System.nanoTime();
            this.isMeasuring = true;
        }

        @Override
//        public double getTimePassed() {
//            return (System.nanoTime() - this.start) / 1000.0;
//        }
        public long getTimePassed() {
            return System.nanoTime() - this.start;
        }

        @Override
        public boolean isMeasuring() {
            return this.isMeasuring;
        }

//        @Override
//        public boolean isEnded() {
//            return this.ended;
//        }

        @Override
        public void stop() {
            stop = System.nanoTime();
            this.isMeasuring = false;
            this.ended = true;
        }

        @Override
//        public double getMeasurement() {
//            return (stop - start) / 1000.0;
//        }
        public long getMeasurement() {
            return stop - start;
        }

        public String toString() {
            return this.getMeasurement() + " s.";
        }

        @Override
        public void reset() {
            this.start = 0;
            this.stop = 0;
        }
    }

    public enum ProcessState {
        START_LEARNING,
        END_LEARNING,
        START_WYTAPIANIE,
        END_WYTAPIANIE,
        START_KRZEPNIECIE,
        END_KRZEPNIECIE,
        START_STUDZENIE_1,
        END_STUDZENIE_1,
        START_PODGRZANIE_1,
        END_PODGRZANIE_1,
        START_STUDZENIE_2,
        END_STUDZENIE_2,
        START_PODGRZANIE_2,
        END_PODGRZANIE_2,
        START_USZLACHETNIANIE,
        END_USZLACHETNIANIE,
    }

    public enum StoperType {
        LEARNING,
        WATAPIANIE,
        KRZEPNIECIE,
        STUDZENIE1,
        PODGRZANIE1,
        PODGRZANIE2,
        STUDZENIE2,
        USZLACHETNIANIE,
    }

    public List<StoperType> getProductionStages() {
        final LinkedList<StoperType> result = new LinkedList<>();
        result.add(StoperType.WATAPIANIE);
        result.add(StoperType.KRZEPNIECIE);
        result.add(StoperType.STUDZENIE1);
        result.add(StoperType.PODGRZANIE1);
        result.add(StoperType.STUDZENIE2);
        result.add(StoperType.PODGRZANIE2);
        result.add(StoperType.USZLACHETNIANIE);
        return result;
    }
}
