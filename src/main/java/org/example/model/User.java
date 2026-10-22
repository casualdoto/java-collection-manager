package org.example.model;

public record User(long id, String name, String email) {
    public User {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом.");
        }
        name = normalize(name, "Имя");
        email = normalize(email, "Email");
        if (!email.matches("[^\\s@]+@[^\\s@]+")
                || email.codePoints().anyMatch(c -> Character.isWhitespace(c) || Character.isSpaceChar(c))) {
            throw new IllegalArgumentException("Email должен содержать один @ и не содержать пробелов.");
        }
    }

    private static String normalize(String value, String field) {
        if (value == null || value.strip().isEmpty()) {
            throw new IllegalArgumentException(field + ": значение не должно быть пустым.");
        }
        String normalized = value.strip();
        if (normalized.contains(";") || normalized.contains("\n") || normalized.contains("\r")) {
            throw new IllegalArgumentException(field + ": разделитель ; и переносы строк запрещены.");
        }
        return normalized;
    }
}
