package agh.agents;

public interface ITime {
    void start(TimeAgent.ProductionStage stage);
    void stop(TimeAgent.ProductionStage stage);
    void reset(TimeAgent.ProductionStage stage);
    boolean isMeasuring(TimeAgent.ProductionStage stage);
}
