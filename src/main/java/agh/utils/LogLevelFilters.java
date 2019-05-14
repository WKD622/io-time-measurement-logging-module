package agh.utils;

public enum LogLevelFilters {
    EMPTY("-"),
    AGENT_INFO("Agent"),
    USER_INFO("Użytkownik"),
    DEBUG("Debug"),
    WARNING("Ostrzeżenie"),
    ERROR("Błąd");

    private String displayName;
    LogLevelFilters(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
