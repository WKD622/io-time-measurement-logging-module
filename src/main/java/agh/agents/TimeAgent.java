package agh.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;

public class TimeAgent extends Agent {
    private List<Stoper> stopers = new ArrayList<>();
    private SimpleStringProperty times = new SimpleStringProperty("");
    private RealTimeStoper stoperWytapianie = new RealTimeStoper(StoperType.WATAPIANIE);
    private RealTimeStoper stoperKrzepiniecie = new RealTimeStoper(StoperType.KRZEPNIECIE);
    private RealTimeStoper stoperStudzenie1 = new RealTimeStoper(StoperType.STUDZENIE1);
    private RealTimeStoper stoperPodgrzanie1 = new RealTimeStoper(StoperType.PODGRZANIE1);
    private RealTimeStoper stoperStudzenie2 = new RealTimeStoper(StoperType.STUDZENIE2);
    private RealTimeStoper stoperPodgrzanie2 = new RealTimeStoper(StoperType.PODGRZANIE2);
    private RealTimeStoper stoperUszlachetnianie = new RealTimeStoper(StoperType.USZLACHETNIANIE);
    private CpuTimeStoper stoperLearning = new CpuTimeStoper(StoperType.LEARNING);

    public TimeAgent() {
        this.stopers.addAll(Arrays.asList(stoperWytapianie, stoperKrzepiniecie, stoperStudzenie1, stoperPodgrzanie1, stoperStudzenie2, stoperPodgrzanie2, stoperUszlachetnianie, stoperLearning));
    }

    public SimpleStringProperty getLog() {
        return times;
    }

    protected void setup() {

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                for (Stoper stoper : stopers) {
                    if (stoper.isMeasuring() || stoper.isEnded()) {
                        sb.append(stoper.getType()).append("##").append(stoper.getTimePassed()).append("%%");
                    }
                }
                times.setValue(sb.toString());
            }
        });

        T.start();
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    parseMessage(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }

    private void parseMessage(String message) {
        if (message.contains("START")) {
            if (message.contains(ProcessState.START_WYTAPIANIE.toString())) {
                this.stoperWytapianie.start();
            } else if (message.contains(ProcessState.START_KRZEPNIECIE.toString())) {
                this.stoperKrzepiniecie.start();
            } else if (message.contains(ProcessState.START_STUDZENIE_1.toString())) {
                this.stoperStudzenie1.start();
            } else if (message.contains(ProcessState.START_PODGRZANIE_1.toString())) {
                this.stoperPodgrzanie1.start();
            } else if (message.contains(ProcessState.START_STUDZENIE_2.toString())) {
                this.stoperStudzenie2.start();
            } else if (message.contains(ProcessState.START_PODGRZANIE_2.toString())) {
                this.stoperPodgrzanie2.start();
            } else if (message.contains(ProcessState.START_LEARNING.toString())) {
                this.stoperLearning.start();
            } else if (message.contains(ProcessState.START_USZLACHETNIANIE.toString())) {
                this.stoperUszlachetnianie.start();
            }
        } else if (message.contains("END")) {
            StringBuilder sb = new StringBuilder();
            if (message.contains(ProcessState.END_WYTAPIANIE.toString())) {
                this.stoperWytapianie.stop();
                sb.append(this.stoperWytapianie.getType()).append("##").append(this.stoperWytapianie.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_KRZEPNIECIE.toString())) {
                this.stoperKrzepiniecie.stop();
                sb.append(this.stoperKrzepiniecie.getType()).append("##").append(this.stoperKrzepiniecie.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_STUDZENIE_1.toString())) {
                this.stoperStudzenie1.stop();
                sb.append(this.stoperStudzenie1.getType()).append("##").append(this.stoperStudzenie1.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_PODGRZANIE_1.toString())) {
                this.stoperPodgrzanie1.stop();
                sb.append(this.stoperPodgrzanie1.getType()).append("##").append(this.stoperPodgrzanie1.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_STUDZENIE_2.toString())) {
                this.stoperStudzenie2.stop();
                sb.append(this.stoperStudzenie2.getType()).append("##").append(this.stoperStudzenie2.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_PODGRZANIE_2.toString())) {
                this.stoperPodgrzanie2.stop();
                sb.append(this.stoperPodgrzanie2.getType()).append("##").append(this.stoperPodgrzanie2.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_USZLACHETNIANIE.toString())) {
                this.stoperUszlachetnianie.stop();
                sb.append(this.stoperUszlachetnianie.getType()).append("##").append(this.stoperUszlachetnianie.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            } else if (message.contains(ProcessState.END_LEARNING.toString())) {
                this.stoperLearning.stop();
                sb.append(this.stoperLearning.getType()).append("##").append(this.stoperLearning.getMeasurement()).append("%%");
                times.setValue(sb.toString());
            }
        }
    }

    interface Stoper {
        public StoperType getType();

        public void start();

        public double getTimePassed();

        public boolean isMeasuring();

        public boolean isEnded();

        public void stop();

        public double getMeasurement();

        public void reset();
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
            this.ended = false;
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

        @Override
        public double getTimePassed() {
            return (System.currentTimeMillis() - this.start) / 1000.0;
        }

        @Override
        public boolean isMeasuring() {
            return this.isMeasuring;
        }

        @Override
        public boolean isEnded() {
            return this.ended;
        }

        @Override
        public void stop() {
            stop = System.currentTimeMillis();
            this.isMeasuring = false;
            this.ended = true;
        }

        @Override
        public double getMeasurement() {
            return (stop - start) / 1000.0;
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
        public double getTimePassed() {
            return (System.nanoTime() - this.start) / 1000.0;
        }

        @Override
        public boolean isMeasuring() {
            return this.isMeasuring;
        }

        @Override
        public boolean isEnded() {
            return this.ended;
        }

        @Override
        public void stop() {
            stop = System.nanoTime();
            this.isMeasuring = false;
            this.ended = true;
        }

        @Override
        public double getMeasurement() {
            return (stop - start) / 1000.0;
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

    enum ProcessState {
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

    enum StoperType {
        LEARNING,
        WATAPIANIE,
        KRZEPNIECIE,
        STUDZENIE1,
        PODGRZANIE1,
        PODGRZANIE2,
        STUDZENIE2,
        USZLACHETNIANIE,
    }

    public class MeasuredTimesParser {
        private Map<StoperType, Double> stopers;
        private String times;

        public MeasuredTimesParser(SimpleStringProperty times) {
            this.times = times.getValue();
            this.stopers = new HashMap<>();
            parse();
        }

        private void parse() {
            List<String> timesList = Arrays.asList(times.split("%%"));
            for (String stoper : timesList) {
                String stoperType = stoper.split("##")[0];
                String time = stoper.split("##")[1];
                if (stoperType.contains(StoperType.LEARNING.toString())) {
                    this.stopers.put(StoperType.LEARNING, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.WATAPIANIE.toString())) {
                    this.stopers.put(StoperType.WATAPIANIE, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.KRZEPNIECIE.toString())) {
                    this.stopers.put(StoperType.KRZEPNIECIE, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.STUDZENIE1.toString())) {
                    this.stopers.put(StoperType.STUDZENIE1, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.PODGRZANIE1.toString())) {
                    this.stopers.put(StoperType.PODGRZANIE1, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.STUDZENIE2.toString())) {
                    this.stopers.put(StoperType.STUDZENIE2, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.PODGRZANIE2.toString())) {
                    this.stopers.put(StoperType.PODGRZANIE2, Double.parseDouble(time));
                } else if (stoperType.contains(StoperType.USZLACHETNIANIE.toString())) {
                    this.stopers.put(StoperType.USZLACHETNIANIE, Double.parseDouble(time));
                }
            }
        }

        public boolean containsStoper(StoperType stoperType) {
            return this.stopers.containsKey(stoperType);
        }

        public Set<StoperType> allContainedStopers() {
            return this.stopers.keySet();
        }

        public double getTimeForStoper(StoperType stoperType) {
            return this.stopers.get(stoperType);
        }
    }
}
