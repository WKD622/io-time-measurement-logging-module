package agh.utils;

public enum AgentsFilters {
    EMPTY("-"),
    LEARNING_AGENT("Agent uczący"),
    PROCESS_AGENT("Agent procesu"),
    PRODUCTION_AGENT("Agent produkcji");

    private String displayName;
    AgentsFilters(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
