package agh.utils;

public enum TimeFilters {
    EMPTY("-"),
    REAL_TIME("Czas rzeczywisty"),
    PROCESSOR_TIME("Czas procesora"),
    USER_TIME("Czas użytkownika");

    private String displayName;
    TimeFilters(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
        }
}
