package agh.utils;

public enum LogLevel {
    EMPTY("-"),
    DEBUG("Debug"),
    INFO("Informacja"),
    WARNING("Ostrzeżenie"),
    ERROR("Błąd");

    private String displayName;
    LogLevel(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
