package agh.utils;

public enum LogLevel { // To jest wyświetlane jako "rodzaj" logów, ale odmawiam nazwania tego tak w kodzie
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
