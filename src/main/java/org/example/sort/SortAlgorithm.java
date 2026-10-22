package org.example.sort;

import java.util.Comparator;
import java.util.List;

public interface SortAlgorithm<T> {
    void sort(List<T> items, Comparator<? super T> comparator);
}
