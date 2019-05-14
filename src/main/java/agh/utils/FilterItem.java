package agh.utils;

public class FilterItem<T> {
    public T key;
    public String value;

    public FilterItem(T key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
