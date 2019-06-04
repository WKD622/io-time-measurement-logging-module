package agh.utils;

    public enum LogMessageType {
    STATUS("Status"),
    COMM("Komunikacja"),
    TIME("Pomiar czasu"),
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
