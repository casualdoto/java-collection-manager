package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void trimsFieldsAndAcceptsMinimalEmail() {
        assertEquals(new User(1, "Анна", "a@b"), new User(1, "  Анна  ", " a@b "));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    void rejectsNonPositiveId(long id) {
        assertThrows(IllegalArgumentException.class, () -> new User(id, "Анна", "a@b"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "a;b", "a\nb", "a\rb"})
    void rejectsInvalidName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new User(1, name, "a@b"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc", "@b", "a@", "a@@b", "a b@c", "a@b c", "a;z@b",
            "a\tb@c", "a\nb@c", "a\u00a0b@c"})
    void rejectsInvalidEmail(String email) {
        assertThrows(IllegalArgumentException.class, () -> new User(1, "Анна", email));
    }
}
