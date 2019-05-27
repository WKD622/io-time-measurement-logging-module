package agh.utils;

public enum Agents {
    LEARNING_AGENT("Agent uczący"),
    PROCESS_AGENT("Agent procesu"),
    PRODUCTION_AGENT("Agent produkcji"),
    UI_AGENT("Agent UI"),
    USER("Użytkownik");

    private String displayName;
    Agents(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
