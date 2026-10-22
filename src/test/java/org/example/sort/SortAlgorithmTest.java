package org.example.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortAlgorithmTest {
    @Test
    void integersMatchReferenceForBothAlgorithmsAndDirections() {
        List<List<Integer>> cases = List.of(List.of(), List.of(1), List.of(1, 2, 3),
                List.of(5, 4, 3, 2, 1), List.of(2, -1, 2, 0, -1),
                new Random(42).ints(300, -50, 51).boxed().toList());
        for (SortAlgorithm<Integer> algorithm : List.of(new InsertionSort<Integer>(), new MergeSort<Integer>())) {
            for (Comparator<Integer> comparator : List.of(Comparator.<Integer>naturalOrder(), Comparator.<Integer>reverseOrder())) {
                for (List<Integer> input : cases) {
                    List<Integer> actual = new ArrayList<>(input);
                    algorithm.sort(actual, comparator);
                    assertEquals(input.stream().sorted(comparator).toList(), actual);
                }
            }
        }
    }

    @Test
    void equalNamesKeepTheirRelativeOrder() {
        List<User> input = List.of(new User(3, "Анна", "3@x"), new User(2, "Борис", "2@x"),
                new User(1, "анна", "1@x"));
        Comparator<User> byNameOnly = Comparator.comparing(User::name, String.CASE_INSENSITIVE_ORDER);
        for (SortAlgorithm<User> algorithm : List.of(new InsertionSort<User>(), new MergeSort<User>())) {
            for (Comparator<User> comparator : List.of(byNameOnly, byNameOnly.reversed())) {
                List<User> actual = new ArrayList<>(input);
                algorithm.sort(actual, comparator);
                assertEquals(input.stream().sorted(comparator).toList(), actual);
            }
        }
    }
}
