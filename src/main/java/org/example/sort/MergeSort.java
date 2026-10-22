package org.example.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSort<T> implements SortAlgorithm<T> {
    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        mergeSort(items, new ArrayList<>(items), 0, items.size(), comparator);
    }

    private void mergeSort(List<T> items, List<T> buffer, int from, int to,
                           Comparator<? super T> comparator) {
        if (to - from < 2) {
            return;
        }
        int middle = from + (to - from) / 2;
        mergeSort(items, buffer, from, middle, comparator);
        mergeSort(items, buffer, middle, to, comparator);
        for (int i = from; i < to; i++) {
            buffer.set(i, items.get(i));
        }
        int left = from;
        int right = middle;
        for (int i = from; i < to; i++) {
            if (left < middle && (right >= to
                    || comparator.compare(buffer.get(left), buffer.get(right)) <= 0)) {
                items.set(i, buffer.get(left++));
            } else {
                items.set(i, buffer.get(right++));
            }
        }
    }
}
