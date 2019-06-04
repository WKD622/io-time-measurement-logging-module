package agh.agents;

public interface ITime {
    void start(TimeAgent.StopwatchType type);
    void stop(TimeAgent.StopwatchType type);
    void reset(TimeAgent.StopwatchType type);
    boolean isMeasuring(TimeAgent.StopwatchType type);
    long time(TimeAgent.StopwatchType type);
}
