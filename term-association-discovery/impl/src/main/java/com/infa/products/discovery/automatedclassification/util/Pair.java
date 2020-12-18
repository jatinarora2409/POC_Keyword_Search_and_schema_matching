package com.infa.products.discovery.automatedclassification.util;

import java.util.Objects;

public class Pair<T, V> {
    T first;
    V Second;

    public Pair() {

    }

    public Pair(T first, V second) {
        this.first = first;
        Second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public V getSecond() {
        return Second;
    }

    public void setSecond(V second) {
        Second = second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", Second=" + Second +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(Second, pair.Second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, Second);
    }
}
