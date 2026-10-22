package org.example.sort;

import java.util.Comparator;
import java.util.List;

public class InsertionSort<T> implements SortAlgorithm<T> {
    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        for (int i = 1; i < items.size(); i++) {
            T value = items.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(items.get(j), value) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }
            items.set(j + 1, value);
        }
    }
}
