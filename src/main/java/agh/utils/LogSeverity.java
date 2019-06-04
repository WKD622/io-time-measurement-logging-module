package agh.utils;

public enum LogSeverity { // Uneccessary because of LogLevels, however was in requirements
    LOW("1"),
    MEDIUM("2"),
    HIGH("3");

    private String displayName;
    LogSeverity(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
