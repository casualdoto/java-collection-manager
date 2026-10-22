package org.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.example.model.User;
import org.example.sort.SortAlgorithm;

public class UserService {
    public static final Comparator<User> BY_NAME = Comparator
            .comparing(User::name, String.CASE_INSENSITIVE_ORDER).thenComparingLong(User::id);
    private final List<User> users = new ArrayList<>();

    public void add(User user) {
        Objects.requireNonNull(user, "Пользователь не задан.");
        if (users.stream().anyMatch(existing -> existing.id() == user.id())) {
            throw new IllegalArgumentException("Пользователь с ID " + user.id() + " уже существует.");
        }
        users.add(user);
    }

    public List<User> getAll() {
        return List.copyOf(users);
    }

    public List<String> findEmails(String namePart) {
        String query = namePart.strip().toLowerCase(Locale.ROOT);
        return users.stream()
                .filter(user -> user.name().toLowerCase(Locale.ROOT).contains(query))
                .sorted(BY_NAME)
                .map(User::email)
                .toList();
    }

    public List<User> sorted(SortAlgorithm<User> algorithm, boolean descending) {
        List<User> result = new ArrayList<>(users);
        algorithm.sort(result, descending ? BY_NAME.reversed() : BY_NAME);
        return result;
    }
}
