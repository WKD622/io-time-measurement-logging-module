package agh.agents;

public interface ITime {
    void start(TimeAgent.StopwatchType stage);
    void stop(TimeAgent.StopwatchType stage);
    void reset(TimeAgent.StopwatchType stage);
    boolean isMeasuring(TimeAgent.StopwatchType stage);
    long time(TimeAgent.StopwatchType stage);
}
