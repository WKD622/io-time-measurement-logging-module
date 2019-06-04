package agh.agents;

import java.util.function.Supplier;

public interface ITime {
    void start(TimeAgent.StopwatchType type);
    void stop(TimeAgent.StopwatchType type);
    void reset(TimeAgent.StopwatchType type);
    boolean isMeasuring(TimeAgent.StopwatchType type);
    long time(TimeAgent.StopwatchType type);
    void initializeMeasurement(TimeAgent.StopwatchType type, Supplier<Long> measurement);
}
