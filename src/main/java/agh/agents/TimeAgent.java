package agh.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimeAgent extends Agent {
    private List<Stoper> stopers = new ArrayList<>();
    private RealTimeStoper stoperWytapianie = new RealTimeStoper(StoperType.WATAPIANIE);
    private RealTimeStoper stoperKrzepiniecie = new RealTimeStoper(StoperType.KRZEPNIECIE);
    private RealTimeStoper stoperStudzenie1 = new RealTimeStoper(StoperType.STUDZENIE1);
    private RealTimeStoper stoperPodgrzanie1 = new RealTimeStoper(StoperType.PODGRZANIE1);
    private RealTimeStoper stoperStudzenie2 = new RealTimeStoper(StoperType.STUDZENIE2);
    private RealTimeStoper stoperPodgrzanie2 = new RealTimeStoper(StoperType.PODGRZANIE2);
    private RealTimeStoper stoperUszlachetnianie = new RealTimeStoper(StoperType.USZLACHETNIANIE);
    private CpuTimeStoper stoperLearning = new CpuTimeStoper(StoperType.LEARNING);
    private HashMap<StoperType, Double> times = new HashMap<>();

    public TimeAgent() {
        this.stopers.addAll(Arrays.asList(stoperWytapianie, stoperKrzepiniecie, stoperStudzenie1, stoperPodgrzanie1, stoperStudzenie2, stoperPodgrzanie2, stoperUszlachetnianie, stoperLearning));
    }

    public HashMap getLog() {
        return times;
    }

    public void setup() {

        Thread T = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Stoper stoper : stopers) {
                    if (stoper.isMeasuring() || stoper.isEnded()) {
                        times.put(stoper.getType(), stoper.getTimePassed());
                    }
                }
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
            if (message.contains(ProcessState.END_WYTAPIANIE.toString())) {
                this.stoperWytapianie.stop();
                times.put(this.stoperWytapianie.getType(), this.stoperWytapianie.getMeasurement());
            } else if (message.contains(ProcessState.END_KRZEPNIECIE.toString())) {
                this.stoperKrzepiniecie.stop();
                times.put(this.stoperKrzepiniecie.getType(), this.stoperKrzepiniecie.getMeasurement());
            } else if (message.contains(ProcessState.END_STUDZENIE_1.toString())) {
                this.stoperStudzenie1.stop();
                times.put(this.stoperStudzenie1.getType(), this.stoperStudzenie1.getMeasurement());
            } else if (message.contains(ProcessState.END_PODGRZANIE_1.toString())) {
                this.stoperPodgrzanie1.stop();
                times.put(this.stoperPodgrzanie1.getType(), this.stoperPodgrzanie1.getMeasurement());
            } else if (message.contains(ProcessState.END_STUDZENIE_2.toString())) {
                this.stoperStudzenie2.stop();
                times.put(this.stoperStudzenie2.getType(), this.stoperStudzenie2.getMeasurement());
            } else if (message.contains(ProcessState.END_PODGRZANIE_2.toString())) {
                this.stoperPodgrzanie2.stop();
                times.put(this.stoperPodgrzanie2.getType(), this.stoperPodgrzanie2.getMeasurement());
            } else if (message.contains(ProcessState.END_USZLACHETNIANIE.toString())) {
                this.stoperUszlachetnianie.stop();
                times.put(this.stoperUszlachetnianie.getType(), this.stoperUszlachetnianie.getMeasurement());
            } else if (message.contains(ProcessState.END_LEARNING.toString())) {
                this.stoperLearning.stop();
                times.put(this.stoperLearning.getType(), this.stoperLearning.getMeasurement());
            }
        }
    }

    interface Stoper {
        StoperType getType();

        void start();

        double getTimePassed();

        boolean isMeasuring();

        boolean isEnded();

        void stop();

        double getMeasurement();

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
}
