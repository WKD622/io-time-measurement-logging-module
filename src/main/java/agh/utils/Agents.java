package agh.utils;

public enum Agents {
    EMPTY("-"),
    LEARNING_AGENT("Agent uczący"),
    PROCESS_AGENT("Agent procesu"),
    PRODUCTION_AGENT("Agent produkcji"),
    UI_AGENT("Agent UI");

    private String displayName;
    Agents(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
