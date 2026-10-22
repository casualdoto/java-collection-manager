package org.example.service;

import java.util.List;
import org.example.model.User;
import org.example.sort.InsertionSort;
import org.example.sort.MergeSort;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService sample() {
        UserService service = new UserService();
        service.add(new User(3, "Борис", "b@x"));
        service.add(new User(2, "Анна", "a2@x"));
        service.add(new User(1, "анна", "a1@x"));
        return service;
    }

    @Test
    void rejectsDuplicateIdButAllowsDuplicateEmail() {
        UserService service = sample();
        assertThrows(IllegalArgumentException.class, () -> service.add(new User(1, "Другое имя", "new@x")));
        service.add(new User(4, "Иван", "b@x"));
        assertEquals(4, service.getAll().size());
    }

    @Test
    void filtersSortsAndMapsWithoutChangingOriginal() {
        UserService service = sample();
        List<User> original = service.getAll();
        assertEquals(List.of("a1@x", "a2@x"), service.findEmails(" НН "));
        assertEquals(List.of("a1@x", "a2@x", "b@x"), service.findEmails(""));
        assertTrue(service.findEmails("нет совпадений").isEmpty());
        assertEquals(original, service.getAll());
        assertThrows(UnsupportedOperationException.class, () -> original.clear());
    }

    @Test
    void customSortsReturnCopiesInBothDirections() {
        UserService service = sample();
        List<User> original = service.getAll();
        for (var algorithm : List.of(new InsertionSort<User>(), new MergeSort<User>())) {
            for (boolean descending : List.of(false, true)) {
                var comparator = descending ? UserService.BY_NAME.reversed() : UserService.BY_NAME;
                assertEquals(original.stream().sorted(comparator).toList(), service.sorted(algorithm, descending));
                assertEquals(original, service.getAll());
            }
        }
    }
}
