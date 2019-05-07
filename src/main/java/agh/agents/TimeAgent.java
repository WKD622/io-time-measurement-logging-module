package agh.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;

public class TimeAgent extends Agent implements ILogging {
    private List<Stoper> stopers = new ArrayList<>();
    private SimpleStringProperty times = new SimpleStringProperty("");
    private Stoper stoperWytapianie = new Stoper(StoperType.WATAPIANIE);
    private Stoper stoperKrzepiniecie = new Stoper(StoperType.KRZEPNIECIE);
    private Stoper stoperStudzenie1 = new Stoper(StoperType.STUDZENIE1);
    private Stoper stoperPodgrzanie1 = new Stoper(StoperType.PODGRZANIE1);
    private Stoper stoperStudzenie2 = new Stoper(StoperType.STUDZENIE2);
    private Stoper stoperPodgrzanie2 = new Stoper(StoperType.PODGRZANIE2);
    private Stoper stoperUszlachetnianie = new Stoper(StoperType.USZLACHETNIANIE);
    private Stoper stoperLearning = new Stoper(StoperType.LEARNING);

    public TimeAgent() {
        this.stopers.addAll(Arrays.asList(stoperWytapianie, stoperKrzepiniecie, stoperStudzenie1, stoperPodgrzanie1, stoperStudzenie2, stoperPodgrzanie2, stoperUszlachetnianie, stoperLearning));
    }

    @Override
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
        registerO2AInterface(ILogging.class, this);
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
            if (message.contains(TimeStamps.START_WYTAPIANIE.toString())) {
                this.stoperWytapianie.start();
            } else if (message.contains(TimeStamps.START_KRZEPNIECIE.toString())) {
                this.stoperKrzepiniecie.start();
            } else if (message.contains(TimeStamps.START_STUDZENIE_1.toString())) {
                this.stoperStudzenie1.start();
            } else if (message.contains(TimeStamps.START_PODGRZANIE_1.toString())) {
                this.stoperPodgrzanie1.start();
            } else if (message.contains(TimeStamps.START_STUDZENIE_2.toString())) {
                this.stoperStudzenie2.start();
            } else if (message.contains(TimeStamps.START_PODGRZANIE_2.toString())) {
                this.stoperPodgrzanie2.start();
            } else if (message.contains(TimeStamps.START_LEARNING.toString())) {
                this.stoperLearning.start();
            } else if (message.contains(TimeStamps.START_USZLACHETNIANIE.toString())) {
                this.stoperUszlachetnianie.start();
            }
        } else if (message.contains("END")) {
            if (message.contains(TimeStamps.END_WYTAPIANIE.toString())) {
                this.stoperWytapianie.stop();
            } else if (message.contains(TimeStamps.END_KRZEPNIECIE.toString())) {
                this.stoperKrzepiniecie.stop();
            } else if (message.contains(TimeStamps.END_STUDZENIE_1.toString())) {
                this.stoperStudzenie1.stop();
            } else if (message.contains(TimeStamps.END_PODGRZANIE_1.toString())) {
                this.stoperPodgrzanie1.stop();
            } else if (message.contains(TimeStamps.END_STUDZENIE_2.toString())) {
                this.stoperStudzenie2.stop();
            } else if (message.contains(TimeStamps.END_PODGRZANIE_2.toString())) {
                this.stoperPodgrzanie2.stop();
            } else if (message.contains(TimeStamps.END_USZLACHETNIANIE.toString())) {
                this.stoperUszlachetnianie.stop();
            } else if (message.contains(TimeStamps.END_LEARNING.toString())) {
                this.stoperLearning.stop();
            }
        }
    }

    class Stoper {
        private boolean ended;
        private boolean isMeasuring;
        private long start;
        private long stop;
        private StoperType type;

        public Stoper(StoperType type) {
            this.isMeasuring = false;
            this.type = type;
            this.ended = false;
        }

        public StoperType getType() {
            return this.type;
        }

        public void start() {
            this.start = System.currentTimeMillis();
            this.isMeasuring = true;
        }

        public double getTimePassed() {
            return (System.currentTimeMillis() - this.start) / 1000.0;
        }

        public boolean isMeasuring() {
            return this.isMeasuring;
        }

        public boolean isEnded() {
            return this.ended;
        }

        public void stop() {
            stop = System.currentTimeMillis();
            this.isMeasuring = false;
            this.ended = true;
        }

        public double getMeasurement() {
            return (stop - start) / 1000.0;
        }

        public String toString() {
            return this.getMeasurement() + " s.";
        }

        public void reset() {
            this.start = 0;
            this.stop = 0;
        }
    }

    enum TimeStamps {
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
                String stoperType = stoper.split("%%")[0];
                String time = stoper.split("%%")[1];
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
