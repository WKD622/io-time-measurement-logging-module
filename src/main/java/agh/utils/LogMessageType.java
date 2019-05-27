package agh.utils;

public enum LogMessageType {
    STATE("Status"),
    LOG("Log"),
    COMM("Komunikacja"),
    OTHER("Inne");

    private String displayName;
    LogMessageType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
