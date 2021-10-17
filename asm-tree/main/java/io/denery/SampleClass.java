package io.denery;

import java.util.Objects;

public class SampleClass implements Comparable<SampleClass> {
    @Override
    public int compareTo(SampleClass sc) {
        if (Objects.equals(sc, this)) return 1;
        return 0;
    }
}
